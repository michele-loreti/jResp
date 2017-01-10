/**
 * 
 */
package org.cmg.jresp.examples.construction;

import org.cmg.jresp.examples.construction.behaviour.AverageTimeCollector;
import org.cmg.jresp.examples.construction.simulation.DoorNode;
import org.cmg.jresp.examples.construction.simulation.Worker;

import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cmg.jresp.simulation.ExponentialDelayFactory;
import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationAction;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.simulation.SimulationScheduler;

/**
 * @author loreti
 *
 */
public class Main extends JFrame {

	protected Scenario scenario;
	private JPanel internal;
	
	public Main( int worker , int food , int width , int height ) {
		super( "Robotic scenario in jRESP");
		scenario = new Scenario(worker, food, height, width);
		scenario.init();
		init();
		setLocation(550, 100);
		setVisible(true);
		instantiateNet();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	protected void instantiateNet( ) {
		Random r = new Random();
		SimulationScheduler sim = new SimulationScheduler();
		SimulationEnvironment env = new SimulationEnvironment(sim, new RandomSelector(r), new ExponentialDelayFactory(r,1.0),scenario.getNodeConnection());
		Hashtable<String, SimulationNode> nodes = new Hashtable<String, SimulationNode>();
		sim.schedulePeriodicAction(new SimulationAction() {
			
			private double next = 0.0;
			
			@Override
			public void doAction(double time) {
				scenario.step(1);
				if (time > next) {
					System.out.println(next+" "+scenario.getElements());
//					System.out.println(next+" "+scenario.getElementsIn1());
//					System.out.println(next+" "+scenario.getElementsIn2());
					next += 10.0;
				}
			}
			
		}, 0.1, 0.1);

		final AverageTimeCollector foodTimeCollector = new AverageTimeCollector();
		final AverageTimeCollector toNestTimeCollector = new AverageTimeCollector();
		final AverageTimeCollector waitingTimeCollector = new AverageTimeCollector();
		final AverageTimeCollector inOutTimeCollector = new AverageTimeCollector();
		for( int i=0 ; i<scenario.getSize() ; i++ ) {
			Worker w = new Worker(scenario, env, "W"+i , foodTimeCollector , toNestTimeCollector , waitingTimeCollector , inOutTimeCollector );
			nodes.put("W"+i, w);
		}
		
		DoorNode d1 = new DoorNode(scenario, env, 1);		
		nodes.put("D1", d1);
		DoorNode d2 = new DoorNode(scenario, env, 2);
		nodes.put("D2", d2);

		env.simulate(3000);
		env.schedule( new SimulationAction() {
			
			@Override
			public void doAction(double time) {
				System.out.println("Average food: "+foodTimeCollector.average());
				System.out.println("Average to nest: "+toNestTimeCollector.average());
				System.out.println("Average waiting time: "+waitingTimeCollector.average());
				System.out.println("In/Out time: "+inOutTimeCollector.average());
			}
		}, 6000);
	}
	
	private void init() {
		JPanel panel = new JPanel();
		internal = new SpatialPanel(this.scenario);
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(internal), BorderLayout.CENTER);		
		setContentPane(panel);
		pack();
	}

	public static void main( String[] argv ) {
		int robots = inputRobots("Number of robots", 50);		
		int food = inputRobots("Number of sandbags", 5);		
		new Main( robots , food , 600 , 600 );		
	}
	
	public static int inputRobots(String message , int value) {
		while (true) {
			String size = JOptionPane.showInputDialog(message,value);
			try {
				return Integer.parseInt(size);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, size+" is not an integer!", "Error...", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
