package finalproject.airbnb.model.dao;

import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.StayFilterDTO;
import finalproject.airbnb.model.pojo.Stay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class StayDAO {
    public static final String UPDATE_STAY_RATING_SQL = "UPDATE stays SET rating = ? WHERE id = ?;";
    public static final String GET_HOST_ID_SQL = "SELECT host_id FROM stays WHERE id = ?;";
    public static final String GET_PROPERTY_TYPE_BY_ID = "SELECT property_type_name FROM property_types WHERE id = ?";
    public static final String GET_STAY_TYPE_BY_ID = "SELECT type_name FROM place_types WHERE id = ?";
    private static final String ADD_STAY_SQL = "INSERT INTO stays (host_id, location_id, price, stay_description, title, type_id, " +
            "instant_book, property_type_id, rules, num_of_beds, num_of_bedrooms, num_of_bathrooms)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_STAY_SQL = "DELETE FROM stays WHERE id = ?;";
    private static final String GET_STAY_BY_ID_SQL = "SELECT id, price, rating, stay_description, title, type_id, property_type_id, host_id, location_id, instant_book, rules, num_of_beds, num_of_bedrooms, num_of_bathrooms FROM stays WHERE id = ?";
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
    @Autowired
    UserDAO userDAO;

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
            connection.close();
        }
    }

    public String deleteStay(long id) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_STAY_SQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
        return "Stay deleted!";
    }

    public Stay getStayById(long id) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_STAY_BY_ID_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return new Stay(
                        result.getLong("id"),
                        result.getDouble("price"),
                        result.getDouble("rating"),
                        result.getString("stay_description"),
                        result.getString("title"),
                        result.getBoolean("instant_book"),
                        userDAO.getUserById(result.getLong("host_id")),
                        locDAO.getLocationById(result.getLong("location_id")),
                        result.getString("rules"),
                        result.getInt("num_of_beds"),
                        result.getInt("num_of_bedrooms"),
                        result.getInt("num_of_bathrooms"),
                        getStayTypeById(result.getLong("type_id")),
                        getPropertyTypeById(result.getLong("property_type_id"))
                );
            }
            else {
                return null;
            }
        }
    }
    public List<GetStayDTO> getStaysByUserId(long id) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_STAYS_BY_USER_ID)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            List<GetStayDTO> stays = new ArrayList<>();
            while (result.next()) {
                GetStayDTO getStayDTO = new GetStayDTO(result);
                stays.add(getStayDTO);
            }
            return stays;
        }
    }

    public long getHostId(long id) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_HOST_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getLong(1);
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
    public Stay.stayType getStayTypeById(long id) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_STAY_TYPE_BY_ID)){
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            result.next();
            return Stay.stayType.valueOf(result.getString("type_name"));
        }
    }

    public Stay.propertyType getPropertyTypeById(long id) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_PROPERTY_TYPE_BY_ID)){
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            result.next();
            return Stay.propertyType.valueOf(result.getString("property_type_name"));
        }
    }


    public void updateRating(long id, double updatedRating) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_STAY_RATING_SQL)) {
            statement.setDouble(1, updatedRating);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

    public List<GetStayDTO> filterStays(StayFilterDTO stayFilterDTO) throws SQLException {
        String filterStaysSQL = "SELECT u.first_name, u.last_name, u.profile_picture, l.street_address, l.city, c.country_name, " +
                " s.price, s.rating, s.stay_description, s.title," +
                " p.type_name, s.instant_book, pr.property_type_name," +
                " s.rules, s.num_of_beds, s.num_of_bedrooms, s.num_of_bathrooms " +
                " FROM stays AS s" +
                " JOIN users AS u ON (s.host_id = u.id)" +
                " JOIN locations AS l ON (s.location_id = l.id)" +
                " JOIN countries AS c ON (l.country_id = c.id)" +
                " JOIN place_types AS p ON (s.type_id = p.id)" +
                " JOIN property_types AS pr ON (s.property_type_id = pr.id)" +
                " WHERE " + stayFilterSQL(stayFilterDTO) + " ;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(filterStaysSQL)) {
            ResultSet result = statement.executeQuery();
            List<GetStayDTO> stays = new ArrayList<>();
            while (result.next()) {
                GetStayDTO getStayDTO = new GetStayDTO(result);
                stays.add(getStayDTO);
            }
            return stays;
        }
    }

    private String stayFilterSQL(StayFilterDTO stayFilterDTO) {
        StringBuilder sql = new StringBuilder(" ");
        double minPrice = stayFilterDTO.getMinPrice();
        double maxPrice = stayFilterDTO.getMaxPrice();
        int numOfBeds = stayFilterDTO.getNumOfBeds();
        int numOfBedrooms = stayFilterDTO.getNumOfBedrooms();
        int numOfBathrooms = stayFilterDTO.getNumOfBathrooms();
        String stayType = stayFilterDTO.getStayType();
        String propertyType = stayFilterDTO.getPropertyType();
        String order = stayFilterDTO.getOrder();
        if(minPrice != 0 && maxPrice != 0) {
            sql.append(" s.price BETWEEN " + minPrice + " AND " + maxPrice);
        }
        if(numOfBathrooms != 0) {
            sql.append(" AND s.num_of_bathrooms = " + numOfBathrooms);
        }
        if(numOfBedrooms != 0) {
            sql.append(" AND s.num_of_bedrooms = " + numOfBedrooms);
        }
        if(numOfBeds != 0) {
            sql.append(" AND s.num_of_beds = " + numOfBeds);
        }
        if(stayType != null) {
            sql.append(" AND s.type_id = " + Stay.stayType.valueOf(stayType).getTypeId());
        }
        if(propertyType != null) {
            sql.append(" AND s.property_type_id = " + Stay.propertyType.valueOf(propertyType).getPropertyTypeId());
        }
        if(order != null) {
            if(order.equalsIgnoreCase("ascending")) {
                sql.append(" ORDER BY price ASC");
            }
            if(order.equalsIgnoreCase("descending")) {
                sql.append(" ORDER BY price DESC");
            }
        }
        return sql.toString();
    }
}
