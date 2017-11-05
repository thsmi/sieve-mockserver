/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package org.mozdev.sieve.server;

import org.mozdev.sieve.exceptions.SieveConfigurationException;

public enum SieveTestFlagsEnum {
    SERVER_SIGNATURE_BROKEN, 
    SERVER_SIGNATURE_INLINE, 
    HAS_CYRUS_STARTTLS_BUG,
    SUPPORT_TLS,
    FRAGMENTED,
    SIMULATE_DELAY,
    KEYSTORE,
    TRUSTSTORE;
    
    public static SieveTestFlagsEnum getFlagByName(String name) throws SieveConfigurationException {
	try {
	    return SieveTestFlagsEnum.valueOf(name.toUpperCase());
	} catch (IllegalArgumentException e) {
	    throw new SieveConfigurationException("Unknown flag " + name);
	}
    }
}