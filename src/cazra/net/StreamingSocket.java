package cazra.net;

import java.net.*;
import java.io.*;

/** A convenient wrapper class for stream (TCP) Sockets designed to read text. */
public class StreamingSocket {
  
  /** The wrapped streaming socket. */
  public Socket socket;
  
  /** Used to write text to socket. */
  private PrintWriter writer;
  
  /** Used to read text from socket. */
  private BufferedReader reader;
  
  /** The character set being used to translate the socket's bytes into text. */
  private String charSet;
  
  
  public StreamingSocket(Socket socket, String charset) throws IOException {
    this.socket = socket;
    this.charSet = charset;
    
    getIO();
  }
  
  /** Default character set is UTF-8. */
  public StreamingSocket(Socket socket) throws IOException {
    this(socket, "UTF-8");
  }
  
  
  /** Obtains streams for read and write operations on the socket. */
  private void getIO() throws IOException {
    // get the reader and writer.
    InputStream is = socket.getInputStream();
    reader = new BufferedReader(new InputStreamReader(is, charSet));
    
    OutputStream os = socket.getOutputStream();
    writer = new PrintWriter(new OutputStreamWriter(os, charSet));
  }
  
  /** writes a message to the socket. */
  public void println(String msg) {
    writer.println(msg);
    writer.flush();
  }
  
  /** Reads a line of text from the socket and returns it. */
  public String readLine() throws Exception {
    String msg = reader.readLine();
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