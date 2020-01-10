package finalproject.airbnb.model.dao;

import finalproject.airbnb.model.pojo.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class LocDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public long addLocation(Location location) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "INSERT INTO locations (street_address, city, country_id) VALUES(?,?,?);";
        long countryID = getCountryId(location.getCountry());
        if(countryID == -1) {
            countryID = addCountry(location.getCountry());
        }
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS )) {
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
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "SELECT id FROM countries WHERE country_name = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, country);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return result.getLong(1);
            }
            return -1;
        }
    }

    private long addCountry(String country) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "INSERT INTO countries (country_name) VALUES (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, country);
            statement.executeUpdate();
            return getCountryId(country);
        }
    }


    public Location getLocationById(long id) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        ResultSet result;
        String sql = "SELECT l.street_address AS street_address, l.city AS city, c.country_name AS country FROM locations AS l JOIN countries AS c ON (l.country_id = c.id) WHERE l.id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            result = statement.executeQuery();
            result.next();
            return new Location(id, result.getString("street_address"), result.getString("city"), result.getString("country"));
        }
  }

    public Location editLocation (Location location) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "UPDATE locations SET street_address = ?, city = ?, country_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, location.getAddress());
            statement.setString(2, location.getCity());
            statement.setLong(3, getCountryId(location.getCountry()));
            statement.setLong(4, location.getId());
            statement.executeUpdate();
            return location;
        }
    }
}

