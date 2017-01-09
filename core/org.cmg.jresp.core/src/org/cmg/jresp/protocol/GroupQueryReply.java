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
import org.cmg.jresp.topology.PointToPoint;

/**
 * This is message identifies the replay to a {@link GroupQueryRequest}.
 * 
 * @author Michele Loreti
 *
 */
public class GroupQueryReply extends UnicastMessage {

	private Tuple tuple;

	/**
	 * Creates a new object instance.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param target
	 *            name of target node
	 * @param tupleSession
	 *            an integer value that will be used to confirm the execution of
	 *            put.
	 * @param values
	 *            values of attributes contained in the associated
	 *            {@link GroupPutRequest}
	 */
	public GroupQueryReply(PointToPoint source, int session, String target, Tuple tuple) {
		super(MessageType.GROUP_QUERY_REPLY, source, session, target);
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
	 * Returns the result of the query.
	 * 
	 * @return the result of the query.
	 */
	public Tuple getTuple() {
		return tuple;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			GroupQueryReply gqr = (GroupQueryReply) obj;
			return tuple.equals(gqr.tuple);
		}
		return false;
	}

	@Override
	public String toString() {
		return getType() + "[" + super.toString() + "," + tuple.toString() + "]";
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ tuple.hashCode();
	}

}
