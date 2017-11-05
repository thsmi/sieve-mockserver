/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package org.mozdev.sieve.tests;

import org.mozdev.sieve.server.SieveMockServer;

public class SievePlainTest implements SieveTestable {

    @Override
    public void doTest(SieveMockServer mock) throws Exception {
	
	// start the test
	mock
	  .doInit("PLAIN")
	  .doStartTLS("PLAIN")
	  .doSaslPlain()
	  .doListScript();
	
	System.out.println("SASL Plain Test passed...");
	 
	
    }

}