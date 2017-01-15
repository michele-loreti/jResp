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

import java.io.Serializable;
import java.util.HashMap;

import org.cmg.jresp.knowledge.Attribute;

/**
 * @author Michele Loreti
 *
 */

public abstract class GroupPredicate implements Serializable {

	public enum PredicateType {
		FALSE, TRUE, ISEQUAL, ISGTR, ISGEQ, ISLEQ, ISLES, AND, OR, NOT
	}

	private PredicateType type;

	public abstract boolean evaluate(HashMap<String, Attribute> data);

	public GroupPredicate(PredicateType type) {
		this.type = type;
	}

	public PredicateType getType() {
		return this.type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof GroupPredicate) {
			return this.type == ((GroupPredicate) obj).type;
		}
		return false;
	}

}

/*
 * 
 * public abstract class GroupPredicate {
 * 
 * protected String[] parameters;
 * 
 * protected GroupPredicate( String ... parameters ) { this.parameters =
 * parameters; }
 * 
 * public int size() { return parameters.length; }
 * 
 * public String getParameterName( int i ) { return parameters[i]; }
 * 
 * public abstract boolean evaluate( Attribute[] data );
 * 
 * public String[] getParameters() { return parameters; }
 * 
 * }
 */
