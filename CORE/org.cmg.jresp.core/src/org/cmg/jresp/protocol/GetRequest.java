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

import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.topology.PointToPoint;

/**
 * This message is sent when a node asks for the execution of a get action.
 * 
 * @author Michele Loreti
 *
 */
public class GetRequest extends UnicastMessage {

	/**
	 * Template associated to the get.
	 */
	private Template template;

	/**
	 * Creates a new message.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param target
	 *            name of the target node
	 * @param template
	 *            get template
	 */
	public GetRequest(PointToPoint source, int session, String target, Template template) {
		super(MessageType.GET_REQUEST, source, session, target);
		this.template = template;
	}

	/**
	 * Returns the template associated to the get.
	 * 
	 * @return the template associated to the get.
	 */
	public Template getTemplate() {
		return template;
	}

	@Override
	public void accept(MessageHandler messageHandler) throws IOException, InterruptedException {
		messageHandler.handle(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return template.equals((((GetRequest) obj).template));
		}
		return false;
	}

	@Override
	public String toString() {
		return getType() + "[ " + super.toString() + " , " + template.toString() + " ]";
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ template.hashCode();
	}

}