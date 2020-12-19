package net.tschmid.sieve.mock.tests.steps.sasl;

import static net.tschmid.sieve.mock.config.ConfigurationParameter.*;

import org.w3c.dom.Element;

import net.tschmid.sieve.mock.tests.TestContext;
import net.tschmid.sieve.mock.tests.steps.Step;

public class SaslScramSha256 implements Step {

  @Override
  public boolean is(Element elm) {
    return true;
  }

  @Override
  public void execute(TestContext context, Element elm) throws Exception {
    /*
     * We use test sequence from the RFC 5802
     *
     * C: n,,n=user,r=fyko+d2lbbFgONRv9qkxdawL S:
     * r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,s=QSXCR+Q6sek8bf92,i=4096 C:
     * c=biws,r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,p=v0X8v3Bz2T0CJGbJQyF0X+
     * HI4Ts= S: v=rmF9pqV8S7suAoZWja4dJRkFsKQ=
     *
     * As SCRAM is secure and this test is dumb we need to tweak/force the client to
     * use a a predefined nonce
     * 
     * nonce = fyko+d2lbbFgONRv9qkxdawL username = user password = pencil
     * 
     * C: biwsbj11c2VyLHI9ZnlrbytkMmxiYkZnT05Sdjlxa3hkYXdM S:
     * cj1meWtvK2QybGJiRmdPTlJ2OXFreGRhd0wzcmZjTkhZSlkxWlZ2V1ZzN2oscz1RU1hDUitRNnNlazhiZjkyLGk9NDA5Ng
     * == C:
     * Yz1iaXdzLHI9ZnlrbytkMmxiYkZnT05Sdjlxa3hkYXdMM3JmY05IWUpZMVpWdldWczdqLHA9djBYOHYzQnoyVDBDSkdiSlF5RjBYK0hJNFRzPQ
     * == S: dj1ybUY5cHFWOFM3c3VBb1pXamE0ZEpSa0ZzS1E9
     */

    context.getServer()
      .waitFor("AUTHENTICATE \"SCRAM-SHA-256\" \"biwsbj11c2VyLHI9ck9wck5HZndFYmVSV2diTkVrcU8=\"")
      .doReturn("\"cj1yT3ByTkdmd0ViZVJXZ2JORWtxTyVodllEcFdVYTJSYVRDQWZ1eEZJbGopaE5sRiRrMCxzPVcyMlphSjBTTlk3c29Fc1VFamI2Z1E9PSxpPTQwOTY=\"\r\n")
      .waitFor("\"Yz1iaXdzLHI9ck9wck5HZndFYmVSV2diTkVrcU8laHZZRHBXVWEyUmFUQ0FmdXhGSWxqKWhObEYkazAscD1kSHpiWmFwV0lrNGpVaE4rVXRlOXl0YWc5empmTUhnc3FtbWl6N0FuZFZRPQ==\"");

    String verifier;

    if (context.getServer().getFlags().isEnabled(SERVER_SIGNATURE_BROKEN))
      verifier = "\"dj1ybUY5cHFWOFM3c3VBb1pXamE0ZEpSa0ZzS0k=\"";
    else
      verifier = "\"dj02cnJpVFJCaTIzV3BSUi93dHVwK21NaFVaVW4vZEI1bkxUSlJzamw5NUc0PQ==\"";

    if (context.getServer().getFlags().isEnabled(SERVER_SIGNATURE_INLINE)) {
      context.getServer().doReturn("OK (SASL " + verifier + ")\r\n");
      return;
    }

    context.getServer()
      .doReturn(verifier + "\r\n")
      .waitFor("\"\"")
      .doReturn("OK\r\n");
  }
  
}
