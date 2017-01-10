package problems;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.AnyComponent;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.VirtualPort;

public class Problem2_4 {
	
	public static VirtualPort vp = new VirtualPort(8080);
	public static GroupPredicate any = new AnyComponent();

	public static void main(String[] argv) {

		// Number of nodes in the P2P network
		int N = 5;

		// Array of nodes (one for each peer)
		Node[] node = new Node[N];
		// Arrays of agents in the nodes
		Peer[] peer = new Peer[N];

		for(int i=0; i<N; i++){
			node[i]= new Node("node"+i, new TupleSpace());
			node[i].addPort(vp);
			node[i].setGroupActionWaitingTime(1000);
			peer[i] = new Peer(i);
			node[i].addAgent(peer[i]);
		}		
		
		// It is important in this example to start the nodes *after* having created the nodes
		// Otherwise an agent may try to access a non-existing node, thus raising an exception
		for(int i=0; i<N; i++){
			node[i].start();
		}		

	}

	public static class Peer extends Agent {
		
		protected int id;

		public Peer(int id) {
			super("peer"+id);
			this.id = id;
		}

		@Override
		protected void doRun() {
			Tuple t;
			int key;
			try {
				// The peer starts adding some data to itself: namely, his name with key his id
				System.out.println("Peer " + name + " storing item <" + id + "," + name + ">...");
				put(new Tuple(id,name),Self.SELF);
				// Then looks up for some other data, the name a peer with identifier id+1
				key = id+1;
				t = query(new Template(new ActualTemplateField(key),new FormalTemplateField(String.class)), new Group(any));
				if ( t != null ){
					System.out.println("Peer " + name + " found item <" + key + "," + t.getElementAt(String.class, 1) + ">");
				} else {
					System.out.println("Peer " + name + " was unlucky.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}