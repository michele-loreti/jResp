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
package org.cmg.jresp.comp;

import java.util.HashMap;

/**
 * @author Michele Loreti
 *
 */
public class NodeInterface {

	protected INode node;

	protected HashMap<String, AttributeCollector> attributes;

	public NodeInterface(INode node) {
		this.node = node;
	}

}
