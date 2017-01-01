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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import org.cmg.jresp.RESPFactory;
import org.cmg.jresp.protocol.jRESPMessage;

import com.google.gson.Gson;

/**
 * @author Michele Loreti
 *
 */
public class Forwarder implements MessageDispatcher, Runnable {

	private String name;
	private LinkedList<jRESPMessage> fromPortToRemote;
	private int serverPort;
	private Gson gson = RESPFactory.getGSon();
	private AbstractPort port;

	public Forwarder(String name, int serverPort, AbstractPort port) {
		this.name = name;
		this.serverPort = serverPort;
		this.port = port;
		this.fromPortToRemote = new LinkedList<jRESPMessage>();
		this.port.register(this);
	}

	@Override
	public void addMessage(jRESPMessage msg) {
		synchronized (fromPortToRemote) {
			fromPortToRemote.add(msg);
			fromPortToRemote.notify();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run() {
		try {
			ServerSocket ssocket = new ServerSocket(serverPort);
			Socket clientSocket = ssocket.accept();
			Thread t1 = new Thread(new IncomingThread(new PrintWriter(clientSocket.getOutputStream())));
			Thread t2 = new Thread(
					new ReceivingThread(new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))));
			t1.start();
			t2.start();
			t1.join();
			t2.join();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public class IncomingThread implements Runnable {

		private PrintWriter writer;

		public IncomingThread(PrintWriter writer) {
			this.writer = writer;
		}

		@Override
		public void run() {
			try {
				while (true) {
					jRESPMessage message = getNextMessage();
					writer.println(gson.toJson(message));
					writer.println("%%%");
					writer.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public jRESPMessage getNextMessage() throws InterruptedException {
		synchronized (fromPortToRemote) {
			while (fromPortToRemote.isEmpty()) {
				fromPortToRemote.wait();
			}
			return fromPortToRemote.removeFirst();
		}
	}

	public class ReceivingThread implements Runnable {

		private BufferedReader reader;

		public ReceivingThread(BufferedReader reader) {
			this.reader = reader;
		}

		@Override
		public void run() {
			try {
				String buffer = "";
				while (true) {
					String tmp = reader.readLine();
					if (tmp.equals("%%%")) {
						jRESPMessage msg = gson.fromJson(buffer, jRESPMessage.class);
						port.deliver(msg);
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

}
