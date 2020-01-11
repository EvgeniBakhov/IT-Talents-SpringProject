package finalproject.airbnb.utilities;

import finalproject.airbnb.model.pojo.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationValidator {

    public boolean isValidLocation(Location location) {
        if(!location.getAddress().matches("[A-z0-9-. ]{10,100}$")){
            return false;
        }
        if(!location.getCity().matches("[A-z- ]{3,30}$")){
            return false;
        }
        if(!location.getCountry().matches("[A-z- ]{3,20}")){
            return false;
        }
        return true;
    }

}
