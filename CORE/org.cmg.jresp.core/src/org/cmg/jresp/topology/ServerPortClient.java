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
package org.cmg.jresp.topology;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.cmg.jresp.RESPFactory;
import org.cmg.jresp.protocol.UnicastMessage;
import org.cmg.jresp.protocol.jRESPMessage;

import com.google.gson.Gson;

/**
 * @author Michele Loreti
 *
 */
public class ServerPortClient extends AbstractPort {

	private ServerPortAddress serverAddress;
	private ServerSocket localAddress;
	private Gson gson;

	public ServerPortClient(ServerPortAddress serverAddress, ServerSocket localAddress) {
		this.serverAddress = serverAddress;
		this.localAddress = localAddress;
		this.gson = RESPFactory.getGSon();
		Thread t = new Thread(new SocketReceiver(this.localAddress, this));
		t.setDaemon(true);
		t.start();
	}

	@Override
	public boolean canSendTo(Target l) {
		return (l instanceof PointToPoint) && (((PointToPoint) l).getAddress().equals(serverAddress));
	}

	protected void sendToServer(jRESPMessage message) throws IOException {
		InetSocketAddress isc = serverAddress.getAddress();
		Socket socket = new Socket(isc.getAddress(), isc.getPort());
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		writer.println(gson.toJson(message));
		writer.close();
		socket.close();
	}

	@Override
	protected void send(Address address, UnicastMessage message) throws IOException, InterruptedException {
		sendToServer(message);
	}

	@Override
	protected void send(jRESPMessage message) throws IOException, InterruptedException {
		sendToServer(message);
	}

	@Override
	public Address getAddress() {
		return serverAddress;
	}

}
