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

public class SieveScramSha256Test implements SieveTestable {
    
    public void doTest(SieveMockServer mock) throws Exception {
	
	mock
	  .doInit("SCRAM-SHA-256")
	  .doStartTLS("SCRAM-SHA-256")
	  .doSaslScramSha256()
	  .doListScript()
	  .sleep(60*1000);	

	System.out.println("SCRAM-SHA-256 Test passed...");
    }

}
