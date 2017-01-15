package test;



import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.KnowledgeManager;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.knowledge.ts.fifo.FIFOSpace;
import org.cmg.jresp.knowledge.ts.stack.StackSpace;
import org.cmg.jresp.knowledge.ts.random.RandomSpace;
import org.cmg.jresp.topology.Self;

public class test_Spaces {

	public static void main(String[] argv) {

		KnowledgeManager K = new TupleSpace();
		//KnowledgeManager K = new RandomSpace();
		//KnowledgeManager K = new FIFOSpace();
		//KnowledgeManager K = new StackSpace();
		
		Node node = new Node("TS", K);
		Agent A = new A("A");
		node.addAgent(A);
		node.start();

	}

	public static class A extends Agent {

		// This constructor records the name of the agent
		public A(String name) {
			super(name);
		}
		// This is the function invoked when the agent starts running in a node
		@Override
		protected void doRun() {
			try {
				int N = 10;
				
				// Produce
				for (int i = 0; i < N; i++) {
					put(new Tuple(i), Self.SELF);
				}
				
				// Consume
				for (int i = 0; i < N; i++) {
					Tuple t = get(new Template(new FormalTemplateField(Integer.class)), Self.SELF);
					System.out.println(t.getElementAt(Integer.class,0));
				}
				
				System.out.println("Done.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
