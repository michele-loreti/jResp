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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * This class is used to deserialize an {@link ActualTemplateField} from a
 * {@link JsonElement} (see {@link JsonDeserializer}).
 * 
 * @author Michele Loreti
 *
 */
public class ActualTemplateFieldDeserializer implements JsonDeserializer<ActualTemplateField> {

	@Override
	public ActualTemplateField deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return new ActualTemplateField(SCELJsonUtil.objectFromJson(json, context));
	}

}
