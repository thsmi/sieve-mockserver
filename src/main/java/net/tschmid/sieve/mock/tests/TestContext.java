package net.tschmid.sieve.mock.tests;

import net.tschmid.sieve.mock.config.Configurable;
import net.tschmid.sieve.mock.sieve.FakeServer;
import net.tschmid.sieve.mock.tests.steps.Step;

public interface TestContext {
  
  Step getStep(final String step);
  boolean hasStep(final String step);
  FakeServer getServer();
  Configurable getFlags();
}
