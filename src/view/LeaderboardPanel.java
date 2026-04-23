package view;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class LeaderboardPanel extends JPanel {

    private final User currentUser;
    private final UserDAO userDAO;
    private JPanel mainContentPanel;
    private JPanel podiumPanel;
    private JPanel listPanel;

    // Colors extracted from the image style
    private static final Color BG_GRADIENT_1 = new Color(50, 20, 100); // Deep Purple
    private static final Color BG_GRADIENT_2 = new Color(130, 40, 210); // Lighter Violet
    private static final Color CARD_BG = new Color(255, 255, 255, 30); // Glassy White
    private static final Color TEXT_GOLD = new Color(255, 215, 0);

    public LeaderboardPanel(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Custom Background Panel with Gradient
        mainContentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, BG_GRADIENT_1, 0, h, BG_GRADIENT_2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                // Optional: Add some subtle circles/shapes in background for "texture" like the
                // image
                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillOval(-50, -50, 300, 300);
                g2d.fillOval(w - 200, h / 2, 400, 400);
            }
        };

        // Header (Title)
        JLabel title = new JLabel("CLASSEMENT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        mainContentPanel.add(title, BorderLayout.NORTH);

        // Center ScrollForm (Contains Podium + List)
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(new EmptyBorder(0, 20, 20, 20));

        // 1. Podium Section
        podiumPanel = new JPanel(new GridBagLayout()); // Use GridBag for flexible alignment
        podiumPanel.setOpaque(false);
        podiumPanel.setPreferredSize(new Dimension(600, 260)); // Fixed height for podium area
        podiumPanel.setMaximumSize(new Dimension(800, 260));
        scrollContent.add(podiumPanel);

        scrollContent.add(Box.createVerticalStrut(20));

        // 2. List Section
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        scrollContent.add(listPanel);

        // ScrollPane wrapper
        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Custom ScrollBar UI (Dark/Minimal)
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(255, 255, 255, 100);
                this.trackColor = new Color(0, 0, 0, 0);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });

        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        // Refresh Button (Floating bottom right or just top right? Let's put it top
        // right overlay if possible, or just keep it simple)
        // For simplicity, add it to the NORTH panel east side
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(title, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("↻");
        refreshBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadLeaderboard());

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.setOpaque(false);
        btnWrapper.add(refreshBtn);

        topBar.add(btnWrapper, BorderLayout.EAST);
        // Add transparent spacer to left to balance center title
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(btnWrapper.getPreferredSize());
        topBar.add(spacer, BorderLayout.WEST);

        mainContentPanel.add(topBar, BorderLayout.NORTH);

        add(mainContentPanel, BorderLayout.CENTER);

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        podiumPanel.removeAll();
        listPanel.removeAll();

        try {
            List<User> topUsers = userDAO.getTopRatedUsers(10);

            if (topUsers.isEmpty()) {
                JLabel empty = new JLabel("Aucun classement pour le moment.");
                empty.setForeground(Color.WHITE);
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                listPanel.add(empty);
            } else {
                // Split into Podium (Top 3) and List (Rest)

                // PODIUM LOGIC
                // We want layout: Size 2 (Left), Size 1 (Center/Top), Size 3 (Right)
                // Indices in topUsers: 0 -> #1, 1 -> #2, 2 -> #3

                User user1 = topUsers.size() > 0 ? topUsers.get(0) : null;
                User user2 = topUsers.size() > 1 ? topUsers.get(1) : null;
                User user3 = topUsers.size() > 2 ? topUsers.get(2) : null;

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(0, 10, 0, 10);
                gbc.anchor = GridBagConstraints.SOUTH;

                // Rank 2 (Left)
                if (user2 != null) {
                    gbc.gridx = 0;
                    podiumPanel.add(createPodiumCard(user2, 2), gbc);
                }

                // Rank 1 (Center) - Slightly lifted or just physically larger handled in
                // createPodiumCard
                if (user1 != null) {
                    gbc.gridx = 1;
                    // Add a bottom margin to push it "up" visually relative to others if aligned
                    // bottom?
                    // Or just use the card size. createPodiumCard(..., 1) will be bigger.
                    podiumPanel.add(createPodiumCard(user1, 1), gbc);
                }

                // Rank 3 (Right)
                if (user3 != null) {
                    gbc.gridx = 2;
                    podiumPanel.add(createPodiumCard(user3, 3), gbc);
                }

                // REST OF THE LIST (4 to 10)
                int rank = 4;
                for (int i = 3; i < topUsers.size(); i++) {
                    User u = topUsers.get(i);
                    boolean isMe = (currentUser != null && u.getUserId() == currentUser.getUserId());
                    listPanel.add(createListCard(u, rank++, isMe));
                    listPanel.add(Box.createVerticalStrut(8));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // --- Podium Card Component ---
    private JPanel createPodiumCard(User user, int rank) {
        // scale based on rank: 1 is biggest.
        int avatarSize = (rank == 1) ? 100 : (rank == 2 ? 80 : 75);
        int ringWidth = (rank == 1) ? 4 : 3;
        Color ringColor = (rank == 1) ? TEXT_GOLD : (rank == 2 ? new Color(192, 192, 192) : new Color(205, 127, 50));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Crown for #1
        JLabel crown = new JLabel("♛");
        crown.setFont(new Font("Segoe UI Symbol", Font.BOLD, (rank == 1 ? 32 : 0))); // Only show for 1
        crown.setForeground(TEXT_GOLD);
        crown.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (rank != 1)
            crown.setVisible(false);
        panel.add(crown);
        if (rank != 1)
            panel.add(Box.createVerticalStrut(20)); // spacer to align avatars roughly if 1 has crown

        // Avatar + Ring
        JLabel avatarLbl = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw Ring
                g2.setColor(ringColor);
                g2.fillOval(0, 0, getWidth(), getHeight());

                // Draw Avatar Bg
                int padding = ringWidth + 2;
                g2.setColor(new Color(60, 20, 110)); // Inner bg
                g2.fillOval(padding, padding, getWidth() - padding * 2, getHeight() - padding * 2);

                // Draw Initial
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, avatarSize / 3));
                String initial = (user.getName() != null) ? user.getName().substring(0, 1).toUpperCase() : "?";
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(initial)) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2 - fm.getDescent();
                g2.drawString(initial, tx, ty);

                // Draw Rank Badge at bottom circle
                int badgeSize = 24;
                g2.setColor(ringColor);
                g2.fillOval((getWidth() - badgeSize) / 2, getHeight() - badgeSize + 5, badgeSize, badgeSize);

                g2.setColor(Color.BLACK); // Text color on badge
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                String rs = String.valueOf(rank);
                FontMetrics rfm = g2.getFontMetrics();
                g2.drawString(rs, (getWidth() - rfm.stringWidth(rs)) / 2,
                        getHeight() - badgeSize / 2 + 5 + rfm.getAscent() / 2 - 2);
            }
        };
        avatarLbl.setPreferredSize(new Dimension(avatarSize + 10, avatarSize + 10)); // +10 for ring/badge
        avatarLbl.setMaximumSize(new Dimension(avatarSize + 10, avatarSize + 10));
        avatarLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(avatarLbl);

        panel.add(Box.createVerticalStrut(10));

        // Name
        JLabel name = new JLabel(user.getName());
        name.setFont(new Font("Segoe UI", Font.BOLD, (rank == 1 ? 16 : 14)));
        name.setForeground(Color.WHITE);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(name);

        // Score
        JLabel score = new JLabel(String.format("%.1f ★", user.getAverageRating()));
        score.setFont(new Font("Segoe UI", Font.BOLD, (rank == 1 ? 18 : 15)));
        score.setForeground(TEXT_GOLD);
        score.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(score);

        return panel;
    }

    // --- List Card Component (Rows) ---
    private JPanel createListCard(User user, int rank, boolean isMe) {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glassy background
                g2.setColor(isMe ? new Color(255, 255, 255, 60) : CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // If Me, draw subtle border
                if (isMe) {
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                }
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(800, 60)); // Slimmer height
        card.setPreferredSize(new Dimension(600, 60));
        card.setBorder(new EmptyBorder(5, 20, 5, 20));

        // Left: Rank & Avatar
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        left.setOpaque(false);

        JLabel rankLbl = new JLabel("#" + rank);
        rankLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rankLbl.setForeground(new Color(220, 220, 220));
        rankLbl.setPreferredSize(new Dimension(30, 30));
        left.add(rankLbl);

        // Small Avatar Circle
        JLabel avatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 165, 0)); // Orange-ish like image
                g2.fillOval(0, 0, 36, 36);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                String initial = (user.getName() != null) ? user.getName().substring(0, 1).toUpperCase() : "?";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initial, (36 - fm.stringWidth(initial)) / 2, (36 + fm.getAscent()) / 2 - 2);
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        left.add(avatar);

        // Name
        JLabel name = new JLabel(user.getName() + (isMe ? " (Moi)" : ""));
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setForeground(Color.WHITE);
        left.add(name);

        card.add(left, BorderLayout.WEST);

        // Right: Stats
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        right.setOpaque(false);

        JLabel score = new JLabel(String.format("%.1f", user.getAverageRating()));
        score.setFont(new Font("Segoe UI", Font.BOLD, 16));
        score.setForeground(TEXT_GOLD); // Yellow text for score

        JLabel trips = new JLabel(user.getTotalTrips() + " Trips");
        trips.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        trips.setForeground(new Color(200, 200, 200));

        right.add(score);
        right.add(trips);

        card.add(right, BorderLayout.EAST);

        return card;
    }
}
