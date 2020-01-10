package finalproject.airbnb.model.dto;

import finalproject.airbnb.model.pojo.Location;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetStayDTO {

    private String hostFirstName;
    private String hostLastName;
    private String hostProfilePicture;
    private Location location;
    private double price;
    private double rating;
    private String description;
    private String title;
    private String type;
    private boolean instantBook;
    private String propertyType;
    private String rules;
    private int numOfBeds;
    private int numOfBedrooms;
    private int numOfBathrooms;

}
