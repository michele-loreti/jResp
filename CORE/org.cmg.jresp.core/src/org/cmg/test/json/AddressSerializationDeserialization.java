package org.cmg.test.json;

import static org.junit.Assert.assertEquals;

import org.cmg.jresp.RESPFactory;
import org.cmg.jresp.topology.Address;
import org.cmg.jresp.topology.ServerPortAddress;
import org.cmg.jresp.topology.SocketPortAddress;
import org.junit.Test;

import com.google.gson.Gson;

public class AddressSerializationDeserialization {

	@Test
	public void testSerializeDeserializeSocket() {
		SocketPortAddress addr = new SocketPortAddress(9999);
		Gson gson = RESPFactory.getGSon();
		String str = gson.toJson(addr);
		Address read = gson.fromJson(str, Address.class);
		assertEquals(addr, read);
	}

	@Test
	public void testSerializeDeserializeServer() {
		ServerPortAddress addr = new ServerPortAddress(9999);
		Gson gson = RESPFactory.getGSon();
		String str = gson.toJson(addr);
		Address read = gson.fromJson(str, Address.class);
		assertEquals(addr, read);
	}

}
