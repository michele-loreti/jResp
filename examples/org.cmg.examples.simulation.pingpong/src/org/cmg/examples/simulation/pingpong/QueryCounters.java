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
package org.cmg.examples.simulation.pingpong;

import java.io.IOException;
import java.util.Random;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.simulation.DeterministicDelayFactory;
import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.simulation.SimulationScheduler;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.VirtualPortAddress;

/**
 * @author Michele Loreti
 *
 */
public class QueryCounters {

	public static void main(String[] argv) throws IOException {
		Random r = new Random();
		SimulationScheduler scheduler = new SimulationScheduler();
		SimulationEnvironment environment = new SimulationEnvironment(scheduler, new RandomSelector(r), new DeterministicDelayFactory(1.0));

		SimulationNode nodeOne = new SimulationNode("one", environment);

		nodeOne.put(new Tuple( "COUNTER" , 0 ) );
		Agent one = new Counter("agentOne", new PointToPoint("two", new VirtualPortAddress(10)));
		Agent two = new Counter("agentTwo", new PointToPoint("one", new VirtualPortAddress(10)));
		nodeOne.addAgent(one);
		SimulationNode nodeTwo = new SimulationNode("two", environment);

		nodeTwo.put(new Tuple( "COUNTER" , 0 ) );
		nodeTwo.addAgent(two);

		scheduler.start();
	}
	
	
	public static class Counter extends Agent {

		PointToPoint other;
		Template counterTemplate = new Template( new ActualTemplateField( "COUNTER" ) , new FormalTemplateField(Integer.class) );
		
		public Counter( String name , PointToPoint other ) {
			super(name);
			this.other = other;
			
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					System.out.println(getName()+": RETRIEVING COUNTER VALUE REMOTELY!");
					Tuple t = query( counterTemplate , other);
					System.out.println(getName()+": COUNTER VALUE RETRIEVED!");
					get(counterTemplate,Self.SELF);
					System.out.println(getName()+": LOCAL COUNTER REMOVED!");
					put( new Tuple( "COUNTER" , t.getElementAt(Integer.class,1)+1 ) , Self.SELF);
					System.out.println(getName()+": COUNTER UPDATED TO "+(t.getElementAt(Integer.class,1)+1)+"!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
