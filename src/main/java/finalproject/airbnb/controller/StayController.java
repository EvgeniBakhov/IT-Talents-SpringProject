package finalproject.airbnb.controller;


import finalproject.airbnb.exceptions.AuthorizationException;
import finalproject.airbnb.exceptions.BadRequestException;
import finalproject.airbnb.exceptions.NotFoundException;
import finalproject.airbnb.model.dao.ReviewDAO;
import finalproject.airbnb.model.dao.BookingDAO;
import finalproject.airbnb.model.dao.StayDAO;
import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.StayDTO;
import finalproject.airbnb.model.dto.StayFilterDTO;
import finalproject.airbnb.model.pojo.Review;
import finalproject.airbnb.model.pojo.Booking;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
import finalproject.airbnb.utilities.LocationValidator;
import finalproject.airbnb.utilities.StayValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class StayController extends AbstractController {

    public static final String UPLOAD_FOLDER = "C:\\Users\\virgi\\IdeaProjects\\IT-Talents-SpringProject\\src\\main\\resources\\StayPictures";

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
    public GetStayDTO addStay(@RequestBody StayDTO stayDTO, HttpSession session) throws SQLException {
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
        if(!stayValidator.isValidStayType(stay.getStayTypeId())) {
            throw new BadRequestException("Invalid stay type!");
        }
        if(!stayValidator.isValidPropertyType(stay.getPropertyTypeId())) {
            throw new BadRequestException("Invalid property type!");
        }
        long stayId = stayDAO.addStay(stay);
        return stayDAO.getStayById(stayId);
    }

    @GetMapping("/stays/{id}")
    public GetStayDTO getStay(@PathVariable long id) throws SQLException {
        GetStayDTO getStayDTO = stayDAO.getStayById(id);
        if(getStayDTO == null) {
            throw new NotFoundException("Stay not found");
        }
        return getStayDTO;
    }

    @PutMapping("/stays/{id}")
    public StayDTO editStay(@PathVariable long id, @RequestBody StayDTO stayDTO, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException();
        }
        if(user.getId() != stayDAO.getHostId(id)){
            throw new AuthorizationException("You must be a host to edit this stay.");
        }
        return stayDAO.editStay(id, stayDTO);
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
        if(user.getId() != stayDAO.getHostId(id)){
            throw new AuthorizationException("You have to be host to see stay's bookings.");
        }
        List<Booking> bookingsForStay = bookingDAO.getBookingByStayId(id);
        if(bookingsForStay == null){
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
        if(user.getId() != stayDAO.getHostId(id)){
            throw new AuthorizationException("You have to be host to see stay's bookings.");
        }
        List<Booking> unacceptedBookings = bookingDAO.getUnacceptedBookingByStayId(id);
        if(unacceptedBookings == null){
            throw new NotFoundException("There are no unaccepted bookings for this stay.");
        }
        return unacceptedBookings;
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

    @PostMapping("/stays/{id}/addImage")
    public String addImage(@PathVariable long id, @RequestParam ("file") MultipartFile file, HttpSession session) throws SQLException, IOException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null){
            throw new AuthorizationException();
        }
        if(stayDAO.getHostId(id) != user.getId()){
            throw new AuthorizationException("You must be a host to add the image to this stay.");
        }
        if(file == null){
            throw new BadRequestException("Cannot upload this file");
        }
        String fileName = LocalDateTime.now().toString() + "_" + user.getId() + "_" + file.getOriginalFilename();
        fileName = fileName.replace(':', '-');
        File localFile  = new File (UPLOAD_FOLDER + file.getOriginalFilename());
        Files.copy(file.getInputStream(), localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        stayDAO.addImage(fileName, id);
        return "Photo added.";
    }
    @GetMapping("/stays/{id}/images")
    public List<String> getStayImages(@PathVariable long id) throws SQLException {
        if(stayDAO.getStayById(id)== null) {
            throw new NotFoundException("Stay not found");
        }
        List<String> images = stayDAO.getStayImages(id);
        if(images.isEmpty()){
            throw new NotFoundException("Stay doesn't have images");
        }
        return images;
    }

    @PostMapping("stays/filters")
    public List<GetStayDTO> getStaysByFilters(@RequestBody StayFilterDTO stayFilterDTO) throws SQLException {
        double minPrice = stayFilterDTO.getMinPrice();
        double maxPrice = stayFilterDTO.getMaxPrice();
        int numOfBeds = stayFilterDTO.getNumOfBeds();
        int numOfBedrooms = stayFilterDTO.getNumOfBedrooms();
        int numOfBathrooms = stayFilterDTO.getNumOfBathrooms();
        long stayType = stayFilterDTO.getStayTypeId();
        long propertyType = stayFilterDTO.getPropertyTypeId();
        String order = stayFilterDTO.getOrder();
        String country = stayFilterDTO.getCountry();
        String city = stayFilterDTO.getCity();
        if(minPrice == 0 && maxPrice == 0 && numOfBeds == 0 && numOfBathrooms == 0 && numOfBedrooms == 0
                && stayType == 0 && propertyType == 0 && order == null && country == null && city == null) {
            throw new BadRequestException("No filter selected!");
        }
        if(minPrice > maxPrice) {
            throw new BadRequestException("Max price should be larger than min price!");
        }
        return stayDAO.filterStays(stayFilterDTO);
    }

}
