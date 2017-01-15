package examples;

import java.util.LinkedList;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class Chapter_3_GetThemAll {

	public static void main(String[] argv) {

		// We create a node for the Appartment
		Node Appartment = new Node("Appartment", new TupleSpace());

		// We create Alice and Bob as Producer/Consumer agents
		// The constructor of agents takes the name of the agent as argument
		Agent Alice = new Producer("Alice");
		Agent Bob = new Consumer("Bob");
		// We add both agents to the node
		Appartment.addAgent(Alice);
		Appartment.addAgent(Bob);
		// We start the node
		Appartment.start();

	}

	public static class Producer extends Agent {

		// This constructor records the name of the agent
		public Producer(String name) {
			super(name);
		}

		// This is the function invoked when the agent starts running in a node
		@Override
		protected void doRun() {
			try {
				System.out.println(name + " adding items to the grocery list...");
				System.out.println(name + " adding milk(1)");
				put(new Tuple("milk",1), Self.SELF);
				System.out.println(name + " adding soap(2)");
				put(new Tuple("soap",2), Self.SELF);
				System.out.println(name + " adding butter(3)");
				put(new Tuple("butter",3), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class Consumer extends Agent {

		public Consumer(String name) {
			super(name);
		}

		@Override
		protected void doRun() {
			// Note how templates are created in jRESP
			// In place of binding variables we need to use so-called formal fields
			// We will see later how these fields can be saved into variables
			// The formal field constructor needs a class as parameter
			Template what = new Template(
					new FormalTemplateField(String.class),
					new FormalTemplateField(Integer.class)
					);
			// The tuple is necessary to capture the result of a get operation
			LinkedList<Tuple> L;
			try {
				while (true) {
					// The getAll operation returns a list of tuples, that we save into L
					L = getAll(what);
					// If we got no tuple, then we sleep for a while and try later
					if (L.isEmpty()) {
						Thread.sleep(1000);
						continue;
					}
					// Otherwise we go shopping
					System.out.println(name + " shopping " + L.toString() + "...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
