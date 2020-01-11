package finalproject.airbnb.model.dao;

import finalproject.airbnb.model.dto.UserReviewDTO;
import finalproject.airbnb.model.pojo.Review;
import finalproject.airbnb.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class ReviewDAO {

    public static final String ADD_REVIEW_SQL = "INSERT INTO reviews (user_id, " +
            "stay_id, " +
            "comment_text, " +
            "cleanliness_rating, " +
            "check_in_rating, " +
            "accuracy_rating, " +
            "communication_rating, " +
            "value_rating, " +
            "location_rating) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    public static final String GET_REVIEW_BY_USER_ID_AND_STAY_ID = "SELECT id, user_id, stay_id, comment_text," +
            " cleanliness_rating, check_in_rating, accuracy_rating, communication_rating, value_rating, location_rating " +
            "FROM reviews WHERE user_id = ? AND stay_id = ?";
    public static final String DELETE_REVIEW_SQL = "DELETE FROM reviews WHERE id = ?;";
    public static final String GET_REVIEW_BY_ID_SQL = "SELECT id, user_id, stay_id, comment_text, cleanliness_rating, " +
            "check_in_rating, accuracy_rating, communication_rating, value_rating, location_rating FROM reviews WHERE id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserDAO userDAO;

    public Review addReview(Review review) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try ( PreparedStatement statement = connection.prepareStatement(ADD_REVIEW_SQL, Statement.RETURN_GENERATED_KEYS)) {
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
            return review;
        }
    }

    public String deleteReview(long id) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_REVIEW_SQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
        return "Review deleted!";
    }

    public Review getReviewByStayIdAndUser(long stayId, User user) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try ( PreparedStatement statement = connection.prepareStatement(GET_REVIEW_BY_USER_ID_AND_STAY_ID)) {
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
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try ( PreparedStatement statement = connection.prepareStatement(GET_REVIEW_BY_ID_SQL)) {
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



}
