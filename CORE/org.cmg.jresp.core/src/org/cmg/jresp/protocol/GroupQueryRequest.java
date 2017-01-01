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
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.PointToPoint;

/**
 * This message is sent by a node that performs a query of on a group of nodes
 * satisfying a given predicate on attributes.
 * 
 * @author Michele Loreti
 *
 */
public class GroupQueryRequest extends jRESPMessage {

	private Template template;
	private GroupPredicate groupPredicate;

	/**
	 * 
	 * Creates a new object instance.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param template
	 *            query termplate
	 * @param groupPredicate
	 *            attribute names
	 */
	public GroupQueryRequest(PointToPoint source, int session, Template template, GroupPredicate groupPredicate) {
		super(MessageType.GROUP_QUERY_REQUEST, source, session);
		this.template = template;
		this.groupPredicate = groupPredicate;

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
	 * Returns the template used to perform the query.
	 * 
	 * @return the template used to perform the query.
	 */
	public Template getTemplate() {
		return template;
	}

	/**
	 * Returns the attribute names used to evaluate if a node is involved or not
	 * in the communication.
	 * 
	 * @return the attribute names used to evaluate if a node is involved or not
	 *         in the communication.
	 */
	@Override
	public GroupPredicate getGroupPredicate() {
		return groupPredicate;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			GroupQueryRequest ggr = (GroupQueryRequest) obj;
			return template.equals(ggr.template) && groupPredicate.equals(ggr.groupPredicate);
		}
		return false;
	}

	@Override
	public String toString() {
		return getType() + "[" + super.toString() + "," + template.toString() + " , " + groupPredicate + "]";
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ template.hashCode() ^ groupPredicate.hashCode();
	}

}
