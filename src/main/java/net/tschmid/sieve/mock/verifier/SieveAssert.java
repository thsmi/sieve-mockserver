package net.tschmid.sieve.mock.verifier;

import net.tschmid.sieve.mock.exceptions.SieveTestException;

public class SieveAssert {

    /**
     * Checks if the expected value starts with the actual value.
     * In case it does not match the expected string an exception will be thrown.
     * 
     * @param actual
     *   the actual value as string
     * @param expected
     *   the expected value as string
     * @throws SieveTestException
     *   an exception in case the string does not meet the expectation.
     *   
     */
    public static void assertTrue(final String actual, final String expected) throws SieveTestException {
	if (actual == null || actual.equals("") )
	    throw new SieveTestException(expected + " expected but got an empty string");
	
	if (actual.startsWith(expected))
	    return;

	throw new SieveTestException(expected + " expected but got " + actual);
    }
}
