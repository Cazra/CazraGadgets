package cazra.net;

import java.net.*;
import java.io.*;

public class UDPEchoServer {
  
  public int port;
  
  public UDPEchoServer(int port) {
    this.port = port;
  }
  
  public void serve() {
    try {
      DiscreteSocket socket = new DiscreteSocket(port);
      
      System.out.println("UDP Echo Server is running.");
      
      while(true) {
        // wait to receive a message fro ma client.
        String[] addrmsg = socket.receiveAddressedMsg();
        
        // get the return address of the remote client. 
        String rhost = addrmsg[0];
        int rport = Integer.parseInt(addrmsg[1]);
        String msg = addrmsg[2];
        
        // echo the message back to the client.
        socket.sendMsg(InetAddress.getByName(rhost), rport, msg);
      }
    }
    catch(Exception e) {
      // pokemon exception : gotta catchem all
    }
  }
  
  
  
  
  public static void main(String[] args) {
    int port = Integer.parseInt(args[0]);
    
    UDPEchoServer server = new UDPEchoServer(port);
    server.serve();
  }
}
