import java.io.IOException;
import javax.swing.JTextArea;

public class QueryReceiver extends Thread {
	
	public QueryReceiver(JavaClient jc, JTextArea serverField) {
		this.jc = jc;
		this.serverField = serverField;
	}
	
	@Override
	public void run() {
		
		while(true) { //I know there is a better way, look into later
			try {
				String s = jc.readLine();
				serverField.setText("Server: "+s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private JavaClient jc;
	private JTextArea serverField;
}
