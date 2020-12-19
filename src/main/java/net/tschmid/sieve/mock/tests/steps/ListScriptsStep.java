package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

public class ListScriptsStep implements Step {

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {
    String[] scripts = { "SCRIPT" };

    if (elm.hasAttribute("scripts"))
      scripts = elm.getAttribute("scripts").split(";");


    context.getServer().waitFor("LISTSCRIPTS");

    String result = "";
  
    for (String script : scripts) {
      result += "\""+script+"\"\r\n";
    }
  
    result += "OK \"Listscript completed.\"\r\n";
  
    context.getServer().doReturn(result);
  }
  
}
