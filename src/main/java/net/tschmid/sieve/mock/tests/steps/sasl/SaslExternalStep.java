package net.tschmid.sieve.mock.tests.steps.sasl;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;
import net.tschmid.sieve.mock.tests.steps.Step;

public class SaslExternalStep implements Step {

  /**
   * Creates a new instance
   */
  public SaslExternalStep() {    
  }

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {
    String authorization = "";

    if (elm.hasAttribute("authorization"))
      authorization = elm.getAttribute("authorization");

    context.getServer()
      .waitFor("AUTHENTICATE \"EXTERNAL\" \""+authorization+"\"")
      .doReturn("OK \"Authentication completed.\"\r\n");
  }
  
}
