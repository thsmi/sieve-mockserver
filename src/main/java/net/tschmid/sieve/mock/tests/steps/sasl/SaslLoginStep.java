package net.tschmid.sieve.mock.tests.steps.sasl;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;
import net.tschmid.sieve.mock.tests.steps.Step;

/**
 * Implements a test step for the SASL LOGIN mechanism.
 */
public class SaslLoginStep implements Step {

  @Override
  public boolean is(final Element elm) {
    return true;
  }

  @Override
  public void execute(final TestContext context, final Element elm) throws Exception {
    context.getServer()
      .waitFor("AUTHENTICATE \"LOGIN\"")
      .doReturn("\"Username\"\r\n");

    // The Username...
    context.getServer()
      .waitForAnything()
      .doReturn("\"Password\"\r\n");

    // The Password...
    context.getServer()
      .waitForAnything()
      .doReturn("OK \"Authentication completed.\"\r\n");    
  }
  
}
