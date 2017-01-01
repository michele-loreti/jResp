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
package org.cmg.jresp.behaviour;

/**
 * This interface identifies a generic agents container.
 * 
 * @author Michele Loreti
 *
 */
public interface AgentContainer {

	/**
	 * Adds agent <code>a</code> to the container.
	 * 
	 * @param a
	 *            agent to add into the container.
	 */
	public void addAgent(Agent a);

	/**
	 * Starts computation of all agents.
	 */
	public void start();

	/**
	 * Stops computation of all agents.
	 */
	public void stop();

	/**
	 * Generates a new agent identifier.
	 * 
	 * @return a new agent identifier.
	 */
	public int getAgentId();

}
