package org.cmg.test.json;

import static org.junit.Assert.assertEquals;

import org.cmg.jresp.RESPFactory;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.FormalTemplateField;
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
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.ServerPortAddress;
import org.cmg.jresp.topology.SocketPortAddress;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class MessageSerializationDeserialization {

	protected Gson gson;

	@Before
	public void init() {
		gson = RESPFactory.getGSon();
	}

	@Test
	public void testSerializeDeserializeAck() {
		// Ack ack = new Ack(new PointToPoint("test", new
		// SocketPortAddress(9999)), 23,"pippo");
		// Ack ack = new Ack(new PointToPoint("test", new
		// VirtualPortAddress(9999)), 23,"pippo");
		Ack ack = new Ack(new PointToPoint("test", new ServerPortAddress(9999)), 23, "pippo");
		String txt = gson.toJson(ack);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(ack, msg);
	}

	@Test
	public void testSerializeDeserializeFail() {
		Fail fail = new Fail(new PointToPoint("test", new SocketPortAddress(9999)), 23, "pippo", "error");
		String txt = gson.toJson(fail);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(fail, msg);
	}

	@Test
	public void testSerializeDeserializeAttributeRequest() {
		AttributeRequest fail = new AttributeRequest(new PointToPoint("test", new SocketPortAddress(9999)), 23, "pippo",
				new String[] { "attr1", "attr2" });
		String txt = gson.toJson(fail);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(fail, msg);
	}

	@Test
	public void testSerializeDeserializeAttributeReply() {
		AttributeReply fail = new AttributeReply(new PointToPoint("test", new SocketPortAddress(9999)), 23, "pippo",
				new Attribute[] { new Attribute("attr1", 34), new Attribute("attr2", false) });
		String txt = gson.toJson(fail);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(fail, msg);
	}

	@Test
	public void testSerializeDeserializeTuple() {
		Tuple t = new Tuple(true, 34);
		String txt = gson.toJson(t);
		System.out.println(txt);
		Tuple msg = gson.fromJson(txt, Tuple.class);
		assertEquals(t, msg);
	}

	@Test
	public void testSerializeDeserializePutRequest() {
		PutRequest request = new PutRequest(new PointToPoint("test", new SocketPortAddress(9999)), 34, "pippo",
				new Tuple(true, 34, new AnObject(2, 3),
						new AnObjectWithArrays(new AnObject(1, 2), new AnObject(3, 4))));
		String txt = gson.toJson(request);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(request, msg);
	}

	@Test
	public void testSerializeDeserializeTupleReply() {
		TupleReply request = new TupleReply(new PointToPoint("test", new SocketPortAddress(9999)), 34, "pippo",
				new Tuple(true, 34, new AnObject(2, 3),
						new AnObjectWithArrays(new AnObject(1, 2), new AnObject(3, 4))));
		String txt = gson.toJson(request);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(request, msg);
	}

	@Test
	public void testSerializeDeserializeGetRequest() {
		GetRequest request = new GetRequest(new PointToPoint("test", new SocketPortAddress(9999)), 34, "pippo",
				new Template(new ActualTemplateField(true), new FormalTemplateField(Integer.class),
						new ActualTemplateField(new AnObject(2, 3)),
						new ActualTemplateField(new AnObjectWithArrays(new AnObject(1, 2), new AnObject(3, 4)))));
		String txt = gson.toJson(request);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(request, msg);
	}

	@Test
	public void testSerializeDeserializeQueryRequest() {
		QueryRequest request = new QueryRequest(new PointToPoint("test", new SocketPortAddress(9999)), 34, "pippo",
				new Template(new ActualTemplateField(true), new FormalTemplateField(Integer.class),
						new ActualTemplateField(new AnObject(2, 3)),
						new ActualTemplateField(new AnObjectWithArrays(new AnObject(1, 2), new AnObject(3, 4)))));
		String txt = gson.toJson(request);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(request, msg);
	}

	@Test
	public void testSerializeDeserializeGroupQueryRequest() {
		GroupQueryRequest request = new GroupQueryRequest(new PointToPoint("test", new SocketPortAddress(9999)), 34,
				new Template(new ActualTemplateField(true), new FormalTemplateField(Integer.class),
						new ActualTemplateField(new AnObject(2, 3)),
						new ActualTemplateField(new AnObjectWithArrays(new AnObject(1, 2), new AnObject(3, 4)))),
				new HasValue("attr1", 35)
		// new String[] { "attr1" , "attr2" }
		);
		String txt = gson.toJson(request);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(request, msg);
	}

	@Test
	public void testSerializeDeserializeGroupGetRequest() {
		GroupGetRequest request = new GroupGetRequest(new PointToPoint("test", new SocketPortAddress(9999)), 34,
				new Template(new ActualTemplateField(true), new FormalTemplateField(Integer.class),
						new ActualTemplateField(new AnObject(2, 3)),
						new ActualTemplateField(new AnObjectWithArrays(new AnObject(1, 2), new AnObject(3, 4)))),
				new HasValue("attr1", 35));
		String txt = gson.toJson(request);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(request, msg);
	}

	@Test
	public void testSerializeDeserializeGroupPutRequest() {
		GroupPutRequest request = new GroupPutRequest(new PointToPoint("test", new SocketPortAddress(9999)), 34,
				new HasValue("attr1", 35), new Tuple(true, 34));
		String txt = gson.toJson(request);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(request, msg);
	}

	@Test
	public void testSerializeDeserializeGroupGetReply() {
		GroupGetReply request = new GroupGetReply(new PointToPoint("test", new SocketPortAddress(9999)), 34, "pippo",
				48, new Attribute[] { new Attribute("attr1", 34), new Attribute("attr2", false) }, new Tuple(true, 34));
		String txt = gson.toJson(request);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(request, msg);
	}

	@Test
	public void testSerializeDeserializeGroupQueryReply() {
		GroupQueryReply request = new GroupQueryReply(new PointToPoint("test", new SocketPortAddress(9999)), 34,
				"pippo", new Tuple(true, 34));
		String txt = gson.toJson(request);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(request, msg);
	}

	@Test
	public void testSerializeDeserializeGroupPutReply() {
		GroupPutReply fail = new GroupPutReply(new PointToPoint("test", new SocketPortAddress(9999)), 23, "pippo", 34);
		String txt = gson.toJson(fail);
		System.out.println(txt);
		jRESPMessage msg = gson.fromJson(txt, jRESPMessage.class);
		assertEquals(fail, msg);
	}

	/*
	 * GET_REQUEST(3,"G_REQUEST"), QUERY_REQUEST(4,"Q_REQUEST"),
	 */
}
