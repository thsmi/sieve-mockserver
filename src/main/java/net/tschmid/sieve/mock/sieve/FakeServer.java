/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.sieve;

import static net.tschmid.sieve.mock.config.ConfigurationParameter.SIMULATE_DELAY;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.SIMULATE_FRAGMENTATION;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.SUPPORT_TLS;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.CUSTOM_KEYSTORE;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.DEFAULT_KEYSTORE;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.CUSTOM_KEYSTORE_PASSPHRASE;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.DEFAULT_KEYSTORE_PASSPHRASE;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.CUSTOM_TRUSTSTORE;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.DEFAULT_TRUSTSTORE;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.CUSTOM_TRUSTSTORE_PASSPHRASE;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.DEFAULT_TRUSTSTORE_PASSPHRASE;

import static net.tschmid.sieve.mock.verifier.SieveAssert.assertTrue;

import java.io.IOException;

import net.tschmid.sieve.mock.config.Configurable;
import net.tschmid.sieve.mock.log.ActivityLog;

public class FakeServer {

  private FakeClientSocket socket;
  private final FakeServerSocket serverSocket;

  private final Configurable flags;

  /**
   * Initializes a mock sever which runs on the sieve default port 4190.
   * 
   * @throws IOException
   */
  public FakeServer(final Configurable flags) throws IOException {
    this(4190, flags);
  }

  /**
   * Initializes a mock server with a custom port
   * 
   * @param port the port which should be set
   * @throws IOException
   */
  public FakeServer(final int port, final Configurable flags) throws IOException {
    this.serverSocket = new FakeServerSocket(port).create();
    this.flags = flags;
  }

  /**
   * Returns the server listening port.
   * 
   * @return
   *   the port on which the server listens to incoming request.
   */
  public int getPort() {
    return this.serverSocket.getPort();
  }

  public Configurable getFlags() {
    return this.flags;
  }

  public FakeServer startSSL() throws IOException {
    socket.startSSL();
    return this;
  }

  /**
   * Checks if the a secure connection is used
   * 
   * @return
   *   true in case the connection is secure otherwise false.
   */
  public boolean isSecure() {
    return socket.isSecure();
  }

  /**
   * Waits for the expected line to arrive. 
   * 
   * In case the received string differs an exception will be thrown.
   * @param expected
   *   the string which should be used for the comparison.
   *   It is a formatted string which allows placeholders.
   * @param args
   *   arguments which should replace placeholders.
   * @return a self reference
   * @throws Exception
   */
  public FakeServer waitFor(final String expected, final Object... args) throws Exception {
    assertTrue(socket.readLine(), String.format(expected, args));
    return this;
  }

  /**
   * Waits until anything was received. In case the stream's end was reached it
   * will return immediately.
   * 
   * @return a self reference
   * @throws Exception
   */
  public FakeServer waitForAnything() throws Exception {
    socket.readLine();
    return this;
  }

  /**
   * Returns a message to the client.
   * 
   * @param data
   *   the message to be returned. 
   *   It is a formatted string which allows placeholders.
   * @param args
   *   arguments which should replace the place holders.
   * @return
   *    a self reference
   */
  public FakeServer doReturn(String data, final Object... args) {

    // Simulate a delayed response
    if (this.getFlags().isEnabled(SIMULATE_DELAY))
      sleep(2000);

    data = String.format(data, args);

    if (this.getFlags().isEnabled(SIMULATE_FRAGMENTATION)) {
      socket.sendPacket(data);
      return this;
    }

    // ok simulate fragmentation...
    final String[] lines = data.split("\\r\\n");

    for (final String line : lines) {
      socket.sendPacket(line + "\r\n");
      sleep(2000);
    }

    return this;
  }

  public FakeServer accept() throws Exception {
    if (this.socket != null)
      this.socket.close();

    if (this.getFlags().isEnabled(SUPPORT_TLS)) {
      this.log("Initializing TLS key and truststore");
      this.serverSocket.initTLS(this.getFlags().getFlag(CUSTOM_KEYSTORE, DEFAULT_KEYSTORE),
          this.getFlags().getFlag(CUSTOM_KEYSTORE_PASSPHRASE, DEFAULT_KEYSTORE_PASSPHRASE),
          this.getFlags().getFlag(CUSTOM_TRUSTSTORE, DEFAULT_TRUSTSTORE),
          this.getFlags().getFlag(CUSTOM_TRUSTSTORE_PASSPHRASE, DEFAULT_TRUSTSTORE_PASSPHRASE));
    }
    this.log("Waiting for client on port " + this.serverSocket.getPort() + "...");
    this.socket = serverSocket.accept();

    this.log("... Client connected to port " + this.serverSocket.getPort() + ".");

    return this;
  }

  /**
   * Pauses the thread for the given amount of time.
   * 
   * @param duration
   *            the number of milliseconds to sleep.
   * @return a self reference
   */
  public FakeServer sleep(final long duration) {

    try {
      Thread.currentThread().sleep(duration);
    } catch (InterruptedException e) {
      this.log("Sleep was interrupted");
      e.printStackTrace();
    }

    return this;
  }

  public String readLine() throws Exception {
    return socket.readLine();
  }


  /**
   * Shuts down the sever including all child sockets.
   * @throws Exception
   */
  public void shutdown() throws Exception {

    this.close();
    this.serverSocket.shutdown();
  }

  /**
   * Closes and frees the child socket.
   * @throws Exception
   */
  public void close() throws Exception {

    if (this.socket == null)
      return;
       
    this.socket.close();
    this.socket = null;
  }

  /**
   * Logs the given message into the activity log.
   * It will be prefixed with the server ip and port.
   * @param msg
   *   the message to be logged
   * @return
   *   a self reference.
   */
  public FakeServer log(final String msg) {
    ActivityLog.getInstance().log(""
      + "[127.0.0.1:"+this.getPort() +"]"
      + " " + msg);

    return this;
  }


}
