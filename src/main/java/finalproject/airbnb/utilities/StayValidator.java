package finalproject.airbnb.utilities;

import finalproject.airbnb.model.pojo.Location;
import org.springframework.stereotype.Component;

@Component
public class StayValidator {

    private static final int MAX_STAY_PRICE = 100000;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_BEDROOMS = 50;
    private static final int MAX_BEDS = 50;
    private static final int MAX_BATHROOMS = 50;

    public boolean isValidPrice(double price) {
        return price >= 0 && price < MAX_STAY_PRICE;
    }

    public boolean isValidTitle(String title) {
        return title != null && !title.isEmpty() && title.length() <= MAX_TITLE_LENGTH;
    }

    public boolean isValidNumOfBeds(int numOfBeds) {
        return numOfBeds >= 1 && numOfBeds <= MAX_BEDS;
    }

    public boolean isValidNumOfBedrooms(int numOfBedrooms) {
        return numOfBedrooms >= 1 && numOfBedrooms <= MAX_BEDROOMS;
    }

    public boolean isValidNumOfBathrooms(int numOfBathrooms) {
        return numOfBathrooms >= 1 && numOfBathrooms <= MAX_BATHROOMS;
    }

}
