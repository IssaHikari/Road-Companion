package view;

import model.User;
import model.Trip;
import service.TripService;
import util.IconManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class CreateTripPanel extends JPanel {

    private final User currentUser;
    private final TripService tripService;
    private Trip tripToEdit; // If not null, we are in edit mode

    // Preloaded background image
    private Image backgroundImage = null;

    // Fields
    private JButton originButton;
    private JButton destinationButton;
    private String selectedOrigin = "";
    private String selectedDestination = "";
    private JSpinner seatsSpinner;
    private JTextField priceField;
    private JTextField dateField;
    private JTextField timeField;
    private JTextArea descriptionArea;
    private JButton actionButton;

    public CreateTripPanel(User user) {
        this(user, null);
    }

    // Static cache for background
    private static Image cachedBackgroundImage = null;

    public CreateTripPanel(User user, Trip tripToEdit) {
        this.currentUser = user;
        this.tripToEdit = tripToEdit;
        this.tripService = new TripService();

        // Use cached image if available, otherwise load async
        if (cachedBackgroundImage != null) {
            this.backgroundImage = cachedBackgroundImage;
        } else {
            new Thread(() -> {
                ImageIcon bgIcon = IconManager.getIcon("trip_bg.jpg");
                if (bgIcon != null && bgIcon.getImage() != null) {
                    cachedBackgroundImage = bgIcon.getImage();
                    this.backgroundImage = cachedBackgroundImage;
                    SwingUtilities.invokeLater(this::repaint);
                }
            }).start();
        }

        if (!user.getRole().equals("DRIVER")) {
            displayAccessDenied();
            return;
        }

        initializeUI();
        if (tripToEdit != null) {
            prefillData();
        }
    }

    private void displayAccessDenied() {
        setLayout(new BorderLayout());
        JLabel denied = new JLabel("Erreur : Seuls les conducteurs peuvent publier des trajets.",
                SwingConstants.CENTER);
        denied.setFont(new Font("Arial", Font.BOLD, 20));
        add(denied, BorderLayout.CENTER);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Main Container with Background
        // Main Container with Background
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                if (backgroundImage != null) {
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    // Draw directly - much faster than getScaledInstance
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                    // Add a very light dark overlay for better text readability
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    // Fallback gradient if image not found
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    GradientPaint gp = new GradientPaint(0, 0, new Color(44, 62, 80), getWidth(), getHeight(),
                            new Color(63, 81, 181));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // Custom rounded panel - full size
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded rectangle background - very transparent to show image
                g2d.setColor(new Color(255, 255, 255, 150)); // Very transparent
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Draw subtle shadow border
                g2d.setColor(new Color(0, 0, 0, 60));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);

                super.paintComponent(g);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5; // Distribute
                           // width
        int row = 0;

        // Title
        JLabel title = new JLabel(tripToEdit == null ? "Publier un Trajet" : "Modifier le Trajet",
                SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Slightly larger font
        title.setForeground(new Color(33, 47, 61)); // Darker color for better visibility
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(title, gbc);

        // Driver Info
        JLabel driverInfo = new JLabel("Conducteur: " + currentUser
                .getName());
        driverInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        driverInfo.setForeground(new Color(52, 73, 94)); // Darker
                                                         // gray
        driverInfo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = row++;
        formPanel.add(driverInfo, gbc);

        gbc.gridy = row++;
        formPanel.add(new JSeparator(), gbc);

        // Reset to 1 col for fields
        gbc.gridwidth = 1;

        // Row: Origin | Destination
        gbc.gridy = row; // Labels row
        gbc.gridx = 0;
        formPanel.add(createStyledLabel("Départ (Wilaya / Baladiya)"), gbc);
        gbc.gridx = 1;
        formPanel.add(createStyledLabel("Arrivée (Wilaya / Baladiya)"), gbc);

        row++; // Components row
        gbc.gridy = row;

        gbc.gridx = 0;
        originButton = createStyledInputButton("📍 Départ");
        originButton.addActionListener(e -> {
            LocationSelectorDialog dialog = new LocationSelectorDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            String result = dialog.getSelectedLocation();
            if (result != null) {
                selectedOrigin = result;
                updateLocationButton(originButton, result, "📍");
            }
        });
        formPanel.add(originButton, gbc);

        gbc.gridx = 1;
        destinationButton = createStyledInputButton("🏁 Arrivée");
        destinationButton.addActionListener(e -> {
            LocationSelectorDialog dialog = new LocationSelectorDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            String result = dialog.getSelectedLocation();
            if (result != null) {
                selectedDestination = result;
                updateLocationButton(destinationButton, result, "🏁");
            }
        });
        formPanel.add(destinationButton, gbc);

        row++; // Advanced by 2 rows (labels + components)

        // Date | Time
        gbc.gridy = row; // Labels row
        gbc.gridx = 0;
        formPanel.add(createStyledLabel("Date (AAAA-MM-JJ)"), gbc);
        gbc.gridx = 1;
        formPanel.add(createStyledLabel("Heure (HH:MM)"), gbc);

        row++; // Components row
        gbc.gridy = row;
        dateField = new JTextField(LocalDate.now().plusDays(1).toString());
        styleField(dateField);
        gbc.gridx = 0;
        formPanel.add(dateField, gbc);

        timeField = new JTextField("08:00");
        styleField(timeField);
        gbc.gridx = 1;
        formPanel.add(timeField, gbc);

        row++;

        // Price | Seats
        gbc.gridy = row; // Labels
        gbc.gridx = 0;
        formPanel.add(createStyledLabel("Prix (DZD)"), gbc);
        gbc.gridx = 1;
        formPanel.add(createStyledLabel("Places"), gbc);

        row++; // Components
        gbc.gridy = row;
        priceField = new JTextField();
        styleField(priceField);
        gbc.gridx = 0;
        formPanel.add(priceField, gbc);

        seatsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        seatsSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        seatsSpinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true), // Rounded border
                new EmptyBorder(5, 8, 5, 8))); // Padding
        gbc.gridx = 1;
        formPanel.add(seatsSpinner, gbc);

        row++;

        // Description (Full width)
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(createStyledLabel("Description (Optionnel)"), gbc);

        descriptionArea = new JTextArea(3, 20); // Reduced lines
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setBackground(new Color(255, 255, 255, 180)); // Transparent to show background
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true), // Rounded border
                new EmptyBorder(8, 12, 8, 12))); // Padding
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(null); // Remove scroll pane border

        gbc.gridy = row++;
        formPanel.add(descScroll, gbc);

        // Action Button
        actionButton = new JButton(tripToEdit == null ? "PUBLIER" : "ENREGISTRER");
        actionButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        actionButton.setBackground(new Color(63, 81, 181));
        actionButton.setForeground(Color.WHITE);
        actionButton.setFocusPainted(false);
        actionButton.setPreferredSize(new Dimension(200, 45));
        actionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        actionButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 65, 150), 1, true), // Rounded border
                BorderFactory.createEmptyBorder(10, 20, 10, 20))); // Padding
        actionButton.addActionListener(e -> handleSubmit());

        gbc.gridy = row++;
        gbc.insets = new Insets(15, 10, 10, 10);
        formPanel.add(actionButton, gbc);

        // Add form to main container - fill entire space
        mainContainer.add(formPanel, BorderLayout.CENTER);

        // Wrap in ScrollPane just in case on very small screens, but hide borders
        JScrollPane scroll = new JScrollPane(mainContainer);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    // This method is no longer used as layout is handled inline for more control.
    // private void addLabelAndComponent(JPanel p, GridBagConstraints gbc, String
    // text, JComponent c) {
    // // Manually handled in init to ensure proper grid alignment,
    // // removing this but keeping method signature locally if needed or just
    // inline it in Init.
    // // I inlined it above to handle the 2x2 grid properly.
    // }

    private void updateLocationButton(JButton btn, String text, String icon) {
        btn.setText(icon + " " + text);
        btn.setForeground(new Color(50, 50, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(255, 255, 255, 180)); // Transparent to show background
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true), // Rounded border
                new EmptyBorder(8, 12, 8, 12))); // More padding
    }

    private void prefillData() {
        if (tripToEdit == null)
            return;

        selectedOrigin = tripToEdit.getOrigin();
        updateLocationButton(originButton, selectedOrigin, "📍");

        selectedDestination = tripToEdit.getDestination();
        updateLocationButton(destinationButton, selectedDestination, "🏁");

        priceField.setText(String.valueOf(tripToEdit.getPrice()));
        seatsSpinner.setValue(tripToEdit.getSeatsAvailable());
        dateField.setText(tripToEdit.getDate().toString());
        timeField.setText(tripToEdit.getTime().toString());
        descriptionArea.setText(tripToEdit.getDescription());
    }

    private void handleSubmit() {
        String origin = selectedOrigin;
        String destination = selectedDestination;

        // Use full strings (Wilaya / Baladiya)

        try {
            double price = Double.parseDouble(priceField.getText());
            int seats = (int) seatsSpinner.getValue();
            LocalDate tripDate = LocalDate.parse(dateField.getText());
            LocalTime tripTime = LocalTime.parse(timeField.getText());

            if (tripToEdit == null) {
                // CREATE
                Trip newTrip = new Trip();
                newTrip.setDriverId(currentUser.getUserId());
                newTrip.setOrigin(origin);
                newTrip.setDestination(destination);
                newTrip.setDate(tripDate);
                newTrip.setTime(tripTime);
                newTrip.setSeatsAvailable(seats);
                newTrip.setPrice(price);
                newTrip.setDescription(descriptionArea.getText().trim());
                newTrip.setStatus("AVAILABLE");

                int id = tripService.offerTrip(newTrip);
                JOptionPane.showMessageDialog(this, "Trajet publié avec succès! ID: " + id);

                // Reset form
                resetForm();
            } else {
                // UPDATE
                tripToEdit.setOrigin(origin);
                tripToEdit.setDestination(destination);
                tripToEdit.setDate(tripDate);
                tripToEdit.setTime(tripTime);
                tripToEdit.setSeatsAvailable(seats);
                tripToEdit.setPrice(price);
                tripToEdit.setDescription(descriptionArea.getText().trim());

                if (tripService.updateTrip(tripToEdit)) {
                    JOptionPane.showMessageDialog(this, "Trajet mis à jour avec succès!");
                    // Close window if it's a dialog, purely by checking context?
                    // Or just let user close it.
                    Window w = SwingUtilities.getWindowAncestor(this);
                    if (w instanceof JDialog) {
                        w.dispose();
                    }
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez vérifier le prix.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format de date incorrect. Utilisez AAAA-MM-JJ et HH:MM", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        selectedOrigin = "";
        originButton.setText("📍 Départ");
        selectedDestination = "";
        destinationButton.setText("🏁 Arrivée");
        priceField.setText("");
        dateField.setText(LocalDate.now().plusDays(1).toString());
        timeField.setText("08:00");
        descriptionArea.setText("");
        seatsSpinner.setValue(1);
    }

    private JButton createStyledInputButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(280, 42));
        btn.setBackground(new Color(255, 255, 255, 180)); // Transparent to show background
        btn.setForeground(Color.GRAY);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true), // Rounded border
                BorderFactory.createEmptyBorder(8, 12, 8, 12))); // More padding
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(33, 47, 61)); // Dark color for visibility
        return label;
    }
}
