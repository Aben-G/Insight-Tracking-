package Pages;





import javax.swing.*;

import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;




import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.util.ArrayList;
import java.util.Date;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;



public class MainPage extends JPanel {

	private static ArrayList<String> groupList = new ArrayList<>();
    private JPanel groupPanel;
    private JTextField totalGroupsLabel;
    private JTextField totalMembersLabel;
   private JTextField totalLeadersLabel;
    private ArrayList<String> tasks = new ArrayList<>();
    private JTable taskTable;
    private static DefaultTableModel tableModel;
    boolean[] isMaximized = {false};
    private JTable groupTable;
    private static JTable table;
    private static  JLabel titleLabel;
    private JComboBox<String> sortComboBox;
    private JButton deleteButton ;
    private JButton Notes ;
    private JButton createGroupButton;
    private JButton leadersButton;
    private JButton changeThemeButton; 
    private JTextField inProgressCountLabel;
    private JTextField completedCountLabel;
    private JPanel infoPanel;
    
    

    public MainPage() {
   	  infoPanel = new JPanel();
   	 
   	 setLayout(new BorderLayout());
   	
   	 	JButton Report = new JButton("Reports");
        Report.setFont(new Font("Arial", Font.BOLD, 14));
      
        Report.setFocusPainted(false);
        Report.addActionListener(e -> {
        	Reports();
        });
        infoPanel.add(Report);

         Notes = new JButton("Notes");
        Notes.setFont(new Font("Arial", Font.BOLD, 14));
        Report.setBackground(new Color(38, 38, 38));
        Report.setForeground(Color.white);
        Notes.setBackground(new Color(38, 38, 38));
        Notes.setForeground(Color.white);
        
        Notes.addActionListener(e -> Notes());
        Notes.setFocusPainted(false);
        infoPanel.add(Notes);
        
      
    
       JPanel topPanel = new JPanel(new BorderLayout());
       JPanel titleSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
       JLabel titleLabel = new JLabel("Bible Study Management");
       titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
       titleLabel.setForeground(Color.BLACK);
       titleSearchPanel.add(titleLabel);
       titleSearchPanel.setBackground(new Color(186, 168,118));

       JTextField searchField = new JTextField(20);
       searchField.setFont(new Font("Arial", Font.PLAIN, 16));
       searchField.setPreferredSize(new Dimension(280, 35));
       setupPlaceholder2(searchField, "Search member....");
       searchField.putClientProperty(FlatClientProperties.STYLE, "roundRect:true;showClearButton:true;");
       
     
       searchField.putClientProperty(FlatClientProperties.STYLE, ""
               + "roundRect:true;"           
         		
               + "showClearButton:true;"); 
         this.setFocusable(true);
        
         titleSearchPanel.add(searchField);
         searchField.addActionListener(e -> {
        	    String searchText = searchField.getText().trim();
        	    if (searchText.isEmpty()) {
        	        JOptionPane.showMessageDialog(null, "Please enter a name to search.");
        	        return;
        	    }

        	    loadInfoFromDatabase(searchText);
        	});
         searchField.addFocusListener(new FocusAdapter() {
             @Override
             public void focusGained(FocusEvent e) {
                 if (searchField.getText().equals("Search member....")) {
                     searchField.setText("");
                 }
             }

             @Override
             public void focusLost(FocusEvent e) {
                 if (searchField.getText().isEmpty()) {
                     setupPlaceholder2(searchField, "Search member....");
                 }
             }
         });

       
         searchField.addKeyListener(new KeyAdapter() {
             @Override
             public void keyPressed(KeyEvent e) {
                 if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                     searchField.transferFocus(); 
                     if (searchField.getText().isEmpty()) {
                         setupPlaceholder2(searchField, "Search member....");
                     }
                 }
             }
         });

         topPanel.addMouseListener(new MouseAdapter() {
             @Override
             public void mousePressed(MouseEvent e) {
                 if (!searchField.contains(e.getPoint())) {
                     searchField.transferFocus();
                     if (searchField.getText().isEmpty()) {
                         setupPlaceholder2(searchField, "Search member....");
                     }
                 }
             }
         });
       this.setFocusable(true);
       titleSearchPanel.add(searchField);
       topPanel.add(titleSearchPanel, BorderLayout.CENTER);

       JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 20));
       changeThemeButton = new JButton("Light");
       changeThemeButton.setBackground(new Color(38, 38, 38));
       changeThemeButton.setForeground(Color.white);
       changeThemeButton.setFont(new Font("Arial", Font.PLAIN, 12));
       changeThemeButton.setFocusPainted(false);
       themePanel.add(changeThemeButton); 
       themePanel.setBackground(new Color(186, 168,118));
       

changeThemeButton.addActionListener(e -> {
           if (UIManager.getLookAndFeel() instanceof FlatLightLaf) {
               FlatMacDarkLaf.setup();
               changeThemeButton.setText("Light");
               Report.setBackground(new Color(38, 38, 38));
               Report.setForeground(Color.white);
               Notes.setBackground(new Color(38, 38, 38));
               Notes.setForeground(Color.white);
               
               createGroupButton.setBackground(new Color(38, 38, 38));
               createGroupButton.setForeground(Color.white);
               changeThemeButton.setBackground(new Color(38, 38, 38));
               changeThemeButton.setForeground(Color.white);
               
               leadersButton.setBackground(new Color(38, 38, 38));
               leadersButton.setForeground(Color.white);
           } else {
               FlatMacLightLaf.setup();
               Report.setBackground(Color.white);
               Report.setForeground(Color.black);
               Notes.setBackground(Color.white);
               Notes.setForeground(Color.black);
               
               createGroupButton.setBackground(Color.white);
               createGroupButton.setForeground(Color.black);
               
               changeThemeButton.setBackground(Color.white);
               changeThemeButton.setForeground(Color.black);
               leadersButton.setBackground(Color.white);
               leadersButton.setForeground(Color.black);
               changeThemeButton.setText("Dark");
           
               
               
               
           }
           SwingUtilities.updateComponentTreeUI(JFrame.getFrames()[0]);
           JFrame.getFrames()[0].revalidate();
           JFrame.getFrames()[0].repaint();
       });
       topPanel.setBackground(new Color(186, 168,118));
       topPanel.add(themePanel, BorderLayout.EAST);
       add(topPanel, BorderLayout.NORTH);

     
      
       infoPanel.setLayout(new GridLayout(6, 1, 10, 10));
       TitledBorder titledBorder = BorderFactory.createTitledBorder("Options");
       titledBorder.setTitleColor(Color.BLACK);
       infoPanel.setBorder(titledBorder);
       infoPanel.setPreferredSize(new Dimension(250, 0));
       infoPanel.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               if (!searchField.contains(e.getPoint())) {
                   searchField.transferFocus();
                   if (searchField.getText().isEmpty()) {
                       setupPlaceholder2(searchField, "Search....");
                   }
               }
           }
       });
      
       infoPanel.setBackground(new Color(186, 168,118));
      
       totalGroupsLabel = new JTextField("Total Groups: 0");
       totalGroupsLabel.setFont(new Font("Arial", Font.BOLD, 16));
       totalGroupsLabel.setHorizontalAlignment(SwingConstants.CENTER);
       totalGroupsLabel.setEditable(false); 
       totalGroupsLabel.setBackground(new Color(193, 183, 155));  
       totalGroupsLabel.setFocusable(false);  
       totalGroupsLabel.setRequestFocusEnabled(false);
       totalGroupsLabel.putClientProperty(FlatClientProperties.STYLE, ""
               + "roundRect:true;"); 
       infoPanel.add(totalGroupsLabel);

       totalLeadersLabel = new JTextField("Total Leaders: 0");
       totalLeadersLabel.setFont(new Font("Arial", Font.BOLD, 16));
       totalLeadersLabel.setHorizontalAlignment(SwingConstants.CENTER);
       totalLeadersLabel.setEditable(false);  
       totalLeadersLabel.setBackground(new Color(193, 183, 155));  
       totalLeadersLabel.setFocusable(false);  
       totalLeadersLabel.setRequestFocusEnabled(false);
       totalLeadersLabel.putClientProperty(FlatClientProperties.STYLE, ""
               + "roundRect:true;"); 
       infoPanel.add(totalLeadersLabel);

       totalMembersLabel = new JTextField("Total Members: 0");
       totalMembersLabel.setFont(new Font("Arial", Font.BOLD, 16));
       totalMembersLabel.setHorizontalAlignment(SwingConstants.CENTER);
       totalMembersLabel.setEditable(false);  
       totalMembersLabel.setBackground(new Color(193, 183, 155)); 
       totalMembersLabel.setFocusable(false); 
       totalMembersLabel.setRequestFocusEnabled(false);
       totalMembersLabel.putClientProperty(FlatClientProperties.STYLE, ""
               + "roundRect:true;"); 
       infoPanel.add(totalMembersLabel);



       add(infoPanel, BorderLayout.WEST);

     
       String[] columnNames = {"No.", "Leader", "Address"};
       tableModel = new DefaultTableModel(columnNames, 0);
       groupTable = new JTable(tableModel);
       groupTable.setFont(new Font("Arial", Font.PLAIN, 14));
       groupTable.setRowHeight(30);
     

       
       groupTable.setDefaultEditor(Object.class, null);  

       groupTable.addMouseListener(new MouseAdapter() {
           @Override
           public void mouseClicked(MouseEvent e) {
               if (e.getClickCount() == 2) { 
                   int row = groupTable.getSelectedRow();
                   if (row != -1) {
                      
                       String leaderName = groupTable.getValueAt(row, 1).toString(); 
                       Manage(leaderName); 
                   }
               }
           }
       });
       
       groupTable.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               if (!searchField.contains(e.getPoint())) {
                   searchField.transferFocus();
                   if (searchField.getText().isEmpty()) {
                       setupPlaceholder2(searchField, "Search....");
                   }
               }
           }
       });

       
       
       TableColumn noColumn = groupTable.getColumnModel().getColumn(0);
       noColumn.setPreferredWidth(35); 
       noColumn.setMaxWidth(40);       
       noColumn.setMinWidth(35);      
       JScrollPane scrollPane = new JScrollPane(groupTable);
       add(scrollPane, BorderLayout.CENTER);

       loadGroups();


       JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
       
       
       

      
       leadersButton = new JButton("Leaders");
       leadersButton.setFocusPainted(false);
       leadersButton.setBackground(new Color(38, 38, 38));
       leadersButton.setForeground(Color.white);
       leadersButton.addActionListener(e -> {
       	Leaders2();});
       footerPanel.add(leadersButton);

       createGroupButton = new JButton("Create Group");
       createGroupButton.setFocusPainted(false);
       createGroupButton.setBackground(new Color(38, 38, 38));
       createGroupButton.setForeground(Color.white);
       createGroupButton.addActionListener(e -> openCreateGroupDialog());
       footerPanel.add(createGroupButton);
    
       deleteButton = new JButton("Delete Group");
       deleteButton.setBackground(Color.red);
       deleteButton.setForeground(Color.white);
       deleteButton.setFocusPainted(false);
    
       deleteButton.addActionListener(e -> {
    	    int selectedRow = groupTable.getSelectedRow();
    	    if (selectedRow != -1) {
    	        String leaderName = groupTable.getValueAt(selectedRow, 1).toString();
    	        
    	        int confirm = JOptionPane.showConfirmDialog(null, 
    	            "Are you sure you want to delete the group led by " + leaderName + "?", 
    	            "Confirm Deletion", 
    	            JOptionPane.YES_NO_OPTION);
    	        
    	        if (confirm == JOptionPane.YES_OPTION) {
    	            
    	            boolean success = deleteGroupFromDatabase(leaderName);
    	            if (success) {
    	                tableModel.removeRow(selectedRow);
    	                
    	                rearrangeRows();
    	                
    	                updateDashboardInfo(); 
    	                JOptionPane.showMessageDialog(null, "Group deleted successfully.");
    	            }
    	        }
    	    } else {
    	        JOptionPane.showMessageDialog(this, 
    	            "Please select a task to delete.", 
    	            "No Task Selected", 
    	            JOptionPane.WARNING_MESSAGE);
    	    }
    	});

       footerPanel.add(deleteButton);
       footerPanel.setBackground(new Color(186, 168,118));
       footerPanel.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               if (!searchField.contains(e.getPoint())) {
                   searchField.transferFocus();
                   if (searchField.getText().isEmpty()) {
                       setupPlaceholder2(searchField, "Search....");
                   }
               }
           }
       });

       add(footerPanel, BorderLayout.SOUTH);
       
       updateDashboardInfo();
       
   }

    void loadGroups() {
        tableModel.setRowCount(0);  

        String query = "SELECT leader_name, leader_address FROM bible_study_groups";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Assemble_BS", "ABEN", "011248Agb_2H");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int groupNumber = 1; 

            while (rs.next()) {
                String leaderName = rs.getString("leader_name");
                String address = rs.getString("leader_address");

                Object[] rowData = {
                    groupNumber++,            
                    leaderName,                  
                    address,                    
                };

                tableModel.addRow(rowData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading groups from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        groupTable.revalidate();  
        groupTable.repaint();    
    }


    private void loadInfoFromDatabase(String searchText) {
        JDialog infoDialog = new JDialog();
        infoDialog.setTitle("Search Results");
        infoDialog.setSize(600, 500);
        infoDialog.setLayout(null);
        infoDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Center the dialog on the screen
        infoDialog.setLocationRelativeTo(null);

        // Create labels and text fields for displaying information
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        nameField.setEditable(false);

        JLabel roleLabel = new JLabel("Role:");
        JTextField roleField = new JTextField();
        roleField.setEditable(false);

        JLabel groupLabel = new JLabel("Group:");
        JTextField groupField = new JTextField();
        groupField.setEditable(false);

        JLabel leaderLabel = new JLabel("Leader:");
        JTextField leaderField = new JTextField();
        leaderField.setEditable(false);

        JLabel primaryPhoneLabel = new JLabel("Primary Phone:");
        JTextField primaryPhoneField = new JTextField();
        primaryPhoneField.setEditable(false);

        JLabel secondaryPhoneLabel = new JLabel("Secondary Phone:");
        JTextField secondaryPhoneField = new JTextField();
        secondaryPhoneField.setEditable(false);

        JLabel genderLabel = new JLabel("Gender:");
        JTextField genderField = new JTextField();
        genderField.setEditable(false);

        JLabel maritalStatusLabel = new JLabel("Marital Status:");
        JTextField maritalStatusField = new JTextField();
        maritalStatusField.setEditable(false);

        // Set bounds for components
        int labelWidth = 150, fieldWidth = 350, fieldHeight = 25, spacing = 40;
        nameLabel.setBounds(20, 20, labelWidth, fieldHeight);
        nameField.setBounds(180, 20, fieldWidth, fieldHeight);

        roleLabel.setBounds(20, 20 + spacing, labelWidth, fieldHeight);
        roleField.setBounds(180, 20 + spacing, fieldWidth, fieldHeight);

        groupLabel.setBounds(20, 20 + 2 * spacing, labelWidth, fieldHeight);
        groupField.setBounds(180, 20 + 2 * spacing, fieldWidth, fieldHeight);

        leaderLabel.setBounds(20, 20 + 3 * spacing, labelWidth, fieldHeight);
        leaderField.setBounds(180, 20 + 3 * spacing, fieldWidth, fieldHeight);

        primaryPhoneLabel.setBounds(20, 20 + 4 * spacing, labelWidth, fieldHeight);
        primaryPhoneField.setBounds(180, 20 + 4 * spacing, fieldWidth, fieldHeight);

        secondaryPhoneLabel.setBounds(20, 20 + 5 * spacing, labelWidth, fieldHeight);
        secondaryPhoneField.setBounds(180, 20 + 5 * spacing, fieldWidth, fieldHeight);

        genderLabel.setBounds(20, 20 + 6 * spacing, labelWidth, fieldHeight);
        genderField.setBounds(180, 20 + 6 * spacing, fieldWidth, fieldHeight);

        maritalStatusLabel.setBounds(20, 20 + 7 * spacing, labelWidth, fieldHeight);
        maritalStatusField.setBounds(180, 20 + 7 * spacing, fieldWidth, fieldHeight);

        // Add components to the dialog
        infoDialog.add(nameLabel);
        infoDialog.add(nameField);
        infoDialog.add(roleLabel);
        infoDialog.add(roleField);
        infoDialog.add(groupLabel);
        infoDialog.add(groupField);
        infoDialog.add(leaderLabel);
        infoDialog.add(leaderField);
        infoDialog.add(primaryPhoneLabel);
        infoDialog.add(primaryPhoneField);
        infoDialog.add(secondaryPhoneLabel);
        infoDialog.add(secondaryPhoneField);
        infoDialog.add(genderLabel);
        infoDialog.add(genderField);
        infoDialog.add(maritalStatusLabel);
        infoDialog.add(maritalStatusField);

      
        String modifiedSearchText = "%" + searchText.replace(" ", "%") + "%";

        // Load data from the database
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT m.full_name AS member_name, m.role AS member_role, " +
                         "g.group_name AS group_name, g.leader_name AS leader_name, " +
                         "m.primary_phone, m.secondary_phone, m.gender, m.marital_status " +
                         "FROM members m " +
                         "LEFT JOIN member_groups mg ON m.id = mg.member_id " +
                         "LEFT JOIN bible_study_groups g ON mg.group_id = g.group_id " +
                         "WHERE m.full_name LIKE ? " +
                         "OR g.leader_name LIKE ?";  // Modified to search for leader name as well

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, modifiedSearchText);
                stmt.setString(2, modifiedSearchText); // Added second condition for leader name
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        nameField.setText(rs.getString("member_name") != null ? rs.getString("member_name") : "N/A");
                        roleField.setText(rs.getString("member_role") != null ? rs.getString("member_role") : "N/A");
                        groupField.setText(rs.getString("group_name") != null ? rs.getString("group_name") : "N/A");
                        leaderField.setText(rs.getString("leader_name") != null ? rs.getString("leader_name") : "N/A");
                        primaryPhoneField.setText(rs.getString("primary_phone") != null ? rs.getString("primary_phone") : "N/A");
                        secondaryPhoneField.setText(rs.getString("secondary_phone") != null ? rs.getString("secondary_phone") : "N/A");
                        genderField.setText(rs.getString("gender") != null ? rs.getString("gender") : "N/A");
                        maritalStatusField.setText(rs.getString("marital_status") != null ? rs.getString("marital_status") : "N/A");
                    } else {
                        JOptionPane.showMessageDialog(null, "No member or leader found with the specified name.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading information from the database.\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Make the dialog visible
        infoDialog.setVisible(true);
    }


    public String getLeaderName(JPanel groupRow) {
        if (groupRow == null) {
            System.out.println("groupRow is null!"); 
            return null;
        }

        Component[] components = groupRow.getComponents();
        if (components.length > 0 && components[0] instanceof JLabel) {
            JLabel leaderNameLabel = (JLabel) components[0];
            return leaderNameLabel.getText();
        } else {
            System.out.println("Leader name label not found in groupRow!"); 
            return null;
        }
    }

    private static boolean deleteGroupFromDatabase(String leaderName) {
        if (leaderName == null || leaderName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid leader name.");
            return false;
        }

        try (Connection conn = Database.getConnection()) {
            
           
            String deleteMembersQuery = "DELETE FROM `member_groups` WHERE `group_id` IN (SELECT `group_id` FROM `bible_study_groups` WHERE `leader_name` = ?)";
            PreparedStatement deleteMembersStmt = conn.prepareStatement(deleteMembersQuery);
            deleteMembersStmt.setString(1, leaderName);
            deleteMembersStmt.executeUpdate();
            
           
            String query = "DELETE FROM `bible_study_groups` WHERE leader_name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, leaderName);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                return true; 
            } else {
                JOptionPane.showMessageDialog(null, "No group found with the specified leader name.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting group.");
            return false; 
        }
    }


    private void rearrangeRows() {
       
        DefaultTableModel model = (DefaultTableModel) groupTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);  
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void Reports() {
        removeAll();
        setLayout(new BorderLayout());

        // Taskbar Panel
        JPanel taskbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.ORANGE);
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new MainPage());
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        JButton exportReportButton = new JButton("Export Report");
        exportReportButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Export Report feature is under development.",
                    "Export", JOptionPane.INFORMATION_MESSAGE);
        });

        taskbar.add(backButton);
        taskbar.add(exportReportButton);
        add(taskbar, BorderLayout.NORTH);

        // Main Report Panel
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));

        try (Connection conn = Database.getConnection()) {

            // Group Overview Panel
            String groupOverviewSql = """
                    SELECT g.leader_name, COUNT(g.group_id) AS group_count, 
                           SUM(CASE WHEN mg.group_id IS NOT NULL THEN 1 ELSE 0 END) AS total_members,
                           g.created_at 
                    FROM bible_study_groups g
                    LEFT JOIN member_groups mg ON g.group_id = mg.group_id
                    LEFT JOIN members m ON mg.member_id = m.id
                    GROUP BY g.leader_name, g.created_at
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(groupOverviewSql);
                 ResultSet rs = stmt.executeQuery()) {
                JPanel groupOverviewPanel = new JPanel(new BorderLayout());
                groupOverviewPanel.setBorder(BorderFactory.createTitledBorder("Group Overview"));

                String[] columnNames = {"Leader", "Groups", "Total Members", "Created At"};
                DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
                JTable groupTable = new JTable(tableModel);
                groupTable.setFillsViewportHeight(true);

                while (rs.next()) {
                    String leaderName = rs.getString("leader_name");
                    int groupCount = rs.getInt("group_count");
                    int totalMembers = rs.getInt("total_members");
                    String createdAt = rs.getTimestamp("created_at").toLocalDateTime().toLocalDate().toString();

                    tableModel.addRow(new Object[]{leaderName, groupCount, totalMembers, createdAt});
                }

                JScrollPane tableScrollPane = new JScrollPane(groupTable);
                groupOverviewPanel.add(tableScrollPane, BorderLayout.CENTER);

                JButton leaderButton = new JButton("Leaders");
                leaderButton.addActionListener(e -> Leaders());
                leaderButton.setBackground(new Color(85, 174, 85));
                leaderButton.setForeground(Color.WHITE);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.add(leaderButton);
                groupOverviewPanel.add(buttonPanel, BorderLayout.SOUTH);

                reportPanel.add(groupOverviewPanel);
            }

            // Members by Role Panel
            String roleBreakdownSql = "SELECT role, COUNT(*) AS role_count FROM members GROUP BY role";

            try (PreparedStatement stmt = conn.prepareStatement(roleBreakdownSql);
                 ResultSet rs = stmt.executeQuery()) {
                JPanel roleBreakdownPanel = new JPanel(new BorderLayout());
                roleBreakdownPanel.setBorder(BorderFactory.createTitledBorder("Members by Role"));

                String[] columnNames = {"Role", "Members", "Leaders"};
                DefaultTableModel roleTableModel = new DefaultTableModel(columnNames, 0);
                JTable roleTable = new JTable(roleTableModel);
                roleTable.setFillsViewportHeight(true);

                while (rs.next()) {
                    String role = rs.getString("role");
                    int roleCount = rs.getInt("role_count");
                    // Placeholder for leaders count - replace with a proper query if necessary
                    int leaderCount = (role.equalsIgnoreCase("Leader")) ? roleCount : 0;

                    roleTableModel.addRow(new Object[]{role, roleCount - leaderCount, leaderCount});
                }

                JScrollPane roleScrollPane = new JScrollPane(roleTable);
                roleBreakdownPanel.add(roleScrollPane, BorderLayout.CENTER);
                reportPanel.add(roleBreakdownPanel);
            }

            // Task Completion Panel
            String taskCompletionSql = """
                    SELECT 
                           SUM(CASE WHEN progress = 'Completed' THEN 1 ELSE 0 END) AS completed_tasks, 
                           SUM(CASE WHEN progress = 'InProgress' THEN 1 ELSE 0 END) AS in_progress_tasks 
                    FROM Notes
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(taskCompletionSql);
                 ResultSet rs = stmt.executeQuery()) {
                JPanel taskCompletionPanel = new JPanel(new BorderLayout());
                taskCompletionPanel.setBorder(BorderFactory.createTitledBorder("Task Completion"));
                taskCompletionPanel.setPreferredSize(new Dimension(200, 100)); // Make the panel smaller

                StringBuilder content = new StringBuilder();
                if (rs.next()) {
                    int completedTasks = rs.getInt("completed_tasks");
                    int inProgressTasks = rs.getInt("in_progress_tasks");
                    content.append(String.format("Completed Tasks: %d%nIn-Progress Tasks: %d",
                            completedTasks, inProgressTasks));
                }

                JTextArea textArea = new JTextArea(content.toString());
                textArea.setEditable(false);
                taskCompletionPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel();
                JButton completedButton = new JButton("View Notes");
                completedButton.setBackground(new Color(85, 174, 85));
                completedButton.setForeground(Color.white);
                completedButton.addActionListener(e -> Notes());
                buttonPanel.add(completedButton);
                
                
                JButton InProgress = new JButton("InProgress");
                InProgress.setBackground(Color.orange);
                InProgress.setForeground(Color.white);
                InProgress.addActionListener(e -> {
                    try {
                        // Locate the "Back" button coordinates
                        Point backButtonLocation = backButton.getLocationOnScreen();
                        int x = backButtonLocation.x + backButton.getWidth() / 2; // Center of the button
                        int y = backButtonLocation.y + backButton.getHeight() / 2;

                        // Simulate the mouse movement and click for "Back" button
                        Robot robot = new Robot();
                        robot.mouseMove(x, y);
                        robot.delay(100); // Optional: add delay for smoother movement
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                        // Wait for the "Back" button action to complete before continuing
                       

                    } catch (AWTException awtEx) {
                        awtEx.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Unable to simulate a click.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });


                buttonPanel.add(InProgress);

                taskCompletionPanel.add(buttonPanel, BorderLayout.SOUTH);
                reportPanel.add(taskCompletionPanel);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating reports from the database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(reportPanel);
        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 

   

    public void Leaders2() {
        removeAll();
        setLayout(new BorderLayout());

        JPanel taskbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.ORANGE);
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new MainPage());
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        taskbar.add(backButton);
        add(taskbar, BorderLayout.NORTH);

        JPanel leaderPanel = new JPanel();
        leaderPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Leader Name", "Role", "Address", "Phone", "Secondary Phone", "Gender", "Marriage Status", "Sub City", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable leaderTable = new JTable(model);
        leaderTable.setFillsViewportHeight(true);
        leaderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leaderTable.setDefaultEditor(Object.class, null); 

        JScrollPane tableScrollPane = new JScrollPane(leaderTable);
        leaderPanel.add(tableScrollPane, BorderLayout.CENTER);

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(85, 174, 85));
        updateButton.setForeground(Color.white);
        updateButton.addActionListener(e -> {
           
            int selectedRow = leaderTable.getSelectedRow();
            if (selectedRow != -1) {
                String leaderName = (String) leaderTable.getValueAt(selectedRow, 0);
                String role = (String) leaderTable.getValueAt(selectedRow, 1);
                String address = (String) leaderTable.getValueAt(selectedRow, 2);
                String phone = (String) leaderTable.getValueAt(selectedRow, 3);
                String secondaryPhone = (String) leaderTable.getValueAt(selectedRow, 4);
                String gender = (String) leaderTable.getValueAt(selectedRow, 5);
                String marriageStatus = (String) leaderTable.getValueAt(selectedRow, 6);
                String subCity = (String) leaderTable.getValueAt(selectedRow, 7);
                String createdAt = (String) leaderTable.getValueAt(selectedRow, 8);

               
                openUpdateDialog(leaderTable, leaderName, role, address, phone, secondaryPhone, gender, marriageStatus, subCity, createdAt);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a leader to update.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(updateButton);
        leaderPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadLeadersFromDatabase(leaderTable); 

        JScrollPane scrollPane = new JScrollPane(leaderPanel);
        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void Leaders() {
        removeAll();
        setLayout(new BorderLayout());

        JPanel taskbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.ORANGE);
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> {
            Reports(); 
        });

        taskbar.add(backButton);
        add(taskbar, BorderLayout.NORTH);

        JPanel leaderPanel = new JPanel();
        leaderPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Leader Name", "Role", "Address", "Phone", "Secondary Phone", "Gender", "Marriage Status", "Sub City", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable leaderTable = new JTable(model);
        leaderTable.setFillsViewportHeight(true);
        leaderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leaderTable.setDefaultEditor(Object.class, null); 

        JScrollPane tableScrollPane = new JScrollPane(leaderTable);
        leaderPanel.add(tableScrollPane, BorderLayout.CENTER);

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(85, 174, 85));
        updateButton.setForeground(Color.white);
        updateButton.addActionListener(e -> {
           
            int selectedRow = leaderTable.getSelectedRow();
            if (selectedRow != -1) {
                String leaderName = (String) leaderTable.getValueAt(selectedRow, 0);
                String role = (String) leaderTable.getValueAt(selectedRow, 1);
                String address = (String) leaderTable.getValueAt(selectedRow, 2);
                String phone = (String) leaderTable.getValueAt(selectedRow, 3);
                String secondaryPhone = (String) leaderTable.getValueAt(selectedRow, 4);
                String gender = (String) leaderTable.getValueAt(selectedRow, 5);
                String marriageStatus = (String) leaderTable.getValueAt(selectedRow, 6);
                String subCity = (String) leaderTable.getValueAt(selectedRow, 7);
                String createdAt = (String) leaderTable.getValueAt(selectedRow, 8);

              
                openUpdateDialog(leaderTable, leaderName, role, address, phone, secondaryPhone, gender, marriageStatus, subCity, createdAt);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a leader to update.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(updateButton);
        leaderPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadLeadersFromDatabase(leaderTable); 

        JScrollPane scrollPane = new JScrollPane(leaderPanel);
        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    
    private void loadLeadersFromDatabase(JTable leaderTable) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT leader_name, leader_role, leader_address, leader_phone, leader_secondary_phone, " +
                         "leader_gender, leader_marriage_status, sub_city, created_at FROM bible_study_groups";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                DefaultTableModel model = (DefaultTableModel) leaderTable.getModel();
                model.setRowCount(0); 

                while (rs.next()) {
                    String leaderName = rs.getString("leader_name");
                    String role = rs.getString("leader_role");
                    String address = rs.getString("leader_address");
                    String phone = rs.getString("leader_phone");
                    String secondaryPhone = rs.getString("leader_secondary_phone");
                    String gender = rs.getString("leader_gender");
                    String marriageStatus = rs.getString("leader_marriage_status");
                    String subCity = rs.getString("sub_city");
                    String createdAt = rs.getTimestamp("created_at").toLocalDateTime().toLocalDate().toString();

                    model.addRow(new Object[]{leaderName, role, address, phone, secondaryPhone, gender, marriageStatus, subCity, createdAt});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading leaders from the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    private void openUpdateDialog(JTable leaderTable, String leaderName, String role, String address, String phone, String secondaryPhone,
            String gender, String marriageStatus, String subCity, String createdAt) {
        JDialog updateDialog = new JDialog((JFrame) null, "Update Leader", true);
        updateDialog.setLayout(null);
        updateDialog.setSize(400, 500);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int centerX = (screenSize.width - updateDialog.getWidth()) / 2;
        int centerY = (screenSize.height - updateDialog.getHeight()) / 2;
        updateDialog.setLocation(centerX, centerY);

       
        JTextField nameField = createLabelAndTextField(updateDialog, "Leader Name:", leaderName, 20);
        JTextField roleField = createLabelAndTextField(updateDialog, "Role:", role, 60);
        JTextField addressField = createLabelAndTextField(updateDialog, "Address:", address, 100);
        JTextField phoneField = createLabelAndTextField(updateDialog, "Phone:", phone, 140);
        JTextField secondaryPhoneField = createLabelAndTextField(updateDialog, "Secondary Phone:", secondaryPhone, 180);
        JTextField genderField = createLabelAndTextField(updateDialog, "Gender:", gender, 220);
        JTextField marriageStatusField = createLabelAndTextField(updateDialog, "Marriage Status:", marriageStatus, 260);
        JTextField subCityField = createLabelAndTextField(updateDialog, "Sub City:", subCity, 300);
        JTextField createdAtField = createLabelAndTextField(updateDialog, "Created At:", createdAt, 340);

       
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(200, 380, 100, 30);
        cancelButton.setBackground(Color.red);
        cancelButton.setForeground(Color.white);
        cancelButton.addActionListener(e -> updateDialog.dispose());
        updateDialog.add(cancelButton);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(50, 380, 100, 30);
        saveButton.addActionListener(e -> {
            saveUpdatedLeaderData(leaderTable, leaderName, nameField.getText(), roleField.getText(), addressField.getText(),
                                  phoneField.getText(), secondaryPhoneField.getText(), genderField.getText(),
                                  marriageStatusField.getText(), subCityField.getText(), createdAtField.getText());
            updateDialog.dispose();
        });
        updateDialog.add(saveButton);

        updateDialog.setVisible(true);
    }

   
    private JTextField createLabelAndTextField(JDialog dialog, String label, String value, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(20, y, 120, 30);
        dialog.add(lbl);

        JTextField textField = new JTextField(value);
        textField.setBounds(150, y, 200, 30);
        dialog.add(textField);

        return textField;
    }


    private void saveUpdatedLeaderData(JTable leaderTable, String oldLeaderName, String leaderName, String role, String address,
                                       String phone, String secondaryPhone, String gender, String marriageStatus,
                                       String subCity, String createdAt) {
        try (Connection conn = Database.getConnection()) {
            String updateSql = "UPDATE bible_study_groups SET leader_name = ?, leader_role = ?, leader_address = ?, " +
                               "leader_phone = ?, leader_secondary_phone = ?, leader_gender = ?, leader_marriage_status = ?, " +
                               "sub_city = ?, created_at = ? WHERE leader_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, leaderName);
                stmt.setString(2, role);
                stmt.setString(3, address);
                stmt.setString(4, phone);
                stmt.setString(5, secondaryPhone);
                stmt.setString(6, gender);
                stmt.setString(7, marriageStatus);
                stmt.setString(8, subCity);
                stmt.setString(9, createdAt);
                stmt.setString(10, oldLeaderName);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Leader information updated successfully.");
                    loadLeadersFromDatabase(leaderTable); // Refresh the table immediately
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating leader information.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void Notes() {
        removeAll();

        groupPanel = new JPanel();
        groupPanel.setLayout(new BorderLayout());

        String[] columnNames = {"No.", "Task", "Progress", "Deadline"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; 
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        taskTable = new JTable(tableModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 2) { // Progress column
                    JComboBox<String> comboBox = new JComboBox<>(new String[]{"InProgress", "Completed"});
                    comboBox.addActionListener(e -> {
                        String progress = (String) comboBox.getSelectedItem();
                        String task = (String) tableModel.getValueAt(row, 1);
                        updateTaskProgressInDatabase(task, progress);
                        updateTaskCounts(); 
                    });
                    return new DefaultCellEditor(comboBox);
                }
                return super.getCellEditor(row, column);
            }
        };

        // Set column widths
        TableColumn noColumn = taskTable.getColumnModel().getColumn(0);
        noColumn.setPreferredWidth(35);
        noColumn.setMaxWidth(40);
        noColumn.setMinWidth(35);

        taskTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 2) {
                    String task = (String) tableModel.getValueAt(row, 1);
                    String progress = (String) table.getValueAt(row, column);
                    if ("InProgress".equals(progress)) {
                        cell.setBackground(Color.ORANGE);
                        cell.setForeground(Color.BLACK);
                    } else if ("Completed".equals(progress)) {
                        cell.setBackground(Color.GREEN.brighter());
                        cell.setForeground(Color.BLACK);
                    } else {
                        cell.setBackground(Color.WHITE);
                        cell.setForeground(Color.BLACK);
                    }
                } else if (column == 3) {
                    String progress = (String) table.getValueAt(row, 2);
                    String deadlineStr = (String) table.getValueAt(row, column);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    if ("Completed".equals(progress)) {
                        cell.setBackground(Color.GREEN.brighter());
                        cell.setForeground(Color.BLACK);
                    } else {
                        try {
                            Date deadline = dateFormat.parse(deadlineStr);
                            long diffInMillis = deadline.getTime() - new Date().getTime();
                            long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);

                            if (diffInDays <= 2) {
                                cell.setBackground(Color.RED);
                                cell.setForeground(Color.WHITE);
                            } else {
                                cell.setBackground(Color.WHITE);
                                cell.setForeground(Color.BLACK);
                            }
                        } catch (Exception ex) {
                            cell.setBackground(Color.WHITE);
                            cell.setForeground(Color.BLACK);
                        }
                    }
                } else {
                    cell.setBackground(Color.WHITE);
                    cell.setForeground(Color.BLACK);
                }

                return cell;
            }
        });

        taskTable.setRowHeight(30);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane taskScrollPane = new JScrollPane(taskTable);
        taskScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.ORANGE);
        backButton.setForeground(Color.BLACK);

        sortComboBox = new JComboBox<>(new String[]{"All", "InProgress", "Completed"});
        sortComboBox.addActionListener(this::filterTasks);

        backButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new MainPage());
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        addButton.addActionListener(this::showAddTaskDialog);

        deleteButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();

            if (selectedRow != -1) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this task?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    String task = (String) tableModel.getValueAt(selectedRow, 1);

                    boolean success = deleteTaskFromDatabase(task);

                    if (success) {
                        tableModel.removeRow(selectedRow);
                        rearrangeRows2();
                        updateTaskCounts(); // Update counts after deletion
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to delete the task from the database.",
                                "Deletion Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a task to delete.",
                        "No Task Selected",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

       
        JPanel taskCountPanel = new JPanel();
        taskCountPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JLabel inProgressLabel = new JLabel("InProgress: ");
        JLabel completedLabel = new JLabel("Completed: ");


        inProgressCountLabel = new JTextField(5);
        completedCountLabel = new JTextField(5);
        inProgressCountLabel.setEditable(false);
        completedCountLabel.setEditable(false);

        taskCountPanel.add(inProgressLabel);
        taskCountPanel.add(inProgressCountLabel);
        taskCountPanel.add(completedLabel);
        taskCountPanel.add(completedCountLabel);

        buttonPanel.add(backButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(sortComboBox);

        groupPanel.add(buttonPanel, BorderLayout.NORTH);
        buttonPanel.add(taskCountPanel, BorderLayout.EAST);
        groupPanel.add(taskScrollPane, BorderLayout.CENTER);
        add(groupPanel, BorderLayout.CENTER);

        loadNotesFromDatabase(taskTable);

        revalidate();
        repaint();
    }



    private void updateTaskCounts() {
        int inProgressCount = 0;
        int completedCount = 0;

     
        for (int i = 0; i < taskTable.getRowCount(); i++) {
            String progress = (String) taskTable.getValueAt(i, 2);
            if ("InProgress".equals(progress)) {
                inProgressCount++;
            } else if ("Completed".equals(progress)) {
                completedCount++;
            }
        }

       
        inProgressCountLabel.setText(String.valueOf(inProgressCount));
        completedCountLabel.setText(String.valueOf(completedCount));
    }

    private void filterTasks(ActionEvent e) {
        String selectedFilter = (String) sortComboBox.getSelectedItem();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT task, progress, deadline FROM Notes";
            if (!"All".equals(selectedFilter)) {
                sql += " WHERE progress = ?";
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (!"All".equals(selectedFilter)) {
                    stmt.setString(1, selectedFilter);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    DefaultTableModel model = (DefaultTableModel) taskTable.getModel();
                    model.setRowCount(0);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    int serialNumber = 1;

                    while (rs.next()) {
                        String task = rs.getString("task");
                        String progress = rs.getString("progress");
                        java.sql.Date deadlineDate = rs.getDate("deadline");
                        String deadline = deadlineDate != null
                                ? deadlineDate.toLocalDate().format(formatter)
                                : "No DeadLine";

                        model.addRow(new Object[]{serialNumber, task, progress, deadline});
                        serialNumber++;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error filtering tasks.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean deleteTaskFromDatabase(String task) {
        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM Notes WHERE task = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, task);
                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {
                    System.out.println("Task deleted successfully from the database: " + task);
                    return true;
                } else {
                    System.out.println("No matching task found to delete in the database.");
                    return false;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting the task from the database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    
    private void updateTaskProgressInDatabase(String task, String newProgress) {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE Notes SET progress = ? WHERE task = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newProgress);
                stmt.setString(2, task);
                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Progress updated successfully for task: " + task);
                } else {
                    System.out.println("No matching task found to update progress.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating progress in the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void rearrangeRows2() {
        DefaultTableModel model = (DefaultTableModel) taskTable.getModel();  
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0); 
        }
    }

    private void showAddTaskDialog(ActionEvent e) {
        JDialog addTaskDialog = new JDialog((Frame) null, "Add Task", true);
        addTaskDialog.setSize(400, 400);
        addTaskDialog.setLocationRelativeTo(this);
        addTaskDialog.setLayout(null);

        JLabel noteLabel = new JLabel("Note:");
        noteLabel.setBounds(30, 30, 100, 30);
        JTextArea noteTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(noteTextArea);
        scrollPane.setBounds(30, 70, 320, 100);

      
        JCheckBox deadlineCheckbox = new JCheckBox("Add Deadline");
        deadlineCheckbox.setBounds(30, 190, 150, 30);
        
        
        JLabel deadlineLabel = new JLabel("Deadline:");
        deadlineLabel.setBounds(30, 230, 100, 30);
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(30, 270, 320, 30);
        
       
        deadlineLabel.setVisible(false);
        dateSpinner.setVisible(false);
        
       
        deadlineCheckbox.addActionListener(event -> {
            boolean selected = deadlineCheckbox.isSelected();
            deadlineLabel.setVisible(selected);
            dateSpinner.setVisible(selected);
        });

        JButton addButton = new JButton("Add");
        addButton.setBounds(80, 320, 100, 30);
        addButton.addActionListener(event -> {
            String note = noteTextArea.getText().trim();
            String deadline = "No DeadLine";

            if (deadlineCheckbox.isSelected()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                deadline = dateFormat.format(dateSpinner.getValue());
            }

            if (!note.isEmpty()) {
                int nextNumber = tableModel.getRowCount() + 1;

                
                tableModel.addRow(new Object[]{nextNumber, note, "InProgress", deadline});

             
                insertNoteIntoDatabase(note, "InProgress", deadline.equals("No DeadLine") ? null : deadline);

                addTaskDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(addTaskDialog, "Please enter a note.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


      
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(200, 320, 100, 30);
        cancelButton.addActionListener(event -> addTaskDialog.dispose());

        addTaskDialog.add(noteLabel);
        addTaskDialog.add(scrollPane);
        addTaskDialog.add(deadlineCheckbox);
        addTaskDialog.add(deadlineLabel);
        addTaskDialog.add(dateSpinner);
        addTaskDialog.add(addButton);
        addTaskDialog.add(cancelButton);
        addTaskDialog.setVisible(true);
    }

    private void insertNoteIntoDatabase(String task, String progress, String deadline) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO Notes (task, progress, deadline) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, task);
                stmt.setString(2, progress);
                stmt.setDate(3, deadline != null && !deadline.equals("No DeadLine") 
                             ? java.sql.Date.valueOf(LocalDate.parse(deadline, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                             : null);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Task added to the database successfully.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving task to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void loadNotesFromDatabase(JTable taskTable) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT task, progress, deadline FROM Notes";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                DefaultTableModel model = (DefaultTableModel) taskTable.getModel();
                model.setRowCount(0);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                int serialNumber = 1;

                while (rs.next()) {
                    String task = rs.getString("task");
                    String progress = rs.getString("progress");
                    java.sql.Date deadlineDate = rs.getDate("deadline");
                    String deadline = deadlineDate != null
                            ? deadlineDate.toLocalDate().format(formatter)
                            : "No DeadLine";

                    model.addRow(new Object[]{serialNumber, task, progress, deadline});
                    serialNumber++;
                }

                
                updateTaskCounts();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading tasks from the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void Manage(String group) {
        removeAll();

        titleLabel = new JLabel("Manage Group: " + group);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));

        String[] columnNames = {"No.", "Full Name", "Gender", "Role in Church", "Primary Phone", "Secondary Phone", "Marital Status"};
        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        JTable membersTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(membersTable);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        
        loadMembers(membersTable, group);

        for (int i = 0; i < membersTable.getColumnModel().getColumnCount(); i++) {
            membersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JButton addMemberButton = new JButton("Add Member");
        addMemberButton.addActionListener(e -> {
            showAddMemberDialog(membersTable, group);
            updateRowNumbers(tableModel);
        });

        JButton ExportButton = new JButton("Export Data");
        ExportButton.addActionListener(e -> {
           
            if (membersTable.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "There is nothing to export. The table is empty.",
                        "No Data to Export",
                        JOptionPane.WARNING_MESSAGE);
                return; 
            }

            exportToCSV(membersTable);
            
        });

        JButton removeMemberButton = new JButton("Delete members");
        removeMemberButton.setBackground(Color.RED);
        removeMemberButton.setForeground(Color.white);
        removeMemberButton.addActionListener(e -> {
            int selectedRow = membersTable.getSelectedRow();
            if (selectedRow != -1) {
                String memberName = tableModel.getValueAt(selectedRow, 1).toString();

                int confirmation = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete member \"" + memberName + "\"?",
                        "Delete Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    if (deleteMemberFromDatabase(memberName)) {
                        tableModel.removeRow(selectedRow); // Remove from the table model
                        updateRowNumbers(tableModel);     // Update row numbers after removal
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a member to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.ORANGE);
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new MainPage());
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(titleLabel)
                                .addComponent(tableScrollPane)
                                .addGroup(layout.createSequentialGroup()
                                		 .addComponent(backButton)
                                        .addComponent(addMemberButton)
                                        .addComponent(ExportButton)
                                        .addComponent(removeMemberButton)
                                       )
                                       
                        )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addComponent(tableScrollPane)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        		  .addComponent(backButton)
                        		.addComponent(addMemberButton)
                        	    .addComponent(ExportButton)
                                .addComponent(removeMemberButton)
                            
                              
                        )
        );

        revalidate();
        repaint();
    }
 
  
    public void exportToCSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save As");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Ensure the file has a .csv extension
            if (!file.getName().endsWith(".csv")) {
                file = new File(file + ".csv");
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel();

            try (FileWriter writer = new FileWriter(file)) {
                // Write table headers
                for (int col = 0; col < model.getColumnCount(); col++) {
                    writer.write(model.getColumnName(col) + ",");
                }
                writer.write("\n");

                // Write table data
                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        writer.write(model.getValueAt(row, col).toString() + ",");
                    }
                    writer.write("\n");
                }

                JOptionPane.showMessageDialog(null, 
                        "Data exported successfully to " + file.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, 
                        "An error occurred while exporting the data.",
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static boolean deleteMemberFromDatabase(String memberName) {
        if (memberName == null || memberName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid member name.");
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Check the correct column name for member_name
            String columnName = "full_name"; // Replace with the correct column name

            // 1. Delete from member_groups table first
            String deleteFromMemberGroupsSQL = "DELETE FROM member_groups WHERE member_id IN (SELECT id FROM members WHERE " + columnName + " = ?)";
            stmt1 = conn.prepareStatement(deleteFromMemberGroupsSQL);
            stmt1.setString(1, memberName);
            stmt1.executeUpdate();

            // 2. Then delete from the members table
            String deleteFromMembersSQL = "DELETE FROM members WHERE " + columnName + " = ?";
            stmt2 = conn.prepareStatement(deleteFromMembersSQL);
            stmt2.setString(1, memberName);
            stmt2.executeUpdate();

            conn.commit(); // Commit the transaction
            JOptionPane.showMessageDialog(null, "Member deleted successfully.");
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction if an exception occurs
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error rolling back transaction: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting member: " + e.getMessage());
            return false;
        } finally {
            try {
                if (stmt1 != null) stmt1.close();
                if (stmt2 != null) stmt2.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Turn auto commit back on
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void updateRowNumbers(DefaultTableModel model) {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
    }

    private void showAddMemberDialog(JTable membersTable, String groupName) {

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        JDialog addMemberDialog = new JDialog(parentFrame, "Add Member", true);
        addMemberDialog.setLayout(null);
        addMemberDialog.setSize(400, 400);
        addMemberDialog.setLocationRelativeTo(parentFrame);


        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(20, 20, 100, 25);
        addMemberDialog.add(nameLabel);
        JTextField nameField = new JTextField();
        nameField.setBounds(150, 20, 200, 30);
        addMemberDialog.add(nameField);


        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(20, 60, 100, 25);
        addMemberDialog.add(genderLabel);
        JRadioButton maleRadio = new JRadioButton("Male");
        maleRadio.setBounds(150, 60, 70, 25);
        JRadioButton femaleRadio = new JRadioButton("Female");
        femaleRadio.setBounds(230, 60, 80, 25);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        addMemberDialog.add(maleRadio);
        addMemberDialog.add(femaleRadio);


        JLabel roleLabel = new JLabel("Role in Church:");
        roleLabel.setBounds(20, 100, 100, 25);
        addMemberDialog.add(roleLabel);


        String[] roles = {"Member", "Pastor", "Apostle", "Deacon", "Elder", "Choir", "Minister", "Youth Minister/Leader", "Sunday School Teacher", "Usher", "Missionary", "Media/Tech Team"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBounds(150, 100, 200, 30);
        addMemberDialog.add(roleComboBox);



        JLabel primaryPhoneLabel = new JLabel("Primary Phone:");
        primaryPhoneLabel.setBounds(20, 140, 100, 25);
        addMemberDialog.add(primaryPhoneLabel);
        JTextField primaryPhoneField = new JTextField();
        primaryPhoneField.setBounds(150, 140, 200, 30);
        addMemberDialog.add(primaryPhoneField);


        JLabel secondaryPhoneLabel = new JLabel("Secondary Phone:");
        secondaryPhoneLabel.setBounds(20, 180, 120, 25);
        addMemberDialog.add(secondaryPhoneLabel);
        JTextField secondaryPhoneField = new JTextField();
        secondaryPhoneField.setBounds(150, 180, 200, 30);
        addMemberDialog.add(secondaryPhoneField);


        JLabel maritalStatusLabel = new JLabel("Marital Status:");
        maritalStatusLabel.setBounds(20, 220, 100, 25);
        addMemberDialog.add(maritalStatusLabel);
        JComboBox<String> maritalStatusComboBox = new JComboBox<>(new String[]{"Married", "Single", "Divorced"});
        maritalStatusComboBox.setBounds(150, 220, 200, 30);
        addMemberDialog.add(maritalStatusComboBox);


        JButton saveButton = new JButton("Save");
        saveButton.setBounds(100, 300, 80, 30);
        saveButton.addActionListener(e -> {
            String fullName = nameField.getText();
            String gender = maleRadio.isSelected() ? "Male" : (femaleRadio.isSelected() ? "Female" : "");
            String role = (String) roleComboBox.getSelectedItem();
            String primaryPhone = primaryPhoneField.getText();
            String secondaryPhone = secondaryPhoneField.getText();
            String maritalStatus = (String) maritalStatusComboBox.getSelectedItem();

            if (fullName.isEmpty() || gender.isEmpty() || role.isEmpty() || primaryPhone.isEmpty()) {
                JOptionPane.showMessageDialog(addMemberDialog, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try (Connection conn = Database.getConnection()) {
                    // Insert new member into Members table
                    String sql = "INSERT INTO Members (full_name, gender, role, primary_phone, secondary_phone, marital_status) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, fullName);
                        stmt.setString(2, gender);
                        stmt.setString(3, role);
                        stmt.setString(4, primaryPhone);
                        stmt.setString(5, secondaryPhone);
                        stmt.setString(6, maritalStatus);
                        stmt.executeUpdate();

                        // Get the generated member ID
                        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int memberId = generatedKeys.getInt(1);  // Member ID

                                // Retrieve the group_id from the database based on the group name
                                String groupSql = "SELECT group_id FROM bible_study_groups WHERE leader_name = ?";
                                try (PreparedStatement groupStmt = conn.prepareStatement(groupSql)) {
                                    groupStmt.setString(1, groupName);

                                    try (ResultSet rs = groupStmt.executeQuery()) {
                                        if (rs.next()) {
                                            int groupId = rs.getInt("group_id");

                                            // Insert into member_groups for the selected group
                                            String memberGroupSql = "INSERT INTO member_groups (member_id, group_id) VALUES (?, ?)";
                                            try (PreparedStatement memberGroupStmt = conn.prepareStatement(memberGroupSql)) {
                                                memberGroupStmt.setInt(1, memberId);  // Set member ID
                                                memberGroupStmt.setInt(2, groupId);   // Set group ID
                                                memberGroupStmt.executeUpdate();  // Execute the insertion
                                            }
                                        }
                                    }
                                }

                                // Update the table with the new member
                                DefaultTableModel model = (DefaultTableModel) membersTable.getModel();
                                model.addRow(new Object[]{model.getRowCount() + 1, fullName, gender, role, primaryPhone, secondaryPhone, maritalStatus});

                                JOptionPane.showMessageDialog(addMemberDialog, "Member added successfully.");
                                addMemberDialog.dispose();
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(addMemberDialog, "Error saving member to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        addMemberDialog.add(saveButton);


        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(200, 300, 100, 30);
        cancelButton.setBackground(Color.RED);
        cancelButton.addActionListener(e -> addMemberDialog.dispose());
        addMemberDialog.add(cancelButton);

        addMemberDialog.setVisible(true);
    }



       
    
    private void loadMembers(JTable membersTable, String groupName) {
        DefaultTableModel model = (DefaultTableModel) membersTable.getModel();
        model.setRowCount(0);  // Clears any existing rows

        // SQL query for loading members filtered by group name
        String sql = "SELECT m.full_name, m.gender, m.role, m.primary_phone, m.secondary_phone, m.marital_status " +
                "FROM members m " +
                "JOIN member_groups mg ON m.id = mg.member_id " +
                "JOIN bible_study_groups bsg ON mg.group_id = bsg.group_id " +
                "WHERE bsg.leader_name = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, groupName);  // Set the group name to filter members

            try (ResultSet rs = stmt.executeQuery()) {
                int rowNumber = 1; // Start row numbers from 1

                while (rs.next()) {
                    String fullName = rs.getString("full_name");
                    String gender = rs.getString("gender");
                    String role = rs.getString("role");
                    String primaryPhone = rs.getString("primary_phone");
                    String secondaryPhone = rs.getString("secondary_phone");
                    String maritalStatus = rs.getString("marital_status");

                    // Handling potential null values to avoid NullPointerException
                    primaryPhone = (primaryPhone != null) ? primaryPhone : "N/A";
                    secondaryPhone = (secondaryPhone != null) ? secondaryPhone : "N/A";
                    maritalStatus = (maritalStatus != null) ? maritalStatus : "Unknown";

                    // Add row to the table with row number instead of member ID
                    model.addRow(new Object[]{rowNumber++, fullName, gender, role, primaryPhone, secondaryPhone, maritalStatus});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading members: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void openCreateGroupDialog() {
        CreateGroupDialog dialog = new CreateGroupDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

       
        if (dialog.isGroupCreated()) {
            String leaderName = dialog.getLeaderName();
            String address = dialog.getAddress();

         
            int rowNumber = tableModel.getRowCount() + 1;

          
            Object[] newRow = new Object[]{rowNumber, leaderName, address, 
                createViewButton(), createManageButton(leaderName), createDeleteButton()};

            
            tableModel.addRow(newRow);

           
            groupTable.revalidate();
            groupTable.repaint();

            
            updateDashboardInfo();
        }
    }

    private JButton createViewButton() {
        JButton viewButton = new JButton("View");
        viewButton.addActionListener(e -> JOptionPane.showMessageDialog(
            null, 
            "View button clicked", 
            "Info", 
            JOptionPane.INFORMATION_MESSAGE
        ));
        return viewButton;
    }

    private JButton createManageButton(String leaderName) {
        JButton manageButton = new JButton("Manage");
        manageButton.addActionListener(e -> Manage(leaderName));
        return manageButton;
    }

    private JButton createDeleteButton() {
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> {
        
        });
        return deleteButton;
    }

   
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            return this;
        }
    }

  
    static class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> JOptionPane.showMessageDialog(
                button, 
                "Button clicked in row: " + table.getSelectedRow(),
                "Info", 
                JOptionPane.INFORMATION_MESSAGE
            ));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = value != null ? value.toString() : "";
            button.setText(label);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateDashboardInfo() {
        try (Connection conn = Database.getConnection()) {
            // Fetch total groups
            String groupCountQuery = "SELECT COUNT(*) AS totalGroups FROM bible_study_groups";
            int totalGroups = 0;
            try (PreparedStatement groupStmt = conn.prepareStatement(groupCountQuery);
                 ResultSet groupRs = groupStmt.executeQuery()) {
                if (groupRs.next()) {
                    totalGroups = groupRs.getInt("totalGroups");
                }
            }

            // Fetch total leaders
            String leaderCountQuery = "SELECT COUNT(DISTINCT leader_name) AS totalLeaders FROM bible_study_groups";
            int totalLeaders = 0;
            try (PreparedStatement leaderStmt = conn.prepareStatement(leaderCountQuery);
                 ResultSet leaderRs = leaderStmt.executeQuery()) {
                if (leaderRs.next()) {
                    totalLeaders = leaderRs.getInt("totalLeaders");
                }
            }

            // Fetch total members
            String memberCountQuery = "SELECT COUNT(*) AS totalMembers FROM member_groups";
            int totalMembers = 0;
            try (PreparedStatement memberStmt = conn.prepareStatement(memberCountQuery);
                 ResultSet memberRs = memberStmt.executeQuery()) {
                if (memberRs.next()) {
                    totalMembers = memberRs.getInt("totalMembers");
                }
            }

            // Update labels
            totalGroupsLabel.setText("Total Groups: " + totalGroups);
            totalGroupsLabel.setForeground(Color.BLACK);

            totalLeadersLabel.setText("Total Leaders: " + totalLeaders);
            totalLeadersLabel.setForeground(Color.BLACK);

            totalMembersLabel.setText("Total Members: " + totalMembers);
            totalMembersLabel.setForeground(Color.BLACK);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating dashboard information.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private static void setupPlaceholder2(JTextField field, String placeholder) {
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
   }