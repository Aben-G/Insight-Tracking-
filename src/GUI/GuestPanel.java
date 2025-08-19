package GUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

public class GuestPanel extends JPanel {
    private JButton signUpButton;
    private JButton addCustomerButton;
    private JButton addOrderButton;
    private JButton closeButton;
    private Timer animationTimer;
    private float gradientShift = 0f;

    private final Color Bulish = new Color(0, 25, 51);
    private final Color orangeYellow = new Color(200, 150, 50, 200);

    public GuestPanel() {
        setLayout(null);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(750, 450));
      
        addGuestButtons();
       
        addHeadline();
        addPlaceholderFeatures();

        animationTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gradientShift += 0.005f;
                if (gradientShift > 1f) gradientShift = 0f;
                repaint();
            }
        });
        animationTimer.start();
    }

  

    private void addHeadline() {
        JLabel headline = new JLabel("Welcome, Guest!");
        headline.setBounds(225, 20, 300, 50);
        headline.setFont(new Font("Arial", Font.BOLD, 30));
        headline.setForeground(Color.WHITE);
        headline.setHorizontalAlignment(SwingConstants.CENTER);
        add(headline);
    }

    private void addGuestButtons() {
        JPanel buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

                g2d.dispose();
            }
        };
        buttonPanel.setBounds(175, 100, 400, 150);
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setOpaque(false);
        add(buttonPanel);

        signUpButton = createGlassButton("Sign Up or Sign In", new Color(255, 215, 0), Color.BLACK);
        buttonPanel.add(signUpButton);
        signUpButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(GuestPanel.this);
            if (window != null) {
                window.dispose();
            }

            SwingUtilities.invokeLater(() -> {
                new WelcomePage();
            });
        });
        addCustomerButton = createGlassButton("Add Customer", new Color(0, 75, 150), Color.WHITE);
        buttonPanel.add(addCustomerButton);
        addCustomerButton.addActionListener(e -> showAddCustomerDialog());
        addOrderButton = createGlassButton("Add Order", new Color(0, 75, 150), Color.WHITE);
        buttonPanel.add(addOrderButton);
        addOrderButton.addActionListener(e -> showAddOrderForm());
    }

    private void addPlaceholderFeatures() {
        JLabel placeholderLabel = new JLabel("BGR Print & Advert");
        placeholderLabel.setBounds(175, 270, 400, 30);
        placeholderLabel.setFont(new Font("Arial", Font.BOLD, 20));
        placeholderLabel.setForeground(Color.WHITE);
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(placeholderLabel);

        JTextArea placeholderText = new JTextArea("As a guest, you can explore limited features. Sign up to unlock the full experience!");
        placeholderText.setBounds(125, 310, 500, 60);
        placeholderText.setFont(new Font("Arial", Font.PLAIN, 16));
        placeholderText.setForeground(Color.WHITE);
        placeholderText.setBackground(new Color(0, 0, 0, 0));
        placeholderText.setEditable(false);
        placeholderText.setLineWrap(true);
        placeholderText.setWrapStyleWord(true);
        add(placeholderText);
    }

    private JButton createGlassButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                g2d.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                g2d.setColor(fgColor);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        GradientPaint gradient = new GradientPaint(
                (float) (width * gradientShift), 0, Bulish,
                (float) (width * (1 - gradientShift)), height, orangeYellow);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        GradientPaint gradient2 = new GradientPaint(
                0, (float) (height * gradientShift), orangeYellow,
                width, (float) (height * (1 - gradientShift)), Bulish);
        g2d.setPaint(gradient2);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.fillRect(0, 0, width, height);
    }

    
    public static void showAddOrderForm() {
        JDialog addOrderDialog = new JDialog();
        addOrderDialog.setUndecorated(true);
        addOrderDialog.setModal(true);
        addOrderDialog.setSize(500, 580); // Adjusted size for better spacing
        addOrderDialog.setLayout(null);
        addOrderDialog.setLocationRelativeTo(null);

        // Custom panel for background and rounded corners
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color startColor = new Color(0, 25, 51, 0); // Start color for gradient
                Color endColor = new Color(0, 75, 150, 25);  // End color for gradient

                // Gradient background
                GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Rounded corners

                // Fixed border color (semi-transparent white)
                Color borderColor = new Color(255, 255, 255, 50); // Border color

                // Border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20); // Rounded corners

                g2d.dispose();
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, 500, 580);
        addOrderDialog.add(mainPanel);
        addOrderDialog.getRootPane().putClientProperty("JComponent.roundRect", true);


        // Title Label
        JLabel titleLabel = new JLabel("Add New Order");
        titleLabel.setBounds(150, 20, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
       
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        // Close Button (X)
        JButton closeButton = new JButton("X");
        closeButton.setBounds(460, 10, 30, 30);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(0, 25, 51));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setFocusable(false);
        closeButton.addActionListener(e -> addOrderDialog.dispose());
        mainPanel.add(closeButton);

        // Customer Name Field
        JLabel customerNameLabel = new JLabel("Customer Name:");
        customerNameLabel.setBounds(50, 70, 150, 30);
       
        mainPanel.add(customerNameLabel);

        JTextField customerNameField = new JTextField();
        customerNameField.setBounds(200, 70, 250, 30);
        customerNameField.setBackground(new Color(255, 255, 255, 100));
        customerNameField.setForeground(Color.black);
        customerNameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(customerNameField);

        JLabel customerWarningLabel = new JLabel("Customer does not exist.");
        customerWarningLabel.setForeground(Color.RED);
        customerWarningLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        customerWarningLabel.setBounds(200, 100, 250, 20);
        customerWarningLabel.setVisible(false);
        mainPanel.add(customerWarningLabel);

        JLabel registerLink = new JLabel("<html><u>Register Customer</u></html>");
        registerLink.setForeground(Color.BLUE);
        registerLink.setFont(new Font("Arial", Font.PLAIN, 12));
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.setBounds(200, 120, 250, 20);
        registerLink.setVisible(false);
        mainPanel.add(registerLink);


        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean customerAdded = showAddCustomerDialog(customerNameField.getText().trim());
                if (customerAdded) {
                    customerWarningLabel.setVisible(false);
                    registerLink.setVisible(false);
                }
            }
        });

        customerNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String customerName = customerNameField.getText().trim();
                if (!customerName.isEmpty()) {
                    try (Connection conn = Database.getConnection()) {
                        String sql = "SELECT COUNT(*) FROM Customers WHERE Name = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, customerName);
                            try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next() && rs.getInt(1) == 0) {
                                    customerWarningLabel.setVisible(true);
                                    registerLink.setVisible(true);
                                } else {
                                    customerWarningLabel.setVisible(false);
                                    registerLink.setVisible(false);
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Product Field
        JLabel productLabel = new JLabel("Product:");
        productLabel.setBounds(50, 150, 150, 30);
       
        mainPanel.add(productLabel);

        JTextField productField = new JTextField();
        productField.setBounds(200, 150, 250, 30);
        productField.setBackground(new Color(255, 255, 255, 100));
        productField.setForeground(Color.black);
        productField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(productField);

        // Quantity Field
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(50, 190, 150, 30);
        
        mainPanel.add(quantityLabel);

        JTextField quantityField = new JTextField();
        quantityField.setBounds(200, 190, 250, 30);
        quantityField.setBackground(new Color(255, 255, 255, 100));
        quantityField.setForeground(Color.black);
        quantityField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(quantityField);

        // Price Field
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(50, 230, 150, 30);
        
        mainPanel.add(priceLabel);

        JTextField priceField = new JTextField();
        priceField.setBounds(200, 230, 250, 30);
        priceField.setBackground(new Color(255, 255, 255, 100));
        priceField.setForeground(Color.black);
        priceField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(priceField);

        // Total Price Field
        JLabel totalPriceLabel = new JLabel("Total Price:");
        totalPriceLabel.setBounds(50, 270, 150, 30);
        
        mainPanel.add(totalPriceLabel);

        JTextField totalPriceField = new JTextField("0.00");
        totalPriceField.setBounds(200, 270, 250, 30);
        totalPriceField.setBackground(new Color(255, 255, 255, 100));
        totalPriceField.setForeground(Color.black);
        totalPriceField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        totalPriceField.setEditable(false);
        mainPanel.add(totalPriceField);

        // Advanced Payment Components
        JLabel advancedPaymentLabel = new JLabel("Advanced Payment:");
        advancedPaymentLabel.setBounds(50, 310, 150, 30);
     
        mainPanel.add(advancedPaymentLabel);

        JCheckBox advancedPaymentCheckBox = new JCheckBox();
        advancedPaymentCheckBox.setBounds(200, 310, 20, 30);
        advancedPaymentCheckBox.setBackground(new Color(0, 25, 51));
        mainPanel.add(advancedPaymentCheckBox);

        JTextField advancedPaymentField = new JTextField("0.00");
        advancedPaymentField.setBounds(230, 310, 220, 30);
        advancedPaymentField.setBackground(new Color(255, 255, 255, 100));
        advancedPaymentField.setForeground(Color.black);
        advancedPaymentField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        advancedPaymentField.setVisible(false);
        mainPanel.add(advancedPaymentField);

        advancedPaymentCheckBox.addItemListener(e -> {
            if (advancedPaymentCheckBox.isSelected()) {
                advancedPaymentField.setVisible(true);
            } else {
                advancedPaymentField.setVisible(false);
            }
        });

        // Received Date Field
        JLabel receivedDateLabel = new JLabel("Received Date:");
        receivedDateLabel.setBounds(50, 350, 150, 30);
       
        mainPanel.add(receivedDateLabel);

        JTextField receivedDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        receivedDateField.setBounds(200, 350, 250, 30);
        receivedDateField.setBackground(new Color(255, 255, 255, 100));
        receivedDateField.setForeground(Color.black);
        receivedDateField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        receivedDateField.setEditable(false);
        mainPanel.add(receivedDateField);

        // Deadline Date Field
        JLabel deadlineLabel = new JLabel("Deadline Date:");
        deadlineLabel.setBounds(50, 390, 150, 30);
        
        mainPanel.add(deadlineLabel);

        JComboBox<String> dayCombo = new JComboBox<>();
        JComboBox<String> monthCombo = new JComboBox<>();
        JComboBox<String> yearCombo = new JComboBox<>();

        LocalDate today = LocalDate.now();
        int currentDay = today.getDayOfMonth();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        for (int i = currentDay; i <= 31; i++) dayCombo.addItem(String.valueOf(i));
        for (int i = currentMonth; i <= 12; i++) monthCombo.addItem(String.valueOf(i));
        for (int i = currentYear; i <= 2030; i++) yearCombo.addItem(String.valueOf(i));

        dayCombo.setBounds(200, 390, 50, 30);
        monthCombo.setBounds(260, 390, 50, 30);
        yearCombo.setBounds(320, 390, 80, 30);

        mainPanel.add(dayCombo);
        mainPanel.add(monthCombo);
        mainPanel.add(yearCombo);

        JButton addButton = createButton("Add", 100, 500, 120, 40, new Color(0, 120, 215,50));
        mainPanel.add(addButton);

        JButton cancelButton = createButton("Cancel", 280, 500, 120, 40, new Color(150, 0, 0,180));
        mainPanel.add(cancelButton);

        // Document Listener for Total Price Calculation
        DocumentListener updateTotalPriceListener = new DocumentListener() {
            private void updateTotalPrice() {
                try {
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    totalPriceField.setText(String.format("%.2f", quantity * price));
                } catch (NumberFormatException e) {
                    totalPriceField.setText("0.00");
                }
            }
            public void insertUpdate(DocumentEvent e) { updateTotalPrice(); }
            public void removeUpdate(DocumentEvent e) { updateTotalPrice(); }
            public void changedUpdate(DocumentEvent e) { updateTotalPrice(); }
        };
        quantityField.getDocument().addDocumentListener(updateTotalPriceListener);
        priceField.getDocument().addDocumentListener(updateTotalPriceListener);

        customerNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	productField.requestFocus();  
                }
            }
        });
        productField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	quantityField.requestFocus();  
                }
            }
        });
        quantityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	priceField.requestFocus();  
                }
            }
        });
        priceField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	totalPriceField.requestFocus();  
                }
            }
        });

        
        // Add Button Action
        addButton.addActionListener(e -> {
            String customerName = customerNameField.getText().trim();
            String product = productField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String priceText = priceField.getText().trim();
            String receivedDate = receivedDateField.getText().trim();
            String deadlineDate = yearCombo.getSelectedItem() + "-" +
                    String.format("%02d", Integer.parseInt((String) monthCombo.getSelectedItem())) + "-" +
                    String.format("%02d", Integer.parseInt((String) dayCombo.getSelectedItem()));

            // Validation
            if (customerName.isEmpty() || product.isEmpty() || quantityText.isEmpty() || priceText.isEmpty() || receivedDate.isEmpty()) {
                JOptionPane.showMessageDialog(addOrderDialog, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!quantityText.matches("\\d+") || Integer.parseInt(quantityText) <= 0) {
                JOptionPane.showMessageDialog(addOrderDialog, "Quantity must be a positive integer.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!priceText.matches("\\d+(\\.\\d{1,2})?")) {
                JOptionPane.showMessageDialog(addOrderDialog, "Price must be a valid number (e.g., 100 or 100.50).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double totalPrice = Integer.parseInt(quantityText) * Double.parseDouble(priceText);
            double advancedPayment = 0.00;  // Default value if not checked

            if (advancedPaymentCheckBox.isSelected()) {
                String advancedPaymentText = advancedPaymentField.getText().trim();
                if (advancedPaymentText.isEmpty() || !advancedPaymentText.matches("\\d+(\\.\\d{1,2})?")) {
                    JOptionPane.showMessageDialog(addOrderDialog, "Advanced Payment must be a valid number (e.g., 100 or 100.50).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                advancedPayment = Double.parseDouble(advancedPaymentText);
            }

            try (Connection conn = Database.getConnection()) {
                String insertQuery = "INSERT INTO orders (customer_name, product, quantity, price, total_price, received_date, deadline_date, progress, advance_payment) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setString(1, customerName);
                    stmt.setString(2, product);
                    stmt.setInt(3, Integer.parseInt(quantityText));
                    stmt.setDouble(4, Double.parseDouble(priceText));
                    stmt.setDouble(5, totalPrice);

                    // Validate date format before inserting into the database
                    try {
                        stmt.setDate(6, java.sql.Date.valueOf(receivedDate)); // receivedDate is expected to be "yyyy-MM-dd"
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(addOrderDialog, "Invalid received date format. Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        stmt.setDate(7, java.sql.Date.valueOf(deadlineDate)); // deadlineDate is expected to be "yyyy-MM-dd"
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(addOrderDialog, "Invalid deadline date format. Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    stmt.setString(8, "Pending");
                    stmt.setDouble(9, advancedPayment);

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(addOrderDialog, "Order added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        addOrderDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(addOrderDialog, "Failed to add order. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(addOrderDialog, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(addOrderDialog, "Failed to establish database connection: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        
        cancelButton.addActionListener(e -> addOrderDialog.dispose());

        addOrderDialog.setVisible(true);
    }
    private static JButton createButton(String text, int x, int y, int width, int height, Color color) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
   
  
    
    private static boolean showAddCustomerDialog(String customerName) {
        JDialog addCustomerDialog = new JDialog();
        addCustomerDialog.setUndecorated(true);
        addCustomerDialog.setModal(true);
        addCustomerDialog.setSize(500, 580); // Adjusted size for better spacing
        addCustomerDialog.setLayout(null);
        addCustomerDialog.setLocationRelativeTo(null);

        // Custom panel for background and rounded corners
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color startColor = new Color(0, 25, 51, 0); // Start color for gradient
                Color endColor = new Color(0, 75, 150, 25);  // End color for gradient

                // Gradient background
                GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Rounded corners

                // Fixed border color (semi-transparent white)
                Color borderColor = new Color(255, 255, 255, 50); // Border color

                // Border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20); // Rounded corners

                g2d.dispose();
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, 500, 580);
        addCustomerDialog.add(mainPanel);
        addCustomerDialog.getRootPane().putClientProperty("JComponent.roundRect", true);

        // Title Label
        JLabel titleLabel = new JLabel("Register Customer");
        titleLabel.setBounds(150, 20, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
       
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        // Close Button (X)
        JButton closeButton = new JButton("X");
        closeButton.setBounds(460, 10, 30, 30);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(0, 25, 51));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setFocusable(false);
        closeButton.addActionListener(e -> addCustomerDialog.dispose());
        mainPanel.add(closeButton);

        // Full Name Field
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(50, 70, 150, 30);
       
        mainPanel.add(nameLabel);

        JTextField nameField = new JTextField(customerName);
        nameField.setBounds(200, 70, 250, 30);
        nameField.setBackground(new Color(255, 255, 255, 100));
        nameField.setForeground(Color.black);
        nameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        nameField.setEditable(false);
        mainPanel.add(nameField);

        // Primary Contact Field
        JLabel primaryContactLabel = new JLabel("Primary Contact:");
        primaryContactLabel.setBounds(50, 110, 150, 30);
        
        mainPanel.add(primaryContactLabel);

        JTextField primaryContactField = new JTextField();
        primaryContactField.setBounds(200, 110, 250, 30);
        primaryContactField.setBackground(new Color(255, 255, 255, 100));
        primaryContactField.setForeground(Color.black);
        primaryContactField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(primaryContactField);

        // Secondary Contact Field
        JLabel secondaryContactLabel = new JLabel("Secondary Contact:");
        secondaryContactLabel.setBounds(50, 150, 150, 30);
       
        mainPanel.add(secondaryContactLabel);

        JTextField secondaryContactField = new JTextField();
        secondaryContactField.setBounds(200, 150, 250, 30);
        secondaryContactField.setBackground(new Color(255, 255, 255, 100));
        secondaryContactField.setForeground(Color.black);
        secondaryContactField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(secondaryContactField);

        // Company Checkbox
        JCheckBox companyCheckBox = new JCheckBox("Represents a Company");
        companyCheckBox.setBounds(50, 190, 200, 30);
        
        companyCheckBox.setOpaque(false);
        mainPanel.add(companyCheckBox);

        // Company Name Field
        JLabel companyNameLabel = new JLabel("Company Name:");
        companyNameLabel.setBounds(50, 230, 150, 30);
        
        companyNameLabel.setVisible(false);
        mainPanel.add(companyNameLabel);

        JTextField companyNameField = new JTextField();
        companyNameField.setBounds(200, 230, 250, 30);
        companyNameField.setBackground(new Color(255, 255, 255, 100));
        companyNameField.setForeground(Color.black);
        companyNameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        companyNameField.setVisible(false);
        mainPanel.add(companyNameField);

        // Company Phone Field
        JLabel companyPhoneLabel = new JLabel("Company Phone:");
        companyPhoneLabel.setBounds(50, 270, 150, 30);
       
        companyPhoneLabel.setVisible(false);
        mainPanel.add(companyPhoneLabel);

        JTextField companyPhoneField = new JTextField();
        companyPhoneField.setBounds(200, 270, 250, 30);
        companyPhoneField.setBackground(new Color(255, 255, 255, 100));
        companyPhoneField.setForeground(Color.black);
        companyPhoneField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        companyPhoneField.setVisible(false);
        mainPanel.add(companyPhoneField);

        JButton addButton = createButton("Add", 100, 500, 120, 40, new Color(0, 120, 215,50));
        mainPanel.add(addButton);

        JButton cancelButton = createButton("Cancel", 280, 500, 120, 40, new Color(150, 0, 0, 180));
        mainPanel.add(cancelButton);

        // Company Checkbox Listener
        companyCheckBox.addItemListener(e -> {
            boolean selected = companyCheckBox.isSelected();
            companyNameLabel.setVisible(selected);
            companyNameField.setVisible(selected);
            companyPhoneLabel.setVisible(selected);
            companyPhoneField.setVisible(selected);
        });

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	primaryContactField.requestFocus();  
                }
            }
        });
        primaryContactField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	secondaryContactField.requestFocus();  
                }
            }
        });
        secondaryContactField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	companyNameField.requestFocus();  
                }
            }
        });
        companyNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	companyPhoneField.requestFocus();  
                }
            }
        });
        companyPhoneField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	addButton.requestFocus();  
                }
            }
        });
        // Add Button Action
        final boolean[] customerAdded = {false};

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String primaryContact = primaryContactField.getText().trim();
            String secondaryContact = secondaryContactField.getText().trim();
            String companyName = companyCheckBox.isSelected() ? companyNameField.getText().trim() : "None";
            String companyPhone = companyCheckBox.isSelected() ? companyPhoneField.getText().trim() : "None";

            if (name.isEmpty() || primaryContact.isEmpty() || primaryContact.length() != 10 || !primaryContact.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(addCustomerDialog, "Invalid primary contact! Must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!secondaryContact.isEmpty() && (secondaryContact.length() != 10 || !secondaryContact.matches("\\d{10}"))) {
                JOptionPane.showMessageDialog(addCustomerDialog, "Invalid secondary contact! Must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (companyCheckBox.isSelected()) {
                if (companyName.isEmpty() || companyPhone.isEmpty() || companyPhone.length() != 10 || !companyPhone.matches("\\d{10}")) {
                    JOptionPane.showMessageDialog(addCustomerDialog, "Company details invalid! Phone must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO Customers (Name, PrimaryContact, SecondaryContact, CompanyName, CompanyPhone) VALUES (?, ?, ?, ?, ?)");) {

                stmt.setString(1, name);
                stmt.setString(2, primaryContact);
                stmt.setString(3, secondaryContact.isEmpty() ? "None" : secondaryContact);
                stmt.setString(4, companyName);
                stmt.setString(5, companyPhone);

                if (stmt.executeUpdate() > 0) {
                    customerAdded[0] = true;
                    JOptionPane.showMessageDialog(addCustomerDialog, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    addCustomerDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addCustomerDialog, "Failed to add customer.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(addCustomerDialog, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addCustomerDialog.dispose());

        addCustomerDialog.setVisible(true);
        return customerAdded[0];
    }

    
    private static boolean showAddCustomerDialog() {
        JDialog addCustomerDialog = new JDialog();
        addCustomerDialog.setUndecorated(true);
        addCustomerDialog.setModal(true);
        addCustomerDialog.setSize(500, 580); // Adjusted size for better spacing
        addCustomerDialog.setLayout(null);
        addCustomerDialog.setLocationRelativeTo(null);

        // Custom panel for background and rounded corners
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color startColor = new Color(0, 25, 51, 0); // Start color for gradient
                Color endColor = new Color(0, 75, 150, 25);  // End color for gradient


                // Gradient background
                GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Rounded corners

                // Fixed border color (semi-transparent white)
                Color borderColor = new Color(255, 255, 255, 50); // Border color

                // Border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20); // Rounded corners

                g2d.dispose();
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, 500, 580);
        addCustomerDialog.add(mainPanel);
        addCustomerDialog.getRootPane().putClientProperty("JComponent.roundRect", true);

        // Title Label
        JLabel titleLabel = new JLabel("Register Customer");
        titleLabel.setBounds(150, 20, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
       
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        // Close Button (X)
        JButton closeButton = new JButton("X");
        closeButton.setBounds(460, 10, 30, 30);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(0, 25, 51));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setFocusable(false);
        closeButton.addActionListener(e -> addCustomerDialog.dispose());
        mainPanel.add(closeButton);

        // Full Name Field
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(50, 70, 150, 30);
       
        mainPanel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(200, 70, 250, 30);
        nameField.setBackground(new Color(255, 255, 255, 100));
        nameField.setForeground(Color.black);
        nameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(nameField);

        // Primary Contact Field
        JLabel primaryContactLabel = new JLabel("Primary Contact:");
        primaryContactLabel.setBounds(50, 110, 150, 30);
       
        mainPanel.add(primaryContactLabel);

        JTextField primaryContactField = new JTextField();
        primaryContactField.setBounds(200, 110, 250, 30);
        primaryContactField.setBackground(new Color(255, 255, 255, 100));
        primaryContactField.setForeground(Color.black);
        primaryContactField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(primaryContactField);

        // Secondary Contact Field
        JLabel secondaryContactLabel = new JLabel("Secondary Contact:");
        secondaryContactLabel.setBounds(50, 150, 150, 30);
        
        mainPanel.add(secondaryContactLabel);

        JTextField secondaryContactField = new JTextField();
        secondaryContactField.setBounds(200, 150, 250, 30);
        secondaryContactField.setBackground(new Color(255, 255, 255, 100));
        secondaryContactField.setForeground(Color.black);
        secondaryContactField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(secondaryContactField);

        // Company Checkbox
        JCheckBox companyCheckBox = new JCheckBox("Represents a Company");
        companyCheckBox.setBounds(50, 190, 200, 30);
       
        companyCheckBox.setOpaque(false);
        mainPanel.add(companyCheckBox);

        // Company Name Field
        JLabel companyNameLabel = new JLabel("Company Name:");
        companyNameLabel.setBounds(50, 230, 150, 30);
       
        companyNameLabel.setVisible(false);
        mainPanel.add(companyNameLabel);

        JTextField companyNameField = new JTextField();
        companyNameField.setBounds(200, 230, 250, 30);
        companyNameField.setBackground(new Color(255, 255, 255, 100));
        companyNameField.setForeground(Color.black);
        companyNameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        companyNameField.setVisible(false);
        mainPanel.add(companyNameField);

        // Company Phone Field
        JLabel companyPhoneLabel = new JLabel("Company Phone:");
        companyPhoneLabel.setBounds(50, 270, 150, 30);
        
        companyPhoneLabel.setVisible(false);
        mainPanel.add(companyPhoneLabel);

        JTextField companyPhoneField = new JTextField();
        companyPhoneField.setBounds(200, 270, 250, 30);
        companyPhoneField.setBackground(new Color(255, 255, 255, 100));
        companyPhoneField.setForeground(Color.black);
        companyPhoneField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        companyPhoneField.setVisible(false);
        mainPanel.add(companyPhoneField);

        JButton addButton = createButton("Add", 100, 500, 120, 40, new Color(0, 120, 215,50));
        mainPanel.add(addButton);

        JButton cancelButton = createButton("Cancel", 280, 500, 120, 40, new Color(150, 0, 0, 180));
        mainPanel.add(cancelButton);

        // Company Checkbox Listener
        companyCheckBox.addItemListener(e -> {
            boolean selected = companyCheckBox.isSelected();
            companyNameLabel.setVisible(selected);
            companyNameField.setVisible(selected);
            companyPhoneLabel.setVisible(selected);
            companyPhoneField.setVisible(selected);
        });

        
        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	primaryContactField.requestFocus();  
                }
            }
        });
        primaryContactField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	secondaryContactField.requestFocus();  
                }
            }
        });
        secondaryContactField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	companyNameField.requestFocus();  
                }
            }
        });
        companyNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	companyPhoneField.requestFocus();  
                }
            }
        });
        companyPhoneField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	addButton.requestFocus();  
                }
            }
        });
        
        // Add Button Action
        final boolean[] customerAdded = {false};

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String primaryContact = primaryContactField.getText().trim();
            String secondaryContact = secondaryContactField.getText().trim();
            String companyName = companyCheckBox.isSelected() ? companyNameField.getText().trim() : "None";
            String companyPhone = companyCheckBox.isSelected() ? companyPhoneField.getText().trim() : "None";

            if (name.isEmpty() || primaryContact.isEmpty() || primaryContact.length() != 10 || !primaryContact.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(addCustomerDialog, "Invalid primary contact! Must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!secondaryContact.isEmpty() && (secondaryContact.length() != 10 || !secondaryContact.matches("\\d{10}"))) {
                JOptionPane.showMessageDialog(addCustomerDialog, "Invalid secondary contact! Must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (companyCheckBox.isSelected()) {
                if (companyName.isEmpty() || companyPhone.isEmpty() || companyPhone.length() != 10 || !companyPhone.matches("\\d{10}")) {
                    JOptionPane.showMessageDialog(addCustomerDialog, "Company details invalid! Phone must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO Customers (Name, PrimaryContact, SecondaryContact, CompanyName, CompanyPhone) VALUES (?, ?, ?, ?, ?)");) {

                stmt.setString(1, name);
                stmt.setString(2, primaryContact);
                stmt.setString(3, secondaryContact.isEmpty() ? "None" : secondaryContact);
                stmt.setString(4, companyName);
                stmt.setString(5, companyPhone);

                if (stmt.executeUpdate() > 0) {
                    customerAdded[0] = true;
                    JOptionPane.showMessageDialog(addCustomerDialog, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    addCustomerDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addCustomerDialog, "Failed to add customer.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(addCustomerDialog, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addCustomerDialog.dispose());

        addCustomerDialog.setVisible(true);
        return customerAdded[0];
    }

}