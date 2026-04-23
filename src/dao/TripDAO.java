package dao;

import model.Trip;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import model.Notification;

/**
 * Data Access Object for Trip operations.
 */
public class TripDAO {

    /**
     * Inserts a new trip into the database.
     */
    public int createTrip(Trip trip) throws SQLException {
        String sql = "INSERT INTO Trips_Offered (driver_id, origin, destination, trip_date, trip_time, price, seats_available, description, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int generatedId = -1;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, trip.getDriverId());
            stmt.setString(2, trip.getOrigin());
            stmt.setString(3, trip.getDestination());
            stmt.setDate(4, Date.valueOf(trip.getDate()));
            stmt.setTime(5, Time.valueOf(trip.getTime()));
            stmt.setDouble(6, trip.getPrice());
            stmt.setInt(7, trip.getSeatsAvailable());
            stmt.setString(8, trip.getDescription());
            stmt.setString(9, trip.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating trip failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                    trip.setTripId(generatedId);
                }
            }
        }
        return generatedId;
    }

    /**
     * Finds all trips with status 'AVAILABLE' and future date.
     */
    public List<Trip> findAllAvailableTrips() throws SQLException {
        List<Trip> trips = new ArrayList<>();
        // Using CURRENT_DATE() to ensure we only get future or today's trips
        String sql = "SELECT * FROM Trips_Offered WHERE status = 'AVAILABLE' AND trip_date >= CURRENT_DATE() ORDER BY trip_date, trip_time";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                trips.add(extractTripFromResultSet(rs));
            }
        }
        return trips;
    }

    /**
     * Search trips with filters.
     */
    public List<Trip> searchTrips(String origin, String destination, LocalDate date) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Trips_Offered WHERE status = 'AVAILABLE'");
        List<Object> params = new ArrayList<>();

        if (origin != null && !origin.trim().isEmpty()) {
            sql.append(" AND origin LIKE ?");
            params.add("%" + origin + "%");
        }
        if (destination != null && !destination.trim().isEmpty()) {
            sql.append(" AND destination LIKE ?");
            params.add("%" + destination + "%");
        }
        if (date != null) {
            sql.append(" AND trip_date = ?");
            params.add(Date.valueOf(date));
        } else {
            // If date is not specified, still show only future trips
            sql.append(" AND trip_date >= CURRENT_DATE()");
        }

        sql.append(" ORDER BY trip_date, trip_time");

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trips.add(extractTripFromResultSet(rs));
                }
            }
        }
        return trips;
    }

    /**
     * Get specific trip details by ID.
     */
    public Trip getTripById(int tripId) throws SQLException {
        String sql = "SELECT * FROM Trips_Offered WHERE trip_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tripId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTripFromResultSet(rs);
                }
            }
        }
        return null;
    }

    private Trip extractTripFromResultSet(ResultSet rs) throws SQLException {
        Trip trip = new Trip();
        trip.setTripId(rs.getInt("trip_id"));
        trip.setDriverId(rs.getInt("driver_id"));
        trip.setOrigin(rs.getString("origin"));
        trip.setDestination(rs.getString("destination"));
        trip.setDate(rs.getDate("trip_date").toLocalDate());
        trip.setTime(rs.getTime("trip_time").toLocalTime());
        trip.setPrice(rs.getDouble("price"));
        trip.setSeatsAvailable(rs.getInt("seats_available"));
        trip.setDescription(rs.getString("description"));
        trip.setStatus(rs.getString("status"));
        return trip;
    }

    public List<Trip> getTripsByDriverId(int driverId) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM Trips_Offered WHERE driver_id = ? ORDER BY trip_date DESC, trip_time DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trips.add(extractTripFromResultSet(rs));
                }
            }
        }
        return trips;
    }

    public boolean deleteTrip(int tripId) throws SQLException {
        // 1. Notify Passengers
        BookingDAO bookingDAO = new BookingDAO();
        NotificationDAO notificationDAO = new NotificationDAO();
        List<Integer> passengers = bookingDAO.getPassengerIdsForTrip(tripId);

        // Fetch trip details for the message
        Trip trip = getTripById(tripId);
        String tripInfo = (trip != null) ? trip.getOrigin() + " - " + trip.getDestination() : "votre voyage";

        for (int passengerId : passengers) {
            String msg = "Le voyage " + tripInfo + " prévu le " + trip.getDate() + " a été annulé par le conducteur.";
            notificationDAO.createNotification(new model.Notification(passengerId, msg));
        }

        // 2. Delete Trip
        String sql = "DELETE FROM Trips_Offered WHERE trip_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tripId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Simple update (status mainly, or details)
    public boolean updateTripStatus(int tripId, String newStatus) throws SQLException {
        String sql = "UPDATE Trips_Offered SET status = ? WHERE trip_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, tripId);
            return stmt.executeUpdate() > 0;
        }

    }

    public boolean updateSeatsAvailable(int tripId, int newSeats) throws SQLException {
        String sql = "UPDATE Trips_Offered SET seats_available = ? WHERE trip_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newSeats);
            stmt.setInt(2, tripId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateTrip(Trip trip) throws SQLException {
        String sql = "UPDATE Trips_Offered SET origin = ?, destination = ?, trip_date = ?, trip_time = ?, price = ?, seats_available = ?, description = ? WHERE trip_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trip.getOrigin());
            stmt.setString(2, trip.getDestination());
            stmt.setDate(3, java.sql.Date.valueOf(trip.getDate()));
            stmt.setTime(4, java.sql.Time.valueOf(trip.getTime()));
            stmt.setDouble(5, trip.getPrice());
            stmt.setInt(6, trip.getSeatsAvailable());
            stmt.setString(7, trip.getDescription());
            stmt.setInt(8, trip.getTripId());
            return stmt.executeUpdate() > 0;
        }
    }
}
