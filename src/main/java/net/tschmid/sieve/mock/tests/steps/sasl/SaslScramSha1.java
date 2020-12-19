package net.tschmid.sieve.mock.tests.steps.sasl;

/**
 * Implements a test step for the SASL SCRAM SHA1 mechanism.
 */
public class SaslScramSha1 extends SaslScramSha {

  @Override
  protected String getDefaultFirstRequest() {
    return "biwsbj11c2VyLHI9ZnlrbytkMmxiYkZnT05Sdjlxa3hkYXdM";
  }

  @Override
  protected String getDefaultFirstResponse() {
    return "cj1meWtvK2QybGJiRmdPTlJ2OXFreGRhd0wzcmZjTkhZSlkxWlZ2V1ZzN2oscz1RU1hDUitRNnNlazhiZjkyLGk9NDA5Ng==";
  }

  @Override
  protected String getDefaultFinalRequest() {
    return "Yz1iaXdzLHI9ZnlrbytkMmxiYkZnT05Sdjlxa3hkYXdMM3JmY05IWUpZMVpWdldWczdqLHA9djBYOHYzQnoyVDBDSkdiSlF5RjBYK0hJNFRzPQ==";  
  }

  @Override
  protected String getDefaultFinalResponse() {
    return "dj1ybUY5cHFWOFM3c3VBb1pXamE0ZEpSa0ZzS1E9";  
  }

  @Override
  protected String getMechanism() {
    return "SCRAM-SHA-1";
  }
}
