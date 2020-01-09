package finalproject.airbnb.model.pojo;

import finalproject.airbnb.model.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stay {

    public enum propertyType {
        HOUSE(1), APARTMENT(2), BED_AND_BREAKFAST(2), BOUTIQUE_HOTEL(3), BUNGALOW(4),
        CABIN(5), CHALET(6), COTTAGE(7), HOSTEL(8), HOTEL(9), LOFT(10), VILLA(11);

        private final int propertyTypeId;

        private propertyType(int propertyTypeId) {
            this.propertyTypeId = propertyTypeId;
        }

        public int getPropertyTypeId() {
            return propertyTypeId;
        }
    };

    public enum stayType {
        ENTIRE_PLACE(1), PRIVATE_ROOM(2), HOTEL_ROOM(3), SHARED_ROOM(4);

        private final int typeId;

        private stayType(int typeId) {
            this.typeId = typeId;
        }

        public int getTypeId() {
            return typeId;
        }
    };

    private long id;
    private double price;
    private double rating;
    private String description;
    private String title;
    private boolean instantBook;
    private User host;
    private String streetAddress;
    private String city;
    private String country;
    private String rules;
    private int numOfBeds;
    private int numOfBedrooms;
    private int numOfBathrooms;
    private stayType stayType;
    private propertyType propertyType;

}
