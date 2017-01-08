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
package org.cmg.test.json;

/**
 * @author Michele Loreti
 *
 */
public class AnObject {

	private int x;
	private int y;

	public AnObject(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof AnObject) {
			AnObject o = (AnObject) arg0;
			return (x == o.x) && (y == o.y);
		}
		return false;
	}

	@Override
	public String toString() {
		return "[ x=" + x + " y=" + y + " ]";
	}

}
