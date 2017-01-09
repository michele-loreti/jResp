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
package org.cmg.jresp.simulation;

import java.util.HashMap;

/**
 * @author loreti
 *
 */
public class TimeSeries<T> {

	protected HashMap<Double,T> data;
	
	protected Measure<T> m;
	
	protected double sampling;

	private SimulationAction simulationaction;
	
	public TimeSeries( Measure<T> m , double sampling ) {
		this.data = new HashMap<Double,T>();
		this.m = m;
		this.sampling = sampling;
		this.simulationaction = new SimulationAction() {
			
			@Override
			public void doAction(double time) {
				data.put(time, getMeasure().eval());
			}
		};
	}
	
	protected Measure<T> getMeasure() {
		return m;
	}

	public SimulationAction getSimulationAction() {
		return simulationaction;
	}
 	
}
