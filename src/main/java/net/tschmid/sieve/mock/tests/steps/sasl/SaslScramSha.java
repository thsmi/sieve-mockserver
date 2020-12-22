package net.tschmid.sieve.mock.tests.steps.sasl;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.tschmid.sieve.mock.tests.TestContext;
import net.tschmid.sieve.mock.tests.steps.Step;

/**
 * SCRAM is a family of client first authentication mechanisms.
 * The all use the SHA algorithms, but differ in their bit length.
 * 
 * This abstract test step implements a wrapper for the different SHA mechanisms.
 */
public abstract class SaslScramSha implements Step {

  /** The optional node name for the first client message */
  public final String NODE_FIRST_REQUEST = "firstrequest";
  /** The optional node name for the first server message */
  public final String NODE_FIRST_RESPONSE = "firstresponse";
  /** The optional node name for the final client message */
  public final String NODE_FINAL_REQUEST = "finalrequest";
  /** The optional node name for the final server message */
  public final String NODE_FINAL_RESPONSE = "finalresponse";

  /** The optional attribute name, indicating that an inline response should be used */
  public final String ATTR_INLINE = "inline";

  /**
   * Retrieves the value of a child element.
   * In case the element does not exist the fallback value is returned.
   * 
   * @param root
   *   the root node
   * @param name
   *   the child nodes name
   * @param fallback
   *   the fallback value in case the child node does not exists.
   * @return
   *   the child element's value or in case it does not exist the fallback value.
   */
  protected String getChildValue(Element root, String name, String fallback) {

    NodeList children = root.getChildNodes();

    for (int idx = 0; idx < children.getLength(); idx++) {
      Node child = children.item(idx);

      if (child.getNodeType() != Node.ELEMENT_NODE)
        continue;

      if (child.getNodeName().equals(name))
        return child.getTextContent().trim();
    }

    return fallback;
  }

  /**
   * Gets the first client message aka the first request.
   * 
   * @return
   *   the first request's message
   */
  abstract protected String getDefaultFirstRequest();

  /**
   * Gets the first server message aka the first response.
   * 
   * @return
   *   the first response's message
   */
  abstract protected String getDefaultFirstResponse();

  /**
   * Gets the final client message aka the final request.
   * 
   * @return
   *   the final request's message
   */
  abstract protected String getDefaultFinalRequest();

  /**
   * Gets the final server message aka the final response.
   * 
   * @return
   *   the final response's message
   */
  abstract protected String getDefaultFinalResponse();

  /**
   * The SASL mechanism official name.
   * @return
   *   the name as string.
   */
  abstract protected String getMechanism();

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

    String firstRequest = this.getChildValue(
      elm, NODE_FIRST_REQUEST, this.getDefaultFirstRequest());
    String firstResponse = this.getChildValue(
      elm, NODE_FIRST_RESPONSE, this.getDefaultFirstResponse());
    String finalRequest = this.getChildValue(
      elm, NODE_FINAL_REQUEST, this.getDefaultFinalRequest());
    String finalResponse = this.getChildValue(
      elm, NODE_FINAL_RESPONSE, this.getDefaultFinalResponse());                    

    context.getServer()
      .log("Starting SASL "+this.getMechanism()+" Authentication")
      .log("Waiting for first request")
      .waitFor("AUTHENTICATE \"%s\" \"%s\"", this.getMechanism(), firstRequest)
      .log("Sending first response")
      .doReturn("\"%s\"\r\n", firstResponse)
      .log("Waiting for final request")
      .waitFor("\"%s\"", finalRequest);

    if (elm.hasAttribute(ATTR_INLINE)) {
      context.getServer()
        .log("Sending inline final response and signal completion")
        .doReturn("OK (SASL \"%s\")\r\n", finalResponse);
      return;
    }

    context.getServer()
      .log("Sending final response")
      .doReturn("\"%s\"\r\n", finalResponse)
      .log("Waiting for empty request")
      .waitFor("\"\"")
      .log("Signal completion")
      .doReturn("OK\r\n");
  }  
  
}
