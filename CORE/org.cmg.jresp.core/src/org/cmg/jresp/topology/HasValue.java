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

import java.util.HashMap;

import org.cmg.jresp.knowledge.Attribute;

/**
 * @author Michele Loreti
 *
 */
public class HasValue extends GroupPredicate {

	private Object value;

	private String attribute;

	public HasValue(String attribute, Object value) {
		super(GroupPredicate.PredicateType.ISEQUAL);
		this.attribute = attribute;
		this.value = value;
	}

	@Override
	public boolean evaluate(HashMap<String, Attribute> data) {
		Attribute a = data.get(attribute);
		if (a == null) {
			return false;
		}
		Object o = a.getValue();
		return (value == o) || ((value != null) && (value.equals(o)));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof HasValue) {
			HasValue hv = (HasValue) obj;
			return attribute.equals(hv.attribute) && value.equals(hv.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return attribute.hashCode() ^ value.hashCode();
	}

	@Override
	public String toString() {
		return "{" + attribute + "==" + value + "}";
	}

	public String getAttribute() {
		return attribute;
	}

	public Object getValue() {
		return value;
	}

}
