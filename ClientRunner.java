import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;


public class ClientRunner implements Runnable {
	
	public static void main(String args[]) {
		Thread t = new Thread(new ClientRunner());
		t.start();
	}
	
	@Override
	public void run() {
		System.out.println("Client runner is up.");
		jc = new JavaClient("localhost", 79);
		try {
			jc.connect();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TestStructure ts = new TestStructure();
		
		ts.addTest("jake", "Hooray me!");
		ts.addTest("alex", "I will kill you");
		ts.addTest("no one", "User not found.");
		ts.addTest("Failure", "Gibberish");
		
		ts.runTests();
		
		try {
			jc.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class TestStructure {
		
		private class Test {
			public Test(String input, String expected) {
				this.input = input;
				this.expected = expected;
			}
			
			public void runTest() {
				jc.sendLine(input);
				try {
					String ret = jc.readLine(); 
					if(!ret.equals(expected)) {
						System.err.println(input+" should return "+expected);
						System.err.println("Instead returns "+ret);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			private String input, expected;
		}
		
		public TestStructure() {
			tests = new LinkedList<Test>();
		}
		
		public void addTest(String input, String expected) {
			tests.add(new Test(input, expected));
		}
		
		public void clearTests() {
			tests.clear();
		}
		
		public void runTests() {
			Iterator<Test> it = tests.iterator();
			while(it.hasNext()) {
				Test ct = it.next();
				ct.runTest();
			}
		}
		
		private LinkedList<Test> tests;
	}
	
	private JavaClient jc;
}
