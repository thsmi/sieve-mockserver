/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.config;

public enum ConfigurationParameter {
  
  SERVER_SIGNATURE_BROKEN, 
  SERVER_SIGNATURE_INLINE, 

  SUPPORT_TLS,

  SIMULATE_FRAGMENTATION,
  SIMULATE_DELAY,
  SIMULATE_CYRUS_STARTTLS_BUG,

  DEFAULT_KEYSTORE,
  DEFAULT_KEYSTORE_PASSPHRASE,
  CUSTOM_KEYSTORE,
  CUSTOM_KEYSTORE_PASSPHRASE,

  DEFAULT_TRUSTSTORE,
  DEFAULT_TRUSTSTORE_PASSPHRASE,
  CUSTOM_TRUSTSTORE,
  CUSTOM_TRUSTSTORE_PASSPHRASE,
  
  NO_RESPAWN,
  SECURE_SASL;
    
  public static ConfigurationParameter getFlagByName(final String name) throws ConfigurationException {
    try {
      return ConfigurationParameter.valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ConfigurationException("Unknown flag " + name);
    }
  }
}