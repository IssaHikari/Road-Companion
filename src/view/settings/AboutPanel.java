package view.settings;

import util.LanguageManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panneau "À propos" de l'application
 * Affiche les informations sur l'application et le développeur
 */
public class AboutPanel extends JPanel {

    private final LanguageManager lang;
    private static final Color BRAND_COLOR = new Color(63, 81, 181);

    public AboutPanel() {
        this.lang = LanguageManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        if (lang.isArabic()) {
            this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Logo et nom de l'application
        contentPanel.add(createAppInfoSection());
        contentPanel.add(Box.createVerticalStrut(30));

        // Informations détaillées
        contentPanel.add(createDetailsSection());
        contentPanel.add(Box.createVerticalStrut(30));

        // Informations développeur
        contentPanel.add(createDeveloperSection());
        contentPanel.add(Box.createVerticalStrut(30));

        // Boutons
        contentPanel.add(createButtonsSection());

        // Wrap content in ScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createAppInfoSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo
        JLabel logoLabel = new JLabel("🚗");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nom de l'application
        JLabel appNameLabel = new JLabel(lang.get("app.name"));
        appNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        appNameLabel.setForeground(BRAND_COLOR);
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Slogan
        JLabel sloganLabel = new JLabel(lang.get("app.slogan"));
        sloganLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        sloganLabel.setForeground(Color.GRAY);
        sloganLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(logoLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(appNameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(sloganLabel);

        return panel;
    }

    private JPanel createDetailsSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 251));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(20, 20, 20, 20)));
        panel.setMaximumSize(new Dimension(500, 250));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = lang.isArabic() ? GridBagConstraints.EAST : GridBagConstraints.WEST;

        // Version
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createInfoLabel(lang.get("about.version")), gbc);
        gbc.gridx = 1;
        panel.add(createValueLabel("1.0.0"), gbc);

        // Date de sortie
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createInfoLabel(lang.get("about.date")), gbc);
        gbc.gridx = 1;
        panel.add(createValueLabel("Dec 2025"), gbc);

        // Technologie
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createInfoLabel(lang.get("about.tech")), gbc);
        gbc.gridx = 1;
        panel.add(createValueLabel("Java Swing + FlatLaf"), gbc);

        // Base de données
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(createInfoLabel(lang.get("about.db")), gbc);
        gbc.gridx = 1;
        panel.add(createValueLabel("MySQL"), gbc);

        // Licence
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(createInfoLabel(lang.get("about.license")), gbc);
        gbc.gridx = 1;
        panel.add(createValueLabel("Projet Académique"), gbc);

        return panel;
    }

    private JPanel createDeveloperSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                lang.get("about.dev.title"),
                lang.isArabic() ? TitledBorder.RIGHT : TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                BRAND_COLOR);
        panel.setBorder(border);
        panel.setMaximumSize(new Dimension(500, 200));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = lang.isArabic() ? GridBagConstraints.EAST : GridBagConstraints.WEST;

        // Nom du développeur
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createInfoLabel(lang.get("about.dev.name")), gbc);
        gbc.gridx = 1;
        panel.add(createValueLabel("Aissa"), gbc);

        // Université
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createInfoLabel(lang.get("about.dev.uni")), gbc);
        gbc.gridx = 1;
        panel.add(createValueLabel(" l'université de Béjaia "), gbc);

        // Année
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createInfoLabel(lang.get("about.dev.year")), gbc);
        gbc.gridx = 1;
        panel.add(createValueLabel("2024-2025"), gbc);

        return panel;
    }

    private JPanel createButtonsSection() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Row 1: Main Action Buttons
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        row1.setOpaque(false);

        JButton websiteBtn = new JButton("🌐 " + lang.get("about.btn.website"));
        styleButton(websiteBtn);
        websiteBtn.addActionListener(e -> openWebsite());

        JButton supportBtn = new JButton("📞 " + (lang.isArabic() ? "الدعم الفني" : "Contact Support"));
        styleButton(supportBtn);
        supportBtn.addActionListener(
                e -> JOptionPane.showMessageDialog(this, "Email: support@aissago.dz\nTel: +213 123 456 789"));

        JButton termsBtn = new JButton("📄 " + (lang.isArabic() ? "شروط الخدمة" : "Terms of Service"));
        styleButton(termsBtn);
        termsBtn.addActionListener(e -> showLicense()); // Reuse license logic for now

        row1.add(websiteBtn);
        row1.add(supportBtn);
        row1.add(termsBtn);

        // Row 2: Social Media
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        row2.setOpaque(false);

        row2.add(createSocialLabel("facebook.png", "Facebook"));
        row2.add(createSocialLabel("twitter.png", "Twitter")); // or X
        row2.add(createSocialLabel("instagram.png", "Instagram"));
        row2.add(createSocialLabel("linkedin.png", "LinkedIn"));

        panel.add(row1);
        panel.add(row2);

        return panel;
    }

    private JLabel createSocialLabel(String iconName, String tooltip) {
        JLabel lbl = new JLabel();
        ImageIcon icon = util.IconManager.getIcon(iconName, 24, 24);
        if (icon != null) {
            lbl.setIcon(icon);
        } else {
            lbl.setText(tooltip.substring(0, 1)); // Fallback text
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
        lbl.setToolTipText(tooltip);
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(AboutPanel.this, "Opening " + tooltip + "...");
            }
        });
        return lbl;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(80, 80, 80));
        if (lang.isArabic())
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(50, 50, 50));
        if (lang.isArabic())
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setBackground(new Color(240, 240, 240));
        btn.setForeground(new Color(80, 80, 80));
    }

    private void showLicense() {
        String license = "MIT License\n\n" +
                "Copyright (c) 2025 AissaGo\n\n" +
                "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
                "of this software and associated documentation files (the \"Software\"), to deal\n" +
                "in the Software without restriction, including without limitation the rights\n" +
                "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
                "copies of the Software, and to permit persons to whom the Software is\n" +
                "furnished to do so, subject to the following conditions:\n\n" +
                "The above copyright notice and this permission notice shall be included in all\n" +
                "copies or substantial portions of the Software.\n\n" +
                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
                "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.";

        JTextArea textArea = new JTextArea(license);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        if (lang.isArabic())
            textArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT); // English License

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, scrollPane, lang.get("about.btn.license"), JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCredits() {
        String credits = "🌟 " + lang.get("about.btn.credits") + "\n\n" +
                lang.get("about.dev.title") + ":\n" +
                "• Aissa\n\n" +
                lang.get("about.tech") + ":\n" +
                "• Java SE 21\n" +
                "• FlatLaf 3.7 (Look and Feel)\n" +
                "• MySQL 9.5\n" +
                "• BCrypt (Security)\n\n" +
                "© 2025 AissaGo";

        JOptionPane.showMessageDialog(this, credits, lang.get("about.btn.credits"), JOptionPane.INFORMATION_MESSAGE);
    }

    private void openWebsite() {
        JOptionPane.showMessageDialog(this,
                lang.get("about.btn.website") + ": https://aissago.example.com",
                lang.get("about.btn.website"),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
