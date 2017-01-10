package problems;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class Problem1_5a {

	public static void main(String[] argv) {
		Node Appartment = new Node("fridge", new TupleSpace());
		Agent Alice = new Alice("Alice");
		Agent Bob = new Bob("Bob");
		Appartment.addAgent(Alice);
		Appartment.addAgent(Bob);
		Appartment.start();
	}

	public static class Alice extends Agent {

		public Alice(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			try {
				System.out.println(name + " starting protocol...");
				put(new Tuple("we need milk"), Self.SELF);
				get(new Template(new ActualTemplateField("here it is")), Self.SELF);
				put(new Tuple("thanks"), Self.SELF);
				System.out.println(name + " done!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class Bob extends Agent {

		public Bob(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			try {
				System.out.println(name + " starting protocol...");
				get(new Template(new ActualTemplateField("we need milk")), Self.SELF);
				put(new Tuple("here it is"), Self.SELF);
				get(new Template(new ActualTemplateField("thanks")), Self.SELF);
				System.out.println(name + " done!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}