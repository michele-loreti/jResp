package test;

import java.util.Iterator;
import java.util.LinkedList;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class test_getAll {

	public static void main(String[] argv) {

		Node node = new Node("TS", new TupleSpace());
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
				for (int i = 0; i < N; i++) {
					put(new Tuple(i), Self.SELF);
					put(new Tuple(i,i), Self.SELF);
					put(new Tuple("blah"), Self.SELF);
				}
				LinkedList<Tuple> L;
				L = getAll(new Template(new FormalTemplateField(Integer.class)));
				Iterator<Tuple> itr = L.iterator();
				while(itr.hasNext()) {
					Tuple t = itr.next();
					System.out.println(t.getElementAt(Integer.class,0));
				}
				System.out.println("Done.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
