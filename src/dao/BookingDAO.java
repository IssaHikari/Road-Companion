package dao;

import model.Booking;
import model.Trip;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean createBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO Bookings (trip_id, passenger_id, seats_booked, payment_status, booking_status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getTripId());
            stmt.setInt(2, booking.getPassengerId());
            stmt.setInt(3, booking.getSeatsBooked());
            stmt.setString(4, booking.getPaymentStatus());
            stmt.setString(5, booking.getBookingStatus() != null ? booking.getBookingStatus() : "PENDING");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next())
                        booking.setBookingId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public List<Booking> getBookingsByPassengerId(int passengerId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        // Join to get Trip details
        String sql = "SELECT b.*, t.*, u.name as driver_name, u.phone_number as driver_phone " +
                "FROM Bookings b " +
                "JOIN Trips_Offered t ON b.trip_id = t.trip_id " +
                "JOIN Users u ON t.driver_id = u.user_id " +
                "WHERE b.passenger_id = ? ORDER BY b.booking_date DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, passengerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setTripId(rs.getInt("trip_id"));
                    b.setPassengerId(rs.getInt("passenger_id"));
                    b.setSeatsBooked(rs.getInt("seats_booked"));
                    b.setPaymentStatus(rs.getString("payment_status"));
                    b.setBookingStatus(rs.getString("booking_status"));
                    b.setBookingDate(rs.getTimestamp("booking_date"));

                    // Populate Trip details embedded
                    Trip trip = new Trip();
                    trip.setTripId(rs.getInt("trip_id"));
                    trip.setOrigin(rs.getString("origin"));
                    trip.setDestination(rs.getString("destination"));
                    trip.setDate(rs.getDate("trip_date").toLocalDate());
                    trip.setTime(rs.getTime("trip_time").toLocalTime());
                    trip.setPrice(rs.getDouble("price"));
                    trip.setStatus(rs.getString("status"));
                    trip.setDriverId(rs.getInt("driver_id")); // Essential for rating

                    User driver = new User();
                    driver.setName(rs.getString("driver_name"));
                    driver.setPhoneNumber(rs.getString("driver_phone"));

                    b.setTripDetails(trip);
                    b.setDriverDetails(driver);

                    bookings.add(b);
                }
            }
        }
        return bookings;
    }

    public boolean updateBookingStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE Bookings SET booking_status = ? WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePaymentStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE Bookings SET payment_status = ? WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Booking> getBookingsByDriverId(int driverId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, t.*, " +
                "u.user_id as p_id, u.name as p_name, u.phone_number as p_phone, " +
                "u.email as p_email, u.gender as p_gender, u.role as p_role, " +
                "u.avatar_path as p_avatar, u.background_path as p_bg, " +
                "u.date_of_birth as p_dob " +
                "FROM Bookings b " +
                "JOIN Trips_Offered t ON b.trip_id = t.trip_id " +
                "JOIN Users u ON b.passenger_id = u.user_id " +
                "WHERE t.driver_id = ? " +
                "ORDER BY b.booking_date DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setTripId(rs.getInt("trip_id"));
                    b.setPassengerId(rs.getInt("passenger_id"));
                    b.setSeatsBooked(rs.getInt("seats_booked"));
                    b.setPaymentStatus(rs.getString("payment_status"));
                    b.setBookingStatus(rs.getString("booking_status"));
                    b.setBookingDate(rs.getTimestamp("booking_date"));

                    Trip trip = new Trip();
                    trip.setTripId(rs.getInt("trip_id"));
                    trip.setOrigin(rs.getString("origin"));
                    trip.setDestination(rs.getString("destination"));
                    trip.setDate(rs.getDate("trip_date").toLocalDate());
                    trip.setTime(rs.getTime("trip_time").toLocalTime());
                    trip.setStatus(rs.getString("status"));
                    trip.setPrice(rs.getDouble("price"));
                    trip.setSeatsAvailable(rs.getInt("seats_available"));
                    b.setTripDetails(trip);

                    User passenger = new User();
                    passenger.setUserId(rs.getInt("p_id"));
                    passenger.setName(rs.getString("p_name"));
                    passenger.setPhoneNumber(rs.getString("p_phone"));
                    passenger.setEmail(rs.getString("p_email"));
                    passenger.setGender(rs.getString("p_gender"));
                    passenger.setRole(rs.getString("p_role"));
                    passenger.setAvatarPath(rs.getString("p_avatar"));
                    passenger.setBackgroundPath(rs.getString("p_bg"));
                    passenger.setAverageRating(5.0); // Default for passenger
                    if (rs.getDate("p_dob") != null) {
                        passenger.setDateOfBirth(rs.getDate("p_dob").toLocalDate());
                    }
                    b.setPassengerDetails(passenger);

                    bookings.add(b);
                }
            }
        }
        return bookings;
    }

    public List<Integer> getPassengerIdsForTrip(int tripId) throws SQLException {
        List<Integer> passengerIds = new ArrayList<>();
        String sql = "SELECT passenger_id FROM Bookings WHERE trip_id = ? AND booking_status IN ('PENDING', 'ACCEPTED')";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tripId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    passengerIds.add(rs.getInt("passenger_id"));
                }
            }
        }
        return passengerIds;
    }
}
