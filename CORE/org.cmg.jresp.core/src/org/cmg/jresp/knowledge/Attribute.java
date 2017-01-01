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
package org.cmg.jresp.knowledge;

import java.io.Serializable;

/**
 * Indicates a generic attribute. This is a named value that can be
 * automatically extracted from knowledge or explicitly associated to a node.
 * 
 * @author Michele Loreti
 *
 */
public class Attribute implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Attribute name
	 */
	private String name;

	/**
	 * Attribute value
	 */
	private Object value;

	/**
	 * Creates a new attribute with specific name and value.
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public Attribute(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Returns attribute name.
	 * 
	 * @return attribute name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns attribute value
	 * 
	 * @return attribute value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns attribute type
	 * 
	 * @return attribute type
	 */
	public Class<?> getType() {
		return value.getClass();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Attribute) {
			Attribute a = (Attribute) obj;
			return name.equals(a.name) && ((value == a.value) || ((value != null) && (value.equals(a.value))));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (value == null ? name.hashCode() : name.hashCode() ^ value.hashCode());
	}

	@Override
	public String toString() {
		return name + "=" + value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
