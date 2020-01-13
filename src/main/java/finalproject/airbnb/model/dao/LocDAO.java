package finalproject.airbnb.model.dao;

import finalproject.airbnb.model.pojo.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class LocDAO {
    public static final String ADD_LOCATION_SQL = "INSERT INTO locations (street_address, city, country_id) VALUES(?,?,?);";
    public static final String GET_COUNTRY_ID_SQL = "SELECT id FROM countries WHERE country_name = ?;";
    public static final String ADD_COUNTRY_SQL = "INSERT INTO countries (country_name) VALUES (?);";
    public static final String GET_LOCATION_BY_ID = "SELECT l.street_address AS street_address, l.city AS city, c.country_name AS country FROM locations AS l JOIN countries AS c ON (l.country_id = c.id) WHERE l.id = ?;";
    public static final String EDIT_LOCATION_SQL = "UPDATE locations SET street_address = ?, city = ?, country_id = ? WHERE id = ?";
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public long addLocation(Location location) throws SQLException {
        long countryID = getCountryId(location.getCountry());
        if(countryID == -1) {
            countryID = addCountry(location.getCountry());
        }
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_LOCATION_SQL, Statement.RETURN_GENERATED_KEYS )) {
            statement.setString(1, location.getAddress());
            statement.setString(2, location.getCity());
            statement.setLong(3, countryID);
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            location.setId(result.getLong(1));
            return result.getLong(1);
        }
    }

    private long getCountryId(String country) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_COUNTRY_ID_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, country);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return result.getLong(1);
            }
            return -1;
        }
    }

    private long addCountry(String country) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_COUNTRY_SQL)) {
            statement.setString(1, country);
            statement.executeUpdate();
            return getCountryId(country);
        }
    }


    public Location getLocationById(long id) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_LOCATION_BY_ID)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            result.next();
            return new Location(id, result.getString("street_address"), result.getString("city"), result.getString("country"));
        }
  }

    public Location editLocation (Location location) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(EDIT_LOCATION_SQL)) {
            long countryID = getCountryId(location.getCountry());
            if(countryID == -1) {
                countryID = addCountry(location.getCountry());
            }
            statement.setString(1, location.getAddress());
            statement.setString(2, location.getCity());
            statement.setLong(3, countryID);
            statement.setLong(4, location.getId());
            statement.executeUpdate();
            return location;
        }
    }
}

