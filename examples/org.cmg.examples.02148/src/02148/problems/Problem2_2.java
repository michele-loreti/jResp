package problems;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class Problem2_2 {

	public static void main(String[] argv) {
		
		Node node = new Node("node", new TupleSpace());
		node.start();
		
		node.addAgent(new A());

	}

	public static class A extends Agent {

		public A() {
			super("some agent");
		}

		@Override
		protected void doRun() {
			Agent P =  new P("P",3);
			Agent Q =  new P("Q",1);
			Agent R =  new P("R",3);
			Agent S =  new P("S",0);
			try {
				runAndWait(P);
				splitAndJoin(Q,R);
				runAndWait(S);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		protected void runAndWait(Agent P){
			try {
				exec(P);
				get(new Template(new ActualTemplateField("done"),new ActualTemplateField(P.getName())),Self.SELF);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		protected void splitAndJoin(Agent P1, Agent P2){
			try {
				exec(P1);
				exec(P2);
				get(new Template(new ActualTemplateField("done"),new ActualTemplateField(P1.getName())),Self.SELF);
				get(new Template(new ActualTemplateField("done"),new ActualTemplateField(P2.getName())),Self.SELF);			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static class P extends Agent {
		
		public int duration;

		public P(String name, int duration) {
			super(name);
			this.duration = duration;
		}

		@Override
		protected void doRun() {

			try {
				System.out.println(name + " running...");
				Thread.sleep(duration*1000);
				System.out.println(name + " done!");
				put(new Tuple("done",name),Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
