package view.settings;

import model.User;
import util.LanguageManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panneau des paramètres de paiement
 */
public class PaymentSettingsPanel extends JPanel {

    private final User currentUser;
    private final LanguageManager lang;
    private static final Color BRAND_COLOR = new Color(63, 81, 181);

    private JRadioButton cardRadio;
    private JRadioButton cashRadio;
    private JTable paymentHistoryTable;

    public PaymentSettingsPanel(User user) {
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

        contentPanel.add(createPaymentMethodSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createPaymentHistorySection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createActionButtons());

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createPaymentMethodSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("payment.method.title")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel radioPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        ButtonGroup group = new ButtonGroup();

        cardRadio = new JRadioButton(lang.get("payment.card"));
        cardRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardRadio.setBackground(Color.WHITE);
        cardRadio.setSelected(true);
        if (lang.isArabic())
            cardRadio.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        cashRadio = new JRadioButton(lang.get("payment.cash"));
        cashRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cashRadio.setBackground(Color.WHITE);
        if (lang.isArabic())
            cashRadio.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        group.add(cardRadio);
        group.add(cashRadio);

        radioPanel.add(cardRadio);
        radioPanel.add(cashRadio);

        JButton testPaymentBtn = new JButton(lang.get("payment.btn.test"));
        styleButton(testPaymentBtn, false);
        testPaymentBtn.addActionListener(e -> testPayment());
        radioPanel.add(testPaymentBtn);

        panel.add(radioPanel);
        return panel;
    }

    private JPanel createPaymentHistorySection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createSectionBorder(lang.get("payment.history.title")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        if (lang.isArabic())
            panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        String[] columns = {
                lang.get("payment.history.col.date"),
                lang.get("payment.history.col.amount"),
                lang.get("payment.history.col.method"),
                lang.get("payment.history.col.status")
        };

        if (lang.isArabic()) {
            // Reverse columns for RTL table
            String[] reversedColumns = new String[columns.length];
            for (int i = 0; i < columns.length; i++)
                reversedColumns[i] = columns[columns.length - 1 - i];
            columns = reversedColumns;
        }

        Object[][] data = {
                { "2025-12-10", "500 DZD", "Carte", "✅ Payé" },
                { "2025-12-08", "750 DZD", "Espèces", "✅ Payé" },
                { "2025-12-05", "600 DZD", "Carte", "✅ Payé" }
        };

        if (lang.isArabic()) {
            // Reverse data for RTL table
            Object[][] reversedData = new Object[data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    reversedData[i][j] = data[i][data[i].length - 1 - j];
                }
            }
            data = reversedData;
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentHistoryTable = new JTable(model);
        paymentHistoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentHistoryTable.setRowHeight(30);
        paymentHistoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        if (lang.isArabic()) {
            paymentHistoryTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.RIGHT);
            paymentHistoryTable.setDefaultRenderer(Object.class, centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(paymentHistoryTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        if (lang.isArabic())
            scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        panel.add(scrollPane, BorderLayout.CENTER);
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

    private void testPayment() {
        String method = cardRadio.isSelected() ? lang.get("payment.card") : lang.get("payment.cash");
        JOptionPane.showMessageDialog(this,
                lang.get("payment.btn.test") + ": " + method + "\n\n" + lang.get("payment.test.success"),
                lang.get("payment.btn.test"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveSettings() {
        JOptionPane.showMessageDialog(this, lang.get("payment.save.success"), lang.get("dialog.success"),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
