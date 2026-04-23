package view.examples;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Exemple de composant utilisant FlatLaf
 * Ce fichier montre comment créer des composants modernes avec FlatLaf
 */
public class FlatLafExamplePanel extends JPanel {

    public FlatLafExamplePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Titre
        JLabel titleLabel = new JLabel("Exemples de composants FlatLaf");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Section Boutons
        contentPanel.add(createSectionLabel("Boutons"));
        contentPanel.add(createButtonsPanel());
        contentPanel.add(Box.createVerticalStrut(20));

        // Section Champs de texte
        contentPanel.add(createSectionLabel("Champs de texte"));
        contentPanel.add(createTextFieldsPanel());
        contentPanel.add(Box.createVerticalStrut(20));

        // Section Composants avancés
        contentPanel.add(createSectionLabel("Composants avancés"));
        contentPanel.add(createAdvancedPanel());

        add(contentPanel, BorderLayout.CENTER);
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(63, 81, 181));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Bouton standard
        JButton standardButton = new JButton("Bouton Standard");
        panel.add(standardButton);

        // Bouton primaire
        JButton primaryButton = new JButton("Bouton Primaire");
        primaryButton.putClientProperty("JButton.buttonType", "roundRect");
        primaryButton.setBackground(new Color(63, 81, 181));
        primaryButton.setForeground(Color.WHITE);
        panel.add(primaryButton);

        // Bouton avec icône
        JButton iconButton = new JButton("Avec Icône", UIManager.getIcon("Tree.closedIcon"));
        panel.add(iconButton);

        // Bouton désactivé
        JButton disabledButton = new JButton("Désactivé");
        disabledButton.setEnabled(false);
        panel.add(disabledButton);

        return panel;
    }

    private JPanel createTextFieldsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Champ de texte standard
        JTextField textField = new JTextField("Champ de texte standard");
        textField.setMaximumSize(new Dimension(300, 35));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(textField);
        panel.add(Box.createVerticalStrut(10));

        // Champ de texte avec placeholder
        JTextField placeholderField = new JTextField();
        placeholderField.putClientProperty("JTextField.placeholderText", "Entrez votre texte ici...");
        placeholderField.setMaximumSize(new Dimension(300, 35));
        placeholderField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(placeholderField);
        panel.add(Box.createVerticalStrut(10));

        // Champ de mot de passe
        JPasswordField passwordField = new JPasswordField();
        passwordField.putClientProperty("JTextField.placeholderText", "Mot de passe");
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passwordField);

        return panel;
    }

    private JPanel createAdvancedPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ComboBox
        String[] items = { "Option 1", "Option 2", "Option 3" };
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setMaximumSize(new Dimension(300, 35));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(comboBox);
        panel.add(Box.createVerticalStrut(10));

        // CheckBox
        JCheckBox checkBox = new JCheckBox("Case à cocher avec FlatLaf");
        checkBox.setBackground(Color.WHITE);
        checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(checkBox);
        panel.add(Box.createVerticalStrut(10));

        // RadioButtons
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ButtonGroup group = new ButtonGroup();

        JRadioButton radio1 = new JRadioButton("Option A", true);
        JRadioButton radio2 = new JRadioButton("Option B");
        radio1.setBackground(Color.WHITE);
        radio2.setBackground(Color.WHITE);

        group.add(radio1);
        group.add(radio2);
        radioPanel.add(radio1);
        radioPanel.add(radio2);
        panel.add(radioPanel);
        panel.add(Box.createVerticalStrut(10));

        // Slider
        JSlider slider = new JSlider(0, 100, 50);
        slider.setMaximumSize(new Dimension(300, 50));
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);
        slider.setBackground(Color.WHITE);
        panel.add(slider);

        return panel;
    }

    /**
     * Méthode de test pour afficher ce panneau
     */
    public static void main(String[] args) {
        // Note: FlatLaf doit être initialisé dans AissaGoApp.java
        // Cette méthode est juste pour tester le panneau isolément

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Exemples FlatLaf");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new FlatLafExamplePanel());
            frame.setVisible(true);
        });
    }
}
