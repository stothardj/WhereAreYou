import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class QuerySender implements ActionListener {

	public QuerySender(JavaClient jc, JPanel fieldPanel, JPanel queryPanel, ButtonHandler bh) throws UnknownHostException, IOException {
		this.fieldPanel = fieldPanel;
		this.queryPanel = queryPanel;
		this.bh = bh;
		this.jc = jc;
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
			if(tls.equals("Lat") || tls.equals("Lon") || tls.equals("Radius")) //float
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
	}

	private ButtonHandler bh;
	private JavaClient jc;
	private JPanel fieldPanel, queryPanel;
}
