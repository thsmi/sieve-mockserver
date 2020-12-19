package net.tschmid.sieve.mock.tests.steps.sasl;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;
import net.tschmid.sieve.mock.tests.steps.Step;

/**
 * Implements a test step for the the SASL PLAIN mechanism.
 */
public class SaslPlainStep implements Step {

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {
    /*
     * [22:54:02.845 server2] Client -> Server: AUTHENTICATE "PLAIN" "xxxxxxxxx"
     * [22:54:02.990 server2] Server -> Client OK
     */

    context.getServer()
      .waitFor("AUTHENTICATE \"PLAIN\" \"")
      .doReturn("OK\r\n");
  }
  
}
