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
public class Group implements Target {

	private GroupPredicate predicate;

	public Group(GroupPredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean isSelf() {
		return false;
	}

	public GroupPredicate getPredicate() {
		return predicate;
	}

	@Override
	public boolean isAGroup() {
		return true;
	}

}
