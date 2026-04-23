package view;

import model.Booking;
import model.User;
import service.BookingService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import model.Rating;

public class PassengerBookingsPanel extends JPanel {

    private final User currentUser;
    private final BookingService bookingService;
    private JPanel bookingsListPanel;

    public PassengerBookingsPanel(User user) {
        this.currentUser = user;
        this.bookingService = new BookingService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250)); // Light grey bg

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Mes Réservations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(50, 50, 50));

        JButton refreshBtn = new JButton("Actualiser");
        styleButton(refreshBtn, new Color(100, 100, 100));
        refreshBtn.addActionListener(e -> loadBookings());

        header.add(title, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Content
        bookingsListPanel = new JPanel();
        bookingsListPanel.setLayout(new BoxLayout(bookingsListPanel, BoxLayout.Y_AXIS));
        bookingsListPanel.setBackground(new Color(245, 247, 250));

        JScrollPane scrollPane = new JScrollPane(bookingsListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        loadBookings();
    }

    private void loadBookings() {
        bookingsListPanel.removeAll();
        try {
            List<Booking> bookings = bookingService.getPassengerBookings(currentUser.getUserId());

            if (bookings.isEmpty()) {
                JPanel emptyPanel = new JPanel(new FlowLayout());
                emptyPanel.setOpaque(false);
                emptyPanel.add(new JLabel("Aucune réservation trouvée."));
                bookingsListPanel.add(emptyPanel);
            } else {
                for (Booking b : bookings) {
                    bookingsListPanel.add(createBookingCard(b));
                    bookingsListPanel.add(Box.createVerticalStrut(15));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
            e.printStackTrace();
        }
        bookingsListPanel.revalidate();
        bookingsListPanel.repaint();
    }

    private JPanel createBookingCard(Booking b) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 20, 15, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);

        String tripTitle = String.format("%s ➝ %s",
                b.getTripDetails().getOrigin(), b.getTripDetails().getDestination());
        JLabel routeLabel = new JLabel(tripTitle);
        routeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        String details = String.format("Date: %s à %s | Chauffeur: %s | Places: %d | Prix: %.2f DZD",
                b.getTripDetails().getDate(), b.getTripDetails().getTime(),
                b.getDriverDetails().getName(), b.getSeatsBooked(),
                b.getTripDetails().getPrice() * b.getSeatsBooked());
        JLabel detailLabel = new JLabel(details);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailLabel.setForeground(Color.GRAY);

        infoPanel.add(routeLabel);
        infoPanel.add(detailLabel);
        card.add(infoPanel, BorderLayout.CENTER);

        // Status & Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JLabel statusLabel = new JLabel(getStatusText(b));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(getStatusColor(b));
        statusLabel.setBorder(new EmptyBorder(0, 0, 0, 15));
        actionPanel.add(statusLabel);

        // PAY BUTTON Logic
        if ("ACCEPTED".equalsIgnoreCase(b.getBookingStatus()) &&
                "PENDING".equalsIgnoreCase(b.getPaymentStatus())) {

            JButton payButton = new JButton("Payer");
            styleButton(payButton, new Color(46, 204, 113)); // Green
            payButton.addActionListener(e -> handlePayment(b));
            actionPanel.add(payButton);
        } else if ("PAID".equalsIgnoreCase(b.getPaymentStatus())) {
            JLabel paidBadge = new JLabel("PAYÉ");
            paidBadge.setOpaque(true);
            paidBadge.setBackground(new Color(46, 204, 113));
            paidBadge.setForeground(Color.WHITE);
            paidBadge.setBorder(new EmptyBorder(5, 10, 5, 10));
            actionPanel.add(paidBadge);

            // Add Rating Button
            JButton rateButton = new JButton("Noter");
            styleButton(rateButton, new Color(255, 193, 7)); // Amber
            rateButton.setForeground(Color.BLACK);
            rateButton.addActionListener(e -> handleRating(b));
            actionPanel.add(rateButton);
        }

        // Add Rating button if Completed and Paid (Optional, but good for flow)
        // ... (Leaving required Rating feature for later as per task list)

        card.add(actionPanel, BorderLayout.EAST);
        return card;
    }

    private void handlePayment(Booking b) {
        // Calculate total amount
        double amount = b.getTripDetails().getPrice() * b.getSeatsBooked();

        // Open Payment Dialog
        Window window = SwingUtilities.getWindowAncestor(this);
        PaymentDialog dialog = new PaymentDialog((JFrame) window, amount, (success) -> {
            if (success) {
                try {
                    bookingService.updatePaymentStatus(b.getBookingId(), "PAID");
                    JOptionPane.showMessageDialog(this, "Paiement effectué avec succès!", "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadBookings(); // Refresh
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour: " + ex.getMessage());
                }
            }
        });
        dialog.setVisible(true);
    }

    private void handleRating(Booking b) {
        int driverId = b.getTripDetails().getDriverId();
        int userId = currentUser.getUserId();

        Window window = SwingUtilities.getWindowAncestor(this);
        RatingDialog dialog = new RatingDialog((JFrame) window, driverId, userId, (rating) -> {
            try {
                bookingService.rateDriver(rating);
                JOptionPane.showMessageDialog(this, "Merci pour votre avis!", "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'envoi de l'avis: " + e.getMessage());
            }
        });
        dialog.setVisible(true);
    }

    private String getStatusText(Booking b) {
        String status = b.getBookingStatus();
        if ("PENDING".equals(status))
            return "En attente";
        if ("ACCEPTED".equals(status))
            return "Confirmé";
        if ("REJECTED".equals(status))
            return "Refusé";
        if ("CANCELLED".equals(status))
            return "Annulé";
        return status;
    }

    private Color getStatusColor(Booking b) {
        String status = b.getBookingStatus();
        if ("PENDING".equals(status))
            return Color.ORANGE;
        if ("ACCEPTED".equals(status))
            return new Color(46, 204, 113);
        if ("REJECTED".equals(status))
            return Color.RED;
        return Color.GRAY;
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private ImageIcon getScaledIcon(String path, int w, int h) {
        try {
            java.io.File f = new java.io.File(path);
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(path);
                Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showReportDialog(User driver) {
        Window win = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(win, "Signaler un conducteur", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(20, 20));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel("Pourquoi signalez-vous " + driver.getName() + " ?");
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lbl);
        content.add(Box.createVerticalStrut(10));

        JTextArea reasonArea = new JTextArea(5, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        content.add(new JScrollPane(reasonArea));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton sendBtn = new JButton("Envoyer");
        sendBtn.setBackground(new Color(231, 76, 60));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.addActionListener(e -> {
            String reason = reasonArea.getText();
            if (!reason.trim().isEmpty()) {
                boolean success = new service.ReportService().submitReport(currentUser.getUserId(), driver.getUserId(),
                        reason);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Signalement envoyé à l'administration.");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de l'envoi.");
                }
            }
        });
        footer.add(sendBtn);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
