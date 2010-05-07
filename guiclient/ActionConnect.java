import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ActionConnect implements ActionListener {

	public ActionConnect(WebApp wa) {
		this.wa = wa;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		try {
			String host = ((JTextField)wa.queryPanel.getComponent(0)).getText();
			int port = Integer.parseInt(((JTextField)wa.queryPanel.getComponent(1)).getText());
			wa.queryPanel.setLayout(new GridLayout(3,1));
			JButton sendQuery = new JButton("Send Query");
			sendQuery.addActionListener(new QuerySender(host, port, wa.fieldPanel, wa.queryPanel, wa.bh));
			wa.queryPanel.removeAll();
			JTextArea cta = new JTextArea("Client: ");
			cta.setLineWrap(true);
			cta.setEditable(false);
			wa.queryPanel.add(cta);
			JTextArea sta = new JTextArea("Server: ");
			sta.setLineWrap(true);
			sta.setEditable(false);
			wa.queryPanel.add(sta);
			wa.queryPanel.add(sendQuery);
			
			wa.queryPanel.revalidate();
			wa.queryPanel.repaint();
		} catch (Exception e) {
			e.printStackTrace();
			JLabel errorMessage = new JLabel("Unable to connect to server.");
			errorMessage.setForeground(Color.red);
			wa.queryPanel.removeAll();
			wa.queryPanel.add(errorMessage);
			wa.queryPanel.revalidate();
			wa.queryPanel.repaint();
		}
		
	}

	private WebApp wa;
}
