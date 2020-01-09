package finalproject.airbnb.controller;

import finalproject.airbnb.exceptions.UserDataException;
import finalproject.airbnb.model.dto.LoginUserDTO;
import finalproject.airbnb.model.dto.RegisterUserDTO;
import finalproject.airbnb.model.dto.UserWithoutPassDTO;
import finalproject.airbnb.model.pojo.User;
import finalproject.airbnb.model.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import finalproject.airbnb.utilities.UserValidations;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Component
@RestController
public class UserController {

    public static final String SESSION_KEY_LOGGED_USER = "logged_user";

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserValidations userValidations;

    @PostMapping("/register")
    public UserWithoutPassDTO registerUser(@RequestBody RegisterUserDTO registerUserDTO, HttpSession session) throws SQLException {
        //TODO validations, exception handling
        User user = new User(registerUserDTO);
        userDAO.addUser(user);
        session.setAttribute(SESSION_KEY_LOGGED_USER, user);
        UserWithoutPassDTO registeredUser = new UserWithoutPassDTO(user);
        return registeredUser;
    }

    @PostMapping("/login")
    public UserWithoutPassDTO loginUser(@RequestBody LoginUserDTO loginUserDTO, HttpSession session) throws SQLException {
        //TODO exception handling, validations
        UserWithoutPassDTO loggedUser = null;
        User user = userDAO.getUserByEmail(loginUserDTO.getEmail());
        if(user == null){
            //user doesn't exist, wrong credentials
        }else{
            if(user.getPassword().equals(loginUserDTO.getPassword())){
                //You're logged!
                session.setAttribute(SESSION_KEY_LOGGED_USER, user);
                loggedUser = new UserWithoutPassDTO(user);
            }else{
                //wrong credentials
            }
        }
        return loggedUser;
    }



}