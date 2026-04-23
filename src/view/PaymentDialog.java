package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class PaymentDialog extends JDialog {

    private final double amount;
    private final Consumer<Boolean> callback;

    private JTextField cardNumberField;
    private JTextField cardHolderField;
    private JTextField expiryField;
    private JTextField cvvField;
    private JButton payButton;

    public PaymentDialog(JFrame parent, double amount, Consumer<Boolean> callback) {
        super(parent, "Paiement Sécurisé", true);
        this.amount = amount;
        this.callback = callback;
        initializeUI();
    }

    private void initializeUI() {
        setSize(450, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- Visual Credit Card Panel ---
        JPanel cardPreview = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card Gradient (Purple/Blue like modern fintech)
                GradientPaint gp = new GradientPaint(0, 0, new Color(106, 17, 203),
                        getWidth(), getHeight(), new Color(37, 117, 252));
                g2.setPaint(gp);
                g2.fillRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 20, 20);

                // Chip Simulation
                g2.setColor(new Color(255, 215, 0)); // Gold
                g2.fillRoundRect(50, 60, 50, 35, 5, 5);

                // Card Number Placeholder
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Monospaced", Font.BOLD, 22));
                String num = cardNumberField != null && !cardNumberField.getText().isEmpty()
                        ? cardNumberField.getText()
                        : "**** **** **** ****";
                g2.drawString(num, 50, 130);

                // Card Holder & Expiry
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString("Titulaire", 50, 160);
                g2.drawString("Expire", 300, 160);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String name = cardHolderField != null && !cardHolderField.getText().isEmpty()
                        ? cardHolderField.getText().toUpperCase()
                        : "YOUR NAME";
                g2.drawString(name, 50, 180);

                String date = expiryField != null && !expiryField.getText().isEmpty()
                        ? expiryField.getText()
                        : "MM/YY";
                g2.drawString(date, 300, 180);

                // Logo placeholder (Mastercard circles)
                g2.setColor(new Color(255, 95, 0, 200));
                g2.fillOval(320, 50, 40, 40);
                g2.setColor(new Color(255, 180, 0, 200));
                g2.fillOval(350, 50, 40, 40);
            }
        };
        cardPreview.setPreferredSize(new Dimension(450, 220));
        cardPreview.setBackground(Color.WHITE);

        // --- Form Panel ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        formPanel.add(createLabel("Numéro de carte"));
        cardNumberField = createStyledTextField();
        cardNumberField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cardPreview.repaint();
            }
        });
        formPanel.add(cardNumberField);
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(createLabel("Nom du titulaire"));
        cardHolderField = createStyledTextField();
        cardHolderField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cardPreview.repaint();
            }
        });
        formPanel.add(cardHolderField);
        formPanel.add(Box.createVerticalStrut(15));

        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setBackground(Color.WHITE);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(Color.WHITE);
        left.add(createLabel("Date (MM/YY)"), BorderLayout.NORTH);
        expiryField = createStyledTextField();
        expiryField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cardPreview.repaint();
            }
        });
        left.add(expiryField, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(Color.WHITE);
        right.add(createLabel("CVV"), BorderLayout.NORTH);
        cvvField = createStyledTextField();
        right.add(cvvField, BorderLayout.CENTER);

        row.add(left);
        row.add(right);

        formPanel.add(row);
        formPanel.add(Box.createVerticalStrut(30));

        // --- Pay Button ---
        payButton = new JButton("Payer " + String.format("%.2f DZD", amount));
        styleButton(payButton);
        payButton.addActionListener(this::processPayment);
        formPanel.add(payButton);

        add(cardPreview, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    private void processPayment(ActionEvent e) {
        if (cardNumberField.getText().isEmpty() || cvvField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir les informations bancaires.", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simulate Processing
        payButton.setText("Traitement en cours...");
        payButton.setEnabled(false);

        // Simple SwingWorker simulation
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(2000); // Fake network delay
                return null;
            }

            @Override
            protected void done() {
                dispose();
                callback.accept(true);
            }
        }.execute();
    }

    // --- Helpers from your style guide ---

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.GRAY);
        l.setBorder(new EmptyBorder(0, 0, 5, 0));
        return l;
    }

    private JTextField createStyledTextField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Monospaced", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(100, 40));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)));
        return f;
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(46, 204, 113));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 0, 12, 0));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
