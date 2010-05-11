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
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		JButton src = ((JButton)ae.getSource());
		HashMap<String,String[]> fields = new HashMap<String,String[]>();
		
		fields.put("Create User", new String[] {"First Name", "Last Name",
				"User Name", "Password", "Account Type"} );
		fields.put("Remove User", new String[] {"User Name", "Password"});
		fields.put("Add Zone", new String[] {"User Name", "Zone Name", "Lat", "Lon", "Radius", "Password"});
		fields.put("Remove Zone", new String[] {"User Name", "Zone Name", "Password"});
		fields.put("Add Friend", new String[] {"User Name", "Friend Name", "Password"});
		fields.put("Accept Friend", new String[] {"User Name", "Friend Name", "Password"});
		fields.put("Remove Friend", new String[] {"User Name", "Friend Name", "Password"});
		fields.put("Update Coordinate", new String[] {"User Name", "Lat", "Lon", "Password"});
		
		
		fieldPanel.removeAll();
		currentRequestType = src.getText();
		String[] arr = fields.get(currentRequestType);
		fieldPanel.setLayout(new GridLayout(arr.length, 2));
		for(String field : arr) {
			JLabel flabel = new JLabel(field);
			fieldPanel.add(flabel);
			fieldPanel.add(new JTextField());
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