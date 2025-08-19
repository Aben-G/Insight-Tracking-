package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.formdev.flatlaf.FlatClientProperties;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManageUsers extends JPanel {

    public ManageUsers(JFrame frame, JPanel mainPanel) {
        setLayout(null);
        setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());

        String[] columnNames = { "No.", "Full Name", "UserName", "Email", "Phone Number", "Gender", "id"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }
        };
        

        JTable table = new JTable(model);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 0, mainPanel.getWidth() - 50, mainPanel.getHeight() - 100);
        add(scrollPane);
        
        table.removeColumn(table.getColumnModel().getColumn(6));
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        
        
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setMaxWidth(200);
        table.getColumnModel().getColumn(1).setMinWidth(200);
        
        
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setMaxWidth(200);
        table.getColumnModel().getColumn(3).setMinWidth(200);
        
        
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(new Color(200, 150, 50)); 
        tableHeader.setForeground(Color.BLACK); 
        tableHeader.setFont(new Font("SansSerif", Font.BOLD, 12));

        loadUsersFromDatabase(model);
        
        JButton addButton = createButton("Add User", 50, mainPanel.getHeight() - 80, 150, 40);
        add(addButton);
        addButton.addActionListener(e -> showAddUserDialog(mainPanel, model));

        JButton deleteButton = createButton("Delete", 250, mainPanel.getHeight() - 80, 150, 40);
        add(deleteButton);
        deleteButton.addActionListener(e -> deleteUser(mainPanel, model, table));
        
        JButton editButton = createButton("Edit User", 450, mainPanel.getHeight() - 80, 150, 40);
        add(editButton);
        editButton.addActionListener(e -> showEditUserDialog(mainPanel, model, table));
    }
    
   
    
    private static void showEditUserDialog(JPanel panel, DefaultTableModel model, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(panel, "Please select a user to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

     
        Object idObj = model.getValueAt(selectedRow, 6); // Assuming the ID is in the 6th column
        String id = idObj != null ? idObj.toString() : ""; // Convert to String if it's an Integer

        String fullName = (String) model.getValueAt(selectedRow, 1);
        String userName = (String) model.getValueAt(selectedRow, 2);
        String email = (String) model.getValueAt(selectedRow, 3);
        String phoneNumber = (String) model.getValueAt(selectedRow, 4);
        String gender = (String) model.getValueAt(selectedRow, 5);

        JDialog editUserDialog = new JDialog();
        editUserDialog.setUndecorated(true);
        editUserDialog.setModal(true);
        editUserDialog.setSize(500, 580); // Adjusted size for better spacing
        editUserDialog.setLayout(null);
        editUserDialog.setLocationRelativeTo(null);

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
        editUserDialog.add(mainPanel);
        editUserDialog.getRootPane().putClientProperty("JComponent.roundRect", true);

        // Title Label
        JLabel titleLabel = new JLabel("Edit User");
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
        closeButton.addActionListener(e -> editUserDialog.dispose());
        mainPanel.add(closeButton);

        // Full Name Field
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setBounds(50, 70, 150, 30);
        mainPanel.add(fullNameLabel);

        JTextField fullNameField = new JTextField(fullName);
        fullNameField.setBounds(200, 70, 250, 30);
        fullNameField.setBackground(new Color(255, 255, 255, 100));
        fullNameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(fullNameField);
        fullNameField.setForeground(Color.black);

        // Username Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 110, 150, 30);
        mainPanel.add(usernameLabel);

        JTextField usernameField = new JTextField(userName);
        usernameField.setBounds(200, 110, 250, 30);
        usernameField.setBackground(new Color(255, 255, 255, 100));
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(usernameField);
        usernameField.setForeground(Color.black);

        // Email Field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 150, 150, 30);
        mainPanel.add(emailLabel);

        JTextField emailField = new JTextField(email);
        emailField.setBounds(200, 150, 250, 30);
        emailField.setBackground(new Color(255, 255, 255, 100));
        emailField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(emailField);
        emailField.setForeground(Color.black);

        // Gender Field
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(50, 190, 150, 30);
        mainPanel.add(genderLabel);

        JComboBox<String> genderField = new JComboBox<>(new String[]{"Male", "Female"});
        genderField.setSelectedItem(gender);
        genderField.setBounds(200, 190, 250, 30);
        genderField.setBackground(new Color(255, 255, 255, 100));
        genderField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(genderField);
        genderField.setForeground(Color.black);

        // Phone Number Field
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(50, 230, 150, 30);
        mainPanel.add(phoneLabel);

        JTextField phoneField = new JTextField(phoneNumber);
        phoneField.setBounds(200, 230, 250, 30);
        phoneField.setBackground(new Color(255, 255, 255, 100));
        phoneField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(phoneField);
        phoneField.setForeground(Color.black);

        // Save and Cancel Buttons
        JButton saveButton = createButton("Save", 100, 500, 120, 40, new Color(0, 120, 215,50));
        mainPanel.add(saveButton);

        JButton cancelButton = createButton("Cancel", 280, 500, 120, 40, new Color(150, 0, 0, 180));
        mainPanel.add(cancelButton);

        saveButton.addActionListener(e -> {
            String newFullName = fullNameField.getText();
            String newUserName = usernameField.getText();
            String newEmail = emailField.getText();
            String newGender = (String) genderField.getSelectedItem();
            String newPhoneNumber = phoneField.getText();

           
            try (Connection conn = Database.getConnection()) {
                String sql = "UPDATE users SET full_name = ?, user_name = ?, email = ?, gender = ?, phone_number = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newFullName);
                    pstmt.setString(2, newUserName);
                    pstmt.setString(3, newEmail);
                    pstmt.setString(4, newGender);
                    pstmt.setString(5, newPhoneNumber);
                    pstmt.setString(6, id);

                    int rowsUpdated = pstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(editUserDialog, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        model.setValueAt(newFullName, selectedRow, 1);
                        model.setValueAt(newUserName, selectedRow, 2);
                        model.setValueAt(newEmail, selectedRow, 3);
                        model.setValueAt(newPhoneNumber, selectedRow, 4);
                        model.setValueAt(newGender, selectedRow, 5);
                        editUserDialog.dispose();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(editUserDialog, "Error updating user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> editUserDialog.dispose());

        editUserDialog.setVisible(true);
    }
    

    private void loadUsersFromDatabase(DefaultTableModel model) {
        try (Connection conn = Database.getConnection()) {
            // Include the "id" column in the SQL query
            String sql = "SELECT id, full_name, user_name, email, phone_number, gender FROM users";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                int count = 1;
                while (rs.next()) {
                
                    int id = rs.getInt("id");
                    String fullName = rs.getString("full_name");
                    String username = rs.getString("user_name");
                    String email = rs.getString("email");
                    String phoneNumber = rs.getString("phone_number");
                    String gender = rs.getString("gender");

                    // Add the "id" column to the table model
                    model.addRow(new Object[]{count++, fullName, username, email, phoneNumber, gender, id});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users from database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBackground(new Color(0, 120, 215,50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private static void showAddUserDialog(JPanel panel, DefaultTableModel model) {
        JDialog addUserDialog = new JDialog();
        addUserDialog.setUndecorated(true);
        addUserDialog.setModal(true);
        addUserDialog.setSize(500, 580); // Adjusted size for better spacing
        addUserDialog.setLayout(null);
        addUserDialog.setLocationRelativeTo(null);

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
        addUserDialog.add(mainPanel);
        addUserDialog.getRootPane().putClientProperty("JComponent.roundRect", true);

        // Title Label
        JLabel titleLabel = new JLabel("Add User");
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
        closeButton.addActionListener(e -> addUserDialog.dispose());
        mainPanel.add(closeButton);

        // Full Name Field
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setBounds(50, 70, 150, 30);
        mainPanel.add(fullNameLabel);

        JTextField fullNameField = new JTextField();
        fullNameField.setBounds(200, 70, 250, 30);
        fullNameField.setBackground(new Color(255, 255, 255, 100));
        fullNameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(fullNameField);
        fullNameField.setForeground(Color.black);
        // Username Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 110, 150, 30);
        mainPanel.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(200, 110, 250, 30);
        usernameField.setBackground(new Color(255, 255, 255, 100));
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(usernameField);
        usernameField.setForeground(Color.black);
        // Email Field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 150, 150, 30);
        mainPanel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(200, 150, 250, 30);
        emailField.setBackground(new Color(255, 255, 255, 100));
        emailField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(emailField);
        emailField.setForeground(Color.black);
        // Gender Field
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(50, 190, 150, 30);
        mainPanel.add(genderLabel);

        JComboBox<String> genderField = new JComboBox<>(new String[]{"Male", "Female"});
        genderField.setBounds(200, 190, 250, 30);
        genderField.setBackground(new Color(255, 255, 255, 100));
        genderField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(genderField);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 230, 150, 30);
        mainPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(200, 230, 250, 30);
        passwordField.setBackground(new Color(255, 255, 255, 100));
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(passwordField);
        passwordField.setForeground(Color.black);
        // Confirm Password Field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setBounds(50, 270, 150, 30);
        mainPanel.add(confirmPasswordLabel);

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(200, 270, 250, 30);
        confirmPasswordField.setBackground(new Color(255, 255, 255, 100));
        confirmPasswordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(confirmPasswordField);
        confirmPasswordField.setForeground(Color.black);
        // Phone Number Field
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(50, 310, 150, 30);
        mainPanel.add(phoneLabel);

        JTextField phoneField = new JTextField();
        phoneField.setBounds(200, 310, 250, 30);
        phoneField.setBackground(new Color(255, 255, 255, 100));
        phoneField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(phoneField);
        phoneField.setForeground(Color.black);
        // Add and Cancel Buttons
        JButton addButton = createButton("Add", 100, 500, 120, 40, new Color(0, 120, 215,50));
        mainPanel.add(addButton);

        JButton cancelButton = createButton("Cancel", 280, 500, 120, 40, new Color(150, 0, 0, 180));
        mainPanel.add(cancelButton);
        
       

        addButton.addActionListener(e -> {
            String FullNameText = fullNameField.getText();
            String UserNameText = usernameField.getText();
            String emailText = emailField.getText();
            String genderText = (String) genderField.getSelectedItem();
            String passwordText = new String(passwordField.getPassword());
            String confirmPasswordText = new String(confirmPasswordField.getPassword());
            String phoneText = phoneField.getText();

            // Check if passwords match
            if (!passwordText.equals(confirmPasswordText)) {
                JOptionPane.showMessageDialog(addUserDialog, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (emailText.isEmpty()) {
                emailText = "none";
            }
            
            // Check if username or email already exists
            try (Connection conn = Database.getConnection()) {
                // Check if username exists
                String checkUsernameSql = "SELECT * FROM users WHERE user_name = ?";
                try (PreparedStatement checkUsernameStmt = conn.prepareStatement(checkUsernameSql)) {
                    checkUsernameStmt.setString(1, UserNameText);
                    try (ResultSet rs = checkUsernameStmt.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(addUserDialog, "Username already taken!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

             // Check if email exists (except for "none")
                if (emailText != null && !emailText.equals("none")) {
                    String checkEmailSql = "SELECT * FROM users WHERE email = ? AND email <> 'none'";
                    try (PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql)) {
                        checkEmailStmt.setString(1, emailText);
                        try (ResultSet rs = checkEmailStmt.executeQuery()) {
                            if (rs.next()) {
                                JOptionPane.showMessageDialog(addUserDialog, "Email already taken!", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }
                }

                // Insert new user if username and email are unique
                String insertSql = "INSERT INTO users (full_name, user_name, email, gender, password, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, FullNameText);
                    insertStmt.setString(2, UserNameText);
                    insertStmt.setString(3, emailText);
                    insertStmt.setString(4, genderText);
                    insertStmt.setString(5, passwordText);
                    insertStmt.setString(6, phoneText);

                    int rowsInserted = insertStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        // Retrieve the auto-generated ID
                        try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int id = generatedKeys.getInt(1); // Get the generated ID

                                // Add the new user to the table
                                Object[] rowData = {
                                    model.getRowCount() + 1, // No.
                                    FullNameText,            // Full Name
                                    UserNameText,            // Username
                                    emailText,               // Email
                                    phoneText,               // Phone Number
                                    genderText,             // Gender
                                    id                      // ID (hidden)
                                };
                                model.addRow(rowData); // Add the row to the table model
                            }
                        }

                        JOptionPane.showMessageDialog(addUserDialog, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        addUserDialog.dispose();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addUserDialog, "Error during registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        fullNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	usernameField.requestFocus();  
                }
            }
        });
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	emailField.requestFocus();  
                }
            }
        });
        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	genderField.requestFocus();  
                }
            }
        });
        genderField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	passwordField.requestFocus();  
                }
            }
        });
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	confirmPasswordField.requestFocus();  
                }
            }
        });

        confirmPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	phoneField.requestFocus();  
                }
            }
        });
        phoneField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	addButton.requestFocus();  
                }
            }
        });

        cancelButton.addActionListener(e -> addUserDialog.dispose());

        addUserDialog.setVisible(true);
    }

    private static JButton createButton(String text, int x, int y, int width, int height, Color color) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    private static void deleteUser(JPanel panel, DefaultTableModel model, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int confirmation = JOptionPane.showConfirmDialog(panel, "Delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                // Convert view index to model index
                int modelRow = table.convertRowIndexToModel(selectedRow);

                // Retrieve the userId from the correct column index (6 for "id")
                int userId = (int) model.getValueAt(modelRow, 6); // Corrected column index

                try (Connection conn = Database.getConnection()) {
                    if (conn == null) {
                        JOptionPane.showMessageDialog(panel, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String sql = "DELETE FROM users WHERE id = ?"; // Ensure `id` is the correct column name
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, userId);
                        int rowsDeleted = pstmt.executeUpdate();
                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(panel, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            model.removeRow(modelRow); // Remove the row from the model

                            // Reset the "No." column for remaining rows
                            for (int i = 0; i < model.getRowCount(); i++) {
                                model.setValueAt(i + 1, i, 0); // Update the "No." column
                            }
                        } else {
                            JOptionPane.showMessageDialog(panel, "Failed to delete user!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error deleting user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(panel, "Please select a row to delete!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}