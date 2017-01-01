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

import java.util.Arrays;
import java.util.Random;

import org.cmg.jresp.comp.NodeConnection;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.simulation.IDelayFactory;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.topology.GroupPredicate;

/**
 * @author loreti
 *
 */
public class Scenario {

	public static final String BASIC_REQUESTS_COMPONENT = "basic";
	public static final String STANDARD_REQUESTS_COMPONENT = "standard";
	public static final String PREMIUM_REQUESTS_COMPONENT = "premium";
	public static final String SPREMIUM_REQUESTS_COMPONENT = "spremium";
	
	public static final int BASE_LEVEL = 0;
	public static final int STANDARD_LEVEL = 1;
	public static final int PREMIUM_LEVEL = 2;
	public static final int SUPERPREMIUM_LEVEL = 3;
	
	public static final double TRANSMISSION_RATE = 1.0;
	public static final double CALL_RATE = 50.0;
	public static final double CALL_BASIC_RATE = 0.4*CALL_RATE;
	public static final double CALL_STANDARD_RATE = 0.35*CALL_RATE;
	public static final double CALL_PREMIUM_RATE = 0.15*CALL_RATE;
	public static final double CALL_SPREMIUM_RATE = 0.10*CALL_RATE;
	
	public static double SERVICE_RATE = 10.0;
	
	public static double AVERAGE_TASK_EXECUTION_TIME = 0.3; 

	protected int[] load;
	protected int[] level;
	private Random random;
	private int max_load;
		
	public Scenario( Random random , int size , int max_load ) {
		this( random , size , size , size , size , max_load );
	}

	public Scenario( int size , int max_load ) {
		this( size , size , size , size , max_load );
	}

	public Scenario(int base_size, int standard_size, int premium_size, int spremium_size , int max_load) {
		this( new Random() , base_size , standard_size , premium_size , spremium_size , max_load );
	}

	public Scenario(Random random , int base_size, int standard_size, int premium_size, int spremium_size , int max_load) {
		this.max_load = max_load;
		this.random = random;
		int total = base_size+standard_size+premium_size+spremium_size;
		this.load = new int[total];
		this.level = new int[total];
		int bound = base_size;
		for( int i=0 ; i<bound ; i++ ) {
			this.load[i] = max_load;
			this.level[i] = BASE_LEVEL;
		}
		for( int i=bound ; i<bound+standard_size ; i++ ) {
			this.load[i] = max_load;
			this.level[i] = STANDARD_LEVEL;
		}
		bound += standard_size;
		for( int i=bound ; i<bound+premium_size ; i++ ) {
			this.load[i] = max_load;
			this.level[i] = PREMIUM_LEVEL;
		}
		bound += premium_size;
		for( int i=bound ; i<bound+spremium_size ; i++ ) {
			this.load[i] = max_load;
			this.level[i] = SUPERPREMIUM_LEVEL;
		}
	}
	
	public double getServiceRate( int i ) {
		return SERVICE_RATE;
	}
	
	
	public void beginTaskAt( int i ) {
		this.load[i] = this.load[i]-1;
	}
	
	public void endTaskAt( int i ) {
		this.load[i] = this.load[i]+1;
	}

	public int getServiceLevel(int idx) {
		return this.level[idx];
	}

	public double getServiceExecutionTime(int level) {
		return 1 + (long) sampleExponential(AVERAGE_TASK_EXECUTION_TIME);
	}

	public int getLoad(int idx) {
		return this.load[idx];
	}
	
	private double sampleExponential( double rate ) {
		return 1/rate*-Math.log(random.nextDouble());
	}

	public NodeConnection getNodeConnection() {
		return new NodeConnection() {
			
			@Override
			public void waitInTouch(String src, String target)
					throws InterruptedException {
				
			}
			
			@Override
			public boolean areInTouch(String src, String target) {
				return true;
			}
		};
	}

	public IDelayFactory getDelayFactory() {
		return new IDelayFactory() {
			
			@Override
			public double getRetryTime(SimulationNode node) {
				return sampleExponential(1.0);
			}
			
			@Override
			public double getRemoteQueryTime(SimulationNode src, Template t,
					SimulationNode trg) {
				return sampleExponential(1.0);
			}
			
			@Override
			public double getRemotePutTime(SimulationNode src, Tuple t,
					SimulationNode trg) {
				return sampleExponential(1.0);
			}
			
			@Override
			public double getRemoteGetTime(SimulationNode src, Template t,
					SimulationNode trg) {
				return sampleExponential(1.0);
			}
			
			@Override
			public double getLocalQueryTime(SimulationNode node, Template t) {
				return 1.0;
			}
			
			@Override
			public double getLocalPutTime(SimulationNode node, Tuple t) {
				String name = node.getName();
				if (Scenario.BASIC_REQUESTS_COMPONENT.equals(name)) {
					return sampleExponential(CALL_BASIC_RATE);
				}
				if (Scenario.STANDARD_REQUESTS_COMPONENT.equals(name)) {
					return sampleExponential(CALL_STANDARD_RATE);
				}
				if (Scenario.PREMIUM_REQUESTS_COMPONENT.equals(name)) {
					return sampleExponential(CALL_PREMIUM_RATE);
				}
				if (Scenario.SPREMIUM_REQUESTS_COMPONENT.equals(name)) {
					return sampleExponential(CALL_SPREMIUM_RATE);
				}				
				return sampleExponential(Scenario.AVERAGE_TASK_EXECUTION_TIME);
			}
			
			@Override
			public double getLocalGetTime(SimulationNode node, Template t) {
				return 1.0;
			}
			
			@Override
			public double getGroupQueryTime(SimulationNode src, Template t,
					GroupPredicate target, SimulationNode trg) {
				int idx = 0;
				try {
					idx = Integer.parseInt(src.getName());
				} catch (NumberFormatException e) {
					return 1.0;
				}
				double service_rate = getServiceRate(idx);
				if (service_rate != 0) {
					return sampleExponential(service_rate)+sampleExponential(TRANSMISSION_RATE);
				}
				return 1.0;
			}
			
			@Override
			public double getGroupPutTime(SimulationNode src, Tuple t,
					GroupPredicate target) {
				int idx = 0;
				try {
					idx = Integer.parseInt(src.getName());
				} catch (NumberFormatException e) {
					return 1.0;
				}
				double service_rate = getServiceRate(idx);
				if (service_rate != 0) {
					return sampleExponential(service_rate);
				}
				return 1.0;
			}
			
			@Override
			public double getGroupGetTime(SimulationNode src, Template t,
					GroupPredicate target, SimulationNode trg) {
				int idx = 0;
				try {
					idx = Integer.parseInt(src.getName());
				} catch (NumberFormatException e) {
					return 1.0;
				}
				double service_rate = getServiceRate(idx);
				if (service_rate != 0) {
					return sampleExponential(service_rate);
				}
				return 1.0;
			}
		};
	}

	public String info() {
		return "\nL:"+Arrays.toString(load)+"\n :"+Arrays.toString(level);
	}

	public int size() {
		return load.length;
	}

	public int getMaxLoad() {
		return max_load;
	}

	public double getMinLoadLevel() {
		int min = max_load - load[0];
		for (int i=1 ; i<load.length ; i++ ) {
			if (min > (max_load - load[i])) {
				min = max_load - load[i];
			}
		}
		return ((double) min)/max_load;
	}

	public double getMaxLoadLevel() {
		int max = max_load - load[0];
		for (int i=1 ; i<load.length ; i++ ) {
			if (max < (max_load - load[i])) {
				max = max_load - load[i];
			}
		}
		return ((double) max)/max_load;
	}
	
	public double getAverageLoadLevel() {
		double sum = 0;
		for ( int i=0 ; i<load.length ; i++ ) {
			sum += (max_load - load[i]);
		}
		return (sum/max_load)/load.length;
	}

	public double getMinLoadLevel(int level) {
		int min = Integer.MAX_VALUE;
		for (int i=0 ; i<load.length ; i++ ) {
			if (this.level[i] == level) {
				if (min > (max_load - load[i])) {
					min = max_load - load[i];
				}
			}
		}
		if (min == Integer.MAX_VALUE) {
			min = 0;
		}
		return ((double) min)/max_load;
	}
	
	public double getMaxLoadLevel( int level ) {
		int max = 0;
		for (int i=1 ; i<load.length ; i++ ) {
			if (this.level[i] == level) {
				if (max < (max_load - load[i])) {
					max = max_load - load[i];
				}
			}
		}
		return ((double) max)/max_load;
	}
	


	public double getAverageLoadLevel(int level) {
		double sum = 0;
		int count = 0;
		for ( int i=0 ; i<load.length ; i++ ) {
			if (this.level[i] == level) {
				sum += (max_load - load[i]);
				count++;
			}
		}
		if (count == 0) {
			return count;
		} else {
			return (sum/max_load)/count;
		}
	}


}
