package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import static net.tschmid.sieve.mock.config.ConfigurationParameter.*;

import net.tschmid.sieve.mock.tests.TestContext;

public class CapabilityStep implements Step {

  private final String INIT_RESPONSE = "" 
      + "\"IMPLEMENTATION\" \"Replay Server\"\r\n" 
      + "\"SASL\" \"%s\"\r\n"
      + "\"SIEVE\" \"fileinto reject envelope vacation imapflags notify subaddress relational comparator-i;ascii-numeric regex\"\r\n"
      + "%s"
      + "OK \"Capability completed.\"\r\n";  

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {

    String sasl = "";

    if (elm.hasAttribute("sasl"))
      sasl = elm.getAttribute("sasl");

    this.execute(context, sasl);
  }

  public void execute(TestContext context, String sasl) throws Exception {
    String tls = "";

    if (!context.getServer().isSecure() &&  (context.getFlags().isEnabled(SUPPORT_TLS)))
      tls = "\"STARTTLS\"\r\n";

    context.getServer()
      .log("Waiting for Capabilities")
      .waitFor("CAPABILITY")
      .doReturn(INIT_RESPONSE, sasl, tls);
  }
  
}
