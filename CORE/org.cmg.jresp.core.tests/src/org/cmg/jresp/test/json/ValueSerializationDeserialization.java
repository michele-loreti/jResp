package org.cmg.jresp.test.json;

import static org.junit.Assert.assertEquals;

import org.cmg.jresp.RESPFactory;
import org.cmg.jresp.knowledge.Tuple;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class ValueSerializationDeserialization {
	protected Gson gson;

	@Before
	public void init() {
		gson = RESPFactory.getGSon();
	}

	@Test
	public void testSerializeDeserializeSimpleObjects() {
		Tuple t = new Tuple(new AnObject(1, 2), new AnObject(3, 4));
		String str = gson.toJson(t);
		Tuple t2 = gson.fromJson(str, Tuple.class);
		assertEquals(t, t2);
	}

	@Test
	public void testSerializeDeserializeObjectsWithArray() {
		Tuple t = new Tuple(new AnObjectWithArrays(new AnObject(1, 2), new AnObject(3, 4)));
		String str = gson.toJson(t);
		Tuple t2 = gson.fromJson(str, Tuple.class);
		assertEquals(t, t2);
	}
}

// protected Gson gson;

// @Before
// public void init() {
// GsonBuilder builder = new GsonBuilder();
// builder.registerTypeHierarchyAdapter(SCELValue.class, new
// ValueDeserializer());
// gson = builder.setPrettyPrinting().create();
// }

/*
 * public void init() { GsonBuilder builder = new GsonBuilder();
 * builder.registerTypeHierarchyAdapter(SCELValue.class, new
 * ValueDeserializer()); //
 * builder.registerTypeHierarchyAdapter(Protocol.Message.class, new
 * MessageDeserializer()); gson = builder.setPrettyPrinting().create(); }
 * 
 * protected void doTest( Object expected ) { String txt =
 * gson.toJson(expected); System.out.println(txt+"\n\n"); SCELValue v =
 * gson.fromJson(txt, SCELValue.class); assertEquals(expected,v); }
 * 
 * @Test public void serializeDeserializeTrue() { SCELBoolean b =
 * SCELValue.getBoolean(true); doTest(b); }
 * 
 * @Test public void serializeDeserializeFalse() { SCELBoolean b =
 * SCELValue.getBoolean(false); doTest(b); }
 * 
 * @Test public void serializeDeserializeByte() { SCELByte b =
 * SCELValue.getByte((byte) 100); doTest(b); }
 * 
 * @Test public void serializeDeserializeChar() { SCELChar c =
 * SCELValue.getChar('a'); doTest(c); }
 * 
 * @Test public void serializeDeserializeFloat() { SCELFloat f =
 * SCELValue.getFloat((float) 1.0); doTest(f); }
 * 
 * @Test public void serializeDeserializeDouble() { SCELDouble f =
 * SCELValue.getDouble(1.0); doTest(f); }
 * 
 * @Test public void serializeDeserializeInteger() { SCELInteger i =
 * SCELValue.getInteger(1); doTest(i); }
 * 
 * @Test public void serializeDeserializeLong() { SCELLong l =
 * SCELValue.getLong((long) 1); doTest(l); }
 * 
 * @Test public void serializeDeserializeShort() { SCELShort s =
 * SCELValue.getShort((short) 1); doTest(s); }
 * 
 * @Test public void serializeDeserializeString() { SCELString s =
 * SCELValue.getString("To be or not to be, this is the question!"); doTest(s);
 * }
 * 
 * @Test public void serializeDeserializeTag() { SCELTag t = SCELValue.getTag(
 * "exTag" , new Tuple( SCELValue.getBoolean(true) , SCELValue.getInteger(2) ,
 * SCELValue.getDouble(2.3) ) ); doTest(t); }
 * 
 * @Test public void testSerializeDeserializeTuple() { Tuple t = new Tuple(
 * SCELValue.getBoolean(true) , SCELValue.getInteger(34)); String txt =
 * gson.toJson(t); System.out.println(txt); Tuple msg = gson.fromJson(txt,
 * Tuple.class); assertEquals(t, msg); }
 * 
 * 
 * }
 */
/*
 * STRING, TAG;
 */
