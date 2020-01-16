package finalproject.airbnb.model.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private long id;
    private String address;
    private String city;
    private String country;

    public Location(String address, String city, String country) {
        this.address = address;
        this.city = city;
        this.country = country;
    }
}
