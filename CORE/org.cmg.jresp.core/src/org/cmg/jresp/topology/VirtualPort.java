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
package org.cmg.jresp.topology;

import java.io.IOException;

import org.cmg.jresp.comp.NodeConnection;
import org.cmg.jresp.protocol.UnicastMessage;
import org.cmg.jresp.protocol.jRESPMessage;

/**
 * @author Michele Loreti
 *
 */
public class VirtualPort extends AbstractPort {

	private VirtualPortAddress address;
	private NodeConnection connection;

	public VirtualPort(int portId, NodeConnection connection) {
		super();
		this.address = new VirtualPortAddress(portId);
		this.connection = connection;
	}

	public VirtualPort(int portId) {
		this(portId, null);
	}

	@Override
	public boolean canSendTo(Target l) {
		return l.isAGroup() || ((l instanceof PointToPoint) && (((PointToPoint) l).getAddress().equals(getAddress())));
	}

	@Override
	protected synchronized void send(Address address, UnicastMessage message) throws IOException, InterruptedException {
		if (this.address.equals(address)) {
			MessageDispatcher n = nodes.get(message.getTarget());
			if (n != null) {
				if (connection != null) {
					connection.waitInTouch(message.getSource().getName(), message.getTarget());
				}
				n.addMessage(message);
			} else {
				sendFail(message.getSource(), message.getSource().getName(), message.getSession(),
						"Node " + message.getTarget() + " is unknown at " + getAddress());
			}

		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected synchronized void send(jRESPMessage m) {
		for (MessageDispatcher n : nodes.values()) {
			if ((connection == null) || (connection.areInTouch(m.getSource().getName(), n.getName()))) {
				n.addMessage(m);
			}
		}
	}

	@Override
	public Address getAddress() {
		return address;
	}

}
