/**
 * 
 */
package org.cmg.jresp.examples.cloud;

import java.util.Hashtable;
import java.util.Random;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.simulation.DeterministicDelayFactory;
import org.cmg.jresp.simulation.RandomSelector;
import org.cmg.jresp.simulation.SimulationAction;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.simulation.SimulationScheduler;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.Target;
import org.cmg.jresp.topology.VirtualPort;

/**
 * @author Giorgio
 *
 */
public class Main {
	private Scenario scenario;
	private Random r = new Random();
	private Target id;
	
	public Main(int size){
		this( new Scenario(size, new Random() , 900, 1000, 99, 100) );
	}
	
	public Main(Scenario scenario) {
		this.scenario = scenario;
		instantiateNet();
	}

	private void instantiateNet() {
		Random r = new Random();
		SimulationScheduler sim = new SimulationScheduler();
		SimulationEnvironment env = new SimulationEnvironment(sim, new RandomSelector(r), new DeterministicDelayFactory(1.0) );
		sim.schedulePeriodicAction(new SimulationAction() {
			
			@Override
			public void doAction(double time) {
				System.out.println("TIME "+time+": "+scenario.getInfo());
			}
			
		}, 0.0 , 1000.0);
		scenario.setEnvironment( env );
		Hashtable<String, SimulationNode> nodes = new Hashtable<String, SimulationNode>();
		for(int i=0; i<scenario.getSize(); i++){
			SimulationNode n = new SimulationNode("Nodo"+i, env );
			n.addSensor(scenario.getCpuSensor(i));
			n.addSensor(scenario.getMemorySensor(i));
			n.addActuator(scenario.getServiceInvocationActuator(i, n));
			n.addAttributeCollector(scenario.getCpuLoadAttributeCollector());
			n.addAttributeCollector(scenario.getCpuRateAttributeCollector(i));
			n.addAttributeCollector(scenario.getMemoryAttributeCollector());
			if (i==0) {
				n.put(new Tuple("REQUEST", 1, new CloudService("1", 10, 2.0), n.getLocalAddress() ));
			}
			n.put( new Tuple( "LOCATION" , n.getLocalAddress() ) ); 
		    Agent a= new RequestHandler(); 
			n.addAgent(a);
			a=new OfferAgent();
			n.addAgent(a);
			nodes.put(n.getName(), n);
		}
		env.simulate(10000);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int size=3;
		CloudComponent c0 = new CloudComponent( 0 , 0 , 1 );
		CloudComponent c1 = new CloudComponent( 1 , 100 , 1 );
		CloudComponent c2 = new CloudComponent( 2 , 100 , 1 );
		new Main( new Scenario( c0 , c1 , c2 ));
//		new Main(size);

	}

}
