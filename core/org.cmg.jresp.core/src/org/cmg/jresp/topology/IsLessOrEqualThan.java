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

import java.util.HashMap;

import org.cmg.jresp.knowledge.Attribute;

/**
 * @author Michele Loreti
 *
 */
public class IsLessOrEqualThan extends GroupPredicate {

	private Number value;
	private String attribute;

	public IsLessOrEqualThan(String attribute, Number value) {
		super(GroupPredicate.PredicateType.ISLEQ);
		this.attribute = attribute;
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.topology.GroupPredicate#evaluate(java.util.HashMap)
	 */
	@Override
	public boolean evaluate(HashMap<String, Attribute> data) {
		Attribute a = data.get(attribute);
		Object v = (a != null ? a.getValue() : null);
		if ((v != null) && (v instanceof Number)) {
			return ((Number) v).doubleValue() <= value.doubleValue();
		}
		return false;
	}

	public String getAttribute() {
		return attribute;
	}

	public Number getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.topology.GroupPredicate#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (super.equals(obj)) {
			IsLessOrEqualThan p = (IsLessOrEqualThan) obj;
			if (!this.attribute.equals(p.attribute)) {
				return false;
			}
			return (this.value == p.value) || ((this.value != null) && (this.value.equals(p.value)));
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return attribute.hashCode() ^ (value == null ? 0 : value.hashCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return attribute + ">" + value;
	}

}
