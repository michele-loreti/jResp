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

import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.protocol.jRESPMessage;

/**
 * @author Michele Loreti
 *
 */
public interface MessageSender {

	boolean canSendTo(Target l);

	void sendTuple(PointToPoint to, String name, int session, Tuple tuple) throws IOException, InterruptedException;

	void sendAck(PointToPoint to, String name, int session) throws IOException, InterruptedException;

	void sendFail(PointToPoint to, String name, int session, String message) throws IOException, InterruptedException;

	void sendAttributes(PointToPoint to, String name, int session, Attribute[] attributes)
			throws IOException, InterruptedException;

	void sendPutRequest(PointToPoint l, String name, int session, Tuple t) throws IOException, InterruptedException;

	void sendGetRequest(PointToPoint l, String name, int session, Template t) throws IOException, InterruptedException;

	void sendQueryRequest(PointToPoint l, String name, int session, Template t)
			throws IOException, InterruptedException;

	void sendGroupPutRequest(String name, int session, GroupPredicate groupPredicate, Tuple t)
			throws IOException, InterruptedException;

	void sendGroupGetRequest(String name, int session, GroupPredicate groupPredicate, Template t)
			throws IOException, InterruptedException;

	void sendGroupQueryRequest(String name, int session, GroupPredicate groupPredicate, Template t)
			throws IOException, InterruptedException;

	void sendGroupPutReply(PointToPoint l, String name, int session, int tupleSession)
			throws IOException, InterruptedException;

	void sendGroupGetReply(PointToPoint l, String name, int session, int tupleSession, Attribute[] attributes, Tuple t)
			throws IOException, InterruptedException;

	void sendGroupQueryReply(PointToPoint l, String name, int session, Tuple t)
			throws IOException, InterruptedException;

	void deliver(jRESPMessage msg) throws IOException, InterruptedException;

	void sendAttributeRequest(PointToPoint l, String name, int session, String[] attrs)
			throws IOException, InterruptedException;
}
