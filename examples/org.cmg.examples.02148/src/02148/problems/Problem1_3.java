package problems;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class Problem1_3 {

	public static void main(String[] argv) {
		
		int N = 3;
		Node table = new Node("table", new TupleSpace());
		Agent[] philosopher = new Philosopher[N];

		for(int i=0; i<N; i++){
			if (i == 0) {
				philosopher[i] = new Philosopher(i,(i+1)%N,i);
			} else {
				philosopher[i] = new Philosopher(i,i,(i+1)%N);
			}
			table.addAgent(philosopher[i]);
		}
		table.start();
	}

	public static class Philosopher extends Agent {
		
		public int left;
		public int right;

		public Philosopher(int id, int left,int right) {
			super("philosopher_"+ id);
			this.left = left;
			this.right = right;
		}

		@Override
		protected void doRun() {
			Tuple fork1, fork2;
			Template left_fork = new Template(
					new ActualTemplateField("fork"),
					new ActualTemplateField(left)
					);
			Template right_fork = new Template(
					new ActualTemplateField("fork"),
					new ActualTemplateField(right)
					);
			try {
				put(new Tuple("fork",id),Self.SELF);
				while(true) {
					System.out.println(name + " getting fork " + left + "...");
					fork1 = get(left_fork, Self.SELF);
					System.out.println(name + " getting fork " + right+ "...");
					fork2 = get(right_fork, Self.SELF);
					System.out.println(name + " eating...");
					put(fork1, Self.SELF);
					put(fork2, Self.SELF);
					System.out.println(name + " meditating...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}