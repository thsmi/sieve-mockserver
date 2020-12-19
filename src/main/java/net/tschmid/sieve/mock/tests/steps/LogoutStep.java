package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

/**
 * Waits for a logout command and terminates the client connection.
 */
public class LogoutStep implements Step {

  /**
   * Creates a new instance.
   */
  public LogoutStep() {
  }

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {
    context.getServer()
      .waitFor("LOGOUT")
      .doReturn("OK\r\n")
      .close();
  }
  
}
