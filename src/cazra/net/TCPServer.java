package cazra.net;

import java.net.*;
import java.io.*;

/** Provides the foundation for creating a basic TCP server. */
public abstract class TCPServer {
  
  /** The port the server runs on localhost. */
  public int port;
  
  public TCPServer(int port) {
    this.port = port;
  }
  
  public void serve() {
    try {
      ServerSocket connectionSocket = new ServerSocket(port);
      
      while(true) {
        Socket dataSocket = connectionSocket.accept();

        TCPSessionThread session = createSession(dataSocket);
        session.start();
      }
    }
    catch(Exception e) {
      // pokemon exception : gotta catchem all
    }
  }
  
  /** Creates a session for a data socket. */
  public abstract TCPSessionThread createSession(Socket dataSocket) throws Exception;
}
