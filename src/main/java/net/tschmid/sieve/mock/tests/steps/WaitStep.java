package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

/**
 * Waits for incoming data.
 * 
 * In case the "for" attribute is specified the incoming data 
 * must match the attributes value
 */
public class WaitStep implements Step {

  /** Optional attribute, which expects the incoming data to match */
  public static final String ATTR_FOR = "for";

  @Override
  public boolean is(final Element elm) {
    return true;
  }

  @Override
  public void execute(final TestContext context, final Element elm) throws Exception {
    String token = "";

    if (elm.hasAttribute(ATTR_FOR))
      token = elm.getAttribute(ATTR_FOR);

    if (token.equals("")) {
      context.getServer().waitForAnything();
      return;
    }

    context.getServer().waitFor(token);
  }
  
}
