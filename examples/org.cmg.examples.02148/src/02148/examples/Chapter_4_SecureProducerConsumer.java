package examples;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.policy.ActionThisID;
import org.cmg.jresp.policy.RequestAttributeName;
import org.cmg.jresp.policy.automaton.PolicyAutomaton;
import org.cmg.jresp.policy.facpl.FacplPolicyState;
import org.cmg.jresp.policy.facpl.RuleEffect;
import org.cmg.jresp.policy.facpl.algorithm.DenyUnlessPermit;
import org.cmg.jresp.policy.facpl.elements.PolicySet;
import org.cmg.jresp.policy.facpl.elements.Rule;
import org.cmg.jresp.policy.facpl.elements.TargetConnector;
import org.cmg.jresp.policy.facpl.elements.TargetExpression;
import org.cmg.jresp.policy.facpl.elements.util.TargetTreeRepresentation;
import org.cmg.jresp.policy.facpl.function.comparison.Equal;
import org.cmg.jresp.policy.facpl.function.comparison.PatternMatch;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.VirtualPort;

public class Chapter_4_SecureProducerConsumer {
	
	// In this example we will create several nodes
	// and agents will communicate also to remote nodes.
	// For this purpose we create a virtual port to which all nodes will be connected
	protected static VirtualPort vp = new VirtualPort(8080);
	// The node that everybody knows is the bag of tasks
	protected static String bag_name = "bag";

	public static void main(String[] argv) {

		// We create a node for the bag of tasks
		Node bag = new Node(bag_name, new TupleSpace());
		bag.addPort(vp);
		
		// Access control policy for the bag
		PolicyAutomaton	p = new PolicyAutomaton();
		FacplPolicyState start = new FacplPolicyState(new Policy());
		p.addState(start);
		bag.setPolicy(p);
		bag.start();

		// We create some trusted Producer and Consumer agents
		Node Alice = new Node("Alice",new TupleSpace());
		Alice.addPort(vp);
		Agent a = new Producer("Alice");
		Alice.addAgent(a);
		Alice.start();
		
		Node Bob = new Node("Bob",new TupleSpace());
		Bob.addPort(vp);
		Agent b = new Consumer("Bob");
		Bob.addAgent(b);
		Bob.start();

		// Malicious nodes, attempting to put/get tasks
		Node Alize = new Node("Alize",new TupleSpace());
		Alize.addPort(vp);
		Agent aa = new Producer("Alize");
		Alize.addAgent(aa);
		Alize.start();
		
		Node Bohb = new Node("Bohb",new TupleSpace());
		Bohb.addPort(vp);
		Agent bb = new Consumer("Bohb");
		Bohb.addAgent(bb);
		Bohb.start();


	}

	public static class Producer extends Agent {
		
		PointToPoint bag = new PointToPoint(bag_name, vp.getAddress());

		// This constructor records the name of the agent
		public Producer(String node_name) {
			super("producer@" + node_name);
		}

		// This is the function invoked when the agent starts running in a node
		@Override
		protected void doRun() {
			try {
				System.out.println(name + " adding tasks...");
				for (int i = 0; i < 10; i++){
					String task_name = "task_by_" + name + "_" + i;
					put(new Tuple("task",task_name), bag);
					System.out.println(name + " added task " + task_name);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class Consumer extends Agent {
		
		PointToPoint bag = new PointToPoint(bag_name, vp.getAddress());

		public Consumer(String node_name) {
			super("consumer@" + node_name);
		}

		@Override
		protected void doRun() {
			// Note how templates are created in jRESP
			// In place of binding variables we need to use so-called formal fields
			// We will see later how these fields can be saved into variables
			// The formal field constructor needs a class as parameter
			Template what = new Template(
					new ActualTemplateField("task"),
					new FormalTemplateField(String.class)
					);
			// The tuple is necessary to capture the result of a get operation
			Tuple t;
			try {
				while (true) {
					System.out.println(name + " trying to get a task...");
					t = get(what, bag);
					System.out.println(name + " working on " + t.getElementAt(String.class,1) + "...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static class Policy extends PolicySet {

		public Policy() {
			addId("BagPolicy");
			addCombiningAlg(DenyUnlessPermit.class);
			addElement(new AllowAliceToPut());
			addElement(new AllowBobToGet());
		}

		class AllowAliceToPut extends Rule {

			AllowAliceToPut() {
				addId("AllowAliceToPut");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(
								TargetConnector.AND,
								new TargetTreeRepresentation(new TargetExpression(Equal.class,
										new RequestAttributeName("action", "id"), ActionThisID.ACCEPT_PUT)),
								new TargetTreeRepresentation(
										new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
												new Template(new ActualTemplateField(("task")),
														new FormalTemplateField(String.class))))),
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("subject", "id"), "Alice"))));
			}
		}
		
		class AllowBobToGet extends Rule {

			AllowBobToGet() {
				addId("AllowBobToGet");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(
								TargetConnector.AND,
								new TargetTreeRepresentation(new TargetExpression(Equal.class,
										new RequestAttributeName("action", "id"), ActionThisID.ACCEPT_GET)),
								new TargetTreeRepresentation(
										new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
												new Template(new ActualTemplateField(("task")),
														new FormalTemplateField(String.class))))),
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("subject", "id"), "Bob"))));
			}
		}

	}

}