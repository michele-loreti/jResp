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
package org.cmg.jresp.topology;

import rice.p2p.commonapi.Id;

/**
 * @author Michele Loreti
 *
 */
public class ScribePortAddress extends Address {

	private Id id;

	public ScribePortAddress(Id id) {
		super(id.toStringFull());
		this.id = id;
	}

	public Id getId() {
		return id;
	}

	public String toString() {
		return id.toStringFull();
	}

}
