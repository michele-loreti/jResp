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
package org.cmg.jresp.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

/**
 * This class provides static methods used to perform recurrent tasks for
 * objects serialization/deserialization.
 * 
 * @author Michele Loreti
 *
 */
public class SCELJsonUtil {

	/**
	 * Json tag associated to string referring to {@ling Class} names.
	 */
	public static final String TYPE_ID = "type";

	/**
	 * Json tag associated to generic objects. The type of the specific object
	 * is determined by relying on attribute {@link SCELJsonUtil#TYPE_ID}
	 */
	public static final String VALUE_ID = "value";

	/**
	 * Serialize an object into a {@link JsonElement}. The object is rendered as
	 * a {@link JsonObject} containing two attributes:
	 * <ul>
	 * <li><code>type</code>, containing a string with the fully qualified name
	 * of the serialized object;
	 * <li><code>value</code>, containing the {@link JsonElement} associated to
	 * the serialized object.
	 * </ul>
	 * 
	 * When the object will be deserialized, the first attribute will be used to
	 * identify the object class, while the second one will be used to retrieve
	 * object status.
	 * 
	 * 
	 * @param o
	 *            object to serialize
	 * @param context
	 *            Context for serialization
	 * @return a json representation of o
	 */
	public static JsonElement jsonFromObject(Object o, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add(SCELJsonUtil.TYPE_ID, new JsonPrimitive(o.getClass().getName()));
		json.add(SCELJsonUtil.VALUE_ID, context.serialize(o));
		return json;
	}

	/**
	 * Deserialize an object from a {@link JsonElement}. We assume that the
	 * received JsonElement is a {@link JsonObject} providing two attributes:
	 * <ul>
	 * <li><code>type</code>, containing a string with the fully qualified name
	 * of the serialized object;
	 * <li><code>value</code>, containing the {@link JsonElement} associated to
	 * the serialized object.
	 * </ul>
	 * 
	 * 
	 * @param json
	 *            element to deserialize
	 * @param context
	 *            context for serialization
	 * @return the object represented by json
	 */
	public static Object objectFromJson(JsonElement json, JsonDeserializationContext context) {
		if (!json.isJsonObject()) {
			throw new JsonParseException("Unexpected JsonElement!");
		}
		JsonObject jo = (JsonObject) json;
		if ((!jo.has("type")) || (!jo.has("value"))) {
			throw new JsonParseException("Required attributes are not available!");
		}
		String className = jo.get(TYPE_ID).getAsString();
		try {
			Class<?> c = Class.forName(className);
			return context.deserialize(jo.get(VALUE_ID), c);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}

}
