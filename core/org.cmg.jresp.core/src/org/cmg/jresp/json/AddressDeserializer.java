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
import java.net.InetSocketAddress;

import org.cmg.jresp.topology.Address;
import org.cmg.jresp.topology.ServerPortAddress;
import org.cmg.jresp.topology.SocketPortAddress;
import org.cmg.jresp.topology.VirtualPortAddress;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @author Michele Loreti
 *
 */
public class AddressDeserializer implements JsonDeserializer<Address> {

	@Override
	public Address deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (json.isJsonObject()) {
			JsonObject o = json.getAsJsonObject();
			return doDeserialize(o.get("addressCode").getAsString(), o, context);
		}
		throw new IllegalStateException("This is not an Address!");
	}

	private Address doDeserialize(String addressCode, JsonObject json, JsonDeserializationContext context) {
		if (addressCode == null) {
			throw new IllegalStateException("This is not an Address!");
		}
		if (addressCode.equals(SocketPortAddress.ADDRESS_CODE)) {
			return new SocketPortAddress(
					new InetSocketAddress(json.get("host").getAsString(), json.get("port").getAsInt()));
		}
		if (addressCode.equals(ServerPortAddress.ADDRESS_CODE)) {
			return new ServerPortAddress(
					new InetSocketAddress(json.get("host").getAsString(), json.get("port").getAsInt()));
		}
		if (addressCode.equals(VirtualPortAddress.ADDRESS_CODE)) {
			return new VirtualPortAddress((Integer) context.deserialize(json.get("id"), Integer.class));
		}
		throw new IllegalStateException("This is not an Address!");
	}

}
