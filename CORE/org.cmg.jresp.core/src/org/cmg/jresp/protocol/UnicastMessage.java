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
package org.cmg.jresp.protocol;

import org.cmg.jresp.topology.PointToPoint;

/**
 * Identifies a generic unicast message.
 * 
 * @author Michele Loreti
 *
 */
public abstract class UnicastMessage extends jRESPMessage {

	private String target;

	/**
	 * Creates a new object instance.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param target
	 *            name of the target node
	 */
	public UnicastMessage(MessageType type, PointToPoint source, int session, String target) {
		super(type, source, session);
		this.target = target;
	}

	/**
	 * Returns the name of the node that should receive the message.
	 * 
	 * @return the name of the node that should receive the message.
	 */
	public String getTarget() {
		return target;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnicastMessage) {
			return super.equals(obj) && (target.equals(((UnicastMessage) obj).target));
		}
		return false;
	}

	@Override
	public String toString() {
		return super.toString() + " , " + target;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ target.hashCode();
	}

}
