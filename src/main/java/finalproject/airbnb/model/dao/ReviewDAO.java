package finalproject.airbnb.model.dao;

import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.ReviewDTO;
import finalproject.airbnb.model.dto.UserReviewDTO;
import finalproject.airbnb.model.pojo.Review;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewDAO {

    private static final String ADD_REVIEW_SQL = "INSERT INTO reviews (user_id, " +
            "stay_id, " +
            "comment_text, " +
            "cleanliness_rating, " +
            "check_in_rating, " +
            "accuracy_rating, " +
            "communication_rating, " +
            "value_rating, " +
            "location_rating) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String GET_REVIEW_BY_USER_ID_AND_STAY_ID = "SELECT id, user_id, stay_id, comment_text," +
            " cleanliness_rating, check_in_rating, accuracy_rating, communication_rating, value_rating, location_rating " +
            "FROM reviews WHERE user_id = ? AND stay_id = ?";
    private static final String DELETE_REVIEW_SQL = "DELETE FROM reviews WHERE id = ?;";
    private static final String GET_REVIEW_BY_ID_SQL = "SELECT id, user_id, stay_id, comment_text, cleanliness_rating, " +
            "check_in_rating, accuracy_rating, communication_rating, value_rating, location_rating FROM reviews WHERE id = ?";
    private static final String GET_REVIEW_BY_STAY_ID_SQL = "SELECT id, user_id, stay_id, comment_text, cleanliness_rating, " +
            "check_in_rating, accuracy_rating, communication_rating, value_rating, location_rating FROM reviews WHERE stay_id = ?";
    private static final String EDIT_REVIEW_SQL = "UPDATE reviews SET comment_text = ?, cleanliness_rating = ?, " +
            "check_in_rating = ?, accuracy_rating = ?, communication_rating = ?, value_rating = ?, location_rating = ? WHERE id = ?;";
    private static final String GET_REVIEWS_BY_USER_ID_SQL = "SELECT id, user_id, stay_id, comment_text, cleanliness_rating, " +
            "check_in_rating, accuracy_rating, communication_rating, value_rating, location_rating FROM reviews WHERE user_id = ?";

    public static final int RATING_TYPES_COUNT = 6;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private StayDAO stayDAO;

    public Review addReview(Review review) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try ( PreparedStatement statement = connection.prepareStatement(ADD_REVIEW_SQL, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            statement.setLong(1, review.getUser().getId());
            statement.setLong(2, review.getStayId());
            statement.setString(3, review.getComment());
            statement.setInt(4, review.getCleanlinessRating());
            statement.setInt(5, review.getCheckInRating());
            statement.setInt(6, review.getAccuracyRating());
            statement.setInt(7, review.getCommunicationRating());
            statement.setInt(8, review.getValueRating());
            statement.setInt(9, review.getLocationRating());
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            review.setId(result.getLong(1));
            updateStayRating(review);
            connection.commit();
            return review;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    public String deleteReview(long id) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_REVIEW_SQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
        return "Review deleted!";
    }

    public Review getReviewByStayIdAndUser(long stayId, User user) throws SQLException {
        try ( Connection connection = jdbcTemplate.getDataSource().getConnection();
              PreparedStatement statement = connection.prepareStatement(GET_REVIEW_BY_USER_ID_AND_STAY_ID)) {
            statement.setLong(1, user.getId());
            statement.setLong(2, stayId);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return new Review(result.getLong("id"),
                        result.getLong("stay_id"),
                        new UserReviewDTO(user),
                        result.getString("comment_text"),
                        result.getInt("cleanliness_rating"),
                        result.getInt("check_in_rating"),
                        result.getInt("accuracy_rating"),
                        result.getInt("communication_rating"),
                        result.getInt("value_rating"),
                        result.getInt("location_rating"));
            }
            return null;
        }
    }

    public Review getReviewById(long id) throws SQLException {
        try ( Connection connection = jdbcTemplate.getDataSource().getConnection();
              PreparedStatement statement = connection.prepareStatement(GET_REVIEW_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return new Review(result.getLong("id"),
                        result.getLong("stay_id"),
                        new UserReviewDTO(userDAO.getUserById(result.getLong("user_id"))),
                        result.getString("comment_text"),
                        result.getInt("cleanliness_rating"),
                        result.getInt("check_in_rating"),
                        result.getInt("accuracy_rating"),
                        result.getInt("communication_rating"),
                        result.getInt("value_rating"),
                        result.getInt("location_rating"));
            }
            return null;
        }
    }

    public List<Review> getReviewsByStayId(long stayId) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_REVIEW_BY_STAY_ID_SQL)) {
            statement.setLong(1, stayId);
            ResultSet result = statement.executeQuery();
            List<Review> reviews = new ArrayList<>();
            while (result.next()) {
                Review review = new Review(result.getLong("id"),
                        result.getLong("stay_id"),
                        new UserReviewDTO(userDAO.getUserById(result.getLong("user_id"))),
                        result.getString("comment_text"),
                        result.getInt("cleanliness_rating"),
                        result.getInt("check_in_rating"),
                        result.getInt("accuracy_rating"),
                        result.getInt("communication_rating"),
                        result.getInt("value_rating"),
                        result.getInt("location_rating"));
                reviews.add(review);
            }
            return reviews;
        }
    }

    public List<Review> getReviewsByUserId(long userId) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_REVIEWS_BY_USER_ID_SQL)) {
            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();
            List<Review> reviews = new ArrayList<>();
            while (result.next()) {
                Review review = new Review(result.getLong("id"),
                        result.getLong("stay_id"),
                        new UserReviewDTO(userDAO.getUserById(result.getLong("user_id"))),
                        result.getString("comment_text"),
                        result.getInt("cleanliness_rating"),
                        result.getInt("check_in_rating"),
                        result.getInt("accuracy_rating"),
                        result.getInt("communication_rating"),
                        result.getInt("value_rating"),
                        result.getInt("location_rating"));
                reviews.add(review);
            }
            return reviews;
        }
    }

    public ReviewDTO editReview(long id, ReviewDTO reviewDTO) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(EDIT_REVIEW_SQL)) {
            statement.setString(1, reviewDTO.getComment());
            statement.setInt(2, reviewDTO.getCleanlinessRating());
            statement.setInt(3, reviewDTO.getCheckInRating());
            statement.setInt(4, reviewDTO.getAccuracyRating());
            statement.setInt(5, reviewDTO.getCommunicationRating());
            statement.setInt(6, reviewDTO.getValueRating());
            statement.setInt(7, reviewDTO.getLocationRating());
            statement.setLong(8, id);
            statement.executeUpdate();
            return reviewDTO;
        }
    }

    private void updateStayRating(Review review) throws SQLException {
        GetStayDTO stay = stayDAO.getStayById(review.getStayId());
        double currentStayRating = stay.getRating();
        int numberOfStayReviews = getReviewsByStayId(stay.getId()).size();
        double avgReviewRating = (double)(review.getCleanlinessRating() + review.getAccuracyRating() +
                review.getCommunicationRating() + review.getCheckInRating() +
                review.getLocationRating() + review.getValueRating()) / RATING_TYPES_COUNT;
        double updatedRating = ((numberOfStayReviews * currentStayRating) + avgReviewRating ) / (numberOfStayReviews + 1);
        stayDAO.updateRating(stay.getId(), updatedRating);
    }

}
