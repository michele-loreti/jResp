package test;



import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.KnowledgeManager;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class test_NodeResources {

	public static void main(String[] argv) {
		
		Node node;
		int i = 0;

		while (true) {
			node = new Node("TS", new TupleSpace());
			Agent A = new A("n" + i);
			node.addAgent(A);
			node.start();
			node.stop();
			i++;
			try {
				// Crashes after few hundreds nodes are created
				Thread.sleep(1);
				// Does not crash (perhaps because the garbage collector has time to do his job?)
				//Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

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
				int N = 1;
				
				// Produce
//				for (int i = 0; i < N; i++) {
//					put(new Tuple(i), Self.SELF);
//				}
				
				
				System.out.println(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
