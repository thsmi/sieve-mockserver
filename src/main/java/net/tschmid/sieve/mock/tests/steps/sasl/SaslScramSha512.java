package net.tschmid.sieve.mock.tests.steps.sasl;

/**
 * Implements a test step for the SASL SCRAM SHA512 mechanism.
 */
public class SaslScramSha512 extends SaslScramSha {

  @Override
  protected String getDefaultFirstRequest() {
    throw new RuntimeException("Implement me");
  }

  @Override
  protected String getDefaultFirstResponse() {
    throw new RuntimeException("Implement me");
  }

  @Override
  protected String getDefaultFinalRequest() {
    throw new RuntimeException("Implement me");
  }

  @Override
  protected String getDefaultFinalResponse() {
    throw new RuntimeException("Implement me");
  }

  @Override
  protected String getMechanism() {
    throw new RuntimeException("Implement me");
  }
}
