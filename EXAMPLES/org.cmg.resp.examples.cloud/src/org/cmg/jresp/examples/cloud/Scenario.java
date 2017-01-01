/**
 * Copyright (c) 2013 Concurrency and Mobility Group.
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
package org.cmg.jresp.examples.cloud;

import java.util.Random;

import org.cmg.jresp.comp.AttributeCollector;
import org.cmg.jresp.comp.INode;
import org.cmg.jresp.knowledge.AbstractActuator;
import org.cmg.jresp.knowledge.AbstractSensor;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.simulation.SimulationAction;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.topology.Self;

/**
 * @author loreti
 *
 */
public class Scenario {
	
	protected static final double LOAD_TO_TIME_FACTOR = 100.0;
	private CloudComponent[] components;	
	private int size; 
	
	private SimulationEnvironment environment;
	
	public Scenario( CloudComponent ... components ) {
		this.components = components;
		this.size = components.length;
	}
	
	public Scenario( int size , Random r , int minMem , int maxMem , int minCpuRate , int maxCpuRate ) {
		this.size=size;
		this.components = new CloudComponent[size];
		for( int i=0 ; i<size ; i++ ) {
			this.components[i] = new CloudComponent(i , minMem + r.nextInt(maxMem-minMem) , minCpuRate+(r.nextDouble()*(maxCpuRate-minCpuRate)) );
		}
	}
	
	public int getSize(){
		return size;
	}
	public AbstractSensor getMemorySensor( int id ) {
		return components[id].getMemorySensor();
	}
	
	public AbstractSensor getCpuSensor(  int id ) {
		return components[id].getCpuSenros();
	}
	
	public AttributeCollector getMemoryAttributeCollector( ) {
		return new AttributeCollector( "MEMORY" , CloudComponent.MEMORY_SENSOR_TEMPLATE ) {
			
			@Override
			protected Object doEval(Tuple... t) {
				if (t.length==0) {
					return null;
				}
				try {
					return t[0].getElementAt(Integer.class, 1);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		};
	}

	public AttributeCollector getCpuLoadAttributeCollector( ) {
		return new AttributeCollector( "CPU_LOAD" , CloudComponent.CPU_SENSOR_TEMPLATE) {
			
			@Override
			protected Object doEval(Tuple... t) {
				if (t.length==0) {
					return null;
				}
				try {
					return t[0].getElementAt(Double.class, 1);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		};
	}

	public AttributeCollector getCpuRateAttributeCollector( final int id ) {
		return new AttributeCollector( "CPU_RATE" ) {
			
			@Override
			protected Object doEval(Tuple... t) {
				return components[id].getCPURate();
			}
		};
	}
	
	public AbstractActuator getServiceInvocationActuator( final int id , final INode n ) {
		return new AbstractActuator( "SERVICE_CALL" ) {
			
			@Override
			public void send(final Tuple t) {
				final CloudService service = t.getElementAt(CloudService.class , 2 );
				components[id].execute(service);
//				new Thread( new Runnable() {
//
//					@Override
//					public void run() {
//						try {
//							Thread.sleep((long) (service.getCPULoad()*Scenario.LOAD_TO_TIME_FACTOR));
//						} catch (Exception e) {
//						}
//						n.put( new Tuple( "DONE" , t.getElementAt(1)));
//					}
//					
//				}).start();
				Scenario.this.environment.schedule( new SimulationAction() {
					
					@Override
					public void doAction(double time) {
						System.out.println("Time: "+time+" Service "+service.getName()+" completed!");
						components[id].completed(service);
						n.put( new Tuple( "DONE" , t.getElementAt(1)));						
					}
					
				} , service.getCPULoad()*Scenario.LOAD_TO_TIME_FACTOR );				
				System.out.println("EXECUTINON SCHEDULED AT :"+(service.getCPULoad()*Scenario.LOAD_TO_TIME_FACTOR ));
			}
			
			@Override
			public Template getTemplate() {
				return new Template( new ActualTemplateField("SERVICE_CALL") , new FormalTemplateField(Integer.class) , new FormalTemplateField(CloudService.class));
			}
		};
	}

	public String getInfo() {
		double averageLoad = getAverageCPULoad();
		double averageMemory = getAverageMemoryUsage();
		return "CPU: "+averageLoad+" MEM: "+averageMemory;
	}

	private double getAverageCPULoad() {
		double totalLoad = 0.0;
		for(  int i=0 ; i<components.length ; i++ ) {
			totalLoad += components[i].getCPULoad();
		}	
		return totalLoad/components.length;
	}

	private double getAverageMemoryUsage() {
		double totalLoad = 0.0;
		for(  int i=0 ; i<components.length ; i++ ) {
			totalLoad += components[i].getMemoryLoad();
		}	
		return totalLoad/components.length;
	}

	public void setEnvironment(SimulationEnvironment env) {
		this.environment = env;
	}

}
