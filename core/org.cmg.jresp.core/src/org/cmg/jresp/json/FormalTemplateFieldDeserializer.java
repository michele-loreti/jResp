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

import org.cmg.jresp.knowledge.FormalTemplateField;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * This class is used to deserialize a {@link FormalTemplateField} from a
 * {@link JsonElement} (see {@link JsonDeserializer}).
 * 
 * @author Michele Loreti
 *
 */
public class FormalTemplateFieldDeserializer implements JsonDeserializer<FormalTemplateField> {

	@Override
	public FormalTemplateField deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (!json.isJsonObject()) {
			throw new JsonParseException("Unexpected JsonElement!");
		}
		JsonObject jo = (JsonObject) json;
		if ((!jo.has("type"))) {
			throw new JsonParseException("Required attributes are not available!");
		}
		try {
			Class<?> c = Class.forName(jo.get("type").getAsString());
			return new FormalTemplateField(c);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}

}
