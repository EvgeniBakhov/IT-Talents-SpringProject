package finalproject.airbnb.model.dao;

import finalproject.airbnb.model.dto.UserWithoutPassDTO;
import finalproject.airbnb.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;


@Component
public class UserDAO {

    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?;";
    private static final String GET_USER_BY_ID_SQL = "SELECT u.*, l.street_address AS address, l.city AS city, c.country_name AS country " +
            "FROM users AS u JOIN locations AS l ON(u.user_location_id = l.id)" +
            "JOIN countries AS c ON (l.country_id = c.id) WHERE u.id = ?;";
    private static final String GET_USER_BY_EMAIL_SQL = "SELECT u.*, l.street_address AS address, l.city AS city, c.country_name AS country " +
            "FROM users AS u JOIN locations AS l ON(u.user_location_id = l.id)" +
            "JOIN countries AS c ON (l.country_id = c.id) WHERE u.email = ?;";
    private static final String EDIT_USER_SQL = "UPDATE users SET first_name = ?, last_name = ?, " +
            "email = ?, birthday = ?, phone_number = ?, user_description = ?, profile_picture = ?, password = ? WHERE id = ?;";
    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static final String ADD_USER_SQL = "INSERT INTO users (email, " +
            "first_name, " +
            "last_name, " +
            "birthday," +
            " phone_number, " +
            "join_date, " +
            "user_description, " +
            "user_location_id, " +
            "profile_picture, " +
            "password) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    @Autowired
    private LocDAO locationDAO;

    public User addUser(User user) throws SQLException {
        try ( Connection connection = jdbcTemplate.getDataSource().getConnection();
              PreparedStatement statement = connection.prepareStatement(ADD_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            statement.setString(5, user.getPhoneNumber());
            statement.setDate(6, Date.valueOf(LocalDate.now()));
            statement.setString(7, user.getUserDescription());
            statement.setLong(8, locationDAO.addLocation(user.getLocation()));
            statement.setString(9, user.getProfilePicture());
            statement.setString(10, user.getPassword());
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            user.setId(result.getLong(1));
            return user;
        }
    }

    public User deleteUser(User user) throws SQLException {
        try ( Connection connection = jdbcTemplate.getDataSource().getConnection();
              PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL);) {
            statement.setLong(1, user.getId());
            statement.executeUpdate();
            return user;
        }
    }

    public User getUserById(long id) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_USER_BY_ID_SQL);) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return new User(
                        result.getLong("id"),
                        result.getString("email"),
                        result.getString("password"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getDate("birthday").toLocalDate(),
                        result.getString("phone_number"),
                        result.getDate("join_date").toLocalDate(),
                        result.getString("user_description"),
                        locationDAO.getLocationById(result.getLong("user_location_id")),
                        result.getString("profile_picture"));
            }
            else {
                return null;
            }
        }
    }

    public User getUserByEmail(String email) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_USER_BY_EMAIL_SQL)) {
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return new User(
                        result.getLong("id"),
                        result.getString("email"),
                        result.getString("password"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getDate("birthday").toLocalDate(),
                        result.getString("phone_number"),
                        result.getDate("join_date").toLocalDate(),
                        result.getString("user_description"),
                        locationDAO.getLocationById(result.getLong("user_location_id")),
                        result.getString("profile_picture"));
            }
            else {
                return null;
            }
        }
    }

    public UserWithoutPassDTO editUser(User user) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(EDIT_USER_SQL)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            statement.setString(5, user.getPhoneNumber());
            statement.setString(6, user.getUserDescription());
            statement.setString(7, user.getProfilePicture());
            statement.setString(8, user.getPassword());
            statement.setLong(9, user.getId());
            statement.executeUpdate();
            locationDAO.editLocation(user.getLocation());
            return new UserWithoutPassDTO(getUserById(user.getId()));
        }
    }

}
