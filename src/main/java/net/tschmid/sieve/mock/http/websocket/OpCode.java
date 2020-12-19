package net.tschmid.sieve.mock.http.websocket;

import net.tschmid.sieve.mock.http.exceptions.WebSocketException;

public enum OpCode {
  CONTINUATION_FRAME(0),
  TEXT_FRAME(1),
  BINARY_FRAME(2),
  CONNECTION_CLOSE_FRAME(8),
  PING_FRAME(9),
  PONG_FRAME(10);

  private final int value;

  private OpCode(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public boolean equals(byte opcode) {
    return this.equals((int)opcode);
  }

  public boolean equals(int opcode) {
    return (this.value == opcode);
  }

  public static OpCode valueOf(int opcode) throws WebSocketException {

    for (OpCode item : OpCode.values()) {
      if (item.equals(opcode))
        return item;
    }

    throw new WebSocketException("Unknown OpCode");
  }          
}