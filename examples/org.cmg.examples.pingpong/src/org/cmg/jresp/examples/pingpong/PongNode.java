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
package org.cmg.jresp.examples.pingpong;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.SocketPort;
import org.cmg.jresp.topology.SocketPortAddress;

/**
 * @author Michele Loreti
 *
 */
public class PongNode {

	public static void main(String[] argv) throws IOException {
		int localPort = inputPort("Local");
		String localHost = JOptionPane.showInputDialog("Local address:");
		int remotePort = inputPort("Remote");
		String remoteHost = JOptionPane.showInputDialog("Remote address:");
		SocketPort pongPort = new SocketPort(localHost, localPort);
		Agent pong = new PongAgent(remoteHost, remotePort);
		Node pongNode = new Node("pong", new TupleSpace());
		pongNode.addPort(pongPort);
		pongNode.addAgent(pong);
		pongNode.start();
	}

	public static int inputPort(String type) {
		while (true) {
			String input = JOptionPane.showInputDialog(type + " port (quit for exit):");
			if ("quit".equals(input)) {
				System.exit(0);
			}
			try {
				return Integer.parseInt(input);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "An integer is expected!");
			}
		}

	}

	public static class PongAgent extends Agent {

		PointToPoint other;

		public PongAgent(String remoteHost, int remotePort) {
			super("PONG");
			this.other = new PointToPoint("ping", new SocketPortAddress(remoteHost, remotePort));
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					get(new Template(new ActualTemplateField("PING")), Self.SELF);
					System.out.println("PONG!");
					put(new Tuple("PONG"), other);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
