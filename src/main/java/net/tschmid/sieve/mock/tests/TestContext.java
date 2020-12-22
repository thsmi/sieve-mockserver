/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.tests;

import net.tschmid.sieve.mock.config.Configurable;
import net.tschmid.sieve.mock.sieve.FakeServer;
import net.tschmid.sieve.mock.tests.steps.Step;

/**
 * Provides a context for the test.
 */
public interface TestContext {
  
  /**
   * Returns a test step by its name.
   * @param step
   *   the step to be returned
   * @return
   *   the test step or null in case it is unknown
   */
  Step getStep(final String step);

  /**
   * Checks it the test step name is known
   * 
   * @return 
   *   true in case the test name is known otherwise false.
   */
  boolean hasStep(final String step);

  /**
   * Returns the server associated with this context.
   * 
   * @return 
   *   a reference to the current test server.
   */
  FakeServer getServer();

  /**
   * Returns the current configuration
   * 
   * @return
   *   a reference to the current test configuration
   */
  Configurable getFlags();
}
