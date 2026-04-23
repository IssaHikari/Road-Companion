package view;

import model.User;
import dao.UserDAO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.sql.SQLException;

public class ProfileDialog extends JDialog {

    private final User user;
    private final UserDAO userDAO;

    // UI Components to update
    private JPanel headerPanel;
    private CircularAvatar avatarPanel;
    private boolean isViewOnly = false;
    private JPanel detailsPanel; // Make this a field to access it

    // Colors
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color ACCENT_COLOR = new Color(30, 136, 229);
    private static final Color BG_COLOR = new Color(250, 250, 250);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);

    public ProfileDialog(Window owner, User user) {
        this(owner, user, false);
    }

    public ProfileDialog(Window owner, User user, boolean isViewOnly) {
        super(owner, isViewOnly ? "Profil Conducteur" : "Mon Profil", ModalityType.APPLICATION_MODAL);
        this.user = user;
        this.isViewOnly = isViewOnly;
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Wider and Shorter Dimensions
        // Wider and Shorter Dimensions
        int dialogWidth = 600;
        int dialogHeight = 600; // Reduced height to be compact but fit content
        setSize(dialogWidth, dialogHeight);
        setLocationRelativeTo(getOwner());

        // Main Container with Rounded Corner & Shadow
        JPanel mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);

                // Main Background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);

                // Blue Border
                g2.setColor(PRIMARY_COLOR);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 12, getHeight() - 12, 30, 30);

                g2.dispose();
            }
        };
        mainContainer.setLayout(null);
        mainContainer.setOpaque(false);
        setContentPane(mainContainer);

        // --- 1. Header (Banner) ---
        headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Clip top rounded corners
                g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 30, 30, 30));

                if (user.getBackgroundPath() != null && !user.getBackgroundPath().isEmpty()) {
                    ImageIcon img = new ImageIcon(user.getBackgroundPath());
                    g2.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Gradient Default
                    g2.setPaint(new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), ACCENT_COLOR));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
            }
        };
        // Add padding: 10px from top, left, right
        int headerPadding = 10;
        headerPanel.setBounds(headerPadding, headerPadding, dialogWidth - 10 - (headerPadding * 2), 140);
        headerPanel.setLayout(null);

        // Close Button
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(dialogWidth - 50, 10, 30, 30);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(0, 0, 0, 50));
        closeBtn.setBorder(null);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        headerPanel.add(closeBtn);

        // Edit Banner Button
        if (!isViewOnly) {
            JPanel bgCameraBtn = createCameraButton();
            bgCameraBtn.setBounds(10, 10, 35, 35);
            bgCameraBtn.setToolTipText("Changer la bannière");
            bgCameraBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    chooseImage(true);
                }
            });
            headerPanel.add(bgCameraBtn);
        }
        mainContainer.add(headerPanel);

        // --- 2. Circular Avatar (HIDDEN) ---
        avatarPanel = new CircularAvatar(user);
        int avatarSize = 110;
        int centerX = (dialogWidth - 10) / 2;
        avatarPanel.setBounds(centerX - (avatarSize / 2), 85, avatarSize, avatarSize);

        if (!isViewOnly) {
            JPanel avatarCameraBtn = createCameraButton();
            avatarCameraBtn.setBounds((avatarSize / 2) - 15, (avatarSize / 2) - 15, 30, 30);
            avatarCameraBtn.setToolTipText("Changer l'avatar");
            avatarCameraBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    chooseImage(false);
                }
            });
            avatarPanel.setLayout(null);
            avatarPanel.add(avatarCameraBtn);

            avatarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            avatarPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    chooseImage(false);
                }
            });
        }
        // Show the avatar panel
        avatarPanel.setVisible(true);
        mainContainer.add(avatarPanel);

        // CRITICAL: Ensure Avatar is on TOP of Header (Z-Order 0 = Top)
        mainContainer.setComponentZOrder(avatarPanel, 0);

        // --- 3. User Info ---
        JLabel nameLabel = new JLabel(user.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setBounds(20, 200, dialogWidth - 50, 30);
        mainContainer.add(nameLabel);

        String role = user.getRole() != null ? user.getRole() : "User";
        JLabel roleLabel = new JLabel(role, SwingConstants.CENTER);
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabel.setForeground(PRIMARY_COLOR);
        roleLabel.setBounds(20, 230, dialogWidth - 50, 20);
        mainContainer.add(roleLabel);

        // --- 4. Details Grid Panel (Initially created) ---
        detailsPanel = new JPanel(new GridLayout(0, 2, 20, 15));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Increased height from 160 to 230 to prevent text clipping
        detailsPanel.setBounds(30, 290, dialogWidth - 60, 230);

        detailsPanel.add(createModernDetailItem("Email", user.getEmail(), "icons/email.png"));
        detailsPanel.add(createModernDetailItem("Téléphone", user.getPhoneNumber(), "icons/telephone.png"));
        detailsPanel.add(createModernDetailItem("Sexe", user.getGender(), "icons/avatars-masculins-et-feminins.png"));

        // --- LEGENDARY RATING SYSTEM (Visual Stars) ---
        if ("DRIVER".equals(role)) {
            JPanel ratingContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            ratingContainer.setOpaque(false);
            ratingContainer.setBounds(20, 250, dialogWidth - 50, 30);

            // Star Visuals
            StarRatingComponent stars = new StarRatingComponent(user.getAverageRating());
            ratingContainer.add(stars);

            // Text Score
            JLabel scoreLbl = new JLabel(String.format("%.1f", user.getAverageRating()));
            scoreLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            scoreLbl.setForeground(new Color(255, 193, 7)); // Gold
            ratingContainer.add(scoreLbl);

            JLabel countLbl = new JLabel("(" + user.getTotalTrips() + " avis)");
            countLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            countLbl.setForeground(Color.GRAY);
            ratingContainer.add(countLbl);

            mainContainer.add(ratingContainer);

            // Adjust details panel position if driver (same bounds)
            detailsPanel.setBounds(30, 290, dialogWidth - 60, 230);
        }

        if ("DRIVER".equals(user.getRole())) {
            detailsPanel.add(createModernDetailItem("Date de naissance",
                    user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "N/A", "icons/juin.png"));
            detailsPanel.add(createModernDetailItem("Modèle", user.getVehicleModel(), "icons/car_2523139.png"));
            detailsPanel.add(createModernDetailItem("Matricule", user.getVehiclePlate(), "icons/matricule.png"));
            detailsPanel
                    .add(createModernDetailItem("Couleur", user.getVehicleColor(), "icons/watercolors_3436187.png"));

            // Verification Status
            String status = user.getVerificationStatus();
            if (status == null)
                status = "UNVERIFIED";
            String statusText = "Non Vérifié";
            Color statusColor = Color.RED;

            if ("VERIFIED".equals(status)) {
                statusText = "Vérifié";
                statusColor = new Color(76, 175, 80);
            } else if ("PENDING".equals(status)) {
                statusText = "En Attente";
                statusColor = Color.ORANGE;
            }

            // Using statut.png icon for status
            JPanel statusPanel = createModernDetailItem("Statut", statusText, "icons/statut.png");
            ((JLabel) statusPanel.getComponent(1)).setForeground(statusColor);
            detailsPanel.add(statusPanel);
        } else {
            detailsPanel.add(createModernDetailItem("Date de naissance",
                    user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "N/A", "icons/juin.png"));
        }
        mainContainer.add(detailsPanel);

        // --- Extra Actions for Driver (Edit Info & Upload License) ---
        if (!isViewOnly && "DRIVER".equals(user.getRole())) {
            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0)); // Spacing between buttons
            actionsPanel.setOpaque(false);
            // Moved to 530 to follow the Expanded details panel
            actionsPanel.setBounds(30, 530, dialogWidth - 60, 50);

            JButton editInfoBtn = createStyledActionButton("Modifier Véhicule", new Color(33, 150, 243));
            editInfoBtn.addActionListener(e -> showEditVehicleDialog());

            JButton uploadLicenseBtn = createStyledActionButton("Permis de Conduire", new Color(255, 193, 7));
            uploadLicenseBtn.setForeground(Color.BLACK); // Dark text for yellow button
            uploadLicenseBtn.addActionListener(e -> uploadLicense());

            actionsPanel.add(editInfoBtn);
            actionsPanel.add(uploadLicenseBtn);

            mainContainer.add(actionsPanel);
        }

        // --- 5. Report Button (Wider & Distinct) ---
        if (isViewOnly) {
            JButton reportBtn = new JButton("🚨 SIGNALER CE PROFIL");
            // width 220, height 40, centered horizontally
            int btnWidth = 220;
            int btnHeight = 40;
            reportBtn.setBounds((dialogWidth - btnWidth) / 2, 540, btnWidth, btnHeight);

            reportBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            reportBtn.setForeground(Color.WHITE);
            reportBtn.setBackground(new Color(220, 53, 69)); // Bootstrap Danger Red
            reportBtn.setFocusPainted(false);
            reportBtn.setBorderPainted(false);
            reportBtn.setContentAreaFilled(false);
            reportBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Custom Rounded UI (Pill Shape)
            reportBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Shadow
                    g2.setColor(new Color(0, 0, 0, 40));
                    g2.fillRoundRect(3, 3, c.getWidth() - 6, c.getHeight() - 6, c.getHeight(), c.getHeight());

                    // Button Fill
                    if (reportBtn.getModel().isPressed()) {
                        g2.setColor(c.getBackground().darker());
                    } else {
                        g2.setColor(c.getBackground());
                    }
                    // Full Height Radius for Pill Shape
                    g2.fillRoundRect(0, 0, c.getWidth() - 4, c.getHeight() - 4, c.getHeight(), c.getHeight());

                    super.paint(g2, c);
                    g2.dispose();
                }
            });

            reportBtn.addActionListener(e -> showReportDialog());
            mainContainer.add(reportBtn);
        }
    }

    private JPanel createModernDetailItem(String title, String value, String iconPath) {
        JPanel p = new JPanel(new BorderLayout(5, 0)); // Add horizontal gap
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240))); // Bottom separator only

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(TEXT_SECONDARY);

        // Add Icon if provided
        if (iconPath != null && !iconPath.isEmpty()) {
            File iconFile = new File(iconPath);
            if (iconFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(iconPath);
                Image img = originalIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH); // Scale to 16x16
                t.setIcon(new ImageIcon(img));
                t.setIconTextGap(8); // Space between icon and text
            }
        }

        JLabel v = new JLabel(value != null ? value : "-");
        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
        v.setForeground(TEXT_PRIMARY);
        // Add left padding to value to align nicely with title text (approx icon width
        // + gap)
        v.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));

        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);

        return p;
    }

    private int reporterId = 0;

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    private void showReportDialog() {
        if (reporterId == 0) {
            JOptionPane.showMessageDialog(this, "Erreur: Impossible d'identifier l'utilisateur signalant.");
            return;
        }

        JTextArea textArea = new JTextArea(5, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Motif du signalement :",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String reason = textArea.getText().trim();
            if (!reason.isEmpty()) {
                boolean success = new service.ReportService().submitReport(reporterId, user.getUserId(), reason);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Signalement envoyé aux administrateurs.", "Merci",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'envoi du signalement.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private JPanel createCameraButton() {
        JPanel btn = new JPanel() {
            private boolean hover = false;
            {
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? new Color(0, 0, 0, 180) : new Color(0, 0, 0, 100)); // Dark overlay
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "📷";
                g2.drawString(icon, (getWidth() - fm.stringWidth(icon)) / 2,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        return btn;
    }

    private void showEditVehicleDialog() {
        JTextField modelField = new JTextField(user.getVehicleModel());
        JTextField plateField = new JTextField(user.getVehiclePlate());
        JTextField colorField = new JTextField(user.getVehicleColor());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Modèle du véhicule:"));
        panel.add(modelField);
        panel.add(new JLabel("Matricule:"));
        panel.add(plateField);
        panel.add(new JLabel("Couleur:"));
        panel.add(colorField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Modifier les informations du véhicule",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newModel = modelField.getText().trim();
            String newPlate = plateField.getText().trim();
            String newColor = colorField.getText().trim();

            user.setVehicleModel(newModel);
            user.setVehiclePlate(newPlate);
            user.setVehicleColor(newColor);

            try {
                if (userDAO.updateDriverInfo(user)) {
                    JOptionPane.showMessageDialog(this, "Informations mises à jour !");
                    dispose();
                    new ProfileDialog(getOwner(), user, false).setVisible(true); // Re-open to refresh
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
            }
        }
    }

    private void uploadLicense() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choisir une image du permis");
        chooser.setFileFilter(new FileNameExtensionFilter("Images (JPG, PNG)", "jpg", "png", "jpeg"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();

            user.setLicensePath(path);
            user.setVerificationStatus("PENDING"); // Reset to Pending on new upload

            try {
                if (userDAO.updateDriverInfo(user)) {
                    JOptionPane.showMessageDialog(this, "Permis envoyé ! Votre statut est maintenant 'En Attente'.");
                    repaint();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private JButton createStyledActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 40));

        // Custom Rounded UI with Shadow
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(2, 2, c.getWidth() - 4, c.getHeight() - 4, 20, 20);

                // Button Body
                if (btn.getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (btn.getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, c.getWidth() - 4, c.getHeight() - 4, 20, 20);

                super.paint(g2, c);
                g2.dispose();
            }
        });
        return btn;
    }

    private void chooseImage(boolean isBackground) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choisir une image");
        chooser.setFileFilter(new FileNameExtensionFilter("Images (JPG, PNG)", "jpg", "png", "jpeg"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            try {
                if (isBackground)
                    user.setBackgroundPath(path);
                else
                    user.setAvatarPath(path);

                if (userDAO.updateUserImages(user.getUserId(), user.getAvatarPath(), user.getBackgroundPath())) {
                    repaint(); // Update UI
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Modern Circular Avatar with White Border
    private static class CircularAvatar extends JPanel {
        private final User user;

        public CircularAvatar(User user) {
            this.user = user;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight());
            int padding = 6; // White border width

            // 1. Drop Shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillOval(2, 2, size - 4, size - 4);

            // 2. White Border Background
            g2.setColor(Color.WHITE);
            g2.fillOval(0, 0, size - 4, size - 4);

            // 3. Avatar Image
            int imgSize = size - (padding * 2) - 4;
            int offset = padding;

            if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()
                    && new File(user.getAvatarPath()).exists()) {
                ImageIcon img = new ImageIcon(user.getAvatarPath());
                g2.setClip(new java.awt.geom.Ellipse2D.Float(offset, offset, imgSize, imgSize));
                g2.drawImage(img.getImage(), offset, offset, imgSize, imgSize, null);
            } else {
                g2.setColor(PRIMARY_COLOR);
                g2.fillOval(offset, offset, imgSize, imgSize);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 40));
                String initial = user.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initial, offset + (imgSize - fm.stringWidth(initial)) / 2,
                        offset + (imgSize - fm.getHeight()) / 2 + fm.getAscent() - 5);
            }
            g2.dispose();
        }
    }

    // --- NEW LEGENDARY STAR RATING COMPONENT ---
    private static class StarRatingComponent extends JComponent {
        private final double rating; // 0.0 to 5.0
        private final int starSize = 16;
        private final int gap = 2;

        public StarRatingComponent(double rating) {
            this.rating = rating;
            setPreferredSize(new Dimension(5 * (starSize + gap), starSize));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < 5; i++) {
                int x = i * (starSize + gap);
                double fillPercent = Math.max(0, Math.min(1, rating - i));

                // Draw Background (Empty) Star
                drawStar(g2, x, 0, starSize, Color.LIGHT_GRAY);

                // Draw Filled Star (Clipped)
                if (fillPercent > 0) {
                    g2.setClip(new Rectangle(x, 0, (int) (starSize * fillPercent), starSize));
                    drawStar(g2, x, 0, starSize, new Color(255, 193, 7)); // Gold
                    g2.setClip(null); // Reset clip
                }
            }
            g2.dispose();
        }

        private void drawStar(Graphics2D g2, int x, int y, int size, Color color) {
            GeneralPath p = new GeneralPath();
            double cx = x + size / 2.0;
            double cy = y + size / 2.0;
            double outerRadius = size / 2.0;
            double innerRadius = size / 5.0;

            for (int i = 0; i < 5; i++) {
                double outerAngle = Math.toRadians(-90 + i * 72);
                double innerAngle = Math.toRadians(-90 + i * 72 + 36);

                double ox = cx + Math.cos(outerAngle) * outerRadius;
                double oy = cy + Math.sin(outerAngle) * outerRadius;
                double ix = cx + Math.cos(innerAngle) * innerRadius;
                double iy = cy + Math.sin(innerAngle) * innerRadius;

                if (i == 0)
                    p.moveTo(ox, oy);
                else
                    p.lineTo(ox, oy);
                p.lineTo(ix, iy);
            }
            p.closePath();
            g2.setColor(color);
            g2.fill(p);
        }
    }
}
