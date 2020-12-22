package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

/**
 * Implements a log step inside the server context.
 */
public class LogStep implements Step {

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {     
    context.getServer().log(elm.getTextContent().trim());
  }
  
}
