package view;

import model.User;
import service.UserService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class RegistrationFrame extends JFrame {

    private UserService userService;
    private JPanel cards;
    private CardLayout cardLayout;

    // Brand Colors
    private final Color BRAND_COLOR = new Color(63, 81, 181);
    private final Color BG_COLOR = new Color(240, 242, 245); // Light grey background like Facebook/LinkedIn

    // Step 1: Basic Info
    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> genderComboBox;
    private JRadioButton driverRadio, passengerRadio;
    private ButtonGroup roleGroup;
    private JTextField dobField;

    // Step 2: Driver Info
    private JTextField carModelField, carPlateField;

    private User tempUser;

    private Image backgroundImage;

    public RegistrationFrame() {
        super("AissaGo - Inscription");

        userService = new UserService();
        tempUser = new User();
        initializeUI();

        // Load image in a background thread to prevent UI lag ("Heaviness")
        new Thread(() -> {
            ImageIcon icon = new ImageIcon("images/bagraoung-Register.jpg");
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                backgroundImage = icon.getImage();
                // Repaint once loaded
                SwingUtilities.invokeLater(this::repaint);
            }
        }).start();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Large interface as requested
        setLocationRelativeTo(null);

        // Main Background Panel with Image
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            private Image cachedBackground = null;
            private int lastWidth = -1;
            private int lastHeight = -1;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Only resize image if size changed or not cached
                if (backgroundImage != null
                        && (cachedBackground == null || lastWidth != getWidth() || lastHeight != getHeight())) {
                    cachedBackground = backgroundImage.getScaledInstance(
                            getWidth(), getHeight(), Image.SCALE_SMOOTH);
                    lastWidth = getWidth();
                    lastHeight = getHeight();
                }

                if (cachedBackground != null) {
                    g.drawImage(cachedBackground, 0, 0, this);
                } else {
                    // Fallback sleek gradient while loading
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setPaint(new GradientPaint(0, 0, BG_COLOR, getWidth(), getHeight(), new Color(220, 225, 230)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // Create the "Card" Container with Rounded Corners & High Transparency
        JPanel cardContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // More transparent white (Alpha 180)
                g2.setColor(new Color(255, 255, 255, 180));

                // Rounded Rectangle
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
            }
        };
        // Wider size and shorter height (Wide not tall)
        cardContainer.setPreferredSize(new Dimension(900, 580));
        cardContainer.setOpaque(false);
        cardContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header inside the Card (Transparent, No Blue)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Créer un compte", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        cardContainer.add(headerPanel, BorderLayout.NORTH);

        // Content Cards
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setOpaque(false);

        cards.add(createStep1Panel(), "STEP1");
        cards.add(createStep2Panel(), "STEP2");

        cardContainer.add(cards, BorderLayout.CENTER);

        // Add Card Container to Main Panel (Centered)
        mainPanel.add(cardContainer);
        setContentPane(mainPanel);
    }

    private JComponent createStep1Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 5, 15); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // --- Row 1 ---
        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createFormLabel("Nom complet"), gbc);

        gbc.gridy = 1;
        panel.add(nameField = createStyledTextField(), gbc);

        // Email
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(createFormLabel("Email"), gbc);

        gbc.gridy = 1;
        panel.add(emailField = createStyledTextField(), gbc);

        // --- Row 2 ---
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 15, 5, 15); // More top margin for new row
        panel.add(createFormLabel("Téléphone"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 15, 5, 15);
        panel.add(phoneField = createStyledTextField(), gbc);

        // DOB
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 15, 5, 15);
        panel.add(createFormLabel("Date de naissance (AAAA-MM-JJ)"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 15, 5, 15);
        panel.add(dobField = createStyledTextField(), gbc);

        // --- Row 3 ---
        // Gender
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 15, 5, 15);
        panel.add(createFormLabel("Sexe"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(5, 15, 5, 15);
        genderComboBox = new JComboBox<>(new String[] { "Homme", "Femme" });
        styleComboBox(genderComboBox);
        panel.add(genderComboBox, gbc);

        // Role
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 15, 5, 15);
        panel.add(createFormLabel("Je suis :"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(5, 15, 5, 15);
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolePanel.setOpaque(false);

        driverRadio = new JRadioButton("Chauffeur");
        passengerRadio = new JRadioButton("Passager", true);
        passengerRadio.setOpaque(false);
        driverRadio.setOpaque(false);
        passengerRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        driverRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        roleGroup = new ButtonGroup();
        roleGroup.add(driverRadio);
        roleGroup.add(passengerRadio);
        rolePanel.add(driverRadio);
        rolePanel.add(Box.createHorizontalStrut(20));
        rolePanel.add(passengerRadio);
        panel.add(rolePanel, gbc);

        // --- Row 4 ---
        // Password
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.insets = new Insets(15, 15, 5, 15);
        panel.add(createFormLabel("Mot de passe"), gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(5, 15, 5, 15);
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        panel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.insets = new Insets(15, 15, 5, 15);
        panel.add(createFormLabel("Confirmer mot de passe"), gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(5, 15, 5, 15);
        confirmPasswordField = new JPasswordField();
        styleTextField(confirmPasswordField);
        panel.add(confirmPasswordField, gbc);

        // --- Buttons ---
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2; // Span across both columns
        gbc.insets = new Insets(30, 15, 10, 15);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);

        JButton backBtn = new JButton("Retour à la connexion");
        styleLinkButton(backBtn);
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        JButton nextBtn = new JButton("SUIVANT");
        styleButton(nextBtn);
        nextBtn.addActionListener(this::handleStep1Actions);

        btnPanel.add(backBtn);
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(nextBtn);

        panel.add(btnPanel, gbc);

        return panel;
    }

    private JComponent createStep2Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // Title Row
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Informations Véhicule");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(BRAND_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, gbc);

        // Fields Row
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // Model
        gbc.gridx = 0;
        panel.add(createFormLabel("Modèle de voiture"), gbc);
        gbc.gridy = 2;
        panel.add(carModelField = createStyledTextField(), gbc);

        // Plate
        gbc.gridy = 1;
        gbc.gridx = 1;
        panel.add(createFormLabel("Matricule"), gbc);
        gbc.gridy = 2;
        panel.add(carPlateField = createStyledTextField(), gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(40, 15, 10, 15);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        JButton backBtn = new JButton("Précédent");
        styleLinkButton(backBtn);
        backBtn.addActionListener(e -> cardLayout.show(cards, "STEP1"));

        JButton finishBtn = new JButton("TERMINER");
        styleButton(finishBtn);
        finishBtn.addActionListener(this::handleFinalRegistration);

        btnPanel.add(backBtn);
        btnPanel.add(finishBtn);
        panel.add(btnPanel, gbc);

        return panel;
    }

    // --- Helpers ---

    private JLabel createFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(80, 80, 80));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createStyledTextField() {
        JTextField f = new JTextField();
        styleTextField(f);
        return f;
    }

    // --- Styled Components Helper Methods ---

    // Vivid Light Green for primary actions
    private final Color PRIMARY_GREEN = new Color(0, 200, 83);
    private final Color HOVER_GREEN = new Color(0, 230, 118);

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Smaller height as requested (was 50, now 35)
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setPreferredSize(new Dimension(300, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBackground(Color.WHITE);
        field.setCaretColor(PRIMARY_GREEN);

        // Custom Rounded Border
        field.setBorder(new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(field.isFocusOwner() ? PRIMARY_GREEN : new Color(200, 200, 200));
                g2.drawRoundRect(x, y, width - 1, height - 1, 15, 15);
                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(5, 10, 5, 10);
            }
        });

        // Add Focus Listener for repaint to show color change
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.repaint();
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                field.repaint();
            }
        });
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBackground(Color.WHITE);
        // Constrain width so it's not huge
        box.setMaximumSize(new Dimension(200, 35));
        box.setPreferredSize(new Dimension(200, 35));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Remove default border and add custom
        if (box.getEditor().getEditorComponent() instanceof JComponent) {
            ((JComponent) box.getEditor().getEditorComponent()).setBorder(null);
        }
        box.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY_GREEN);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Remove default look
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);

        // Dimensions
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setMaximumSize(new Dimension(150, 40));

        // Custom Paint for Rounded Green Button
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 2, c.getWidth() - 2, c.getHeight() - 2, 20, 20);

                // Draw Button Background
                if (btn.getModel().isPressed()) {
                    g2.setColor(PRIMARY_GREEN.darker());
                } else if (btn.getModel().isRollover()) {
                    g2.setColor(HOVER_GREEN);
                } else {
                    g2.setColor(PRIMARY_GREEN);
                }
                g2.fillRoundRect(0, 0, c.getWidth() - 2, c.getHeight() - 2, 20, 20);

                // Paint Text (super call handles text centering)
                super.paint(g2, c);
                g2.dispose();
            }
        });
    }

    private void styleLinkButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(100, 100, 100));
        btn.setBackground(Color.WHITE);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(PRIMARY_GREEN);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(100, 100, 100));
            }
        });
    }

    // --- Validation Methods ---

    /**
     * Validates phone number format
     * Must start with 06, 05, or 07 and be exactly 10 digits
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        // Remove any spaces or dashes
        String cleanPhone = phone.trim().replaceAll("[\\s-]", "");

        // Check if it's exactly 10 digits and starts with 06, 05, or 07
        return cleanPhone.matches("^(06|05|07)\\d{8}$");
    }

    /**
     * Validates email format
     * Must contain @ and a valid domain
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Basic email validation: must contain @ and a domain with at least one dot
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.trim().matches(emailPattern);
    }

    // --- Logic ---

    private void handleStep1Actions(ActionEvent e) {
        String pass = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Check if required fields are empty
        if (nameField.getText().isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir les champs obligatoires.", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate password confirmation
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this,
                    "Les mots de passe ne correspondent pas.\nVeuillez vous assurer que les deux mots de passe sont identiques.",
                    "Erreur de mot de passe",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Format d'email invalide.\nL'email doit contenir @ et un domaine valide (ex: exemple@gmail.com)",
                    "Erreur d'email",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate phone number format
        if (!phone.isEmpty() && !isValidPhoneNumber(phone)) {
            JOptionPane.showMessageDialog(this,
                    "Format de téléphone invalide.\nLe numéro doit commencer par 06, 05 ou 07 et contenir exactement 10 chiffres.\nExemple: 0612345678",
                    "Erreur de téléphone",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        tempUser.setName(nameField.getText());
        tempUser.setEmail(email);
        tempUser.setPassword(pass);
        tempUser.setPhoneNumber(phone);
        tempUser.setGender("Femme".equals(genderComboBox.getSelectedItem()) ? "Female" : "Male");

        try {
            if (!dobField.getText().trim().isEmpty()) {
                tempUser.setDateOfBirth(LocalDate.parse(dobField.getText().trim()));
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Date invalide (AAAA-MM-JJ)", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (driverRadio.isSelected()) {
            tempUser.setRole("DRIVER");
            cardLayout.show(cards, "STEP2");
        } else {
            tempUser.setRole("PASSENGER");
            handleFinalRegistration(e);
        }
    }

    private void handleFinalRegistration(ActionEvent e) {
        try {
            if ("DRIVER".equals(tempUser.getRole())) {
                tempUser.setVehicleModel(carModelField.getText());
                tempUser.setVehiclePlate(carPlateField.getText());

                if (tempUser.getVehicleModel().isEmpty() || tempUser.getVehiclePlate().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Info véhicule manquante.", "Attention",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            userService.registerUser(tempUser);
            JOptionPane.showMessageDialog(this, "Inscription réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            this.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}