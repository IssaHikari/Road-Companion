package view;

import model.Booking;
import model.User;
import service.BookingService;
import util.LanguageManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class DriverBookingsPanel extends JPanel {

    private final User currentUser;
    private final BookingService bookingService;
    private final LanguageManager lang;
    private JPanel bookingsContainer;

    public DriverBookingsPanel(User user) {
        this.currentUser = user;
        this.bookingService = new BookingService();
        this.lang = LanguageManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        if (lang.isArabic()) {
            this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        JLabel title = new JLabel((lang.isArabic()) ? "طلبات الحجز" : "Demandes de Réservation", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        header.add(title, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("↻");
        refreshBtn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 24));
        refreshBtn.setToolTipText("Actualiser");
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadBookings());
        header.add(refreshBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        bookingsContainer = new JPanel();
        bookingsContainer.setLayout(new BoxLayout(bookingsContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(bookingsContainer);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        loadBookings();
    }

    private void loadBookings() {
        bookingsContainer.removeAll();
        try {
            List<Booking> requests = bookingService.getDriverBookingRequests(currentUser.getUserId());

            if (requests.isEmpty()) {
                bookingsContainer
                        .add(new JLabel((lang.isArabic()) ? "لا توجد طلبات حجز." : "Aucune demande de réservation."));
            } else {
                for (Booking booking : requests) {
                    bookingsContainer.add(createBookingCard(booking));
                    bookingsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }
        } catch (Exception e) {
            bookingsContainer.add(
                    new JLabel((lang.isArabic() ? "خطأ في التحميل: " : "Erreur de chargement: ") + e.getMessage()));
        }
        bookingsContainer.revalidate();
        bookingsContainer.repaint();
    }

    private JPanel createBookingCard(Booking booking) {
        JPanel card = new JPanel(new BorderLayout(15, 10)); // Increased H-Gap

        // 1. Enhanced Card Border (Rounded + Shadow)
        card.setBorder(new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(x + 3, y + 3, width - 6, height - 6, 20, 20);

                // Background (needed for non-opaque panel look)
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y, width - 4, height - 4, 20, 20);

                // Fine Border
                g2.setColor(new Color(220, 220, 220));
                g2.drawRoundRect(x, y, width - 4, height - 4, 20, 20);

                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(15, 20, 15, 20);
            }
        });

        card.setMaximumSize(new Dimension(850, 100));
        card.setPreferredSize(new Dimension(850, 100));

        card.setOpaque(false); // Enable custom painting
        if (lang.isArabic()) {
            card.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 0)); // Reduced gaps
        infoPanel.setOpaque(false);
        if (lang.isArabic()) {
            infoPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        User passenger = booking.getPassengerDetails();
        String passengerName = (passenger != null) ? passenger.getName() : "Passager #" + booking.getPassengerId();

        String lblPass = lang.isArabic() ? "الراكب: " : "Passager: ";
        String lblTrip = lang.isArabic() ? "المسار: " : "Trajet: ";
        String lblSeats = lang.isArabic() ? "المقاعد: " : "Places: ";
        String lblDate = lang.isArabic() ? "التاريخ: " : "Date: ";

        infoPanel.add(createLabel(lblPass + passengerName, Font.BOLD));
        infoPanel.add(createLabel(
                lblTrip + booking.getTripDetails().getOrigin() + " -> " + booking.getTripDetails().getDestination(),
                Font.BOLD));
        infoPanel.add(createLabel(lblSeats + booking.getSeatsBooked(), Font.PLAIN));
        infoPanel.add(createLabel(lblDate + booking.getBookingDate(), Font.PLAIN));

        // 2. Circular Avatar Panel (Left)
        JPanel avatarPanel = new JPanel(new GridBagLayout()); // Centering
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(80, 80));

        JLabel avatarLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight());

                // Circle Clip
                g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));

                if (getIcon() != null) {
                    g2.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, size, size, this);
                } else {
                    g2.setColor(new Color(63, 81, 181));
                    g2.fillOval(0, 0, size, size);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 24));
                    String text = getText();
                    if (text != null && !text.isEmpty()) {
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(text, (size - fm.stringWidth(text)) / 2, (size + fm.getAscent()) / 2 - 4);
                    }
                }
                g2.dispose();
            }
        };

        avatarLabel.setPreferredSize(new Dimension(65, 65));
        avatarLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        avatarLabel.setToolTipText(lang.isArabic() ? "عرض ملف الراكب" : "Voir le profil du passager");

        if (passenger != null && passenger.getAvatarPath() != null && new File(passenger.getAvatarPath()).exists()) {
            // Load original to paint in circle
            avatarLabel.setIcon(new ImageIcon(passenger.getAvatarPath()));
        } else {
            avatarLabel.setText(passengerName.substring(0, 1).toUpperCase());
        }

        avatarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (passenger != null) {
                    Window win = SwingUtilities.getWindowAncestor(DriverBookingsPanel.this);
                    new ProfileDialog(win, passenger, true).setVisible(true);
                }
            }
        });

        avatarPanel.add(avatarLabel);
        if (lang.isArabic()) {
            card.add(avatarPanel, BorderLayout.EAST);
            card.add(infoPanel, BorderLayout.CENTER);
            // Action panel will be added to West effectively in RTL or manual
        } else {
            card.add(avatarPanel, BorderLayout.WEST);
            card.add(infoPanel, BorderLayout.CENTER);
        }

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15)); // Align vertically
        actionPanel.setOpaque(false);

        String status = booking.getBookingStatus();
        if (status == null)
            status = "PENDING";

        if ("PENDING".equals(status)) {
            // 3. Styled Buttons
            JButton acceptBtn = createStyledButton(lang.isArabic() ? "قبول" : "Accepter", new Color(76, 175, 80));
            acceptBtn.addActionListener(e -> processBooking(booking, true));

            JButton rejectBtn = createStyledButton(lang.isArabic() ? "رفض" : "Refuser", new Color(244, 67, 54));
            rejectBtn.addActionListener(e -> processBooking(booking, false));

            actionPanel.add(acceptBtn);
            actionPanel.add(rejectBtn);
        } else {
            String statusText;
            Color statusColor;
            if ("ACCEPTED".equals(status)) {
                statusText = lang.isArabic() ? "مقبول" : "ACCEPTED";
                statusColor = new Color(76, 175, 80);
            } else if ("REJECTED".equals(status)) {
                statusText = lang.isArabic() ? "مرفوض" : "REJECTED";
                statusColor = new Color(244, 67, 54);
            } else {
                statusText = status;
                statusColor = Color.BLACK;
            }

            JLabel statusLabel = new JLabel((lang.isArabic() ? "الحالة: " : "Statut: ") + statusText);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
            statusLabel.setForeground(statusColor);

            actionPanel.add(statusLabel);
        }

        if (lang.isArabic()) {
            card.add(actionPanel, BorderLayout.WEST);
        } else {
            card.add(actionPanel, BorderLayout.EAST);
        }

        return card;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 34)); // Fixed size

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20); // Rounded 20
                super.paint(g2, c);
                g2.dispose();
            }
        });
        return btn;
    }

    private JLabel createLabel(String text, int style) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", style, 14));
        return lbl;
    }

    private void processBooking(Booking booking, boolean accepted) {
        try {
            bookingService.processBookingDecision(booking, accepted);
            String message = accepted
                    ? (lang.isArabic() ? "تم قبول الحجز." : "Réservation acceptée.")
                    : (lang.isArabic() ? "تم رفض الحجز." : "Réservation refusée.");
            JOptionPane.showMessageDialog(this, message);
            loadBookings(); // Refresh
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, (lang.isArabic() ? "خطأ: " : "Erreur: ") + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
