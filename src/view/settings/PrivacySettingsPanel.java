package view.settings;

import model.User;
import view.LoginFrame;
import util.LanguageManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panneau des paramètres de confidentialité et sécurité
 */
public class PrivacySettingsPanel extends JPanel {

    private final User currentUser;
    private final LanguageManager lang;
    private static final Color BRAND_COLOR = new Color(63, 81, 181);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);

    private JCheckBox showPhoneCheckbox;

    public PrivacySettingsPanel(User user) {
        this.currentUser = user;
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

        contentPanel.add(createPrivacySection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createSecuritySection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createDangerZoneSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createActionButtons());

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createPrivacySection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("privacy.title")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Use FlowLayout with correct alignment based on language
        JPanel innerPanel = new JPanel(new FlowLayout(lang.isArabic() ? FlowLayout.RIGHT : FlowLayout.LEFT, 15, 15));
        innerPanel.setBackground(Color.WHITE);
        if (lang.isArabic())
            innerPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        showPhoneCheckbox = new JCheckBox(lang.get("privacy.show_phone"));
        showPhoneCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        showPhoneCheckbox.setBackground(Color.WHITE);
        showPhoneCheckbox.setSelected(true);
        if (lang.isArabic())
            showPhoneCheckbox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        innerPanel.add(showPhoneCheckbox);
        panel.add(innerPanel);

        return panel;
    }

    private JPanel createSecuritySection() {
        JPanel panel = new JPanel(new FlowLayout(lang.isArabic() ? FlowLayout.RIGHT : FlowLayout.LEFT, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("privacy.security.title")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton changePasswordBtn = new JButton(lang.get("account.btn.changepassword"));
        styleButton(changePasswordBtn, false);
        changePasswordBtn.addActionListener(e -> changePassword());

        JButton logoutBtn = new JButton(lang.get("privacy.btn.logout"));
        styleButton(logoutBtn, false);
        logoutBtn.addActionListener(e -> logout());

        panel.add(changePasswordBtn);
        panel.add(logoutBtn);

        return panel;
    }

    private JPanel createDangerZoneSection() {
        JPanel panel = new JPanel(new FlowLayout(lang.isArabic() ? FlowLayout.RIGHT : FlowLayout.LEFT, 15, 15));
        panel.setBackground(new Color(255, 245, 245));

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DANGER_COLOR, 2),
                lang.get("privacy.danger.title"),
                lang.isArabic() ? TitledBorder.RIGHT : TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                DANGER_COLOR);
        panel.setBorder(border);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton deleteAccountBtn = new JButton(lang.get("privacy.btn.delete"));
        deleteAccountBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteAccountBtn.setFocusPainted(false);
        deleteAccountBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteAccountBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        deleteAccountBtn.setBackground(DANGER_COLOR);
        deleteAccountBtn.setForeground(Color.WHITE);
        deleteAccountBtn.addActionListener(e -> deleteAccount());

        panel.add(deleteAccountBtn);

        return panel;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(lang.isArabic() ? FlowLayout.LEFT : FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton saveBtn = new JButton(lang.get("btn.save"));
        styleButton(saveBtn, true);
        saveBtn.addActionListener(e -> saveSettings());

        panel.add(saveBtn);
        return panel;
    }

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

    private void changePassword() {
        JOptionPane.showMessageDialog(this,
                lang.get("account.info.title") + " -> " + lang.get("account.btn.changepassword"));
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                lang.get("privacy.logout.confirm"),
                lang.get("privacy.btn.logout"),
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Window window = SwingUtilities.getWindowAncestor(this);
            window.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void deleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                lang.get("privacy.delete.confirm"),
                lang.get("privacy.btn.delete"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    lang.get("privacy.delete.success"),
                    lang.get("privacy.danger.title"),
                    JOptionPane.INFORMATION_MESSAGE);
            // In a real app, call service to delete account
            Window window = SwingUtilities.getWindowAncestor(this);
            window.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void saveSettings() {
        JOptionPane.showMessageDialog(this, lang.get("privacy.save.success"), lang.get("dialog.success"),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
