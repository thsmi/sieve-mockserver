package net.tschmid.sieve.mock.tests.steps.sasl;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;
import net.tschmid.sieve.mock.tests.steps.Step;

public class SaslStep implements Step {

  Map<String, Step> commands = new HashMap<>();

  public SaslStep() {
    commands.put("LOGIN", new SaslLoginStep());
    commands.put("PLAIN", new SaslPlainStep());
    commands.put("EXTERNAL", new SaslExternalStep());

    commands.put("SCRAM-SHA-1", new SaslScramSha1());
    commands.put("SCRAM-SHA-256", new SaslScramSha256());
    commands.put("SCRAM-SHA-512", new SaslScramSha512());
  }

  @Override
  public boolean is(Element elm) {

    if (!elm.getNodeName().equals("sasl"))
      return false;

    String sasl = elm.getAttribute("mechanism");

    if (!commands.containsKey(sasl))
      return false;

    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {
    String sasl = elm.getAttribute("mechanism");    

    if (!commands.containsKey(sasl))
      throw new Exception("Unknown sasl mechanism " + sasl + " for element " + elm.getNodeName() + " specified");

    commands.get(sasl).execute(context, elm);
  }
  
}
