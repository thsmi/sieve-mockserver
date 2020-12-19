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

  private final Map<ConfigurationParameter, String> flags = new ConcurrentHashMap<>();

  @Override
  public boolean isEnabled(final ConfigurationParameter key) {
    return Boolean.parseBoolean(this.flags.get(key));
  }

  @Override
  public boolean hasFlag(final ConfigurationParameter key) {
    return this.flags.containsKey(key);
  }

  @Override
  public String getFlag(final ConfigurationParameter key) {
    return this.flags.get(key);
  }

  @Override
  public String getFlag(final ConfigurationParameter key, final ConfigurationParameter fallback) {
    if (this.hasFlag(key))
      return this.getFlag(key);

    return this.getFlag(fallback);
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

  /**
   * Converts the current configuration into a json object.
   * It is a poor mans implementation.
   * 
   * @return
   *   the json string.
   */
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
