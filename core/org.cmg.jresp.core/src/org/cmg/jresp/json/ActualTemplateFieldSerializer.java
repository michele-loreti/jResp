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

import org.cmg.jresp.knowledge.ActualTemplateField;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This class is used to serialize an {@link ActualTemplateField} into a
 * {@link JsonElement} (see {@link JsonDeserializer}).
 * 
 * @author Michele Loreti
 *
 */
public class ActualTemplateFieldSerializer implements JsonSerializer<ActualTemplateField> {

	@Override
	public JsonElement serialize(ActualTemplateField src, Type typeOfSrc, JsonSerializationContext context) {
		return SCELJsonUtil.jsonFromObject(src.getValue(), context);
	}

}
