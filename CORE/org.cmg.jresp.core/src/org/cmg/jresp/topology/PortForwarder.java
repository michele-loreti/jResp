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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.cmg.jresp.RESPFactory;
import org.cmg.jresp.protocol.UnicastMessage;
import org.cmg.jresp.protocol.jRESPMessage;

import com.google.gson.Gson;

/**
 * @author Michele Loreti
 *
 */
public class PortForwarder extends AbstractPort {

	private Gson gson;
	private PrintWriter writer;
	private BufferedReader reader;
	private Socket remote;
	private Address remotePortAddress;

	public PortForwarder(Address remotePortAddress, String serverAddress, int serverPort)
			throws UnknownHostException, IOException {
		this.remote = new Socket(serverAddress, serverPort);
		this.writer = new PrintWriter(remote.getOutputStream());
		this.reader = new BufferedReader(new InputStreamReader(remote.getInputStream()));
		this.gson = RESPFactory.getGSon();
		this.remotePortAddress = remotePortAddress;
		new Thread(new PortThread()).start();
	}

	@Override
	public boolean canSendTo(Target l) {
		return l.isAGroup() || ((l instanceof PointToPoint) && (((PointToPoint) l).getAddress().equals(getAddress())));
	}

	@Override
	protected void send(Address address, UnicastMessage message) throws IOException, InterruptedException {
		writer.println(gson.toJson(message));
		writer.print("%%%");
		writer.flush();
	}

	@Override
	protected void send(jRESPMessage message) throws IOException, InterruptedException {
		writer.println(gson.toJson(message));
		writer.print("%%%");
		writer.flush();
	}

	@Override
	public Address getAddress() {
		return remotePortAddress;
	}

	public class PortThread implements Runnable {

		@Override
		public void run() {
			try {
				String buffer = "";
				while (true) {
					String tmp = reader.readLine();
					if (tmp.equals("%%%")) {
						jRESPMessage msg = gson.fromJson(buffer, jRESPMessage.class);
						dispatch(msg);
						buffer = "";
					} else {
						buffer += tmp + "\n";
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void dispatch(jRESPMessage msg) throws IOException, InterruptedException {
		if (msg instanceof UnicastMessage) {
			UnicastMessage uMsg = (UnicastMessage) msg;
			MessageDispatcher n = nodes.get(uMsg.getTarget());
			if (n != null) {
				n.addMessage(uMsg);
			} else {
				sendFail(uMsg.getSource(), uMsg.getSource().getName(), uMsg.getSession(),
						"Node " + uMsg.getTarget() + " is unknown at " + getAddress());
			}
		} else {
			for (MessageDispatcher n : nodes.values()) {
				n.addMessage(msg);
			}
		}
	}

}
