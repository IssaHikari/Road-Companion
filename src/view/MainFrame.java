package view;

import model.User;
import util.LanguageManager;
import util.IconManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import view.settings.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MainFrame extends JFrame {

    private final User currentUser;
    private final LanguageManager lang;
    private JPanel mainContainer;
    private JPanel settingsMenuContainer;
    private boolean isSettingsExpanded = false;

    private Image cachedAvatarImage;

    // Design Colors - Matching AdminDashboard
    private static final Color SIDEBAR_COLOR = new Color(33, 47, 61); // Dark Blue/Grey
    private static final Color BG_COLOR = new Color(240, 242, 245); // Light Grey
    private static final Color HOVER_COLOR = new Color(44, 62, 80);
    private static final Color TEXT_COLOR_MAIN = Color.WHITE;
    private static final Color TEXT_COLOR_SUB = new Color(200, 200, 200);

    public MainFrame(User user) {
        this.currentUser = user;
        this.lang = LanguageManager.getInstance();

        // Pre-load avatar to avoid lag in paintComponent
        loadAvatarImage();

        setTitle("AissaGo - " + lang.get("menu.welcome") + " " + user.getName());

        // Setup Frame
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // Handle Orientation
        if (lang.isArabic()) {
            this.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        // --- 1. Sidebar ---
        if (lang.isArabic()) {
            add(createSidebar(), BorderLayout.EAST);
        } else {
            add(createSidebar(), BorderLayout.WEST);
        }

        // --- 2. Main Content (Center) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(true);
        centerPanel.setBackground(BG_COLOR);

        if (lang.isArabic())
            centerPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Navbar (North of Center)
        centerPanel.add(createNavBar(), BorderLayout.NORTH);

        // Dynamic Content Area
        mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        if (lang.isArabic())
            mainContainer.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Load Default Dashboard based on role
        String defaultCommand = currentUser.getRole().equals("DRIVER") ? "CREATE_TRIP" : "SEARCH_TRIPS";
        handleMenuAction(defaultCommand);

        centerPanel.add(mainContainer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loadAvatarImage() {
        if (currentUser.getAvatarPath() != null && new File(currentUser.getAvatarPath()).exists()) {
            try {
                cachedAvatarImage = ImageIO.read(new File(currentUser.getAvatarPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // =========================================================================
    // SIDEBAR
    // =========================================================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 0, 20, 0)); // No side padding here, handled by buttons

        if (lang.isArabic())
            sidebar.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Logo
        JLabel logoText = new JLabel("AissaGo");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoText.setForeground(Color.WHITE);
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoText);

        sidebar.add(Box.createVerticalStrut(50));

        // Menu Items using IconManager
        sidebar.add(createMenuItem(lang.get("menu.search"), "SEARCH_TRIPS", "chercher.png"));

        if ("DRIVER".equals(currentUser.getRole())) {
            sidebar.add(createMenuItem(lang.get("menu.publish"), "CREATE_TRIP", "menu-burger.png"));
            sidebar.add(createMenuItem(lang.get("menu.my_trips"), "MY_TRIPS", "la-communication.png"));
            sidebar.add(createMenuItem(lang.get("menu.requests"), "DRIVER_BOOKINGS", "information.png"));
        } else {
            sidebar.add(createMenuItem(lang.get("menu.my_bookings"), "MY_BOOKINGS", "carte-de-credit.png"));
        }

        // Add Leaderboard for everyone
        sidebar.add(createMenuItem("Classement", "LEADERBOARD", "tasse.png")); // "tasse" (cup) or "ranking"

        if (currentUser.isIsAdmin()) {
            sidebar.add(createMenuItem("Admin Panel", "ADMIN_DASHBOARD", "verrouiller.png"));
        }

        // Settings Toggle Button
        sidebar.add(createSettingsToggleButton());

        // Settings Sub-menu Container
        settingsMenuContainer = new JPanel();
        settingsMenuContainer.setLayout(new BoxLayout(settingsMenuContainer, BoxLayout.Y_AXIS));
        settingsMenuContainer.setBackground(SIDEBAR_COLOR);
        settingsMenuContainer.setVisible(false);

        // Add sub-items
        settingsMenuContainer.add(createSubMenuButton("Compte", "SETTINGS_ACCOUNT"));
        settingsMenuContainer.add(createSubMenuButton("Apparence", "SETTINGS_APPEARANCE"));
        settingsMenuContainer.add(createSubMenuButton("Notifications", "SETTINGS_NOTIFICATIONS"));
        settingsMenuContainer.add(createSubMenuButton("Confidentialité", "SETTINGS_PRIVACY"));
        settingsMenuContainer.add(createSubMenuButton("À propos", "SETTINGS_ABOUT"));

        sidebar.add(settingsMenuContainer);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createMenuItem(lang.get("menu.logout"), "LOGOUT", "sortir.png"));

        return sidebar;
    }

    private JPanel createMenuItem(String text, String command, String iconName) {
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setMaximumSize(new Dimension(280, 50));
        btnPanel.setBackground(SIDEBAR_COLOR);
        btnPanel.setBorder(new EmptyBorder(10, 25, 10, 20)); // Consistent padding
        btnPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel info = new JLabel(text);
        info.setFont(new Font("Segoe UI", Font.BOLD, 15));
        info.setForeground(TEXT_COLOR_MAIN);

        ImageIcon icon = IconManager.getIcon(iconName, 20, 20); // Slightly smaller for professional look
        if (icon != null) {
            info.setIcon(icon);
            info.setIconTextGap(15);
        }

        if (lang.isArabic()) {
            info.setHorizontalAlignment(SwingConstants.RIGHT);
            info.setHorizontalTextPosition(SwingConstants.LEFT);
        }

        btnPanel.add(info, BorderLayout.CENTER);

        // Interaction
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnPanel.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnPanel.setBackground(SIDEBAR_COLOR);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMenuAction(command);
            }
        };

        btnPanel.addMouseListener(ma);
        return btnPanel;
    }

    private JPanel createSettingsToggleButton() {
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setMaximumSize(new Dimension(280, 50));
        btnPanel.setBackground(SIDEBAR_COLOR);
        btnPanel.setBorder(new EmptyBorder(10, 25, 10, 20));
        btnPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel info = new JLabel(lang.get("menu.settings"));
        info.setFont(new Font("Segoe UI", Font.BOLD, 15));
        info.setForeground(TEXT_COLOR_MAIN);

        ImageIcon icon = IconManager.getIcon("configuration.png", 20, 20);
        if (icon != null) {
            info.setIcon(icon);
            info.setIconTextGap(15);
        }

        JLabel arrow = new JLabel("▼");
        arrow.setForeground(TEXT_COLOR_MAIN);
        arrow.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        if (lang.isArabic()) {
            info.setHorizontalAlignment(SwingConstants.RIGHT);
            info.setHorizontalTextPosition(SwingConstants.LEFT);
            btnPanel.add(arrow, BorderLayout.WEST);
        } else {
            btnPanel.add(arrow, BorderLayout.EAST);
        }

        btnPanel.add(info, BorderLayout.CENTER);

        // Interaction
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnPanel.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnPanel.setBackground(SIDEBAR_COLOR);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                isSettingsExpanded = !isSettingsExpanded;
                settingsMenuContainer.setVisible(isSettingsExpanded);
                arrow.setText(isSettingsExpanded ? "▲" : "▼");
                settingsMenuContainer.revalidate();
            }
        };
        btnPanel.addMouseListener(ma);

        return btnPanel;
    }

    private JPanel createSubMenuButton(String text, String command) {
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setMaximumSize(new Dimension(280, 40));
        btnPanel.setBackground(SIDEBAR_COLOR);

        // Indentation
        int defaultPad = 20;
        int indentPad = 60;

        if (lang.isArabic()) {
            btnPanel.setBorder(new EmptyBorder(10, defaultPad, 10, indentPad));
        } else {
            btnPanel.setBorder(new EmptyBorder(10, indentPad, 10, defaultPad));
        }

        btnPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel info = new JLabel(text);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setForeground(TEXT_COLOR_SUB);

        if (lang.isArabic()) {
            info.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        btnPanel.add(info, BorderLayout.CENTER);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnPanel.setBackground(HOVER_COLOR);
                info.setForeground(TEXT_COLOR_MAIN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnPanel.setBackground(SIDEBAR_COLOR);
                info.setForeground(TEXT_COLOR_SUB);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMenuAction(command);
            }
        };
        btnPanel.addMouseListener(ma);

        return btnPanel;
    }

    private void handleMenuAction(String command) {
        if ("LOGOUT".equals(command)) {
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        JPanel panelToShow = null;

        switch (command) {
            case "SEARCH_TRIPS":
                panelToShow = new SearchTripPanel(currentUser);
                break;
            case "CREATE_TRIP":
                panelToShow = new CreateTripPanel(currentUser);
                break;
            case "MY_TRIPS":
                panelToShow = new DriverTripsPanel(currentUser);
                break;
            case "DRIVER_BOOKINGS":
                panelToShow = new DriverBookingsPanel(currentUser);
                break;
            case "MY_BOOKINGS":
                panelToShow = new PassengerBookingsPanel(currentUser);
                break;
            case "LEADERBOARD":
                panelToShow = new LeaderboardPanel(currentUser);
                break;
            case "SETTINGS":
                panelToShow = new view.settings.SettingsPanel(currentUser);
                break;
            case "SETTINGS_ACCOUNT":
                panelToShow = new AccountSettingsPanel(currentUser);
                break;
            case "SETTINGS_APPEARANCE":
                panelToShow = new AppearanceSettingsPanel(currentUser);
                break;
            case "SETTINGS_NOTIFICATIONS":
                panelToShow = new NotificationsSettingsPanel(currentUser);
                break;
            case "SETTINGS_PRIVACY":
                panelToShow = new PrivacySettingsPanel(currentUser);
                break;
            case "SETTINGS_ABOUT":
                panelToShow = new AboutPanel();
                break;
            case "ADMIN_DASHBOARD":
                new view.admin.AdminDashboard(currentUser).setVisible(true);
                return;
            default:
                return;
        }

        if (panelToShow != null) {
            if (lang.isArabic()) {
                panelToShow.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            }

            mainContainer.removeAll();
            mainContainer.add(panelToShow, BorderLayout.CENTER);
            mainContainer.revalidate();
            mainContainer.repaint();
        }
    }

    // =========================================================================
    // NAVBAR (Top Right)
    // =========================================================================
    private JPanel createNavBar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setOpaque(true);
        navbar.setBackground(Color.WHITE); // Cleaner navbar background like admin
        navbar.setBorder(new EmptyBorder(10, 20, 10, 20)); // Reduced padding
        if (lang.isArabic())
            navbar.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Profile / User Info (Right)
        JPanel userPanel = new JPanel(new FlowLayout(lang.isArabic() ? FlowLayout.LEFT : FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);
        if (lang.isArabic())
            userPanel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel notifIcon = new JLabel("🔔");
        notifIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        notifIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel userName = new JLabel(currentUser.getName());
        userName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userName.setForeground(new Color(50, 50, 50));

        // Circular Avatar Painting with cached image
        JLabel avatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (cachedAvatarImage != null) {
                    g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, 35, 35));
                    g2.drawImage(cachedAvatarImage, 0, 0, 35, 35, null);
                } else {
                    g2.setColor(SIDEBAR_COLOR);
                    g2.fillOval(0, 0, 35, 35);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 16));
                    String initial = currentUser.getName().substring(0, 1).toUpperCase();
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (35 - fm.stringWidth(initial)) / 2;
                    int y = (35 - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(initial, x, y);
                }
            }
        };
        avatar.setPreferredSize(new Dimension(35, 35));

        avatar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ProfileDialog(MainFrame.this, currentUser).setVisible(true);
                // Reload avatar if changed
                loadAvatarImage();
                navbar.revalidate();
                navbar.repaint();
            }
        });

        userPanel.add(notifIcon);
        userPanel.add(userName);
        userPanel.add(avatar);

        navbar.add(userPanel, lang.isArabic() ? BorderLayout.WEST : BorderLayout.EAST);

        return navbar;
    }
}
