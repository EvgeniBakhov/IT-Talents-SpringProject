package finalproject.airbnb.controller;


import finalproject.airbnb.model.dao.StayDAO;
import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.StayDTO;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@RestController
public class StayController {

    @Autowired
    private StayDAO stayDAO;

    @PostMapping("/stays")
    public Stay addStay(@RequestBody StayDTO stayDTO , HttpSession session) throws SQLException {
        //User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        User user = null;
        Stay stay = new Stay(stayDTO);
        stay.setHost(user);
        stayDAO.addStay(stay);
        return stay;
    }

    @GetMapping("/stays/{id}")
    public GetStayDTO getStay(@PathVariable long id) throws SQLException {
        GetStayDTO getStayDTO = stayDAO.getStayById(id);
        if(getStayDTO == null) {
            //no stay
        }
        return getStayDTO;
    }

    @DeleteMapping("/stays/{id}")
    public String deleteStay(@PathVariable long id) throws SQLException {
        if(stayDAO.getStayById(id) == null) {
            //no stay
        }
        return stayDAO.deleteStay(id);
    }

}
