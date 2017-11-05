/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.tests;

import net.tschmid.sieve.mock.server.SieveMockServer;

// test a referral after a successful authentication.
public class SieveAuthenticatedReferalTest implements SieveTestable {

    @Override
    public void doTest(SieveMockServer mock) throws Exception {
	/*
	 * [22:54:02.842 server2] Server -> Client "IMPLEMENTATION"
	 * "Cyrus timsieved (Murder) v2.4.14-univie-1.1" "SASL" "PLAIN" "SIVE"
	 * "comparator-i;ascii-numeric fileinto reject vacation imapflags notify envelope relational regex subaddress copy"
	 * "STARTTLS" "UNAUTHENTICATE" OK
	 */
	/*
	 * [22:54:02.991 server2] Client -> Server: LISTSCRIPTS
	 * [22:54:03.012 server2] Server -> Client BYE (REFERRAL "sieve://lyle.univie.ac.at") "Try Remote."
	 */	
	/*
	 * > [22:54:03.012 server2] Disconnected ... > [22:54:03.012 server2] Referred
	 * to Server: lyle.univie.ac.at > [22:54:03.013 server2] Connecting to
	 * lyle.univie.ac.at:4190 ... > [22:54:03.013 server2] Using Proxy: Direct >
	 * [22:54:03.013 server2] Stop request received ... > [22:54:03.060 server2]
	 * Connected to lyle.univie.ac.at:4190 ...
	 */

	
	mock
	  .doInit("PLAIN")
	  .doSaslPlain()
	  .waitFor("LISTSCRIPTS")
	  .doReferal(2001);
	
	mock
	  .doInit("Plain")
	  .doSaslPlain()
	  .doListScript();

	/*
	 * [22:54:03.061 server2] Server -> Client > "IMPLEMENTATION"
	 * "Cyrus timsieved (Murder) v2.4.14-univie-1.1" > "SASL" "PLAIN" > "SIEVE"
	 * "comparator-i;ascii-numeric fileinto reject vacation imapflags > notify
	 * envelope relational regex subaddress copy" > "STARTTLS" > "UNAUTHENTICATE" >
	 * OK
	 * > [22:54:03.063 server2] Client -> Server: > AUTHENTICATE "PLAIN"
	 * "xxxxxxxxxxx"
	 * [22:54:03.198 server2] Server -> Client OK
	 */
	System.out.println("Referral2 Test passed");

	/*
	 * > [22:54:03.198 server2] Invoking Listeners for onChannelCreated >
	 * SivFilerExplorer.sivSendRequest: > [22:54:03.199 server2] Client -> Server: >
	 * LOGOUT > [22:54:03.330 server2] Server -> Client > OK "Logout Complete" >
	 * [22:54:03.330 server2] Disconnected ... > [22:54:03.330 server2] Stop request
	 * received ...
	 */

    }
}
