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
package org.cmg.jresp.topology;

/**
 * @author Michele Loreti
 *
 */
public class VirtualPortAddress extends Address {

	public static final String ADDRESS_CODE = "virtual";
	private int id;

	public VirtualPortAddress(int id) {
		super(ADDRESS_CODE);
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VirtualPortAddress) {
			VirtualPortAddress vpa = (VirtualPortAddress) obj;
			return this.id == vpa.id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ADDRESS_CODE.hashCode() ^ id;
	}

	@Override
	public String toString() {
		return ADDRESS_CODE + ":" + id;
	}

	public int getId() {
		return id;
	}

}
