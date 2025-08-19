package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customers extends JPanel {

    public Customers(JFrame frame, JPanel mainPanel) {
        setLayout(null);
        setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());

      
        String[] columnNames = { "No.", "Full Name", "Primary Contact", "Secondary Contact", "Company Name", "Company Contact" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0; 
            }
        };

      
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setPreferredWidth(5);
        
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(new Color(200, 150, 50)); 
        tableHeader.setForeground(Color.BLACK); 
        tableHeader.setFont(new Font("SansSerif", Font.BOLD, 12));

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        
        
        loadCustomersFromDatabase( model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 0, mainPanel.getWidth() - 50, mainPanel.getHeight() - 100);
        add(scrollPane);

        JButton addButton = createButton("Add Customer", 50, mainPanel.getHeight() - 80, 150, 40);
        add(addButton);

        addButton.addActionListener(e -> showAddCustomerDialog(mainPanel, model));

        JButton deleteButton = createButton("Delete", 250, mainPanel.getHeight() - 80, 150, 40);
        add(deleteButton);
        
        deleteButton.addActionListener(e -> deleteCustomer(mainPanel, model, table));
     // Add the export button beside the delete button
        JButton exportButton = createButton("Export", 450, mainPanel.getHeight() - 80, 150, 40);
        add(exportButton);

        // Add action listener to the export button
        exportButton.addActionListener(e -> exportTableToCSV(table));
        
        
    }

    private static JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBackground(new Color(0, 120, 215,50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void loadCustomersFromDatabase(DefaultTableModel model) {
        model.setRowCount(0); 

        String sql = "SELECT Name, PrimaryContact, SecondaryContact, CompanyName, CompanyPhone FROM Customers";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String fullName = rs.getString("Name");
                String primaryContact = rs.getString("PrimaryContact");
                String secondaryContact = rs.getString("SecondaryContact");
                String companyName = rs.getString("CompanyName");
                String companyContact = rs.getString("CompanyPhone");

                model.addRow(new Object[]{
                    model.getRowCount() + 1, 
                    fullName,
                    primaryContact,
                    secondaryContact,
                    companyName,
                    companyContact
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading customers from database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportTableToCSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");
        
        // Set default file name
        fileChooser.setSelectedFile(new File("customers.csv"));
        
        int userSelection = fileChooser.showSaveDialog(null);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Write column headers
                TableModel model = table.getModel();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.write(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
                
                // Write table data
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        writer.write(model.getValueAt(i, j).toString());
                        if (j < model.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.write("\n");
                }
                
                JOptionPane.showMessageDialog(null, "Data exported successfully to " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error exporting data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

 
    private static void showAddCustomerDialog(JPanel panel, DefaultTableModel model) {
        JDialog addCustomerDialog = new JDialog();
        addCustomerDialog.setUndecorated(true);
        addCustomerDialog.setModal(true);
        addCustomerDialog.setSize(500, 580); // Adjusted size for better spacing
        addCustomerDialog.setLayout(null);
        addCustomerDialog.setLocationRelativeTo(null);

       
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

        
        
        companyCheckBox.addItemListener(e -> {
            boolean selected = companyCheckBox.isSelected();
            companyNameLabel.setVisible(selected);
            companyNameField.setVisible(selected);
            companyPhoneLabel.setVisible(selected);
            companyPhoneField.setVisible(selected);
        });

        // Add Button Action
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

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    Object[] customerData = {
                        model.getRowCount() + 1,
                        name,
                        primaryContact,
                        secondaryContact.isEmpty() ? "None" : secondaryContact,
                        companyName,
                        companyPhone
                    };
                    model.addRow(customerData);
                    JOptionPane.showMessageDialog(addCustomerDialog, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    addCustomerDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addCustomerDialog, "Failed to add customer!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(addCustomerDialog, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addCustomerDialog.dispose());

        addCustomerDialog.setVisible(true);
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


    private static void deleteCustomer(JPanel panel, DefaultTableModel model, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int confirmation = JOptionPane.showConfirmDialog(panel, "Delete this customer?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
               
                String name = model.getValueAt(selectedRow, 1).toString();
                String primaryContact = model.getValueAt(selectedRow, 2).toString();

                String sql = "DELETE FROM Customers WHERE Name = ? AND PrimaryContact = ?";

                try (Connection conn = Database.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, name);
                    pstmt.setString(2, primaryContact);

                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        
                        model.removeRow(selectedRow);
                       
                        for (int i = 0; i < model.getRowCount(); i++) {
                            model.setValueAt(i + 1, i, 0);
                        }
                        JOptionPane.showMessageDialog(panel, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Failed to delete customer. Customer not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error deleting customer from database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(panel, "Please select a row to delete!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


}
