import java.io.*;
import java.net.*;

public class JavaClient{
	public JavaClient(String name, String host, int port) {
		QueryReceiver q = new QueryReceiver(this, name);
		q.start();
		this.host = host;
		this.port = port;
		this.delimeter = "\n";
	}
	
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public void disconnect() throws IOException {
		out.close();
		in.close();
		socket.close();
	}
	
	protected void finalize() throws Throwable {
		disconnect();
	}
	
	public void sendLine(String message) {
		out.print(message + delimeter);
		out.flush();
	}
	
	public String readLine() throws IOException {
		return in.readLine();
	}
	
	//public:
	public String delimeter;
	
	//private:
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private String host;
	private int port;
}
