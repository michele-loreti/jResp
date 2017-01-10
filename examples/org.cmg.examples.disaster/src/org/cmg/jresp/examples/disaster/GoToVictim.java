package org.cmg.jresp.examples.disaster;

import java.awt.geom.Point2D;
import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.Self;

public class GoToVictim extends Agent {

	private int robotId;

	private Scenario scenario;

	public GoToVictim(int robotId, Scenario scenario) {
		super("GoToVictim");
		this.robotId = robotId;
		this.scenario = scenario;
	}

	@Override
	protected void doRun() throws IOException, InterruptedException {
		Tuple t = query(new Template(new ActualTemplateField("victim"), new FormalTemplateField(Integer.class),
				new FormalTemplateField(Integer.class)), new Group(new HasValue("role", Scenario.LANDMARK)));
		int id = t.getElementAt(Integer.class, 1);
		int d = t.getElementAt(Integer.class, 2);

		put(new Tuple("start"), Self.SELF);

		double dir = towards(id);

		put(new Tuple("direction", dir), Self.SELF);

		while (d > 0) {
			d = d - 1;
			t = query(new Template(new ActualTemplateField("victim"), new FormalTemplateField(Integer.class),
					new ActualTemplateField(d)), new Group(new HasValue("role", Scenario.LANDMARK)));

			id = t.getElementAt(Integer.class, 1);

			dir = towards(id);
			put(new Tuple("direction", dir), Self.SELF);
		}

		boolean found = false;
		while (!found) {
			Tuple t1 = query(new Template(new ActualTemplateField("VICTIM_PERCEIVED"), new ActualTemplateField(true)),
					Self.SELF);
			found = t1.getElementAt(Boolean.class, 1);
			if (found) {
				put(new Tuple("stop"), Self.SELF);
				// put( new Tuple( "stop" , Scenario.WORKER_ARRIVED ) ,
				// Self.SELF );
				// System.out.print("Worker "+robotId+" is arrived at the
				// victim\n");
			}
		}

	}

	private double towards(int i) {
		Point2D.Double workerPosition = scenario.getPosition(robotId);
		Point2D.Double landmarkPosition = scenario.getPosition(i);
		double dx = landmarkPosition.x - workerPosition.x;
		double dy = landmarkPosition.y - workerPosition.y;
		return Math.atan2(dy, dx);
	}

}
