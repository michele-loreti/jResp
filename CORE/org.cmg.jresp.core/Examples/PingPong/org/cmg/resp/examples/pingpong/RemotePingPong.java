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

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.SocketPort;
import org.cmg.jresp.topology.SocketPortAddress;

/**
 * @author Michele Loreti
 *
 */
public class RemotePingPong {

	public static void main(String[] argv) throws IOException {
		SocketPort pingPort = new SocketPort(9999);
		SocketPort pongPort = new SocketPort(9998);
		Node pingNode = new Node("ping", new TupleSpace());
		pingNode.addPort(pingPort);
		Agent ping = new PingAgent();
		Agent pong = new PongAgent();
		pingNode.addAgent(ping);
		Node pongNode = new Node("pong", new TupleSpace());
		pongNode.addPort(pongPort);
		pongNode.addAgent(pong);
		pongNode.start();
		pingNode.start();
	}

	public static class PingAgent extends Agent {

		PointToPoint other = new PointToPoint("pong", new SocketPortAddress("127.0.0.1", 9998));

		public PingAgent() {
			super("PING");
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

		PointToPoint other = new PointToPoint("ping", new SocketPortAddress("127.0.0.1", 9999));

		public PongAgent() {
			super("PONG");
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
