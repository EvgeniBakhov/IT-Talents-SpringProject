package finalproject.airbnb.model.pojo;

import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.StayDTO;
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
        HOUSE(1), APARTMENT(2), BED_AND_BREAKFAST(3), BOUTIQUE_HOTEL(4),
        CABIN(5), CHALET(6), COTTAGE(7), HOSTEL(8), HOTEL(9), LOFT(10), VILLA(11);

        private final int propertyTypeId;

        propertyType(int propertyTypeId) {
            this.propertyTypeId = propertyTypeId;
        }

        public int getPropertyTypeId() {
            return propertyTypeId;

        }
    }

    public enum stayType {
        ENTIRE_PLACE(1), PRIVATE_ROOM(2), HOTEL_ROOM(3), SHARED_ROOM(4);

        private final long typeId;

        stayType(long typeId) {
            this.typeId = typeId;
        }

        public long getTypeId() {
            return typeId;
        }
    }

    private long id;
    private double price;
    private double rating;
    private String description;
    private String title;
    private boolean instantBook;
    private User host;
    private Location location;
    private String rules;
    private int numOfBeds;
    private int numOfBedrooms;
    private int numOfBathrooms;
    private long stayTypeId;
    private long propertyTypeId;

    public Stay(StayDTO stayDTO) {
        setPrice(stayDTO.getPrice());
        setDescription(stayDTO.getDescription());
        setTitle(stayDTO.getTitle());
        setInstantBook(stayDTO.isInstantBook());
        setLocation(new Location(stayDTO.getStreetAddress(),stayDTO.getCity(),stayDTO.getCountry()));
        setRules(stayDTO.getRules());
        setNumOfBeds(stayDTO.getNumOfBeds());
        setNumOfBedrooms(stayDTO.getNumOfBedrooms());
        setNumOfBathrooms(stayDTO.getNumOfBathrooms());
        setStayTypeId(stayDTO.getStayTypeId());
        setPropertyTypeId(stayDTO.getPropertyTypeId());
    }

}
