package net.tschmid.sieve.mock.tests.steps.sasl;

/**
 * Implements a test step for the SASL SCRAM SHA256 mechanism.
 */
public class SaslScramSha256 extends SaslScramSha {

  @Override
  protected String getDefaultFirstRequest() {
    return "biwsbj11c2VyLHI9ck9wck5HZndFYmVSV2diTkVrcU8=";
  }

  @Override
  protected String getDefaultFirstResponse() {
    return "cj1yT3ByTkdmd0ViZVJXZ2JORWtxTyVodllEcFdVYTJSYVRDQWZ1eEZJbGopaE5sRiRrMCxzPVcyMlphSjBTTlk3c29Fc1VFamI2Z1E9PSxpPTQwOTY=";
  }

  @Override
  protected String getDefaultFinalRequest() {
    return "Yz1iaXdzLHI9ck9wck5HZndFYmVSV2diTkVrcU8laHZZRHBXVWEyUmFUQ0FmdXhGSWxqKWhObEYkazAscD1kSHpiWmFwV0lrNGpVaE4rVXRlOXl0YWc5empmTUhnc3FtbWl6N0FuZFZRPQ==";
  }

  @Override
  protected String getDefaultFinalResponse() {
    return "dj02cnJpVFJCaTIzV3BSUi93dHVwK21NaFVaVW4vZEI1bkxUSlJzamw5NUc0PQ==";
  }

  @Override
  protected String getMechanism() {
    return "SCRAM-SHA-256";
  }
}
