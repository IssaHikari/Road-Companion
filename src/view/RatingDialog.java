package view;

import model.Rating;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class RatingDialog extends JDialog {

    private int selectedScore = 5;
    private JTextArea commentArea;

    public RatingDialog(JFrame parent, int ratedUserId, int raterUserId, Consumer<Rating> callback) {
        super(parent, "Noter le Chauffeur", true);
        initializeUI(ratedUserId, raterUserId, callback);
    }

    private void initializeUI(int ratedUserId, int raterUserId, Consumer<Rating> callback) {
        setSize(400, 450);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(245, 247, 250));
        header.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel title = new JLabel("Donnez votre avis");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Stars
        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        starPanel.setBackground(Color.WHITE);

        ButtonGroup group = new ButtonGroup();
        for (int i = 1; i <= 5; i++) {
            JToggleButton starBtn = new JToggleButton(String.valueOf(i));
            starBtn.setPreferredSize(new Dimension(40, 40));
            starBtn.setFocusPainted(false);
            starBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

            final int score = i;
            starBtn.addActionListener(e -> selectedScore = score);
            if (i == 5)
                starBtn.setSelected(true);

            group.add(starBtn);
            starPanel.add(starBtn);
        }
        content.add(new JLabel("Note (1-5):"));
        content.add(Box.createVerticalStrut(10));
        content.add(starPanel);

        content.add(Box.createVerticalStrut(20));
        content.add(new JLabel("Commentaire (Optionnel):"));
        content.add(Box.createVerticalStrut(10));

        commentArea = new JTextArea(5, 20);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        content.add(new JScrollPane(commentArea));

        add(content, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(10, 0, 20, 0));

        JButton submitBtn = new JButton("Envoyer");
        submitBtn.setPreferredSize(new Dimension(150, 40));
        submitBtn.setBackground(new Color(100, 149, 237)); // Cornflower Blue
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> {
            Rating r = new Rating(ratedUserId, raterUserId, selectedScore, commentArea.getText());
            callback.accept(r);
            dispose();
        });

        footer.add(submitBtn);
        add(footer, BorderLayout.SOUTH);
    }
}
