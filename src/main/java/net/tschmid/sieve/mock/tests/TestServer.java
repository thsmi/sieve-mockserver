package net.tschmid.sieve.mock.tests;

import java.net.SocketException;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.tschmid.sieve.mock.config.Configurable;
import net.tschmid.sieve.mock.config.ConfigurationParameter;
import net.tschmid.sieve.mock.log.ActivityLog;
import net.tschmid.sieve.mock.sieve.FakeServer;
import net.tschmid.sieve.mock.tests.steps.Step;


public class TestServer implements Runnable, TestContext {

  private Element root = null;
  private FakeServer server = null;
  private Thread thread = new Thread(this);

  private boolean stopped = true;

  private final Map<String, Step> steps;

  public TestServer(Map<String, Step> steps) {
    this.steps = steps;    
  }

  @Override
  public Step getStep(final String step){
    return this.steps.get(step);
  }

  @Override
  public boolean hasStep(final String step) {
    return this.steps.containsKey(step);
  }

  @Override
  public FakeServer getServer() {
    return this.server;
  }

  @Override
  public Configurable getFlags() {
    return this.getServer().getFlags();
  }  

  public void stop() throws Exception {

    this.stopped = true;

    if (this.server != null)
      this.server.shutdown();

    this.thread.interrupt();
  }

  public boolean isAlive() {
    return this.thread.isAlive();
  }

  public TestServer start(Element root, Configurable flags) throws Exception {

    if (this.isAlive())
      throw new Exception("Server already started");

    int port = 4190;
    if (root.hasAttribute("port"))
      port = Integer.parseInt(root.getAttribute("port"));

    this.server = new FakeServer(port, flags);

    this.root = root;
    this.thread.start();

    return this;
  }


  protected void doWait(final Element elm) throws Exception {
    String token = "";

    if (elm.hasAttribute("for"))
      token = elm.getAttribute("for");

    if (token.equals("")) {
      this.getServer().waitForAnything();
      return;
    }

    this.getServer().waitFor(token);
    return;
  }

  protected void doSleep(final Element elm) {
    int duration = 10 * 1000;

    if (elm.hasAttribute("duration"))
      duration = Integer.parseInt(elm.getAttribute("duration")) * 1000;

    this.getServer().sleep(duration);
  }

  protected void doSuccess(Element elm) {
    String msg = "Test passed";
    if (elm.hasAttribute("msg"))
      msg = elm.getAttribute("msg");

    this.doLog(msg);
  }

  protected void doLog(Element elm) {
    ActivityLog.getInstance().log(elm.getAttribute("msg"));
  }

  protected void doLog(String msg) {
    ActivityLog.getInstance().log(""
      + "["+this.server.getPort() +"]"      
      + " " + msg);
    // TODO Trigger a onLog callback
  }

  protected void doTest(final Element elm) throws Exception {

    String name = elm.getNodeName();

    if (this.steps.containsKey(name)) {
      if (this.steps.get(name).is(elm)) {
        this.steps.get(name).execute(this, elm);
        return;
      }
    }

    if (elm.getNodeName().equals("wait")) {
      doWait(elm);
      return;
    }

    if (elm.getNodeName().equals("sleep")) {
      doSleep(elm);
      return;
    }

    if (elm.getNodeName().equals("log")) {
      doLog(elm);
      return;
    }

    if (elm.getNodeName().equals("success")) {
      doSuccess(elm);
      return;
    }

    throw new Exception("Unknown element " + elm.getNodeName());
  }

  public void run() {

    this.stopped = false;

    do {
      try {

        this.getServer().log("Starting Test Sequence");

        NodeList children = root.getChildNodes();

        for (int idx = 0; idx < children.getLength(); idx++) {
          Node child = children.item(idx);

          if (child.getNodeType() != Node.ELEMENT_NODE)
            continue;

          doTest((Element) child);
        }

        this.getServer().close();
        this.getServer().log("Stopping Server Sequence");

      } catch (SocketException | InterruptedException ex) {
        break;
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    } while (!this.stopped && !this.server.getFlags().isEnabled(ConfigurationParameter.NO_RESPAWN));

    this.getServer().log("Sequence completed shutting down client");

    try {
      server.shutdown();        
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    this.stopped = true;
  }
}