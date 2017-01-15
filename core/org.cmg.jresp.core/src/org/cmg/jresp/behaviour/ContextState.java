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
 * Identifies context status.
 * 
 * @author Michele Loreti
 *
 */
public enum ContextState {
	/**
	 * The context is ready to start. Execution of an agent in a READY context
	 * is blocked until state RUNNING is reached.
	 */
	READY,

	/**
	 * Context is running and all the enclosing agents are involved in their
	 * computations.
	 */
	RUNNING,

	/**
	 * A closing signal has been received. In this state, no action can be
	 * performed.
	 */
	CLOSING,

	/**
	 * Context execution is terminated.
	 */
	HALT
}
