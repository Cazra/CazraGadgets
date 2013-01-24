package cazra.net;

import java.net.*;
import java.io.*;
import java.util.HashSet;

/** A convenient wrapper class for multicast UDP Sockets designed to read text. */
public class MCSocket {
  
  /** The wrapped multicast socket. */
  public MulticastSocket socket;
  
  /** A list of groups the socket is currently a member of. */
  public HashSet<InetAddress> groups = new HashSet<InetAddress>();
  
  /** The port of the group the socket is connected to. */
  public int port;
  
  /** The maximum packet size for the socket. Default is 1024 (1 KB). */
  public int MAXPACKET = 1024;
  
  /** The character set being used to translate the socket's bytes into text. */
  public String charset;
  
  
  public MCSocket(int port, String charset, int ttl) throws IOException {
    this.socket = new MulticastSocket(port);
    this.charset = charset;
    this.port = port;
    socket.setTimeToLive(ttl);
  }
  
  /** Default character set is UTF-8. */
  public MCSocket(int port) throws IOException {
    this(port, "UTF-8", 1);
  }
  
  /** Join a multicast group. */
  public void joinGroup(InetAddress mcastAddr) throws IOException {
    socket.joinGroup(mcastAddr);
    groups.add(mcastAddr);
  }
  
  /** Leave a multicast group. */
  public void leaveGroup(InetAddress mcastAddr) throws IOException {
    socket.leaveGroup(mcastAddr);
    groups.remove(mcastAddr);
  }
  
  /** writes a message to the socket. */
  public void println(InetAddress mcastAddr, String msg) throws IOException {
    if(groups.contains(mcastAddr)) {
      byte[] msgbytes = msg.getBytes(charset);
      DatagramPacket packet = new DatagramPacket(msgbytes, msgbytes.length, mcastAddr, port);
      socket.send(packet);
    }
    else {
      throw new IOException("This socket is not joined to group " + mcastAddr);
    }
    
  }
  
  /** Reads a line of text from the socket and returns it. */
  public String readLine() throws IOException {
    byte[] buf = new byte[MAXPACKET];
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    
    socket.receive(packet);
    
    String msg = new String(buf, 0, packet.getLength(), charset);
    return msg;
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
  public void close() throws IOException {
    socket.close();
  }
  
  
  /** returns socket's toString result. */
  public String toString() {
    return socket.toString();
  }
}
