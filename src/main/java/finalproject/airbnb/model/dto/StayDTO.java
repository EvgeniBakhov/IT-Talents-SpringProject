package finalproject.airbnb.model.dto;

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
public class StayDTO {

    private double price;
    private String description;
    private String title;
    private boolean instantBook;
    private String streetAddress;
    private String city;
    private String country;
    private String rules;
    private int numOfBeds;
    private int numOfBedrooms;
    private int numOfBathrooms;
    private long stayTypeId;
    private long propertyTypeId;

}
