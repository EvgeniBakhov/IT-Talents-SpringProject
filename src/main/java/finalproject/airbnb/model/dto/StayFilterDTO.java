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
    private String stayType;
    private String propertyType;
    private String order;

}
