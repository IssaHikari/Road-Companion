package view;

import model.Trip;
import model.User;
import service.BookingService;
import service.ReportService;
import javax.swing.*;
import java.awt.*;

public class TripDetailsDialog extends JDialog {

    private final BookingService bookingService;
    private final ReportService reportService;
    private final User currentUser;
    private final User driver;
    private final Trip trip;

    public TripDetailsDialog(Window owner, Trip trip, User driver, User currentUser) {
        super(owner, "Détails du Trajet", ModalityType.APPLICATION_MODAL);
        this.trip = trip;
        this.driver = driver;
        this.currentUser = currentUser;
        this.bookingService = new BookingService();
        this.reportService = new ReportService();

        setSize(450, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(owner);

        // --- Header (Route) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(63, 81, 181));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        JLabel routeLabel = new JLabel(trip.getOrigin() + " ➝ " + trip.getDestination());
        routeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        routeLabel.setForeground(Color.WHITE);
        headerPanel.add(routeLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Details Panel ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Trip Info
        addDetailRow(contentPanel, gbc, row++, "Date :", trip.getDate().toString());
        addDetailRow(contentPanel, gbc, row++, "Heure :", trip.getTime().toString());
        addDetailRow(contentPanel, gbc, row++, "Prix :", String.format("%.2f DZ", trip.getPrice()));
        addDetailRow(contentPanel, gbc, row++, "Places disp. :", String.valueOf(trip.getSeatsAvailable()));

        // Divider
        JSeparator sep = new JSeparator();
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        contentPanel.add(sep, gbc);

        // Driver Info
        gbc.gridwidth = 1;
        addDetailRow(contentPanel, gbc, row++, "Conducteur :", driver.getName());
        addDetailRow(contentPanel, gbc, row++, "Contact :", driver.getPhoneNumber());
        addDetailRow(contentPanel, gbc, row++, "Véhicule :",
                (driver.getVehicleModel() != null ? driver.getVehicleModel() : "N/A"));

        // Car Image Placeholder
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        JLabel carImg = new JLabel("Photo Véhicule (Placeholder)", SwingConstants.CENTER);
        carImg.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        carImg.setPreferredSize(new Dimension(300, 100));
        contentPanel.add(carImg, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // --- Actions ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        // Only show Book/Report button if user is a PASSENGER and not the driver
        if (currentUser.getRole().equalsIgnoreCase("PASSENGER") && currentUser.getUserId() != driver.getUserId()) {

            // BOOK TRIP
            JButton bookButton = new JButton("Réserver");
            bookButton.setBackground(new Color(76, 175, 80)); // Green
            bookButton.setForeground(Color.WHITE);
            bookButton.addActionListener(e -> handleBooking());
            buttonPanel.add(bookButton);

            // REPORT DRIVER
            JButton reportButton = new JButton("Signaler");
            reportButton.setBackground(new Color(244, 67, 54)); // Red
            reportButton.setForeground(Color.WHITE);
            reportButton.addActionListener(e -> handleReport());
            buttonPanel.add(reportButton);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(val, gbc);
    }

    private void handleBooking() {
        String seatsStr = JOptionPane.showInputDialog(this, "Nombre de places à réserver :", "1");
        if (seatsStr != null) {
            try {
                int seats = Integer.parseInt(seatsStr);
                bookingService.bookTrip(trip.getTripId(), currentUser.getUserId(), seats);
                JOptionPane.showMessageDialog(this, "Réservation réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Nombre invalide.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleReport() {
        JTextArea textArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(textArea);
        int result = JOptionPane.showConfirmDialog(this, new Object[] { "Motif du signalement :", scrollPane },
                "Signaler le conducteur", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                reportService.submitReport(currentUser.getUserId(), driver.getUserId(), textArea.getText());
                JOptionPane.showMessageDialog(this, "Signalement envoyé.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
            }
        }
    }
}
