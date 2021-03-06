package org.cmg.jresp.topology;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SocketPortAddress extends Address {

	public static final String ADDRESS_CODE = "socket";
	private InetSocketAddress address;

	public SocketPortAddress(int port) {
		this(new InetSocketAddress(port));
	}

	public SocketPortAddress(InetAddress addr, int port) {
		this(new InetSocketAddress(addr, port));
	}

	public SocketPortAddress(String host, int port) {
		this(new InetSocketAddress(host, port));
	}

	public SocketPortAddress(InetSocketAddress address) {
		super(ADDRESS_CODE);
		this.address = address;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof SocketPortAddress) {
			SocketPortAddress spa = (SocketPortAddress) arg0;
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
