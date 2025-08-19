package Pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateGroupDialog extends JDialog {
	private JTextField leaderNameField, leaderAddressField, groupNameField, groupCountField, leaderPhoneField, leaderSecondaryPhoneField;
	private JComboBox<String> leaderRoleComboBox, leaderMarriageStatusComboBox,groupCountComboBox, subCityComboBox;
	private boolean groupCreated = false;
	private JRadioButton maleRadioButton, femaleRadioButton;
	private JTable groupTable;
	public CreateGroupDialog(JFrame parent) {
		 super(parent, "Create Bible Study Group", true);
		    this.groupTable = groupTable;
	    setLayout(null);

	   
	    JLabel Label = new JLabel("Leader's Info");
	    Label.setFont(new Font("Arial", Font.BOLD, 26)); 
	    Label.setBounds(150, 20, 300, 40); 
	    add(Label);

	    JLabel leaderNameLabel = new JLabel("Name:");
	    leaderNameLabel.setBounds(20, 80, 120, 25);
	    add(leaderNameLabel);

	    leaderNameField = new JTextField();
	    leaderNameField.setBounds(180, 80, 250, 30); 
	    add(leaderNameField);

	    JLabel leaderRoleLabel = new JLabel("Role in Church:");
	    leaderRoleLabel.setBounds(20, 120, 150, 25);
	    add(leaderRoleLabel);

	    String[] roles = {"Member", "Pastor", "Apostle", "Deacon", "Elder", "Choir", "Minister", "Youth Minister/Leader", "Sunday School Teacher", "Usher", "Missionary", "Media/Tech Team"};
	    leaderRoleComboBox = new JComboBox<>(roles);
	    leaderRoleComboBox.setBounds(180, 120, 250, 30);
	    add(leaderRoleComboBox);

	    JLabel leaderAddressLabel = new JLabel("Leader's Address:");
	    leaderAddressLabel.setBounds(20, 160, 120, 25);
	    add(leaderAddressLabel);

	    leaderAddressField = new JTextField();
	    leaderAddressField.setBounds(180, 160, 250, 30);
	    add(leaderAddressField);

	    JLabel leaderPhoneLabel = new JLabel("Phone Number:");
	    leaderPhoneLabel.setBounds(20, 200, 150, 25);
	    add(leaderPhoneLabel);

	    leaderPhoneField = new JTextField();
	    leaderPhoneField.setBounds(180, 200, 250, 30);
	    add(leaderPhoneField);

	    JLabel leaderSecondaryPhoneLabel = new JLabel("Secondary Phone:");
	    leaderSecondaryPhoneLabel.setBounds(20, 240, 180, 25);
	    add(leaderSecondaryPhoneLabel);

	    leaderSecondaryPhoneField = new JTextField();
	    leaderSecondaryPhoneField.setBounds(180, 240, 250, 30);
	    add(leaderSecondaryPhoneField);

	    JLabel leaderGenderLabel = new JLabel("Gender:");
	    leaderGenderLabel.setBounds(20, 280, 120, 25);
	    add(leaderGenderLabel);

	    maleRadioButton = new JRadioButton("Male");
	    maleRadioButton.setBounds(180, 280, 70, 25);
	    add(maleRadioButton);

	    femaleRadioButton = new JRadioButton("Female");
	    femaleRadioButton.setBounds(260, 280, 70, 25);
	    add(femaleRadioButton);

	    ButtonGroup genderGroup = new ButtonGroup();
	    genderGroup.add(maleRadioButton);
	    genderGroup.add(femaleRadioButton);

	    JLabel leaderMarriageStatusLabel = new JLabel("Marriage Status:");
	    leaderMarriageStatusLabel.setBounds(20, 320, 160, 25);
	    add(leaderMarriageStatusLabel);

	    String[] marriageStatusOptions = {"Married", "Single", "Divorced"};
	    leaderMarriageStatusComboBox = new JComboBox<>(marriageStatusOptions);
	    leaderMarriageStatusComboBox.setBounds(180, 320, 250, 30);
	    add(leaderMarriageStatusComboBox);

	    JLabel groupCountLabel = new JLabel("Number of Groups:");
	    groupCountLabel.setBounds(20, 360, 160, 25);
	    add(groupCountLabel);

	    String[] groupCounts = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
	    groupCountComboBox = new JComboBox<>(groupCounts);
	    groupCountComboBox.setBounds(180, 360, 250, 30);
	    add(groupCountComboBox);

	    JLabel groupNameLabel = new JLabel("Group Name:");
	    groupNameLabel.setBounds(20, 400, 120, 25); 
	    add(groupNameLabel);

	    groupNameField = new JTextField(); 
	    groupNameField.setBounds(180, 400, 250, 30);
	    add(groupNameField);

	    JLabel subCityLabel = new JLabel("Tutoring SubCity:");
	    subCityLabel.setBounds(20, 440, 120, 25);
	    add(subCityLabel);

	    String[] subCities = {"Yeka", "Addis Ketema", "Akaky Kaliti", "Arada", "Bole", "Gulele", "Kirkos", "Kolfe Keranio", "Lideta", "Nifas Silk-Lafto", "Lemi Kura"};
	    subCityComboBox = new JComboBox<>(subCities);
	    subCityComboBox.setBounds(180, 440, 250, 30);
	    add(subCityComboBox);

	    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
	    separator.setBounds(0, 393, 500, 10);
	    add(separator);

	    JButton saveButton = new JButton("Create");
	    saveButton.setBounds(120, 480, 120, 30);
	    saveButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            createGroup();
	            groupCreated = true;
	            dispose();
	        }
	    });
	    add(saveButton);

	    JButton cancelButton = new JButton("Cancel");
	    cancelButton.setBounds(260, 480, 120, 30);
	    cancelButton.setBackground(Color.RED);
	    cancelButton.addActionListener(e -> dispose());
	    add(cancelButton);
	    setResizable(false);
	    setSize(500, 550); 
	    setLocationRelativeTo(parent); 
	}

	public static boolean addGroupToDatabase(String groupName, String leaderName, String leaderRole, String leaderAddress, 
	        String leaderPhone, String leaderSecondaryPhone, String leaderGender, 
	        String leaderMarriageStatus, int groupCount, String subCity) {
	    String checkSQL = "SELECT COUNT(*) FROM bible_study_groups WHERE group_name = ? AND leader_name = ?";
	    String insertSQL = "INSERT INTO bible_study_groups (group_name, leader_name, leader_role, leader_address, leader_phone, " +
	            "leader_secondary_phone, leader_gender, leader_marriage_status, group_count, sub_city) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (Connection connection = Database.getConnection(); 
	         PreparedStatement checkStmt = connection.prepareStatement(checkSQL);
	         PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {

	        checkStmt.setString(1, groupName);
	        checkStmt.setString(2, leaderName);
	        var rs = checkStmt.executeQuery();
	        rs.next();
	        if (rs.getInt(1) > 0) {
	            System.out.println("Group and leader already exist: " + groupName + ", " + leaderName);
	            return false; 
	        }

	        insertStmt.setString(1, groupName);
	        insertStmt.setString(2, leaderName);
	        insertStmt.setString(3, leaderRole);
	        insertStmt.setString(4, leaderAddress);
	        insertStmt.setString(5, leaderPhone);
	        insertStmt.setString(6, leaderSecondaryPhone);
	        insertStmt.setString(7, leaderGender);
	        insertStmt.setString(8, leaderMarriageStatus);
	        insertStmt.setInt(9, groupCount);
	        insertStmt.setString(10, subCity);

	        int rowsAffected = insertStmt.executeUpdate();
	        return rowsAffected > 0; 

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	private void createGroup() {
	    String groupName = groupNameField.getText();  // Correct field for group name
	    String leaderName = leaderNameField.getText();
	    String leaderRole = (String) leaderRoleComboBox.getSelectedItem();
	    String leaderAddress = leaderAddressField.getText();
	    String leaderPhone = leaderPhoneField.getText();
	    String leaderSecondaryPhone = leaderSecondaryPhoneField.getText();
	    String leaderGender = maleRadioButton.isSelected() ? "Male" : "Female";
	    String leaderMarriageStatus = (String) leaderMarriageStatusComboBox.getSelectedItem();
	    int groupCount = Integer.parseInt((String) groupCountComboBox.getSelectedItem());  // Using ComboBox value
	    String subCity = (String) subCityComboBox.getSelectedItem();  // Using ComboBox for SubCity

	    if (groupName.isEmpty() || leaderName.isEmpty() || leaderRole.isEmpty() || leaderAddress.isEmpty() || leaderPhone.isEmpty() || leaderGender.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    boolean isGroupAdded = addGroupToDatabase(groupName, leaderName, leaderRole, leaderAddress, leaderPhone, 
	            leaderSecondaryPhone, leaderGender, leaderMarriageStatus, groupCount, subCity);

	    if (isGroupAdded) {
	        JOptionPane.showMessageDialog(this, "Group Created Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

	        if (groupTable != null) {
	            DefaultTableModel model = (DefaultTableModel) groupTable.getModel();
	            model.addRow(new Object[]{model.getRowCount() + 1, leaderName, leaderAddress});
	        } 

	    } else {
	        JOptionPane.showMessageDialog(this, "Failed to create group. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}


    public boolean isGroupCreated() {
        return groupCreated;
    }

    public String getLeaderName() {
        return leaderNameField.getText();
    }
    
    public String getAddress() {
    	return leaderAddressField.getText();
    }
}