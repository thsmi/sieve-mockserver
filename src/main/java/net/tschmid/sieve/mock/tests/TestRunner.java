package net.tschmid.sieve.mock.tests;

import static net.tschmid.sieve.mock.config.ConfigurationParameter.*;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.tschmid.sieve.mock.config.Configuration;
import net.tschmid.sieve.mock.exceptions.SieveTestException;
import net.tschmid.sieve.mock.log.ActivityLog;
import net.tschmid.sieve.mock.tests.steps.CapabilityStep;
import net.tschmid.sieve.mock.tests.steps.DeleteScriptStep;
import net.tschmid.sieve.mock.tests.steps.InitStep;
import net.tschmid.sieve.mock.tests.steps.ListScriptsStep;
import net.tschmid.sieve.mock.tests.steps.LogoutStep;
import net.tschmid.sieve.mock.tests.steps.ReferralStep;
import net.tschmid.sieve.mock.tests.steps.StartTLSStep;
import net.tschmid.sieve.mock.tests.steps.Step;
import net.tschmid.sieve.mock.tests.steps.sasl.SaslStep;



public class TestRunner {

  private static TestRunner singleton = null;

  private final List<TestServer> servers = new LinkedList<>();

  private final Configuration flags = new Configuration();

  public static synchronized TestRunner getInstance() {
    if (TestRunner.singleton == null)
      TestRunner.singleton = new TestRunner();

    return TestRunner.singleton;
  }

  protected TestRunner() {
    // Initialize the flags.
    this.flags.setFlag(SUPPORT_TLS);

    this.flags.setFlag(DEFAULT_KEYSTORE, 
      this.getClass().getResource("./certs/keystore.jks").getPath());
    this.flags.setFlag(DEFAULT_KEYSTORE_PASSPHRASE, "secret");

    this.flags.setFlag(DEFAULT_TRUSTSTORE,
      this.getClass().getResource("./certs/truststore.jks").getPath());
    this.flags.setFlag(DEFAULT_TRUSTSTORE_PASSPHRASE, "secret");

    this.flags.setFlag(SIMULATE_CYRUS_STARTTLS_BUG);
  }

  public Configuration getFlags() {
    return flags;    
  }

  public ActivityLog getLog() {
    return ActivityLog.getInstance();
  }

  public boolean isRunning() {
    for (TestServer server: this.servers) {
      if (server.isAlive())
        return true;
    }

    return false;
  }

  public void start(String test) throws Exception {
    // Ensure any previous session is closed.
    this.stop();


    // Initialize the test steps, they are stateless and can be reused for multiple severs.
    Map<String, Step> steps = new HashMap<>();
    steps.put("sasl", new SaslStep());
    steps.put("init", new InitStep());
    steps.put("logout", new LogoutStep());
    steps.put("deletescript", new DeleteScriptStep());
    steps.put("listscripts", new ListScriptsStep());
    steps.put("referral", new ReferralStep());
    steps.put("starttls", new StartTLSStep());
    steps.put("capability", new CapabilityStep());    

    // Start parsing the xml.
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(test));
    Document doc = dBuilder.parse(is);

    doc.getDocumentElement().normalize();    

    Element root = doc.getDocumentElement();

    if (!root.getNodeName().equals("test"))
      throw new SieveTestException("Expected root node test");      

    NodeList children = root.getChildNodes();
    for (int idx = 0; idx < children.getLength(); idx++) {
      Node child = children.item(idx);

      if (child.getNodeType() != Node.ELEMENT_NODE)
        continue;

      if (child.getNodeName().equals("server")) {
        this.servers.add(
          (new TestServer(steps)).start((Element)child, this.getFlags()) );
        continue;
      }

      if (child.getNodeName().equals("log")) {
        ActivityLog.getInstance().log(((Element)child).getAttribute("msg"));
        continue;
      }

      System.out.println("Unknown element "+child.getNodeName());
      throw new SieveTestException("Unknown element "+child.getNodeName());
    }

  }

  /*@Override
  public void run() {
    try {
      SieveTestable test = new SieveParametrizedTest(this.test);

      do {
        try {
          test.doTest(this.mock);
        } finally {
          synchronized (this.lock) {
            this.close();
          }
        }

        synchronized (this.lock) {
          if (this.mock == null)
            break;

          if (!this.mock.getFlags().hasFlag(ConfigurationParameter.RESPAWN))
            break;
        }

      } while (true);

      this.mock = null;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }*/

  public void stop() throws Exception {

    for (TestServer server : this.servers)
      server.stop();

    this.servers.clear();
  }
 
}
