/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */
package net.tschmid.sieve.mock.exceptions;

public class SieveConfigurationException extends Exception {
    
    private static final long serialVersionUID = 3394506758273129222L;

    public SieveConfigurationException(String message) {
	super(message);
    }
}
