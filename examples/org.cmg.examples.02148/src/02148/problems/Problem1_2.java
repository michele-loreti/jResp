package problems;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class Problem1_2 {

	public static void main(String[] argv) {
		Node Appartment = new Node("fridge", new TupleSpace());
		Agent Alice = new Producer("Alice");
		Agent Bob = new ConsumerB("Charlie");
		Agent Charlie = new ConsumerB("Bob");
		Appartment.addAgent(Alice);
		Appartment.addAgent(Bob);
		Appartment.addAgent(Charlie);
		Appartment.start();
	}

	public static class Producer extends Agent {

		public Producer(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			try {
				System.out.println(name + " adding items to the grocery list...");
				System.out.println(name + " adding one bottle(s) of milk");
				put(new Tuple("milk",1), Self.SELF);
				System.out.println(name + " adding one piece of soap");
				put(new Tuple("soap",2), Self.SELF);
				System.out.println(name + " adding three piecess of butter");
				put(new Tuple("butter",3), Self.SELF);
				System.out.println(name + " adding four oranges");
				put(new Tuple("orange",4), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class ConsumerA extends Agent {

		public ConsumerA(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			Tuple t;
			Template what = new Template(
					new FormalTemplateField(String.class),
					new FormalTemplateField(Integer.class)
					);
			try {
				while (true) {
					for(int i=1; i<=3; i++){
						t = get(what, Self.SELF);
						// add item to some local list
						System.out.println("    " + name + " will shop " + t.getElementAt(Integer.class, 1) + " units of " + t.getElementAt(String.class, 0) + "...");
					}
					System.out.println("    " + name + " shopping...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static class ConsumerB extends Agent {

		public ConsumerB(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			Tuple t;
			Template what = new Template(
					new FormalTemplateField(String.class),
					new FormalTemplateField(Integer.class)
					);
			try {
				Thread.sleep(3000);
				System.out.println("    " + name + " waking up...");
				while (true) {
					t = getp(what);
					if (t == null) {
						break;
					}
					System.out.println("    " + name + " will shop " + t.getElementAt(Integer.class, 1) + " units of " + t.getElementAt(String.class, 0) + "...");
				}
				System.out.println("    " + name + " shopping...");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
