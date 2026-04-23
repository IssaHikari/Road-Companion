package view.settings;

import model.User;
import util.LanguageManager;
import view.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Locale;

/**
 * Panneau des paramètres d'apparence
 * Permet de changer le thème, la langue et la taille de police
 */
public class AppearanceSettingsPanel extends JPanel {

    private final User currentUser;
    private final LanguageManager lang;
    private static final Color BRAND_COLOR = new Color(63, 81, 181);

    private JRadioButton lightThemeRadio;
    private JRadioButton darkThemeRadio;
    private JComboBox<LanguageOption> languageCombo;
    private JSlider fontSizeSlider;
    private JLabel previewLabel;

    // Helper class for ComboBox items
    private static class LanguageOption {
        String label;
        Locale locale;

        public LanguageOption(String label, Locale locale) {
            this.label = label;
            this.locale = locale;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public AppearanceSettingsPanel(User user) {
        this.currentUser = user;
        this.lang = LanguageManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // RTL Support
        if (lang.isArabic()) {
            this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(createThemeSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createLanguageSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createFontSizeSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createPreviewSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createActionButtons());

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createThemeSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("appearance.theme")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel radioPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        ButtonGroup group = new ButtonGroup();

        lightThemeRadio = new JRadioButton(lang.get("appearance.theme.light"));
        lightThemeRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lightThemeRadio.setBackground(Color.WHITE);
        lightThemeRadio.setSelected(!FlatDarkLaf.isLafDark());
        lightThemeRadio.addActionListener(e -> updatePreview());
        if (lang.isArabic())
            lightThemeRadio.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        darkThemeRadio = new JRadioButton(lang.get("appearance.theme.dark"));
        darkThemeRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        darkThemeRadio.setBackground(Color.WHITE);
        darkThemeRadio.setSelected(FlatDarkLaf.isLafDark());
        darkThemeRadio.addActionListener(e -> updatePreview());
        if (lang.isArabic())
            darkThemeRadio.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        group.add(lightThemeRadio);
        group.add(darkThemeRadio);

        radioPanel.add(lightThemeRadio);
        radioPanel.add(darkThemeRadio);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (lang.isArabic())
            infoPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        infoPanel.setBackground(new Color(255, 248, 220));
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 193, 7), 1));
        JLabel infoLabel = new JLabel("ℹ️ " + lang.get("appearance.theme.info"));
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoPanel.add(infoLabel);

        panel.add(radioPanel);
        panel.add(infoPanel);

        return panel;
    }

    private JPanel createLanguageSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        if (lang.isArabic())
            panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 15));

        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("appearance.language")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel label = new JLabel(lang.get("appearance.language.label"));
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));

        LanguageOption[] options = {
                new LanguageOption("🇫🇷 Français", LanguageManager.FRENCH),
                new LanguageOption("🇬🇧 English", LanguageManager.ENGLISH),
                new LanguageOption("🇸🇦 العربية", LanguageManager.ARABIC)
        };

        languageCombo = new JComboBox<>(options);
        languageCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        languageCombo.setPreferredSize(new Dimension(200, 35));

        // Select current language
        Locale current = lang.getCurrentLocale();
        for (int i = 0; i < options.length; i++) {
            if (options[i].locale.getLanguage().equals(current.getLanguage())) {
                languageCombo.setSelectedIndex(i);
                break;
            }
        }

        // Add listener WITHOUT triggering immediately
        languageCombo.addActionListener(e -> changeLanguage());

        panel.add(label);
        panel.add(languageCombo);

        return panel;
    }

    private void changeLanguage() {
        LanguageOption selected = (LanguageOption) languageCombo.getSelectedItem();
        if (selected != null && !selected.locale.getLanguage().equals(lang.getCurrentLocale().getLanguage())) {
            // Update language
            lang.setLanguage(selected.locale);

            // Show confirmation and reload MainFrame
            int choice = JOptionPane.showConfirmDialog(this,
                    lang.isArabic() ? "تم تغيير اللغة. هل تريد إعادة تحميل الواجهة الآن؟"
                            : "Language changed. Reload interface now?",
                    lang.get("settings.title"),
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                // Find MainFrame and reload
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof MainFrame) {
                    window.dispose();
                    new MainFrame(currentUser).setVisible(true);
                } else if (window instanceof JFrame) {
                    // Fallback for testing
                    window.dispose();
                    new MainFrame(currentUser).setVisible(true);
                }
            }
        }
    }

    private JPanel createFontSizeSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("appearance.fontsize")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel sliderPanel = new JPanel(new BorderLayout(10, 10));
        sliderPanel.setBackground(Color.WHITE);
        sliderPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        fontSizeSlider = new JSlider(JSlider.HORIZONTAL, 12, 20, 14);
        fontSizeSlider.setMajorTickSpacing(2);
        fontSizeSlider.setMinorTickSpacing(1);
        fontSizeSlider.setPaintTicks(true);
        fontSizeSlider.setPaintLabels(true);
        fontSizeSlider.setBackground(Color.WHITE);
        fontSizeSlider.addChangeListener(e -> updatePreview());
        if (lang.isArabic())
            fontSizeSlider.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel sizeLabel = new JLabel(lang.isArabic() ? "14px :الحجم" : "Taille: 14px");
        sizeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        if (lang.isArabic())
            sizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        fontSizeSlider.addChangeListener(e -> {
            sizeLabel.setText(lang.isArabic() ? fontSizeSlider.getValue() + "px :الحجم"
                    : "Taille: " + fontSizeSlider.getValue() + "px");
            updatePreview();
        });

        sliderPanel.add(sizeLabel, BorderLayout.NORTH);
        sliderPanel.add(fontSizeSlider, BorderLayout.CENTER);

        panel.add(sliderPanel);
        return panel;
    }

    private JPanel createPreviewSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("appearance.preview")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        previewPanel.setBackground(Color.WHITE);
        previewPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        previewLabel = new JLabel(lang.get("appearance.preview.text"));
        previewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton previewButton = new JButton("Button");
        previewButton.setFont(new Font("Segoe UI", Font.BOLD, 13));

        previewPanel.add(previewLabel);
        previewPanel.add(previewButton);

        panel.add(previewPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        if (lang.isArabic())
            panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton applyBtn = new JButton(lang.get("appearance.btn.apply"));
        styleButton(applyBtn, false);
        applyBtn.addActionListener(e -> applyTheme());

        JButton saveBtn = new JButton("💾 " + lang.get("btn.save"));
        styleButton(saveBtn, true);
        saveBtn.addActionListener(e -> saveSettings());

        panel.add(applyBtn);
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

    private void updatePreview() {
        int fontSize = fontSizeSlider.getValue();
        previewLabel.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));

        if (darkThemeRadio.isSelected()) {
            previewLabel.setForeground(Color.WHITE);
        } else {
            previewLabel.setForeground(Color.BLACK);
        }
    }

    private void applyTheme() {
        try {
            if (darkThemeRadio.isSelected()) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }

            // Update all open windows
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);

                // If main frame, might need to refresh specific colors that are hardcoded
                if (window instanceof view.MainFrame) {
                    // MainFrame uses specific colors constants, we might need to trigger a repaint
                    // or re-init
                    window.repaint();
                }
            }

            // Persist preference (Optional - simplistic approach for now)
            // java.util.prefs.Preferences.userNodeForPackage(getClass()).put("theme",
            // darkThemeRadio.isSelected() ? "dark" : "light");

            JOptionPane.showMessageDialog(this,
                    lang.isArabic() ? "تم تطبيق المظهر بنجاح" : "Theme applied successfully",
                    lang.get("appearance.theme"),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error applying theme: " + ex.getMessage());
        }
    }

    private void saveSettings() {
        JOptionPane.showMessageDialog(this,
                "Settings Saved (Simulation)",
                lang.get("btn.save"),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
