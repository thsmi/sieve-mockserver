/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.config;

public interface Configurable {

  public boolean isEnabled(final ConfigurationParameter key);
  public boolean hasFlag(final ConfigurationParameter key);
  public String getFlag(final ConfigurationParameter key);
  public String getFlag(final ConfigurationParameter key, final ConfigurationParameter fallback);
}
