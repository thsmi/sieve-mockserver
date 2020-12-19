package net.tschmid.sieve.mock.tests.steps;

import static net.tschmid.sieve.mock.config.ConfigurationParameter.SECURE_SASL;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.SUPPORT_TLS;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

/**
 * Implements the initial welcome message which is send upon connect.
 * 
 * It does a blocking wait for a an incoming connection
 * 
 * It will not advertise any sasl mechanisms if "SECURE_SASL" flag is enabled.
 * This will be then done by the new capability advertisement after the secure
 * handshake.
 */
public class InitStep implements Step {

  private final String INIT_RESPONSE = "" 
      + "\"IMPLEMENTATION\" \"Replay Server\"\r\n" 
      + "\"SASL\" \"%s\"\r\n"
      + "\"SIEVE\" \"fileinto reject envelope vacation imapflags notify subaddress relational comparator-i;ascii-numeric regex\"\r\n"
      + "%s" 
      + "OK\r\n";  

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {

    String sasl = "";

    if (context.getServer().getFlags().isEnabled(SECURE_SASL) && elm.hasAttribute("sasl"))
      sasl = elm.getAttribute("sasl");

    // Wait for an incoming connection.
    context.getServer().accept();

    context.getServer().doReturn(
      INIT_RESPONSE, 
      sasl, 
      (context.getServer().getFlags().isEnabled(SUPPORT_TLS) ? "\"STARTTLS\"\r\n" : ""));
  }
  
}
