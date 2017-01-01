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

import org.cmg.jresp.knowledge.Tuple;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This class is used to serialize a {@link Tuple} into a {@link JsonElement}
 * (see {@link JsonDeserializer}).
 * 
 * @author Michele Loreti
 *
 */
public class TupleSerializer implements JsonSerializer<Tuple> {

	@Override
	public JsonElement serialize(Tuple src, Type typeOfSrc, JsonSerializationContext context) {
		JsonArray toReturn = new JsonArray();
		for (Object o : src) {
			toReturn.add(SCELJsonUtil.jsonFromObject(o, context));
		}
		return toReturn;
	}

}
