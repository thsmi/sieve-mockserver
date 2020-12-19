package net.tschmid.sieve.mock.tests.steps.sasl;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;
import net.tschmid.sieve.mock.tests.steps.Step;

public class SaslScramSha512 implements Step {

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {
    throw new Error("Implement me");
  }
  
}
