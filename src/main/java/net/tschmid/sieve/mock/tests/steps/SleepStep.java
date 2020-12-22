package net.tschmid.sieve.mock.tests.steps;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;

/**
 * Sleeps for the given amount of seconds.
 * 
 * The optional "duration" attribute defines how long. 
 * In case it is omitted it will fallback to 10 seconds
 */
public class SleepStep implements Step {

  /** 
   * The optional duration attribute overrides the default sleep time.
   */
  public static String ATTR_DURATION = "duration";

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {

    int duration = 10 * 1000;

    if (elm.hasAttribute(ATTR_DURATION))
      duration = Integer.parseInt(elm.getAttribute(ATTR_DURATION)) * 1000;

    context.getServer().sleep(duration);
  }
  
}
