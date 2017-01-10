package problems;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.VirtualPort;
import org.cmg.jresp.topology.VirtualPortAddress;

public class Problem2_1a {

	public static void main(String[] argv) {
		
		int N = 10;
		
		Node[] node = new Node[N];
		Agent[] linker = new Linker[N];
		Agent[] monitor = new Monitor[N];
		
		VirtualPort vp = new VirtualPort(8080);
		for(int i=0; i<N; i++){
			node[i]= new Node("node"+i, new TupleSpace());
			node[i].addPort(vp);
			linker[i] = new Linker("node"+((i+1)%N));
			node[i].addAgent(linker[i]);
			monitor[i] = new Monitor("monitor"+i);
			node[i].addAgent(monitor[i]);
		}
		
		PointToPoint start = new PointToPoint("node"+0, new VirtualPortAddress(8080));
		Agent broadcaster = new Broadcaster(start);
		node[0].addAgent(broadcaster);
		
		for(int i=0; i<N; i++){
			node[i].start();
		}

	}

	public static class Broadcaster extends Agent {

		PointToPoint home;
		PointToPoint here;

		public Broadcaster(PointToPoint home) {
			super("broadcaster");
			this.home = home;
			this.here = home;
		}

		@Override
		protected void doRun() {
			Tuple t;
			Template whatNext = new Template(
					new ActualTemplateField("next"),
					new FormalTemplateField(String.class)
					);
			try {
				//System.out.println("Starting at " + home.getName() + "...");
				//Thread.sleep(2000);
				do {
					//System.out.println("Broadcasting at " + here.getName() + "...");
					put(new Tuple("message","blah"), here);
					//System.out.println("DONE!");
					//System.out.println("Looking for next node...");
					t = query(whatNext, here);
					here = new PointToPoint(t.getElementAt(String.class,1), new VirtualPortAddress(8080));
					//System.out.println("DONE!");
				} while(! here.getName().equals(home.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class Linker extends Agent {

		public String next;

		public Linker(String next) {
			super("linker");
			this.next = next;
		}

		@Override
		protected void doRun() {
			try {
				put(new Tuple("next",next), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public static class Monitor extends Agent {

		public Monitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			Tuple t;
			Template what = new Template(
					new ActualTemplateField("message"),
					new FormalTemplateField(String.class)
					);
			try {
				t = query(what, Self.SELF);
				System.out.println("    " + name + " saw " + t.getElementAt(String.class, 1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}