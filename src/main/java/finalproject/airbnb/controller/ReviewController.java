package finalproject.airbnb.controller;

import finalproject.airbnb.exceptions.AuthorizationException;
import finalproject.airbnb.exceptions.BadRequestException;
import finalproject.airbnb.exceptions.NotFoundException;
import finalproject.airbnb.model.dao.ReviewDAO;
import finalproject.airbnb.model.dao.StayDAO;
import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.ReviewDTO;
import finalproject.airbnb.model.dto.UserReviewDTO;
import finalproject.airbnb.model.pojo.Review;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
@RestController
public class ReviewController extends AbstractController {

    public static final int RATING_TYPES_COUNT = 6;
    @Autowired
    private ReviewDAO reviewDAO;
    @Autowired
    private StayDAO stayDAO;

    @PostMapping("/stays/{id}/reviews")
    public Review addReview(@RequestBody ReviewDTO reviewDTO, @PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null) {
            throw new AuthorizationException();
        }
        GetStayDTO stay = stayDAO.getStayById(id);
        if(stay == null) {
            throw new NotFoundException("Stay not found");
        }
        if(user.getId() == stayDAO.getHostId(id)){
            throw new AuthorizationException("You can't make a review to your own stay.");
        }
        if(reviewDAO.getReviewByStayIdAndUser(id,user) != null) {
            throw new BadRequestException("You have already added a review");
        }
        Review review = new Review(reviewDTO);
        review.setUser(new UserReviewDTO(user));
        review.setStayId(id);
        reviewDAO.addReview(review);
        updateStayRating(review);
        return review;
    }

    @DeleteMapping("reviews/{id}")
    public String deleteReview(@PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null) {
            throw new AuthorizationException();
        }
        Review review = reviewDAO.getReviewById(id);
        if(review == null) {
            throw new NotFoundException("Review not found");
        }
        if(user.getId() != review.getUser().getId()) {
            throw new AuthorizationException("You don't have permissions to delete this review!");
        }
        reviewDAO.deleteReview(id);
        return "Review deleted!";
    }

    @PutMapping("reviews/{id}")
    public ReviewDTO editReview(@RequestBody ReviewDTO reviewDTO, @PathVariable long id, HttpSession session) throws SQLException {
        User user = (User) session.getAttribute(UserController.SESSION_KEY_LOGGED_USER);
        if(user == null) {
            throw new AuthorizationException();
        }
        Review review = reviewDAO.getReviewById(id);
        if(review == null) {
            throw new NotFoundException("Review not found");
        }
        if(user.getId() != review.getUser().getId()) {
            throw new AuthorizationException("You don't have permissions to edit this review!");
        }
        return reviewDAO.editReview(id, reviewDTO);
    }

    private void updateStayRating(Review review) throws SQLException {
        GetStayDTO stay = stayDAO.getStayById(review.getStayId());
        double currentStayRating = stay.getRating();
        int numberOfStayReviews = reviewDAO.getReviewsByStayId(stay.getId()).size();
        double avgReviewRating = (double)(review.getCleanlinessRating() + review.getAccuracyRating() +
                review.getCommunicationRating() + review.getCheckInRating() +
                review.getLocationRating() + review.getValueRating()) / RATING_TYPES_COUNT;
        double updatedRating = ((numberOfStayReviews * currentStayRating) + avgReviewRating ) / (numberOfStayReviews + 1);
        stayDAO.updateRating(stay.getId(), updatedRating);
    }

}
