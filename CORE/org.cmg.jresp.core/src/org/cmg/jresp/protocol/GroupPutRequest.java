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

import java.io.IOException;

import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.PointToPoint;

/**
 * This message is sent when a node performs a put on a group of nodes.
 * 
 * @author Michele Loreti
 *
 */
public class GroupPutRequest extends jRESPMessage {

	/**
	 * Tuple argument of put action
	 */
	private Tuple tuple;

	/**
	 * Names of attributes used to select target nodes
	 */
	private GroupPredicate groupPredicate;

	/**
	 * Creates a new object instance.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param attributes
	 *            names of attributes used to select target nodes
	 * @param tuple
	 *            tuple to put
	 */
	public GroupPutRequest(PointToPoint source, int session, GroupPredicate groupPredicate, Tuple tuple) {
		super(MessageType.GROUP_PUT_REQUEST, source, session);
		this.groupPredicate = groupPredicate;
		this.tuple = tuple;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.scel.protocol.Message#accept(org.cmg.scel.protocol.
	 * MessageHandler)
	 */
	@Override
	public void accept(MessageHandler messageHandler) throws IOException, InterruptedException {
		messageHandler.handle(this);
	}

	/**
	 * Returns the names of attributes used to select target nodes.
	 * 
	 * @return the names of attributes used to select target nodes.
	 */
	@Override
	public GroupPredicate getGroupPredicate() {
		return groupPredicate;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			GroupPutRequest ggr = (GroupPutRequest) obj;
			return this.groupPredicate.equals(ggr.groupPredicate) && tuple.equals(ggr.tuple);
		}
		return false;
	}

	@Override
	public String toString() {
		return getType() + "[" + super.toString() + " , " + groupPredicate + " , " + tuple.toString() + " ]";
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ groupPredicate.hashCode() ^ tuple.hashCode();
	}

	/**
	 * Return the tuple argument of put action
	 * 
	 * @return the tuple argument of put action
	 */
	public Tuple getTuple() {
		return tuple;
	}

}
