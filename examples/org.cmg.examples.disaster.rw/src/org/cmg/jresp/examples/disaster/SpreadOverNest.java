package org.cmg.jresp.examples.disaster;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.Self;
//Agente che ordina di seguire il primo landmark che si � fermato fuori dal nest.
public class SpreadOverNest extends Agent { 

	// robot identifier
	private int robotId;
	private Scenario scenario;

	public SpreadOverNest(int robotId, Scenario s) {
		super("SpreadOverNest");
		this.robotId = robotId;
		this.scenario = s;
	}

	@Override
	protected void doRun() throws IOException, InterruptedException {
		Tuple t = query(new Template(new ActualTemplateField("list"),
				new FormalTemplateField(Integer.class)), new Group(new HasValue( "role" , Scenario.LANDMARK )));
		int land = t.getElementAt(Integer.class, 1); // L'ID del primo robot a fermarsi fuori dal nest.
		if (land!=robotId){ // Tutti tranne se stesso
			double dir = towards(land);
			put( new Tuple( "direction" ,  dir) , Self.SELF ); 
		}
	}
	
	private double towards(int i) {
		Point2D.Double thisPosition = scenario.getPosition(robotId);
		Point2D.Double landmarkPosition = scenario.getPosition(i);
		double dx = landmarkPosition.x - thisPosition.x;
        double dy = landmarkPosition.y - thisPosition.y;
		return Math.atan2(dy, dx);
	}

}
