import java.io.IOException;

public class QueryReceiver extends Thread {
	
	public QueryReceiver(JavaClient jc, String user) {
		this.jc = jc;
		this.user = user;
	}
	
	@Override
	public void run() {
		
		while(jc.isConnected()) { //I know there is a better way, look into later
			try {
				String s = jc.readLine();
				System.out.println("Server to "+user+": "+s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private JavaClient jc;
	private String user;
}
