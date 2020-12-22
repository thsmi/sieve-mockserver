/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.sieve;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class FakeServerSocket {

  private ServerSocket server = null;
  private SSLContext sslContext = null;

  /** The port the server is listening */
  private final int port;

  /**
   * Creates an new instance.
   * @param port
   *   the port which should be used for incoming requests.
   */
  public FakeServerSocket(final int port) {
    this.port = port;
  }

  /**
   * The port on which the server is listening.
   * @return
   *   the port number.
   */
  public int getPort() {
    return this.port;
  }  

  /**
   * Create a new socket. In case it is already connected. 
   * The socket will be restarted.
   * 
   * @return
   *   a self reference.
   * @throws IOException
   */
  public FakeServerSocket create() throws IOException {
    this.shutdown();

    this.server = new ServerSocket();
    this.server.setReuseAddress(true);
    this.server.bind(new InetSocketAddress(this.port));

    return this;
  }

  /**
   * Shuts the socket down and closes any incoming connections.
   * @return
   *   a self reference.
   * @throws IOException
   */
  public FakeServerSocket shutdown() throws IOException {
    if (this.server == null)
      return this;

    this.server.close();
    this.server = null;
    return this;
  }


  public FakeServerSocket initTLS(final String keyStore, final String keyStorePassphrase, final String trustStore,
      final String trustStorePassphrase) throws Exception {

    // http://blog.jteam.nl/2009/11/10/securing-connections-with-tls/
    // Preload the cert stuff...

    // Key store for your own private key and signing certificates.
    try (
      InputStream keyStoreIS = new FileInputStream(keyStore);
      InputStream trustStoreIS = new FileInputStream(trustStore);)
    {
      final KeyStore ksKeys = KeyStore.getInstance("PKCS12");
      ksKeys.load(keyStoreIS, keyStorePassphrase.toCharArray());

      // KeyManager decides which key material to use.
      final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ksKeys, keyStorePassphrase.toCharArray());

      // Trust store contains certificates of trusted certificate authorities.
      // We'll need this to do client authentication.
      
      final KeyStore ksTrust = KeyStore.getInstance("JKS");
      ksTrust.load(trustStoreIS, trustStorePassphrase.toCharArray());

      // TrustManager decides which certificate authorities to use.
      final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(ksTrust);

      this.sslContext = SSLContext.getInstance("TLS");
      this.sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    }

    return this;
  }

  /**
   * Accepts incoming connection. This call is blocking.
   * 
   * @return
   *   the incoming connection.
   * @throws Exception
   */
  public FakeClientSocket accept() throws Exception {
    return new FakeClientSocket(server.accept(), this.sslContext);
  }
}
