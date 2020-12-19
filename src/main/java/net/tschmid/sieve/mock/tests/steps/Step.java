package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

/**
 * Implements a test step.
 */
public interface Step {

  /**
   * Checks it the step can handle the given element
   * @param elm
   *   the xml element to be checked.
   * @return
   *   true in case this step can handle the element.
   */
  boolean is(Element elm);

  /**
   * Executes a test step.
   * 
   * @param context
   *   the test context in which the step should be executed.
   * @param elm
   *   the xml element with additional information for this step.
   * @throws Exception
   *   thrown in case the test step failed.
   */
  void execute(TestContext context, Element elm) throws Exception;
}
