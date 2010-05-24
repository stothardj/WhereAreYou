import java.io.*;
import java.net.*;

public class JavaClient{
	public JavaClient(String name, String host, int port) {
		q = new QueryReceiver(this, name);
		this.host = host;
		this.port = port;
		this.delimeter = "\n";
	}
	
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		connected = true;
		q.start();
	}
	
	public void disconnect() throws IOException {
		connected = false;
		out.close();
		in.close();
		socket.close();
	}
	
	protected void finalize() throws Throwable {
		try {
			disconnect();
		} finally {
			super.finalize();
		}
	}
	
	public void sendLine(String message) {
		out.print(message + delimeter);
		out.flush();
	}
	
	public String readLine() throws IOException {
		return in.readLine();
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	//public:
	public String delimeter;
	
	//private:
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private String host;
	private int port;
	private QueryReceiver q;
	private boolean connected;
}
