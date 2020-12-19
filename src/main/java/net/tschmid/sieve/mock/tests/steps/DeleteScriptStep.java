package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

public class DeleteScriptStep implements Step {

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {
    
    String script = "SCRIPT";

    if (elm.hasAttribute("script"))
      script = elm.getAttribute("script");

    boolean fail = false;

    if (elm.hasAttribute("fail"))
      fail = Boolean.valueOf(elm.getAttribute("fail"));

    context.getServer().waitFor("DELETESCRIPT \"" + script + "\"");

    if (fail) {
      context.getServer().doReturn("NO \"Something failed\"\r\n");
      return;
    }

    context.getServer().doReturn("OK\r\n");
  }

}
