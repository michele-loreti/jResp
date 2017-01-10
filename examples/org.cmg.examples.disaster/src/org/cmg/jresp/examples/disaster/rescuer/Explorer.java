package org.cmg.jresp.examples.disaster.rescuer;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Self;

public class Explorer extends Agent {

	private int robotId;
	private Scenario scenario;

	public Explorer(int robotId, Scenario scenario) {
		super("Explorer");
		this.robotId = robotId;
		this.scenario = scenario;
	}

	@Override
	protected void doRun() throws IOException, InterruptedException {
		boolean found = false;
		while (!found) {
			Tuple t = query(new Template(new ActualTemplateField("VICTIM_PERCEIVED"), new ActualTemplateField(true)),
					Self.SELF);

			// TODO DA FARE CON POLICY (bloccare Rescuer quando si passa a
			// helpRescuer)
			if (!scenario.getRole(robotId).equals(Scenario.HELP_RES)
					&& !scenario.getRole(robotId).equals(Scenario.LOW_BATT)) {

				found = t.getElementAt(Boolean.class, 1);
				if (found) {
					// TODO robot must stop by using POLICY !!!
					put(new Tuple("stop"), Self.SELF);
					// Pass to RESCUER state
					put(new Tuple("role", Scenario.RESCUER), Self.SELF);

					System.out.print("Robot " + robotId + " has become RESCUER\n");

					found();
					put(new Tuple("rescue", scenario.getPosition(robotId).getX(), scenario.getPosition(robotId).getY()),
							Self.SELF);
				}
			} else {
				break;
			}

		}
		System.out.println("Explorer finish");
	}

	private void found() throws InterruptedException, IOException {
		put(new Tuple("victim", scenario.getPosition(robotId).getX(), scenario.getPosition(robotId).getY(),
				scenario.getRescuersSwarmSize() - 1), Self.SELF);

		// put( new Tuple( "victim" , robotId , 0 ) , Self.SELF );
	}

}
