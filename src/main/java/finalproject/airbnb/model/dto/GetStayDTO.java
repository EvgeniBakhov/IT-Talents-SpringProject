package finalproject.airbnb.model.dto;

import finalproject.airbnb.model.pojo.Location;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public GetStayDTO(ResultSet result) throws SQLException {
        this(result.getString("u.first_name"),
                result.getString("u.last_name"),
                result.getString("u.profile_picture"),
                new Location(result.getString("l.street_address"),
                        result.getString("l.city"),
                        result.getString("c.country_name")),
                result.getDouble("s.price"),
                result.getDouble("s.rating"),
                result.getString("s.stay_description"),
                result.getString("s.title"),
                result.getString("p.type_name"),
                result.getBoolean("s.instant_book"),
                result.getString("pr.property_type_name"),
                result.getString("s.rules"),
                result.getInt("s.num_of_beds"),
                result.getInt("s.num_of_bedrooms"),
                result.getInt("s.num_of_bathrooms"));
    }

    public GetStayDTO(Stay stay){
        setHostFirstName(stay.getHost().getFirstName());
        setHostLastName(stay.getHost().getLastName());
        setDescription(stay.getDescription());
        setHostProfilePicture(stay.getHost().getProfilePicture());
        setInstantBook(stay.isInstantBook());
        setLocation(stay.getLocation());
        setNumOfBathrooms(stay.getNumOfBathrooms());
        setNumOfBedrooms(stay.getNumOfBedrooms());
        setNumOfBeds(stay.getNumOfBeds());
        setPrice(stay.getPrice());
        setPropertyType(stay.getPropertyType().toString());
        setRules(stay.getRules());
        setRating(stay.getRating());
        setTitle(stay.getTitle());
        setType(stay.getStayType().toString());
    }

}
