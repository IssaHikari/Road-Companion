package view.settings;

import model.User;
import util.LanguageManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panneau principal des paramètres avec navigation par onglets
 * Structure professionnelle et moderne avec FlatLaf
 */
public class SettingsPanel extends JPanel {

    private final User currentUser;
    private JTabbedPane tabbedPane;
    private final LanguageManager lang;

    // Couleurs du thème
    private static final Color BRAND_COLOR = new Color(63, 81, 181);

    public SettingsPanel(User user) {
        this.currentUser = user;
        this.lang = LanguageManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Handle Orientation (LTR vs RTL) - Basic Implementation
        if (lang.isArabic()) {
            this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        } else {
            this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }

        // En-tête
        add(createHeader(), BorderLayout.NORTH);

        // Onglets de paramètres
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if (lang.isArabic()) {
            tabbedPane.setTabPlacement(JTabbedPane.RIGHT);
            tabbedPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        // Ajouter les différents panneaux de paramètres
        addSettingsTabs();

        // Wrap tabbedPane in ScrollPane for small screens
        JScrollPane scrollPane = new JScrollPane(tabbedPane);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("⚙️ " + lang.get("settings.title"));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));

        JLabel subtitleLabel = new JLabel(lang.get("settings.subtitle"));
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);

        if (lang.isArabic()) {
            titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            subtitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        // Align header text
        if (lang.isArabic()) {
            header.add(textPanel, BorderLayout.EAST);
        } else {
            header.add(textPanel, BorderLayout.WEST);
        }

        return header;
    }

    private void addSettingsTabs() {
        // 1. Compte
        tabbedPane.addTab(icon("👤 ") + lang.get("settings.tab.account"), new AccountSettingsPanel(currentUser));

        // 2. Trajets
        tabbedPane.addTab(icon("🚗 ") + lang.get("settings.tab.trips"), new TripsSettingsPanel(currentUser));

        // 3. Paiement
        tabbedPane.addTab(icon("💳 ") + lang.get("settings.tab.payment"), new PaymentSettingsPanel(currentUser));

        // 4. Notifications
        tabbedPane.addTab(icon("🔔 ") + lang.get("settings.tab.notifications"),
                new NotificationsSettingsPanel(currentUser));

        // 5. Confidentialité
        tabbedPane.addTab(icon("🔐 ") + lang.get("settings.tab.privacy"), new PrivacySettingsPanel(currentUser));

        // 6. Apparence
        tabbedPane.addTab(icon("🎨 ") + lang.get("settings.tab.appearance"), new AppearanceSettingsPanel(currentUser));

        // 7. À propos
        tabbedPane.addTab(icon("ℹ️ ") + lang.get("settings.tab.about"), new AboutPanel());
    }

    private String icon(String emoji) {
        // Optional: remove emoji or adjust based on OS support if needed
        return emoji;
    }
}
