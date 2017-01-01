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
import java.io.Serializable;

import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.PointToPoint;

/**
 * Identifies a generic Message.
 * 
 * @author Michele Loreti
 *
 */
public abstract class jRESPMessage implements Serializable {

	private PointToPoint source;
	private int session;
	private MessageType type;

	/**
	 * Creates a new object instance.
	 * 
	 * @param type
	 *            message type
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 */
	public jRESPMessage(MessageType type, PointToPoint source, int session) {
		this.source = source;
		this.session = session;
		this.type = type;
	}

	/**
	 * Returns the address of the node originating the message.
	 * 
	 * @return the address of the node originating the message.
	 */
	public PointToPoint getSource() {
		return source;
	}

	/**
	 * Returns the session identifier associated to the message.
	 * 
	 * @return the session identifier associated to the message.
	 */
	public int getSession() {
		return session;
	}

	/**
	 * Returns the message type.
	 * 
	 * @return the message type.
	 */
	public MessageType getType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof jRESPMessage) {
			jRESPMessage msg = (jRESPMessage) obj;
			return (type == msg.type) && (source.equals(msg.source)) && (session == msg.session);
		}
		return false;
	}

	@Override
	public String toString() {
		return source + ":" + session;
	}

	/**
	 * This method is used to pass the message to the right method in the
	 * message handler.
	 * 
	 * @param messageHandler
	 *            message handler
	 * @throws IOException
	 *             if a communication error occurs
	 * @throws InterruptedException
	 *             if the current thread is interrupted
	 */
	public abstract void accept(MessageHandler messageHandler) throws IOException, InterruptedException;

	@Override
	public int hashCode() {
		return type.hashCode() ^ source.hashCode() ^ session;
	}

	public GroupPredicate getGroupPredicate() {
		return null;
	}

}