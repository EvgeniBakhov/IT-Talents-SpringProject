package finalproject.airbnb.controller;


import finalproject.airbnb.model.dao.StayDAO;
import finalproject.airbnb.model.dto.StayDTO;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

}
