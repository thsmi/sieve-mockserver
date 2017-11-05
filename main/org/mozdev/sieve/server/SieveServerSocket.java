/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package org.mozdev.sieve.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SieveServerSocket {
    final static char[] keyStorePassphrase = "secret".toCharArray();
    final static char[] trustStorePassphrase = "secret".toCharArray();

    private ServerSocket server = null;
    private SSLContext sslContext = null;

    private String keystore = ".\\keystore.p12";
    private String truststore = ".\\truststore.jks";

    public SieveServerSocket(int port) throws IOException {
	// create the server...
	this.server = new ServerSocket(port);
    }

    public SieveServerSocket setSSLContext(String keystore, String truststore) {
	if (keystore == null)
	    keystore = ".\\keystore.p12";
	
	this.keystore = keystore;
	   

	if (truststore == null)
	    truststore = ".\\truststore.jks";
	
	this.truststore = truststore;
	
	return this;
    }

    public SieveServerSocket initTLS() throws Exception {
	// http://blog.jteam.nl/2009/11/10/securing-connections-with-tls/
	// Preload the cert stuff...

	// Key store for your own private key and signing certificates.
	InputStream keyStoreIS = new FileInputStream(this.keystore);

	KeyStore ksKeys = KeyStore.getInstance("PKCS12");
	ksKeys.load(keyStoreIS, keyStorePassphrase);

	// KeyManager decides which key material to use.
	KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	kmf.init(ksKeys, keyStorePassphrase);

	// Trust store contains certificates of trusted certificate authorities.
	// We'll need this to do client authentication.
	InputStream trustStoreIS = new FileInputStream(this.truststore);

	KeyStore ksTrust = KeyStore.getInstance("JKS");
	ksTrust.load(trustStoreIS, trustStorePassphrase);

	// TrustManager decides which certificate authorities to use.
	TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
	tmf.init(ksTrust);

	this.sslContext = SSLContext.getInstance("TLS");
	this.sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

	return this;
    }

    public SieveSocket accept() throws Exception {
	return new SieveSocket(server.accept(), this.sslContext);
    }

    public void close() throws IOException {
	this.server.close();
    }

}
