package service;

import dao.TripDAO;
import model.Trip;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Handles business logic for Trip management.
 */
public class TripService {

    private final TripDAO tripDAO;

    public TripService() {
        this.tripDAO = new TripDAO();
    }

    /**
     * Offers a new trip after validating rules.
     */
    public int offerTrip(Trip trip) throws Exception {

        // 1. Basic Validations
        if (trip.getOrigin() == null || trip.getOrigin().trim().isEmpty() ||
                trip.getDestination() == null || trip.getDestination().trim().isEmpty()) {
            throw new IllegalArgumentException("Origin and Destination are required.");
        }

        if (trip.getOrigin().equalsIgnoreCase(trip.getDestination())) {
            throw new IllegalArgumentException("Origin and Destination cannot be the same.");
        }

        // 2. Date/Time Validation
        if (trip.getDate() == null || trip.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Trip date must be in the future.");
        }

        // 3. Price and Seats
        if (trip.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }

        if (trip.getSeatsAvailable() <= 0) {
            throw new IllegalArgumentException("Must provide at least one seat.");
        }

        try {
            return tripDAO.createTrip(trip);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error while creating trip: " + e.getMessage());
        }
    }

    /**
     * Search for trips based on criteria.
     */
    public List<Trip> searchTrips(String origin, String destination, LocalDate date) throws Exception {
        try {
            return tripDAO.searchTrips(origin, destination, date);
        } catch (SQLException e) {
            throw new Exception("Failed to search trips: " + e.getMessage());
        }
    }

    /**
     * Get all available future trips.
     */
    public List<Trip> findAllAvailableTrips() throws Exception {
        try {
            return tripDAO.findAllAvailableTrips();
        } catch (SQLException e) {
            throw new Exception("Failed to retrieve trips: " + e.getMessage());
        }
    }

    /**
     * Get trip by specific ID.
     */
    public Trip getTripById(int tripId) throws Exception {
        try {
            return tripDAO.getTripById(tripId);
        } catch (SQLException e) {
            throw new Exception("Failed to get trip details: " + e.getMessage());
        }
    }

    public List<Trip> getTripsByDriverId(int driverId) throws Exception {
        try {
            return tripDAO.getTripsByDriverId(driverId);
        } catch (SQLException e) {
            throw new Exception("Failed to retrieve driver trips: " + e.getMessage());
        }
    }

    public boolean deleteTrip(int tripId) throws Exception {
        try {
            return tripDAO.deleteTrip(tripId);
        } catch (SQLException e) {
            throw new Exception("Failed to delete trip: " + e.getMessage());
        }
    }

    public boolean updateTripStatus(int tripId, String status) throws Exception {
        try {
            return tripDAO.updateTripStatus(tripId, status);
        } catch (SQLException e) {
            throw new Exception("Failed to update trip status: " + e.getMessage());
        }
    }

    public boolean updateTrip(Trip trip) throws Exception {
        // Validate basics
        if (trip.getOrigin() == null || trip.getDestination() == null || trip.getPrice() <= 0) {
            throw new IllegalArgumentException("Invalid trip data for update.");
        }
        try {
            return tripDAO.updateTrip(trip);
        } catch (SQLException e) {
            throw new Exception("Failed to update trip: " + e.getMessage());
        }
    }
}
