package finalproject.airbnb.model.dao;

import finalproject.airbnb.model.pojo.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sun.reflect.generics.tree.Tree;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Component
public class BookingDAO {
    private static final String ADD_BOOKING_SQL = "INSERT INTO bookings (stay_id, user_id, from_date, to_date, accepted, valid) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String GET_BOOKING_BY_STAY_SQL = "SELECT stay_id, user_id, from_date, to_date, accepted, valid " +
            "FROM bookings WHERE stay_id = ? ORDER BY from_date";
    private static final String GET_BOOKINGS_BETWEEN_DATES_SQL = "SELECT stay_id, user_id, from_date, to_date, accepted, valid FROM bookings " +
            "WHERE stay_id = ? AND accepted = 1 AND from_date BETWEEN ? AND ? OR to_date BETWEEN ? AND ?";
    private static final String GET_ALL_BOOKINGS_BY_USER_ID = "SELECT stay_id, user_id, from_date, to_date, accepted, valid FROM bookings WHERE user_id = ? ORDER BY from_date";
    private static final String DELETE_BOOKING = "DELETE FROM bookings WHERE id = ?";
    @Autowired
    JdbcTemplate jdbcTemplate;
    public Booking addBooking(Booking booking) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try(PreparedStatement statement = connection.prepareStatement(ADD_BOOKING_SQL)){
            statement.setLong(1, booking.getStayId());
            statement.setLong(2, booking.getUserId());
            statement.setDate(3, Date.valueOf(booking.getFromDate()));
            statement.setDate(4, Date.valueOf(booking.getToDate()));
            statement.setBoolean(5, booking.isAccepted());
            statement.setBoolean(6, booking.isValid());
            statement.executeUpdate();
        }
        return booking;
    }

    public List<Booking> getBookingByStayId(long stayId) throws SQLException {
        ResultSet result;
        List<Booking> bookings = new ArrayList<>();
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try(PreparedStatement statement = connection.prepareStatement(GET_BOOKING_BY_STAY_SQL)) {
            statement.setLong(1, stayId);
            result = statement.executeQuery();
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

    public boolean getBookingsBetweenDates(long stayId, LocalDate fromDate, LocalDate toDate) throws SQLException {
        ResultSet result;
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try(PreparedStatement statement = connection.prepareStatement(GET_BOOKINGS_BETWEEN_DATES_SQL)){
            statement.setLong(1, stayId);
            statement.setDate(2, Date.valueOf(fromDate));
            statement.setDate(3, Date.valueOf(toDate));
            statement.setDate(4, Date.valueOf(fromDate));
            statement.setDate(5, Date.valueOf(toDate));
            result = statement.executeQuery();
        }
        return result.next();
    }

    public List<Booking> getAllBookingsByUserId(long id) throws SQLException {
        ResultSet result;
        List<Booking> bookings = new ArrayList<>();
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try(PreparedStatement statement = connection.prepareStatement(GET_ALL_BOOKINGS_BY_USER_ID)) {
            statement.setLong(1, id);
            result = statement.executeQuery();
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

    public Booking getBookingById(long id) throws SQLException {
        ResultSet result;
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try(PreparedStatement statement = connection.prepareStatement("SELECT id, stay_id, user_id, from_date, to_date, accepted, valid WHERE id = ?")){
            result = statement.executeQuery();
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
        }
        return null;
    }

    public String deleteBooking(long id) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try(PreparedStatement statement = connection.prepareStatement(DELETE_BOOKING)){
            statement.setLong(1, id);
            statement.executeUpdate();
        }
        return "Booking deleted.";
    }
}
