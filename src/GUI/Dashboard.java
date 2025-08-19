package GUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Dashboard {
    private JFrame frame;
    private JPanel sidePanel;
    private JPanel mainPanel;
    private JButton toggleButton;
    private boolean isExpanded = false;
    private JLabel lbl1;
    private Label label;
    private JTextField Search;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JPanel indicatorPanel;
    private JButton button5;
    private JButton signOutButton;
    private String signedInUsername;

    public Dashboard() {
        frame = new JFrame("BGR:Customer Handling v1.0.0");
        frame.setSize(1100, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/pics/bgrlogo.png"));
        frame.setIconImage(logoIcon.getImage());


        // Side panel with rounded border
        sidePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.gray); // Border color
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        sidePanel.setBounds(10, 10, 70, 540);
        sidePanel.setLayout(null);
        sidePanel.setOpaque(false);
        frame.add(sidePanel);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if the click is outside the side panel
                if (isExpanded && !sidePanel.getBounds().contains(e.getPoint())) {
                    toggleSidePanel(); // Collapse the side panel
                }

                // Check if the click is outside the search field
                if (Search.isFocusOwner() && !Search.getBounds().contains(e.getPoint())) {
                    Search.transferFocus(); // Lose focus
                    if (Search.getText().isEmpty()) {
                        setupPlaceholder(Search, " 🔍 Search"); // Restore placeholder
                    }
                }
            }
        });


        
        // Label for "Options"
        label = new Label("Options");
        label.setFont(new Font("Ariel", Font.BOLD, 20));
        label.setBounds(70, 10, 100, 30);
        sidePanel.add(label);

        // Toggle button for side panel
        toggleButton = createButton("☰", "/pics/option.png", 10, 10, 50, 30);
        toggleButton.addActionListener(e -> toggleSidePanel());
        sidePanel.add(toggleButton);

        // Indicator panel (2px wide, 26px long)
        indicatorPanel = new JPanel();
        indicatorPanel.setBounds(4, 55, 4, 26); // Initial position for Dashboard button
        indicatorPanel.setBackground(Color.orange); // Blue color for indicator
        sidePanel.add(indicatorPanel);

        // Dashboard button
        button1 = createButton("Dashboard", "/pics/homes.png", 0, 50, 200, 40);
        sidePanel.add(button1);
        button1.addActionListener(e -> {
            lbl1.setText("Dashboard");
            loadPage(new DefaultPage(frame, mainPanel), frame, mainPanel);
            indicatorPanel.setBounds(4, 55, 4, 26); // Move indicator to Dashboard button
        });
        
        // Customers button
        button2 = createButton("Customers", "/pics/customs.png", 0, 95, 200, 40);
        sidePanel.add(button2);
        button2.addActionListener(e -> {
            lbl1.setText("Customers");
            loadPage(new Customers(frame, mainPanel), frame, mainPanel);
            indicatorPanel.setBounds(4, 98, 4, 26); // Move indicator to Customers button
        });

        // Orders button
        button3 = createButton("Orders", "/pics/cart.png", 0, 140, 200, 40);
        sidePanel.add(button3);
        button3.addActionListener(e -> {
            lbl1.setText("Orders");
            loadPage(new Orders(frame, mainPanel), frame, mainPanel);
            indicatorPanel.setBounds(4, 143, 4, 26); // Move indicator to Orders button
        });

        // Users button
        button5 = createButton("Users", "/pics/staffs.png", 0, 179, 200, 40);
        sidePanel.add(button5);
        button5.addActionListener(e -> {
            lbl1.setText("Users");
            loadPage(new ManageUsers(frame, mainPanel), frame, mainPanel);
            indicatorPanel.setBounds(4, 184, 4, 26); // Move indicator to Users button
        });

        // Sign Out button
        signOutButton = createButton("Sign Out", "/pics/exit.png", 0, 480, 200, 40);
        sidePanel.add(signOutButton);
        signOutButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to sign out?", "Sign Out Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                frame.dispose();
                new WelcomePage();
            }
        });

        // Dashboard label
        lbl1 = new JLabel("Dashboard");
        lbl1.setBounds(100, 20, 200, 40);
        lbl1.setFont(new Font("Ariel", Font.BOLD, 34));
        frame.add(lbl1);

        // Search bar
        Search = new JTextField("🔍 Search");
        Search.setBounds(300, 20, 300, 40);
        Search.putClientProperty(FlatClientProperties.STYLE, ""
                + "roundRect:true;"
                + "showClearButton:true;");
        frame.add(Search);

        setupPlaceholder(Search, " 🔍 Search");
        Search.setFocusable(false);
        Search.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Search.setFocusable(true);
                Search.requestFocusInWindow();
            }
        });

        Search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchAndDisplayResults();
                }
            }
        });

        Search.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (Search.getText().equals(" 🔍 Search")) {
                    Search.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (Search.getText().isEmpty()) {
                    setupPlaceholder(Search, " 🔍 Search");
                }
            }
        });

        Search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Search.transferFocus();
                    if (Search.getText().isEmpty()) {
                        setupPlaceholder(Search, " 🔍 Search");
                    }
                }
            }
        });

        Search.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!Search.contains(e.getPoint())) {
                    Search.transferFocus();
                    if (Search.getText().isEmpty()) {
                        setupPlaceholder(Search, " 🔍 Search");
                    }
                }
            }
        });
        
    
      

        
        JButton profileButton = new JButton("My Profile");  // Assuming firstName is already defined
        profileButton.setBounds(868, 20, 150, 40);
        profileButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Set up FlatLaf properties for round edges
        profileButton.putClientProperty("JButton.buttonType", "round");
        profileButton.putClientProperty("JButton.arc", 30);  // Adjust the arc value for more or less roundness
        profileButton.putClientProperty("JButton.borderColor", Color.WHITE); // White border color

        profileButton.setBackground(new Color(0, 120, 215)); // Blue background color
        profileButton.setForeground(Color.WHITE);
        profileButton.setFocusPainted(false);

        // Make the button round and apply the border
        profileButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));  // White border
        profileButton.setOpaque(true);
        profileButton.setContentAreaFilled(false);  // Set the background to transparent, to see the border clearly
        profileButton.setBorderPainted(true);

        // Add the button to the frame
        frame.add(profileButton);

        // Add action listener to the profile button
        profileButton.addActionListener(e -> {
            // Clear the main panel and load the user profile page
            mainPanel.removeAll();
            try {
                loadUserProfilePage();
                lbl1.setText("Profile");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            mainPanel.revalidate();
            mainPanel.repaint();
        });

        // Set round corners by changing the border
       

        

        HideText();
        mainPanel = new JPanel();
        mainPanel.setBounds(100, 70, 970, 480);
        mainPanel.setLayout(null);
        
        
       

        lbl1.setText("Dashboard");
        loadPage(new DefaultPage(frame, mainPanel), frame, mainPanel);
        frame.add(mainPanel);
        frame.setVisible(true);
    }
   
    private void searchAndDisplayResults() {
        String searchText = Search.getText().trim();

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a name to search.", "Search Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String modifiedSearchText = "%" + searchText + "%";
        JDialog infoDialog = new JDialog();
        infoDialog.setTitle("Search Results");
        infoDialog.setSize(600, 700);
        infoDialog.setLayout(new BorderLayout());
        infoDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        infoDialog.setLocationRelativeTo(null);

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM orders WHERE customer_name LIKE ? OR product LIKE ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                stmt.setString(1, modifiedSearchText);
                stmt.setString(2, modifiedSearchText);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(frame, "No search results found.\n Or the Customer didn't made an order yet", "No Results", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    rs.beforeFirst(); // Reset cursor
                    JPanel mainPanel = new JPanel();
                    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

                    int orderCount = 1;
                    while (rs.next()) {
                        JPanel orderPanel = new JPanel();
                        orderPanel.setLayout(null);
                        orderPanel.setBorder(BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(new Color(200, 150, 50), 2),
                                "Order " + orderCount++,
                                TitledBorder.LEFT,
                                TitledBorder.TOP,
                                new Font("Arial", Font.BOLD, 16),
                                new Color(200, 150, 50))); 

                        int yPosition = 30;
                        int labelWidth = 150, fieldWidth = 350, fieldHeight = 25, spacing = 40;

                        addLabelAndField(orderPanel, "Customer Name:", rs.getString("customer_name"), yPosition, labelWidth, fieldWidth, fieldHeight);
                        yPosition += spacing;
                        addLabelAndField(orderPanel, "Product:", rs.getString("product"), yPosition, labelWidth, fieldWidth, fieldHeight);
                        yPosition += spacing;
                        addLabelAndField(orderPanel, "Quantity:", String.valueOf(rs.getInt("quantity")), yPosition, labelWidth, fieldWidth, fieldHeight);
                        yPosition += spacing;
                        addLabelAndField(orderPanel, "Price:", String.format("%.2f", rs.getDouble("price")), yPosition, labelWidth, fieldWidth, fieldHeight);
                        yPosition += spacing;
                        addLabelAndField(orderPanel, "Total Price:", String.format("%.2f", rs.getDouble("total_price")), yPosition, labelWidth, fieldWidth, fieldHeight);
                        yPosition += spacing;
                        addLabelAndField(orderPanel, "Received Date:", rs.getDate("received_date").toString(), yPosition, labelWidth, fieldWidth, fieldHeight);
                        yPosition += spacing;
                        addLabelAndField(orderPanel, "Deadline Date:", rs.getDate("deadline_date").toString(), yPosition, labelWidth, fieldWidth, fieldHeight);
                        yPosition += spacing;
                        addLabelAndField(orderPanel, "Progress:", rs.getString("progress"), yPosition, labelWidth, fieldWidth, fieldHeight);

                        orderPanel.setPreferredSize(new Dimension(550, yPosition + 50));
                        mainPanel.add(orderPanel);
                    }

                    JScrollPane scrollPane = new JScrollPane(mainPanel);
                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    infoDialog.add(scrollPane, BorderLayout.CENTER);
                    infoDialog.setVisible(true);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading data from the database.\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addLabelAndField(JPanel panel, String labelText, String fieldValue, int yPosition, int labelWidth, int fieldWidth, int fieldHeight) {
        JLabel label = new JLabel(labelText);
        label.setBounds(20, yPosition, labelWidth, fieldHeight);
        panel.add(label);

        JTextField textField = new JTextField(fieldValue);
        textField.setBounds(180, yPosition, fieldWidth, fieldHeight);
        textField.setEditable(false);
        panel.add(textField);
    }


    private JButton createButton(String text, String iconPath, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(new Color(80, 80, 80,100));
        button.setForeground(Color.WHITE); // Default text color

        URL iconURL = getClass().getResource(iconPath);
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(iconURL)
                    .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            button.setIcon(icon);
        } else {
            System.out.println("Icon not found: " + iconPath);
        }
        button.setIconTextGap(20);

        // Mouse hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.ORANGE); // Change text color on hover

               
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE); // Restore original text color
            }
        });

        return button;
    }





    private void toggleSidePanel() {
        if (isExpanded) {
            sidePanel.setBounds(10, 10, 70, 540);
            toggleButton.setText("☰");
            mainPanel.setLocation(100, 70); // Move instead of resize
            lbl1.setLocation(100, 20);
            Search.setLocation(300, 20);
            HideText();
        } else {
            sidePanel.setBounds(10, 10, 200, 540);
            toggleButton.setText("✕");
            mainPanel.setLocation(220, 70); // Move instead of resize
            lbl1.setLocation(220, 20);
            Search.setLocation(420, 20);
            ShowText();
        }
        isExpanded = !isExpanded;
    }

    private void HideText() {
	button1.setText("");
	button2.setText("");
	button3.setText("");
	
	button5.setText("");
	label.setText("");
 	signOutButton.setText("");
	
	button1.setBounds(10, 50, 50, 40); 
    button2.setBounds(10, 93, 50, 40);
    button3.setBounds(10, 136, 50, 40);
   
    button5.setBounds(10, 179, 50, 40);
    signOutButton.setBounds( 10, 480, 50, 40);

    button1.setHorizontalAlignment(SwingConstants.CENTER);
    button2.setHorizontalAlignment(SwingConstants.CENTER);
    button3.setHorizontalAlignment(SwingConstants.CENTER);
 
    button5.setHorizontalAlignment(SwingConstants.CENTER);
    signOutButton.setHorizontalAlignment(SwingConstants.CENTER);

	
	}
    private void ShowText() {
    	button1.setText("Dashboard");
    	button2.setText("Customers");
    	button3.setText("Orders");
    
    	button5.setText("Users");
    	signOutButton.setText("Sign Out");
    	label.setText("Options");
    	
    	button1.setBounds(10, 50, 180, 40);
        button2.setBounds(10, 93, 180, 40);
        button3.setBounds(10, 136, 180, 40);
     
        button5.setBounds(10, 179, 180, 40);
        signOutButton.setBounds( 10, 480, 180, 40);
       
        button1.setHorizontalAlignment(SwingConstants.LEFT);
        button2.setHorizontalAlignment(SwingConstants.LEFT);
        button3.setHorizontalAlignment(SwingConstants.LEFT);
   
        button5.setHorizontalAlignment(SwingConstants.LEFT);
        signOutButton.setHorizontalAlignment(SwingConstants.LEFT);
    	}

	private void loadPage(JPanel page, JFrame frame, JPanel mainPanel) {
    	mainPanel.removeAll();
        page.setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());
        mainPanel.add(page);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
	

    private static void setupPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.LIGHT_GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
  
    private void loadUserProfilePage() throws SQLException {
        // Clear the main panel
        mainPanel.removeAll();
        mainPanel.setLayout(null); // Use absolute positioning for simplicity

        // Fetch the signed-in user's information from the currentUser
        String signedInUsername = WelcomePage.currentUser; // Access the static currentUser variable from WelcomePage
        if (signedInUsername == null) {
            JOptionPane.showMessageDialog(frame, "No user is signed in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch user details from the database
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT full_name, user_name, email, phone_number, gender, password FROM users WHERE user_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, signedInUsername);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String fullName = rs.getString("full_name");
                    String username = rs.getString("user_name");
                    String email = rs.getString("email");
                    String phoneNumber = rs.getString("phone_number");
                    String gender = rs.getString("gender");
                    String password = rs.getString("password"); // Load password from the database

                    // Create labels and text fields for user information
                    JLabel fullNameLabel = new JLabel("Full Name:");
                    fullNameLabel.setBounds(50, 50, 100, 30);
                    mainPanel.add(fullNameLabel);

                    JTextField fullNameField = new JTextField(fullName);
                    fullNameField.setBounds(150, 50, 300, 30);
                    fullNameField.setEditable(false); // Initially non-editable
                    mainPanel.add(fullNameField);

                    JLabel usernameLabel = new JLabel("Username:");
                    usernameLabel.setBounds(50, 100, 100, 30);
                    mainPanel.add(usernameLabel);

                    JTextField usernameField = new JTextField(username);
                    usernameField.setBounds(150, 100, 300, 30);
                    usernameField.setEditable(false); // Username cannot be changed
                    mainPanel.add(usernameField);

                    JLabel emailLabel = new JLabel("Email:");
                    emailLabel.setBounds(50, 150, 100, 30);
                    mainPanel.add(emailLabel);

                    JTextField emailField = new JTextField(email);
                    emailField.setBounds(150, 150, 300, 30);
                    emailField.setEditable(false); // Initially non-editable
                    mainPanel.add(emailField);

                    JLabel phoneLabel = new JLabel("Phone Number:");
                    phoneLabel.setBounds(50, 200, 100, 30);
                    mainPanel.add(phoneLabel);

                    JTextField phoneField = new JTextField(phoneNumber);
                    phoneField.setBounds(150, 200, 300, 30);
                    phoneField.setEditable(false); // Initially non-editable
                    mainPanel.add(phoneField);

                    JLabel genderLabel = new JLabel("Gender:");
                    genderLabel.setBounds(50, 250, 100, 30);
                    mainPanel.add(genderLabel);

                    JTextField genderField = new JTextField(gender);
                    genderField.setBounds(150, 250, 300, 30);
                    genderField.setEditable(false); // Initially non-editable
                    mainPanel.add(genderField);

                    // Password field
                    JLabel passwordLabel = new JLabel("Password:");
                    passwordLabel.setBounds(50, 300, 100, 30);
                    mainPanel.add(passwordLabel);

                    JPasswordField passwordField = new JPasswordField(password);
                    passwordField.setBounds(150, 300, 300, 30);
                    passwordField.setEditable(false); // Disable direct editing
                    passwordField.putClientProperty(FlatClientProperties.STYLE, "roundRect:true;showRevealButton:true");
                    mainPanel.add(passwordField);

                    // Edit Profile / Save Changes button
                    JButton editProfileButton = new JButton("Edit Profile");
                    editProfileButton.setBounds(150, 350, 150, 30);
                    editProfileButton.setBackground(new Color(0, 120, 215)); // Blue color
                    editProfileButton.setForeground(Color.WHITE);
                    editProfileButton.setFocusPainted(false);
                    editProfileButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                    mainPanel.add(editProfileButton);

                    // Change Password button
                    JButton changePasswordButton = new JButton("Change Password");
                    changePasswordButton.setBounds(320, 350, 150, 30);
                    changePasswordButton.setBackground(new Color(0, 120, 215)); // Blue color
                    changePasswordButton.setForeground(Color.WHITE);
                    changePasswordButton.setFocusPainted(false);
                    changePasswordButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                    mainPanel.add(changePasswordButton);

                    // Add action listener to the Edit Profile / Save Changes button
                    editProfileButton.addActionListener(e -> {
                        if (editProfileButton.getText().equals("Edit Profile")) {
                            // Enable editing of fields
                            fullNameField.setEditable(true);
                            emailField.setEditable(true);
                            phoneField.setEditable(true);
                            genderField.setEditable(true);
                            editProfileButton.setText("Save Changes"); // Toggle to Save button
                        } else {
                            // Save changes
                            String newFullName = fullNameField.getText();
                            String newEmail = emailField.getText();
                            String newPhoneNumber = phoneField.getText();
                            String newGender = genderField.getText();

                            if (newFullName.isEmpty() || newEmail.isEmpty() || newPhoneNumber.isEmpty() || newGender.isEmpty()) {
                                JOptionPane.showMessageDialog(frame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            // Update user information in the database
                            try (Connection connUpdate = Database.getConnection()) {
                                String updateSql = "UPDATE users SET full_name = ?, email = ?, phone_number = ?, gender = ? WHERE user_name = ?";
                                try (PreparedStatement pstmtUpdate = connUpdate.prepareStatement(updateSql)) {
                                    pstmtUpdate.setString(1, newFullName);
                                    pstmtUpdate.setString(2, newEmail);
                                    pstmtUpdate.setString(3, newPhoneNumber);
                                    pstmtUpdate.setString(4, newGender);
                                    pstmtUpdate.setString(5, username);
                                    pstmtUpdate.executeUpdate();
                                    JOptionPane.showMessageDialog(frame, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(frame, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
                            }

                            // Disable editing of fields
                            fullNameField.setEditable(false);
                            emailField.setEditable(false);
                            phoneField.setEditable(false);
                            genderField.setEditable(false);
                            editProfileButton.setText("Edit Profile"); // Toggle back to Edit button
                        }
                    });

                    // Add action listener to the Change Password button
                    changePasswordButton.addActionListener(e -> {
                        // Create a new JDialog for the password change UI
                        JDialog passwordChangeDialog = new JDialog(frame, "Change Password", true); // Modal dialog
                        passwordChangeDialog.setLayout(null); // Use null layout
                        passwordChangeDialog.setSize(400, 300); // Set dialog size
                        passwordChangeDialog.setLocationRelativeTo(frame); // Center the dialog relative to the main frame

                        // Create the change password panel
                        JPanel changePasswordPanel = new JPanel();
                        changePasswordPanel.setLayout(null); // Use null layout
                        changePasswordPanel.setBounds(0, 0, 400, 300); // Set bounds for the panel

                        // Password fields
                        JPasswordField currentPasswordField = new JPasswordField();
                        currentPasswordField.setBounds(150, 50, 200, 30); // Set bounds for current password field

                        JPasswordField newPasswordField = new JPasswordField();
                        newPasswordField.setBounds(150, 120, 200, 30); // Set bounds for new password field

                        // Add FlatLaf reveal button for current password field
                        currentPasswordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
                        // Add FlatLaf reveal button for new password field
                        newPasswordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");

                        // Label to display "Not correct" in red
                        JLabel errorLabel = new JLabel();
                        errorLabel.setForeground(Color.RED); // Set text color to red
                        errorLabel.setBounds(150, 80, 200, 20); // Set bounds for error label
                        errorLabel.setVisible(false); // Initially hidden

                        // Labels for fields
                        JLabel currentPasswordLabel = new JLabel("Current Password:");
                        currentPasswordLabel.setBounds(20, 50, 120, 30); // Set bounds for label

                        JLabel newPasswordLabel = new JLabel("New Password:");
                        newPasswordLabel.setBounds(20, 120, 120, 30); // Set bounds for label

                        // Add components to the change password panel
                        changePasswordPanel.add(currentPasswordLabel);
                        changePasswordPanel.add(currentPasswordField);
                        changePasswordPanel.add(errorLabel); // Add the error label below the current password field
                        changePasswordPanel.add(newPasswordLabel);
                        changePasswordPanel.add(newPasswordField);

                        // Add FocusListener to currentPasswordField to validate the password on focus loss
                        currentPasswordField.addFocusListener(new FocusAdapter() {
                            @Override
                            public void focusLost(FocusEvent e) {
                                String enteredPassword = new String(currentPasswordField.getPassword());
                                if (!enteredPassword.equals(password)) {
                                    errorLabel.setText("Password not correct!"); // Show error message
                                    errorLabel.setVisible(true); // Make the label visible
                                } else {
                                    errorLabel.setVisible(false); // Hide the label if the password is correct
                                }
                            }
                        });

                        // Create Save and Cancel buttons
                        JButton saveButton = new JButton("Save");
                        saveButton.setBounds(100, 200, 100, 30); // Set bounds for Save button

                        JButton cancelButton = new JButton("Cancel");
                        cancelButton.setBounds(220, 200, 100, 30); // Set bounds for Cancel button

                        // Style the buttons
                        saveButton.setBackground(new Color(0, 120, 215,50)); // Blue color
                        saveButton.setForeground(Color.WHITE);
                        saveButton.setFocusPainted(false);
                        saveButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

                        cancelButton.setBackground(new Color(200, 50, 50)); // Red color
                        cancelButton.setForeground(Color.WHITE);
                        cancelButton.setFocusPainted(false);
                        cancelButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

                        // Add buttons to the change password panel
                        changePasswordPanel.add(saveButton);
                        changePasswordPanel.add(cancelButton);

                        // Add action listener to the Save button
                        saveButton.addActionListener(saveEvent -> {
                            String currentPassword = new String(currentPasswordField.getPassword());
                            String newPassword = new String(newPasswordField.getPassword());

                            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                                JOptionPane.showMessageDialog(passwordChangeDialog, "Both fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            // Check if the current password is correct
                            if (!currentPassword.equals(password)) {
                                JOptionPane.showMessageDialog(passwordChangeDialog, "Incorrect current password.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            // Update the password in the database
                            try (Connection connUpdate = Database.getConnection()) {
                                String updateSql = "UPDATE users SET password = ? WHERE user_name = ?";
                                try (PreparedStatement pstmtUpdate = connUpdate.prepareStatement(updateSql)) {
                                    pstmtUpdate.setString(1, newPassword);
                                    pstmtUpdate.setString(2, username);
                                    pstmtUpdate.executeUpdate();
                                    JOptionPane.showMessageDialog(passwordChangeDialog, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    passwordField.setText(newPassword); // Update the displayed password
                                    passwordChangeDialog.dispose(); // Close the dialog
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(passwordChangeDialog, "Failed to update password.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });

                        // Add action listener to the Cancel button
                        cancelButton.addActionListener(cancelEvent -> {
                            passwordChangeDialog.dispose(); // Close the dialog
                        });

                        // Add the change password panel to the dialog
                        passwordChangeDialog.add(changePasswordPanel);

                        // Make the dialog visible
                        passwordChangeDialog.setVisible(true);
                    });
                }
            }
        }
    }
        // Method to check if the current password is correct
        private boolean isCurrentPasswordCorrect(String username, String currentPassword) {
            try (Connection conn = Database.getConnection()) {
                String sql = "SELECT * FROM users WHERE user_name = ? AND password = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, currentPassword);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        return rs.next(); // If result set has a row, current password is correct
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false; // Return false in case of SQL errors
            }
        }
    
    
   
    private boolean updatePasswordInDatabase(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_name = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
}
