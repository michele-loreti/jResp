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
package org.cmg.jresp.example.disaster.simulation;

import java.awt.BorderLayout;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.AttributeCollector;
import org.cmg.jresp.examples.disaster.DataForwarder;
import org.cmg.jresp.examples.disaster.GoToVictim;
import org.cmg.jresp.examples.disaster.RandomWalk;
import org.cmg.jresp.examples.disaster.Scenario;
import org.cmg.jresp.examples.disaster.SpatialPanel;
import org.cmg.jresp.examples.disaster.VictimSeeker;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.simulation.ExponentialDelayFactory;
import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationAction;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.simulation.SimulationScheduler;

/**
 * @author Michele Loreti
 *
 */
public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Scenario scenario;
	private JPanel internal;
	private JTable table;

	private static final double HEIGHT = 700;
	private static final double WIDTH = 550;

	public Main(int landmarks, int workers, double height, double width) {
		super("Disaster scenario in jRESP");
		scenario = new Scenario(landmarks, workers, 1, height, width);
		scenario.init();
		init();
		setLocation(550, 100);
		setVisible(true);
		instantiateNet();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void instantiateNet() {
		Random r = new Random();
		SimulationScheduler sim = new SimulationScheduler();
		SimulationEnvironment env = new SimulationEnvironment(sim, new RandomSelector(r),
				new ExponentialDelayFactory(r, 1.0), scenario.getNodeConnection());

		Hashtable<String, SimulationNode> nodes = new Hashtable<String, SimulationNode>();
		sim.schedulePeriodicAction(new SimulationAction() {

			@Override
			public void doAction(double time) {
				scenario.step(1);
			}

		}, 0.1, 0.1);

		for (int i = 0; i < scenario.getLandmarks(); i++) {
			final SimulationNode n = new SimulationNode("" + i, env);
			n.addActuator(scenario.getDirectionActuator(i));
			n.addSensor(scenario.getCollisionSensor(i));
			n.addActuator(scenario.getStopActuator(i));
			n.addSensor(scenario.getVictimSensor(i));
			n.addSensor(scenario.getWalkingSensor(i));
			n.addSensor(scenario.getDirectionSensor(i));
			n.addSensor(scenario.getPositionSensor(i));

			n.put(new Tuple("role", Scenario.LANDMARK));

			n.addAttributeCollector(new AttributeCollector("role",
					new Template(new ActualTemplateField("role"), new FormalTemplateField(String.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					return t[0].getElementAt(String.class, 1);
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
			n.addAttributeCollector(new AttributeCollector("position_attribute",
					new Template(new ActualTemplateField("POSITION"), new FormalTemplateField(Point2D.Double.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					return t[0].getElementAt(Point2D.class, 1);
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

			// n.addObserver(new Observer() {
			//
			// @Override
			// public void update(Observable o, Object arg) {
			// System.out.println(n.getName() + "> Interface updated: " +
			// n.getInterface()
			// );
			// }
			// });

			Agent a = new RandomWalk();
			n.addAgent(a);
			a = new VictimSeeker(i);
			n.addAgent(a);
			a = new DataForwarder(i);
			n.addAgent(a);
			nodes.put(n.getName(), n);
		}

		for (int i = scenario.getLandmarks(); i < (scenario.getLandmarks() + scenario.getWorkers()); i++) {
			final SimulationNode n = new SimulationNode("" + i, env);
			n.addActuator(scenario.getDirectionActuator(i));
			n.addSensor(scenario.getCollisionSensor(i));
			n.addActuator(scenario.getStopActuator(i));
			n.addSensor(scenario.getVictimSensor(i));
			n.addSensor(scenario.getWalkingSensor(i));
			n.addSensor(scenario.getDirectionSensor(i));
			n.addSensor(scenario.getPositionSensor(i));

			n.put(new Tuple("role", Scenario.WORKER));

			n.addAttributeCollector(new AttributeCollector("role",
					new Template(new ActualTemplateField("role"), new FormalTemplateField(String.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					return t[0].getElementAt(String.class, 1);
				}
			});
			n.addAttributeCollector(new AttributeCollector("walking_attribute",
					new Template(new ActualTemplateField("WALKING"), new FormalTemplateField(Boolean.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					return t[0].getElementAt(Boolean.class, 1);
				}
			});
			n.addAttributeCollector(new AttributeCollector("position_attribute",
					new Template(new ActualTemplateField("POSITION"), new FormalTemplateField(Point2D.Double.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					return t[0].getElementAt(Point2D.class, 1);
				}
			});

			n.addAttributeCollector(new AttributeCollector("direction_attribute",
					new Template(new ActualTemplateField("DIRECTION"), new FormalTemplateField(Double.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					return t[0].getElementAt(Double.class, 1);
				}
			});

			n.addObserver(new Observer() {

				@Override
				public void update(Observable o, Object arg) {
					// System.out.println(n.getName() + "> Interface updated: "
					// +
					// n.getInterface()
					// );
				}
			});

			Agent a = new GoToVictim(i, scenario);
			n.addAgent(a);
			// System.out.println(n.getName() + "> Interface updated: "
			// +n.getInterface());
			nodes.put(n.getName(), n);
			// a = new VictimSeeker(i);
			// n.addAgent(a);
		}

		env.simulate(5000);
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
		int numLandmarks = inputRobots("Number of LANDMARK robots", 20);
		int numWorkers = inputRobots("Number of WORKER robots", 4);
		// double width = inputHeightWidth("Arena width", 500);
		// double height = inputHeightWidth("Arena height", 500);
		new Main(numLandmarks, numWorkers, HEIGHT, WIDTH);
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
