package finalproject.airbnb.controller;

import finalproject.airbnb.exceptions.AuthorizationException;
import finalproject.airbnb.exceptions.BadRequestException;
import finalproject.airbnb.exceptions.NotFoundException;
import finalproject.airbnb.model.dao.BookingDAO;
import finalproject.airbnb.model.dao.StayDAO;
import finalproject.airbnb.model.dto.BookingDTO;
import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.pojo.Booking;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
import finalproject.airbnb.utilities.BookingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BookingController extends AbstractController{
    @Autowired
    BookingDAO bookingDAO;
    @Autowired
    BookingValidator bookingValidator;
    @Autowired
    StayDAO stayDAO;
    @PostMapping("/stays/{id}/booking")
    public Booking addBooking(@RequestBody BookingDTO bookingDTO, @PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        GetStayDTO getStayDTO = stayDAO.getStayById(id);
        if(user==null){
            throw new AuthorizationException();
        }
        if(getStayDTO == null){
            throw new NotFoundException("Not found stay with this id");
        }
        if(user.getId() == stayDAO.getHostId(id)){
            throw new AuthorizationException("You can't book your own stay.");
        }
        Booking booking = new Booking(bookingDTO, user.getId(), id);
        if(!bookingValidator.validateBooking(booking)){
            throw new BadRequestException("Start date must be before the end date.");
        }
        if(!bookingDAO.getBookingsBetweenDates(id, booking.getFromDate(), booking.getToDate()).isEmpty()){
            throw new BadRequestException("Sorry, stay is already booked for this date.");
        }
        if(getStayDTO.isInstantBook()){
            booking.setAccepted(true);
        }
        bookingDAO.addBooking(booking);
        return booking;
    }
    @DeleteMapping("bookings/{id}")
    public String deleteBooking(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null) {
            throw new AuthorizationException();
        }
        Booking booking = bookingDAO.getBookingById(id);
        if(booking == null) {
            throw new NotFoundException("Booking not found.");
        }
        if(user.getId() != booking.getUserId() && user.getId() != stayDAO.getHostId(booking.getStayId())) {
            throw new AuthorizationException("You have no permissions to delete this booking");
        }
        return bookingDAO.deleteBooking(id);
    }

    @PutMapping("/bookings/{id}/accept")
    public String acceptBooking(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException();
        }
        Booking booking = bookingDAO.getBookingById(id);
        if(booking == null) {
            throw new BadRequestException("Booking doesn't exist");
        }
        if(user.getId() != stayDAO.getHostId(booking.getStayId())){
            throw new AuthorizationException("You have to be host to accept stay's bookings.");
        }
        return bookingDAO.acceptBooking(booking);
    }

}
