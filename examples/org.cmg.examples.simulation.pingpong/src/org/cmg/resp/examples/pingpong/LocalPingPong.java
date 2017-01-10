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
package org.cmg.jresp.simulation.pingpong;

import java.util.Random;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.simulation.DeterministicDelayFactory;
import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationAction;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.simulation.SimulationScheduler;
import org.cmg.jresp.topology.Self;

/**
 * @author Michele Loreti
 *
 */
public class LocalPingPong {
	
	public static void main(String[] argv) {

		Random r = new Random();
		SimulationScheduler sim = new SimulationScheduler();
		SimulationEnvironment env = new SimulationEnvironment(sim, new RandomSelector(r), new DeterministicDelayFactory(1.0));
		SimulationNode node = new SimulationNode("pingpong",env);
		sim.schedulePeriodicAction( new SimulationAction( ) {
			
			@Override
			public void doAction(double time ) {
				System.out.println("\n####\n####\nTICK: "+time+"\n####\n####\n");
			}
		}, 0.0, 2.0);
		Agent ping = new PingAgent();
		Agent pong = new PongAgent();
		node.addAgent(ping);		
		node.addAgent(pong);
		System.out.println("START!");
		env.simulate(3000);
	}
	
	
	public static class PingAgent extends Agent {

		public PingAgent() {
			super("PING");
		}

		@Override
		protected void doRun() throws Exception {
//			while (true) {
				System.out.println("PING!");
				put(new Tuple( "PING" ) , Self.SELF);
				query(new Template(new ActualTemplateField( "PONG" )) , Self.SELF);
				System.out.println("QUERY PONG!");
				get(new Template(new ActualTemplateField( "PONG")) , Self.SELF);
				System.out.println("GET PONG!");
//			}
		}
		
	}


	public static class PongAgent extends Agent {
	
		public PongAgent() {
			super("PONG");
		}
	
		@Override
		protected void doRun() throws Exception {
//			while (true) {
				get(new Template(new ActualTemplateField( "PING" )) , Self.SELF);
				System.out.println("PONG!");
				put(new Tuple( "PONG" ) , Self.SELF);
//			}
		}
		
	}

}
