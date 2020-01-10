package finalproject.airbnb.model.dao;


import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.StayDTO;
import finalproject.airbnb.model.pojo.Location;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class StayDAO {

    private static final String ADD_STAY_SQL = "INSERT INTO stays (host_id, location_id, price, stay_description, title, type_id, " +
            "instant_book, property_type_id, rules, num_of_beds, num_of_bedrooms, num_of_bathrooms)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_STAY_SQL = "DELETE FROM stays WHERE id = ?;";
    private static final String GET_STAY_BY_ID_SQL = "SELECT u.first_name, u.last_name, u.profile_picture," +
            " l.street_address, l.city, c.country_name," +
            " s.price, s.rating, s.stay_description, s.title," +
            " p.type_name, s.instant_book, pr.property_type_name," +
            " s.rules, s.num_of_beds, s.num_of_bedrooms, s.num_of_bathrooms " +
            " FROM stays AS s" +
            " JOIN users AS u ON (s.host_id = u.id)" +
            " JOIN locations AS l ON (s.location_id = l.id)" +
            " JOIN countries AS c ON (l.country_id = c.id)" +
            " JOIN place_types AS p ON(s.type_id = p.id) " +
            " JOIN property_types AS pr ON(s.property_type_id = pr.id)" +
            " WHERE s.id = ?";
    private static final String GET_STAYS_BY_USER_ID = "SELECT u.first_name, u.last_name, u.profile_picture," +
            " l.street_address, l.city, c.country_name," +
            " s.price, s.rating, s.stay_description, s.title," +
            " p.type_name, s.instant_book, pr.property_type_name," +
            " s.rules, s.num_of_beds, s.num_of_bedrooms, s.num_of_bathrooms " +
            " FROM stays AS s" +
            " JOIN users AS u ON (s.host_id = u.id)" +
            " JOIN locations AS l ON (s.location_id = l.id)" +
            " JOIN countries AS c ON (l.country_id = c.id)" +
            " JOIN place_types AS p ON(s.type_id = p.id) " +
            " JOIN property_types AS pr ON(s.property_type_id = pr.id)" +
            " WHERE s.host_id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LocDAO locDAO;

    public Stay addStay(Stay stay) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try(PreparedStatement statement = connection.prepareStatement(ADD_STAY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            long locID = locDAO.addLocation(stay.getLocation());
            statement.setLong(1, stay.getHost().getId());
            statement.setLong(2, locID);
            statement.setDouble(3, stay.getPrice());
            statement.setString(4, stay.getDescription());
            statement.setString(5, stay.getTitle());
            statement.setLong(6, stay.getStayType().getTypeId());
            statement.setBoolean(7, stay.isInstantBook());
            statement.setLong(8, stay.getPropertyType().getPropertyTypeId());
            statement.setString(9, stay.getRules());
            statement.setInt(10, stay.getNumOfBeds());
            statement.setInt(11, stay.getNumOfBedrooms());
            statement.setInt(12, stay.getNumOfBathrooms());
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            stay.setId(result.getLong(1));
            connection.commit();
            return stay;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        finally {
            connection.setAutoCommit(true);
        }
    }

    public String deleteStay(long id) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_STAY_SQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
        return "Stay deleted!";
    }

    public GetStayDTO getStayById(long id) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try(PreparedStatement statement = connection.prepareStatement(GET_STAY_BY_ID_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return new GetStayDTO(result.getString("u.first_name"),
                        result.getString("u.last_name"),
                        result.getString("u.profile_picture"),
                        new Location(result.getString("l.street_address"),
                                result.getString("l.city"),
                                result.getString("c.country_name")),
                        result.getDouble("s.price"),
                        result.getDouble("s.rating"),
                        result.getString("s.stay_description"),
                        result.getString("s.title"),
                        result.getString("p.type_name"),
                        result.getBoolean("s.instant_book"),
                        result.getString("pr.property_type_name"),
                        result.getString("s.rules"),
                        result.getInt("s.num_of_beds"),
                        result.getInt("s.num_of_bedrooms"),
                        result.getInt("s.num_of_bathrooms"));
            }
            else{
                return null;
            }
        }
    }

    /*public StayDTO editStay(StayDTO stayDTO) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        Location location =
        locDAO.editLocation(location);
        String sql = "UPDATE stays SET price = ?, stay_description = ?, " +
                "title = ?, birthday = ?, phone_number = ?, user_description = ?, profile_picture = ?, password = ? WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
    }*/

    public List<GetStayDTO> getStaysByUserId(long id) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(GET_STAYS_BY_USER_ID)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            List<GetStayDTO> stays = new ArrayList<>();
            while (result.next()) {
                GetStayDTO getStayDTO = new GetStayDTO(result.getString("u.first_name"),
                        result.getString("u.last_name"),
                        result.getString("u.profile_picture"),
                        new Location(result.getString("l.street_address"),
                                result.getString("l.city"),
                                result.getString("c.country_name")),
                        result.getDouble("s.price"),
                        result.getDouble("s.rating"),
                        result.getString("s.stay_description"),
                        result.getString("s.title"),
                        result.getString("p.type_name"),
                        result.getBoolean("s.instant_book"),
                        result.getString("pr.property_type_name"),
                        result.getString("s.rules"),
                        result.getInt("s.num_of_beds"),
                        result.getInt("s.num_of_bedrooms"),
                        result.getInt("s.num_of_bathrooms"));
                stays.add(getStayDTO);
            }
            return stays;
        }
    }

    public long getHostId(long id) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String sql = "SELECT host_id FROM stays WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getLong(1);
        }
    }

}
