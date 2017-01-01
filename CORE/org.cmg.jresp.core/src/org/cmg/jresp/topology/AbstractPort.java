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
import java.util.Hashtable;

import org.cmg.jresp.exceptions.DuplicateNameException;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.protocol.Ack;
import org.cmg.jresp.protocol.AttributeReply;
import org.cmg.jresp.protocol.AttributeRequest;
import org.cmg.jresp.protocol.Fail;
import org.cmg.jresp.protocol.GetRequest;
import org.cmg.jresp.protocol.GroupGetReply;
import org.cmg.jresp.protocol.GroupGetRequest;
import org.cmg.jresp.protocol.GroupPutReply;
import org.cmg.jresp.protocol.GroupPutRequest;
import org.cmg.jresp.protocol.GroupQueryReply;
import org.cmg.jresp.protocol.GroupQueryRequest;
import org.cmg.jresp.protocol.PutRequest;
import org.cmg.jresp.protocol.QueryRequest;
import org.cmg.jresp.protocol.TupleReply;
import org.cmg.jresp.protocol.UnicastMessage;
import org.cmg.jresp.protocol.jRESPMessage;

/**
 * An <code>AbstractPort</code> is used to identify a generic communication
 * channel that nodes use to interact with each other.
 * 
 * @author Michele Loreti
 *
 */
public abstract class AbstractPort implements MessageSender, MessageReceiver {

	/**
	 * A table mapping names to dispatchers.
	 */
	Hashtable<String, MessageDispatcher> nodes;

	/**
	 * Constructs a new <code>AbstractPort</code>.
	 */
	public AbstractPort() {
		this.nodes = new Hashtable<String, MessageDispatcher>();
	}

	@Override
	public abstract boolean canSendTo(Target l);

	@Override
	public void sendTuple(PointToPoint target, String name, int session, Tuple tuple)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(target.getAddress(), new TupleReply(source, session, target.getName(), tuple));
	}

	/**
	 * Concrete extensions of <code>AbstractPort</code> has to implement this
	 * method to provide the actual point-to-point communication mechanism.
	 * Indeed, this method is used to send message <code>message</code> to node
	 * at address <code>address</code>.
	 * 
	 * @param address
	 *            receiver address
	 * @param message
	 *            message
	 * @throws IOException
	 *             is thrown when an I/O error occurs in the communciation
	 * @throws InterruptedException
	 *             is thrown when the thread is interrupted while is is waiting
	 *             for action completion.
	 */
	protected abstract void send(Address address, UnicastMessage message) throws IOException, InterruptedException;

	/**
	 * Concrete extensions of <code>AbstractPort</code> has to implement this
	 * method to provide the actual communication mechanism.
	 *
	 * @param m
	 *            sent message
	 * @throws IOException
	 *             is thrown when an I/O error occurs in the communciation
	 * @throws InterruptedException
	 *             is thrown when the thread is interrupted while is is waiting
	 *             for action completion.
	 */
	protected abstract void send(jRESPMessage m) throws IOException, InterruptedException;

	/**
	 * Returns port address. The address obtained by this method is attached to
	 * each message sent through the port.
	 * 
	 * @return port address.
	 */
	public abstract Address getAddress();

	@Override
	public void sendAck(PointToPoint target, String name, int session) throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(target.getAddress(), new Ack(source, session, target.getName()));
	}

	@Override
	public void sendFail(PointToPoint target, String name, int session, String message)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(target.getAddress(), new Fail(source, session, target.getName(), message));
	}

	@Override
	public void sendAttributes(PointToPoint target, String name, int session, Attribute[] attributes)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(target.getAddress(), new AttributeReply(source, session, target.getName(), attributes));
	}

	@Override
	public void sendPutRequest(PointToPoint target, String name, int session, Tuple t)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(target.getAddress(), new PutRequest(source, session, target.getName(), t));
	}

	@Override
	public void sendGetRequest(PointToPoint target, String name, int session, Template t)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(target.getAddress(), new GetRequest(source, session, target.getName(), t));
	}

	@Override
	public void sendQueryRequest(PointToPoint target, String name, int session, Template t)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(target.getAddress(), new QueryRequest(source, session, target.getName(), t));
	}

	public synchronized void register(MessageDispatcher n) {
		if (nodes.contains(n.getName())) {
			throw new DuplicateNameException(this, n.getName());
		}
		nodes.put(n.getName(), n);
	}

	@Override
	public synchronized void receiveMessage(jRESPMessage m) throws InterruptedException {
		if (m instanceof UnicastMessage) {
			receiveUnicastMessage((UnicastMessage) m);
		} else {
			for (MessageDispatcher n : nodes.values()) {
				n.addMessage(m);
			}
		}
	}

	@Override
	public synchronized void receiveUnicastMessage(UnicastMessage m) throws InterruptedException {
		String target = m.getTarget();
		MessageDispatcher targetNode = nodes.get(target);
		if (targetNode == null) {
			try {
				sendFail(m.getSource(), null, m.getSession(), "Node " + target + " is unknown at " + getAddress());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		targetNode.addMessage(m);
	}

	@Override
	public void sendGroupPutRequest(String name, int session, GroupPredicate groupPredicate, Tuple t)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(new GroupPutRequest(source, session, groupPredicate, t));
	}

	@Override
	public void sendGroupGetRequest(String name, int session, GroupPredicate groupPredicate, Template t)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(new GroupGetRequest(source, session, t, groupPredicate));
	}

	@Override
	public void sendGroupQueryRequest(String name, int session, GroupPredicate groupPredicate, Template t)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(new GroupQueryRequest(source, session, t, groupPredicate));
	}

	@Override
	public void sendGroupPutReply(PointToPoint l, String name, int session, int tupleSession)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(l.getAddress(), new GroupPutReply(source, session, l.getName(), tupleSession));
	}

	@Override
	public void sendGroupGetReply(PointToPoint l, String name, int session, int tupleSession, Attribute[] attributes,
			Tuple t) throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(l.getAddress(), new GroupGetReply(source, session, l.getName(), tupleSession, attributes, t));
	}

	@Override
	public void sendGroupQueryReply(PointToPoint l, String name, int session, Tuple t)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(l.getAddress(), new GroupQueryReply(source, session, l.getName(), t));
	}

	public void sendAttributeRequest(PointToPoint l, String name, int session, String[] attrs)
			throws IOException, InterruptedException {
		PointToPoint source = new PointToPoint(name, getAddress());
		send(l.getAddress(), new AttributeRequest(source, session, l.getName(), attrs));
	}

	@Override
	public String toString() {
		return getAddress().toString();
	}

	@Override
	public void deliver(jRESPMessage msg) throws IOException, InterruptedException {
		if (msg instanceof UnicastMessage) {
			send(getAddress(), (UnicastMessage) msg);
		} else {
			send(msg);
		}
	}

}
