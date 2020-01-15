package finalproject.airbnb.model.dto;

import finalproject.airbnb.model.dao.StayDAO;
import finalproject.airbnb.model.pojo.Location;
import finalproject.airbnb.model.pojo.Stay;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetStayDTO {

    private long id;
    private String hostFirstName;
    private String hostLastName;
    private String hostProfilePicture;
    private Location location;
    private double price;
    private double rating;
    private String description;
    private String title;
    private String stayType;
    private boolean instantBook;
    private String propertyType;
    private String rules;
    private int numOfBeds;
    private int numOfBedrooms;
    private int numOfBathrooms;


}
