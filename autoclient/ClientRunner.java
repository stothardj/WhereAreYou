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
	
	public static void pause(int milli) {
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
	}
	
	public void runTests() {
		AndroidUser jake = new AndroidUser("stothard");
		AndroidUser brian = new AndroidUser("bagrm");
		AndroidUser alex = new AndroidUser("fork");
		jake.emptyAll();
		jake.createUser("Jake", "Stothard", "cake", "User");
		brian.createUser("Brian", "Garfield", "password", "User");
		alex.createUser("Alex", "Farkas", "knife", "User");
		
		ClientRunner.pause(2000);
		
		jake.login("cake");
		brian.login("password");		
		alex.login("spoon");
		alex.login("knife");
		
		ClientRunner.pause(2000);
		
		jake.addFriend("not here");
		jake.addFriend("fork");
		
		ClientRunner.pause(2000);
		
		jake.updateCoordinate("3", "4.2");
		
		ClientRunner.pause(2000);
		
		alex.acceptFriend("stothard");
		
		ClientRunner.pause(2000);
		
		jake.updateCoordinate("5.1", "-1.2");
		
		ClientRunner.pause(2000);	
	}
	
	public void interactiveMode() {
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
			while((fromUser = stdIn.readLine()) != null && !fromUser.equals("exit")) {
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
	
	@Override
	public void run() {
		
		runTests();
		
		if(INTERACTIVE) {
			interactiveMode();
		}
		
		System.out.println("Client runner is down.");
	}
}
