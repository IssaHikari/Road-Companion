package view;

import util.AlgeriaLocations;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class LocationSelectorDialog extends JDialog {

    private String selectedLocation = null;
    private JPanel baladiyaContainer;
    private JLabel currentWilayaLabel;

    public LocationSelectorDialog(Window owner) {
        super(owner, "Location Selector", ModalityType.APPLICATION_MODAL);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(800, 500);
        setLocationRelativeTo(getOwner());
        setUndecorated(true);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(63, 81, 181));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Choisir une destination (Wilaya / Baladiya)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton closeBtn = new JButton("X");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // --- Main Content (Split Pane) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);

        // Left: Wilayas List
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(245, 245, 245));

        JPanel wilayaListPanel = new JPanel();
        wilayaListPanel.setLayout(new BoxLayout(wilayaListPanel, BoxLayout.Y_AXIS));
        wilayaListPanel.setBackground(Color.WHITE);

        for (String wilaya : AlgeriaLocations.getWilayas()) {
            JPanel item = createWilayaItem(wilaya);
            wilayaListPanel.add(item);
        }

        JScrollPane leftScroll = new JScrollPane(wilayaListPanel);
        leftScroll.setBorder(null);
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        // Right: Baladiyas Grid
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        // Right Header
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rightHeader.setBackground(new Color(250, 250, 250));
        rightHeader.setBorder(new EmptyBorder(10, 20, 10, 20));
        currentWilayaLabel = new JLabel("Survolez une wilaya...");
        currentWilayaLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        currentWilayaLabel.setForeground(new Color(100, 100, 100));
        rightHeader.add(currentWilayaLabel);
        rightPanel.add(rightHeader, BorderLayout.NORTH);

        // Right Content
        baladiyaContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        baladiyaContainer.setBackground(Color.WHITE);
        baladiyaContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane rightScroll = new JScrollPane(baladiyaContainer);
        rightScroll.setBorder(null);
        rightScroll.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(rightScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createWilayaItem(String wilayaName) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        p.setPreferredSize(new Dimension(300, 50));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel l = new JLabel(wilayaName);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setBorder(new EmptyBorder(0, 20, 0, 0));
        p.add(l, BorderLayout.CENTER);

        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                p.setBackground(new Color(230, 240, 255)); // Light Blue
                l.setForeground(new Color(63, 81, 181));
                updateBaladiyas(wilayaName);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                p.setBackground(Color.WHITE);
                l.setForeground(Color.BLACK);
            }
        });

        return p;
    }

    private void updateBaladiyas(String wilaya) {
        currentWilayaLabel.setText(wilaya);
        currentWilayaLabel.setForeground(new Color(63, 81, 181));
        baladiyaContainer.removeAll();

        List<String> cities = AlgeriaLocations.getBaladiyas(wilaya);
        if (cities.isEmpty()) {
            JLabel empty = new JLabel("(Aucune donnée)");
            empty.setForeground(Color.GRAY);
            baladiyaContainer.add(empty);
        } else {
            for (String city : cities) {
                JButton btn = new JButton(city);
                styleBaladiyaButton(btn);
                btn.addActionListener(e -> {
                    selectedLocation = wilaya + "/" + city;
                    dispose();
                });
                baladiyaContainer.add(btn);
            }
        }

        baladiyaContainer.revalidate();
        baladiyaContainer.repaint();
    }

    private void styleBaladiyaButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(new Color(245, 245, 245));
        btn.setForeground(new Color(50, 50, 50));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(new Color(63, 81, 181));
                btn.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent evt) {
                btn.setBackground(new Color(245, 245, 245));
                btn.setForeground(new Color(50, 50, 50));
            }
        });
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }
}
