package view;

import model.Trip;
import model.User;
import service.TripService;
import util.LanguageManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Driver Trips Panel with i18n support
 */
public class DriverTripsPanel extends JPanel {

    private final User currentUser;
    private final TripService tripService;
    private final LanguageManager lang;
    private JPanel tripsListPanel;

    public DriverTripsPanel(User user) {
        this.currentUser = user;
        this.tripService = new TripService();
        this.lang = LanguageManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        if (lang.isArabic()) {
            this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));
        if (lang.isArabic())
            header.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel title = new JLabel(lang.get("driver.trips.title"));
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(50, 50, 50));

        JButton refreshBtn = new JButton(lang.get("driver.trips.refresh"));
        styleButton(refreshBtn, new Color(100, 100, 100));
        refreshBtn.addActionListener(e -> loadTrips());

        header.add(title, lang.isArabic() ? BorderLayout.EAST : BorderLayout.WEST);
        header.add(refreshBtn, lang.isArabic() ? BorderLayout.WEST : BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Content
        tripsListPanel = new JPanel();
        tripsListPanel.setLayout(new BoxLayout(tripsListPanel, BoxLayout.Y_AXIS));
        tripsListPanel.setBackground(new Color(245, 247, 250));

        JScrollPane scrollPane = new JScrollPane(tripsListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        if (lang.isArabic())
            scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        add(scrollPane, BorderLayout.CENTER);

        loadTrips();
    }

    private void loadTrips() {
        tripsListPanel.removeAll();
        try {
            List<Trip> trips = tripService.getTripsByDriverId(currentUser.getUserId());

            if (trips.isEmpty()) {
                JPanel emptyPanel = new JPanel(new FlowLayout());
                emptyPanel.setOpaque(false);
                emptyPanel.add(new JLabel(lang.get("driver.trips.no_trips")));
                tripsListPanel.add(emptyPanel);
            } else {
                for (Trip t : trips) {
                    tripsListPanel.add(createTripCard(t));
                    tripsListPanel.add(Box.createVerticalStrut(15));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, lang.get("dialog.error") + ": " + e.getMessage());
        }
        tripsListPanel.revalidate();
        tripsListPanel.repaint();
    }

    private JPanel createTripCard(Trip t) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 20, 15, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        if (lang.isArabic())
            card.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        if (lang.isArabic())
            infoPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        String tripTitle = String.format("%s ➝ %s", t.getOrigin(), t.getDestination());
        JLabel routeLabel = new JLabel(tripTitle);
        routeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        if (lang.isArabic())
            routeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        String details = String.format("%s %s %s %s | %s %.2f DZD | %s %s",
                lang.get("driver.trips.depart"), t.getDate(), lang.get("driver.trips.at"), t.getTime(),
                lang.get("driver.trips.price"), t.getPrice(),
                lang.get("driver.trips.status"), t.getStatus());

        JLabel detailLabel = new JLabel(details);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailLabel.setForeground(Color.GRAY);
        if (lang.isArabic())
            detailLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        infoPanel.add(routeLabel);
        infoPanel.add(detailLabel);
        card.add(infoPanel, BorderLayout.CENTER);

        // Actions
        JPanel actionPanel = new JPanel(new FlowLayout(lang.isArabic() ? FlowLayout.LEFT : FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        if (lang.isArabic())
            actionPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        if (!"COMPLETED".equals(t.getStatus()) && !"CANCELLED".equals(t.getStatus())) {
            JButton completeBtn = new JButton(lang.get("driver.trips.btn.complete"));
            styleButton(completeBtn, new Color(76, 175, 80));
            completeBtn.addActionListener(e -> markCompleted(t));
            actionPanel.add(completeBtn);

            JButton editBtn = new JButton(lang.isArabic() ? "تعديل" : "Modify");
            styleButton(editBtn, new Color(255, 193, 7)); // Amber color
            editBtn.setForeground(Color.BLACK); // Better contrast on amber
            editBtn.addActionListener(e -> editTrip(t));
            actionPanel.add(editBtn);
        }

        JButton deleteBtn = new JButton(lang.get("driver.trips.btn.delete"));
        styleButton(deleteBtn, new Color(244, 67, 54));
        deleteBtn.addActionListener(e -> deleteTrip(t));
        actionPanel.add(deleteBtn);

        card.add(actionPanel, lang.isArabic() ? BorderLayout.WEST : BorderLayout.EAST);
        return card;
    }

    private void editTrip(Trip t) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                lang.isArabic() ? "تعديل الرحلة" : "Edit Trip", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);

        CreateTripPanel editPanel = new CreateTripPanel(currentUser, t);
        dialog.add(editPanel);

        dialog.setVisible(true);
        // Logic continues after dialog is closed
        loadTrips(); // Refresh list after edit
    }

    private void deleteTrip(Trip t) {
        int confirm = JOptionPane.showConfirmDialog(this,
                lang.get("driver.trips.delete.confirm.msg"),
                lang.get("driver.trips.delete.confirm.title"), JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (tripService.deleteTrip(t.getTripId())) {
                    loadTrips();
                } else {
                    JOptionPane.showMessageDialog(this, lang.get("driver.trips.delete.error"));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, lang.get("dialog.error") + ": " + e.getMessage());
            }
        }
    }

    private void markCompleted(Trip t) {
        try {
            tripService.updateTripStatus(t.getTripId(), "COMPLETED");
            loadTrips();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, lang.get("dialog.error") + ": " + e.getMessage());
        }
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
