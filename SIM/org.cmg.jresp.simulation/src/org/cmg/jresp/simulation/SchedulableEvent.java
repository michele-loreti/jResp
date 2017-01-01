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

/**
 * @author loreti
 *
 */
public abstract class SchedulableEvent implements Comparable<SchedulableEvent> {
	
	
	protected double time;
	
	private int priority;
	
	public SchedulableEvent( double time ) {
		this( time , 0 );
	}
	
	
	public SchedulableEvent( double time , int priority ) {
		this.priority = priority;
		this.time = time;
	}


	public double getTime() {
		return time;
	}
	
	public abstract void execute();


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SchedulableEvent arg0) {
		if (this.time<arg0.time) {
			return -1;
		}
		if (this.time == arg0.time) {
			return this.priority - arg0.priority;
		}
		return 1;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[ time: "+time+" , priority: "+priority+" ]";
	}


	public void shift(double currentTime) {
		time+=currentTime;
	}


	public abstract void cancelled();

}
