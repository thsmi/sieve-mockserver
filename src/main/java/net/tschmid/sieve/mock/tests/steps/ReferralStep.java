package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

/**
 * Sends a referral message and terminates the client connection.
 */
public class ReferralStep implements Step {

  @Override
  public boolean is(final Element elm) {
    return true;
  }

  @Override
  public void execute(final TestContext context, final Element elm) throws Exception {
    int port = 2001;

    if (elm.hasAttribute("port"))
      port = Integer.parseInt(elm.getAttribute("port"));

    context.getServer()
      .doReturn("BYE (REFERRAL \"sieve://127.0.0.1:" + port + "\") \"Try Remote.\"\r\n")
      .close();

    // doReturn("BYE (REFERRAL \"sieve://localhost:" + port + "\") \"Try Remote.\"\r\n");
  }
  
}
