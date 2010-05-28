import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Random;

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
	
	public void pushTest(int delay) {
		AndroidUser jake = new AndroidUser("stothard");
		AndroidUser brian = new AndroidUser("bagrm");
		AndroidUser alex = new AndroidUser("fork");
		jake.emptyAll();
		
		ClientRunner.pause(delay);
		
		jake.createUser("Jake", "Stothard", "cake", "User");
		brian.createUser("Brian", "Garfield", "password", "User");
		alex.createUser("Alex", "Farkas", "knife", "User");
		
		ClientRunner.pause(delay);
		
		jake.login("cake");
		brian.login("password");		
		alex.login("spoon");
		alex.login("knife");
		
		ClientRunner.pause(delay);
		
		jake.addFriend("not here");
		jake.addFriend("fork");
		jake.addZone("Home", "13.4", "-7.4", "13.1", "SHOWTEXT", "I am asleep");
		jake.addZone("Work", "103.4", "-7.04", "103.1", "SHOWGPS", "Playing on the computer");
		
		
		ClientRunner.pause(delay);

		jake.updateCoordinate("3", "4.2");
		
		ClientRunner.pause(delay);
		
		alex.acceptFriend("stothard");
		
		ClientRunner.pause(delay);
		
		jake.updateCoordinate("5.1", "-1.2");
		
		ClientRunner.pause(delay);
		
		jake.disconnect();
		alex.disconnect();
		brian.disconnect();
	}
	
	public void stressTest(int delay, int numPeople, int numPosUpdates) {
		AndroidUser[] as = new AndroidUser[numPeople];
		AndroidUser temp = new AndroidUser("temp");
		temp.emptyAll();
		temp.disconnect();
		ClientRunner.pause(delay);
		for(int i=0; i<as.length ; i++) {
			as[i] = new AndroidUser(Integer.toString(i));
			as[i].createUser("f", "l", "***", "User");
		}
		ClientRunner.pause(delay);
		for(int i=0; i<as.length ; i++) {
			as[i].login("***");
		}
		ClientRunner.pause(delay);
		for(int i=0; i<as.length ; i++) {
			for(int i2 = i + 1; i2<as.length; ++i2) {
				as[i].addFriend(Integer.toString(i2));
			}
		}
		ClientRunner.pause(delay);
		for(int i=0; i<as.length ; i++) {
			for(int i2 = 0; i2 < i; ++i2) {
				as[i].acceptFriend(Integer.toString(i2));
			}
		}
		ClientRunner.pause(delay);
		Random r = new Random();
		for(int a=0; a<numPosUpdates; a++) {
			as[r.nextInt(numPeople)].updateCoordinate(Double.toString(r.nextInt()+r.nextDouble()), Double.toString(r.nextInt()+r.nextDouble()));
		}
		ClientRunner.pause(delay);
		ClientRunner.pause(delay);
		for(AndroidUser a : as) 
			a.disconnect();
	}
	
	public void runTests(int delay) {
		this.stressTest(delay, 50, 200);
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
		
		runTests(5000);
		
		if(INTERACTIVE) {
			interactiveMode();
		}
		
		System.out.println("Client runner is down.");
	}
}
