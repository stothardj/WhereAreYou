/*
Copyright 2010 Jake Stothard, Brian Garfinkel, Adam Shwert, Hongchen Yu, Yijie Wang, Ryan Rosario, Jiho Kim

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

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

        public String readOneLine()
        {
                if(connected == true)
                {
                        String s = null;
                        try
                        {
                                s = in.readLine();
                                if(s != null)
                                {
                                        return s;
                                }
                                else
                                {
                                        connected = false;
                                        return "";
                                }
                        }
                        catch (IOException e)
                        {
                                connected = false;
                                return "";
                        }
                }
                else
                {
                        return "";
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

        public boolean getConnected()
        {
                return connected;
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
