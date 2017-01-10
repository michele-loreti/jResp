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
package org.cmg.jresp.examples.pingpong;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.VirtualPort;
import org.cmg.jresp.topology.VirtualPortAddress;

/**
 * @author Michele Loreti
 *
 */
public class VirtualPingPong {

	public static void main(String[] argv) throws IOException {
		VirtualPort vp = new VirtualPort(10);
		Node pingNode = new Node("ping", new TupleSpace());
		pingNode.addPort(vp);
		Agent ping = new PingAgent();
		Agent pong = new PongAgent();
		pingNode.addAgent(ping);
		Node pongNode = new Node("pong", new TupleSpace());
		pongNode.addPort(vp);
		pongNode.addAgent(pong);
		pongNode.start();
		pingNode.start();
	}

	public static class PingAgent extends Agent {

		PointToPoint other = new PointToPoint("pong", new VirtualPortAddress(10));

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

		PointToPoint other = new PointToPoint("ping", new VirtualPortAddress(10));

		public PongAgent() {
			super("PONG");
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					get(new Template(new ActualTemplateField("PING")), Self.SELF);
					System.out.println("GET PING!");
					System.out.println("PONG!");
					put(new Tuple("PONG"), other);
					System.out.println("PONG DONE!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
