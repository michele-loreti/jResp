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
package org.cmg.res.examples.robotic;

import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.AttributeCollector;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.simulation.DeterministicDelayFactory;
import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationAction;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.simulation.SimulationScheduler;
import org.cmg.jresp.topology.VirtualPort;


/**
 * @author Michele Loreti
 *
 */
public class MainPerformance extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Scenario scenario;
	private JPanel internal;
	private JTable table;
	private SimulationEnvironment env;


	public MainPerformance( int size , double height , double width , BatteryConsumptionFunction batteryDischargingFunction ) {
		super( "Robotic scenario in jRESP");
		scenario = new Scenario(size, height, width, batteryDischargingFunction);
		init();
		setVisible(true);
		instantiateNet();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	private void instantiateNet() {
		Random r = new Random();
		final SimulationScheduler sim = new SimulationScheduler();
		env = new SimulationEnvironment(sim, new RandomSelector(r), new DeterministicDelayFactory(10.0),scenario.getNodeConnection());

		sim.schedulePeriodicAction(new SimulationAction() {
			
			@Override
			public void doAction(double time) {
				System.out.println("TIME: "+time);
				scenario.step(0.1);
				if (scenario.goalReached()) {
					sim.stopSimulation();
				}
			}
			
		}, 0.1, 0.1);

		Hashtable<String, SimulationNode> nodes = new Hashtable<String, SimulationNode>();
		for (int i=0 ;i<scenario.getSize();i++) {
			SimulationNode n = new SimulationNode(""+i, env);
			n.addActuator(scenario.getDirectionActuator(i));
			n.addSensor(scenario.getLocationSensor(i));
			n.addActuator(scenario.getStopActuator(i));
			n.addSensor(scenario.getBatterySensor(i));
			n.addSensor(scenario.getTargetSensor(i));
			n.put(new Tuple( "controlStep" , new RandomWalk() ));
			n.put(new Tuple( "lowBattery" , false ));
			n.put(new Tuple( "informed" , false));
			n.put(new Tuple( "task" , i%2) );
			n.addAttributeCollector( new AttributeCollector("task", 
					new Template( new ActualTemplateField( "task"),
								new FormalTemplateField(Integer.class)
							)
			) {
				
				@Override
				protected Object doEval(Tuple ... t) {
					return t[0].getElementAt(Integer.class, 1);
				}
			});
			Agent a = new ManagedElement();
			n.addAgent(a);
			a = new TargetSeeker();
			n.addAgent(a);
			a= new BatteryMonitor();
			n.addAgent(a);
			a = new DataSeeker(i%2);
			n.addAgent(a);
			nodes.put(n.getName(), n);
		}
		env.simulate(5000);

//		for (Node n: nodes.values()) {
//			n.start();
//		}
	}


	private void init() {
		JPanel panel = new JPanel();
		internal = new SpatialPanel(this.scenario);
//		table = new JTable(new BatteryLevelData(scenario));
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(internal), BorderLayout.CENTER);
//		panel.add(new JScrollPane(table) , BorderLayout.EAST );
		setContentPane(panel);
		pack();
	}
	
	


	public static void main( String[] argv ) {
		int size = inputSize();
		double width = inputHeightWidth("Arena width", 500);
		double height = inputHeightWidth("Arena height", 500);
		new MainPerformance( size , height , width , new LinearConsumptionFunction(0.0001) );
	}
	
	public static int inputSize() {
		while (true) {
			String size = JOptionPane.showInputDialog("Number of robots",25);
			try {
				return Integer.parseInt(size);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, size+" is not an integer!", "Error...", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public static double inputHeightWidth( String message , double value ) {
		while (true) {
			String size = JOptionPane.showInputDialog(message,value);
			try {
				return Double.parseDouble(size);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, size+" is not a double!", "Error...", JOptionPane.ERROR_MESSAGE);
			}
		}		
	}


//	@Override
//	public void update(Observable arg0, Object arg1) {
//		internal.repaint();
//		table.repaint();
//	}


}
