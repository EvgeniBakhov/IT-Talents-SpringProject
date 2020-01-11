package finalproject.airbnb.utilities;

import finalproject.airbnb.model.pojo.Location;
import org.springframework.stereotype.Component;

@Component
public class StayValidator {

    public static final int MAX_STAY_PRICE = 100000;
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_BEDROOMS = 50;
    public static final int MAX_BEDS = 50;
    public static final int MAX_BATHROOMS = 50;

    public boolean isValidPrice(double price) {
        if(price >= 0 && price < MAX_STAY_PRICE) {
            return true;
        }
        return false;
    }

    public boolean isValidTitle(String title) {
        if(title != null && !title.isEmpty() && title.length() <= MAX_TITLE_LENGTH) {
            return true;
        }
        return false;
    }

    public boolean isValidNumOfBeds(int numOfBeds) {
        if(numOfBeds >= 1 && numOfBeds <= MAX_BEDS) {
            return true;
        }
        return false;
    }

    public boolean isValidNumOfBedrooms(int numOfBedrooms) {
        if(numOfBedrooms >= 1 && numOfBedrooms <= MAX_BEDROOMS) {
            return true;
        }
        return false;
    }

    public boolean isValidNumOfBathrooms(int numOfBathrooms) {
        if(numOfBathrooms >= 1 && numOfBathrooms <= MAX_BATHROOMS) {
            return true;
        }
        return false;
    }

}
