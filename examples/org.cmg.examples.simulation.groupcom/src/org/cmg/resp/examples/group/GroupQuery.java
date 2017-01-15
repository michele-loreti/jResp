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
package org.cmg.jresp.examples.group;

import java.io.IOException;
import java.util.Random;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.simulation.DeterministicDelayFactory;
import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.simulation.SimulationScheduler;
import org.cmg.jresp.topology.AnyComponent;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.VirtualPort;

/**
 * @author Michele Loreti
 *
 */
public class GroupQuery {

	public static VirtualPort vp = new VirtualPort(10);

	public static GroupPredicate any = new AnyComponent();
	
	
	public static class GGetAgent extends Agent {

		public GGetAgent() {
			super("GGetAgent");
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					Template tmp = new Template( new FormalTemplateField(String.class) );
					Group g = new Group(any);
					Tuple t = query( tmp , g );
					System.out.println("RECEIVED: "+t.toString());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public static void main(String[] argv) {
		Random r = new Random();
		SimulationScheduler sim = new SimulationScheduler();
		SimulationEnvironment env = new SimulationEnvironment(sim, new RandomSelector(r), new DeterministicDelayFactory(1.0));
		SimulationNode node1 = new SimulationNode( "node1" , env );
		SimulationNode node2 = new SimulationNode( "node2" , env );
		node2.put(new Tuple(("TEST_1") ) );
		SimulationNode node3 = new SimulationNode( "node3" , env );
		node3.put(new Tuple(("TEST_2") ) );
		Agent agent1 = new GGetAgent();
		node1.addAgent(agent1);
		env.simulate(100.0);
		
	}
	
}
