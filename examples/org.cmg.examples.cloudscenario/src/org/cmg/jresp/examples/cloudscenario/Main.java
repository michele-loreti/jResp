/**
 * Copyright (c) 2014 Concurrency and Mobility Group.
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
package org.cmg.jresp.examples.cloudscenario;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.cmg.jresp.comp.AttributeCollector;
import org.cmg.jresp.knowledge.AbstractActuator;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.simulation.DeterministicDelayFactory;
import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationAction;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.simulation.SimulationScheduler;

/**
 * @author loreti
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
//		final Scenario scenario = new Scenario(18,12,6,2, load);
		//STATIC: 10,9,4,3
		//SCALAR: 15,5,2,1
		//DYNAMIC: 23,0,0,0
		//

//		CloudSimulator sim = new CloudSimulator(0, 6, 5, 3, 2, 15);
//		CloudSimulator sim = new CloudSimulator(1, 7,4,2,0, 15);
//		CloudSimulator sim = new CloudSimulator(2, 12, 0, 0, 0, 15);
		
//		long start = System.currentTimeMillis();
//		sim.run(100,4.0);
//		System.out.println("TOTAL TIME: "+(System.currentTimeMillis()-start));

		
		doSimulate();



	}
	
	
	protected static void doSimulate() throws FileNotFoundException, InterruptedException {
		
		PrintStream output = new PrintStream("./simulation_ensemble_0.dat");
		CloudSimulator sim = new CloudSimulator(0, 6, 5, 3, 2, 15);
		sim.simulate(1000, 10.0, 100);
		sim.printData(output, 10.0, 100);
		output.close();
		
//		output = new PrintStream("./simulation_ensemble_1.dat");
//		sim = new CloudSimulator(1, 7,4,2,0, 15);
//		sim.simulate(1000, 10.0, 100);
//		sim.printData(output, 10.0, 100);
//		output.close();
//
//		output = new PrintStream("./simulation_ensemble_2.dat");
//		sim = new CloudSimulator(2, 12, 0, 0, 0, 15);
//		sim.simulate(1000, 10.0, 100);
//		sim.printData(output, 10.0, 100);
//		output.close();
	}


	protected static int[] computeStatistics(SimulationNode basicNode,
			SimulationNode standardNode, SimulationNode premiumNode,
			SimulationNode spremiumNode) {
		int[] waitingTasks = new int[4];
		waitingTasks[0] = basicNode.getNumberOfTuplesMatching( new Template( 
										new ActualTemplateField("TASK") , 
										new FormalTemplateField(Integer.class)
									)
						);
		waitingTasks[1] = standardNode.getNumberOfTuplesMatching( new Template( 
				new ActualTemplateField("TASK") , 
				new FormalTemplateField(Integer.class)
			)
);
		waitingTasks[2] = premiumNode.getNumberOfTuplesMatching( new Template( 
				new ActualTemplateField("TASK") , 
				new FormalTemplateField(Integer.class)
			)
);
		waitingTasks[3] = spremiumNode.getNumberOfTuplesMatching( new Template( 
				new ActualTemplateField("TASK") , 
				new FormalTemplateField(Integer.class)
			)
);
		
		return waitingTasks;
	}


	private static SimulationNode createProducerNode(String name,
			SimulationEnvironment env, final int level) {
		SimulationNode node = new SimulationNode(name, env);
		node.addAttributeCollector( new AttributeCollector("LEVEL") {
			
			@Override
			protected Object doEval(Tuple... t) {
				return level;
			}
			
		});
		node.addAgent( new Client(name+"_client") );
		return node;
	}


	public static int[] computeStatistics( Scenario scenario , SimulationNode[] nodes ) {
		int[] waitingTasks = new int[4];
		for (int i=0 ; i< nodes.length ; i++) {
			waitingTasks[scenario.getServiceLevel(i)] += 
					nodes[i].getNumberOfTuplesMatching( new Template( 
							new ActualTemplateField("TASK") , 
							new FormalTemplateField(Integer.class)
					)
			);
		}
		return waitingTasks;
	}

}
