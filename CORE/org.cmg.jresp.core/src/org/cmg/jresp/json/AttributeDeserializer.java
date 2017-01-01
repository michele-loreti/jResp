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

import java.lang.reflect.Type;

import org.cmg.jresp.knowledge.Attribute;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * This class is used to deserialize an {@link Attribute} from a
 * {@link JsonElement} (see {@link JsonDeserializer}).
 * 
 * @author Michele Loreti
 *
 */
public class AttributeDeserializer implements JsonDeserializer<Attribute> {

	@Override
	public Attribute deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (!json.isJsonObject()) {
			throw new JsonParseException("Unexpected JsonElement!");
		}
		JsonObject jo = (JsonObject) json;
		if ((!jo.has("name")) || (!jo.has("value"))) {
			throw new JsonParseException("Required attributes are not available!");
		}
		return new Attribute(jo.get("name").getAsString(), SCELJsonUtil.objectFromJson(jo.get("value"), context));
	}

}
