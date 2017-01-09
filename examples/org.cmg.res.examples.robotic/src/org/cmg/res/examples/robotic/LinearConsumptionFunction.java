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
package org.cmg.res.examples.robotic;

/**
 * @author loreti
 *
 */
public class LinearConsumptionFunction implements BatteryConsumptionFunction {
	
	private double dischargingFactor;

	public LinearConsumptionFunction( double dischargingFactor ) {
		this.dischargingFactor = dischargingFactor;
	}

	/* (non-Javadoc)
	 * @see org.cmg.res.examples.robotic.BatteryConsumptionFunction#nextBatteryLevel(double, double, double)
	 */
	@Override
	public double nextBatteryLevel(double dt, double battery, double speed) {
		double result = battery - dischargingFactor*dt;
		if (result < 0.0) {
			result = 0.0;
		}
		return result;
	}

}
