package cazra.net;

import java.net.*;
import java.io.*;

/** An abstract thread used to handle a TCP server's connection to an individual client. */
public abstract class TCPSessionThread extends Thread {
  public TCPServer server;
  public StreamingSocket clientSocket;
  
  public TCPSessionThread(TCPServer server, Socket clientSocket) throws IOException {
    this.server = server;
    this.clientSocket = new StreamingSocket(clientSocket);
  }
  
  /* Example run implementation: 
  public void run() {
    try {
      clientSocket.setTimeout(MAXWAIT);
      
      try {
        // loop until it times out.
        while(true) {
          String msg = clientSocket.readLine();
          // do stuff.
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
  */
  
  
}