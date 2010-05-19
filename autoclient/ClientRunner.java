import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class ClientRunner implements Runnable {
	/*
	 * Set this to true if you want to be put in an
	 * interactive client terminal after tests have
	 * been run. 
	 */
	public static final boolean INTERACTIVE = true;
	
	public static void main(String args[]) {
		Thread t = new Thread(new ClientRunner());
		t.start();
	}
	
	@Override
	public void run() {
		
		AndroidUser jake = new AndroidUser("stothard");
		AndroidUser brian = new AndroidUser("bagrm");
		jake.login("cake");
		brian.login("password");
		
		//TODO: Add various tests using functions you create
		
		/*
		 * I want to easily be able to add tests here so that
		 * I can repeatedly run them against the server. 
		 */
		
		if(INTERACTIVE) {
			JavaClient jc = new JavaClient("general", "localhost", 79);
			try {
				jc.connect();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			System.out.println("Entered interactive mode.");
			System.out.println("Enter 'exit' to leave.");
			
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			String fromUser;
			
			try {
				while(!(fromUser = stdIn.readLine()).equals("exit")) {
					jc.sendLine(fromUser);
					System.out.println("Client: "+fromUser);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Leaving interactive mode.");
			
			try {
				jc.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Client runner is down.");
	}
}
