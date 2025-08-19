package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.formdev.flatlaf.json.ParseException;


import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;


public class Orders extends JPanel {

    private JTable orderTable;
    private DefaultTableModel tableModel;

    public Orders(JFrame frame, JPanel mainPanel) {
        setLayout(null);
        setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());

        String[] columnNames = {"No.", "Customer", "Product", "Quantity", "Price", "Advance", "Total Price", "Received Date", "Deadline Date", "Progress", "Id"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 9) {
                    return String.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        orderTable.getColumnModel().getColumn(0).setMaxWidth(50);
        orderTable.getColumnModel().getColumn(0).setMinWidth(50);
        
        
        
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        orderTable.getColumnModel().getColumn(1).setMaxWidth(160);
        orderTable.getColumnModel().getColumn(1).setMinWidth(160);
        

        JTableHeader tableHeader = orderTable.getTableHeader();
        tableHeader.setBackground(new Color(200, 150, 50));
        tableHeader.setForeground(Color.BLACK);
        tableHeader.setFont(new Font("SansSerif", Font.BOLD, 12));

        JComboBox<String> progressComboBox = new JComboBox<>(new String[]{"Pending", "Completed"});
        DefaultCellEditor progressEditor = new DefaultCellEditor(progressComboBox);
        orderTable.getColumnModel().getColumn(9).setCellEditor(progressEditor);

        // Add listener to handle progress updates
        progressEditor.addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow == -1) {
                    return; // No row selected, do nothing
                }

                try {
                    int orderId = (int) tableModel.getValueAt(selectedRow, 10);
                    String progress = (String) progressEditor.getCellEditorValue();

                    System.out.println("Updating progress for order ID: " + orderId + " with progress: " + progress);

                    // Update the database first
                    if (updateProgressInDatabase(orderId, progress)) {
                        // If the database update is successful, update the table model
                        tableModel.setValueAt(progress, selectedRow, 9);  // Assuming column 9 is the progress column
                        orderTable.repaint();
                        JOptionPane.showMessageDialog(frame, "Progress updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                       

                        // Refresh the Orders panel
                        mainPanel.removeAll(); // Remove the current Orders panel
                        mainPanel.add(new Orders(frame, mainPanel)); // Reinitialize the Orders panel
                        mainPanel.revalidate(); // Refresh the layout
                        mainPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to update progress. Possible causes:\n- Incorrect DB connection\n- Wrong column index\n- Order ID not found in DB.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid order ID format: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
                // Handle editing canceled if needed
            }
        });

        orderTable.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value == null) {
                    c.setBackground(Color.YELLOW);
                    return c;
                }
                String progressValue = value.toString();
                if ("Pending".equalsIgnoreCase(progressValue)) {
                    c.setBackground(Color.ORANGE);
                } else if ("Completed".equalsIgnoreCase(progressValue)) {
                    c.setBackground(Color.GREEN.brighter());
                } else {
                    c.setBackground(Color.gray);
                    c.setForeground(Color.black);
                }
                return c;
            }
        });

        orderTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String progress = (String) table.getValueAt(row, 9);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    if (value != null) {
                        Date deadlineDate = sdf.parse(value.toString());
                        long diff = (deadlineDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24);

                        if ("Completed".equalsIgnoreCase(progress)) {
                            c.setBackground(Color.GREEN.brighter());
                            c.setForeground(Color.BLACK);
                        } else if (diff <= 2) {
                            c.setBackground(Color.RED);
                            c.setForeground(Color.WHITE);
                        } else if (diff <= 5) {
                            c.setBackground(Color.YELLOW);
                            c.setForeground(Color.BLACK);
                        } else {
                            c.setBackground(Color.gray);
                            c.setForeground(Color.black);
                        }
                    }
                } catch (Exception e) {
                    c.setBackground(Color.gray);
                    c.setForeground(Color.black);
                }
                return c;
            }
        });

        loadOrdersFromDatabase(tableModel, orderTable);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBounds(0, 0, mainPanel.getWidth() - 50, mainPanel.getHeight() - 100);
        add(scrollPane);

        JButton addButton = createButton("Add", 50, mainPanel.getHeight() - 80, 150, 40, Color.DARK_GRAY);
        add(addButton);
        addButton.addActionListener(e -> showAddOrderForm(orderTable, tableModel));

        JButton deleteButton = createButton("Delete", 250, mainPanel.getHeight() - 80, 150, 40, Color.DARK_GRAY);
        add(deleteButton);
        deleteButton.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an order to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String customerName = tableModel.getValueAt(selectedRow, 1).toString();
            String product = tableModel.getValueAt(selectedRow, 2).toString();

            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to delete this order?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (deleteOrderFromDatabase(customerName, product)) {
                    tableModel.removeRow(selectedRow);
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        tableModel.setValueAt(i + 1, i, 0);
                    }
                    JOptionPane.showMessageDialog(frame, "Order deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to delete order from the database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton printReceiptButton = createButton("Print Receipt", 450, mainPanel.getHeight() - 80, 150, 40, Color.DARK_GRAY);
        add(printReceiptButton);
        
        printReceiptButton.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an order to print the receipt.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extract data from the selected row
            String customerName = tableModel.getValueAt(selectedRow, 1).toString();
            String product = tableModel.getValueAt(selectedRow, 2).toString();
            int quantity = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());
            double price = Double.parseDouble(tableModel.getValueAt(selectedRow, 4).toString());
            double totalPrice = Double.parseDouble(tableModel.getValueAt(selectedRow, 6).toString());
            String receivedDate = tableModel.getValueAt(selectedRow, 7).toString();
            String deadline = tableModel.getValueAt(selectedRow, 8).toString();

            // Call the showInvoiceFrame method with the extracted data
            showInvoiceFrame(customerName, product, quantity, price, totalPrice, receivedDate, deadline);
        });
        
        JButton exportButton = createButton("Export", 650, mainPanel.getHeight() - 80, 150, 40, Color.DARK_GRAY);
        add(exportButton);
        exportButton.addActionListener(e -> exportTableToCSV(orderTable));
    }
    
    private void exportTableToCSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");
        
        // Set default file name
        fileChooser.setSelectedFile(new File("Orders.csv"));
        
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
                        Object value = model.getValueAt(i, j);
                        writer.write(value != null ? value.toString() : ""); // Handle null values
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


    private static boolean updateProgressInDatabase(int orderId, String progress) {
        String sql = "UPDATE orders SET progress = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, progress);
            pstmt.setInt(2, orderId);

            System.out.println("Executing SQL: " + pstmt.toString()); 

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected); 

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating progress: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    private static JButton createButton(String text, int x, int y, int width, int height, Color color) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBackground(new Color(0, 120, 215,50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private void showAddOrderForm(JTable orderTable, DefaultTableModel tableModel) {
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
        JLabel titleLabel = new JLabel("Add Order");
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

        // Customer Warning Label
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

        // Advanced Payment Field
        JLabel advancedPaymentLabel = new JLabel("Advanced Payment:");
        advancedPaymentLabel.setBounds(50, 270, 150, 30);
        
        mainPanel.add(advancedPaymentLabel);

        JCheckBox advancedPaymentCheckBox = new JCheckBox();
        advancedPaymentCheckBox.setBounds(200, 270, 20, 30);
        advancedPaymentCheckBox.setOpaque(false);
        mainPanel.add(advancedPaymentCheckBox);

        JTextField advancedPaymentField = new JTextField("0.00");
        advancedPaymentField.setBounds(230, 270, 220, 30);
        advancedPaymentField.setBackground(new Color(255, 255, 255, 100));
        advancedPaymentField.setForeground(Color.black);
        advancedPaymentField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        advancedPaymentField.setVisible(false);
        mainPanel.add(advancedPaymentField);

        advancedPaymentCheckBox.addActionListener(e -> {
            if (advancedPaymentCheckBox.isSelected()) {
                advancedPaymentField.setVisible(true);
                advancedPaymentField.setText("0.00");
            } else {
                advancedPaymentField.setVisible(false);
                advancedPaymentField.setText("0.00");
            }
        });

        // Total Price Field
        JLabel totalPriceLabel = new JLabel("Total Price:");
        totalPriceLabel.setBounds(50, 310, 150, 30);
       
        mainPanel.add(totalPriceLabel);

        JTextField totalPriceField = new JTextField("0.00");
        totalPriceField.setBounds(200, 310, 250, 30);
        totalPriceField.setBackground(new Color(255, 255, 255, 100));
        totalPriceField.setForeground(Color.black);
        totalPriceField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        totalPriceField.setEditable(false);
        mainPanel.add(totalPriceField);

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
        // Add and Cancel Buttons
        JButton addButton = createButton("Add", 100, 500, 120, 40, new Color(0, 120, 215,50));
        mainPanel.add(addButton);
       
        
        

       
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds( 280, 500, 120, 40);
        cancelButton.setBackground(new Color(150, 0, 0, 180));
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
            String advancedPaymentText = advancedPaymentField.getText().trim();

            String deadlineDate = yearCombo.getSelectedItem() + "-" +
                    String.format("%02d", Integer.parseInt((String) monthCombo.getSelectedItem())) + "-" +
                    String.format("%02d", Integer.parseInt((String) dayCombo.getSelectedItem()));

            if (customerName.isEmpty() || product.isEmpty() || quantityText.isEmpty() || priceText.isEmpty() || receivedDate.isEmpty()) {
                JOptionPane.showMessageDialog(addOrderDialog, "All fields except advance payment must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
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
            if (advancedPaymentCheckBox.isSelected() && !advancedPaymentText.matches("\\d+(\\.\\d{1,2})?")) {
                JOptionPane.showMessageDialog(addOrderDialog, "Advanced payment must be a valid number (e.g., 100 or 100.50).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double totalPrice = Integer.parseInt(quantityText) * Double.parseDouble(priceText);
            double advancedPayment = advancedPaymentCheckBox.isSelected() ? Double.parseDouble(advancedPaymentText) : 0.00;
            totalPriceField.setText(String.valueOf(totalPrice));

            try (Connection conn = Database.getConnection()) {
                String insertQuery = "INSERT INTO orders (customer_name, product, quantity, price, total_price, advance_payment, received_date, deadline_date, progress) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

                    stmt.setString(1, customerName);
                    stmt.setString(2, product);
                    stmt.setInt(3, Integer.parseInt(quantityText));
                    stmt.setDouble(4, Double.parseDouble(priceText));
                    stmt.setDouble(5, totalPrice);
                    stmt.setDouble(6, advancedPayment);

                    try {
                        stmt.setDate(7, java.sql.Date.valueOf(receivedDate)); // receivedDate is expected to be "yyyy-MM-dd"
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(addOrderDialog, "Invalid received date format. Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        stmt.setDate(8, java.sql.Date.valueOf(deadlineDate)); // deadlineDate is expected to be "yyyy-MM-dd"
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(addOrderDialog, "Invalid deadline date format. Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    stmt.setString(9, "Pending");

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        Object[] orderData = {
                                tableModel.getRowCount() + 1,
                                customerName != null ? customerName : "",
                                product != null ? product : "",
                                quantityText != null ? quantityText : "0",
                                priceText != null ? String.format("%.2f", Double.parseDouble(priceText)) : "0.00",
                                advancedPaymentCheckBox.isSelected() ? String.format("%.2f", advancedPayment) : "0.00", // Advanced Payment
                                String.format("%.2f", totalPrice), // Total Price
                                receivedDate != null ? receivedDate : "N/A",
                                deadlineDate != null ? deadlineDate : "N/A",
                                "Pending"
                        };
                        tableModel.addRow(orderData);
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

        // Add and Cancel Buttons
        JButton addButton = createButton("Add", 100, 500, 120, 40, new Color(0, 120, 215,50));
        mainPanel.add(addButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds( 280, 500, 120, 40);
        cancelButton.setBackground(new Color(150, 0, 0, 180));
        mainPanel.add(cancelButton);


        // Company Checkbox Listener
        companyCheckBox.addItemListener(e -> {
            boolean selected = companyCheckBox.isSelected();
            companyNameLabel.setVisible(selected);
            companyNameField.setVisible(selected);
            companyPhoneLabel.setVisible(selected);
            companyPhoneField.setVisible(selected);
        });

        // Add Button Action
        final boolean[] customerAdded = {false};
        
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

    
    private static boolean deleteOrderFromDatabase(String customerName, String product) {
        try (Connection connection = Database.getConnection()) {
            String query = "DELETE FROM orders WHERE customer_name = ? AND product = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, customerName);
            stmt.setString(2, product);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
   


    private void loadOrdersFromDatabase(DefaultTableModel tableModel, JTable orderTable) {
        tableModel.setRowCount(0);

        String sql = "SELECT id, customer_name, product, quantity, price, advance_payment, total_price, received_date, deadline_date, progress FROM orders";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("id");
                String customerName = rs.getString("customer_name");
                String product = rs.getString("product");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                double advance = rs.getDouble("advance_payment");
                double totalPrice = rs.getDouble("total_price");
                String receivedDate = rs.getString("received_date");
                String deadlineDate = rs.getString("deadline_date");
                String progress = rs.getString("progress");

               
                tableModel.addRow(new Object[]{
                        tableModel.getRowCount() + 1,
                        customerName,
                        product,
                        quantity,
                        price,
                        advance,
                        totalPrice,
                        receivedDate,
                        deadlineDate,
                        progress,
                        orderId
                });
            }

            orderTable.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (value == null) {
                        c.setBackground(Color.yellow);
                        return c;
                    }

                    String progressValue = value.toString();
                    if ("Pending".equalsIgnoreCase(progressValue)) {
                        c.setBackground(Color.YELLOW);
                        c.setForeground(Color.BLACK);
                    } else if ("Completed".equalsIgnoreCase(progressValue)) {
                        c.setBackground(Color.GREEN);
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(Color.gray);
                        c.setForeground(Color.black);
                    }
                    return c;
                }
            });

            
            orderTable.removeColumn(orderTable.getColumnModel().getColumn(10));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders from database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void showInvoiceFrame(String customerName, String product, int quantity, double price, double totalPrice, String receivedDate, String deadline) {
        JFrame frame = new JFrame("Invoice Receipt");
        frame.setSize(500, 650);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setResizable(false);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int xPosition = (screenSize.width - frame.getWidth()) / 2 + 80;
        int yPosition = 20;
        frame.setLocation(xPosition, yPosition);

        JPanel invoicePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);

                // Company Name and Address
                g.setFont(new Font("Serif", Font.BOLD, 24));
                g.drawString("BGR: Print and Advert", 120, 50);

                g.setFont(new Font("Serif", Font.BOLD, 16));

                int leftX = 40;
                int billToY = 170;
                g.drawString("BILL TO:", leftX, billToY);
                g.drawString("Customer Name: " + customerName, leftX, billToY + 30);
                g.drawString("Address: 4 killo, Mekane Yesus Building, 2nd Floor", leftX, billToY + 50);
                g.drawString("Addis Ababa, Ethiopia, 1000", leftX, billToY + 70);

                int rightX = leftX + 180 + 30;
               // g.drawString("INVOICE # 00000001", rightX, 100);
                g.drawString("DATE: " + receivedDate, rightX, 130);
                g.drawString("INVOICE DUE DATE: " + deadline, rightX, 160);

                int startY = 270;
                g.setFont(new Font("Serif", Font.BOLD, 14));
                g.setColor(Color.BLACK);
                g.fillRect(40, startY, 420, 30);
                g.setColor(Color.WHITE);
                g.drawString("ITEM", 50, startY + 20);
                g.drawString("PRODUCT", 120, startY + 20);
                g.drawString("QUANTITY", 220, startY + 20);
                g.drawString("PRICE", 320, startY + 20);
                g.drawString("TOTAL", 395, startY + 20);

                g.setFont(new Font("Serif", Font.PLAIN, 14));
                g.setColor(Color.BLACK);
                int rowHeight = 30;
                int currentY = startY + rowHeight;

                g.drawRect(40, currentY, 420, rowHeight);
                g.drawString("1", 50, currentY + 20); // Item number (static for now)
                g.drawString(product, 140, currentY + 20);
                g.drawString(String.valueOf(quantity), 240, currentY + 20);
                g.drawString("$" + price, 320, currentY + 20);
                g.drawString("$" + totalPrice, 400, currentY + 20);

                currentY += rowHeight + 20;
                g.setFont(new Font("Serif", Font.BOLD, 16));
                g.drawString("TOTAL: $" + totalPrice, 300, currentY);

                // Generate QR Code
                String qrCodeData = "Customer Name: " + customerName + "\n" +
                        "Product: " + product + "\n" +
                        "Quantity: " + quantity + "\n" +
                        "Price: $" + price + "\n" +
                        "Total Price: $" + totalPrice + "\n" +
                        "Received Date: " + receivedDate + "\n" +
                        "Deadline: " + deadline;

                BufferedImage qrCodeImage = null;
                try {
                    qrCodeImage = generateQRCodeImage(qrCodeData);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                if (qrCodeImage != null) {
                    int qrX = (getWidth() - qrCodeImage.getWidth()) / 2;
                    int qrY = currentY + 20;

                    g.drawImage(qrCodeImage, qrX, qrY, null);
                }
            }
        };

        frame.add(invoicePanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save as PNG");
        saveButton.addActionListener(e -> {
            try {
                BufferedImage invoiceImage = new BufferedImage(invoicePanel.getWidth(), invoicePanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = invoiceImage.createGraphics();
                invoicePanel.paint(g2d);
                g2d.dispose();

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Invoice As PNG");
                fileChooser.setAcceptAllFileFilterUsed(false);

                FileFilter pngFilter = new FileFilter() {
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
                    }

                    public String getDescription() {
                        return "PNG Files (*.png)";
                    }
                };
                fileChooser.addChoosableFileFilter(pngFilter);

                int result = fileChooser.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String fileName = selectedFile.getAbsolutePath();

                    if (!fileName.toLowerCase().endsWith(".png")) {
                        fileName += ".png";
                    }

                    ImageIO.write(invoiceImage, "PNG", new File(fileName));
                    JOptionPane.showMessageDialog(frame, "Invoice saved successfully to:\n" + fileName, "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving invoice: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(saveButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    public static BufferedImage generateQRCodeImage(String data) throws WriterException {
        int size = 200;
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size, hints);

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }
        return image;
    }

    public static void saveInvoiceAsImage(JPanel invoicePanel, String fileName) {
        try {
            
            BufferedImage image = new BufferedImage(
                invoicePanel.getWidth(),
                invoicePanel.getHeight(),
                BufferedImage.TYPE_INT_ARGB
            );

            
            Graphics2D g2d = image.createGraphics();
            invoicePanel.paint(g2d);
            g2d.dispose();

        
            File outputFile = new File(fileName);
            ImageIO.write(image, "png", outputFile);

            System.out.println("Invoice saved successfully as: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Failed to save invoice as image.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}