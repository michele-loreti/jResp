package org.cmg.test.pastry;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.cmg.jresp.topology.ScribePort;

import rice.environment.Environment;

public class Test {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		Environment env = new Environment();

		// disable the UPnP setting (in case you are testing this on a NATted
		// LAN)
		env.getParameters().setString("nat_search_policy", "never");

		ScribePort port1 = ScribePort.createScribePort(new InetSocketAddress("127.0.0.1", 9999).getAddress(), 9999,
				null, env);
		ScribePort port2 = ScribePort.createScribePort(new InetSocketAddress("127.0.0.1", 9998).getAddress(), 9998,
				new InetSocketAddress("127.0.0.1", 9999), env);
		Integer i = 87;

		synchronized (i) {
			i.wait();
		}
		// ScribePort port2 = ScribePort.createScribePort(9998, new
		// InetSocketAddress(9998), new Environment());
	}

}
