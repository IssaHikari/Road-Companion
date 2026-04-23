package view.settings;

import model.User;
import util.LanguageManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panneau des paramètres des trajets
 * Différent selon le rôle (Chauffeur / Passager)
 */
public class TripsSettingsPanel extends JPanel {

    private final User currentUser;
    private final LanguageManager lang;
    private static final Color BRAND_COLOR = new Color(63, 81, 181);

    // Paramètres chauffeur
    private JSpinner maxPassengersSpinner;
    private JTextField defaultPriceField;
    private JCheckBox autoAcceptCheckbox;
    private JSpinner autoCancelSpinner;

    // Paramètres passager
    private JCheckBox noSmokingCheckbox;
    private JCheckBox musicCheckbox;
    private JComboBox<String> driverGenderCombo;

    public TripsSettingsPanel(User user) {
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

        if ("DRIVER".equals(currentUser.getRole())) {
            contentPanel.add(createDriverSettings());
        } else {
            contentPanel.add(createPassengerSettings());
        }

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createActionButtons());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        if (lang.isArabic())
            scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createDriverSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Section Capacité
        JPanel capacityPanel = new JPanel(new GridBagLayout());
        capacityPanel.setBackground(Color.WHITE);
        capacityPanel.setBorder(createSectionBorder(lang.get("trips.driver.title")));
        capacityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        if (lang.isArabic())
            capacityPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Nombre max de passagers
        gbc.gridx = 0;
        gbc.gridy = 0;
        capacityPanel.add(createLabel(lang.get("trips.driver.max_passengers")), gbc);
        gbc.gridx = 1;
        maxPassengersSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 8, 1));
        maxPassengersSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) maxPassengersSpinner.getEditor()).getTextField().setEditable(false);
        capacityPanel.add(maxPassengersSpinner, gbc);

        // Prix par défaut
        gbc.gridx = 0;
        gbc.gridy = 1;
        capacityPanel.add(createLabel(lang.get("trips.driver.default_price")), gbc);
        gbc.gridx = 1;
        defaultPriceField = new JTextField("500", 10);
        defaultPriceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        defaultPriceField.setPreferredSize(new Dimension(150, 35));
        if (lang.isArabic())
            defaultPriceField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        capacityPanel.add(defaultPriceField, gbc);

        panel.add(capacityPanel);
        panel.add(Box.createVerticalStrut(15));

        // Section Automatisation
        JPanel autoPanel = new JPanel();
        autoPanel.setLayout(new BoxLayout(autoPanel, BoxLayout.Y_AXIS));
        autoPanel.setBackground(Color.WHITE);
        autoPanel.setBorder(createSectionBorder(lang.get("trips.driver.automation")));
        autoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel innerPanel = new JPanel(new GridBagLayout());
        innerPanel.setBackground(Color.WHITE);
        if (lang.isArabic())
            innerPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = lang.isArabic() ? GridBagConstraints.EAST : GridBagConstraints.WEST;

        // Acceptation automatique
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        autoAcceptCheckbox = new JCheckBox(lang.get("trips.driver.auto_accept"));
        autoAcceptCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        autoAcceptCheckbox.setBackground(Color.WHITE);
        if (lang.isArabic())
            autoAcceptCheckbox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        innerPanel.add(autoAcceptCheckbox, gbc);

        // Annulation automatique
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        innerPanel.add(createLabel(lang.get("trips.driver.auto_cancel")), gbc);
        gbc.gridx = 1;
        autoCancelSpinner = new JSpinner(new SpinnerNumberModel(24, 1, 168, 1));
        autoCancelSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) autoCancelSpinner.getEditor()).getTextField().setEditable(false);
        innerPanel.add(autoCancelSpinner, gbc);

        autoPanel.add(innerPanel);
        panel.add(autoPanel);

        return panel;
    }

    private JPanel createPassengerSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Section Préférences
        JPanel prefsPanel = new JPanel();
        prefsPanel.setLayout(new BoxLayout(prefsPanel, BoxLayout.Y_AXIS));
        prefsPanel.setBackground(Color.WHITE);
        prefsPanel.setBorder(createSectionBorder(lang.get("trips.passenger.title")));
        prefsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JPanel innerPanel = new JPanel(new GridBagLayout());
        innerPanel.setBackground(Color.WHITE);
        if (lang.isArabic())
            innerPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = lang.isArabic() ? GridBagConstraints.EAST : GridBagConstraints.WEST;

        // Non-fumeur
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        noSmokingCheckbox = new JCheckBox(lang.get("trips.passenger.nosmoking"));
        noSmokingCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        noSmokingCheckbox.setBackground(Color.WHITE);
        noSmokingCheckbox.setSelected(true);
        if (lang.isArabic())
            noSmokingCheckbox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        innerPanel.add(noSmokingCheckbox, gbc);

        // Musique
        gbc.gridy = 1;
        musicCheckbox = new JCheckBox(lang.get("trips.passenger.music"));
        musicCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        musicCheckbox.setBackground(Color.WHITE);
        musicCheckbox.setSelected(true);
        if (lang.isArabic())
            musicCheckbox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        innerPanel.add(musicCheckbox, gbc);

        // Genre du chauffeur
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        innerPanel.add(createLabel(lang.get("trips.passenger.driver_pref")), gbc);
        gbc.gridx = 1;
        driverGenderCombo = new JComboBox<>(new String[] {
                lang.get("trips.passenger.driver_any"),
                lang.get("trips.passenger.driver_male"),
                lang.get("trips.passenger.driver_female")
        });
        driverGenderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        driverGenderCombo.setPreferredSize(new Dimension(200, 35));
        if (lang.isArabic()) {
            ((JLabel) driverGenderCombo.getRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        innerPanel.add(driverGenderCombo, gbc);

        prefsPanel.add(innerPanel);
        panel.add(prefsPanel);

        // Section Info
        panel.add(Box.createVerticalStrut(15));
        JPanel infoPanel = new JPanel(new FlowLayout(lang.isArabic() ? FlowLayout.RIGHT : FlowLayout.LEFT, 20, 20));
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 1));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        if (lang.isArabic())
            infoPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel infoLabel = new JLabel(lang.get("trips.info"));
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (lang.isArabic())
            infoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        infoPanel.add(infoLabel);

        panel.add(infoPanel);

        return panel;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(lang.isArabic() ? FlowLayout.LEFT : FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton resetBtn = new JButton(lang.get("trips.reset"));
        styleButton(resetBtn, false);
        resetBtn.addActionListener(e -> resetToDefaults());

        JButton saveBtn = new JButton(lang.get("btn.save"));
        styleButton(saveBtn, true);
        saveBtn.addActionListener(e -> saveSettings());

        panel.add(resetBtn);
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

    private void resetToDefaults() {
        if ("DRIVER".equals(currentUser.getRole())) {
            maxPassengersSpinner.setValue(4);
            defaultPriceField.setText("500");
            autoAcceptCheckbox.setSelected(false);
            autoCancelSpinner.setValue(24);
        } else {
            noSmokingCheckbox.setSelected(true);
            musicCheckbox.setSelected(true);
            driverGenderCombo.setSelectedIndex(0);
        }
        JOptionPane.showMessageDialog(this, lang.get("trips.reset") + "!", lang.get("dialog.info"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveSettings() {
        // Simuler la sauvegarde
        JOptionPane.showMessageDialog(this, lang.get("trips.save.success"), lang.get("dialog.success"),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
