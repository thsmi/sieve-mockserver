package net.tschmid.sieve.mock.tests;

import net.tschmid.sieve.mock.config.Configurable;
import net.tschmid.sieve.mock.sieve.FakeServer;
import net.tschmid.sieve.mock.tests.steps.Step;

public interface TestContext {
  
  public Step getStep(final String step);
  public boolean hasStep(final String step);
  public FakeServer getServer();
  public Configurable getFlags();
}
