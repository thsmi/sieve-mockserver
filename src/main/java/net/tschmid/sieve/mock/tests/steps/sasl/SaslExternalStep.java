package net.tschmid.sieve.mock.tests.steps.sasl;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;
import net.tschmid.sieve.mock.tests.steps.Step;

/**
 * Implements a test step for the SASL EXTERNAL mechanism.
 */
public class SaslExternalStep implements Step {

  @Override
  public boolean is(final Element elm) {
    return true;
  }

  @Override
  public void execute(final TestContext context, final Element elm) throws Exception {
    String authorization = "";

    if (elm.hasAttribute("authorization"))
      authorization = elm.getAttribute("authorization");

    context.getServer()
      .waitFor("AUTHENTICATE \"EXTERNAL\" \""+authorization+"\"")
      .doReturn("OK \"Authentication completed.\"\r\n");
  }
  
}
