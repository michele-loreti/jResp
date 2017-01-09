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
public class jRESPSimulator {
	
	protected SimulationFactory factory;
	
	public jRESPSimulator( SimulationFactory factory ) {
		this.factory = factory;
	}
	
	public void run(double deadline) {
		SimulationEnvironment environment = factory.getSimulationEnvironment();
		environment.simulate( deadline );
	}
	

}
