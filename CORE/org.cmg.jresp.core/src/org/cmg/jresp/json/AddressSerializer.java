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

import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.topology.Address;
import org.cmg.jresp.topology.ServerPortAddress;
import org.cmg.jresp.topology.SocketPortAddress;
import org.cmg.jresp.topology.VirtualPortAddress;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This class is used to serialize an {@link Attribute} into a
 * {@link JsonElement} (see {@link JsonDeserializer}).
 * 
 * @author Michele Loreti
 *
 */
public class AddressSerializer implements JsonSerializer<Address> {

	@Override
	public JsonElement serialize(Address src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		String addressCode = src.getAddressCode();
		json.add("addressCode", new JsonPrimitive(addressCode));
		if (addressCode.equals(SocketPortAddress.ADDRESS_CODE)) {
			InetSocketAddress inetSocketAddress = ((SocketPortAddress) src).getAddress();
			String host = inetSocketAddress.getHostName();
			json.add("host", new JsonPrimitive(host));
			int port = inetSocketAddress.getPort();
			json.add("port", new JsonPrimitive(port));
		}
		if (addressCode.equals(ServerPortAddress.ADDRESS_CODE)) {
			InetSocketAddress inetSocketAddress = ((ServerPortAddress) src).getAddress();
			String host = inetSocketAddress.getHostName();
			json.add("host", new JsonPrimitive(host));
			int port = inetSocketAddress.getPort();
			json.add("port", new JsonPrimitive(port));
		}
		if (addressCode.equals(VirtualPortAddress.ADDRESS_CODE)) {
			json.add("id", new JsonPrimitive(((VirtualPortAddress) src).getId()));
		}
		return json;
	}

}
