package problems;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class Problem1_1 {

	public static void main(String[] argv) {
		Node Appartment = new Node("fridge", new TupleSpace());
		Agent Alice = new Producer("Alice");
		Agent Bob = new Producer("Charlie");
		Agent Charlie = new ConsumerDrugs("Bob");
		Agent Dave = new ConsumerFood("Dave");
		Agent Eve = new ConsumerAll("Eve");
		Appartment.addAgent(Alice);
		Appartment.addAgent(Bob);
		Appartment.addAgent(Charlie);
		Appartment.addAgent(Dave);
		Appartment.addAgent(Eve);
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
				put(new Tuple("milk","food",1), Self.SELF);
				System.out.println(name + " adding one piece of soap");
				put(new Tuple("soap","drug",2), Self.SELF);
				System.out.println(name + " adding three piecess of butter");
				put(new Tuple("butter","food",3), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class ConsumerAll extends Agent {

		public ConsumerAll(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					Template what = new Template(
							new FormalTemplateField(String.class),
							new FormalTemplateField(String.class),
							new FormalTemplateField(Integer.class)
							);
					Tuple t = get(what, Self.SELF);
					System.out.println("    " + name + " shopping " + t.getElementAt(Integer.class, 2) + " units of " + t.getElementAt(String.class, 0) + "...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static class ConsumerDrugs extends Agent {

		public ConsumerDrugs(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					Template what = new Template(
							new FormalTemplateField(String.class),
							new ActualTemplateField("drug"),
							new FormalTemplateField(Integer.class)
							);
					Tuple t = get(what, Self.SELF);
					System.out.println("    " + name + " shopping " + t.getElementAt(Integer.class, 2) + " units of " + t.getElementAt(String.class, 0) + "...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static class ConsumerFood extends Agent {

		public ConsumerFood(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			try {
				while (true) {
					Template what = new Template(
							new FormalTemplateField(String.class),
							new ActualTemplateField("food"),
							new FormalTemplateField(Integer.class)
							);
					Tuple t = get(what, Self.SELF);
					System.out.println("    " + name + " shopping " + t.getElementAt(Integer.class, 2) + " units of " + t.getElementAt(String.class, 0) + "...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
