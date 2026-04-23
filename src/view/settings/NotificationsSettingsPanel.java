package view.settings;

import model.User;
import util.LanguageManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panneau des paramètres de notifications
 */
public class NotificationsSettingsPanel extends JPanel {

    private final User currentUser;
    private final LanguageManager lang;
    private static final Color BRAND_COLOR = new Color(63, 81, 181);

    private JCheckBox newTripCheckbox;
    private JCheckBox bookingAcceptedCheckbox;
    private JCheckBox tripCancelledCheckbox;
    private JCheckBox appNotifCheckbox;
    private JCheckBox emailNotifCheckbox;

    public NotificationsSettingsPanel(User user) {
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

        contentPanel.add(createNotificationTypesSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createNotificationChannelsSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createActionButtons());

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createNotificationTypesSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("notif.types.title")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel checkboxPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        checkboxPanel.setBackground(Color.WHITE);
        checkboxPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        newTripCheckbox = createCheckbox(lang.get("notif.type.new_trip"));
        bookingAcceptedCheckbox = createCheckbox(lang.get("notif.type.booking_accepted"));
        tripCancelledCheckbox = createCheckbox(lang.get("notif.type.trip_cancelled"));

        checkboxPanel.add(newTripCheckbox);
        checkboxPanel.add(bookingAcceptedCheckbox);
        checkboxPanel.add(tripCancelledCheckbox);

        panel.add(checkboxPanel);
        return panel;
    }

    private JPanel createNotificationChannelsSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("notif.channels.title")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel checkboxPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        checkboxPanel.setBackground(Color.WHITE);
        checkboxPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        appNotifCheckbox = createCheckbox(lang.get("notif.channel.app"));
        emailNotifCheckbox = createCheckbox(lang.get("notif.channel.email"));

        checkboxPanel.add(appNotifCheckbox);
        checkboxPanel.add(emailNotifCheckbox);

        panel.add(checkboxPanel);
        return panel;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(lang.isArabic() ? FlowLayout.LEFT : FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton testBtn = new JButton(lang.get("notif.btn.test"));
        styleButton(testBtn, false);
        testBtn.addActionListener(e -> testNotification());

        JButton saveBtn = new JButton(lang.get("btn.save"));
        styleButton(saveBtn, true);
        saveBtn.addActionListener(e -> saveSettings());

        panel.add(testBtn);
        panel.add(saveBtn);

        return panel;
    }

    private JCheckBox createCheckbox(String text) {
        JCheckBox checkbox = new JCheckBox(text);
        checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        checkbox.setBackground(Color.WHITE);
        checkbox.setSelected(true);
        if (lang.isArabic())
            checkbox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return checkbox;
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

    private void testNotification() {
        JOptionPane.showMessageDialog(this,
                lang.get("notif.type.booking_accepted"),
                lang.get("notif.btn.test"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveSettings() {
        JOptionPane.showMessageDialog(this, lang.get("notif.save.success"), lang.get("dialog.success"),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
