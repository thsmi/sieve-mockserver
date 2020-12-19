package net.tschmid.sieve.mock.http.websocket;

import net.tschmid.sieve.mock.http.exceptions.WebSocketException;

/**
 * An enum representing a websocket frame type.
 * Each type a unique integer value assigned.
 */
public enum OpCode {
  CONTINUATION_FRAME(0),
  TEXT_FRAME(1),
  BINARY_FRAME(2),
  CONNECTION_CLOSE_FRAME(8),
  PING_FRAME(9),
  PONG_FRAME(10);

  /** the opcodes unique value */
  private final int value;

  OpCode(int value) {
    this.value = value;
  }

  /**
   * Returns the opcodes numeric value.
   * 
   * @return
   *   the op codes unique value as defined in the rfc
   */
  public int getValue() {
    return value;
  }

  /**
   * Converts a numeric opcode into an enum.
   * @param opcode
   *   the opcode to be converted.
   * @return
   *   the enum element which corresponds to the opcode
   * @throws WebSocketException
   *   an exception in caee the opcode can not be converted because it is unknown.
   */
  public static OpCode valueOf(int opcode) throws WebSocketException {

    for (OpCode item : OpCode.values()) {
      if (item.getValue() == opcode)
        return item;
    }

    throw new WebSocketException("Unknown OpCode");
  }          
}