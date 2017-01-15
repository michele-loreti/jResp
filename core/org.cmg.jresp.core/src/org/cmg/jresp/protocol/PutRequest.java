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
 * This message is sent by a node that aims at performing a put action.
 * 
 * @author Michele Loreti
 *
 */
public class PutRequest extends UnicastMessage {

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
	 *            tuple to put
	 */
	public PutRequest(PointToPoint source, int session, String target, Tuple tuple) {
		super(MessageType.PUT_REQUEST, source, session, target);
		this.tuple = tuple;
	}

	/**
	 * Returns the tuple argument of the put action.
	 * 
	 * @return the tuple argument of the put action.
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
			return tuple.equals(((PutRequest) obj).tuple);
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
