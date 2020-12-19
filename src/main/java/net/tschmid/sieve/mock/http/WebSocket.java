package net.tschmid.sieve.mock.http;

import static net.tschmid.sieve.mock.http.websocket.OpCode.CONNECTION_CLOSE_FRAME;
import static net.tschmid.sieve.mock.http.websocket.OpCode.PONG_FRAME;
import static net.tschmid.sieve.mock.http.websocket.OpCode.TEXT_FRAME;
import static net.tschmid.sieve.mock.http.websocket.OpCode.BINARY_FRAME;
import static net.tschmid.sieve.mock.http.websocket.OpCode.CONTINUATION_FRAME;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import net.tschmid.sieve.mock.http.exceptions.HttpException;
import net.tschmid.sieve.mock.http.exceptions.WebSocketException;
import net.tschmid.sieve.mock.http.exceptions.connection.ConnectionException;
import net.tschmid.sieve.mock.http.exceptions.connection.ConnectionWriteException;
import net.tschmid.sieve.mock.http.websocket.OpCode;
import net.tschmid.sieve.mock.http.websocket.WebSocketMessage;

/**
 * Upgrades the http connection to a websocket connection.
 */
public class WebSocket {

  public final static String HEADER_UPGRADE = "Upgrade";
  public final static String HEADER_CONNECTION = "Connection";
  public final static String HEADER_SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
  public final static String HEADER_SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";

  private final HttpRequest request;
  private final HttpResponse response;

  private boolean upgraded = false;

  /**
   * Create a new websocket, it be ready after calling upgraded.
   * The upgrade needs to be done before sending the response header.
   * 
   * @param request
   *   the incoming http request.
   * @param response
   *   the outgoing http response.
   */
  public WebSocket(final HttpRequest request, final HttpResponse response) {
    this.request = request;
    this.response = response;
  }

  /**
   * Performs the websocket upgrade.
   * @return
   *   a self reference
   * @throws HttpException
   * @throws ConnectionWriteException
   */
  public WebSocket upgrade() throws HttpException, ConnectionWriteException {

    if (!this.request.hasHeader(HEADER_UPGRADE))
      throw new HttpException("500 Upgrade Header Expected");

    if (!this.request.getHeader(HEADER_UPGRADE).equals("websocket"))
      throw new HttpException("500 Incompatible upgrade ");

    if (!this.request.hasHeader(HEADER_SEC_WEBSOCKET_KEY))
      throw new HttpException("500 Sec Key Header Expected");

    String secKey = this.request.getHeader(HEADER_SEC_WEBSOCKET_KEY);

    try {
      MessageDigest crypt = MessageDigest.getInstance("SHA-1");
      crypt.reset();
      crypt.update(secKey.getBytes("UTF-8"));
      crypt.update("258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes("UTF-8"));

      secKey = new String(Base64.getEncoder().encode(crypt.digest()));
    } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
      throw new HttpException("500 Failed to calculate sec key");
    }

    response.setStatus("101 Switch Protocols");
    response.addHeader(HEADER_UPGRADE, "websocket");
    response.addHeader(HEADER_CONNECTION, "Upgrade");
    response.addHeader(HEADER_SEC_WEBSOCKET_ACCEPT, secKey);

    response.sendHeader();

    response.flush();

    // We are now in websocket mode.
    this.upgraded = true;

    return this;
  }

  public WebSocket send(String data) throws WebSocketException, ConnectionWriteException {
    if (!this.upgraded)
      throw new WebSocketException("Socket not upgraded");

    byte[] bytes = data.getBytes();

    byte[] header = new byte[2];
    header[0] = (byte) 0b10000001;

    byte[] length;

    if (bytes.length < 127) {
      header[1] = (byte) (bytes.length & 0b01111111);
      length = new byte[0];
    } else if (bytes.length <= 0xFFFF) {
      header[1] = 126;

      length = new byte[2];
      length[0] = (byte) ((bytes.length >> (1 * 8)) & 0xFF);
      length[1] = (byte) ((bytes.length >> (0 * 8)) & 0xFF);
    } else {
      header[1] = 127;

      length = new byte[8];
      length[0] = (byte) ((bytes.length >> (7 * 8)) & 0xFF);
      length[1] = (byte) ((bytes.length >> (6 * 8)) & 0xFF);
      length[2] = (byte) ((bytes.length >> (5 * 8)) & 0xFF);
      length[3] = (byte) ((bytes.length >> (4 * 8)) & 0xFF);
      length[4] = (byte) ((bytes.length >> (3 * 8)) & 0xFF);
      length[5] = (byte) ((bytes.length >> (2 * 8)) & 0xFF);
      length[6] = (byte) ((bytes.length >> (1 * 8)) & 0xFF);
      length[7] = (byte) ((bytes.length >> (0 * 8)) & 0xFF);
    }

    this.response.sendRaw(header);
    this.response.sendRaw(length);
    this.response.sendRaw(bytes);

    this.response.flush();

    return this;
  }

  protected int read16() throws ConnectionException {
    byte[] bytes = this.request.read(2);
    return (bytes[0] << (1 * 8)) + (bytes[1] << (0 * 8));
  }

  protected int read64() throws ConnectionException {
    byte[] bytes = this.request.read(8);
    return 0 + (bytes[0] << (7 * 8)) + (bytes[1] << (6 * 8)) + (bytes[2] << (5 * 8)) + (bytes[3] << (4 * 8))
        + (bytes[4] << (3 * 8)) + (bytes[5] << (2 * 8)) + (bytes[6] << (1 * 8)) + (bytes[7] << (0 * 8));
  }

  protected int readLength(byte header) throws ConnectionException {
    int length = header & 0b01111111;

    if (length == 126)
      return this.read16();

    if (length == 127)
      return this.read64();

    return length;
  }

  protected byte[] readMaskedData(int length) throws ConnectionException {
    byte[] mask = this.request.read(4);
    byte[] data = this.request.read(length);

    for (int i=0; i<data.length; i++)
      data[i] = (byte)(data[i] ^ (mask[i % 4]));

    return data;
  }

  public WebSocketMessage read() throws WebSocketException, ConnectionException {
    if (!this.upgraded)
      throw new WebSocketException("Socket not upgraded");

    byte[] header = this.request.read(2);

    boolean isFinal = (header[0] & 0b10000000) != 0;
    
    OpCode opcode = OpCode.valueOf((byte)(header[0] & 0b00001111));

    int length = this.readLength((byte)header[1]);

    if (opcode.equals(CONNECTION_CLOSE_FRAME)) {
      throw new WebSocketException("Connection closed by server");
    }

    if (opcode.equals(PONG_FRAME)) {
      throw new WebSocketException("Implement pong.");
      // TODO send a pong and continue reading..
      //return this.read();
    }
        
    if ((header[1] & 0b10000000) == 0)
      throw new WebSocketException("Client to server messages have to be masked");

    if (opcode.equals(TEXT_FRAME) || opcode.equals(BINARY_FRAME) ||opcode.equals(CONTINUATION_FRAME) ) {
      return new WebSocketMessage(opcode, isFinal, this.readMaskedData(length));
    }

    throw new WebSocketException("Invalid opcode.");    
  }
}
