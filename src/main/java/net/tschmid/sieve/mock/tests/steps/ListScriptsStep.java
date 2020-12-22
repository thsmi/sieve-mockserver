package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

/**
 * Waits for the LISTSCRIPTS command and returns the scripts 
 * specified in the script attribute.
 * 
 * In case the attribute is omitted and empty list is returned.
 */
public class ListScriptsStep implements Step {

  @Override
  public boolean is(final Element elm) {
    return true;
  }

  @Override
  public void execute(final TestContext context, final Element elm) throws Exception {
    String[] scripts = { };

    if (elm.hasAttribute("scripts"))
      scripts = elm.getAttribute("scripts").split(";");


    context.getServer()
      .log("Waiting for LISTSCRIPT command")
      .waitFor("LISTSCRIPTS");

    String result = "";
  
    for (final String script : scripts) {
      result += "\""+script+"\"\r\n";
    }
  
    result += "OK \"Listscript completed.\"\r\n";
  
    context.getServer().doReturn(result);
  }
  
}
