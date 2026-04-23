
package model;

import java.time.LocalDate;

public class User {

    // معلومات أساسية
    private int userId;
    private String name;
    private String email;
    private String password; // يجب تخزينها مشفرة في الإنتاج!
    private String phoneNumber;
    private String gender; // 'Male' or 'Female'
    private String role; // 'PASSENGER' or 'DRIVER'
    private boolean isAdmin;
    private LocalDate dateOfBirth;

    // تفاصيل الملف الشخصي
    private String avatarPath;
    private String backgroundPath;

    // تفاصيل السائق (يتم جلبها عند الحاجة)
    private double averageRating;
    private int totalTrips;

    // ... Constructor, Getters and Setters for all fields ...

    // مثال لـ Getter:
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // ... (تكملة باقي الدوال) ...

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getTotalTrips() {
        return totalTrips;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void setBackgroundPath(String backgroundPath) {
        this.backgroundPath = backgroundPath;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public void setTotalTrips(int totalTrips) {
        this.totalTrips = totalTrips;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // Vehicle Details (for Drivers)
    private String vehicleModel;
    private String vehiclePlate;
    private String vehicleColor; // NEW
    private String licensePath; // NEW
    private String verificationStatus; // 'UNVERIFIED', 'PENDING', 'VERIFIED', 'REJECTED'

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getLicensePath() {
        return licensePath;
    }

    public void setLicensePath(String licensePath) {
        this.licensePath = licensePath;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}