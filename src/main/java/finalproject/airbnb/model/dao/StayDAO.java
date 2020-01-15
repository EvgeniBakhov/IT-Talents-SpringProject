package finalproject.airbnb.model.dao;

import finalproject.airbnb.model.dto.GetStayDTO;
import finalproject.airbnb.model.dto.StayDTO;
import finalproject.airbnb.model.dto.StayFilterDTO;
import finalproject.airbnb.model.pojo.Location;
import finalproject.airbnb.model.pojo.Picture;
import finalproject.airbnb.model.pojo.Stay;
import finalproject.airbnb.utilities.StayValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class StayDAO {
    public static final String UPDATE_STAY_RATING_SQL = "UPDATE stays SET rating = ? WHERE id = ?;";
    public static final String GET_HOST_ID_SQL = "SELECT host_id FROM stays WHERE id = ?;";
    public static final String GET_PROPERTY_TYPE_BY_ID = "SELECT property_type_name FROM property_types WHERE id = ?";
    public static final String GET_STAY_TYPE_BY_ID = "SELECT type_name FROM stay_types WHERE id = ?";
    private static final String ADD_PICTURE_SQL = "INSERT INTO pictures (stay_id, picture_url) VALUES(?, ?);";
    private static final String GET_STAY_PICTURES_URL = "SELECT picture_url FROM pictures WHERE stay_id = ?";
    private static final String ADD_STAY_SQL = "INSERT INTO stays (host_id, location_id, price, stay_description, title, stay_type_id, " +
            "instant_book, property_type_id, rules, num_of_beds, num_of_bedrooms, num_of_bathrooms)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_STAY_SQL = "DELETE FROM stays WHERE id = ?;";
    private static final String GET_STAY_BY_ID_SQL = "SELECT s.id, u.first_name, u.last_name, u.profile_picture," +
            " l.street_address, l.city, c.country_name," +
            " s.price, s.rating, s.stay_description, s.title," +
            " st.type_name, s.instant_book, pr.property_type_name," +
            " s.rules, s.num_of_beds, s.num_of_bedrooms, s.num_of_bathrooms " +
            " FROM stays AS s" +
            " JOIN users AS u ON (s.host_id = u.id)" +
            " JOIN locations AS l ON (s.location_id = l.id)" +
            " JOIN countries AS c ON (l.country_id = c.id)" +
            " JOIN stay_types AS st ON(s.stay_type_id = st.id) " +
            " JOIN property_types AS pr ON(s.property_type_id = pr.id)" +
            " WHERE s.id = ?";
    private static final String GET_STAYS_BY_USER_ID = "SELECT s.id, u.first_name, u.last_name, u.profile_picture," +
            " l.street_address, l.city, c.country_name," +
            " s.price, s.rating, s.stay_description, s.title," +
            " st.type_name, s.instant_book, pr.property_type_name," +
            " s.rules, s.num_of_beds, s.num_of_bedrooms, s.num_of_bathrooms " +
            " FROM stays AS s" +
            " JOIN users AS u ON (s.host_id = u.id)" +
            " JOIN locations AS l ON (s.location_id = l.id)" +
            " JOIN countries AS c ON (l.country_id = c.id)" +
            " JOIN stay_types AS st ON(s.stay_type_id = st.id) " +
            " JOIN property_types AS pr ON(s.property_type_id = pr.id)" +
            " WHERE s.host_id = ?";
    private static final String DELETE_PICTURE_SQL = "DELETE FROM pictures WHERE id = ?";
    private static final String GET_PICTURE_BY_ID = "SELECT id, stay_id, picture_url FROM pictures WHERE id = ?";


    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LocDAO locDAO;
    @Autowired
    UserDAO userDAO;

    public long addStay(Stay stay) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try(PreparedStatement statement = connection.prepareStatement(ADD_STAY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            long locID = locDAO.addLocation(stay.getLocation());
            statement.setLong(1, stay.getHost().getId());
            statement.setLong(2, locID);
            statement.setDouble(3, stay.getPrice());
            statement.setString(4, stay.getDescription());
            statement.setString(5, stay.getTitle());
            statement.setLong(6, stay.getStayTypeId());
            statement.setBoolean(7, stay.isInstantBook());
            statement.setLong(8, stay.getPropertyTypeId());
            statement.setString(9, stay.getRules());
            statement.setInt(10, stay.getNumOfBeds());
            statement.setInt(11, stay.getNumOfBedrooms());
            statement.setInt(12, stay.getNumOfBathrooms());
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            stay.setId(result.getLong(1));
            connection.commit();
            return stay.getId();
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

    public GetStayDTO getStayById(long id) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_STAY_BY_ID_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return new GetStayDTO(
                        id,
                        result.getString("u.first_name"),
                        result.getString("u.last_name"),
                        result.getString("u.profile_picture"),
                        new Location(result.getString("l.street_address"),
                                result.getString("l.city"),
                                result.getString("c.country_name")),
                        result.getDouble("s.price"),
                        result.getDouble("s.rating"),
                        result.getString("s.stay_description"),
                        result.getString("s.title"),
                        result.getString("st.type_name"),
                        result.getBoolean("s.instant_book"),
                        result.getString("pr.property_type_name"),
                        result.getString("s.rules"),
                        result.getInt("s.num_of_beds"),
                        result.getInt("s.num_of_bedrooms"),
                        result.getInt("s.num_of_bathrooms")
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
                GetStayDTO getStayDTO = new GetStayDTO(result.getLong("s.id"),
                        result.getString("u.first_name"),
                        result.getString("u.last_name"),
                        result.getString("u.profile_picture"),
                        new Location(result.getString("l.street_address"),
                                result.getString("l.city"),
                                result.getString("c.country_name")),
                        result.getDouble("s.price"),
                        result.getDouble("s.rating"),
                        result.getString("s.stay_description"),
                        result.getString("s.title"),
                        result.getString("st.type_name"),
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

    public long getHostId(long stayId) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_HOST_ID_SQL)) {
            statement.setLong(1, stayId);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getLong(1);
        }
    }

    public StayDTO editStay(long id, StayDTO stayDTO) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try( PreparedStatement statement = connection.prepareStatement("UPDATE stays SET price = ?, stay_description = ?, title = ?, instant_book = ?, rules = ?, num_of_beds = ?, num_of_bedrooms = ?, num_of_bathrooms = ?, type_id = ?, property_type_id = ? WHERE id = ?;" )) {
            Location location = new Location(stayDTO.getStreetAddress(), stayDTO.getCity(), stayDTO.getCountry());
            location.setId(getStayById(id).getLocation().getId());
            connection.setAutoCommit(false);
            locDAO.editLocation(location);
            statement.setDouble(1, stayDTO.getPrice());
            statement.setString(2, stayDTO.getDescription());
            statement.setString(3, stayDTO.getTitle());
            statement.setBoolean(4, stayDTO.isInstantBook());
            statement.setString(5, stayDTO.getRules());
            statement.setInt(6, stayDTO.getNumOfBeds());
            statement.setInt(7, stayDTO.getNumOfBedrooms());
            statement.setInt(8, stayDTO.getNumOfBathrooms());
            statement.setLong(9, stayDTO.getStayTypeId());
            statement.setLong(10, stayDTO.getPropertyTypeId());
            statement.setLong(11, id);
            statement.executeUpdate();
            connection.commit();
            return stayDTO;
        }
        catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }
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
        List<Object> params = new ArrayList<>();
        String filterStaysSQL = "SELECT u.first_name, u.last_name, u.profile_picture, l.street_address, l.city, c.country_name, " +
                " s.price, s.rating, s.stay_description, s.title," +
                " st.type_name, s.instant_book, pr.property_type_name," +
                " s.rules, s.num_of_beds, s.num_of_bedrooms, s.num_of_bathrooms " +
                " FROM stays AS s" +
                " JOIN users AS u ON (s.host_id = u.id)" +
                " JOIN locations AS l ON (s.location_id = l.id)" +
                " JOIN countries AS c ON (l.country_id = c.id)" +
                " JOIN stay_types AS st ON (s.stay_type_id = st.id)" +
                " JOIN property_types AS pr ON (s.property_type_id = pr.id)" +
                " WHERE " + stayFilterSQL(stayFilterDTO, params) + " ;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(filterStaysSQL)) {
            for (int i = 0; i < params.size(); i++) {
                if(params.get(i) instanceof Integer){
                    statement.setInt(i+1, (Integer) params.get(i));
                }
                if (params.get(i) instanceof Double) {
                    statement.setDouble(i+1, (Double) params.get(i));
                }
                if (params.get(i) instanceof String) {
                    statement.setString(i+1, (String) params.get(i));
                }
                if (params.get(i) instanceof Long) {
                    statement.setLong(i+1, (Long) params.get(i));
                }
            }
            ResultSet result = statement.executeQuery();
            List<GetStayDTO> stays = new ArrayList<>();
            while (result.next()) {
                GetStayDTO getStayDTO = new GetStayDTO(result.getLong("s.id"),
                        result.getString("u.first_name"),
                        result.getString("u.last_name"),
                        result.getString("u.profile_picture"),
                        new Location(result.getString("l.street_address"),
                                result.getString("l.city"),
                                result.getString("c.country_name")),
                        result.getDouble("s.price"),
                        result.getDouble("s.rating"),
                        result.getString("s.stay_description"),
                        result.getString("s.title"),
                        result.getString("st.type_name"),
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

    private String stayFilterSQL(StayFilterDTO stayFilterDTO, List params) {
        StringBuilder sql = new StringBuilder(" ");
        double minPrice = stayFilterDTO.getMinPrice();
        double maxPrice = stayFilterDTO.getMaxPrice();
        int numOfBeds = stayFilterDTO.getNumOfBeds();
        int numOfBedrooms = stayFilterDTO.getNumOfBedrooms();
        int numOfBathrooms = stayFilterDTO.getNumOfBathrooms();
        long stayTypeId = stayFilterDTO.getStayTypeId();
        long propertyTypeId = stayFilterDTO.getPropertyTypeId();
        String order = stayFilterDTO.getOrder();
        String city = stayFilterDTO.getCity();
        String country = stayFilterDTO.getCountry();
        if(country != null){
            if(!country.isEmpty()) {
                sql.append(" c.country_name = ? AND ");
                params.add(country);
            }
        }
        if(city != null){
            if(!city.isEmpty()){
                sql.append(" l.city = ? AND ");
                params.add(city);
            }
        }
        if(numOfBathrooms != 0) {
            sql.append(" s.num_of_bathrooms = ? AND ");
            params.add(numOfBathrooms);
        }
        if(numOfBedrooms != 0) {
            sql.append(" s.num_of_bedrooms = ? AND ");
            params.add(numOfBedrooms);
        }
        if(numOfBeds != 0) {
            sql.append(" s.num_of_beds = ? AND ");
            params.add(numOfBeds);
        }
        if(stayTypeId != 0) {
            sql.append(" s.stay_type_id = ? AND ");
            params.add(stayTypeId);
        }
        if(propertyTypeId != 0) {
            sql.append(" s.property_type_id = ? AND ");
        }
        if(minPrice != 0 && maxPrice != 0) {
            sql.append(" (s.price BETWEEN ? AND ?) ");
            params.add(minPrice);
            params.add(maxPrice);
        } else {
            sql.append(" (s.price BETWEEN ? AND ?) ");
            params.add(0);
            params.add(StayValidator.MAX_STAY_PRICE);
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


    public String addImage(String path, long id) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(ADD_PICTURE_SQL)){
            statement.setLong(1, id);
            statement.setString(2, path);
            statement.executeUpdate();
        }
        return path;
    }
    public List<String> getStayImages(long stayId) throws SQLException {
        List<String> imagePaths = new ArrayList<>();
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_STAY_PICTURES_URL)){
            statement.setLong(1, stayId);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                imagePaths.add(result.getString("picture_url"));
            }
        }
        return imagePaths;
    }

    public Picture getPictureById(long picId) throws SQLException {
        ResultSet result;
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_PICTURE_BY_ID)){
            statement.setLong(1, picId);
            result = statement.executeQuery();
            result.next();
            return new Picture(result.getLong("id"),
                    result.getLong("stay_id"),
                    result.getString("picture_url"));
        }
    }

    public void deletePicture(long picId) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_PICTURE_SQL)){
            statement.setLong(1, picId);
            statement.executeUpdate();
        }
    }
}
