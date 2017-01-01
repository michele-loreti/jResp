/**
 * 
 */
package org.cmg.resp.examples.ca;
 
import java.awt.geom.Point2D;
import java.io.IOException;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.topology.Group;
import org.cmg.resp.topology.IsLessThan;
import org.cmg.resp.topology.Self;

/**
 * @author loreti
 *
 */
public class WalkingAgent extends Agent {

	public WalkingAgent() {
		super("WalkingAgent");
	}
	/* (non-Javadoc)
	 * @see org.cmg.resp.behaviour.Agent#doRun()
	 */
	@Override
	protected void doRun() throws Exception {
		boolean victimreached = false;
		Integer step = null;
		while (!victimreached) {
			Tuple victim = query( 
					new Template( new ActualTemplateField("VICTIM_PERCEIVED") , new FormalTemplateField(Boolean.class) , new FormalTemplateField(Point2D.Double.class)) , 
					Self.SELF
			); 
			if (victim.getElementAt(Boolean.class, 1)) {
				put( new Tuple( "GO_TO" , victim.getElementAt(Point2D.Double.class, 2) ) , Self.SELF );
				return ;
			}
			if ((step == null)||(step > 0)) {
				step = goToNextSensor( step );
			}
		}
		
		

	}
	
	private Integer goToNextSensor(Integer step) throws InterruptedException, IOException {
		Tuple t1 = query( 	
				new Template( new ActualTemplateField("PATH") , 
					new FormalTemplateField(Point2D.Double.class) ,
					new FormalTemplateField(Double.class) ,
					(step == null?new FormalTemplateField(Integer.class):new ActualTemplateField(step-1)) 
				) ,
				new Group(new IsLessThan( "path_radiation_level" , Scenario.USER_RADIATION_LIMIT ) )
			);
		int nextStep = t1.getElementAt(Integer.class, 3);
		put( new Tuple( "GO_TO" , t1.getElementAt(1) ) , Self.SELF );
		return nextStep;
	}

}
