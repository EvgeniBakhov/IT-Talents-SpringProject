package finalproject.airbnb.model.pojo;

import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.ReviewDTO;
import finalproject.airbnb.model.dto.UserReviewDTO;
import finalproject.airbnb.model.dto.UserWithoutPassDTO;
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
    private long stayId;
    private UserReviewDTO user;
    private String comment;
    private int cleanlinessRating;
    private int checkInRating;
    private int accuracyRating;
    private int communicationRating;
    private int valueRating;
    private int locationRating;

    public Review(ReviewDTO reviewDTO) {
        setComment(reviewDTO.getComment());
        setCleanlinessRating(reviewDTO.getCleanlinessRating());
        setCheckInRating(reviewDTO.getCheckInRating());
        setAccuracyRating(reviewDTO.getAccuracyRating());
        setCommunicationRating(reviewDTO.getCommunicationRating());
        setValueRating(reviewDTO.getValueRating());
        setLocationRating(reviewDTO.getLocationRating());
    }
}
