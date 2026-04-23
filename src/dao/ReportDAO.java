package dao;

import model.Report;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public ReportDAO() {
        createTableIfNotExists();
        checkAndAddColumns();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS Reports (" +
                "report_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "reporter_id INT NOT NULL, " +
                "reported_user_id INT NOT NULL, " +
                "reason TEXT NOT NULL, " +
                "status ENUM('PENDING', 'DISMISSED', 'RESOLVED') DEFAULT 'PENDING', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (reporter_id) REFERENCES Users(user_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (reported_user_id) REFERENCES Users(user_id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB;";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkAndAddColumns() {
        // Migration to add created_at if it doesn't exist
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement()) {
            try {
                stmt.execute("ALTER TABLE Reports ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            } catch (SQLException e) {
                if (e.getErrorCode() != 1060) { // If error is NOT duplicate column
                    // e.printStackTrace();
                }
            }

            try {
                stmt.execute("ALTER TABLE Reports ADD COLUMN reason TEXT NOT NULL");
            } catch (SQLException e) {
                if (e.getErrorCode() != 1060) {
                    // e.printStackTrace();
                }
            }

            try {
                stmt.execute(
                        "ALTER TABLE Reports ADD COLUMN status ENUM('PENDING', 'DISMISSED', 'RESOLVED') DEFAULT 'PENDING'");
            } catch (SQLException e) {
                if (e.getErrorCode() != 1060) {
                    // e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createReport(Report report) throws SQLException {
        // Attempt to write to both 'reason' AND 'content' to handle legacy/mixed schema
        // constraints
        // The error 'Field content doesn't have a default value' implies 'content'
        // exists and is NOT NULL.
        String sql = "INSERT INTO Reports (reporter_id, reported_user_id, reason, content) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, report.getReporterId());
            ps.setInt(2, report.getReportedUserId());
            ps.setString(3, report.getReason());
            ps.setString(4, report.getReason()); // Fill content so it's not empty
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // If 'content' column does NOT exist (e.g. fresh DB), this will fail. Fallback
            // to normal insert.
            if (e.getMessage().contains("Unknown column") || e.getMessage().contains("'content'")) {
                String fallbackSql = "INSERT INTO Reports (reporter_id, reported_user_id, reason) VALUES (?, ?, ?)";
                try (Connection conn = DBConnection.getConnection();
                        PreparedStatement psFallback = conn.prepareStatement(fallbackSql)) {
                    psFallback.setInt(1, report.getReporterId());
                    psFallback.setInt(2, report.getReportedUserId());
                    psFallback.setString(3, report.getReason());
                    return psFallback.executeUpdate() > 0;
                }
            }
            throw e; // Rethrow if it's another error
        }
    }

    public List<Report> getAllReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT r.*, u1.name as reporter_name, u2.name as reported_name " +
                "FROM Reports r " +
                "JOIN Users u1 ON r.reporter_id = u1.user_id " +
                "JOIN Users u2 ON r.reported_user_id = u2.user_id " +
                "ORDER BY r.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Report report = new Report();
                report.setId(rs.getInt("report_id"));
                report.setReporterId(rs.getInt("reporter_id"));
                report.setReporterName(rs.getString("reporter_name"));
                report.setReportedUserId(rs.getInt("reported_user_id"));
                report.setReportedUserName(rs.getString("reported_name"));

                // Robustly fetch reason/content
                String reasonText = null;
                try {
                    reasonText = rs.getString("content");
                } catch (SQLException ignored) {
                }

                if (reasonText == null || reasonText.trim().isEmpty()) {
                    try {
                        reasonText = rs.getString("reason");
                    } catch (SQLException ignored) {
                    }
                }

                report.setReason(reasonText != null ? reasonText : "Pas de motif");

                report.setStatus(rs.getString("status"));
                report.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                reports.add(report);
            }
        }
        return reports;
    }

    public boolean deleteReport(int reportId) throws SQLException {
        String sql = "DELETE FROM Reports WHERE report_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int reportId, String status) throws SQLException {
        String sql = "UPDATE Reports SET status = ? WHERE report_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reportId);
            return ps.executeUpdate() > 0;
        }
    }
}
