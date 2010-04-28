/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.*;
import java.net.*;

public class KnockKnockClient {
  public static void main(String[] args) throws IOException {

    Socket kkSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    String host = "localhost";
    int port = 79;

    /*
     * This MUST match up with the delimiter used by the twisted
     * server. Never use System.out.println as it chooses it's
     * own delimiter for the newline
     */
    String delimiter = "\n";
    String nl = delimiter; //for shorthand

    System.out.print("Started client."+nl);
    try {
        kkSocket = new Socket("localhost", port);
        out = new PrintWriter(kkSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
    } catch (UnknownHostException e) {
        System.err.print("Don't know about host: localhost."+nl);
        System.exit(1);
    } catch (IOException e) {
        System.err.print("Couldn't get I/O for the connection to: localhost."+nl);
        System.exit(1);
    }

    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    String fromServer;
    String fromUser;

    System.out.print("Waiting for server input."+nl);
    while ((fromServer = in.readLine()) != null) {
      System.out.print("Server: " + fromServer+nl);
      if (fromServer.equals("Bye."))
        break;

      fromUser = stdIn.readLine();
      if (fromUser != null) {
        System.out.print("Client: " + fromUser+nl);
        out.print(fromUser+nl);
        out.flush();
      }
      System.out.print("Waiting for server reply."+nl);
    }

    System.out.print("Closing connections."+nl);

    out.close();
    in.close();
    stdIn.close();
    kkSocket.close();
  }
}
