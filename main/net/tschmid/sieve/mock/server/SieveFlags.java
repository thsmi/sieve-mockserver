/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.server;

import java.util.HashMap;
import java.util.Map;

import net.tschmid.sieve.mock.exceptions.SieveConfigurationException;

public class SieveFlags {

    private Map<SieveTestFlagsEnum, String> flags = new HashMap<>();
    
    /**
     * Checks if the flag is enabled. Returns false in case the flag is unset or
     * does not evaluate to true.
     * 
     * @param key
     *            the flag's name
     * @return true in case the flag is enabled otherwise false.
     */
    public boolean isEnabled(SieveTestFlagsEnum key) {
	return Boolean.parseBoolean(this.flags.get(key));
    }
    
    /**
     * Checks if a value was set for the given key.
     * @param key
     *   the key which should be checked
     * @return
     *   true in case a value was set for this flag otherwise false.
     */
    public boolean hasFlag(SieveTestFlagsEnum key) {
	return this.flags.containsKey(key);
    }

    /**
     * Sets the given flag to true.
     * 
     * @param key
     *            the key which should be enabled
     * @return a self reference.
     */
    public SieveFlags setFlag(SieveTestFlagsEnum key) {
	return this.setFlag(key, true);
    }

    /**
     * Sets the given flag to the specified boolean value.
     * 
     * @param key
     *            the key which should be changed
     * @param value
     *            the key's new value
     * @return a self reference
     */
    public SieveFlags setFlag(SieveTestFlagsEnum key, boolean value) {
	return this.setFlag(key, Boolean.toString(value));
    }

    /**
     * Sets the given flag to the specified value
     * 
     * @param key
     *            the key which should be changed
     * @param value
     *            the key's new value.
     * @return a self reference
     */
    public SieveFlags setFlag(SieveTestFlagsEnum key, String value) {
	this.flags.put(key, value);
	return this;
    }
    
    /**
     * Returns the value for the given flag or null
     * in case no value is stored.
     * 
     * @param key
     *   the key for which the value should be returned.
     * @return
     *   the value or null in case no value for the given key exists.
     */
    public String getFlag(SieveTestFlagsEnum key) {
	return this.flags.get(key);
    }

    public SieveFlags parseFlag(String parameter) throws SieveConfigurationException {

	    String[] items = parameter.split("=", 2);
	    
	    SieveTestFlagsEnum key = SieveTestFlagsEnum.getFlagByName(items[0]);
	    
	    if (items.length == 1) {
		this.setFlag(key);
		return this;
	    }
	    
	    this.setFlag(key, items[1]);
	    return this;
    }

}
