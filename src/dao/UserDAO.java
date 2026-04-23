package dao;

import model.User;
import util.PasswordUtils;
import java.sql.*;

public class UserDAO {

    /**
     * التحقق من المستخدم لتسجيل الدخول.
     * 
     * @param email    إيميل المستخدم
     * @param password كلمة المرور
     * @return User كائن المستخدم المكتمل إذا نجح تسجيل الدخول، وإلا null.
     */
    public User authenticate(String email, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        String sql = "SELECT * FROM Users WHERE email = ?";

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (PasswordUtils.checkPassword(password, storedHash)) {
                    user = extractUserFromResultSet(rs, false);
                }
            }
        } finally {
            // إغلاق الموارد دائمًا
            DBConnection.close(rs);
            DBConnection.close(ps);
            DBConnection.close(conn);
        }
        return user;
    }

    // داخل كلاس UserDAO
    public boolean isEmailTaken(String email) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // البريد الإلكتروني موجود بالفعل
            }
            return false;
        } finally {
            DBConnection.close(rs);
            DBConnection.close(ps);
            DBConnection.close(conn);
        }
    }

    // داخل كلاس UserDAO

    public User registerUser(User user) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;

        // notice that we use "role", "gender", "phone_number", "is_admin"
        // Explicitly setting average_rating and total_trips to 0
        String sql = "INSERT INTO Users (name, email, password, role, gender, phone_number, is_admin, date_of_birth, vehicle_model, vehicle_plate, average_rating, total_trips) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // connection to database
            // We must use PreparedStatement.RETURN_GENERATED_KEYS to get the new user_id
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Setting values
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, PasswordUtils.hashPassword(user.getPassword())); // Encrypted Password
            ps.setString(4, user.getRole());
            ps.setString(5, user.getGender());
            ps.setString(6, user.getPhoneNumber());
            // isAdmin = FALSE by default upon registration
            ps.setBoolean(7, false);

            if (user.getDateOfBirth() != null) {
                ps.setDate(8, Date.valueOf(user.getDateOfBirth()));
            } else {
                ps.setNull(8, Types.DATE);
            }

            ps.setString(9, user.getVehicleModel());
            ps.setString(10, user.getVehiclePlate());

            // Set initial rating and trips to 0
            ps.setDouble(11, 0.0);
            ps.setInt(12, 0);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("فشل إنشاء المستخدم، لم يتم تعديل أي صف.");
            }

            // استرجاع الـ ID المُنشأ تلقائياً
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("فشل إنشاء المستخدم، لم يتم الحصول على ID.");
                }
            }
            return user;
        } finally {
            DBConnection.close(ps);
            DBConnection.close(conn);
        }
    }

    /**
     * Findings a user by their ID.
     * Useful for fetching driver details for a trip.
     */
    public User getUserById(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        String sql = "SELECT * FROM Users WHERE user_id = ?";

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            rs = ps.executeQuery();

            if (rs.next()) {
                user = extractUserFromResultSet(rs, false); // Do not set password when fetching by ID
            }
        } finally {
            DBConnection.close(rs);
            DBConnection.close(ps);
            DBConnection.close(conn);
        }
        return user;
    }

    private User extractUserFromResultSet(ResultSet rs, boolean includePassword) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        if (includePassword) {
            user.setPassword(rs.getString("password"));
        }
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setGender(rs.getString("gender"));
        user.setRole(rs.getString("role"));
        user.setIsAdmin(rs.getBoolean("is_admin"));

        if (rs.getDate("date_of_birth") != null) {
            user.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        }

        user.setAvatarPath(rs.getString("avatar_path"));
        user.setBackgroundPath(rs.getString("background_path"));

        // Driver Details
        user.setVehicleModel(rs.getString("vehicle_model"));
        user.setVehiclePlate(rs.getString("vehicle_plate"));

        try {
            user.setVehicleColor(rs.getString("vehicle_color"));
            user.setLicensePath(rs.getString("license_path"));
            user.setVerificationStatus(rs.getString("verification_status"));
        } catch (SQLException e) {
            // Columns might not exist yet if DB script wasn't run
        }

        // Fetch average rating computed from Ratings table or stored
        // For now, if stored in user table:
        try {
            user.setAverageRating(rs.getDouble("average_rating"));
            user.setTotalTrips(rs.getInt("total_trips"));
        } catch (SQLException e) {
            // ignore
        }

        return user;
    }

    public boolean updateUserImages(int userId, String avatarPath, String backgroundPath) throws SQLException {
        String sql = "UPDATE Users SET avatar_path = ?, background_path = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, avatarPath);
            ps.setString(2, backgroundPath);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateUserRating(int userId, double avgRating, int totalTrips) throws SQLException {
        // total_trips is usually "trips offered", but sometimes used as "ratings count"
        // depending on interpretation.
        // The UI says "X Trips" (totalTrips).
        // If we want totalTrips to count ratings, we update it here.
        // If totalTrips counts *drives*, we shouldn't overwrite it with rating count.
        // Let's assume for now totalTrips is trips driven. But UserDAO.extract... reads
        // it.
        // TripService usually increments totalTrips when a trip is completed.
        // BUT, looking at User model: private int totalTrips;
        // SearchTripPanel displays: driver.getTotalTrips() + " trips".
        // If we want to store rating count, maybe we should used a different column or
        // rely on Trips table count.
        // For now, I will ONLY update average_rating.

        String sql = "UPDATE Users SET average_rating = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, avgRating);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateDriverInfo(User user) throws SQLException {
        String sql = "UPDATE Users SET vehicle_model = ?, vehicle_plate = ?, vehicle_color = ?, license_path = ?, verification_status = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getVehicleModel());
            stmt.setString(2, user.getVehiclePlate());
            stmt.setString(3, user.getVehicleColor());
            stmt.setString(4, user.getLicensePath());
            stmt.setString(5, user.getVerificationStatus());
            stmt.setInt(6, user.getUserId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Admin Methods
    public java.util.List<User> getPendingDrivers() throws SQLException {
        java.util.List<User> drivers = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Users WHERE role = 'DRIVER' AND verification_status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                drivers.add(extractUserFromResultSet(rs, false));
            }
        }
        return drivers;
    }

    public boolean updateVerificationStatus(int userId, String status) throws SQLException {
        String sql = "UPDATE Users SET verification_status = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public java.util.List<User> getTopRatedUsers(int limit) throws SQLException {
        java.util.List<User> users = new java.util.ArrayList<>();
        // Fetch top rated drivers (role = 'DRIVER')
        // Order by average_rating DESC, then total_trips DESC (as tie breaker)
        String sql = "SELECT * FROM Users WHERE role = 'DRIVER' ORDER BY average_rating DESC, total_trips DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs, false));
                }
            }
        }
        return users;
    }

    // --- Admin Management Methods ---

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public java.util.List<User> searchUsersByRole(String role, String query) throws SQLException {
        java.util.List<User> users = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Users WHERE role = ?";

        if (query != null && !query.trim().isEmpty()) {
            sql += " AND (name LIKE ? OR email LIKE ? OR phone_number LIKE ?)";
        }

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);

            if (query != null && !query.trim().isEmpty()) {
                String searchPattern = "%" + query.trim() + "%";
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs, false));
                }
            }
        }
        return users;
    }
}