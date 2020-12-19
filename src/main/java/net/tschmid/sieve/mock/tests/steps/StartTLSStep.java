package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import static net.tschmid.sieve.mock.config.ConfigurationParameter.SIMULATE_CYRUS_STARTTLS_BUG;
import static net.tschmid.sieve.mock.config.ConfigurationParameter.SUPPORT_TLS;

import net.tschmid.sieve.mock.tests.TestContext;

import net.tschmid.sieve.mock.exceptions.SieveTestException;

/**
 * Implements a StartTLS handshake.
 * 
 * This means waiting for the "STARTTLS" message, then upgrading
 * the socket to be secure and then returning the initial message.
 * 
 * TLS needs to be enabled by setting the SUPPORT_TLS flag.
 * 
 * It is possible to simulate the starttls bug by enabling the
 * SIMULATE_CYRUS_STARTTLS_BUG flag.
 */
public class StartTLSStep implements Step {

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {
    String sasl = "";

    if (elm.hasAttribute("sasl"))
      sasl = elm.getAttribute("sasl");
  
    if (!context.getFlags().isEnabled(SUPPORT_TLS))
      throw new SieveTestException("TLS Not supported");

    if (context.getServer().isSecure())
      throw new SieveTestException("Socket already upgraded to a secure socket");

    context.getServer()
      .waitFor("STARTTLS")
      .doReturn("OK \"Begin TLS negotiation now.\"\r\n");

    context.getServer()
      .log("Upgrading to Secure...")
      .startSSL()
      .log("Socket upgraded");

    if (!context.getFlags().hasFlag(SIMULATE_CYRUS_STARTTLS_BUG)) {
      context.getServer().doReturn(""
        + "\"IMPLEMENTATION\" \"Replay Server\"\r\n" 
        + "\"SASL\" \"" + sasl + "\"\r\n"
        + "\"SIEVE\" \"fileinto reject envelope vacation imapflags notify subaddress relational comparator-i;ascii-numeric regex\"\r\n"
        + "OK \"TLS negotiation successful.\"\r\n");
    }

    ((CapabilityStep)context.getStep("capability")).execute(context, sasl);
  }  
}
