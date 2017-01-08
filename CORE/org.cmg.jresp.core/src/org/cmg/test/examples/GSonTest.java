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
package org.cmg.test.examples;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Michele Loreti
 *
 */
public class GSonTest {

	public static void main(String[] argv) {

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(TestClass.class, new TestSerializer());
		gsonBuilder.registerTypeHierarchyAdapter(TestClass.class, new TestDeserializer());
		Gson gson = gsonBuilder.create();
		TestClass src = new TestClass(new Integer(34));
		String test = gson.toJson(src);
		System.out.println(test);
		TestClass res = gson.fromJson(test, TestClass.class);
		System.out.println("Result: " + src.equals(res));

		// Protocol.Message request2 =
		// gson.fromJson(test, Protocol.Message.class );
		// System.out.println(request2.toString());
	}

	public static class TestClass {

		private Object o;

		public TestClass(Object o) {
			this.o = o;
		}

		public Object get() {
			return o;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TestClass) {
				return o.equals(((TestClass) obj).o);
			}
			return false;
		}

	}

	public static class TestSerializer implements JsonSerializer<TestClass> {

		@Override
		public JsonElement serialize(TestClass src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject o = new JsonObject();
			o.add("type", new JsonPrimitive(src.o.getClass().getName()));
			o.add("value", context.serialize(src.o));
			return o;
		}

	}

	public static class TestDeserializer implements JsonDeserializer<TestClass> {

		@Override
		public TestClass deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			if (!json.isJsonObject()) {
				throw new JsonParseException("?????");
			}
			JsonObject jo = (JsonObject) json;
			if ((!jo.has("type")) || (!jo.has("value"))) {
				throw new JsonParseException("?????");
			}
			String className = jo.get("type").getAsString();
			try {
				Class<?> c = Class.forName(className);
				return new TestClass(context.deserialize(jo.get("value"), c));
			} catch (ClassNotFoundException e) {
				throw new JsonParseException(e);
			}
		}

	}
}
