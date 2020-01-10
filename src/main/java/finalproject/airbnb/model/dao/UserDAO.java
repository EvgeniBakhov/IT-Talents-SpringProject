package finalproject.airbnb.model.dao;

import finalproject.airbnb.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;


@Component
public class UserDAO {

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
        String sql = ADD_USER_SQL;
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try ( PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); ) {
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
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "DELETE FROM users WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setLong(1, user.getId());
            statement.executeUpdate();
            return user;
        }
    }

    public User getUserById(long id) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "SELECT u.*, l.street_address AS address, l.city AS city, c.country_name AS country " +
                "FROM users AS u JOIN locations AS l ON(u.user_location_id = l.id)" +
                "JOIN countries AS c ON (l.country_id = c.id) WHERE u.id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
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
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "SELECT u.*, l.street_address AS address, l.city AS city, c.country_name AS country " +
                "FROM users AS u JOIN locations AS l ON(u.user_location_id = l.id)" +
                "JOIN countries AS c ON (l.country_id = c.id) WHERE u.email = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
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

    public User editUser(User user) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "UPDATE users SET first_name = ?, last_name = ?, " +
                "email = ?, birthday = ?, phone_number = ?, user_description = ?, profile_picture = ?, password = ? WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            statement.setString(5, user.getPhoneNumber());
            statement.setString(6, user.getUserDescription());
            statement.setString(7, user.getProfilePicture());
            statement.setString(8, user.getPassword());
            statement.setLong(9, user.getId());
            return user;
        }
    }

}
