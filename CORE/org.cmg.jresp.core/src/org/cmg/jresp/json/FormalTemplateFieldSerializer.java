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

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This class is used to serialize a {@link FormalTemplateField} into a
 * {@link JsonElement} (see {@link JsonDeserializer}).
 * 
 * @author Michele Loreti
 *
 */
public class FormalTemplateFieldSerializer implements JsonSerializer<FormalTemplateField> {

	@Override
	public JsonElement serialize(FormalTemplateField src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add("type", new JsonPrimitive(src.getFormalFieldType().getName()));
		return json;
	}

}
