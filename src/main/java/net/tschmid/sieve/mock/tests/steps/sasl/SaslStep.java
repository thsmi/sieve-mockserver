package net.tschmid.sieve.mock.tests.steps.sasl;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.exceptions.SieveTestException;
import net.tschmid.sieve.mock.tests.TestContext;
import net.tschmid.sieve.mock.tests.steps.Step;

/**
 * Implements a facade for the different sasl mechanisms.
 * Depending on the configuration it will forward
 * the call to the corresponding SASL Mechanism.
 */
public class SaslStep implements Step {

  /** Manages all supported sasls mechanisms. */
  private final Map<String, Step> mechanisms = new HashMap<>();

  /**
   * Creates a new instance
   */
  public SaslStep() {
    mechanisms.put("LOGIN", new SaslLoginStep());
    mechanisms.put("PLAIN", new SaslPlainStep());
    mechanisms.put("EXTERNAL", new SaslExternalStep());

    mechanisms.put("SCRAM-SHA-1", new SaslScramSha1());
    mechanisms.put("SCRAM-SHA-256", new SaslScramSha256());
    mechanisms.put("SCRAM-SHA-512", new SaslScramSha512());
  }

  @Override
  public boolean is(Element elm) {

    if (!elm.getNodeName().equals("sasl"))
      return false;

    String sasl = elm.getAttribute("mechanism");

    if (!mechanisms.containsKey(sasl))
      return false;

    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {
    String sasl = elm.getAttribute("mechanism");    

    if (!mechanisms.containsKey(sasl))
      throw new SieveTestException("Unknown sasl mechanism " + sasl + " for element " + elm.getNodeName() + " specified");

    mechanisms.get(sasl).execute(context, elm);
  }
  
}
