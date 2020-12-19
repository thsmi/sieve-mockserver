package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

public interface Step {

  public boolean is(Element elm);
  
  public void execute(TestContext context, Element elm) throws Exception;
  
}
