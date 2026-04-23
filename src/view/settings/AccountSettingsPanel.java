package view.settings;

import model.User;
import service.UserService;
import util.LanguageManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;

/**
 * Panneau des paramètres du compte utilisateur
 * Permet de modifier les informations personnelles
 */
public class AccountSettingsPanel extends JPanel {

    private final User currentUser;
    private final UserService userService;
    private final LanguageManager lang;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JLabel avatarLabel;
    private JTextField licenseField; // Pour les chauffeurs

    private static final Color BRAND_COLOR = new Color(63, 81, 181);

    public AccountSettingsPanel(User user) {
        this.currentUser = user;
        this.userService = new UserService();
        this.lang = LanguageManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        if (lang.isArabic()) {
            this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Section Avatar
        contentPanel.add(createAvatarSection());
        contentPanel.add(Box.createVerticalStrut(20));

        // Section Informations personnelles
        contentPanel.add(createPersonalInfoSection());
        contentPanel.add(Box.createVerticalStrut(20));

        // Section Sécurité
        contentPanel.add(createSecuritySection());
        contentPanel.add(Box.createVerticalStrut(20));

        // Boutons d'action
        contentPanel.add(createActionButtons());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        if (lang.isArabic()) {
            scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createAvatarSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("account.avatar.title")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Avatar circulaire
        avatarLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 100;
                if (currentUser.getAvatarPath() != null && new File(currentUser.getAvatarPath()).exists()) {
                    ImageIcon img = new ImageIcon(currentUser.getAvatarPath());
                    g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
                    g2.drawImage(img.getImage(), 0, 0, size, size, null);
                } else {
                    g2.setColor(BRAND_COLOR);
                    g2.fillOval(0, 0, size, size);

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 40));
                    String initial = currentUser.getName() != null && !currentUser.getName().isEmpty()
                            ? currentUser.getName().substring(0, 1).toUpperCase()
                            : "?";
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (size - fm.stringWidth(initial)) / 2;
                    int y = (size - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(initial, x, y);
                }
            }
        };
        avatarLabel.setPreferredSize(new Dimension(100, 100));

        JButton changeAvatarBtn = new JButton(lang.get("account.avatar.change"));
        styleButton(changeAvatarBtn, false);
        changeAvatarBtn.addActionListener(e -> handleChangeAvatar());

        panel.add(avatarLabel);
        panel.add(changeAvatarBtn);

        return panel;
    }

    private JPanel createPersonalInfoSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("account.info.title")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        if (lang.isArabic())
            formPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel(lang.get("account.field.name")), gbc);
        gbc.gridx = 1;
        nameField = createStyledTextField(currentUser.getName());
        formPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel(lang.get("account.field.email")), gbc);
        gbc.gridx = 1;
        emailField = createStyledTextField(currentUser.getEmail());
        formPanel.add(emailField, gbc);

        // Téléphone
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel(lang.get("account.field.phone")), gbc);
        gbc.gridx = 1;
        phoneField = createStyledTextField(currentUser.getPhoneNumber());
        formPanel.add(phoneField, gbc);

        // Numéro de permis (seulement pour les chauffeurs)
        if ("DRIVER".equals(currentUser.getRole())) {
            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(createLabel(lang.get("account.field.license")), gbc);
            gbc.gridx = 1;
            licenseField = createStyledTextField(currentUser.getVehiclePlate());
            licenseField.setEditable(false);
            licenseField.setBackground(new Color(240, 240, 240));
            formPanel.add(licenseField, gbc);
        }

        panel.add(formPanel);
        return panel;
    }

    private JPanel createSecuritySection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        if (lang.isArabic())
            panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 20));

        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("account.security.title")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JButton changePasswordBtn = new JButton(lang.get("account.btn.changepassword"));
        styleButton(changePasswordBtn, false);
        changePasswordBtn.addActionListener(e -> handleChangePassword());

        panel.add(changePasswordBtn);

        return panel;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        if (lang.isArabic())
            panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton cancelBtn = new JButton(lang.get("btn.cancel"));
        styleButton(cancelBtn, false);
        cancelBtn.addActionListener(e -> resetFields());

        JButton saveBtn = new JButton(lang.get("account.btn.save_changes"));
        styleButton(saveBtn, true);
        saveBtn.addActionListener(e -> handleSaveChanges());

        panel.add(cancelBtn);
        panel.add(saveBtn);

        return panel;
    }

    // Méthodes utilitaires

    private TitledBorder createSectionBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                title);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        border.setTitleColor(BRAND_COLOR);
        if (lang.isArabic()) {
            border.setTitleJustification(TitledBorder.RIGHT);
            border.setTitlePosition(TitledBorder.TOP);
        }
        return border;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(80, 80, 80));
        if (lang.isArabic())
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text, 20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)));
        if (lang.isArabic())
            field.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return field;
    }

    private void styleButton(JButton btn, boolean isPrimary) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        if (isPrimary) {
            btn.setBackground(BRAND_COLOR);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(240, 240, 240));
            btn.setForeground(new Color(80, 80, 80));
        }
    }

    // Gestionnaires d'événements

    private void handleChangeAvatar() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images", "jpg", "jpeg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            currentUser.setAvatarPath(selectedFile.getAbsolutePath());
            avatarLabel.repaint();
            JOptionPane.showMessageDialog(this, lang.get("account.avatar.success"), lang.get("dialog.success"),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleChangePassword() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPasswordField currentPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        if (lang.isArabic()) {
            panel.add(createLabel(lang.get("account.password.current")));
            panel.add(currentPasswordField);
            panel.add(createLabel(lang.get("account.password.new")));
            panel.add(newPasswordField);
            panel.add(createLabel(lang.get("account.password.confirm")));
            panel.add(confirmPasswordField);
        } else {
            panel.add(new JLabel(lang.get("account.password.current")));
            panel.add(currentPasswordField);
            panel.add(new JLabel(lang.get("account.password.new")));
            panel.add(newPasswordField);
            panel.add(new JLabel(lang.get("account.password.confirm")));
            panel.add(confirmPasswordField);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, lang.get("account.password.title"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.equals(confirmPassword) && !newPassword.isEmpty()) {
                currentUser.setPassword(newPassword);
                JOptionPane.showMessageDialog(this, lang.get("account.password.success"), lang.get("dialog.success"),
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, lang.get("account.password.mismatch"), lang.get("dialog.error"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSaveChanges() {
        try {
            currentUser.setName(nameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPhoneNumber(phoneField.getText());

            // Simuler la sauvegarde
            JOptionPane.showMessageDialog(this, lang.get("account.save.success"), lang.get("dialog.success"),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), lang.get("dialog.error"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhoneNumber());
    }
}
