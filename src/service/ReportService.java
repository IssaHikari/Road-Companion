package service;

import dao.ReportDAO;
import model.Report;
import java.sql.SQLException;
import java.util.List;
import java.util.Collections;

public class ReportService {

    private final ReportDAO reportDAO;

    public ReportService() {
        this.reportDAO = new ReportDAO();
    }

    public boolean submitReport(int reporterId, int reportedId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("La raison du signalement est requise.");
        }

        Report report = new Report(reporterId, reportedId, reason);
        try {
            return reportDAO.createReport(report);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Report> getAllReports() {
        try {
            return reportDAO.getAllReports();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean deleteReport(int reportId) {
        try {
            return reportDAO.deleteReport(reportId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
