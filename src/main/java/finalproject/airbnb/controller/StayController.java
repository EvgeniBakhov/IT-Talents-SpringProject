package finalproject.airbnb.controller;


import finalproject.airbnb.exceptions.AuthorizationException;
import finalproject.airbnb.exceptions.BadRequestException;
import finalproject.airbnb.exceptions.NotFoundException;
import finalproject.airbnb.model.dao.StayDAO;
import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.StayDTO;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
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

    @PostMapping("/stays")
    public Stay addStay(@RequestBody StayDTO stayDTO , HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null) {
            throw new AuthorizationException();
        }
        if(!stayValidator.isValidLocation(stayDTO.getStreetAddress()) ||
            !stayValidator.isValidLocation(stayDTO.getCity()) ||
            !stayValidator.isValidLocation(stayDTO.getCountry())) {
            throw new BadRequestException("Invalid location!");
        }
        if(!stayValidator.isValidDescription(stayDTO.getDescription())) {
            throw new BadRequestException("Invalid description!");
        }
        if(!stayValidator.isValidPrice(stayDTO.getPrice())) {
            throw new BadRequestException("Invalid price!");
        }
        if(!stayValidator.isValidTitle(stayDTO.getTitle())) {
            throw new BadRequestException("Invalid title!");
        }
        if(!stayValidator.isValidNumOfBathrooms(stayDTO.getNumOfBathrooms()) ||
            !stayValidator.isValidNumOfBeds(stayDTO.getNumOfBeds()) ||
            !stayValidator.isValidNumOfBedrooms(stayDTO.getNumOfBedrooms())) {
            throw new BadRequestException("Number must be between 1 and 50!");
        }
        Stay stay = new Stay(stayDTO);
        stay.setHost(user);
        stayDAO.addStay(stay);
        return stay;
    }

    @GetMapping("/stays/{id}")
    public GetStayDTO getStay(@PathVariable long id) throws SQLException {
        GetStayDTO getStayDTO = stayDAO.getStayById(id);
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
        if(user.getId() != stayDAO.getHostId(id)) {
            throw new AuthorizationException("You don't have permissions to delete this stay!");
        }
        if(stayDAO.getStayById(id) == null) {
            throw new NotFoundException("Stay not found");
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


}
