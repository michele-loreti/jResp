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

import org.cmg.jresp.topology.MessageSender;

/**
 * Thrown when a node with an already existing name is registered to a port.
 * 
 * @author Michele Loreti
 *
 */
public class DuplicateNameException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new class instance.
	 * 
	 * @param port
	 *            involved port
	 * @param name
	 *            duplcated name
	 */
	public DuplicateNameException(MessageSender port, String name) {
		super(" Name " + name + " is already used at port " + port);
	}

	public DuplicateNameException(String clientName) {
		// TODO Auto-generated constructor stub
	}

}
