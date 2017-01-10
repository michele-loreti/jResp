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

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

/**
 * @author Michele Loreti
 *
 */
public class LocalPingPongWithRetry {

	public static void main(String[] argv) {

		Node node = new Node("pingpong", new TupleSpace());
		Agent ping = new PingAgent();
		Agent pong = new PongAgent();
		node.addAgent(ping);
		node.addAgent(pong);
		System.out.println("START!");
		node.start();
	}

	public static class PingAgent extends Agent {

		public PingAgent() {
			super("PING");
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					System.out.println("PING!");
					put(new Tuple("PING"), Self.SELF);
					Tuple t = null;
					System.out.println("QUERY PONG!");
					while (t == null) {
						t = getp(new Template(new ActualTemplateField("PONG")));
						if (t == null) {
							System.out.println("PONG IS NOT YET AVAILABLE");
						}
						Thread.sleep(100);
					}
					// get(new Template(new ActualTemplateField( "PONG")) ,
					// Self.SELF);
					System.out.println("GET PONG!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class PongAgent extends Agent {

		public PongAgent() {
			super("PONG");
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					Tuple t = null;
					while (t == null) {
						t = getp(new Template(new ActualTemplateField("PING")));
						if (t == null) {
							System.out.println("PING IS NOT YET AVAILABLE");
						}
						Thread.sleep(100);
					}
					System.out.println("PONG!");
					put(new Tuple("PONG"), Self.SELF);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
