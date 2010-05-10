import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class QuerySender implements ActionListener {

	public QuerySender(String host, int port, JPanel fieldPanel, JPanel queryPanel, ButtonHandler bh) throws UnknownHostException, IOException {
		this.fieldPanel = fieldPanel;
		this.queryPanel = queryPanel;
		this.bh = bh;
		jc = new JavaClient(host, port);
		jc.connect();
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		//Generate JSON
		String running = "{ \"Request Type\" : \"" + bh.getRequestType() + "\" , ";
		for(int i=0; i < fieldPanel.getComponentCount() - 1; i += 2) {
			JLabel tl = (JLabel)fieldPanel.getComponent(i);
			JTextField tf = (JTextField)fieldPanel.getComponent(i + 1);
			String tls = tl.getText();
			String tfs = tf.getText();
			if(tls.equals("Lat") || tls.equals("Lon")) //int
				running += "\""+tls+"\" : "+ tfs + ", ";
			else //string
				running += "\""+tls+"\" : \""+ tfs + "\", ";
		}
		running = running.substring(0, running.length() - 2);
		running += "}";
		//System.out.println(running);
		((JTextArea)queryPanel.getComponent(0)).setText("Client: "+running);
		
		//Send query to server
		jc.sendLine(running);
		
		try {
			String s = jc.readLine();
			//System.out.println("Server response: "+s);
			((JTextArea)queryPanel.getComponent(1)).setText("Server: "+s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ButtonHandler bh;
	private JavaClient jc;
	private JPanel fieldPanel, queryPanel;
}
