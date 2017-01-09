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
import java.util.Arrays;

import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.PointToPoint;

/**
 * This message identifies a reply to a {@ling GroupGetRequest}.
 * 
 * @author Michele Loreti
 *
 */
public class GroupGetReply extends UnicastMessage {

	private Attribute[] values;
	private int tupleSession;
	private Tuple tuple;

	/**
	 * Creates a new message.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param target
	 *            name of the target node
	 * @param tupleSession
	 *            an integer value that will be used to confirm the tuple
	 *            removement
	 * @param values
	 *            attribute values assoicated to the reply
	 * @param tuple
	 *            available tuple
	 */
	public GroupGetReply(PointToPoint source, int session, String target, int tupleSession, Attribute[] values,
			Tuple tuple) {
		super(MessageType.GROUP_GET_REPLY, source, session, target);
		this.tupleSession = tupleSession;
		this.values = values;
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
	 * Return the session id that will be used to confirm tuple removement.
	 * 
	 * @return the session id that will be used to confirm tuple removement.
	 */
	public int getTupleSession() {
		return tupleSession;
	}

	/**
	 * Returns attributes values associated to the reply.
	 * 
	 * @return attributes values associated to the reply.
	 */
	public Attribute[] getAttributes() {
		return values;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			GroupGetReply ggr = (GroupGetReply) obj;
			return (tupleSession == ggr.tupleSession) && Arrays.deepEquals(values, ggr.values)
					&& tuple.equals(ggr.tuple);
		}
		return false;
	}

	@Override
	public String toString() {
		return getType() + "[" + super.toString() + "," + tupleSession + " , " + Arrays.toString(values) + " , "
				+ tuple.toString() + "]";
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ tupleSession ^ Arrays.hashCode(values) ^ tuple.hashCode();
	}

	/**
	 * Returns the tuple that can be potentially retrieved with the get action.
	 * 
	 * @return the tuple that can be potentially retrieved with the get action.
	 */
	public Tuple getTuple() {
		return tuple;
	}

}
