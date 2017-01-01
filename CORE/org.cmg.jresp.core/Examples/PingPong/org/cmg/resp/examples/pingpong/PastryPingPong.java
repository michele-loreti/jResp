/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universita' di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.resp.examples.pingpong;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.ScribePort;
import org.cmg.jresp.topology.Self;

import rice.environment.Environment;

/**
 * @author Michele Loreti
 *
 */
public class PastryPingPong {

	public static void main(String[] argv) throws IOException, InterruptedException {
		Environment env = new Environment();

		// disable the UPnP setting (in case you are testing this on a NATted
		// LAN)
		env.getParameters().setString("nat_search_policy", "never");

		ScribePort pongPort = ScribePort.createScribePort(new InetSocketAddress("127.0.0.1", 9998).getAddress(), 9998,
				null, env);
		ScribePort pingPort = ScribePort.createScribePort(new InetSocketAddress("127.0.0.1", 9999).getAddress(), 9999,
				new InetSocketAddress("127.0.0.1", 9998), env);
		// for( int i=0 ; i<10 ; i++ ) {
		// ScribePort foo = ScribePort.createScribePort(new
		// InetSocketAddress("127.0.0.1", 10000+i).getAddress(),10000+i, new
		// InetSocketAddress("127.0.0.1", 9998), env);
		// }

		// ScribePort fooPort1 = ScribePort.createScribePort(new
		// InetSocketAddress("127.0.0.1", 9997).getAddress(),9997, new
		// InetSocketAddress("127.0.0.1", 9999), env);
		// ScribePort fooPort2 = ScribePort.createScribePort(new
		// InetSocketAddress("127.0.0.1", 9996).getAddress(),9996, new
		// InetSocketAddress("127.0.0.1", 9999), env);
		// ScribePort fooPort3 = ScribePort.createScribePort(new
		// InetSocketAddress("127.0.0.1", 9995).getAddress(),9995, new
		// InetSocketAddress("127.0.0.1", 9999), env);
		System.out.println("PING:" + pingPort.getAddress());
		System.out.println("PONG:" + pongPort.getAddress());
		// System.out.println("FOO1:"+fooPort1.getAddress());
		// System.out.println("FOO2:"+fooPort2.getAddress());
		// System.out.println("FOO3:"+fooPort3.getAddress());
		Node pingNode = new Node("ping", new TupleSpace());
		pingNode.addPort(pingPort);
		Agent ping = new PingAgent(new PointToPoint("pong", pongPort.getAddress()));
		Agent pong = new PongAgent(new PointToPoint("ping", pingPort.getAddress()));
		pingNode.addAgent(ping);
		Node pongNode = new Node("pong", new TupleSpace());
		pongNode.addPort(pongPort);
		pongNode.addAgent(pong);
		pongNode.start();
		pingNode.start();
	}

	public static class PingAgent extends Agent {

		PointToPoint other;

		public PingAgent(PointToPoint other) {
			super("PING");
			this.other = other;
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					System.out.println("PING!");
					put(new Tuple("PING"), other);
					System.out.println("PING DONE!");
					get(new Template(new ActualTemplateField("PONG")), Self.SELF);
					System.out.println("GET PONG!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class PongAgent extends Agent {

		PointToPoint other;

		public PongAgent(PointToPoint other) {
			super("PONG");
			this.other = other;
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					get(new Template(new ActualTemplateField("PING")), Self.SELF);
					System.out.println("PONG!");
					put(new Tuple("PONG"), other);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
