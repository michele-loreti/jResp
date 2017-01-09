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
 * This message is sent when a node aims at performing a query action.
 * 
 * @author Michele Loreti
 *
 */
public class QueryRequest extends UnicastMessage {

	private Template template;

	/**
	 * Creates a new object instance.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param target
	 *            name of the target node
	 * @param template
	 *            template used in the query action
	 */
	public QueryRequest(PointToPoint source, int session, String target, Template template) {
		super(MessageType.QUERY_REQUEST, source, session, target);
		this.template = template;
	}

	/**
	 * Returns the template used in the query action
	 * 
	 * @return the template used in the query action
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
			return template.equals(((QueryRequest) obj).template);
		}
		return false;
	}

	@Override
	public String toString() {
		return getType() + "[" + super.toString() + "," + template.toString() + "]";
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ template.hashCode();
	}

}