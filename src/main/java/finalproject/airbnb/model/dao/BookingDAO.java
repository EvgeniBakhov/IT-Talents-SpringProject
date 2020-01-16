package finalproject.airbnb.model.dao;

import finalproject.airbnb.model.pojo.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingDAO {
    private static final String ADD_BOOKING_SQL = "INSERT INTO bookings (stay_id, user_id, from_date, to_date, " +
            "accepted, valid) VALUES (?, ?, ?, ?, ?, ?);";
    private static final String GET_BOOKING_BY_STAY_SQL = "SELECT id, stay_id, user_id, from_date, to_date, " +
            "accepted, valid FROM bookings WHERE stay_id = ? ORDER BY from_date;";
    private static final String GET_BOOKINGS_BETWEEN_DATES_SQL = "SELECT id, stay_id, user_id, from_date, to_date," +
            " accepted, valid FROM bookings WHERE stay_id = ? AND accepted = 1 AND (from_date BETWEEN ? AND ? " +
            "OR to_date BETWEEN ? AND ?) OR to_date = ? OR to_date = ? OR from_date = ? OR from_date = ? OR " +
            "(from_date> ? AND to_date< ?);";
    private static final String GET_ALL_BOOKINGS_BY_USER_ID = "SELECT id, stay_id, user_id, from_date, to_date, " +
            "accepted, valid FROM bookings WHERE user_id = ? ORDER BY from_date";
    private static final String DELETE_BOOKING = "DELETE FROM bookings WHERE id = ?;";
    public static final String GET_BOOKING_BY_ID = "SELECT id, stay_id, user_id, from_date, to_date, accepted, " +
            "valid FROM bookings WHERE id = ?";
    private static final String GET_UNACCEPTED_BOOKINGS_SQL = "SELECT id, stay_id, user_id, from_date, to_date," +
            " accepted, valid FROM bookings WHERE stay_id = ? AND accepted = 0 ORDER BY from_date;";
    public static final String ACCEPT_BOOKING_SQL = "UPDATE bookings SET accepted = 1 WHERE id = ?";
    public static final String DELETE_BOOKING_SQL = "DELETE FROM bookings WHERE id = ?;";

    @Autowired
    JdbcTemplate jdbcTemplate;
    public Booking addBooking(Booking booking) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(ADD_BOOKING_SQL, Statement.RETURN_GENERATED_KEYS)){
            statement.setLong(1, booking.getStayId());
            statement.setLong(2, booking.getUserId());
            statement.setDate(3, Date.valueOf(booking.getFromDate()));
            statement.setDate(4, Date.valueOf(booking.getToDate()));
            statement.setBoolean(5, booking.isAccepted());
            statement.setBoolean(6, booking.isValid());
            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            booking.setId(result.getLong(1));
            return booking;
        }
    }

    public List<Booking> getBookingByStayId(long stayId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_BOOKING_BY_STAY_SQL)) {
            statement.setLong(1, stayId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                bookings.add(new Booking(
                        result.getLong("id"),
                        result.getLong("stay_id"),
                        result.getLong("user_id"),
                        result.getDate("from_date").toLocalDate(),
                        result.getDate("to_date").toLocalDate(),
                        result.getBoolean("accepted"),
                        result.getBoolean("valid")
                ));
            }
            return bookings;
        }
    }

    public boolean getBookingsBetweenDates(long stayId, LocalDate fromDate, LocalDate toDate) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_BOOKINGS_BETWEEN_DATES_SQL)){
            statement.setLong(1, stayId);
            statement.setDate(2, Date.valueOf(fromDate));
            statement.setDate(3, Date.valueOf(toDate));
            statement.setDate(4, Date.valueOf(fromDate));
            statement.setDate(5, Date.valueOf(toDate));
            statement.setDate(6, Date.valueOf(fromDate));
            statement.setDate(7, Date.valueOf(toDate));
            statement.setDate(8, Date.valueOf(fromDate));
            statement.setDate(9, Date.valueOf(toDate));
            statement.setDate(10, Date.valueOf(fromDate));
            statement.setDate(11, Date.valueOf(toDate));
            ResultSet result = statement.executeQuery();
            return result.next();
        }
    }

    public List<Booking> getAllBookingsByUserId(long id) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_ALL_BOOKINGS_BY_USER_ID)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                bookings.add(new Booking(
                        result.getLong("id"),
                        result.getLong("stay_id"),
                        result.getLong("user_id"),
                        result.getDate("from_date").toLocalDate(),
                        result.getDate("to_date").toLocalDate(),
                        result.getBoolean("accepted"),
                        result.getBoolean("valid")
                ));
            }
            return bookings;
        }
    }

    public Booking getBookingById(long id) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BOOKING_BY_ID)){
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if(result.next()){
                return new Booking(
                        result.getLong("id"),
                        result.getLong("stay_id"),
                        result.getLong("user_id"),
                        result.getDate("from_date").toLocalDate(),
                        result.getDate("to_date").toLocalDate(),
                        result.getBoolean("accepted"),
                        result.getBoolean("valid")
                );
            }
            return null;
        }
    }

    public String deleteBooking(long id) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_BOOKING)){
            statement.setLong(1, id);
            statement.executeUpdate();
            return "Booking deleted.";
        }
    }

    public List<Booking> getUnacceptedBookingByStayId(long id) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_UNACCEPTED_BOOKINGS_SQL)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                bookings.add(new Booking(
                        result.getLong("id"),
                        result.getLong("stay_id"),
                        result.getLong("user_id"),
                        result.getDate("from_date").toLocalDate(),
                        result.getDate("to_date").toLocalDate(),
                        result.getBoolean("accepted"),
                        result.getBoolean("valid")
                ));
            }
        }
        return bookings;
    }

    public String acceptBooking(Booking booking) throws SQLException {
        try(Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(ACCEPT_BOOKING_SQL)){
            statement.setLong(1, booking.getId());
            statement.executeUpdate();
        }
        return "Booking accepted.";
    }
}
