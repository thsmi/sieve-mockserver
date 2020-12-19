/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve-mockserver/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */
package net.tschmid.sieve.mock;

import net.tschmid.sieve.mock.http.HttpServer;
import net.tschmid.sieve.mock.http.endpoints.ConfigurationEndpoint;
import net.tschmid.sieve.mock.http.endpoints.LogEndpoint;
import net.tschmid.sieve.mock.http.endpoints.MainEndpoint;
import net.tschmid.sieve.mock.http.endpoints.TemplatesEndpoint;
import net.tschmid.sieve.mock.http.endpoints.TestEndpoint;

// Use the following commands to create a keystore and a truststore.
//
// set path="c:\Program Files\Java\jre6\bin";%path%
//
// keytool -genkey -alias server -keyalg RSA -keystore keystore.jks -storepass secret -keypass secret
// keytool -export -alias server -keystore keystore.jks -rfc -file server.cer -storepass secret
// keytool -import -alias ca -file server.cer -keystore truststore.jks -storepass secret
// keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass secret


public class SieveMockService {

	public static void main(String[] args) throws Exception {
		(new HttpServer())
			.addEndpoint(new MainEndpoint())
			.addEndpoint(new TemplatesEndpoint())
			.addEndpoint(new ConfigurationEndpoint())
			.addEndpoint(new TestEndpoint())
			.addEndpoint(new LogEndpoint())
			.start();
	}

}