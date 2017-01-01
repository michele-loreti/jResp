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
 * This message is a reply to either a {@link PutRequest} or to a
 * {@link QueryRequest}.
 * 
 * @author Michele Loreti
 *
 */
public class TupleReply extends UnicastMessage {

	private Tuple tuple;

	/**
	 * Creates a new object instance.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param target
	 *            name of the target node
	 * @param tuple
	 *            returned tuple
	 */
	public TupleReply(PointToPoint source, int session, String target, Tuple tuple) {
		super(MessageType.TUPLE_REPLY, source, session, target);
		this.tuple = tuple;
	}

	/**
	 * Returns the tuple enclosed in the message
	 * 
	 * @return the tuple enclosed in the message
	 */
	public Tuple getTuple() {
		return tuple;
	}

	@Override
	public void accept(MessageHandler messageHandler) throws IOException, InterruptedException {
		messageHandler.handle(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return tuple.equals(((TupleReply) obj).tuple);
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