package finalproject.airbnb.controller;

import finalproject.airbnb.exceptions.AuthorizationException;
import finalproject.airbnb.exceptions.BadRequestException;
import finalproject.airbnb.exceptions.NotFoundException;
import finalproject.airbnb.model.dao.ReviewDAO;
import finalproject.airbnb.model.dao.BookingDAO;
import finalproject.airbnb.model.dto.LoginUserDTO;
import finalproject.airbnb.model.dto.RegisterUserDTO;
import finalproject.airbnb.model.dto.UserWithoutPassDTO;
import finalproject.airbnb.model.pojo.Review;
import finalproject.airbnb.model.pojo.Booking;
import finalproject.airbnb.model.pojo.User;
import finalproject.airbnb.model.dao.UserDAO;
import finalproject.airbnb.utilities.LocationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import finalproject.airbnb.utilities.UserValidator;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;


@RestController
public class UserController extends AbstractController{

    public static final String SESSION_KEY_LOGGED_USER = "logged_user";

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private BookingDAO bookingDAO;
    @Autowired
    private LocationValidator locationValidator;
    @Autowired
    private ReviewDAO reviewDAO;

    @PostMapping("/register")
    public UserWithoutPassDTO registerUser(@RequestBody RegisterUserDTO registerUserDTO, HttpSession session) throws SQLException {
        if(!registerUserDTO.getPassword().equals(registerUserDTO.getConfirmPassword())){
            throw new BadRequestException("Passwords don't match.");
        }
        User user = new User(registerUserDTO);
        if(!userValidator.isValidName(user.getFirstName()) || !userValidator.isValidName(user.getLastName())){
            throw new BadRequestException("Your first name and your last name must contain from 2 to 30 symbols.");
        }
        if(!userValidator.isValidEmail(user.getEmail())){
            throw new BadRequestException("Email is not valid.");
        }
        if(!userValidator.isValidPassword(user.getPassword())){
            throw new BadRequestException("Your password must contain at least 8 characters and at least 1 uppercase letter.");
        }
        if(!userValidator.isValidBirthday(user.getBirthday())){
            throw new BadRequestException("You must be at least 18 years old to register.");
        }
        if(!userValidator.isValidPhoneNumber(user.getPhoneNumber())){
            throw new BadRequestException("Your number must contain from 11 to 15 characters and start with '+'");
        }
        if(!locationValidator.isValidLocation(user.getLocation())){
            throw new BadRequestException("Invalid address or city or country name.");
        }
        userDAO.addUser(user);
        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
        UserWithoutPassDTO registeredUser = new UserWithoutPassDTO(user);
        return registeredUser;
    }

    @PostMapping("/login")
    public UserWithoutPassDTO loginUser(@RequestBody LoginUserDTO loginUserDTO, HttpSession session) throws SQLException {
        UserWithoutPassDTO loggedUser;
        User user = userDAO.getUserByEmail(loginUserDTO.getEmail());
        if(user == null){
            throw new AuthorizationException("Wrong credentials.");
        }else{
            if(user.getPassword().equals(loginUserDTO.getPassword())){
                //You're logged!
                session.setAttribute(SESSION_KEY_LOGGED_USER, user);
                loggedUser = new UserWithoutPassDTO(user);
            }else{
                throw new AuthorizationException("Wrong credentials.");
            }
        }
        return loggedUser;
    }
    @PostMapping("/user/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "You've logged out.";
    }

    @GetMapping("/users/{id}")
    public UserWithoutPassDTO getUserById(@PathVariable long id) throws SQLException {
        User user = userDAO.getUserById(id);
        if(user==null){
            throw new NotFoundException("There is no user with this id.");
        }
        return new UserWithoutPassDTO(user);
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable long id, HttpSession session) throws SQLException {
        User loggedUser = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(loggedUser==null){
            throw new AuthorizationException("You must log in.");
        }
        if(loggedUser.getId()!=id){
            throw new AuthorizationException("You don't have permissions to delete this user.");
        }
        User user = userDAO.getUserById(id);
        if(user==null){
            throw new NotFoundException("There is no user with this id");
        }
        userDAO.deleteUser(user);
        return "User has been deleted";
    }

    @PutMapping("/users/{id}")
    public UserWithoutPassDTO editUser(@PathVariable long id, HttpSession session, @RequestBody RegisterUserDTO editedUserData ) throws SQLException {
        User user = (User) session.getAttribute(SESSION_KEY_LOGGED_USER);
        if(user==null){
            throw new AuthorizationException("You must log in.");
        }
        if(user.getId()!=id){
            throw new AuthorizationException("You don't have permissions to delete edit this user.");
        }
        return userDAO.editUser(user);
    }

    @GetMapping("/users/{id}/reviews")
    public List<Review> getUserReviews(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null) {
            throw new AuthorizationException();
        }
        List<Review> reviews = reviewDAO.getReviewsByUserId(id);
        if(reviews.isEmpty()) {
            throw new NotFoundException("No reviews made by user");
        }
        return reviews;
    }

    @GetMapping("/users/{id}/bookings")
    public List<Booking> getAllBookingsByUser(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user==null){
            throw new AuthorizationException();
        }
        if(user.getId()!=id){
            throw new AuthorizationException("You have no permissions to see bookings of this user.");
        }
        List<Booking> allBookings;
        allBookings = bookingDAO.getAllBookingsByUserId(user.getId());
        if(allBookings == null){
            throw new NotFoundException("You haven't any bookings yet.");
        }
        return allBookings;
    }


}
