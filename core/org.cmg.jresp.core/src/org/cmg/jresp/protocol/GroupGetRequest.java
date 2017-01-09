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
 * Identify a message sent to retrieve a tuple from a gruop of nodes.
 * 
 * @author Michele Loreti
 *
 */
public class GroupGetRequest extends jRESPMessage {

	/**
	 * Get template.
	 * 
	 */
	private Template template;

	/**
	 * Attributes used to select target nodes.
	 */
	private GroupPredicate groupPredicate;

	/**
	 * Crate a new instance.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param template
	 *            get template
	 * @param groupPredicate
	 *            attribute values
	 */
	public GroupGetRequest(PointToPoint source, int session, Template template, GroupPredicate groupPredicate) {
		super(MessageType.GROUP_GET_REQUEST, source, session);
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
	 * Returns template of get action
	 * 
	 * @return template of get action
	 */
	public Template getTemplate() {
		return template;
	}

	/**
	 * Returns the predicate used to identify target nodes.
	 * 
	 * @return the predicate used to identify target nodes.
	 */
	@Override
	public GroupPredicate getGroupPredicate() {
		return groupPredicate;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			GroupGetRequest ggr = (GroupGetRequest) obj;
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
