/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universit? di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.jresp.example.disaster.simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.JOptionPane;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.AttributeCollector;
import org.cmg.jresp.examples.disaster.DataForwarder;
import org.cmg.jresp.examples.disaster.GoToVictim;
import org.cmg.jresp.examples.disaster.RandomWalk;
import org.cmg.jresp.examples.disaster.Scenario;
import org.cmg.jresp.examples.disaster.VictimSeeker;
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
 * Schell class for simulation of Disaster Scenario.
 * 
 * @author Michele Loreti
 *
 */
public class ShellMain {

	private HashMap<Double,Integer> success = new HashMap<Double, Integer>();
	
	private Scenario scenario;

	private static final double HEIGHT= 700;
	private static final double WIDTH= 550;
	
	private static final int LANDMARK= 0;
	private static final int WORKER= 1;


	public ShellMain( int landmarks , int workers , double height , double width  ) {
		scenario = new Scenario(landmarks , workers , 1 , height, width);
	}
	
	
	private SimulationEnvironment instantiateNet() {
		scenario.init();
		Random r = new Random();
		SimulationScheduler sim = new SimulationScheduler();
		SimulationEnvironment env = new SimulationEnvironment(sim, new RandomSelector(r), new DeterministicDelayFactory(1.0),scenario.getNodeConnection());

		Hashtable<String, SimulationNode> nodes = new Hashtable<String, SimulationNode>();
		sim.schedulePeriodicAction(new SimulationAction() {
			
			@Override
			public void doAction(double time) {
				try {
					scenario.step(0.1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}, 0.1, 0.1);
		
	for (int i=0 ;i<scenario.getLandmarks();i++) {
			SimulationNode n = new SimulationNode(""+i, env);
			n.addActuator(scenario.getDirectionActuator(i));
			n.addSensor(scenario.getCollisionSensor(i));
			n.addActuator(scenario.getStopActuator(i));
			n.addSensor(scenario.getVictimSensor(i));			
			
			n.put(new Tuple( "role" , LANDMARK) );
			
			n.addAttributeCollector( new AttributeCollector("role", 
					new Template( new ActualTemplateField( "role"),
								new FormalTemplateField(Integer.class)
							)
			) {
				
				@Override
				protected Object doEval(Tuple ... t) {
					return t[0].getElementAt(Integer.class, 1);
				}
			});
			
						
			Agent a = new RandomWalk();
			n.addAgent(a);
			a = new VictimSeeker(i);
			n.addAgent(a);
			a = new DataForwarder(i);
			n.addAgent(a);
			nodes.put(n.getName(), n);
		}
		
		for (int i=scenario.getLandmarks() ;i<(scenario.getLandmarks()+scenario.getWorkers());i++) {
			SimulationNode n = new SimulationNode(""+i, env);
			n.addActuator(scenario.getDirectionActuator(i));
			n.addSensor(scenario.getCollisionSensor(i));
			n.addActuator(scenario.getStopActuator(i));
			n.addSensor(scenario.getVictimSensor(i));			
			
			n.put(new Tuple( "role" , WORKER) );
			
			n.addAttributeCollector( new AttributeCollector("role", 
					new Template( new ActualTemplateField( "role"),
								new FormalTemplateField(Integer.class)
							)
			) {
				
				@Override
				protected Object doEval(Tuple ... t) {
					return t[0].getElementAt(Integer.class, 1);
				}
			});			
			
			Agent a = new GoToVictim(i,scenario);
			n.addAgent(a);			
			nodes.put(n.getName(), n);
//			a = new VictimSeeker(i);
//			n.addAgent(a);
		}
		
		return env;
	}
	
	


	public static void main( String[] argv ) throws InterruptedException, FileNotFoundException {
//		int numLandmarks = inputRobots("Number of LANDMARK robots", 20);
//		int numWorkers = inputRobots("Number of WORKER robots", 4);
//		//double width = inputHeightWidth("Arena width", 500);
//		//double height = inputHeightWidth("Arena height", 500);
//		ShellMain main = new ShellMain( numLandmarks , numWorkers , HEIGHT , WIDTH );
//		double dt = 10.0;
//		double deadline = 5000;
//		int iterations = 100;
//		HashMap<Double, Integer> results = main.modelCheck(iterations, deadline , dt );
//		double count = 0.0;
//		while (count <= deadline) {
//			Integer value = results.get(count);
//			if (value != null) {
//				double prob = 0.0;
//				prob = value.intValue();
//				prob = prob/iterations;
//				System.out.println(count+" -> "+prob);
//			}
//			count += dt;
//		}
//		modelCheck(100,5,5000,10,1024);
//		modelCheck(50,5,5000,10,1024);
//		modelCheck(20,5,5000,10,1024);
		modelCheck(100,5,4000,10,50);
	}
	
	public static void modelCheck( int numLandmarks , int numWorkers , double deadline , double dt , int iterations ) throws InterruptedException, FileNotFoundException {
		ShellMain main = new ShellMain( numLandmarks , numWorkers , HEIGHT , WIDTH );
		HashMap<Double, Integer> results = main.modelCheck(iterations, deadline , dt );
		PrintWriter writer = new PrintWriter("../data_"+numLandmarks+"_"+numWorkers+"_"+deadline+".dat");
		double count = 0.0;
		while (count <= deadline) {
			Integer value = results.get(count);
			double prob = 0.0;
			if (value != null) {
				prob = value.intValue();
				prob = prob/iterations;
			}
			writer.println(count+"\t"+prob);
			count += dt;
		}
		writer.close();
	}

	
	public HashMap<Double,Integer> modelCheck( int iterations , double deadline , double dt ) throws InterruptedException {

		final HashMap<Double,Integer> results = new HashMap<Double, Integer>();
		
		SimulationAction action = new SimulationAction() {
			
			@Override
			public void doAction(double time) {
				if (scenario.goalReached()) {
					Integer value = results.get(time);
					if (value == null) {
						value = 0;
					}
					results.put(time, value+1);
				}
			}
		};
		for( int i=0 ; i<iterations ; i++ ) {
			long start = System.currentTimeMillis();
			SimulationEnvironment env = instantiateNet();
			env.schedulePeriodicAction( action , 0.0 , dt );
			env.simulate(deadline);
			env.join();
			long end = System.currentTimeMillis();
			System.out.println("\n\nIteration "+i+" completed!");
			System.out.println("Iteration time: "+(end-start)+"\n\n");
		}
		
		return results;
		
	}
	
	public static int inputRobots(String message , int value) {
		while (true) {
			String size = JOptionPane.showInputDialog(message,value);
			try {
				return Integer.parseInt(size);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, size+" is not an integer!", "Error...", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public static double inputHeightWidth( String message , double value ) {
		while (true) {
			String size = JOptionPane.showInputDialog(message,value);
			try {
				return Double.parseDouble(size);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, size+" is not a double!", "Error...", JOptionPane.ERROR_MESSAGE);
			}
		}		
	}


}
