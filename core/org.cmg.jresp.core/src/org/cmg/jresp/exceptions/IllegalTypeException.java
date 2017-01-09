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
 * Thrown when a type error is caught.
 * 
 * @author Michele Loroeti
 *
 */
public class IllegalTypeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance identifying a wrong cast from class was to class
	 * expected.
	 * 
	 * @param expected
	 *            expected java type
	 * @param was
	 *            current java type
	 */
	public IllegalTypeException(Class<?> expected, Class<?> was) {
		super("Illegal type exception: expected " + expected.getName() + " was " + was.getName());
	}

}
