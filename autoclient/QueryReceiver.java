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
				if(s == null)
					jc.disconnect();
				else
					System.out.println("Server to "+user+": "+s);
			} catch (IOException e) {
				//Note: This may seem ugly, but we want to be able to disconnect at any point
				System.err.println("This stack trace is supposed to happen ------");
				e.printStackTrace();
				System.err.println("The previous stack trace was supposed to happen ------");
			}
		}
		
	}
	
	private JavaClient jc;
	private String user;
}
