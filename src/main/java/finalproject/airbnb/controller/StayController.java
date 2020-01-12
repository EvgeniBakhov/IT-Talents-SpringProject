package finalproject.airbnb.controller;


import finalproject.airbnb.exceptions.AuthorizationException;
import finalproject.airbnb.exceptions.BadRequestException;
import finalproject.airbnb.exceptions.NotFoundException;
import finalproject.airbnb.model.dao.ReviewDAO;
import finalproject.airbnb.model.dao.BookingDAO;
import finalproject.airbnb.model.dao.StayDAO;
import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.StayDTO;
import finalproject.airbnb.model.pojo.Review;
import finalproject.airbnb.model.pojo.Booking;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
import finalproject.airbnb.utilities.LocationValidator;
import finalproject.airbnb.utilities.StayValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
public class StayController extends AbstractController {

    @Autowired
    private StayDAO stayDAO;
    @Autowired
    private StayValidator stayValidator;
    @Autowired
    private BookingDAO bookingDAO;
    @Autowired
    private LocationValidator locationValidator;
    @Autowired
    private ReviewDAO reviewDAO;

    @PostMapping("/stays")
    public Stay addStay(@RequestBody StayDTO stayDTO, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null) {
            throw new AuthorizationException();
        }
        Stay stay = new Stay(stayDTO);
        stay.setHost(user);
        if(!locationValidator.isValidLocation(stay.getLocation())) {
            throw new BadRequestException("Invalid location!");
        }
        if(!stayValidator.isValidPrice(stay.getPrice())) {
            throw new BadRequestException("Invalid price!");
        }
        if(!stayValidator.isValidTitle(stay.getTitle())) {
            throw new BadRequestException("Invalid title!");
        }
        if(!stayValidator.isValidNumOfBathrooms(stay.getNumOfBathrooms()) ||
            !stayValidator.isValidNumOfBeds(stay.getNumOfBeds()) ||
            !stayValidator.isValidNumOfBedrooms(stay.getNumOfBedrooms())) {
            throw new BadRequestException("Number must be between 1 and 50!");
        }
        stayDAO.addStay(stay);
        return stay;
    }

    @GetMapping("/stays/{id}")
    public GetStayDTO getStay(@PathVariable long id) throws SQLException {
        GetStayDTO getStayDTO = new GetStayDTO(stayDAO.getStayById(id));
        if(getStayDTO == null) {
            throw new NotFoundException("Stay not found");
        }
        return getStayDTO;
    }

    @DeleteMapping("/stays/{id}")
    public String deleteStay(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null) {
            throw new AuthorizationException();
        }
        if(stayDAO.getStayById(id) == null) {
            throw new NotFoundException("Stay not found");
        }
        if(user.getId() != stayDAO.getHostId(id)) {
            throw new AuthorizationException("You don't have permissions to delete this stay!");
        }
        return stayDAO.deleteStay(id);
    }

    @GetMapping("/users/{id}/stays")
    public List<GetStayDTO> getStaysMadeByUser(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null) {
            throw new AuthorizationException();
        }
        List<GetStayDTO> stays = stayDAO.getStaysByUserId(id);
        if(stays.isEmpty()) {
            throw new NotFoundException("Stays not found");
        }
        return stays;
    }

    @GetMapping("/stays/{id}/reviews")
    public List<Review> getReviewsForStay(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null) {
            throw new AuthorizationException();
        }
        List<Review> reviews = reviewDAO.getReviewsByStayId(id);
        if(reviews.isEmpty()) {
            throw new NotFoundException("No reviews for stay");
        }
        return reviews;
    }
    @GetMapping("/stays/{id}/bookings")
    public List<Booking> getAllBookingsForStay(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException();
        }
        if(user.getId()!=stayDAO.getHostId(id)){
            throw new AuthorizationException("You have to be host to see stay's bookings.");
        }
        List<Booking> bookingsForStay = bookingDAO.getBookingByStayId(id);
        if(bookingsForStay==null){
            throw new NotFoundException("There are no bookings for this stay.");
        }
        return bookingsForStay;
    }
    @GetMapping("/stays/{id}/unaccepted")
    public List<Booking> getAllUnacceptedBookings(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException();
        }
        if(user.getId()!=stayDAO.getHostId(id)){
            throw new AuthorizationException("You have to be host to see stay's bookings.");
        }
        List<Booking> unacceptedBookings = bookingDAO.getUnacceptedBookingByStayId(id);
        if(unacceptedBookings==null){
            throw new NotFoundException("There are no unaccepted bookings for this stay.");
        }
        return unacceptedBookings;
    }

    @PutMapping("/bookings/{id}")
    public String acceptBooking(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException();
        }
        Booking booking = bookingDAO.getBookingById(id);
        if(booking == null) {
            throw new BadRequestException("Booking doesn't exist");
        }
        if(user.getId() != stayDAO.getStayById(booking.getStayId()).getHost().getId()){
            throw new AuthorizationException("You have to be host to accept stay's bookings.");
        }
        return bookingDAO.acceptBooking(booking);
    }
}
