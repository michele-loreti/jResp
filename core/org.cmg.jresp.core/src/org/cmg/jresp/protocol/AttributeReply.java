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
import org.cmg.jresp.topology.PointToPoint;

/**
 * This message is sent to reply to an {@ling AttributeRequest} and contains an
 * array of attribute.
 * 
 * @author Michele Loreti
 *
 */
public class AttributeReply extends UnicastMessage {

	/**
	 * Array of attributes.
	 */
	private Attribute[] values;

	/**
	 * Create a new object instance.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param target
	 *            name of the target node
	 * @param values
	 *            an array of attributes
	 */
	public AttributeReply(PointToPoint source, int session, String target, Attribute[] values) {
		super(MessageType.ATTRIBUTE_REPLY, source, session, target);
		this.values = values;
	}

	/**
	 * Returns the attributes stored in the message.
	 * 
	 * @return attributes stored in the message.
	 */
	public Attribute[] getValues() {
		return values;
	}

	@Override
	public void accept(MessageHandler messageHandler) throws IOException, InterruptedException {
		messageHandler.handle(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return Arrays.deepEquals(values, ((AttributeReply) obj).values);
		}
		return false;
	}

	@Override
	public String toString() {
		return getType() + "[ " + super.toString() + " , " + Arrays.toString(values) + "]";
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(values);
	}

}
