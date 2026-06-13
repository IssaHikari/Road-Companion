package view;

import service.UserService;
import model.User;
import view.admin.AdminDashboard;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeCheckbox;
    private UserService userService;

    // Brand Colors
    private final Color BRAND_COLOR = new Color(63, 81, 181); // Indigo
    private final Color BG_COLOR = new Color(245, 247, 250); // Light Grey

    public LoginFrame() {
        super("AissaGo - Connexion");
        userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2)); // Split View

        // --- LEFT SIDE: Branding / Image ---
        JPanel leftPanel = new JPanel() {
            private Image cachedBackground = null;
            private int lastWidth = -1;
            private int lastHeight = -1;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Only reload/resize image if size changed or not cached
                if (cachedBackground == null || lastWidth != getWidth() || lastHeight != getHeight()) {
                    ImageIcon icon = new ImageIcon("images/login_bg.jpg");
                    if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        cachedBackground = icon.getImage().getScaledInstance(
                                getWidth(), getHeight(), Image.SCALE_SMOOTH);
                        lastWidth = getWidth();
                        lastHeight = getHeight();
                    }
                }

                if (cachedBackground != null) {
                    // Draw cached image
                    g.drawImage(cachedBackground, 0, 0, this);
                } else {
                    // Fallback to brand color
                    g.setColor(BRAND_COLOR);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        leftPanel.setLayout(new BorderLayout());

        JLabel brandTitle = new JLabel("AissaGo", SwingConstants.CENTER);
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 48));
        brandTitle.setForeground(Color.WHITE);

        JLabel brandSubtitle = new JLabel("Voyagez en toute confiance", SwingConstants.CENTER);
        brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        brandSubtitle.setForeground(new Color(200, 200, 255));

        JPanel brandTextPanel = new JPanel(new GridLayout(2, 1));
        brandTextPanel.setOpaque(false); // Make transparent so image shows through
        brandTextPanel.add(brandTitle);
        brandTextPanel.add(brandSubtitle);

        leftPanel.add(brandTextPanel, BorderLayout.CENTER);
        add(leftPanel);

        // --- RIGHT SIDE: Login Form ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        // Title
        JLabel loginTitle = new JLabel("Bienvenue !");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        loginTitle.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0);
        rightPanel.add(loginTitle, gbc);

        // Inputs
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 5, 0);

        gbc.gridy++;
        rightPanel.add(createLabel("Adresse Email"), gbc);

        gbc.gridy++;
        gbc.gridy++;
        emailField = new JTextField(20);
        styleTextField(emailField, "ouvrir-le-courrier.png"); // Using mail icon
        rightPanel.add(emailField, gbc);

        gbc.gridy++;
        rightPanel.add(createLabel("Mot de passe"), gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(20);
        styleTextField(passwordField, "verrouiller.png"); // Using lock icon
        rightPanel.add(passwordField, gbc);

        // Options
        gbc.gridy++;
        rememberMeCheckbox = new JCheckBox("Se souvenir de moi");
        rememberMeCheckbox.setBackground(Color.WHITE);
        rememberMeCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberMeCheckbox.setFocusPainted(false);
        rightPanel.add(rememberMeCheckbox, gbc);

        // Buttons
        gbc.gridy++;
        gbc.insets = new Insets(25, 0, 10, 0);
        JButton loginButton = new JButton("SE CONNECTER");
        styleButton(loginButton, BRAND_COLOR);
        loginButton.addActionListener(this::handleLogin);
        rightPanel.add(loginButton, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        JPanel registerLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerLinkPanel.setBackground(Color.WHITE);
        JLabel noAccountLabel = new JLabel("Pas encore de compte ? ");
        JButton registerButton = new JButton("S'inscrire");
        styleLinkButton(registerButton);
        registerButton.addActionListener(e -> {
            new RegistrationFrame().setVisible(true);
            this.dispose();
        });
        registerLinkPanel.add(noAccountLabel);
        registerLinkPanel.add(registerButton);
        rightPanel.add(registerLinkPanel, gbc);

        add(rightPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.GRAY);
        return label;
    }

    private void styleTextField(JTextField field, String iconName) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Icon Border
        ImageIcon icon = util.IconManager.getIcon(iconName, 16, 16);
        Border outside = BorderFactory.createLineBorder(new Color(200, 200, 200));
        Border inside = new EmptyBorder(8, 10, 8, 10);

        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setBorder(new EmptyBorder(0, 8, 0, 4));

            // We can't easily put component inside border in Swing without external libs or
            // tricks.
            // Better approach: Use a JPanel container OR use MatteBorder with Icon if we
            // want simple.
            // Simplest robust way: Compound border with empty space on left, and paint icon
            // in component or use separate label?
            // Let's us a simple trick: create a border that paints the icon.

            Border iconBorder = new Border() {
                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    // Paint icon
                    int yPos = (height - icon.getIconHeight()) / 2;
                    icon.paintIcon(c, g, x + 10, yPos);
                }

                @Override
                public Insets getBorderInsets(Component c) {
                    return new Insets(0, 35, 0, 0); // Reserve space for icon
                }

                @Override
                public boolean isBorderOpaque() {
                    return false;
                }
            };

            field.setBorder(BorderFactory.createCompoundBorder(
                    outside,
                    BorderFactory.createCompoundBorder(iconBorder, inside)));
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(outside, inside));
        }
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleLinkButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(BRAND_COLOR);
        btn.setBackground(Color.WHITE);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
    }

    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            User user = userService.authenticateUser(email, password);

            if (user != null) {
                // Remove simple message, go straight to app or show nice toast (using standard
                // message for now)
                // JOptionPane.showMessageDialog(this, "Bienvenue " + user.getName(), "Succès",
                // JOptionPane.INFORMATION_MESSAGE);

                if (user.isIsAdmin()) {
                    new AdminDashboard(user).setVisible(true);
                } else {
                    new MainFrame(user).setVisible(true);
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            // Optional: Customize FlatLaf defaults if needed
            // UIManager.put("Button.arc", 10);
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}