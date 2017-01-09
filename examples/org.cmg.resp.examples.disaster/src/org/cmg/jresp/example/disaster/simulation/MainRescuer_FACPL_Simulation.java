/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universit? di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package org.cmg.jresp.example.disaster.simulation;

import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.AttributeCollector;
import org.cmg.jresp.examples.disaster.rescuer.facpl.Explorer;
import org.cmg.jresp.examples.disaster.rescuer.facpl.HelpRescuer;
import org.cmg.jresp.examples.disaster.rescuer.facpl.IsMoving;
import org.cmg.jresp.examples.disaster.rescuer.facpl.RandomWalk;
import org.cmg.jresp.examples.disaster.rescuer.facpl.Scenario;
import org.cmg.jresp.examples.disaster.rescuer.facpl.SpatialPanel;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.policy.ActionThisID;
import org.cmg.jresp.policy.RequestAttributeName;
import org.cmg.jresp.policy.facpl.FacplPolicyState;
import org.cmg.jresp.policy.facpl.algorithm.PermitUnlessDeny;
import org.cmg.jresp.policy.facpl.elements.TargetConnector;
import org.cmg.jresp.policy.facpl.elements.TargetExpression;
import org.cmg.jresp.policy.facpl.elements.util.TargetTreeRepresentation;
import org.cmg.jresp.policy.facpl.function.comparison.Equal;
import org.cmg.jresp.policy.facpl.function.comparison.LessThan;
import org.cmg.jresp.policy.facpl.function.comparison.PatternMatch;
import org.cmg.jresp.simulation.DeterministicDelayFactory;
import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationAction;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.simulation.SimulationScheduler;
import org.cmg.jresp.simulation.policy.SimulationPolicyAutomaton;

/**
 * 
 * @author Andrea Margheri
 *
 */
public class MainRescuer_FACPL_Simulation extends JFrame {

	private static final long serialVersionUID = 1L;
	private Scenario scenario;
	private JPanel internal;

	private static final double HEIGHT = 500;
	private static final double WIDTH = 350;

	public MainRescuer_FACPL_Simulation(int robots, int numSwarmRescuer, double height, double width) {
		super("Disaster scenario in jRESP by using FACPL policies");
		scenario = new Scenario(robots, numSwarmRescuer, Scenario.victim_number, height, width);
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
				new DeterministicDelayFactory(1.0), scenario.getNodeConnection());

		Hashtable<String, SimulationNode> nodes = new Hashtable<String, SimulationNode>();
		sim.schedulePeriodicAction(new SimulationAction() {

			@Override
			public void doAction(double time) {
				scenario.step(0.1);
			}

		}, 0.1, 0.1);

		for (int i = 0; i < scenario.getRobots(); i++) {
			final SimulationNode n = new SimulationNode("" + i, env);

			final int robotIndex = i;

			/**
			 * Actuators
			 */
			n.addActuator(scenario.getChangeRoleActuator(i));
			n.addActuator(scenario.getUpdateVictimStateActuator(i));
			n.addActuator(scenario.getDirectionActuator(i));
			n.addActuator(scenario.getPointDirectionActuator(i));
			n.addActuator(scenario.getStopActuator(i));
			n.addActuator(scenario.getChargingBatteryActuator(i));
			/**
			 * Sensors
			 */
			n.addSensor(scenario.getCollisionSensor(i));
			n.addSensor(scenario.getVictimSensor(i));
			n.addSensor(scenario.getWalkingSensor(i));
			n.addSensor(scenario.getDirectionSensor(i));
			n.addSensor(scenario.getBatteryLevelSensor(i));
			// Used for signal the end of Charging Process
			n.addSensor(scenario.getBatteryChargedSensor(i));

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

			n.addAttributeCollector(
					new AttributeCollector("victim_perceived", new Template(new ActualTemplateField("VICTIM_PERCEIVED"),
							new FormalTemplateField(Boolean.class), new FormalTemplateField(Integer.class))) {

						@Override
						protected Object doEval(Tuple... t) {
							if (t[0] == null) {
								return false;
							} else {
								return t[0].getElementAt(Boolean.class, 1);
							}
						}
					});
			n.addAttributeCollector(new AttributeCollector("walking_attribute",
					new Template(new ActualTemplateField("WALKING"), new FormalTemplateField(Boolean.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					if (t[0] == null) {
						return false;
					} else {
						return t[0].getElementAt(Boolean.class, 1);
					}
				}
			});

			n.addAttributeCollector(new AttributeCollector("direction_attribute",
					new Template(new ActualTemplateField("DIRECTION"), new FormalTemplateField(Double.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					if (t[0] == null) {
						return scenario.getDirection(robotIndex);
					} else {
						return t[0].getElementAt(Double.class, 1);
					}
				}
			});

			n.addAttributeCollector(new AttributeCollector("collision_attribute",
					new Template(new ActualTemplateField("COLLISION"), new FormalTemplateField(Boolean.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					if (t[0] == null) {
						return false;
					} else {
						return t[0].getElementAt(Boolean.class, 1);
					}
				}
			});

			/*
			 * ATTRIBUTE COLLECTORs for battery management: 1 - battery level 2
			 * - whether battery is under re-charging or not
			 */

			n.addAttributeCollector(new AttributeCollector("battery_level",
					new Template(new ActualTemplateField("BATTERY_LEVEL"), new FormalTemplateField(Double.class))) {

				@Override
				protected Object doEval(Tuple... t) {
					if (t[0] == null) {
						return scenario.getBattery(robotIndex);
					} else {
						return t[0].getElementAt(Double.class, 1);
					}
				}
			});

			n.addAttributeCollector(new AttributeCollector("under_recharging"
			// , new Template(
			// new ActualTemplateField("CHARGING"),
			// new FormalTemplateField(Boolean.class))
			) {

				@Override
				protected Object doEval(Tuple... t) {
					return scenario.getUnderRecharging(robotIndex);
				}
			});

			// -----------------------------

			n.addObserver(new Observer() {

				@Override
				public void update(Observable o, Object arg) {

					// System.out.println(n.getName() + "->" +
					// n.getAttribute("battery_level") +
					// n.getAttribute("under_recharging")
					// );

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

			IsMoving isMoving = new IsMoving(i);
			n.addAgent(isMoving);

			// Explorer needs the scenario class in order to get the victim
			// position when found
			Explorer e = new Explorer(i, scenario);
			n.addAgent(e);

			HelpRescuer h = new HelpRescuer(i, scenario);
			n.addAgent(h);

			// create the PolicyAutomaton with four states [from 0 to 1] as a
			// SimulationPolicyAutomaton Node
			final SimulationPolicyAutomaton policy_automaton = new SimulationPolicyAutomaton(
					new FacplPolicyState(PermitUnlessDeny.class, e.getPolicyExplorer()),
					new FacplPolicyState(PermitUnlessDeny.class, e.getPolicyRescuer()),
					new FacplPolicyState(PermitUnlessDeny.class, h.getPolicyHelpRescuer()),
					new FacplPolicyState(PermitUnlessDeny.class, isMoving.getPolicyLowBattery()));

			// add Automaton Transitions
			// 1 - From Explorer [0] to HelpRescuer [2]

			TargetTreeRepresentation cond_from0to2 = new TargetTreeRepresentation(TargetConnector.AND,
					new TargetTreeRepresentation(new TargetExpression(Equal.class,
							new RequestAttributeName("action", "id"), ActionThisID.PUT)),
					new TargetTreeRepresentation(new TargetExpression(PatternMatch.class,
							new RequestAttributeName("action", "arg"), new Template(new ActualTemplateField("role"),
									new ActualTemplateField(Scenario.HELP_RES)))));

			policy_automaton.addTransitionRule(0, 2, cond_from0to2);

			// 2 - From Explorer [0] to Rescuer [1]
			TargetTreeRepresentation cond_from0to1 = new TargetTreeRepresentation(TargetConnector.AND,
					new TargetTreeRepresentation(new TargetExpression(Equal.class,
							new RequestAttributeName("action", "id"), ActionThisID.PUT)),
					new TargetTreeRepresentation(
							new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
									new Template(new ActualTemplateField("rescue")))));

			policy_automaton.addTransitionRule(0, 1, cond_from0to1);

			// 3 - From HelpRescuer [2] to Rescuer [1]
			TargetTreeRepresentation cond_from2to1 = new TargetTreeRepresentation(TargetConnector.AND,
					new TargetTreeRepresentation(new TargetExpression(Equal.class,
							new RequestAttributeName("action", "id"), ActionThisID.PUT)),
					new TargetTreeRepresentation(
							new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
									new Template(new ActualTemplateField("rescue")))));

			policy_automaton.addTransitionRule(2, 1, cond_from2to1);

			// 4 - From Explorer [0] to LowBattery [3]
			TargetTreeRepresentation cond_from0to3 = new TargetTreeRepresentation(TargetConnector.AND,
					new TargetTreeRepresentation(new TargetExpression(Equal.class,
							new RequestAttributeName("action", "id"), ActionThisID.QRY)),
					new TargetTreeRepresentation(
							new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
									new Template(new ActualTemplateField("WALKING"), new ActualTemplateField(true)))),
					new TargetTreeRepresentation(new TargetExpression(LessThan.class,
							new RequestAttributeName("object", "battery_level"), Scenario.dechargedBattery)));

			policy_automaton.addTransitionRule(0, 3, cond_from0to3);

			// 5 - From LowBattery [3] to Explorer [0]
			TargetTreeRepresentation cond_from3to0 = new TargetTreeRepresentation(TargetConnector.AND,
					new TargetTreeRepresentation(new TargetExpression(Equal.class,
							new RequestAttributeName("action", "id"), ActionThisID.PUT)),
					new TargetTreeRepresentation(new TargetExpression(PatternMatch.class,
							new RequestAttributeName("action", "arg"), new Template(new ActualTemplateField("role"),
									new ActualTemplateField(Scenario.EXPLORER)))));

			policy_automaton.addTransitionRule(3, 0, cond_from3to0);

			// Add node to simulation PolicyAutomaton Node
			n.setPolicy(policy_automaton);

			nodes.put(n.getName(), n);
		}

		env.simulate(Scenario.simulationSteps);
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
		new MainRescuer_FACPL_Simulation(numRobots, numRescuerSwarmSize, HEIGHT, WIDTH);
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
