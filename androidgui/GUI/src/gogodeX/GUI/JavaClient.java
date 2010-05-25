package gogodeX.GUI;

import java.io.*;
import java.net.*;

public class JavaClient{
	public JavaClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.delimeter = "\n";
	}
	
	public void connect() throws UnknownHostException, IOException, SocketTimeoutException {
		socket = new Socket();
		socketAddress = new InetSocketAddress(host, port);
		socket.connect(socketAddress, 5000);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//if(!in.readLine().equals("Connection made!"))
			//System.out.println("Connection not validated!");
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
	private InetSocketAddress socketAddress;
	private PrintWriter out;
	private BufferedReader in;
	private String host;
	private int port;
}
