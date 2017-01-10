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

import org.cmg.jresp.knowledge.AbstractSensor;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;

/**
 * @author loreti
 *
 */
public class CloudComponent {
	
	public static Template MEMORY_SENSOR_TEMPLATE = 
			new Template( 
				new ActualTemplateField("MEMORY") , 
				new FormalTemplateField(Integer.class)
			);
	
	public static Template CPU_SENSOR_TEMPLATE = 
			new Template( 
				new ActualTemplateField("CPU") , 
				new FormalTemplateField(Double.class)
			);
	
	private int id;
	
	private int availableMemory;

	private int totalMemory;
	
	private double cpuLoad;
	
	private double cpuRate;

	private AbstractSensor memorySensor;

	private AbstractSensor cpuSensor;
	
	public CloudComponent( int id , int memory ) {
		this( id , memory , 1.0 );
	}

	public CloudComponent( int id , int memory, double cpuRate ) {
		this.id = id;
		this.totalMemory = memory;
		this.availableMemory = memory;
		this.cpuRate = cpuRate;
		this.memorySensor = new AbstractSensor( "MEMORY" , CloudComponent.MEMORY_SENSOR_TEMPLATE ) {
		};
		this.cpuSensor = new AbstractSensor( "CPU_LOAD" , CloudComponent.CPU_SENSOR_TEMPLATE ) {			
		};
		updateSensorsValue();
	}
	
	public int getId() {
		return id;
	}
	
	private void updateSensorsValue() {
		this.memorySensor.setValue( new Tuple( "MEMORY" , availableMemory ));
		this.cpuSensor.setValue( new Tuple( "CPU" , cpuLoad ));
	}
	
	public int getAvailableMemory() {
		return availableMemory;
	}
	
	public int getTotalMemory() {
		return totalMemory;
	}
	
	public double getCPURate() {
		return cpuRate;
	}
	
	public double getCPULoad() {
		return cpuLoad;
	}
	
	public synchronized boolean execute( CloudService s ) {
		if (s.getMemory()>availableMemory) {
			return false;
		}
		if ((s.getCPULoad()/cpuRate)>(100-cpuLoad)) {
			return false;
		}
		availableMemory -= s.getMemory();
		cpuLoad += s.getCPULoad()*cpuRate;
		updateSensorsValue();
		return true;
	}
	
	public synchronized boolean completed( CloudService s ) {
		availableMemory += s.getMemory();
		if (availableMemory>totalMemory) {
			availableMemory = totalMemory;
		}
		cpuLoad -= s.getCPULoad()*cpuRate;
		if (cpuLoad<0.0) {
			cpuLoad = 0.0;
		}
		updateSensorsValue();
		return true;
	}

	public AbstractSensor getMemorySensor() {
		return memorySensor;
	}

	public AbstractSensor getCpuSenros() {
		return cpuSensor;
	}

	public double getMemoryLoad() {
		return (totalMemory - availableMemory)/((double) totalMemory);
	}
	
	
}

