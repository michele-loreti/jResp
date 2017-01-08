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
package org.cmg.jresp.test.json;

import java.util.Arrays;

/**
 * @author Michele Loreti
 *
 */
public class AnObjectWithArrays {

	private AnObject[] data;

	public AnObjectWithArrays(AnObject... data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof AnObjectWithArrays) {
			AnObjectWithArrays o = (AnObjectWithArrays) arg0;
			return Arrays.deepEquals(data, o.data);
		}
		return false;
	}

	@Override
	public String toString() {
		return "[ data=" + Arrays.toString(data) + " ]";
	}

}
