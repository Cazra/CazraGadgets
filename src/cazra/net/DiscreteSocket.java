package cazra.net;

import java.net.*;
import java.io.*;

/** A convenient extension of DataGramSocket which abstracts handling of DatagramPackets. */
public class DiscreteSocket extends DatagramSocket {
  
  /** The maximum packet size for the socket. Default is 1024 (1 KB). */
  public int MAXPACKET = 1024;
  
  /** The character set being used to translate the socket's bytes into text. */
  private String charSet;
  
  
  public DiscreteSocket(String charSet) throws IOException {
    super();
    this.charSet = charSet;
  }
  
  /** Default character set is UTF-8. */
  public DiscreteSocket() throws IOException {
    this("UTF-8");
  }
  
  public DiscreteSocket(int port, String charSet) throws IOException {
    super(port);
    this.charSet = charSet;
  }
  
  public DiscreteSocket(int port) throws IOException {
    this(port, "UTF-8");
  }
  
  
  /** Sends a string message to a remote process. */
  public void sendMsg(InetAddress host, int port, String msg) throws IOException {
    byte[] buf = msg.getBytes(charSet);
    DatagramPacket packet = new DatagramPacket(buf, buf.length, host, port);
    this.send(packet);
  }
  
  
  /** Sends an array of raw bytes to a remote process. */
  public void sendBytes(InetAddress host, int port, byte[] bytes) throws IOException {
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);
    this.send(packet);
  }
  
  /** Waits to receive a string message from a remote process, then returns it. */
  public String receiveMsg() throws IOException {
    byte[] buf = new byte[MAXPACKET]; // default limit: 1024 bytes -> 512 characters.
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    this.receive(packet);
    
    return new String(buf, 0, packet.getLength(), charSet);
  }
  
  /** Waits to receive a number of bytes from a remote process, then returns them. */
  public byte[] receiveBytes(int numBytes) throws IOException {
    byte[] bytes = new byte[numBytes];
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
    this.receive(packet);
    
    return bytes;
  }
  
  /** Waits to receive a string message, then returns a String array containing the sender's host name, port, and the message. */
  public String[] receiveAddressedMsg() throws IOException {
    String[] result = new String[3];
    
    byte[] buf = new byte[MAXPACKET];
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    this.receive(packet);
    
    result[0] = packet.getAddress().getCanonicalHostName();
    result[1] = "" + packet.getPort();
    result[2] = new String(buf, 0, packet.getLength(), charSet);
    
    return result;
  }
  
  
  
  /** Alias for setSoTimeout. */
  public void setTimeout(int millis) throws SocketException {
    setSoTimeout(millis);
  }

  
  /** Alias for getSoTimeout. */
  public int getTimeout() throws SocketException {
    return getSoTimeout();
  }
  
  
  
  /** Main runs a client for a UDP Echo service. */
  public static void main(String[] args) {
    try {
      String host = args[0];
      int port = Integer.parseInt(args[1]);
      
      DiscreteSocket socket = new DiscreteSocket();
      
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
