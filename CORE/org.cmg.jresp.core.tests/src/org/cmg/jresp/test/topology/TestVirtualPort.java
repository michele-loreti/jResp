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
package org.cmg.jresp.test.topology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.cmg.jresp.knowledge.ActualTemplateField;
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
import org.cmg.jresp.protocol.jRESPMessage;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.MessageDispatcher;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.VirtualPort;
import org.cmg.jresp.topology.VirtualPortAddress;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Michele Loreti
 *
 */
public class TestVirtualPort {

	public static final String NODE3 = "node3";

	public static final Tuple TUPLE = new Tuple("test");

	public static final Template TEMPLATE = new Template(new ActualTemplateField("test"));

	private static final String NODE2 = "node2";

	private static final VirtualPortAddress ADDRESS = new VirtualPortAddress(0);

	public static final String NODE1 = "node1";

	protected VirtualPort port = new VirtualPort(0);

	protected jRESPMessage receivedAtOne;

	protected jRESPMessage receivedAtTwo;

	protected MessageDispatcher mdOne = new MessageDispatcher() {

		@Override
		public String getName() {
			return NODE1;
		}

		@Override
		public void addMessage(jRESPMessage msg) {
			TestVirtualPort.this.receivedAtOne = msg;
		}
	};

	protected MessageDispatcher mdTwo = new MessageDispatcher() {

		@Override
		public String getName() {
			return NODE2;
		}

		@Override
		public void addMessage(jRESPMessage msg) {
			TestVirtualPort.this.receivedAtTwo = msg;
		}
	};

	@Before
	public void init() {
		port.register(mdOne);
		port.register(mdTwo);
	}

	@Test
	public void testSendAck() throws IOException, InterruptedException {
		port.sendAck(new PointToPoint(NODE2, ADDRESS), NODE1, 0);
		assertNull(receivedAtOne);
		assertEquals(new Ack(new PointToPoint(NODE1, ADDRESS), 0, NODE2), receivedAtTwo);
	}

	@Test
	public void testSendAckFail() throws IOException, InterruptedException {
		port.sendAck(new PointToPoint(NODE3, ADDRESS), NODE1, 0);
		assertNull(receivedAtTwo);
		assertEquals(
				new Fail(new PointToPoint(NODE1, ADDRESS), 0, NODE1, "Node " + NODE3 + " is unknown at " + ADDRESS),
				receivedAtOne);
	}

	@Test
	public void testSendTuple() throws IOException, InterruptedException {
		port.sendTuple(new PointToPoint(NODE2, ADDRESS), NODE1, 0, TUPLE);
		assertEquals(new TupleReply(new PointToPoint(NODE1, ADDRESS), 0, NODE2, TUPLE), receivedAtTwo);
	}

	@Test
	public void testSendFail() throws IOException, InterruptedException {
		port.sendFail(new PointToPoint(NODE2, ADDRESS), NODE1, 0, "error");
		assertNull(receivedAtOne);
		assertEquals(new Fail(new PointToPoint(NODE1, ADDRESS), 0, NODE2, "error"), receivedAtTwo);
	}

	@Test
	public void testSendAttributes() throws IOException, InterruptedException {
		port.sendAttributes(new PointToPoint(NODE2, ADDRESS), NODE1, 0, new Attribute[0]);
		assertNull(receivedAtOne);
		assertEquals(new AttributeReply(new PointToPoint(NODE1, ADDRESS), 0, NODE2, new Attribute[0]), receivedAtTwo);
	}

	@Test
	public void testSendAttributeRequest() throws IOException, InterruptedException {
		port.sendAttributeRequest(new PointToPoint(NODE2, ADDRESS), NODE1, 0, new String[] { "attr1", "attr2" });
		assertNull(receivedAtOne);
		assertEquals(
				new AttributeRequest(new PointToPoint(NODE1, ADDRESS), 0, NODE2, new String[] { "attr1", "attr2" }),
				receivedAtTwo);
	}

	@Test
	public void testSendPutRequest() throws IOException, InterruptedException {
		port.sendPutRequest(new PointToPoint(NODE2, ADDRESS), NODE1, 0, TUPLE);
		assertNull(receivedAtOne);
		assertEquals(new PutRequest(new PointToPoint(NODE1, ADDRESS), 0, NODE2, TUPLE), receivedAtTwo);
	}

	@Test
	public void testSendGetRequest() throws IOException, InterruptedException {
		port.sendGetRequest(new PointToPoint(NODE2, ADDRESS), NODE1, 0, TEMPLATE);
		assertNull(receivedAtOne);
		assertEquals(new GetRequest(new PointToPoint(NODE1, ADDRESS), 0, NODE2, TEMPLATE), receivedAtTwo);
	}

	@Test
	public void testSendQueryRequest() throws IOException, InterruptedException {
		port.sendQueryRequest(new PointToPoint(NODE2, ADDRESS), NODE1, 0, TEMPLATE);
		assertNull(receivedAtOne);
		assertEquals(new QueryRequest(new PointToPoint(NODE1, ADDRESS), 0, NODE2, TEMPLATE), receivedAtTwo);
	}

	@Test
	public void testSendGroupPutRequest() throws IOException, InterruptedException {
		port.sendGroupPutRequest(NODE3, 0, new HasValue("attr1", 32), TUPLE);
		assertEquals(new GroupPutRequest(new PointToPoint(NODE3, ADDRESS), 0, new HasValue("attr1", 32), TUPLE),
				receivedAtOne);
		assertEquals(new GroupPutRequest(new PointToPoint(NODE3, ADDRESS), 0, new HasValue("attr1", 32), TUPLE),
				receivedAtTwo);
	}

	@Test
	public void testSendGroupGetRequest() throws IOException, InterruptedException {
		port.sendGroupGetRequest(NODE3, 0, new HasValue("attr1", 32), TEMPLATE);
		assertEquals(new GroupGetRequest(new PointToPoint(NODE3, ADDRESS), 0, TEMPLATE, new HasValue("attr1", 32)),
				receivedAtOne);
		assertEquals(new GroupGetRequest(new PointToPoint(NODE3, ADDRESS), 0, TEMPLATE, new HasValue("attr1", 32)),
				receivedAtTwo);
	}

	@Test
	public void testSendGroupQueryRequest() throws IOException, InterruptedException {
		port.sendGroupQueryRequest(NODE3, 0, new HasValue("attr1", 32), TEMPLATE);
		assertEquals(new GroupQueryRequest(new PointToPoint(NODE3, ADDRESS), 0, TEMPLATE, new HasValue("attr1", 32)),
				receivedAtOne);
		assertEquals(new GroupQueryRequest(new PointToPoint(NODE3, ADDRESS), 0, TEMPLATE, new HasValue("attr1", 32)),
				receivedAtTwo);
	}

	@Test
	public void testSendGroupPutReply() throws IOException, InterruptedException {
		port.sendGroupPutReply(new PointToPoint(NODE2, ADDRESS), NODE1, 0, 0);
		assertNull(receivedAtOne);
		assertEquals(new GroupPutReply(new PointToPoint(NODE1, ADDRESS), 0, NODE2, 0), receivedAtTwo);
	}

	@Test
	public void testSendGroupGetReply() throws IOException, InterruptedException {
		port.sendGroupGetReply(new PointToPoint(NODE2, ADDRESS), NODE1, 0, 0, new Attribute[0], TUPLE);
		assertNull(receivedAtOne);
		assertEquals(new GroupGetReply(new PointToPoint(NODE1, ADDRESS), 0, NODE2, 0, new Attribute[0], TUPLE),
				receivedAtTwo);
	}

	@Test
	public void testSendGroupQueryReply() throws IOException, InterruptedException {
		port.sendGroupQueryReply(new PointToPoint(NODE2, ADDRESS), NODE1, 0, TUPLE);
		assertNull(receivedAtOne);
		assertEquals(new GroupQueryReply(new PointToPoint(NODE1, ADDRESS), 0, NODE2, TUPLE), receivedAtTwo);
	}

}
