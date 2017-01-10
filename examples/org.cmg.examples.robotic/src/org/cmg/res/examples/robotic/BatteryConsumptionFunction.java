/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universita'Universita' di Firenze
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
public interface BatteryConsumptionFunction {

	public double nextBatteryLevel( double dt , double battery , double speed ); 
	
}
