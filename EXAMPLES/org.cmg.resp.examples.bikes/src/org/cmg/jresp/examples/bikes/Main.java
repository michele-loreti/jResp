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
package org.cmg.jresp.examples.bikes;

import java.util.HashMap;
import java.util.Random;

import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationAction;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationScheduler;

/**
 * @author loreti
 *
 */
public class Main {



	private Random random;


	public Main() {
		this.random = new Random();
	}
	
	
	public static void main(String[] argv ) throws InterruptedException {
		Main m = new Main();
		HashMap<Double,Double> statisticsMin = new HashMap<Double, Double>();
		HashMap<Double,Double> statisticsMax = new HashMap<Double, Double>();
		HashMap<Double,Double> statisticsAverage = new HashMap<Double, Double>();
		int iterations = 1000;
		for( int i=0 ; i< iterations ; i++ ) {
			SimulationEnvironment env = m.instantiateNet(statisticsMin,statisticsMax,statisticsAverage,40, 4, 4, 5);
			env.simulate(500);
			env.join();
			System.out.println("Simulation "+i+" DONE!");
		}
		for( double i=0.0 ; i<=500.0 ; i++ ) {
			double min = m.getStatistic(i, statisticsMin)/iterations;
			double max = m.getStatistic(i, statisticsMax)/iterations;
			double avg = m.getStatistic(i, statisticsAverage)/iterations;
			System.out.println(i+"\t"+min+"\t"+max+"\t"+avg);
		}
	}
	

	public SimulationEnvironment instantiateNet( final HashMap<Double, Double> statisticsMin, final HashMap<Double, Double> statisticsMax, final HashMap<Double, Double> statisticsAverage, int users , int width , int height , int bikes ) {
		SimulationScheduler sim = new SimulationScheduler();
//		SimulationEnvironment env = new SimulationEnvironment(sim, new RandomSelector(random), new BikeDelayFactory(random), null);
		SimulationEnvironment env = new SimulationEnvironment(sim, new RandomSelector(random), new ConstantBikeDelayFactory(random), null);
		final Scenario scenario = new Scenario( random , env , users , width , height , bikes );
		SimulationAction action = new SimulationAction() {
			
			@Override
			public void doAction(double time) {
				recordStatistic( time , scenario.getMinBikes() , statisticsMin );
				recordStatistic( time , scenario.getMaxBikes() , statisticsMax );
				recordStatistic( time , scenario.averageBikes() , statisticsAverage );
			}
			
		};
		env.schedulePeriodicAction( action , 0.0 , 1.0 );
		return env;
	}
	
	protected void recordStatistic( double time , double value , HashMap<Double,Double> data ) {
		Double stored = data.get(time);
		if (stored == null) {
			stored = value;
		} else {
			stored = stored+value;
		}
		data.put(time, stored);
	}
	
	protected double getStatistic( double time , HashMap<Double,Double> data ) {
		Double stored = data.get(time);
		if (stored == null) {
			return 0.0;
		}
		return stored;
	}
	
}
