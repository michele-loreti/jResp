package org.cmg.jresp.examples.disaster.rescuer;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.Self;

public class HelpRescuer extends Agent {

	private int robotId;
	private Scenario scenario;

	public HelpRescuer(int robotId, Scenario scenario) {
		super("HelpRescuer");
		this.robotId = robotId;
		this.scenario = scenario;
	}

	@Override
	protected void doRun() throws IOException, InterruptedException {
		Tuple t = get(
				new Template(new ActualTemplateField("victim"), new FormalTemplateField(Double.class),
						new FormalTemplateField(Double.class), new FormalTemplateField(Integer.class)),
				new Group(new HasValue("role", Scenario.RESCUER)));

		System.out.println("Robot " + robotId + " gets victim message from Rescuer");

		if (scenario.getRole(robotId).equals(Scenario.RESCUER) || scenario.getRole(robotId).equals(Scenario.LOW_BATT)) {
			// I'm the rescuer, I shouldn't receive this message. (also in
			// Low_Battery level)
			double x = t.getElementAt(Double.class, 1);
			double y = t.getElementAt(Double.class, 2);
			int dimRescuerSwarm = t.getElementAt(Integer.class, 3);

			put(new Tuple("victim", x, y, dimRescuerSwarm), Self.SELF);

			// System.out.println("Rescuer/LowBattery - fatta query del
			// HelpRescuer - Riaggiunta");
		} else {

			double x = t.getElementAt(Double.class, 1);
			double y = t.getElementAt(Double.class, 2);
			int dimRescuerSwarm = t.getElementAt(Integer.class, 3);

			System.out.println("Robot" + robotId + "HelpRescuer receives" + x + " " + y);

			// change to HELP_RESCUER node
			put(new Tuple("role", Scenario.HELP_RES), Self.SELF);

			System.out.print("Robot " + robotId + " has become HELP_RESCUER\n");
			// update the info according to the dimRescuerSwarm
			if (dimRescuerSwarm > 1) {
				int f = dimRescuerSwarm - 1;
				System.out.println("Rescuers rimanenti " + f);
				put(new Tuple("victim", x, y, f), Self.SELF);
			}
			// go to victim positions received
			gotoVictim(x, y);

			boolean found = false;
			while (!found) {
				// reaching the victim and halts for helping the other rescuers
				t = query(new Template(new ActualTemplateField("VICTIM_PERCEIVED"), new ActualTemplateField(true)),
						Self.SELF);
				found = t.getElementAt(Boolean.class, 1);

				if (found) {
					// TODO robot must stop by using POLICY !!!
					put(new Tuple("stop"), Self.SELF);

					// Pass to RESCUER state
					put(new Tuple("role", Scenario.RESCUER), Self.SELF);

					System.out.print("Robot " + robotId + " has become RESCUER\n");
					put(new Tuple("rescue", scenario.getPosition(robotId).getX(), scenario.getPosition(robotId).getY()),
							Self.SELF);
				}
			}
		}
		System.out.println("HelpExplorer finish");
	}

	private void gotoVictim(double x, double y) throws InterruptedException, IOException {
		put(new Tuple("pointDirection", x, y), Self.SELF);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.jresp.behaviour.Agent#doHandle(java.lang.Exception)
	 */
	@Override
	protected void doHandle(Exception e) {
	}
}
