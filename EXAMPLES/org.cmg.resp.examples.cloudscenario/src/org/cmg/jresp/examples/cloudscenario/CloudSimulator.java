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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.cmg.jresp.comp.AttributeCollector;
import org.cmg.jresp.knowledge.AbstractActuator;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationAction;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.simulation.SimulationScheduler;

/**
 * @author loreti
 *
 */
public class CloudSimulator {
	
	private Scenario currentScenario;
	
	private int ensembleType;
	
	private HashMap<Double, Double> averageLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> minLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> maxLoad = new HashMap<Double,Double>();

	private HashMap<Double, Double> averageBaseLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> minBaseLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> maxBaseLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> averageStandardLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> minStandardLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> maxStandardLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> averagePremiumLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> minPremiumLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> maxPremiumLoad = new HashMap<Double,Double>();

	
	private HashMap<Double, Double> averageSPremiumLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> minSPremiumLoad = new HashMap<Double,Double>();
	
	private HashMap<Double, Double> maxSPremiumLoad = new HashMap<Double,Double>();


	private HashMap<Double, Double> baseWaiting = new HashMap<Double,Double>();

	private HashMap<Double, Double> standardWaiting = new HashMap<Double,Double>();

	private HashMap<Double, Double> premiumWaiting = new HashMap<Double,Double>();

	private HashMap<Double, Double> sPremiumWaiting = new HashMap<Double,Double>();

	private SimulationNode basicNode;

	private SimulationNode standardNode;

	private SimulationNode premiumNode;

	private SimulationNode spremiumNode;

	private int simulations;

	private int standard_size;

	private int base_size;

	private int premium_size;

	private int spremium_size;

	private int max_load;
	
	public CloudSimulator( int ensembleType , int base_size , int standard_size, int premium_size, int spremium_size , int max_load) {
		this.ensembleType = ensembleType;
		this.standard_size = standard_size;
		this.base_size = base_size;
		this.premium_size = premium_size;
		this.spremium_size = spremium_size;
		this.max_load = max_load;
	}
	
	
	
	
	private SimulationEnvironment instatiateNet( Random r ) {
		currentScenario = new Scenario( r , base_size , standard_size , premium_size , spremium_size , max_load );
		SimulationScheduler sim = new SimulationScheduler();
		SimulationEnvironment env = new SimulationEnvironment(sim, new RandomSelector(r), currentScenario.getDelayFactory(),currentScenario.getNodeConnection());
		final SimulationNode[] nodes = new SimulationNode[currentScenario.size()];
		for( int i=0 ; i<currentScenario.size() ; i++ ) {
			final SimulationNode n = new SimulationNode(""+i, env);
			nodes[i] = n;
			final int idx = i;
			for( int j=0 ; j<currentScenario.getMaxLoad() ; j++ ) {
				n.addAgent( new ServiceAgent(ensembleType,currentScenario, idx) );
			}
			n.addActuator( new AbstractActuator("TASK_EXECUTOR") {
				
				@Override
				public void send(Tuple t) {
//					System.out.println("Task executed!");
				}			
				
				@Override
				public Template getTemplate() {
					return new Template( new ActualTemplateField( "EXECUTE_TASK" ) );
				}
			});
			//if (scenario.getServiceLevel(idx)>0) {
			//}
		}
		this.basicNode = createProducerNode( Scenario.BASIC_REQUESTS_COMPONENT , env , Scenario.BASE_LEVEL );
		this.standardNode = createProducerNode( Scenario.STANDARD_REQUESTS_COMPONENT , env , Scenario.STANDARD_LEVEL );
		this.premiumNode = createProducerNode( Scenario.PREMIUM_REQUESTS_COMPONENT , env , Scenario.PREMIUM_LEVEL );
		this.spremiumNode = createProducerNode( Scenario.SPREMIUM_REQUESTS_COMPONENT , env , Scenario.SUPERPREMIUM_LEVEL );

		return env;
	}
	
	public void simulate( int iterations , double dt , double deadline ) throws InterruptedException {
		Random r = new Random();
		long totalTime = 0;
		for ( int i=0 ; i<iterations ; i++ ) {
			System.out.println("\nStart iteration "+(i+1));
			long start = System.currentTimeMillis();
			SimulationEnvironment env = instatiateNet( r );
			env.schedulePeriodicAction(new SimulationAction() {
				
				@Override
				public void doAction(double time) {
//					System.out.println(">"+time);
					computeStatistics( time );
				}
				
			}, 0.0, dt);
			env.simulate(deadline);
			env.join();
			long iterationTime = (System.currentTimeMillis()-start);
			totalTime += iterationTime; 
			System.out.println("Iteration "+(i+1)+" completed! (Last: "+iterationTime+" Total: "+totalTime+" Expected: "+((long) (((((double) totalTime)/(i+1))*(iterations-(i+1)))/1000))+")");
			simulations++;
		}
	}
	
	public void printData( PrintStream writer , double dt , double deadline ) {
		double time = 0.0;
		while (time <= deadline) {
			writer.println(time+"\t"+getValue(time,minLoad)+
					"\t"+getValue(time,averageLoad)+
					"\t"+getValue(time,maxLoad)+
					"\t"+getValue(time,minBaseLoad)+
					"\t"+getValue(time,averageBaseLoad)+
					"\t"+getValue(time,maxBaseLoad)+
					"\t"+getValue(time,minStandardLoad)+
					"\t"+getValue(time,averageStandardLoad)+
					"\t"+getValue(time,maxStandardLoad)+
					"\t"+getValue(time,minPremiumLoad)+
					"\t"+getValue(time,averagePremiumLoad)+
					"\t"+getValue(time,maxPremiumLoad)+
					"\t"+getValue(time,minSPremiumLoad)+
					"\t"+getValue(time,averageSPremiumLoad)+
					"\t"+getValue(time,maxSPremiumLoad)+
					"\t"+getValue(time,baseWaiting)+
					"\t"+getValue(time,standardWaiting)+
					"\t"+getValue(time,premiumWaiting)+
					"\t"+getValue(time,sPremiumWaiting));
			time += dt;
		}
	}
	
	private double getValue(double time, HashMap<Double, Double> data) {
		Double value = data.get(time);
		if (value == null) {
			return 0.0;
		}
		return value/simulations;
	}




	private void addValue( double time , double value , HashMap<Double, Double> data ) {
		Double total = data.get(time);
		if (total == null) {
			total = 0.0;
		}
		total = total + value;
		data.put(time, total);
	}
	
	private int getPendingTask( SimulationNode node ) {
		return node.getNumberOfTuplesMatching( new Template( 
				new ActualTemplateField("TASK") , 
				new FormalTemplateField(Integer.class)
			)
		);
	}
	
	protected void computeStatistics(double time) {
		addValue( time , getPendingTask(basicNode) , baseWaiting ); 
		addValue( time , getPendingTask(standardNode) , standardWaiting ); 
		addValue( time , getPendingTask(premiumNode) , premiumWaiting ); 
		addValue( time , getPendingTask(spremiumNode) , sPremiumWaiting ); 
		addValue( time , currentScenario.getMinLoadLevel() , minLoad );
		addValue( time , currentScenario.getMaxLoadLevel() , maxLoad );
		addValue( time , currentScenario.getAverageLoadLevel() , averageLoad );
		addValue( time , currentScenario.getMinLoadLevel(0) , minBaseLoad );
		addValue( time , currentScenario.getMaxLoadLevel(0) , maxBaseLoad );
		addValue( time , currentScenario.getAverageLoadLevel(0) , averageBaseLoad );
		addValue( time , currentScenario.getMinLoadLevel(1) , minStandardLoad );
		addValue( time , currentScenario.getMaxLoadLevel(1) , maxStandardLoad );
		addValue( time , currentScenario.getAverageLoadLevel(1) , averageStandardLoad );
		addValue( time , currentScenario.getMinLoadLevel(2) , minPremiumLoad );
		addValue( time , currentScenario.getMaxLoadLevel(2) , maxPremiumLoad );
		addValue( time , currentScenario.getAverageLoadLevel(2) , averagePremiumLoad );
		addValue( time , currentScenario.getMinLoadLevel(3) , minSPremiumLoad );
		addValue( time , currentScenario.getMaxLoadLevel(3) , maxSPremiumLoad );
		addValue( time , currentScenario.getAverageLoadLevel(3) , averageSPremiumLoad );
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

	public void run( double deadline , double dt ) throws InterruptedException {
		Random r = new Random();
		SimulationEnvironment env = instatiateNet( r );
		env.schedulePeriodicAction(new SimulationAction() {
			
			@Override
			public void doAction(double time) {
				System.out.println("\nTime: "+time);
				System.out.println(info());
			}
			
		}, 0.0, dt);
		env.simulate(deadline);
		env.join();		
	}
	
	protected String info() {
		return "Base Waiting: "+getPendingTask(basicNode)+"\n"+
			   "Standard Waiting: "+getPendingTask(standardNode)+"\n"+ 
			   "Premium Waiting: "+getPendingTask(premiumNode)+"\n"+
			   "Spremium Waiting: "+getPendingTask(spremiumNode)+"\n"+ 
			   "Load: ["+currentScenario.getMinLoadLevel()+"-"
			            +currentScenario.getAverageLoadLevel()+"-"
			            +currentScenario.getMaxLoadLevel()+"]\n"+
			   "Base Load: ["+currentScenario.getMinLoadLevel(0)+"-"
                        +currentScenario.getAverageLoadLevel(0)+"-"
                        +currentScenario.getMaxLoadLevel(0)+"]\n"+
			   "Standard Load: ["+currentScenario.getMinLoadLevel(1)+"-"
                        +currentScenario.getAverageLoadLevel(1)+"-"
                        +currentScenario.getMaxLoadLevel(1)+"]\n"+
			   "Premium Load: ["+currentScenario.getMinLoadLevel(2)+"-"+
			   			currentScenario.getAverageLoadLevel(2)+"-"+
			   			currentScenario.getMaxLoadLevel(2)+"]\n"+
			   "SPremium Load: ["+currentScenario.getMinLoadLevel(3)+"-"+
			   			currentScenario.getAverageLoadLevel(3)+"-"+
			   			currentScenario.getMaxLoadLevel(3)+"]";

	}
	
}
