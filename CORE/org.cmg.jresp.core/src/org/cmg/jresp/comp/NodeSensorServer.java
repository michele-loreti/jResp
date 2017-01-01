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
package org.cmg.jresp.comp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import org.cmg.jresp.RESPFactory;
import org.cmg.jresp.knowledge.AbstractSensor;

import com.google.gson.Gson;

/**
 * This class allows remote components to access to a sensor via a network
 * connection.
 * 
 * @author Michele Loreti
 *
 */
public class NodeSensorServer extends AbstractSensor implements Observer {

	/**
	 * The sensor collecting data.
	 */
	private AbstractSensor sensor;

	/**
	 * Server socket used to accept remote connections.
	 */
	private ServerSocket ssocket;

	/**
	 * Gson object used to serialize/deserialize messages.
	 */
	private Gson gson = RESPFactory.getGSon();

	public NodeSensorServer(AbstractSensor sensor, int port) throws IOException {
		super(sensor.getName(), sensor.getTemplate());
		this.sensor = sensor;
		this.ssocket = new ServerSocket(port);
		new Thread(new SensorThread()).start();
	}

	public class SensorThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Socket s = ssocket.accept();
					PrintWriter writer = new PrintWriter(s.getOutputStream());
					writer.println(gson.toJson(getValue(template)));
					writer.close();
					s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		try {
			setValue(this.sensor.getValue(template));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
