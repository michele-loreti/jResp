	package org.cmg.jresp.examples.disaster;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.Self;

public class DataForwarder extends Agent {
	
	private int robotId;

	public DataForwarder( int robotId ) {
		super("DataForwarder");	
		this.robotId = robotId;
	}
	
	@Override
	protected void doRun() throws IOException, InterruptedException{
			Tuple t = query(new Template(
					 			new ActualTemplateField("victim") , 
					 			new FormalTemplateField(Integer.class), 
					 			new FormalTemplateField(Integer.class)), 
					 			new Group(new HasValue( "role" , Scenario.LANDMARK ) ));
			int d = t.getElementAt(Integer.class, 2);
			
			put( new Tuple( "stop" ) , Self.SELF );
//			put( new Tuple( "stop" , Scenario.BECOME_LANDMARK ) , Self.SELF );
			forward(d);						
	}
	
	private void forward(int distance) throws InterruptedException, IOException {
		int d = distance+1;
		put( new Tuple( "victim" , robotId , d ) , Self.SELF );
		
		//System.out.print("Robot "+robotId+" becomes a landmark with distance: "+d+"\n");
	}

	/* (non-Javadoc)
	 * @see org.cmg.jresp.behaviour.Agent#doHandle(java.lang.Exception)
	 */
	@Override
	protected void doHandle(Exception e) {
	}
}
