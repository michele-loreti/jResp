package org.cmg.jresp.examples.disaster;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Self;

public class VictimSeeker extends Agent {
	
	// robot identifier
	private int robotId;
		
	public VictimSeeker( int robotId ) {
		super("VictimSeeker");
		this.robotId = robotId;
	}

	@Override
	protected void doRun() throws IOException, InterruptedException{
		boolean found = false;
		while (!found) {
//			Thread.sleep(2000);
			Tuple t = query( new Template(
			 		new ActualTemplateField("VICTIM_PERCEIVED") , 
			 		new ActualTemplateField(true)) , 
			 	  Self.SELF );
			found = t.getElementAt(Boolean.class, 1);
			if (found) {
				put( new Tuple( "stop" ) , Self.SELF );
//				put( new Tuple( "stop" , Scenario.VICTIM_FOUND ) , Self.SELF );
				System.out.print("Robot "+robotId+" has found the victim\n");
				found();
			} 
		}
	}
			
 
	private void found() throws InterruptedException, IOException {
		put( new Tuple( "victim" , robotId , 0 ) , Self.SELF );		
	}

}
