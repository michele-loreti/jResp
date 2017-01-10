package problems;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class Problem1_4 {

	public static void main(String[] argv) {

		int N = 4;
		Node n = new Node("workplace", new TupleSpace());

		Agent splitter = new Splitter();
		n.addAgent(splitter);

		Agent[] merger = new Merger[N];

		for(int i=0; i<N; i++){
			merger[i] = new Merger("merger"+i);
			n.addAgent(merger[i]);
		}
		n.start();
	}

	public static class Splitter extends Agent {

		public Splitter() {
			super("splitter");
		}

		@Override
		protected void doRun() {
			Vector<Integer> v;
			try {
				int SIZE = 20;
				put(new Tuple("size",SIZE),Self.SELF);
				for(int i=0; i<SIZE; i++){
					v = new Vector<>();
					v.add(ThreadLocalRandom.current().nextInt(0, 100 + 1));
					System.out.println(name + " putting " + v);
					put(new Tuple("sorted",v),Self.SELF);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class Merger extends Agent {

		public Merger(String name) {
			super(name);
		}

		@Override
		protected void doRun() {
			int size;
			Tuple t, t1, t2;
			Vector<Integer> v1 = new Vector<Integer>();
			Vector<Integer> v2 = new Vector<Integer>();
			Vector<Integer> v3 = new Vector<Integer>();
			Template done = new Template(
					new ActualTemplateField("done"),
					new FormalTemplateField(v1.getClass())
					);
			Template vector = new Template(
					new ActualTemplateField("sorted"),
					new FormalTemplateField(v1.getClass())
					);
			Template sizeT = new Template(
					new ActualTemplateField("size"),
					new FormalTemplateField(Integer.class)
					);
			try {
				t = query(sizeT,Self.SELF);
				size = t.getElementAt(Integer.class,1);
				while(true) {
					
					if(queryp(done) != null) {
						System.out.println(name + " finishing...");
						break;
					}
					
					// Get first vector
					System.out.println(name + " waiting...");
					t1 = getp(vector);
					// If not lucky try later
					if (t1 == null) {
						continue;
					}
					v1 = new Vector<Integer>();
					v1.addAll((Vector<Integer>)t1.getElementAt(v1.getClass(),1));					
					System.out.println(name + " got vector " + v1);
					
					// Try to get the second one
					t2 = getp(vector);
					// Failure may means that somebody else is waiting too
					if (t2 == null) {
						put(t1,Self.SELF);
						continue;
					}
							
					// Move on with the second vector
					v2 = new Vector<Integer>();
					v2.addAll((Vector<Integer>)t2.getElementAt(1));
					System.out.println(name + " got vector " + v2);
					
					// Merge the vectors
					System.out.println(name + " merging...");
					int i = 0;
					int j = 0;
					v3 = new Vector<Integer>();
					while ( i < v1.size() || j < v2.size() ) {
						if ( j >= v2.size() ) {
							v3.add(v1.get(i));
							i++;
						} else if  ( i >= v1.size() )  {
							v3.add(v2.get(j));
							j++;
						} else if ( v1.get(i) < v2.get(j) ) {
							v3.add(v1.get(i));
							i++;
						} else {
							v3.add(v2.get(j));
							j++;
						}
					}
					
					// Check if we are done
					if (v3.size() >= size){
						System.out.println(name + " got the final result " + v3);
						put(new Tuple("done",v3), Self.SELF);
						break;
					}
					
					// Otherwise put the new vector
					put(new Tuple("sorted",v3), Self.SELF);
					System.out.println(name + " putting vector " + v3);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}