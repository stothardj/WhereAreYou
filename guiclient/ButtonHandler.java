import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.util.*;

public class ButtonHandler implements ActionListener {

	public ButtonHandler(JPanel fieldPanel) {
		this.fieldPanel = fieldPanel;
		this.currentRequestType = "Create User";
	}
	public void actionPerformed(ActionEvent ae) {
		JButton src = ((JButton)ae.getSource());
		HashMap<String,String[]> fields = new HashMap<String,String[]>();
		
		fields.put("Create User", new String[] {"First Name", "Last Name",
				"User Name", "Password", "Account Type"} );
		fields.put("Remove User", new String[] {});
		fields.put("Add Zone", new String[] {"Zone Name", "Lat", "Lon", "Radius"});
		fields.put("Remove Zone", new String[] {"Zone Name"});
		fields.put("Add Friend", new String[] {"Friend Name"});
		fields.put("Accept Friend", new String[] {"Friend Name"});
		fields.put("Remove Friend", new String[] {"Friend Name"});
		fields.put("Update Coordinate", new String[] {"Lat", "Lon"});
		fields.put("Empty All", new String[] {});
		fields.put("Empty Friends", new String[] {});
		fields.put("Empty Users", new String[] {});
		fields.put("Empty Zones", new String[] {});
		fields.put("Login", new String[] {"User Name", "Password"});
		fields.put("Show Users", new String[] {});
		fields.put("Show Friends", new String[] {});
		fields.put("Show Zones", new String[] {});
		
		fieldPanel.removeAll();
		currentRequestType = src.getText();
		String[] arr = fields.get(currentRequestType);
		if(arr.length > 0) {
			fieldPanel.setLayout(new GridLayout(arr.length, 2));
			for(String field : arr) {
				JLabel flabel = new JLabel(field);
				fieldPanel.add(flabel);
				fieldPanel.add(new JTextField());
			}
		} else {
			fieldPanel.setLayout(new BorderLayout());
			fieldPanel.add(new JLabel(currentRequestType+" requires no fields. Hit send query and it will go."));
		}
		
		fieldPanel.revalidate();
		fieldPanel.repaint();	
	}
	
	public String getRequestType() {
		return currentRequestType;
	}
	
	private String currentRequestType;
	private JPanel fieldPanel;

}
