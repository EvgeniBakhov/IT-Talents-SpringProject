package finalproject.airbnb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private String comment;
    private int cleanlinessRating;
    private int checkInRating;
    private int accuracyRating;
    private int communicationRating;
    private int valueRating;
    private int locationRating;

}
