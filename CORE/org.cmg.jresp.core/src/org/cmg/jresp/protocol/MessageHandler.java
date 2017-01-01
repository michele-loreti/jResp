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

/**
 * This is the abstract class that, imlementing pattern Visitor, defines the
 * behaviour of a generic message handler.
 * 
 * 
 * @author Michele Loreti
 *
 */
public abstract class MessageHandler {

	public abstract void handle(AttributeRequest msg) throws IOException, InterruptedException;

	public abstract void handle(AttributeReply msg) throws IOException, InterruptedException;

	public abstract void handle(PutRequest msg) throws IOException, InterruptedException;

	public abstract void handle(GetRequest msg) throws IOException, InterruptedException;

	public abstract void handle(QueryRequest msg) throws IOException, InterruptedException;

	public abstract void handle(TupleReply msg) throws IOException, InterruptedException;

	public abstract void handle(Ack msg) throws IOException;

	public abstract void handle(GroupGetRequest msg) throws IOException, InterruptedException;

	public abstract void handle(GroupQueryRequest msg) throws IOException, InterruptedException;

	public abstract void handle(GroupPutRequest msg) throws IOException, InterruptedException;

	public abstract void handle(GroupGetReply msg) throws IOException, InterruptedException;

	public abstract void handle(GroupQueryReply msg) throws IOException, InterruptedException;

	public abstract void handle(GroupPutReply msg) throws IOException, InterruptedException;

	public abstract void handle(Fail msg) throws IOException;

	public final void receive(jRESPMessage msg) throws IOException, InterruptedException {
		msg.accept(this);
	}

}
