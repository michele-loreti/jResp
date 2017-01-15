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

public class Problem2_3 {

	public static void main(String[] argv) {

		int N = 10;

		Node[] node = new Node[N];
		Agent[] elector = new Elector[N];
		Agent[] forwarder = new Forwarder[N];

		VirtualPort vp = new VirtualPort(8080);
		for(int i=0; i<N; i++){
			node[i]= new Node("node"+i, new TupleSpace());
			node[i].addPort(vp);
			elector[i] = new Elector("node"+i,"node"+((i+1)%N));
			node[i].addAgent(elector[i]);
			forwarder[i] = new Forwarder("node"+i,"node"+((i+1)%N));
			node[i].addAgent(forwarder[i]);
		}		
		
		for(int i=0; i<N; i++){
			node[i].start();
		}		

	}

	public static class Elector extends Agent {

		String home;
		PointToPoint next;		

		public Elector(String home,String next) {
			super("elector_"+home);
			this.home = home;
			this.next = new PointToPoint(next, new VirtualPortAddress(8080));
			}

		@Override
		protected void doRun() {
			Tuple t;
			Template leaderT = new Template(
					new ActualTemplateField("leader"),
					new FormalTemplateField(String.class)
					);
			String leader = home;
			try {
				System.out.println(name + " voting for himself...");
				put(new Tuple("vote",home,leader),next);
				t = get(leaderT,Self.SELF); 
				leader = t.getElementAt(String.class,1);
				System.out.println(name + " thinks that " + leader + " is the leader.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static class Forwarder extends Agent {

		PointToPoint home;
		PointToPoint next;
		String leader;

		public Forwarder(String home, String next) {
			super("forwarder" + home);
			this.home = new PointToPoint(home, new VirtualPortAddress(8080));
			this.next = new PointToPoint(next, new VirtualPortAddress(8080));
			this.leader = home;
		}

		@Override
		protected void doRun() {
			Tuple t;
			Template vote = new Template(
					new ActualTemplateField("vote"),
					new FormalTemplateField(String.class),
					new FormalTemplateField(String.class)
					);
			String voter;
			try {
				while(true){
					t = get(vote,Self.SELF);
					//System.out.println("Node " + home.getName() + " got a vote.");
	
					voter = (t.getElementAt(String.class,1));
					// Forward votes if they were issued by another node
					// updating it according to lexicographical order
					if (! voter.equals(home.getName())) {
						leader = (leader.compareTo(t.getElementAt(String.class,2)) > 0) ? leader : t.getElementAt(String.class,2);
						System.out.println("Node " + home.getName() + " forwards a vote from " + voter + " for " + leader + "...");
						put(new Tuple("vote",voter,leader), next);
					} 
					// Otherwise let the elector know who is the leader
					else {
						put(new Tuple("leader",leader),Self.SELF);
					}
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
