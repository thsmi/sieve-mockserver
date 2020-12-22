package net.tschmid.sieve.mock.http.websocket;

public class WebSocketMessage {

  private final OpCode opcode;
  private final byte[] data;
  private final boolean isFinal;

  public WebSocketMessage(final OpCode opcode, final boolean isFinal, final byte[] data) {
    this.opcode = opcode;
    this.data = data;
    this.isFinal = isFinal;
  }

  public boolean isFinal() {
    return this.isFinal;
  }

  public OpCode getOpcode() {
    return this.opcode;
  }

  public byte[] getData() {
    return this.data;
  }

}
