package cazra.net;

import java.net.*;
import java.io.*;

/** A wrapper class for DataGramSocket. */
public class DiscreteSocket {
  
  /** The wrapped DatagramSocket. */
  public DatagramSocket socket;
  
  /** The maximum packet size for the socket. Default is 1024 (1 KB). */
  public int MAXPACKET = 1024;
  
  /** The character set being used to translate the socket's bytes into text. */
  private String charSet;
  
  
  public DiscreteSocket(DatagramSocket socket, String charSet) {
    this.socket = socket;
    this.charSet = charSet;
  }
  
  /** Default character set is UTF-8. */
  public DiscreteSocket(DatagramSocket socket) {
    this(socket, "UTF-8");
  }
  
  
  /** Sends a string message to a remote process. */
  public void sendMsg(InetAddress host, int port, String msg) throws IOException {
    byte[] buf = msg.getBytes(charSet);
    DatagramPacket packet = new DatagramPacket(buf, buf.length, host, port);
    
    socket.send(packet);
  }
  
  /** Waits to receive a string message from a remote process, then returns it. */
  public String receiveMsg() throws IOException {
    byte[] buf = new byte[MAXPACKET];
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    socket.receive(packet);
    
    return new String(buf, 0, packet.getLength(), charSet);
  }
  
  /** Waits to receive a string message, then returns a String array containing the sender's host name, port, and the message. */
  public String[] receiveAddressedMsg() throws IOException {
    String[] result = new String[3];
    
    byte[] buf = new byte[MAXPACKET];
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    socket.receive(packet);
    
    result[0] = packet.getAddress().getCanonicalHostName();
    result[1] = "" + packet.getPort();
    result[2] = new String(buf, 0, packet.getLength(), charSet);
    
    return result;
  }
  
  
  /** Alias for setSoTimeout. */
  public void setTimeout(int millis) throws SocketException {
    setSoTimeout(millis);
  }
  
  /** Sets the socket's timeout. */
  public void setSoTimeout(int millis) throws SocketException {
    socket.setSoTimeout(millis);
  }
  
  /** Alias for getSoTimeout. */
  public int getTimeout() throws SocketException {
    return getSoTimeout();
  }
  
  /** Gets the socket's timeout. */
  public int getSoTimeout() throws SocketException {
    return socket.getSoTimeout();
  }
  
  
  /** Closes the socket. */
  public void close() {
    socket.close();
  }
  
  
  /** returns socket's toString result. */
  public String toString() {
    return socket.toString();
  }
  
  
  /** Main runs a client for a UDP Echo service. */
  public static void main(String[] args) {
    try {
      String host = args[0];
      int port = Integer.parseInt(args[1]);
      
      DiscreteSocket socket = new DiscreteSocket(new DatagramSocket());
      
      BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
      
      while(true) {
        
          System.out.print("Enter message: ");
          String msg = stdin.readLine();
        
          socket.sendMsg(InetAddress.getByName(host), port, msg);
          
          String resp = socket.receiveMsg();
          System.out.println("response: " + resp + "\n");
        
      }
    }
    catch (Exception e) {
      // pokemon exception
      e.printStackTrace();
    }
  }
  
}
