/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.config;

/**
 * Defines a interface for managing configurations.s
 */
public interface Configurable {

  /**
   * Checks if the configuration parameter is enabled.
   * 
   * @param key
   *   the parameter which should be checked.
   * @return
   *   true in case the configuration is enabled otherwise false.
   */
  boolean isEnabled(final ConfigurationParameter key);

  /**
   * Checks if a configuration for the given parameter exists.
   * 
   * @param key
   *   the parameter to be checked.
   * @return
   *   true in case a custom configuration exists otherwise false.
   */  
  boolean hasFlag(final ConfigurationParameter key);

  /**
   * Returns the configuration for the given parameter.
   * 
   * @param key
   *   the parameter for which the configuration should be returned.
   * @return
   *   the configuration as string or null in case no configuration exists.
   */
  String getFlag(final ConfigurationParameter key);

  /**
   * Returns the configuration for the given parameter.
   * 
   * @param key
   *   the parameter for which the configuration should be returned.
   * @param fallback
   *   the fallback to be returned in case the key was not configured
   * @return
   *   the configuration as string or null in case no configuration exits.
   */
  String getFlag(final ConfigurationParameter key, final ConfigurationParameter fallback);
}
