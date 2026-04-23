package service;

import dao.BookingDAO;
import dao.TripDAO;
import model.Booking;
import model.Trip;

public class BookingService {

    private final BookingDAO bookingDAO;
    private final TripDAO tripDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.tripDAO = new TripDAO();
    }

    public void bookTrip(int tripId, int passengerId, int seats) throws Exception {
        // 1. Check availability
        Trip trip = tripDAO.getTripById(tripId);
        if (trip == null)
            throw new Exception("Trip not found.");
        if (!"AVAILABLE".equals(trip.getStatus()))
            throw new Exception("Trip is not available.");
        if (trip.getSeatsAvailable() < seats)
            throw new Exception("Not enough seats available.");

        // 2. Create Booking (PENDING)
        // Note: We do NOT decrement seats yet. Seats are decremented only on ACCEPT.
        // However, to prevent overbooking, some systems reserve seats temporarily.
        // For this simple app, we check seats at request time AND at acceptance time.
        // The prompt says "passenger cannot book more than allocated" -> checked above.

        Booking booking = new Booking();
        booking.setTripId(tripId);
        booking.setPassengerId(passengerId);
        booking.setSeatsBooked(seats);
        booking.setPaymentStatus("PENDING");
        booking.setBookingStatus("PENDING");

        if (!bookingDAO.createBooking(booking)) {
            throw new Exception("Booking failed.");
        }
    }

    public java.util.List<Booking> getPassengerBookings(int passengerId) throws Exception {
        return bookingDAO.getBookingsByPassengerId(passengerId);
    }

    public java.util.List<Booking> getDriverBookingRequests(int driverId) throws Exception {
        return bookingDAO.getBookingsByDriverId(driverId);
    }

    public void updateBookingStatus(int bookingId, String status) throws Exception {
        bookingDAO.updateBookingStatus(bookingId, status);
    }

    public void updatePaymentStatus(int bookingId, String status) throws Exception {
        bookingDAO.updatePaymentStatus(bookingId, status);
    }

    public void rateDriver(model.Rating rating) throws Exception {
        dao.RatingDAO ratingDAO = new dao.RatingDAO();
        ratingDAO.createRating(rating);

        // Recalculate average
        double newAvg = ratingDAO.calculateAverageRating(rating.getRatedUserId());

        // Update User
        // We need UserDAO here.
        dao.UserDAO userDAO = new dao.UserDAO();
        userDAO.updateUserRating(rating.getRatedUserId(), newAvg, 0); // 0 because we are not updating trip count here
    }

    public void processBookingDecision(Booking booking, boolean accepted) throws Exception {
        if (accepted) {
            // Check seats again
            Trip trip = tripDAO.getTripById(booking.getTripId());
            if (trip.getSeatsAvailable() < booking.getSeatsBooked()) {
                throw new Exception("Plus assez de places disponibles pour accepter cette réservation.");
            }
            // Decrement seats
            tripDAO.updateSeatsAvailable(trip.getTripId(), trip.getSeatsAvailable() - booking.getSeatsBooked());
            bookingDAO.updateBookingStatus(booking.getBookingId(), "ACCEPTED");
        } else {
            bookingDAO.updateBookingStatus(booking.getBookingId(), "REJECTED");
        }
        // If accepted and seats == 0, maybe update trip status to FULL?
        // Optional enhancement.
    }
}
