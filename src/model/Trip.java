package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents a Trip offered by a driver.
 * Maps to the Trips_Offered table in the database.
 */
public class Trip {

    // Primary Key
    private int tripId;

    // Driver's User ID (Foreign Key)
    private int driverId;

    // Trip details
    private String origin;
    private String destination;
    private LocalDate date;
    private LocalTime time;
    private double price;
    private int seatsAvailable;
    private String description;

    // Status: AVAILABLE, FULL, COMPLETED, CANCELLED
    private String status;

    public Trip() {
        this.status = "AVAILABLE";
    }

    // ================== Getters and Setters ==================

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId=" + tripId +
                ", driverId=" + driverId +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", price=" + price +
                ", seatsAvailable=" + seatsAvailable +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Trip trip = (Trip) o;
        return tripId == trip.tripId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId);
    }
}
