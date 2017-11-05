package org.mozdev.sieve.tests;

import org.mozdev.sieve.server.SieveMockServer;

public class SieveUnauthenticatedReferalTest implements SieveTestable{

    @Override
    public void doTest(SieveMockServer mock) throws Exception {
	
	mock
	  .doInit("LOGIN")
	  .doStartTLS("LOGIN")
	  .doSaslLogin()
	  .waitFor("LISTSCRIPTS")
	  .doReferal(8080)
	  .doInit("LOGIN");
	  
	/*ServerSocket refServer = new ServerSocket(8080);

	assertTrue(sieve.readLine(), "LISTSCRIPTS");

	System.out.println("Referring");

	// sieve://localhost:8080
	// sieve://localhost:8080/
	// sieve://localhost:8080/mn
	// sieve://localhost
	// sieve://localhost/
	// sieve://localhost/mn
	sieve.sendPacket("BYE (REFERRAL \"sieve://localhost:8080\") \"Try Remote.\"\r\n");

	sieve.close();

	refServer.accept();

	System.out.println("Referral Test passed");

	refServer.close();*/

    }

}
