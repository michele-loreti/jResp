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
import org.cmg.jresp.knowledge.FormalTemplateField;
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
public class QueryCounters {

	public static void main(String[] argv) throws IOException {
		SocketPort portOne = new SocketPort(9999);
		SocketPort portTwo = new SocketPort(9998);
		Node nodeOne = new Node("one", new TupleSpace());
		nodeOne.addPort(portOne);
		nodeOne.put(new Tuple("COUNTER", 0));
		Agent one = new Counter("agentOne", new PointToPoint("two", new SocketPortAddress(9998)));
		Agent two = new Counter("agentTwo", new PointToPoint("one", new SocketPortAddress(9999)));
		nodeOne.addAgent(one);
		Node nodeTwo = new Node("two", new TupleSpace());
		nodeTwo.addPort(portTwo);
		nodeTwo.put(new Tuple("COUNTER", 0));
		nodeOne.put(new Tuple("COUNTER", 0));
		nodeTwo.addAgent(two);
		nodeTwo.start();
		nodeOne.start();
	}

	public static class Counter extends Agent {

		PointToPoint other;
		Template counterTemplate = new Template(new ActualTemplateField("COUNTER"),
				new FormalTemplateField(Integer.class));

		public Counter(String name, PointToPoint other) {
			super(name);
			this.other = other;

		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					System.out.println(getName() + ": RETRIEVING COUNTER VALUE REMOTELY!");
					Tuple t = query(counterTemplate, other);
					System.out.println(getName() + ": COUNTER VALUE RETRIEVED!");
					get(counterTemplate, Self.SELF);
					System.out.println(getName() + ": LOCAL COUNTER REMOVED!");
					put(new Tuple("COUNTER", t.getElementAt(Integer.class, 1) + 1), Self.SELF);
					System.out.println(
							getName() + ": COUNTER UPDATED TO " + (t.getElementAt(Integer.class, 1) + 1) + "!");
					Thread.sleep(10);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
