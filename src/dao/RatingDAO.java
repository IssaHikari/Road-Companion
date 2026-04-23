package dao;

import model.Rating;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO {

    public boolean createRating(Rating rating) throws SQLException {
        String sql = "INSERT INTO Ratings (rated_user_id, rater_user_id, score, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating.getRatedUserId());
            stmt.setInt(2, rating.getRaterUserId());
            stmt.setInt(3, rating.getScore());
            stmt.setString(4, rating.getComment());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Rating> getRatingsForUser(int userId) throws SQLException {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM Ratings WHERE rated_user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rating r = new Rating();
                    r.setRatingId(rs.getInt("rating_id"));
                    r.setRatedUserId(rs.getInt("rated_user_id"));
                    r.setRaterUserId(rs.getInt("rater_user_id"));
                    r.setScore(rs.getInt("score"));
                    r.setComment(rs.getString("comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    ratings.add(r);
                }
            }
        }
        return ratings;
    }

    // Check if passenger has already rated this driver for a specific trip context?
    // The requirement is "rate driver", usually once per trip.
    // The schema links users, not trips directly. Typically we might want to link
    // to trip_id in Ratings to prevent duplicates.
    // However, schema provided only has user_ids. I will stick to schema.

    public boolean hasRated(int raterId, int ratedId) throws SQLException {
        String sql = "SELECT count(*) FROM Ratings WHERE rater_user_id = ? AND rated_user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, raterId);
            stmt.setInt(2, ratedId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public double calculateAverageRating(int userId) throws SQLException {
        String sql = "SELECT AVG(score) FROM Ratings WHERE rated_user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    public int countRatings(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Ratings WHERE rated_user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
