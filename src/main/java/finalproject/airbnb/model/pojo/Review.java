package finalproject.airbnb.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private long id;
    private long userId;
    private long stayId;
    private String comment;
    private int cleanlinessRating;
    private int checkInRating;
    private int accuracyRating;
    private int communicationRating;
    private int valueRating;
    private int locationRating;

}
