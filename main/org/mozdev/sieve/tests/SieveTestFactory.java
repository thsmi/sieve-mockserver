/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package org.mozdev.sieve.tests;

public class SieveTestFactory {

    private static enum Tests {
	ANONYMOUS, FRAGMENTATION, REFERRAL, REFERRAL2, CRAMMD5, LOGIN, SCRAMSHA1, EXTERNAL, UNKNOWN;

	public static Tests getByName(String name) {
	    try {
		return Tests.valueOf(name.toUpperCase());
	    } catch (IllegalArgumentException e) {
		return Tests.UNKNOWN;
	    }
	}
    }

    public SieveTestable getByName(String name) {

	Tests test = Tests.getByName(name);

	switch (test) {
	case ANONYMOUS:
	    return new SieveAnonymousTest();
	case FRAGMENTATION:
	    return new SievePlainTest();
	case REFERRAL:
	    return new SieveUnauthenticatedReferalTest();
	case REFERRAL2:
	    return new SieveAuthenticatedReferalTest();
	case CRAMMD5:
	    return new SieveCramMd5Test();
	case SCRAMSHA1:
	    return new SieveScramSha1Test();
	case LOGIN:
	    return new SieveLoginTest();
	case EXTERNAL:
	    return new SieveExternalTest();
	case UNKNOWN:
	default:
	    return new SieveNullTest(name);
	}

    }

}
