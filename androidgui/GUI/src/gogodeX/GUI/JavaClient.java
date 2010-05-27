package gogodeX.GUI;

import java.io.*;
import java.net.*;
import java.lang.Math;
import java.math.*;

public class JavaClient{
	public JavaClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.delimeter = "\n";
		this.connected = false;
		this.time = 5000;
	}
	
	public void connect() throws UnknownHostException, IOException, SocketTimeoutException {
		if(connected != true)
		{
			socket = new Socket();
			socket.setSoTimeout(5000);
			socketAddress = new InetSocketAddress(host, port);
			socket.connect(socketAddress, 5000);
			connected = true;
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
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
		if(connected == true)
		{
			out.print(message + delimeter);
			out.flush();
		}
	}
	
	public String readLine() {
		if(connected == true)
		{
			try 
			{
				String temp = in.readLine();
				if(temp == null)
				{
					connected = false;
					reconnect();
					return "";
				}
				return temp;
			} 
			catch(SocketTimeoutException e)
			{
				return "";
			}
			catch (IOException e) 
			{
				connected = false;
				try 
				{
					this.connect();
				} 
				catch (SocketTimeoutException e1) 
				{
					connected = false;
					e1.printStackTrace();
				} 
				catch (UnknownHostException e1) 
				{
					connected = false;
					e1.printStackTrace();
				} 
				catch (IOException e1) 
				{
					connected = false;
					e1.printStackTrace();
				}
				
				if(connected == false)
				{
					this.reconnect();
				}
				e.printStackTrace();
				return "";
			}
		}
		else
		{
			return "";
		}
	}
	
	public void reconnect()
	{
		while(connected != true)
		{
			try 
			{
				Thread.sleep((long) time);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			try {
				this.connect();
			} 
			catch (SocketTimeoutException e) 
			{
				connected = false;
				e.printStackTrace();
			} 
			catch (UnknownHostException e) 
			{
				connected = false;
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				connected = false;
				e.printStackTrace();
			}
			if(connected == false)
			{
				time = Math.pow(time, 1.05);
			}
			else
			{
				break;
			}				
		}		
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
	private boolean connected;
	private double time;
}
