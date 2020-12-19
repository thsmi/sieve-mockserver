/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration implements Configurable {

  private Map<ConfigurationParameter, String> flags = new ConcurrentHashMap<>();

  /**
   * Checks if the flag is enabled. Returns false in case the flag is unset or
   * does not evaluate to true.
   * 
   * @param key the flag's name
   * @return true in case the flag is enabled otherwise false.
   */
  public boolean isEnabled(final ConfigurationParameter key) {
    return Boolean.parseBoolean(this.flags.get(key));
  }

  /**
   * Checks if a value was set for the given key.
   * 
   * @param key the key which should be checked
   * @return true in case a value was set for this flag otherwise false.
   */
  public boolean hasFlag(final ConfigurationParameter key) {
    return this.flags.containsKey(key);
  }

  /**
   * Sets the given flag to true.
   * 
   * @param key the key which should be enabled
   * @return a self reference.
   */
  public Configuration setFlag(final ConfigurationParameter key) {
    return this.setFlag(key, true);
  }

  /**
   * Sets the given flag to the specified boolean value.
   * 
   * @param key   the key which should be changed
   * @param value the key's new value
   * @return a self reference
   */
  public Configuration setFlag(final ConfigurationParameter key, final boolean value) {
    return this.setFlag(key, Boolean.toString(value));
  }

  /**
   * Sets the given flag to the specified value
   * 
   * @param key   the key which should be changed
   * @param value the key's new value.
   * @return a self reference
   */
  public Configuration setFlag(final ConfigurationParameter key, final String value) {
    this.flags.put(key, value);
    return this;
  }

  /**
   * Returns the value for the given flag or null in case no value is stored.
   * 
   * @param key the key for which the value should be returned.
   * @return the value or null in case no value for the given key exists.
   */
  public String getFlag(final ConfigurationParameter key) {
    return this.flags.get(key);
  }

  public String getFlag(final ConfigurationParameter key, final ConfigurationParameter fallback) {
    if (this.hasFlag(key))
      return this.getFlag(key);

    return this.getFlag(fallback);
  }

  public Configuration setFlag(final String key, final String value) throws ConfigurationException {

    this.setFlag(ConfigurationParameter.getFlagByName(key), value);
    return this;
  }

  public Configuration parseFlag(final String parameter) throws ConfigurationException {

    String[] items = parameter.split("=", 2);

    ConfigurationParameter key = ConfigurationParameter.getFlagByName(items[0]);

    if (items.length == 1) {
      this.setFlag(key);
      return this;
    }

    this.setFlag(key, items[1]);
    return this;
  }

  public String toJson() {
    String result = "";

    for ( Entry<ConfigurationParameter, String> item : this.flags.entrySet()){
      if (!result.equals(""))
        result += ",\r\n";
      result += "  \""+item.getKey()+"\" : \""+item.getValue()+"\"";
    }

    return "{\r\n"
      + result
      + "\r\n}";
  }

}
