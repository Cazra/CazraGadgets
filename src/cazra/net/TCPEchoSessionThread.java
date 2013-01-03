package cazra.net;

import java.net.*;
import java.io.*;

public class TCPEchoSessionThread extends TCPSessionThread { 
  public int timeout = 60000;
  
  public TCPEchoSessionThread(TCPServer server, Socket clientSocket) throws IOException {
    super(server, clientSocket);
  }
  
  /** Waits for input, then sends the input message back to the sender. */
  public void run() {
    try {
      clientSocket.setTimeout(timeout);
      
      try {
        // loop until it times out.
        while(true) {
          String msg = clientSocket.readLine();
          clientSocket.println(msg);
        }
      }
      catch (SocketTimeoutException ste) {
        clientSocket.println("Session timed out. Connection ended.");
      }
      
      // sever the connection when we timeout.
      clientSocket.close();
    }
    catch(Exception e) { // pokemon exception
    }
  }
}
