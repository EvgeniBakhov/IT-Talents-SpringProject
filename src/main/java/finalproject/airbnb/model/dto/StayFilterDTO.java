package finalproject.airbnb.model.dto;


import finalproject.airbnb.model.pojo.Stay;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StayFilterDTO {

    private double minPrice;
    private double maxPrice;
    private int numOfBeds;
    private int numOfBedrooms;
    private int numOfBathrooms;
    private long stayTypeId;
    private long propertyTypeId;
    private String order;
    private String city;
    private String country;

}
