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
package org.cmg.jresp.examples.mobility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.VirtualPort;

/**
 * @author Michele Loreti
 *
 */
public class SimpleMobility extends JFrame implements Observer {

	private SpatialConnection connection;
	private SpatialPanel panel;
	private Thread movingThread;

	public SimpleMobility(SpatialConnection connection) {
		super("SCEL@Work");
		this.connection = connection;
		this.connection.addObserver(this);
		this.panel = new SpatialPanel();
		setContentPane(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		movingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					SimpleMobility.this.connection.move();
				}
			}
		});
	}

	public void start() {
		movingThread.start();
	}

	public class SpatialPanel extends JPanel {

		public SpatialPanel() {
			super();
			setBackground(Color.WHITE);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension((int) connection.maxX, (int) connection.maxY);
		}

		@Override
		public Dimension getSize(Dimension arg0) {
			arg0.setSize(getPreferredSize());
			return arg0;
		}

		@Override
		public void paint(Graphics arg0) {
			super.paint(arg0);
			Graphics2D g2 = (Graphics2D) arg0;
			g2.setColor(Color.YELLOW);
			Point2D.Double target = connection.getTargetLocation();
			g2.fill(new Ellipse2D.Double(target.x - (connection.targetSize / 2), target.y - (connection.targetSize / 2),
					connection.targetSize, connection.targetSize));
			g2.setColor(Color.RED);
			for (String node : connection.getNodes()) {
				Point2D.Double p = connection.getLocation(node);
				g2.fill(new Ellipse2D.Double(p.x - 5, p.y - 5, 10, 10));
			}
		}

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		this.repaint();
	}

	public static void main(String[] argv) {
		int size = 40;
		SpatialConnection sc = new SpatialConnection(size, 250);
		VirtualPort vp = new VirtualPort(10, sc);
		Hashtable<String, Node> nodes = new Hashtable<String, Node>();
		for (String name : sc.getNodes()) {
			Node n = new Node(name, new TupleSpace());
			n.addPort(vp);
			n.setGroupActionWaitingTime(750);
			n.addActuator(sc.getDirectionActuator(name));
			n.addSensor(sc.getLocationSensor(name));
			n.addSensor(sc.getTargetSensor(name));
			n.addActuator(sc.getStopActuator(name));
			Agent a = new MovementAgent("_driver_" + name, sc.getMaxX(), sc.getMaxY());
			n.addAgent(a);
			a = new CommunicationAgent("_comm_" + name);
			n.addAgent(a);
			nodes.put(name, n);
		}
		SimpleMobility sf = new SimpleMobility(sc);
		sf.setVisible(true);
		for (Node n : nodes.values()) {
			n.start();
		}
		sf.start();
	}

}
