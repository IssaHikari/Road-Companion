package view.admin;

import dao.UserDAO;
import model.User;
import util.IconManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.util.List;

import view.settings.*;
import view.LoginFrame;

public class AdminDashboard extends JFrame {

    private final User adminUser;
    private final UserDAO userDAO;
    private final dao.ReportDAO reportDAO;
    private CardLayout cardLayout;
    private JPanel contentCards;

    // Legacy fields kept for compatibility with existing methods, though they might
    // move
    private JTable driversTable;
    private DefaultTableModel tableModel;

    // Colors - Professional Palette
    private final Color PRIMARY_COLOR = new Color(52, 152, 219); // Blue
    private final Color SIDEBAR_COLOR = new Color(33, 47, 61); // Dark Blue/Grey
    private final Color CONTENT_BG = new Color(240, 242, 245); // Light Grey
    private final Color HEADER_BG = Color.WHITE;
    private final Color TEXT_COLOR = new Color(44, 62, 80);

    public AdminDashboard(User adminUser) {
        this.adminUser = adminUser;
        this.userDAO = new UserDAO();
        this.reportDAO = new dao.ReportDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("AissaGo - Panneau d'administration");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main Content Area
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(CONTENT_BG);

        // Header
        JPanel header = createHeader();
        mainContent.add(header, BorderLayout.NORTH);

        // Card Container
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(CONTENT_BG);
        contentCards.setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. Dashboard Panel
        JPanel dashboardPanel = createDashboardPanel();
        contentCards.add(dashboardPanel, "DASHBOARD");

        // 2. Users Panel (Placeholder for now, will be implemented next step)
        JPanel usersPanel = createUsersManagementPanel();
        contentCards.add(usersPanel, "USERS");

        // 3. Reports Panel (Placeholder for now)
        JPanel reportsPanel = createReportsPanel();
        contentCards.add(reportsPanel, "REPORTS");

        // 4. Settings Panels
        contentCards.add(new AccountSettingsPanel(adminUser), "SETTINGS_ACCOUNT");
        contentCards.add(new AppearanceSettingsPanel(adminUser), "SETTINGS_APPEARANCE");
        contentCards.add(new NotificationsSettingsPanel(adminUser), "SETTINGS_NOTIFICATIONS");
        contentCards.add(new PrivacySettingsPanel(adminUser), "SETTINGS_PRIVACY");
        contentCards.add(new AboutPanel(), "SETTINGS_ABOUT");

        mainContent.add(contentCards, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Initial Data Load (Dashboard)
        loadData();
    }

    private JPanel settingsMenuContainer;
    private boolean isSettingsExpanded = false;

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBorder(new EmptyBorder(20, 0, 0, 0));

        // App Logo/Title
        JLabel title = new JLabel("AissaGo Admin");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);

        sidebar.add(Box.createVerticalStrut(50));

        // Menu Items
        sidebar.add(createSidebarButton("Tableau de bord", "maison.png", "DASHBOARD"));
        sidebar.add(createSidebarButton("Gestion Utilisateurs", "information.png", "USERS"));
        sidebar.add(createSidebarButton("Signalements", "attention.png", "REPORTS"));

        // Settings Toggle Button
        sidebar.add(createSettingsToggleButton());

        // Settings Sub-menu Container (Hidden by default)
        settingsMenuContainer = new JPanel();
        settingsMenuContainer.setLayout(new BoxLayout(settingsMenuContainer, BoxLayout.Y_AXIS));
        settingsMenuContainer.setBackground(SIDEBAR_COLOR);
        settingsMenuContainer.setVisible(false); // Initially hidden

        // Add sub-items
        settingsMenuContainer.add(createSubMenuButton("Compte", "SETTINGS_ACCOUNT"));
        settingsMenuContainer.add(createSubMenuButton("Apparence", "SETTINGS_APPEARANCE"));
        settingsMenuContainer.add(createSubMenuButton("Notifications", "SETTINGS_NOTIFICATIONS"));
        settingsMenuContainer.add(createSubMenuButton("Confidentialité", "SETTINGS_PRIVACY"));
        settingsMenuContainer.add(createSubMenuButton("À propos", "SETTINGS_ABOUT"));

        sidebar.add(settingsMenuContainer);

        sidebar.add(Box.createVerticalGlue());

        // Logout
        JButton logoutBtn = new JButton("Déconnexion");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setMaximumSize(new Dimension(220, 45));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(30));

        return sidebar;
    }

    private JPanel createSidebarButton(String text, String iconName, String cardName) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(260, 50));
        item.setBackground(SIDEBAR_COLOR);
        item.setBorder(new EmptyBorder(10, 25, 10, 20));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        try {
            ImageIcon icon = IconManager.getIcon(iconName, 20, 20);
            if (icon != null) {
                label.setIcon(icon);
                label.setIconTextGap(15);
            }
        } catch (Exception e) {
        }

        item.add(label, BorderLayout.CENTER);

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(contentCards, cardName);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(44, 62, 80));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(SIDEBAR_COLOR);
            }
        });

        return item;
    }

    private JPanel createSettingsToggleButton() {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(260, 50));
        item.setBackground(SIDEBAR_COLOR);
        item.setBorder(new EmptyBorder(10, 25, 10, 20));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel("Paramètres");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        try {
            ImageIcon icon = IconManager.getIcon("configuration.png", 20, 20);
            if (icon != null) {
                label.setIcon(icon);
                label.setIconTextGap(15);
            }
        } catch (Exception e) {
        }

        // Add a small arrow indicator
        JLabel arrow = new JLabel("▼");
        arrow.setForeground(Color.WHITE);
        arrow.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        item.add(label, BorderLayout.CENTER);
        item.add(arrow, BorderLayout.EAST);

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                isSettingsExpanded = !isSettingsExpanded;
                settingsMenuContainer.setVisible(isSettingsExpanded);
                arrow.setText(isSettingsExpanded ? "▲" : "▼");
                item.revalidate();
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(44, 62, 80));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(SIDEBAR_COLOR);
            }
        });

        return item;
    }

    private JPanel createSubMenuButton(String text, String cardName) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(260, 40));
        item.setBackground(SIDEBAR_COLOR); // Slightly different shade could be nice, keeping same for now
        // Increased left padding for indentation
        item.setBorder(new EmptyBorder(10, 60, 10, 20));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setForeground(new Color(200, 200, 200)); // Slightly dimmed text for sub-items
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        item.add(label, BorderLayout.CENTER);

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(contentCards, cardName);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label.setForeground(Color.WHITE);
                item.setBackground(new Color(44, 62, 80));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                label.setForeground(new Color(200, 200, 200));
                item.setBackground(SIDEBAR_COLOR);
            }
        });

        return item;
    }

    // --- DASHBOARD VIEW ---
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        // Use the CreateStatsPanel and CreateTablePanel methods that already exist in
        // the class
        panel.add(createStatsPanel(), BorderLayout.NORTH);
        panel.add(createTablePanel(), BorderLayout.CENTER);
        return panel;
    }

    // --- USERS MANAGEMENT VIEW ---
    private JPanel createUsersManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        // Header with Search
        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolsPanel.setOpaque(false);

        JComboBox<String> roleCombo = new JComboBox<>(new String[] { "DRIVER", "PASSENGER" });
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Rechercher");
        searchBtn.setBackground(PRIMARY_COLOR);
        searchBtn.setForeground(Color.WHITE);

        toolsPanel.add(new JLabel("Role: "));
        toolsPanel.add(roleCombo);
        toolsPanel.add(new JLabel(" Recherche: "));
        toolsPanel.add(searchField);
        toolsPanel.add(searchBtn);

        panel.add(toolsPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Nom", "Email", "Role", "Action" };
        DefaultTableModel userModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Action button
            }
        };
        JTable userTable = new JTable(userModel);
        userTable.setRowHeight(40);

        // Custom Delete Action
        userTable.getColumnModel().getColumn(4)
                .setCellRenderer(new ButtonRenderer("Supprimer", new Color(231, 76, 60)));
        userTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JButton btn = new JButton("Supprimer");
            {
                btn.setBackground(new Color(231, 76, 60));
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> {
                    fireEditingStopped();
                    int row = userTable.getSelectedRow();
                    if (row >= 0) {
                        int userId = (int) userModel.getValueAt(row, 0);
                        int confirm = JOptionPane.showConfirmDialog(panel,
                                "Voulez-vous vraiment supprimer cet utilisateur ?", "Confirmation",
                                JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                userDAO.deleteUser(userId);
                                ((DefaultTableModel) userTable.getModel()).removeRow(row);
                                JOptionPane.showMessageDialog(panel, "Utilisateur supprimé.");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                    int column) {
                return btn;
            }

            @Override
            public Object getCellEditorValue() {
                return "Supprimer";
            }
        });

        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Search Logic
        searchBtn.addActionListener(e -> {
            try {
                String role = (String) roleCombo.getSelectedItem();
                String query = searchField.getText();
                List<User> users = userDAO.searchUsersByRole(role, query);
                userModel.setRowCount(0);
                for (User u : users) {
                    userModel.addRow(
                            new Object[] { u.getUserId(), u.getName(), u.getEmail(), u.getRole(), "Supprimer" });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return panel;
    }

    // --- REPORTS VIEW ---
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        // Columns: Added Date
        String[] columns = { "ID", "Signalé par", "Contre", "Date", "Motif", "Statut", "Action" };
        DefaultTableModel reportModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 6; // Action column
            }
        };
        JTable reportTable = new JTable(reportModel);
        reportTable.setRowHeight(50);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        reportTable.getTableHeader().setBackground(new Color(248, 249, 250));

        // Column Widths
        reportTable.getColumnModel().getColumn(0).setMaxWidth(50); // ID
        reportTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Date
        reportTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Reason

        // Custom Action Renderer (View Details)
        // Using "Voir Détails" as the button label
        reportTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer("Détails", PRIMARY_COLOR));
        reportTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JButton btn = new JButton("Détails");
            {
                btn.setBackground(PRIMARY_COLOR);
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> {
                    fireEditingStopped();
                    int row = reportTable.getSelectedRow();
                    if (row >= 0) {
                        int reportId = (int) reportModel.getValueAt(row, 0);
                        String reporter = (String) reportModel.getValueAt(row, 1);
                        String reported = (String) reportModel.getValueAt(row, 2);
                        String fullReason = (String) reportModel.getValueAt(row, 4); // We store full reason here
                                                                                     // actually
                        showReportDetailsDialog(reportId, reporter, reported, fullReason);
                    }
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                    int column) {
                return btn;
            }

            @Override
            public Object getCellEditorValue() {
                return "Détails";
            }
        });

        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        JButton loadBtn = new JButton("Actualiser");
        loadBtn.setBackground(PRIMARY_COLOR);
        loadBtn.setForeground(Color.WHITE);
        loadBtn.addActionListener(e -> loadReports(reportModel));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);
        headerPanel.add(loadBtn);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Auto-load when visible
        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadReports(reportModel);
            }
        });

        // Initial load
        loadReports(reportModel);

        return panel;
    }

    private void loadReports(DefaultTableModel model) {
        try {
            model.setRowCount(0);
            java.util.List<model.Report> reports = reportDAO.getAllReports();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm");

            for (model.Report r : reports) {
                String dateStr = r.getCreatedAt() != null ? r.getCreatedAt().format(formatter) : "-";

                model.addRow(new Object[] {
                        r.getId(),
                        r.getReporterName() + " (ID: " + r.getReporterId() + ")",
                        r.getReportedUserName() + " (ID: " + r.getReportedUserId() + ")",
                        dateStr,
                        r.getReason(),
                        r.getStatus(),
                        "Détails"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showReportDetailsDialog(int reportId, String reporter, String reported, String fullReason) {
        JDialog dialog = new JDialog(this, "Détails du Signalement #" + reportId, true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(25, 25, 25, 25));
        content.setBackground(Color.WHITE);

        JLabel title = new JLabel("Détails du Signalement");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        // Info block
        content.add(createDetailLabel("De (Passager):", reporter));
        content.add(Box.createVerticalStrut(10));
        content.add(createDetailLabel("Contre (Conducteur):", reported));
        content.add(Box.createVerticalStrut(20));

        JLabel reasonTitle = new JLabel("Motif du signalement:");
        reasonTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reasonTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(reasonTitle);
        content.add(Box.createVerticalStrut(10));

        JTextArea reasonArea = new JTextArea(fullReason);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setEditable(false);
        reasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reasonArea.setBackground(new Color(250, 250, 252));
        reasonArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JScrollPane scroll = new JScrollPane(reasonArea);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(scroll);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);

        JButton deleteBtn = new JButton("Supprimer le signalement");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Supprimer ce signalement ?\nCette action est irréversible.", "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    reportDAO.deleteReport(reportId);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Signalement supprimé avec succès.");
                    // The table will auto-refresh when focusing back? No, we need to manually
                    // refresh or just wait for next load.
                    // Ideally we should trigger a refresh here, but loadReports requires reference
                    // to model which we don't strictly have in this scope easily without passing
                    // it.
                    // But componentShown will trigger on tab switch.
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton closeBtn = new JButton("Fermer");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        closeBtn.addActionListener(e -> dialog.dispose());

        footer.add(deleteBtn);
        footer.add(closeBtn);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private Component createDetailLabel(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(1000, 25));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(Color.GRAY);

        JLabel v = new JLabel(" " + value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        v.setForeground(Color.BLACK);

        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel pageTitle = new JLabel("Vérification des Chauffeurs");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitle.setForeground(TEXT_COLOR);
        pageTitle.setBorder(new EmptyBorder(0, 30, 0, 0));

        JButton refreshBtn = new JButton("Actualiser");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshBtn.setBackground(PRIMARY_COLOR);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorder(new EmptyBorder(5, 15, 5, 15));
        refreshBtn.addActionListener(e -> loadData());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        // Add User Avatar or Name here if needed
        JLabel userLabel = new JLabel("Admin: " + adminUser.getName());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // userLabel.setBorder(new EmptyBorder(0, 10, 0, 20));
        rightPanel.add(userLabel);

        header.add(pageTitle, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 30, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 140));

        // Using available icons: information, ouvrir-le-courrier, verrouiller,
        // traverser
        panel.add(createStatCard("Total Chauffeurs", "150", new Color(46, 204, 113), "information.png"));
        panel.add(createStatCard("En Attente", "--", new Color(243, 156, 18), "ouvrir-le-courrier.png"));
        panel.add(createStatCard("Vérifiés", "120", PRIMARY_COLOR, "verrouiller.png")); // Trusted/Locked
        panel.add(createStatCard("Rejetés", "15", new Color(231, 76, 60), "traverser.png")); // Crossed out

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color, String iconName) {
        // Custom Rounded Panel with Drop Shadow simulation
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);

                // Background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 15, 15);

                // Left Accent Bar
                g2.setColor(color);
                g2.fillRoundRect(0, 0, 8, getHeight() - 5, 15, 15);
                // Cover the rounded right corners of the bar to make it flush
                g2.fillRect(4, 0, 4, getHeight() - 5);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 15));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bolder font
        titleLbl.setForeground(new Color(127, 140, 141));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36)); // Larger text
        valueLbl.setForeground(new Color(44, 62, 80));
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Use ID to update 'Pending' count dynamically if needed later
        // simplified here

        content.add(titleLbl);
        content.add(Box.createVerticalStrut(8));
        content.add(valueLbl);

        card.add(content, BorderLayout.CENTER);

        // Icon
        ImageIcon icon = IconManager.getIcon(iconName, 50, 50); // Bigger icon
        if (icon != null) {
            JLabel iconLbl = new JLabel(icon);
            iconLbl.setVerticalAlignment(SwingConstants.TOP);
            card.add(iconLbl, BorderLayout.EAST);
        }

        return card;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Demandes d'inscription récentes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = { "ID", "Nom", "Email", "Véhicule", "Matricule", "Statut", "Action" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Action button editable
            }
        };

        driversTable = new JTable(tableModel);
        driversTable.setRowHeight(50);
        driversTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        driversTable.setShowVerticalLines(false);
        driversTable.setGridColor(new Color(230, 230, 230));
        driversTable.setSelectionBackground(new Color(232, 246, 255));
        driversTable.setSelectionForeground(TEXT_COLOR);

        // Header Style
        JTableHeader header = driversTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(100, 100, 100));
        header.setPreferredSize(new Dimension(0, 45));

        // Custom Renderers
        driversTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        // Action Button
        driversTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        driversTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Column widths
        driversTable.getColumnModel().getColumn(0).setMaxWidth(50);
        driversTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        driversTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(driversTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            List<User> drivers = userDAO.getPendingDrivers();
            // Optional: update stats card "Pending" here based on drivers.size()

            for (User u : drivers) {
                tableModel.addRow(new Object[] {
                        u.getUserId(),
                        u.getName(),
                        u.getEmail(),
                        u.getVehicleModel() + " (" + u.getVehicleColor() + ")",
                        u.getVehiclePlate(),
                        u.getVerificationStatus(),
                        "Vérifier"
                });
            }
            if (drivers.isEmpty()) {
                // Could show a "No Pending Drivers" message or rows
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showVerificationDialog(int userId) {
        try {
            User driver = userDAO.getUserById(userId);
            if (driver == null)
                return;

            JDialog dialog = new JDialog(this, "Détails du Chauffeur", true);
            dialog.setSize(900, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            dialog.getContentPane().setBackground(CONTENT_BG);

            // Header for Dialog
            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
            header.setBackground(Color.WHITE);
            header.setBorder(new EmptyBorder(15, 20, 15, 20));
            JLabel title = new JLabel("Validation du Chauffeur: " + driver.getName());
            title.setFont(new Font("Segoe UI", Font.BOLD, 20));
            header.add(title);
            dialog.add(header, BorderLayout.NORTH);

            // Content
            JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
            content.setBorder(new EmptyBorder(20, 20, 20, 20));
            content.setBackground(CONTENT_BG);

            // Left: Info
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

            addDetailRow(infoPanel, "Email", driver.getEmail());
            addDetailRow(infoPanel, "Téléphone", driver.getPhoneNumber());
            addDetailRow(infoPanel, "Véhicule", driver.getVehicleModel());
            addDetailRow(infoPanel, "Couleur", driver.getVehicleColor());
            addDetailRow(infoPanel, "Matricule", driver.getVehiclePlate());
            addDetailRow(infoPanel, "Statut Actuel", driver.getVerificationStatus());

            // Right: License Image
            JPanel imagePanel = new JPanel(new BorderLayout());
            imagePanel.setBackground(Color.WHITE);
            imagePanel.setBorder(BorderFactory.createTitledBorder("Permis de Conduire"));

            JLabel imageLabel = new JLabel("Aucune image disponible", SwingConstants.CENTER);
            if (driver.getLicensePath() != null && new File(driver.getLicensePath()).exists()) {
                ImageIcon icon = new ImageIcon(driver.getLicensePath());
                // Scale efficiently
                Image img = icon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                imageLabel.setText("");
                imageLabel.setIcon(new ImageIcon(img));
            }
            imagePanel.add(imageLabel, BorderLayout.CENTER);

            content.add(infoPanel);
            content.add(imagePanel);
            dialog.add(content, BorderLayout.CENTER);

            // Footer Buttons
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
            footer.setBackground(Color.WHITE);

            JButton approveBtn = new JButton("APPROUVER");
            approveBtn.setBackground(new Color(46, 204, 113));
            approveBtn.setForeground(Color.WHITE);
            approveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            approveBtn.setPreferredSize(new Dimension(150, 45));
            approveBtn.setFocusPainted(false);

            JButton rejectBtn = new JButton("REJETER");
            rejectBtn.setBackground(new Color(231, 76, 60));
            rejectBtn.setForeground(Color.WHITE);
            rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            rejectBtn.setPreferredSize(new Dimension(150, 45));
            rejectBtn.setFocusPainted(false);

            // Actions - Reusing previous logic
            approveBtn.addActionListener(e -> {
                try {
                    if ("VERIFIED".equals(driver.getVerificationStatus())) {
                        JOptionPane.showMessageDialog(dialog, "Le chauffeur est déjà vérifié !", "Info",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    userDAO.updateVerificationStatus(userId, "VERIFIED");
                    JOptionPane.showMessageDialog(dialog, "Chauffeur approuvé avec succès !");
                    dialog.dispose();
                    loadData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            rejectBtn.addActionListener(e -> {
                try {
                    userDAO.updateVerificationStatus(userId, "REJECTED");
                    JOptionPane.showMessageDialog(dialog, "Chauffeur rejeté.");
                    dialog.dispose();
                    loadData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            footer.add(rejectBtn);
            footer.add(approveBtn);
            dialog.add(footer, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        row.setBackground(Color.WHITE);
        row.setOpaque(true);
        row.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.GRAY);

        JLabel val = new JLabel(value == null ? "-" : value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        val.setForeground(TEXT_COLOR);

        row.add(lbl, BorderLayout.NORTH);
        row.add(val, BorderLayout.CENTER);

        panel.add(row);
        panel.add(Box.createVerticalStrut(10));
    }

    // --- Renderers & Editors ---

    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if ("VERIFIED".equals(value)) {
                l.setForeground(new Color(46, 204, 113)); // Green
                l.setIcon(IconManager.getIcon("verrouiller.png", 16, 16));
            } else if ("PENDING".equals(value)) {
                l.setForeground(new Color(243, 156, 18)); // Orange
                l.setIcon(IconManager.getIcon("ouvrir-le-courrier.png", 16, 16));
            } else {
                l.setForeground(new Color(231, 76, 60)); // Red
                l.setIcon(IconManager.getIcon("traverser.png", 16, 16));
            }
            l.setFont(l.getFont().deriveFont(Font.BOLD));

            return l;
        }
    }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {

        public ButtonRenderer() {
            this("Examiner", new Color(52, 152, 219)); // Default PRIMARY_COLOR
            setIcon(IconManager.getIcon("chercher.png", 16, 16));
        }

        public ButtonRenderer(String label, Color bg) {
            setOpaque(true);
            setText(label);
            setBackground(bg);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (isSelected) {
                setBackground(getBackground().darker());
            } else {
                // Determine color based on usage? Actually constructing it with specific color
                // handles this
                // but if using default, we might want to reset? To keep it simple, we trust the
                // constructor color.
                // Re-apply the background color stored in the instance (since Swing reuses
                // renderers)
                // However, the instance is created per-column in my usage above, so it holds
                // the color.
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            label = (value == null) ? "Examiner" : value.toString();
            button.setText(label);
            isPushed = true;
            selectedRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                try {
                    int userId = (int) tableModel.getValueAt(selectedRow, 0);
                    // Open dialog later to allow the table to finish editing state cleanly
                    SwingUtilities.invokeLater(() -> showVerificationDialog(userId));
                } catch (Exception e) {
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
