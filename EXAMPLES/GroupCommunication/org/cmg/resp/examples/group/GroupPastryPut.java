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
package org.cmg.resp.examples.group;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.AnyComponent;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.ScribePort;
import org.cmg.jresp.topology.Self;

import rice.environment.Environment;

/**
 * @author Michele Loreti
 *
 */
public class GroupPastryPut {

	public static GroupPredicate any = new AnyComponent();

	public static class GPutAgent extends Agent {

		public GPutAgent() {
			super("GPutAgent");
		}

		@Override
		protected void doRun() {
			try {
				put(new Tuple(("TEST")), new Group(any));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static class GetAgent extends Agent {

		public GetAgent() {
			super("GetAgent");
		}

		@Override
		protected void doRun() {
			try {
				get(new Template(new FormalTemplateField(String.class)), Self.SELF);
				System.out.println("RECEIVED!");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] argv) throws IOException, InterruptedException {
		Environment env = new Environment();

		// disable the UPnP setting (in case you are testing this on a NATted
		// LAN)
		env.getParameters().setString("nat_search_policy", "never");
		// disable the UPnP setting (in case you are testing this on a NATted
		// LAN)
		// env.getParameters().setString("loglevel","INFO");

		ScribePort p1 = ScribePort.createScribePort(new InetSocketAddress("127.0.0.1", 9998).getAddress(), 9998, null,
				env);
		ScribePort p2 = ScribePort.createScribePort(new InetSocketAddress("127.0.0.1", 9999).getAddress(), 9999,
				new InetSocketAddress("127.0.0.1", 9998), env);
		ScribePort p3 = ScribePort.createScribePort(new InetSocketAddress("127.0.0.1", 9997).getAddress(), 9997,
				new InetSocketAddress("127.0.0.1", 9998), env);
		p1.subscribe();
		p2.subscribe();
		p3.subscribe();
		Node node1 = new Node("node1", new TupleSpace());
		Node node2 = new Node("node2", new TupleSpace());
		Node node3 = new Node("node3", new TupleSpace());
		node1.addPort(p1);
		node1.setGroupActionWaitingTime(100);
		node2.addPort(p2);
		node2.setGroupActionWaitingTime(100);
		node3.addPort(p3);
		node3.setGroupActionWaitingTime(100);
		Agent agent1 = new GPutAgent();
		Agent agent2 = new GetAgent();
		Agent agent3 = new GetAgent();
		node1.addAgent(agent1);
		node2.addAgent(agent2);
		node3.addAgent(agent3);
		node3.start();
		node2.start();
		node1.start();

	}

}
