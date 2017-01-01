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
public class PointToPoint implements Target {

	protected String name;
	protected Address address;

	public PointToPoint(String name, Address address) {
		this.name = name;
		this.address = address;
	}

	@Override
	public boolean isSelf() {
		return false;
	}

	public Address getAddress() {
		return address;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof PointToPoint) {
			PointToPoint t = (PointToPoint) arg0;
			return name.equals(t.name) && address.equals(t.address);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode() ^ address.hashCode();
	}

	@Override
	public String toString() {
		return name + "@" + "[" + address + "]";
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isAGroup() {
		return false;
	}

}
