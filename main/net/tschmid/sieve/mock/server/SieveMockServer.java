/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.server;

import static net.tschmid.sieve.mock.server.SieveTestFlagsEnum.*;
import static net.tschmid.sieve.mock.verifier.SieveAssert.assertTrue;

import net.tschmid.sieve.mock.exceptions.SieveTestException;

public class SieveMockServer {

    private final String RESPONSE_INIT = "\"IMPLEMENTATION\" \"Replay Server\"\r\n" + "\"SASL\" \"%s\"\r\n"
	    + "\"SIEVE\" \"fileinto reject envelope vacation imapflags notify subaddress relational comparator-i;ascii-numeric regex\"\r\n"
	    + "%s" + "OK\r\n";

    private SieveSocket socket;
    private SieveServerSocket serverSocket;
    
    public final SieveFlags flags = new SieveFlags();

    private int port;

    /**
     * Initializes a mock sever which runs on the sieve default port 4190.
     */
    public SieveMockServer() {
	this(4190);
    }

    /**
     * Initializes a mock server with a custom port
     * 
     * @param port
     *            the port which should be set
     */
    public SieveMockServer(int port) {
	this.port = port;
    }
    
    public SieveFlags getFlags() {
	return this.flags;
    }

 
    /**
     * Waits for the expected line to arrive. In case of a difference an exception
     * will be thrown.
     * 
     * @param expected
     *            the string with the expected response.
     * @return a self reference
     * @throws SieveTestException
     *             a test exception in case the expected line was not received.
     */
    public SieveMockServer waitFor(String expected) throws SieveTestException {
	assertTrue(socket.readLine(), expected);
	return this;
    }

    /**
     * Waits until anything was received. In case the stream's end was reached it
     * will return immediately.
     * 
     * @return a self reference
     */
    public SieveMockServer waitForAnything() {
	socket.readLine();
	return this;
    }

    public SieveMockServer doReturn(String data, Object... args) {

	// Simulate a delayed response
	if (this.getFlags().isEnabled(SIMULATE_DELAY))
	    sleep(2000);

	data = String.format(data, args);

	if (this.getFlags().isEnabled(FRAGMENTED) == false) {
	    socket.sendPacket(data);
	    return this;
	}

	// ok simulate fragmentation...
	String[] lines = data.split("\\r\\n");

	for (String line : lines) {
	    socket.sendPacket(line + "\r\n");
	    sleep(2000);
	}

	return this;
    }

    public SieveMockServer doInit(String sasl) throws Exception {

	SieveServerSocket oldServerSocket = this.serverSocket;
	SieveSocket oldSocket = this.socket;

	this.serverSocket = new SieveServerSocket(this.port);
	if (this.getFlags().isEnabled(SUPPORT_TLS)) {

	    System.out.println("Initializing TLS key and truststore");
	    
	    this.serverSocket
	      .setSSLContext(this.getFlags().getFlag(KEYSTORE), this.getFlags().getFlag(TRUSTSTORE))
	      .initTLS();
	}
	
	System.out.println("Waiting for client...");
	this.socket = serverSocket.accept();

	if (oldSocket != null)
	    oldSocket.close();

	if (oldServerSocket != null)
	    oldServerSocket.close();

	doReturn(RESPONSE_INIT, sasl, (this.getFlags().isEnabled(SUPPORT_TLS) ? "\"STARTTLS\"\r\n" : ""));

	return this;
    }

    public SieveMockServer doListScript() throws Exception {
	waitFor("LISTSCRIPTS");
	doReturn("\"SCRIPT\"\r\n" + "OK \"Listscript completed.\"\r\n");

	return this;
    }

    /**
     * Pauses the thread for the given amount of time.
     * 
     * @param duration
     *            the number of milliseconds to sleep.
     * @return a self reference
     */
    public SieveMockServer sleep(long duration) {

	try {
	    Thread.sleep(duration);
	} catch (InterruptedException e) {
	    System.out.println("Sleep was interrupted");
	    e.printStackTrace();
	}

	return this;
    }

    public SieveMockServer doStartTLS(String sasl) throws Exception {
	if (!this.getFlags().isEnabled(SUPPORT_TLS))
	    return this;

	waitFor("STARTTLS");
	doReturn("OK \"Begin TLS negotiation now.\"\r\n");

	socket.startSSL();

	System.out.println("SSL STARTED...");
	
	// The server should return his capabilities after tls handshake succeeded.
	// But older cyrus servers fail to do so. 
	if (this.getFlags().isEnabled(HAS_CYRUS_STARTTLS_BUG) == false) {

	    doReturn("\"IMPLEMENTATION\" \"Replay Server\"\r\n" + "\"SASL\" \"" + sasl + "\"\r\n"
		    + "\"SIEVE\" \"fileinto reject envelope vacation imapflags notify subaddress relational comparator-i;ascii-numeric regex\"\r\n"
		    + "OK \"TLS negotiation successful.\"\r\n");
	}

	waitFor("CAPABILITY");

	doReturn("\"IMPLEMENTATION\" \"Replay Server\"\r\n" + "\"SASL\" \"" + sasl + "\"\r\n"
		+ "\"SIEVE\" \"fileinto reject envelope vacation imapflags notify subaddress relational comparator-i;ascii-numeric regex\"\r\n"
		+ "OK \"Capability completed.\"\r\n");

	return this;
    }

    public SieveMockServer doSaslLogin() throws Exception {

	waitFor("AUTHENTICATE \"LOGIN\"");
	doReturn("\"Username\"\r\n");

	// The Username...
	waitForAnything();
	doReturn("\"Password\"\r\n");

	// The Password...
	waitForAnything();
	doReturn("OK \"Authentication completed.\"\r\n");

	return this;
    }

    public SieveMockServer doSaslExternal() throws Exception {

	waitFor("AUTHENTICATE \"EXTERNAL\" \"\"");
	doReturn("OK \"Authentication completed.\"\r\n");

	return this;
    }

    public SieveMockServer doSaslCramMd5() throws Exception {

	waitFor("AUTHENTICATE \"CRAM-MD5\"");
	doReturn("\"PDUxMjk5Njc4MzAwNjM0NTcuMTMwOTg0MTM4N0BteC5tZXpvbi5sb2NhbD4=\"\r\n");

	// We expect the username to be user and the password to be pencil
	waitFor("\"dXNlciBhNWEzM2IxZTUwNjZiN2FjYWI4MmQ1ZDQ3N2E3NTk3Nw==\"");
	doReturn("OK \"Authentication completed.\"\r\n");

	return this;
    }

    public SieveMockServer doSaslScramSha1() throws Exception {
	/*
	 * We use test sequence from the RFC 5802
	 *
	 * C: n,,n=user,r=fyko+d2lbbFgONRv9qkxdawL S:
	 * r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,s=QSXCR+Q6sek8bf92,i=4096 C:
	 * c=biws,r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,p=v0X8v3Bz2T0CJGbJQyF0X+
	 * HI4Ts= S: v=rmF9pqV8S7suAoZWja4dJRkFsKQ=
	 *
	 * As SCRAM is secure and this test is dumb we need to tweak/force the client to
	 * use a a predefined nonce
	 * 
	 * nonce = fyko+d2lbbFgONRv9qkxdawL username = user password = pencil
	 * 
	 * C: biwsbj11c2VyLHI9ZnlrbytkMmxiYkZnT05Sdjlxa3hkYXdM S:
	 * cj1meWtvK2QybGJiRmdPTlJ2OXFreGRhd0wzcmZjTkhZSlkxWlZ2V1ZzN2oscz1RU1hDUitRNnNlazhiZjkyLGk9NDA5Ng
	 * == C:
	 * Yz1iaXdzLHI9ZnlrbytkMmxiYkZnT05Sdjlxa3hkYXdMM3JmY05IWUpZMVpWdldWczdqLHA9djBYOHYzQnoyVDBDSkdiSlF5RjBYK0hJNFRzPQ
	 * == S: dj1ybUY5cHFWOFM3c3VBb1pXamE0ZEpSa0ZzS1E9
	 */

	assertTrue(socket.readLine(),
		"AUTHENTICATE \"SCRAM-SHA-1\" \"biwsbj11c2VyLHI9ZnlrbytkMmxiYkZnT05Sdjlxa3hkYXdM\"");

	doReturn(
		"\"cj1meWtvK2QybGJiRmdPTlJ2OXFreGRhd0wzcmZjTkhZSlkxWlZ2V1ZzN2oscz1RU1hDUitRNnNlazhiZjkyLGk9NDA5Ng==\"\r\n");

	assertTrue(socket.readLine(),
		"\"Yz1iaXdzLHI9ZnlrbytkMmxiYkZnT05Sdjlxa3hkYXdMM3JmY05IWUpZMVpWdldWczdqLHA9djBYOHYzQnoyVDBDSkdiSlF5RjBYK0hJNFRzPQ==\"");

	String verifier;

	if (this.getFlags().isEnabled(SERVER_SIGNATURE_BROKEN))
	    verifier = "\"dj1ybUY5cHFWOFM3c3VBb1pXamE0ZEpSa0ZzS0k=\"";
	else
	    verifier = "\"dj1ybUY5cHFWOFM3c3VBb1pXamE0ZEpSa0ZzS1E9\"";

	if (this.getFlags().isEnabled(SERVER_SIGNATURE_INLINE)) {
	    doReturn("OK (SASL " + verifier + ")\r\n");
	    return this;
	}

	doReturn(verifier + "\r\n");

	assertTrue(socket.readLine(), "\"\"");

	doReturn("OK\r\n");

	return this;
    }

    public SieveMockServer doSaslScramSha256() throws Exception {
	/*
	 * We use test sequence from the RFC 5802
	 *
	 * C: n,,n=user,r=fyko+d2lbbFgONRv9qkxdawL S:
	 * r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,s=QSXCR+Q6sek8bf92,i=4096 C:
	 * c=biws,r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,p=v0X8v3Bz2T0CJGbJQyF0X+
	 * HI4Ts= S: v=rmF9pqV8S7suAoZWja4dJRkFsKQ=
	 *
	 * As SCRAM is secure and this test is dumb we need to tweak/force the client to
	 * use a a predefined nonce
	 * 
	 * nonce = fyko+d2lbbFgONRv9qkxdawL username = user password = pencil
	 * 
	 * C: biwsbj11c2VyLHI9ZnlrbytkMmxiYkZnT05Sdjlxa3hkYXdM S:
	 * cj1meWtvK2QybGJiRmdPTlJ2OXFreGRhd0wzcmZjTkhZSlkxWlZ2V1ZzN2oscz1RU1hDUitRNnNlazhiZjkyLGk9NDA5Ng
	 * == C:
	 * Yz1iaXdzLHI9ZnlrbytkMmxiYkZnT05Sdjlxa3hkYXdMM3JmY05IWUpZMVpWdldWczdqLHA9djBYOHYzQnoyVDBDSkdiSlF5RjBYK0hJNFRzPQ
	 * == S: dj1ybUY5cHFWOFM3c3VBb1pXamE0ZEpSa0ZzS1E9
	 */
	
	assertTrue(socket.readLine(),
		"AUTHENTICATE \"SCRAM-SHA-256\" \"biwsbj11c2VyLHI9ck9wck5HZndFYmVSV2diTkVrcU8=\"");

	doReturn(
		"\"cj1yT3ByTkdmd0ViZVJXZ2JORWtxTyVodllEcFdVYTJSYVRDQWZ1eEZJbGopaE5sRiRrMCxzPVcyMlphSjBTTlk3c29Fc1VFamI2Z1E9PSxpPTQwOTY=\"\r\n");

	assertTrue(socket.readLine(),
		"\"Yz1iaXdzLHI9ck9wck5HZndFYmVSV2diTkVrcU8laHZZRHBXVWEyUmFUQ0FmdXhGSWxqKWhObEYkazAscD1kSHpiWmFwV0lrNGpVaE4rVXRlOXl0YWc5empmTUhnc3FtbWl6N0FuZFZRPQ==\"");

	String verifier;

	if (this.getFlags().isEnabled(SERVER_SIGNATURE_BROKEN))
	    verifier = "\"dj1ybUY5cHFWOFM3c3VBb1pXamE0ZEpSa0ZzS0k=\"";
	else
	    verifier = "\"dj02cnJpVFJCaTIzV3BSUi93dHVwK21NaFVaVW4vZEI1bkxUSlJzamw5NUc0PQ==\"";

	if (this.getFlags().isEnabled(SERVER_SIGNATURE_INLINE)) {
	    doReturn("OK (SASL " + verifier + ")\r\n");
	    return this;
	}

	doReturn(verifier + "\r\n");

	assertTrue(socket.readLine(), "\"\"");

	doReturn("OK\r\n");

	return this;
    }    
    
    
    public SieveMockServer doSaslPlain() throws Exception {
	/*
	 * [22:54:02.845 server2] Client -> Server: AUTHENTICATE "PLAIN" "xxxxxxxxx"
	 * [22:54:02.990 server2] Server -> Client OK
	 */

	waitFor("AUTHENTICATE \"PLAIN\" \"");
	doReturn("OK\r\n");

	return this;
    }

    public SieveMockServer doReferal() throws Exception {
	return this.doReferal(4191);
    }

    public SieveMockServer doReferal(int port) throws Exception {

	doReturn("BYE (REFERRAL \"sieve://localhost:" + port + "\") \"Try Remote.\"\r\n");
	this.port = port;

	return this;
    }

    public void close() throws Exception {
	if (this.socket != null) {
	    this.socket.close();
	    this.socket = null;
	}

	if (this.serverSocket != null) {
	    this.serverSocket.close();
	    this.serverSocket = null;
	}
    }

}
