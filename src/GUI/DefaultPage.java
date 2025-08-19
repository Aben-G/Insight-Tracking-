package GUI;
import javax.swing.border.Border;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.*;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.ui.VerticalAlignment;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;

public class DefaultPage extends JPanel {


	    public DefaultPage(JFrame frame, JPanel mainPanel) {
	        setLayout(null);
	        setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());
	       // setBackground(new Color(30, 30, 30));
	        FlatMacDarkLaf.setup();
	      
	        DefaultPieDataset pieDataset = new DefaultPieDataset();
	        JFreeChart pieChart = createPieChart(pieDataset);
	        ChartPanel pieChartPanel = new ChartPanel(pieChart);
	        pieChartPanel.setOpaque(false);
	        pieChartPanel.setBackground(new Color(0, 0, 0, 0));
	        pieChartPanel.setBounds(20, 20, 300, 250); 
	        add(pieChartPanel);

	        JFreeChart barChart = createBarChart();
	        ChartPanel barChartPanel = new ChartPanel(barChart);
	        barChartPanel.setOpaque(false);
	        barChartPanel.setBackground(new Color(0, 0, 0, 0));
	        barChartPanel.setBounds(335, 20, 300, 250); 
	        add(barChartPanel);

	        JPanel quickActionPanel = new JPanel();
	        quickActionPanel.setLayout(null);
	        quickActionPanel.setBounds(mainPanel.getWidth() - 320, 20, 300, 200); 
	        quickActionPanel.setBorder(BorderFactory.createTitledBorder(
	            BorderFactory.createLineBorder(new Color(30, 30, 30, 100), 2), 
	            "Quick Actions", 
	            TitledBorder.CENTER, 
	            TitledBorder.TOP, 
	            new Font("Arial", Font.BOLD, 16) 
	            
	        ));
	        TitledBorder border3 = (TitledBorder) quickActionPanel.getBorder();
	        border3.setTitleColor(Color.GRAY);
	        add(quickActionPanel);

	        JButton button1 = new JButton("Add Customers");
	        button1.setBounds(25, 70, 220, 40); // Adjusted size
	        button1.setFont(new Font("Arial", Font.BOLD, 14));
	        button1.setForeground(Color.WHITE);
	        button1.setBackground(new Color(70, 130, 180, 50));
	        button1.setFocusPainted(false);
	        button1.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 50, 10), 2));
	        quickActionPanel.add(button1);
	        button1.addActionListener(e -> showAddCustomerDialog());

	        JButton button2 = new JButton("Add Orders");
	        button2.setBounds(25, 120, 220, 40);
	        button2.setFont(new Font("Arial", Font.BOLD, 14));
	        button2.setForeground(Color.WHITE);
	        button2.setBackground(new Color(70, 130, 180, 50));
	        button2.setFocusPainted(false);
	        button2.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 50, 10), 2));
	        quickActionPanel.add(button2);
	        button2.addActionListener(e -> showAddOrderForm());

	       
	     
	        JPanel orderStatusPanel1 = new JPanel();
	        orderStatusPanel1.setBounds(678, 220, 220, 60); // Adjusted position below quick action panel
	        orderStatusPanel1.setLayout(new GridLayout(2, 1, 0, 5)); // Two rows with 5px vertical gap
	        orderStatusPanel1.setOpaque(false); // Make the panel transparent
	        orderStatusPanel1.setBackground(Color.YELLOW); // Temporary background for debugging
	       add(orderStatusPanel1);

	        // Retrieve counts from the database
	        int completedOrders = getCompletedOrderCountFromDatabase();
	        int pendingOrders = getPendingOrderCountFromDatabase();

	        // Add the completed orders box
	        JPanel completedBox = createOrderStatusBox("Completed Orders: " + completedOrders, new Color(0, 150, 0,100)); // Green background
	        orderStatusPanel1.add(completedBox);

	        // Add the pending orders box
	        JPanel pendingBox = createOrderStatusBox("Pending Orders: " + pendingOrders, new Color(200, 0, 0,100)); // Red background
	        orderStatusPanel1.add(pendingBox);

	       

	        JPanel orderCustomerPanel = new JPanel();
	        orderCustomerPanel.setLayout(null);
	        orderCustomerPanel.setBounds(663, 320, 250, 120); // Increased height for both counts
	        orderCustomerPanel.setBorder(BorderFactory.createTitledBorder(
	            BorderFactory.createLineBorder(new Color(200, 150, 0,0), 2), 
	            "Data", 
	           
	            TitledBorder.CENTER, 
	            TitledBorder.TOP, 
	            new Font("Arial", Font.BOLD, 16)
	        ));
	        
	        TitledBorder border = (TitledBorder) orderCustomerPanel.getBorder();
	        border.setTitleColor(Color.GRAY);
	        
	        orderCustomerPanel.setOpaque(false); // Keep transparency
	        add(orderCustomerPanel);

	        // Inner Panel for Order Count
	        JPanel orderPanel = new JPanel();
	        orderPanel.setLayout(null);
	        orderPanel.setBounds(10, 30, 230, 40); 
	        orderPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2)); 
	        orderCustomerPanel.add(orderPanel);

	        JLabel orderCountLabel = new JLabel("Total Orders: 0", SwingConstants.CENTER);
	        orderCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
	        orderCountLabel.setBounds(0, 0, 230, 30); 
	        orderPanel.add(orderCountLabel);

	        // Inner Panel for Customer Count
	        JPanel customerPanel = new JPanel();
	        customerPanel.setLayout(null);
	        customerPanel.setBounds(10, 70, 230, 40); 
	        customerPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2)); 
	        orderCustomerPanel.add(customerPanel);

	        JLabel customerCountLabel = new JLabel(" Total Customers: 0", SwingConstants.CENTER);
	        customerCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
	        customerCountLabel.setBounds(0, 0, 230, 30); 
	        customerPanel.add(customerCountLabel);

	        // Update the counts from the database
	        updateCounts(orderCountLabel, customerCountLabel);


	        
	        JPanel mostOrderedPanel = new JPanel();
	        mostOrderedPanel.setLayout(null);
	        mostOrderedPanel.setBounds(20, 320, 300, 120); 
	        mostOrderedPanel.setBorder(BorderFactory.createTitledBorder(
	            BorderFactory.createLineBorder(new Color(200, 150, 50, 0), 2), 
	            "Most Ordered Product", 
	            TitledBorder.CENTER, 
	            TitledBorder.TOP, 
	            new Font("Arial", Font.BOLD, 16)
	        ));
	        add(mostOrderedPanel);
	        TitledBorder border1 = (TitledBorder) mostOrderedPanel.getBorder();
	        border1.setTitleColor(Color.GRAY);

	        // Product Panel (Left Side - Bigger Box)
	        JPanel productPanel = new JPanel();
	        productPanel.setLayout(null);
	        productPanel.setBounds(10, 30, 150, 80); 
	        productPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 3)); // Bolder border
	        mostOrderedPanel.add(productPanel);

	        JLabel mostOrderedProductLabel = new JLabel("Product: N/A", SwingConstants.CENTER);
	        mostOrderedProductLabel.setFont(new Font("Arial", Font.BOLD, 14));
	        mostOrderedProductLabel.setBounds(0, 0, 150, 80); // Centered in the bigger box
	        productPanel.add(mostOrderedProductLabel);

	        // Quantity and Frequency Panels (Right Side - Stacked Vertically)
	        JPanel rightPanel = new JPanel();
	        rightPanel.setLayout(null);
	        rightPanel.setBounds(170, 30, 120, 80); // Right side container
	        mostOrderedPanel.add(rightPanel);

	        // Quantity Panel (Top Right)
	        JPanel quantityPanel = new JPanel();
	        quantityPanel.setLayout(null);
	        quantityPanel.setBounds(0, 0, 120, 38); // Adjusted height for 2-pixel gap
	        quantityPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
	        rightPanel.add(quantityPanel);

	        JLabel mostOrderedQuantityLabel = new JLabel("Quantity: N/A", SwingConstants.CENTER);
	        mostOrderedQuantityLabel.setFont(new Font("Arial", Font.BOLD, 14));
	        mostOrderedQuantityLabel.setBounds(0, 0, 120, 38); // Centered in the panel
	        quantityPanel.add(mostOrderedQuantityLabel);

	        // Frequency Panel (Bottom Right)
	        JPanel frequencyPanel = new JPanel();
	        frequencyPanel.setLayout(null);
	        frequencyPanel.setBounds(0, 40, 120, 38); // Adjusted height for 2-pixel gap
	        frequencyPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
	        rightPanel.add(frequencyPanel);

	        JLabel mostOrderedFrequencyLabel = new JLabel("Frequency: N/A", SwingConstants.CENTER);
	        mostOrderedFrequencyLabel.setFont(new Font("Arial", Font.BOLD, 14));
	        mostOrderedFrequencyLabel.setBounds(0, 0, 120, 38); // Centered in the panel
	        frequencyPanel.add(mostOrderedFrequencyLabel);

	       
	        

	      

	       
	        JPanel orderStatusPanel = new JPanel();
	        orderStatusPanel.setLayout(null);
	        orderStatusPanel.setBounds(340, 320, 300, 120);
	        orderStatusPanel.setBorder(BorderFactory.createTitledBorder(
	            BorderFactory.createLineBorder(new Color(200, 150, 50, 0), 2), 
	            "Order Status", 
	            TitledBorder.CENTER, 
	            TitledBorder.TOP, 
	            new Font("Arial", Font.BOLD, 16)
	           
	        ));
	        add(orderStatusPanel);

	        TitledBorder border2 = (TitledBorder) orderStatusPanel.getBorder();
	        border2.setTitleColor(Color.GRAY);
	     
	        JLabel completedLabel = new JLabel("Completed Orders", SwingConstants.LEFT);
	        completedLabel.setBounds(10, 20, 280, 15);
	        orderStatusPanel.add(completedLabel);

	       
	        JProgressBar completedProgress = new JProgressBar(0, 100);
	        completedProgress.setValue(70); 
	        completedProgress.setBounds(10, 40, 280, 20);
	        completedProgress.setForeground(new Color(34, 177, 76)); 
	        completedProgress.setBackground(new Color(80, 80, 80));
	        completedProgress.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
	        completedProgress.setStringPainted(true);
	        orderStatusPanel.add(completedProgress);

	     
	        JLabel pendingLabel = new JLabel("Pending Orders", SwingConstants.LEFT);
	        pendingLabel.setBounds(10, 70, 280, 15);
	        orderStatusPanel.add(pendingLabel);

	       
	        JProgressBar pendingProgress = new JProgressBar(0, 100);
	        pendingProgress.setValue(30); 
	        pendingProgress.setBounds(10, 90, 280, 20);
	        pendingProgress.setForeground(new Color(219, 38, 38, 130)); 
	        pendingProgress.setBackground(new Color(80, 80, 80));
	        pendingProgress.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
	        pendingProgress.setStringPainted(true); 
	        orderStatusPanel.add(pendingProgress);

	        loadOrdersFromDatabase(pieDataset, barChart);
	        updateMostOrderedProduct(mostOrderedProductLabel, mostOrderedQuantityLabel, mostOrderedFrequencyLabel);
	        updateOrderStatus(completedProgress, pendingProgress);

	        setVisible(true);
	    }
	    private JPanel createOrderStatusBox(String text, Color backgroundColor) {
	        JPanel box = new JPanel();
	        box.setLayout(new BorderLayout());
	        box.setBackground(backgroundColor);
	        box.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding

	        // Label for text
	        JLabel label = new JLabel(text, SwingConstants.CENTER);
	        label.setFont(new Font("Arial", Font.BOLD, 14));
	        label.setForeground(Color.WHITE);
	        box.add(label, BorderLayout.CENTER);

	        return box;
	    }
	    


	    private int getCompletedOrderCountFromDatabase() {
	        int count = 0;
	        try (Connection conn = Database.getConnection()) {
	            String sql = "SELECT COUNT(*) AS completed_count FROM Orders WHERE Progress = 'Completed'"; // Assuming 'Progress' column exists
	            try (PreparedStatement pstmt = conn.prepareStatement(sql);
	                 ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	                    count = rs.getInt("completed_count");
	                }
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        return count;
	    }

	    private int getPendingOrderCountFromDatabase() {
	        int count = 0;
	        try (Connection conn = Database.getConnection()) {
	            String sql = "SELECT COUNT(*) AS pending_count FROM Orders WHERE Progress = 'Pending'"; // Assuming 'Progress' column exists
	            try (PreparedStatement pstmt = conn.prepareStatement(sql);
	                 ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	                    count = rs.getInt("pending_count");
	                }
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        return count;
	    }

	    
	    private void updateCounts(JLabel orderCountLabel, JLabel customerCountLabel) {
	        int orderCount = getOrderCountFromDatabase();
	        int customerCount = getCustomerCountFromDatabase();
	        
	        orderCountLabel.setText("Total Orders: " + orderCount);
	        customerCountLabel.setText("Total Customers: " + customerCount);
	    }

	    // Fetch order count from the database
	    private int getOrderCountFromDatabase() {
	        int count = 0;
	        try (Connection conn = Database.getConnection()) {
	            String sql = "SELECT COUNT(*) AS order_count FROM Orders"; // Table name: Orders
	            try (PreparedStatement pstmt = conn.prepareStatement(sql);
	                 ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	                    count = rs.getInt("order_count");
	                }
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        return count;
	    }

	    // Fetch customer count from the database
	    private int getCustomerCountFromDatabase() {
	        int count = 0;
	        try (Connection conn = Database.getConnection()) {
	            String sql = "SELECT COUNT(Name) AS customer_count FROM Customers"; // Counting names in Customer table
	            try (PreparedStatement pstmt = conn.prepareStatement(sql);
	                 ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	                    count = rs.getInt("customer_count");
	                }
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        return count;
	    }

	    
	private void updateMostOrderedProduct(JLabel productLabel, JLabel quantityLabel, JLabel frequencyLabel) {
	    String sql = "SELECT product, SUM(quantity) AS total_quantity, COUNT(*) AS frequency " +
	                 "FROM orders " +
	                 "GROUP BY product " +
	                 "ORDER BY total_quantity DESC " +
	                 "LIMIT 1";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        if (rs.next()) {
	            String product = rs.getString("product");
	            int totalQuantity = rs.getInt("total_quantity");
	            int frequency = rs.getInt("frequency");

	            productLabel.setText("Product: " + product);
	            quantityLabel.setText("Quantity: " + totalQuantity);
	            frequencyLabel.setText("Frequency: " + frequency);
	        } else {
	            productLabel.setText("Product: N/A");
	            quantityLabel.setText("Quantity: N/A");
	            frequencyLabel.setText("Frequency: N/A");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Error fetching most ordered product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}

	private void updateOrderStatus(JProgressBar completedProgress, JProgressBar pendingProgress) {
	    String sql = "SELECT progress, COUNT(*) AS count FROM orders GROUP BY progress";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        int completed = 0;
	        int pending = 0;
	        int totalOrders = 0;

	        while (rs.next()) {
	            String progress = rs.getString("progress");
	            int count = rs.getInt("count");
	            totalOrders += count;

	            if ("Completed".equalsIgnoreCase(progress)) {
	                completed = count;
	            } else if ("Pending".equalsIgnoreCase(progress)) {
	                pending = count;
	            }
	        }

	        if (totalOrders > 0) {
	            int completedPercentage = (completed * 100) / totalOrders;
	            int pendingPercentage = (pending * 100) / totalOrders;

	            completedProgress.setValue(completedPercentage);
	            completedProgress.setString(completedPercentage + "%");
	            completedProgress.setStringPainted(true);

	            pendingProgress.setValue(pendingPercentage);
	            pendingProgress.setString(pendingPercentage + "%");
	            pendingProgress.setStringPainted(true);
	        } else {
	            completedProgress.setValue(0);
	            completedProgress.setString("0%");
	            pendingProgress.setValue(0);
	            pendingProgress.setString("0%");
	        }

	        
	        UIManager.put("ProgressBar.selectionForeground", Color.WHITE);
	        UIManager.put("ProgressBar.selectionBackground", Color.WHITE);

	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error fetching order status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
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
        //companyCheckBox.setForeground(Color.WHITE);
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
        companyPhoneField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        companyPhoneField.setForeground(Color.black);
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

	private JFreeChart createPieChart(DefaultPieDataset pieDataset) {
	    // Create a 3D pie chart
	    JFreeChart pieChart = ChartFactory.createPieChart3D(
	            "", // Chart title (empty string)
	            pieDataset, // Dataset
	            false, // Include legend
	            true, // Include tooltips
	            false // Include URLs
	    );

	    // Get the plot and customize it
	    PiePlot3D plot = (PiePlot3D) pieChart.getPlot();
	    plot.setBackgroundPaint(null); // Transparent background
	    plot.setOutlineVisible(false); // No outline
	    plot.setSectionOutlinesVisible(false); // No section outlines
	    plot.setLabelFont(new Font("Arial", Font.BOLD, 12)); // Label font
	    plot.setLabelPaint(Color.gray); // Label color
	    plot.setInteriorGap(0.10); // Increase the gap between sections for better spacing
	    plot.setShadowPaint(null); // No shadow
	    plot.setLabelBackgroundPaint(new Color(0, 0, 0, 0)); // Transparent label background
	    plot.setLabelOutlinePaint(null); // No label outline
	    plot.setLabelShadowPaint(null); // No label shadow

	    // Enhance 3D effect
	    plot.setDepthFactor(0.15); // Increase the depth for a more pronounced 3D effect
	    plot.setDarkerSides(true); // Add shading to the sides for a more realistic 3D look
	    plot.setForegroundAlpha(0.85f); // Slightly transparent sections for a modern look

	    // Customize the chart background
	    pieChart.setBackgroundPaint(new Color(0, 0, 0, 0)); // Transparent chart background

	    // Add a custom title
	    TextTitle chartTitle = new TextTitle(
	            "Order Distribution by Product", // Title text
	            new Font("Arial", Font.BOLD, 16), // Title font (slightly larger)
	            new Color(100, 100, 100), // Title color (darker gray)
	            RectangleEdge.TOP, // Title position
	            HorizontalAlignment.CENTER, // Title alignment
	            VerticalAlignment.TOP, // Title vertical alignment
	            RectangleInsets.ZERO_INSETS // Title margins
	    );
	    pieChart.setTitle(chartTitle);

	    // Customize section colors with gradients
	    GradientPaint gradient1 = new GradientPaint(0, 0, new Color(255, 165, 0), 0, 0, new Color(255, 69, 0)); // Orange gradient
	    GradientPaint gradient2 = new GradientPaint(0, 0, new Color(0, 0, 139), 0, 0, new Color(0, 191, 255)); // Blue gradient
	    GradientPaint gradient3 = new GradientPaint(0, 0, new Color(50, 205, 50), 0, 0, new Color(34, 139, 34)); // Green gradient
	    GradientPaint gradient4 = new GradientPaint(0, 0, new Color(255, 99, 71), 0, 0, new Color(255, 69, 0)); // Red gradient
	    GradientPaint gradient5 = new GradientPaint(0, 0, new Color(138, 43, 226), 0, 0, new Color(75, 0, 130)); // Purple gradient

	    plot.setSectionPaint("Product A", gradient1); // Apply gradient to Product A
	    plot.setSectionPaint("Product B", gradient2); // Apply gradient to Product B
	    plot.setSectionPaint("Product C", gradient3); // Apply gradient to Product C
	    plot.setSectionPaint("Product D", gradient4); // Apply gradient to Product D
	    plot.setSectionPaint("Product E", gradient5); // Apply gradient to Product E

	    // Add a subtle shadow effect to the chart
	    plot.setShadowXOffset(5); // Horizontal shadow offset
	    plot.setShadowYOffset(5); // Vertical shadow offset
	    plot.setShadowPaint(new Color(0, 0, 0, 50)); // Semi-transparent black shadow

	    return pieChart;
	}
	private JFreeChart createBarChart() {
	    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	    // Fetch customer order quantities
	    Map<String, Integer> customerOrderQuantities = loadCustomersFromDatabase();

	    // Add data to the dataset
	    for (Map.Entry<String, Integer> entry : customerOrderQuantities.entrySet()) {
	        dataset.addValue(entry.getValue(), "Orders", entry.getKey());
	    }

	    // Create a bar chart
	    JFreeChart barChart = ChartFactory.createBarChart(
	            "", // Chart title (empty string)
	            "Customers", // X-axis label
	            "Quantity", // Y-axis label
	            dataset, // Dataset
	            PlotOrientation.VERTICAL, // Orientation
	            false, // Include legend
	            true, // Include tooltips
	            false // Include URLs
	    );

	    // Customize the plot
	    CategoryPlot plot = barChart.getCategoryPlot();
	    plot.setBackgroundPaint(null); // Transparent background
	    plot.setOutlineVisible(false); // No outline
	    plot.setRangeGridlinePaint(Color.gray); // Gridline color
	    plot.getDomainAxis().setTickLabelPaint(Color.gray); // X-axis tick label color
	    plot.getDomainAxis().setLabelPaint(Color.gray); // X-axis label color
	    plot.getRangeAxis().setTickLabelPaint(Color.gray); // Y-axis tick label color
	    plot.getRangeAxis().setLabelPaint(Color.gray); // Y-axis label color

	    // Customize the renderer
	    BarRenderer renderer = (BarRenderer) plot.getRenderer();
	    renderer.setBarPainter(new StandardBarPainter());

	    // Gradient paint for bars (Cool gradient: Purple to Teal)
	    renderer.setSeriesPaint(0, new GradientPaint(
	            0, 0, Color.gray, // Purple
	            0, 0, Color.darkGray // Teal
	    ));

	    // Add a shadow effect to the bars
	    renderer.setShadowVisible(true);
	    renderer.setShadowPaint(new Color(0, 0, 0, 50)); // Semi-transparent shadow
	    renderer.setShadowXOffset(3); // Horizontal shadow offset
	    renderer.setShadowYOffset(3); // Vertical shadow offset

	    renderer.setDrawBarOutline(false); // No bar outlines
	    renderer.setDefaultItemLabelFont(new Font("Arial", Font.BOLD, 12)); // Item label font
	    renderer.setDefaultItemLabelsVisible(true); // Show item labels
	    renderer.setDefaultItemLabelPaint(Color.gray); // Item label color
	    renderer.setMaximumBarWidth(0.1); // Set maximum bar width

	    // Customize the chart background
	    barChart.setBackgroundPaint(new Color(0, 0, 0, 0)); // Transparent chart background

	    // Add a custom title
	    TextTitle chartTitle = new TextTitle(
	            "Top 5 Customers by Order Quantity", // Title text
	            new Font("Arial", Font.BOLD, 15), // Title font
	            new Color(100, 100, 100), // Title color (darker gray)
	            RectangleEdge.TOP, // Title position
	            HorizontalAlignment.CENTER, // Title alignment
	            VerticalAlignment.TOP, // Title vertical alignment
	            RectangleInsets.ZERO_INSETS // Title margins
	    );
	    barChart.addSubtitle(chartTitle);

	    return barChart;
	}
    private void loadOrdersFromDatabase(DefaultPieDataset pieDataset, JFreeChart barChart) {
        pieDataset.clear();

        String sql = "SELECT product, SUM(quantity) AS total_quantity FROM orders GROUP BY product";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String product = rs.getString("product");
                int totalQuantity = rs.getInt("total_quantity");
                pieDataset.setValue(product, totalQuantity);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders from database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map<String, Integer> loadCustomersFromDatabase() {
        Map<String, Integer> customerOrderQuantities = new HashMap<>();

        String sql = "SELECT c.Name, SUM(o.quantity) AS total_quantity " +
                     "FROM Customers c " +
                     "JOIN orders o ON c.Name = o.customer_name " +
                     "GROUP BY c.Name " +
                     "ORDER BY total_quantity DESC " +
                     "LIMIT 5";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String customerName = rs.getString("Name");
                int totalQuantity = rs.getInt("total_quantity");
                customerOrderQuantities.put(customerName, totalQuantity);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading customer order quantities: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return customerOrderQuantities;
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

        JTextField advancedPaymentField = new JTextField();
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


}