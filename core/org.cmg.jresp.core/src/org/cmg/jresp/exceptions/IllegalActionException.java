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
package org.cmg.jresp.exceptions;

/**
 * Thrown when an action violating access rights is executed.
 * 
 * @author loreti
 * 
 *
 */
public class IllegalActionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new object instance.
	 * 
	 * @param message
	 *            a string describing the access violation.
	 */
	public IllegalActionException(String message) {
		super(message);
	}

}
