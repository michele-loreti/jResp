/**
 * Copyright (c) 2012 Sysma and CMG Group.
 * - IMT Institute for Advanced Studies Lucca
 * - Universit� di Firenze
 * 
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 *      Francesco Tiezzi
 *      Andrea Vandin
 */
package org.cmg.jresp.examples.emobility;

import java.util.LinkedList;

/**
 * @author loreti
 *
 */
public class Scenario {

	/**
	 * Available parking lots.
	 */
	private ParkingLot[] parkingLots;
	
	/**
	 * Expected trip time between two parking lots.
	 */
	private double[][] tripTime;
	
	/**
	 * Running vehicles.
	 */
	private LinkedList<Vehicle> veicles; 
	
	/**
	 * Create a new instance of a Scenario.
	 * 
	 * @param parkingLot available parking lots
	 * @param tripTime matrix of trip times
	 */
	public Scenario( ParkingLot[] parkingLots , double[][] tripTime ) {
		this.parkingLots = parkingLots; 
		this.tripTime = tripTime;
	}

	/**
	 * Return the expected time needed to move from src to target.
	 * 
	 * @param src id of starting parking lot
	 * @param target id of destination parking log
	 * @return expected trip time
	 */
	public double getTripTime( int src , int target ) {
		return tripTime[src][target];
	}
	
	
	
	
}
