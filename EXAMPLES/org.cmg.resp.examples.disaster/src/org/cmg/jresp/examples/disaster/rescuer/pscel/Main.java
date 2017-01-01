/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universit? di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.jresp.examples.disaster.rescuer.pscel;

import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.AttributeCollector;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.VirtualPort;

/**
 * 
 * @author Andrea Margheri
 *
 */
public class Main extends JFrame {

	/*
	 * ################################################### USE the class
	 * MainRescuer_Policy_Simulation
	 * ###################################################
	 */

	private static final long serialVersionUID = 1L;
	private Scenario scenario;
	private JPanel internal;

	private static final double HEIGHT = 300;
	private static final double WIDTH = 250;

	public Main(int robots, int numSwarmRescuer, double height, double width) {
		super("Disaster scenario in jRESP by using FACPL policies");
		// 1 = number of victim
		scenario = new Scenario(robots, numSwarmRescuer, 1, height, width);
		scenario.init();
		init();
		setVisible(true);
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10);
						scenario.step(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}).start();
		instantiateNet();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void instantiateNet() {
		VirtualPort vp = new VirtualPort(10, scenario.getNodeConnection());
		// VirtualPort vp = new VirtualPort(10, new ConnectionWithRange());
		Hashtable<String, Node> nodes = new Hashtable<String, Node>();

		for (int i = 0; i < scenario.getRobots(); i++) {
			final Node n = new Node("" + i, new TupleSpace());
			final int robotIndex = i;
			n.addPort(vp);
			n.setGroupActionWaitingTime(250);
			/**
			 * Actuators
			 */
			n.addActuator(scenario.getChangeRoleActuator(i));
			n.addActuator(scenario.getDirectionActuator(i));
			n.addActuator(scenario.getPointDirectionActuator(i));
			n.addActuator(scenario.getStopActuator(i));
			/**
			 * Sensors
			 */
			n.addSensor(scenario.getCollisionSensor(i));
			n.addSensor(scenario.getVictimSensor(i));
			n.addSensor(scenario.getWalkingSensor(i));
			n.addSensor(scenario.getDirectionSensor(i));

			// starting robot role
			n.put(new Tuple("role", Scenario.EXPLORER));

			/**
			 * AttributeCollector = exposing the attribute of component in the
			 * interface
			 */
			n.addAttributeCollector(new AttributeCollector("role"
			// ,
			// new Template( new ActualTemplateField("role"),
			// new FormalTemplateField(String.class))
			) {
				@Override
				protected Object doEval(Tuple... t) {
					return scenario.getRole(robotIndex);
				}
			});

			n.addAttributeCollector(new AttributeCollector("victim_perceived",
					new Template(new ActualTemplateField("VICTIM_PERCEIVED"), new FormalTemplateField(Boolean.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					return t[0].getElementAt(Boolean.class, 1);
				}
			});
			n.addAttributeCollector(new AttributeCollector("walking_attribute",
					new Template(new ActualTemplateField("WALKING"), new FormalTemplateField(Boolean.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					return t[0].getElementAt(Boolean.class, 1);
				}
			});

			n.addAttributeCollector(new AttributeCollector("direction_attribute",
					new Template(new ActualTemplateField("DIRECTION"), new FormalTemplateField(Double.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					return t[0].getElementAt(Double.class, 1);
				}
			});

			n.addAttributeCollector(new AttributeCollector("collision_attribute",
					new Template(new ActualTemplateField("COLLISION"), new FormalTemplateField(Boolean.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					return t[0].getElementAt(Boolean.class, 1);
				}
			});

			n.addObserver(new Observer() {

				@Override
				public void update(Observable o, Object arg) {

					// System.out.println(n.getName() + "->" +
					// n.getAttribute("victim_perceived") +
					// n.getAttribute("walking_attribute") +
					// n.getAttribute("collision_attribute") +
					// n.getAttribute("direction_attribute")
					// );

				}
			});

			Agent a = new RandomWalk(i);
			n.addAgent(a);

			// Agent that checks walking status. Used for battery level.
			// Contains also policy of LowBattery State
			IsMoving lowBattery = new IsMoving(i);
			n.addAgent(lowBattery);

			// Explorer needs the scenario class in order to get the victim
			// position when found
			Explorer e = new Explorer(i, scenario);
			n.addAgent(e);

			HelpRescuer h = new HelpRescuer(i, scenario);
			n.addAgent(h);

			nodes.put(n.getName(), n);
		}

		// starting threads
		for (Node n : nodes.values()) {
			n.start();
		}
	}

	private void init() {
		JPanel panel = new JPanel();
		internal = new SpatialPanel(this.scenario);
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(internal), BorderLayout.CENTER);
		setContentPane(panel);
		pack();
	}

	public static void main(String[] argv) {
		int numRobots = inputRobots("Number of robots", 20);
		int numRescuerSwarmSize = inputRobots("Size of RESCUER swarm", 4);
		// double width = inputHeightWidth("Arena width", 500);
		// double height = inputHeightWidth("Arena height", 500);
		new Main(numRobots, numRescuerSwarmSize, HEIGHT, WIDTH);
	}

	public static int inputRobots(String message, int value) {
		while (true) {
			String size = JOptionPane.showInputDialog(message, value);
			try {
				return Integer.parseInt(size);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, size + " is not an integer!", "Error...",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static double inputHeightWidth(String message, double value) {
		while (true) {
			String size = JOptionPane.showInputDialog(message, value);
			try {
				return Double.parseDouble(size);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, size + " is not a double!", "Error...", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
