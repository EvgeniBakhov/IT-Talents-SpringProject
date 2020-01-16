package finalproject.airbnb.utilities;

import finalproject.airbnb.model.pojo.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationValidator {

    public boolean isValidLocation(Location location) {
        if(!location.getAddress().matches("[a-zA-Z0-9-. ]{5,100}$")){
            return false;
        }
        if(!location.getCity().matches("[a-zA-Z- ]{3,30}$")){
            return false;
        }
        if(!location.getCountry().matches("[a-zA-Z- ]{3,20}")){
            return false;
        }
        return true;
    }

}
