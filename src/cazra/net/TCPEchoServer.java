package cazra.net;

import java.net.*;
import java.io.*;

public class TCPEchoServer extends TCPServer {
  
  public TCPEchoServer(int port) {
    super(port);
  }
  
  /** Creates an Echo service session. */
  public TCPSessionThread createSession(Socket dataSocket) throws Exception {
    return new TCPEchoSessionThread(this, dataSocket);
  }
  
  /** takes one argument: the port to run the Echo server on. */
  public static void main(String[] args) {
    int port = Integer.parseInt(args[0]);
    
    TCPEchoServer server = new TCPEchoServer(port);
    server.serve();
  }
}