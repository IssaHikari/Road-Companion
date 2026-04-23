package model;

import java.sql.Timestamp;

public class Booking {
    private int bookingId;
    private int tripId;
    private int passengerId;
    private int seatsBooked;
    private String paymentStatus;
    private String bookingStatus; // "PENDING", "ACCEPTED", "REJECTED"
    private Timestamp bookingDate;

    // Optional: Trip details for display
    private Trip tripDetails;
    private User driverDetails;

    public Booking() {
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Trip getTripDetails() {
        return tripDetails;
    }

    public void setTripDetails(Trip tripDetails) {
        this.tripDetails = tripDetails;
    }

    public User getDriverDetails() {
        return driverDetails;
    }

    public void setDriverDetails(User driverDetails) {
        this.driverDetails = driverDetails;
    }

    private User passengerDetails;

    public User getPassengerDetails() {
        return passengerDetails;
    }

    public void setPassengerDetails(User passengerDetails) {
        this.passengerDetails = passengerDetails;
    }
}
