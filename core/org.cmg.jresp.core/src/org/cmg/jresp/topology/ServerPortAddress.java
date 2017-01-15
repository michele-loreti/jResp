package org.cmg.jresp.topology;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerPortAddress extends Address {

	public static final String ADDRESS_CODE = "server";
	private InetSocketAddress address;

	public ServerPortAddress(int port) {
		this(new InetSocketAddress(port));
	}

	public ServerPortAddress(InetAddress addr, int port) {
		this(new InetSocketAddress(addr, port));
	}

	public ServerPortAddress(String host, int port) {
		this(new InetSocketAddress(host, port));
	}

	public ServerPortAddress(InetSocketAddress address) {
		super(ADDRESS_CODE);
		this.address = address;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof ServerPortAddress) {
			ServerPortAddress spa = (ServerPortAddress) arg0;
			return getAddressCode().equals(spa.getAddressCode()) && getAddress().equals(spa.getAddress());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getAddressCode().hashCode() ^ getAddress().hashCode();
	}

	@Override
	public String toString() {
		return getAddressCode() + ":" + address.toString();
	}

}
