import java.awt.Color;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.*;

public class WebApp extends BaseApplet {
	private static final long serialVersionUID = 1L;

	@Override
	public void createGUI() {
		JPanel buttonPanel = new JPanel();
		fieldPanel = new JPanel();
		
		bh = new ButtonHandler(fieldPanel);
		
		getContentPane().setLayout(new GridLayout(3, 1));
		
		
		buttonPanel.setLayout(new GridLayout(2,4));
		
		LinkedList<String> commands = new LinkedList<String>();
		commands.add("Create User");
		commands.add("Remove User");
		commands.add("Add Zone");
		commands.add("Remove Zone");
		commands.add("Add Friend");
		commands.add("Accept Friend");
		commands.add("Remove Friend");
		commands.add("Update Coordinate");
		
		for(String li : commands) {
			JButton b = new JButton(li);
			b.addActionListener(bh);
			buttonPanel.add(b);
		}
		
		getContentPane().add(buttonPanel);
		
		fieldPanel.add(new JLabel("The fields will appear here."));
		getContentPane().add(fieldPanel);
		
		queryPanel = new JPanel();
		queryPanel.setLayout(new GridLayout(3,1));
		queryPanel.add(new JTextField("localhost"));
		queryPanel.add(new JTextField("79"));
		JButton accept_ipport = new JButton("Accept");
		accept_ipport.addActionListener(new ActionConnect(this));
		queryPanel.add(accept_ipport);
		getContentPane().add(queryPanel);
		
	}

	ButtonHandler bh;
	JPanel fieldPanel, queryPanel;
}
