package view;

import model.Booking;
import model.Trip;
import model.User;
import service.BookingService;
import service.TripService;
import service.UserService;
import util.IconManager;
import util.LanguageManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchTripPanel extends JPanel {

    private final User currentUser;
    private final TripService tripService;
    private final UserService userService;
    private final BookingService bookingService;
    private final LanguageManager lang;

    // Fields
    private JButton originButton;
    private JButton destinationButton;
    private JTextField dateField;
    private String selectedOrigin = "";
    private String selectedDestination = "";

    // Banner Image
    private static Image bannerImage = null;

    // Results Container
    private JPanel resultsPanel;

    public SearchTripPanel(User user) {
        this.currentUser = user;
        this.tripService = new TripService();
        this.userService = new UserService();
        this.bookingService = new BookingService();
        this.lang = LanguageManager.getInstance();

        // Load Banner Image
        if (bannerImage == null) {
            new Thread(() -> {
                // Try to load the image if it exists in icons folder
                ImageIcon icon = IconManager.getIcon("search_hero_banner.jpg");
                if (icon != null) {
                    bannerImage = icon.getImage();
                    SwingUtilities.invokeLater(this::repaint);
                } else {
                    // Fallback try loading directly from file system if IconManager fails
                    try {
                        java.io.File imgFile = new java.io.File("icons/search_hero_banner.jpg");
                        if (imgFile.exists()) {
                            bannerImage = javax.imageio.ImageIO.read(imgFile);
                            SwingUtilities.invokeLater(this::repaint);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 251));

        if (lang != null && lang.isArabic()) {
            this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        // =================================================================================
        // 1. HERO BANNER SECTION
        // =================================================================================
        JPanel bannerPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Custom painting controls the background fully
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                if (bannerImage != null) {
                    g2.drawImage(bannerImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback Blue Gradient matching the image style
                    GradientPaint gp = new GradientPaint(0, 0, new Color(0, 85, 120), getWidth(), 0,
                            new Color(0, 140, 186));
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        // HEIGHT: 150. Width: Set explicit large width (1000) to ensure BorderLayout
        // stretches it.
        // using getWidth() (=0 at init) can cause layout artifacts.
        bannerPanel.setPreferredSize(new Dimension(1000, 150));

        GridBagConstraints gbc = new GridBagConstraints();

        // Title text
        String titleText = (lang != null && lang.isArabic()) ? "فائدة + مشاركة + سفر = مشاركة الرحلات"
                : "Avantage + partage + voyage = covoiturage";
        JLabel titleLabel = new JLabel(titleText);
        // FONT: 22
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        // PADDING: 5
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        bannerPanel.add(titleLabel, gbc);

        // =================================================================================
        // 2. UNIFIED SEARCH BAR (Floating)
        // =================================================================================
        JPanel searchBarContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                // RADIUS: 30
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        searchBarContainer.setOpaque(false);
        // DIMENSIONS: Height 50, Width handled by Layout (Full Width)
        searchBarContainer.setPreferredSize(new Dimension(10, 50));
        searchBarContainer.setLayout(new GridBagLayout());

        GridBagConstraints barGbc = new GridBagConstraints();
        barGbc.fill = GridBagConstraints.BOTH;
        barGbc.weighty = 1.0;

        // --- Segment 1: Origin (Depart) ---
        originButton = createFlatInputButton((lang != null && lang.isArabic()) ? "الانطلاق" : "Départ");
        originButton.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            view.LocationSelectorDialog dialog = new view.LocationSelectorDialog(win);
            dialog.setVisible(true);
            String result = dialog.getSelectedLocation();
            if (result != null) {
                selectedOrigin = result;
                originButton.setText(result);
                // FONT: 13
                originButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
                originButton.setForeground(new Color(33, 33, 33));
            }
        });

        barGbc.gridx = 0;
        barGbc.weightx = 0.3; // 30% width
        searchBarContainer.add(originButton, barGbc);

        // Separator
        barGbc.gridx = 1;
        barGbc.weightx = 0;
        searchBarContainer.add(createVerticalSeparator(), barGbc);

        // --- Segment 2: Destination ---
        destinationButton = createFlatInputButton((lang != null && lang.isArabic()) ? "الوصول" : "Destination");
        destinationButton.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            view.LocationSelectorDialog dialog = new view.LocationSelectorDialog(win);
            dialog.setVisible(true);
            String result = dialog.getSelectedLocation();
            if (result != null) {
                selectedDestination = result;
                destinationButton.setText(result);
                // FONT: 13
                destinationButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
                destinationButton.setForeground(new Color(33, 33, 33));
            }
        });

        barGbc.gridx = 2;
        barGbc.weightx = 0.3; // 30% width
        searchBarContainer.add(destinationButton, barGbc);

        // Separator
        barGbc.gridx = 3;
        barGbc.weightx = 0;
        searchBarContainer.add(createVerticalSeparator(), barGbc);

        // --- Segment 3: Date ---
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setOpaque(false);
        dateField = new JTextField(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateField.setBorder(null);
        // FONT: 13
        dateField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dateField.setForeground(new Color(33, 33, 33));
        dateField.setOpaque(false);

        JLabel dateIcon = new JLabel("📅 ");
        dateIcon.setBorder(new EmptyBorder(0, 15, 0, 0));
        dateIcon.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        datePanel.add(dateIcon, BorderLayout.WEST);
        datePanel.add(dateField, BorderLayout.CENTER);

        barGbc.gridx = 4;
        barGbc.weightx = 0.25; // 25% width
        searchBarContainer.add(datePanel, barGbc);

        // --- Segment 4: Search Button ---
        JButton searchBtn = new JButton((lang != null && lang.isArabic()) ? "بحث" : "Rechercher");
        searchBtn.setBackground(new Color(0, 175, 245)); // Cyan/Blue
        searchBtn.setForeground(Color.WHITE);
        // FONT: 14
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setContentAreaFilled(false); // Essential for rounded transparency
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        searchBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                // RADIUS: Full Height (Pill Shape)
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), c.getHeight(), c.getHeight());
                super.paint(g2, c);
            }
        });
        searchBtn.addActionListener(e -> searchTrips());

        barGbc.gridx = 5;
        barGbc.weightx = 0.15; // 15% width
        // Increased padding to make it look like a floating button
        barGbc.insets = new Insets(5, 5, 5, 10);
        searchBarContainer.add(searchBtn, barGbc);

        // Add Bar to Banner
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontal space
        gbc.weightx = 1.0; // Take up width
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Span full width
        // OVERLAP MARGIN: Top 0, Left 15, Bottom -15, Right 15 (Full length with
        // padding)
        gbc.insets = new Insets(0, 15, -15, 15);
        bannerPanel.add(searchBarContainer, gbc);

        add(bannerPanel, BorderLayout.NORTH);

        // =================================================================================
        // 3. RESULTS SECTION
        // =================================================================================
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(new Color(245, 247, 251)); // Content BG
        // TOP PADDING: 35 (Adjusted)
        resultsPanel.setBorder(new EmptyBorder(35, 20, 20, 20)); // Top padding for the overlap space

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(245, 247, 251));

        if (lang != null && lang.isArabic())
            scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        add(scrollPane, BorderLayout.CENTER);
    }

    // Helper to create the thin vertical lines
    private JComponent createVerticalSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 40));
        sep.setForeground(new Color(230, 230, 230));
        return sep;
    }

    private JButton createFlatInputButton(String placeholder) {
        JButton btn = new JButton(placeholder);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(Color.GRAY);
        btn.setBackground(Color.WHITE);
        btn.setBorder(new EmptyBorder(0, 15, 0, 10)); // Padding
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(2));
                g2.setColor(new Color(200, 200, 200)); // Light gray ring
                g2.drawOval(x, y + 2, 14, 14); // Circle
            }

            @Override
            public int getIconWidth() {
                return 20;
            }

            @Override
            public int getIconHeight() {
                return 20;
            }
        });

        return btn;
    }

    private void searchTrips() {
        String originSearch = selectedOrigin;
        String destSearch = selectedDestination;
        String dateStr = dateField.getText();

        try {
            LocalDate searchDate = null;
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                searchDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            }

            // Search
            System.out.println(
                    "Searching for: Origin='" + originSearch + "', Dest='" + destSearch + "', Date=" + searchDate);
            List<Trip> results = tripService.searchTrips(originSearch, destSearch, searchDate);
            updateResults(results);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, lang.get("search.no_results"), lang.get("search.results"),
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, lang.get("search.error") + ": " + e.getMessage(),
                    lang.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateResults(List<Trip> trips) {
        resultsPanel.removeAll();

        // Fetch my bookings to check status
        Map<Integer, Booking> myBookingsMap = new HashMap<>();
        if ("PASSENGER".equals(currentUser.getRole())) {
            try {
                List<Booking> myBookings = bookingService.getPassengerBookings(currentUser.getUserId());
                if (myBookings != null) {
                    for (Booking b : myBookings) {
                        myBookingsMap.put(b.getTripId(), b);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Trip trip : trips) {
            try {
                User driver = userService.getUserById(trip.getDriverId());
                Booking myBooking = myBookingsMap.get(trip.getTripId());
                resultsPanel.add(createTripCard(trip, driver, myBooking));
                resultsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel createTripCard(Trip trip, User driver, Booking myBooking) {
        JPanel card = new JPanel(new BorderLayout(15, 0));

        // Enhanced Card Border with Smooth Shadow and Roundness
        card.setBorder(new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(x + 3, y + 3, width - 6, height - 6, 25, 25);

                // Background (if opaque is false, we might need to paint it here to ensure
                // shape)
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y, width - 4, height - 4, 25, 25);

                // Subtle Border
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(x, y, width - 4, height - 4, 25, 25);

                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(15, 20, 15, 20);
            }
        });

        card.setOpaque(false); // Let custom border paint background
        card.setMaximumSize(new Dimension(950, 160));
        card.setPreferredSize(new Dimension(950, 160));

        if (lang != null && lang.isArabic()) {
            card.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        // =================================================================================
        // PRICE & ACTION PANEL (The "Side" Panel)
        // =================================================================================
        JPanel actionSidePanel = new JPanel();
        actionSidePanel.setLayout(new BoxLayout(actionSidePanel, BoxLayout.Y_AXIS));
        actionSidePanel.setOpaque(false);
        actionSidePanel.setPreferredSize(new Dimension(160, 130));

        JLabel priceLabel = new JLabel(trip.getPrice() + " DZD");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        priceLabel.setForeground(new Color(0, 140, 186));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Report Icon Button (Replaces Flag)
        JButton reportBtn = new JButton();
        reportBtn.setIcon(getScaledIcon("icons/report.png", 20, 20)); // report.png
        reportBtn.setContentAreaFilled(false);
        reportBtn.setBorderPainted(false);
        reportBtn.setFocusPainted(false);
        reportBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        reportBtn.setToolTipText(lang != null && lang.isArabic() ? "إبلاغ" : "Signaler");
        reportBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        reportBtn.addActionListener(e -> {
            if (driver != null) {
                showReportDialog(driver);
            }
        });

        Component actionComponent;
        if ("PASSENGER".equals(currentUser.getRole())) {
            if (myBooking != null) {
                String status = myBooking.getBookingStatus();
                JLabel statusLbl = new JLabel();
                statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                statusLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                if ("ACCEPTED".equals(status)) {
                    statusLbl.setText((lang != null && lang.isArabic()) ? "مقبول" : "Accepted");
                    statusLbl.setForeground(new Color(76, 175, 80));
                } else if ("REJECTED".equals(status)) {
                    statusLbl.setText((lang != null && lang.isArabic()) ? "مرفوض" : "Refused");
                    statusLbl.setForeground(Color.RED);
                } else {
                    statusLbl.setText((lang != null && lang.isArabic()) ? "قيد الانتظار" : "Pending");
                    statusLbl.setForeground(Color.ORANGE);
                }
                actionComponent = statusLbl;
            } else {
                if ("FULL".equals(trip.getStatus()) || trip.getSeatsAvailable() <= 0) {
                    JLabel fullLbl = new JLabel((lang != null && lang.isArabic()) ? "مكتمل" : "COMPLET");
                    fullLbl.setForeground(Color.RED);
                    fullLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    fullLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                    actionComponent = fullLbl;
                } else {
                    JButton bookBtn = new JButton((lang != null && lang.isArabic()) ? "حجز" : "Réserver");
                    bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    bookBtn.setForeground(Color.WHITE);
                    bookBtn.setBackground(new Color(0, 140, 186));
                    bookBtn.setFocusPainted(false);
                    bookBtn.setBorderPainted(false);
                    bookBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    bookBtn.setMaximumSize(new Dimension(140, 36));
                    bookBtn.addActionListener(e -> showBookingDialog(trip));
                    bookBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

                    // Rounded Button UI
                    bookBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                        @Override
                        public void paint(Graphics g, JComponent c) {
                            Graphics2D g2 = (Graphics2D) g;
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(c.getBackground());
                            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                            super.paint(g2, c);
                        }
                    });

                    actionComponent = bookBtn;
                }
            }
        } else {
            actionComponent = new JLabel("");
        }

        actionSidePanel.add(Box.createVerticalGlue());
        actionSidePanel.add(priceLabel);
        actionSidePanel.add(Box.createVerticalStrut(8));
        actionSidePanel.add(reportBtn); // New Icon Button
        actionSidePanel.add(Box.createVerticalStrut(15));
        actionSidePanel.add(actionComponent);
        actionSidePanel.add(Box.createVerticalGlue());

        // =================================================================================
        // AVATAR PANEL
        // =================================================================================
        JPanel avatarContainer = new JPanel(new GridBagLayout());
        avatarContainer.setOpaque(false);

        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(75, 75));
        avatarLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (driver != null && driver.getAvatarPath() != null && new java.io.File(driver.getAvatarPath()).exists()) {
            ImageIcon icon = new ImageIcon(driver.getAvatarPath());
            Image scaled = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(scaled) {
                @Override
                public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, 75, 75));
                    g2.drawImage(getImage(), 0, 0, c);
                    g2.dispose();
                }
            });
        } else {
            String letter = (driver != null) ? driver.getName().substring(0, 1) : "?";
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(75, 75,
                    java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.fillOval(0, 0, 75, 75);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(letter, (75 - fm.stringWidth(letter)) / 2, (75 + fm.getAscent()) / 2 - 5);
            g2.dispose();
            avatarLabel.setIcon(new ImageIcon(img));
        }

        avatarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (driver != null) {
                    Window win = SwingUtilities.getWindowAncestor(SearchTripPanel.this);
                    view.ProfileDialog profileDialog = new view.ProfileDialog(win, driver, true);
                    profileDialog.setReporterId(currentUser.getUserId());
                    profileDialog.setVisible(true);
                }
            }
        });
        avatarContainer.add(avatarLabel);

        if (lang != null && lang.isArabic()) {
            card.add(actionSidePanel, BorderLayout.WEST);
            card.add(avatarContainer, BorderLayout.EAST);
        } else {
            card.add(avatarContainer, BorderLayout.WEST);
            card.add(actionSidePanel, BorderLayout.EAST);
        }

        // =================================================================================
        // CENTER (Trip Info)
        // =================================================================================
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false); // Transparent to show custom border BG
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));
        if (lang != null && lang.isArabic())
            centerPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Row 1: Name + Badge + Stats
        boolean isArabic = (lang != null && lang.isArabic());
        JPanel row1 = new JPanel(new FlowLayout(isArabic ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 0));
        row1.setOpaque(false);
        if (isArabic)
            row1.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel nameLbl = new JLabel((driver != null) ? driver.getName() : "Unknown");
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLbl.setForeground(new Color(33, 33, 33));
        row1.add(nameLbl);

        if (driver != null && "VERIFIED".equalsIgnoreCase(driver.getVerificationStatus())) {
            // Verified Icon + Text
            JLabel badgeLbl = new JLabel((lang != null && lang.isArabic()) ? "موثق" : "Vérifié");
            badgeLbl.setIcon(getScaledIcon("icons/verified.png", 16, 16));
            badgeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            badgeLbl.setBackground(new Color(232, 245, 233));
            badgeLbl.setOpaque(true);
            badgeLbl.setForeground(new Color(46, 204, 113));
            badgeLbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 230, 201), 1),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)));
            row1.add(badgeLbl);
        }

        String ratingStr = (driver != null) ? String.format("%.1f", driver.getAverageRating()) : "0.0";
        JLabel statsLbl = new JLabel("⭐ " + ratingStr + "  •  "
                + ((driver != null) ? driver.getTotalTrips() : "0")
                + ((lang != null && lang.isArabic()) ? " رحلات" : " trajets"));
        statsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statsLbl.setForeground(Color.GRAY);
        row1.add(Box.createHorizontalStrut(10));
        row1.add(statsLbl);

        // Row 2: Route with Icons (point-depaire -> point-end)
        JPanel row2 = new JPanel(new FlowLayout(isArabic ? FlowLayout.RIGHT : FlowLayout.LEFT, 5, 5));
        row2.setOpaque(false);
        if (isArabic)
            row2.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel iconOrigin = new JLabel(getScaledIcon("icons/point-depaire.png", 18, 18));
        JLabel lblOrigin = new JLabel(trip.getOrigin());
        lblOrigin.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel arrow = new JLabel(" ➝ ");
        arrow.setForeground(Color.GRAY);

        JLabel iconDest = new JLabel(getScaledIcon("icons/point-end.png", 18, 18));
        JLabel lblDest = new JLabel(trip.getDestination());
        lblDest.setFont(new Font("Segoe UI", Font.BOLD, 15));

        if (isArabic) {
            row2.add(lblDest);
            row2.add(iconDest);
            row2.add(arrow);
            row2.add(lblOrigin);
            row2.add(iconOrigin);
        } else {
            row2.add(iconOrigin);
            row2.add(lblOrigin);
            row2.add(arrow);
            row2.add(iconDest);
            row2.add(lblDest);
        }

        // Row 3: Date & Time with Icons
        JPanel row3 = new JPanel(new FlowLayout(isArabic ? FlowLayout.RIGHT : FlowLayout.LEFT, 15, 5));
        row3.setOpaque(false);
        if (isArabic)
            row3.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        String dayMonth = trip.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        String timeStr = trip.getTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        JLabel dateIcon = new JLabel(getScaledIcon("icons/date.png", 16, 16));
        JLabel dateLbl = new JLabel(dayMonth);
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLbl.setForeground(Color.GRAY);

        JLabel timeIcon = new JLabel(getScaledIcon("icons/time.png", 16, 16));
        JLabel timeLbl = new JLabel(timeStr);
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        timeLbl.setForeground(Color.GRAY);

        if (isArabic) {
            row3.add(dateLbl);
            row3.add(dateIcon);
            row3.add(timeLbl);
            row3.add(timeIcon);
        } else {
            row3.add(dateIcon);
            row3.add(dateLbl);
            row3.add(timeIcon);
            row3.add(timeLbl);
        }

        // Row 4: Seats with Icon
        JPanel row4 = new JPanel(new FlowLayout(isArabic ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 5));
        row4.setOpaque(false);
        if (isArabic)
            row4.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel placesIcon = new JLabel(getScaledIcon("icons/place.png", 16, 16));
        String placesText = (lang != null && lang.isArabic()) ? trip.getSeatsAvailable() + " مقاعد"
                : trip.getSeatsAvailable() + " places disp.";
        JLabel seatsLbl = new JLabel(placesText);
        seatsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        seatsLbl.setForeground(Color.DARK_GRAY);

        if (isArabic) {
            row4.add(seatsLbl);
            row4.add(placesIcon);
        } else {
            row4.add(placesIcon);
            row4.add(seatsLbl);
        }

        centerPanel.add(row1);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(row2);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(row3);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(row4);

        card.add(centerPanel, BorderLayout.CENTER);
        return card;
    }

    // Helper Method to Load and Scale Icons Safely
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

    private void showBookingDialog(Trip trip) {
        Window win = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(win, lang != null ? lang.get("search.dialog.title") : "Réserver",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        if (lang != null && lang.isArabic())
            dialog.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel content = new JPanel(new GridLayout(3, 1, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        if (lang != null && lang.isArabic())
            content.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        String msg = MessageFormat.format(lang != null ? lang.get("search.dialog.seats") : "Places ({0})",
                trip.getSeatsAvailable());
        JLabel label = new JLabel(msg);
        if (lang != null && lang.isArabic())
            label.setHorizontalAlignment(SwingConstants.RIGHT);

        content.add(label);
        JSpinner seatSpinner = new JSpinner(new SpinnerNumberModel(1, 1, trip.getSeatsAvailable(), 1));
        if (lang != null && lang.isArabic())
            seatSpinner.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        content.add(seatSpinner);

        JButton confirmBtn = new JButton(lang != null ? lang.get("search.dialog.confirm") : "Confirmer");
        confirmBtn.setBackground(new Color(76, 175, 80));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.addActionListener(e -> {
            int seats = (int) seatSpinner.getValue();
            bookTrip(trip, seats);
            dialog.dispose();
        });

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(confirmBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void bookTrip(Trip trip, int seats) {
        try {
            bookingService.bookTrip(trip.getTripId(), currentUser.getUserId(), seats);
            JOptionPane.showMessageDialog(this, lang != null ? lang.get("search.success") : "Réservation réussie!");
            searchTrips();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    (lang != null ? lang.get("search.error") : "Erreur") + ": " + e.getMessage(),
                    lang != null ? lang.get("dialog.error") : "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReportDialog(User driver) {
        Window win = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(win, "Signaler un conducteur",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
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
