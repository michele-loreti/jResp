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

import org.cmg.jresp.topology.PointToPoint;

/**
 * Identifies an acknowledge message. This message is sent to notify that the
 * execution of a previous requested action is failed.
 * 
 * @author Michele Loreti
 *
 */
public class Fail extends UnicastMessage {

	private String message;

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
	public Fail(PointToPoint source, int session, String target, String message) {
		super(MessageType.FAIL, source, session, target);
		this.message = message;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && message.equals(((Fail) obj).message);
	}

	@Override
	public String toString() {
		return getType() + "[ " + super.toString() + " , " + getMessage() + " ]";
	}

	@Override
	public void accept(MessageHandler messageHandler) throws IOException {
		messageHandler.handle(this);
	}

	public String getMessage() {
		return message;
	}

}
