package finalproject.airbnb.model.dao;


import finalproject.airbnb.managers.DBManager;
import finalproject.airbnb.model.pojo.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LocDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public long addLocation(Location location) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "INSERT INTO locations (street_address, city, country_id) VALUES(?,?,?);";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, location.getAddress());
            statement.setString(2, location.getCity());
            statement.setLong(3, getCountryId(location.getCountry()));
            statement.executeUpdate();
            return statement.getGeneratedKeys().getLong("id");
        }
    }

    private long getCountryId(String country) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "SELECT id FROM countries WHERE country_name = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, country);
            ResultSet result = statement.executeQuery();
            if (result == null) {
                return addCountry(country);
            }
            return result.getLong("id");
        }
    }

    private long addCountry(String country) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "INSERT INTO countries (country_name) VALUES (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, country);
            statement.executeUpdate();
            return getCountryId(country);
        }
    }


    public Location getLocationById(long id) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        ResultSet result;
        String sql = "SELECT l.street_address AS street_address, l.city AS city, c.country_name AS country FROM locations AS l JOIN countries AS c ON (l.country_id = c.id) WHERE l.id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setLong(1, id);
            result = statement.executeQuery();
            return new Location(id, result.getString("street_address"), result.getString("city"), result.getString("country"));
        }
  }

    public Location editLocation (Location location) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "UPDATE locations SET street_address = ?, city = ?, country_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, location.getAddress());
            statement.setString(2, location.getCity());
            statement.setLong(3, getCountryId(location.getCountry()));
            statement.setLong(4, location.getId());
            statement.executeUpdate();
            return location;
        }
    }
}

