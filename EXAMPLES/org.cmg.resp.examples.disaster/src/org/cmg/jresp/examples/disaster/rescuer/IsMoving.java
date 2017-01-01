/**
 * 
 */
package org.cmg.jresp.examples.disaster.rescuer;

import java.util.Random;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Self;

/**
 * @author Andrea Margheri
 *
 */
public class IsMoving extends Agent {

	private int robotId;
	private Scenario scenario;

	Random r = new Random();

	public IsMoving(int robotId, Scenario scenario) {
		super("LowBattery");
		this.robotId = robotId;
		this.scenario = scenario;
	}

	@Override
	protected void doRun() throws Exception {
		while (true) {

			Tuple t = query(new Template(new ActualTemplateField("WALKING"), new ActualTemplateField(true)), Self.SELF);

			// The condition on the battery is evaluated only when we are not in
			// the RESCUER or HELP_RESCUER
			if (!scenario.getRole(robotId).equals(Scenario.RESCUER)
					&& !scenario.getRole(robotId).equals(Scenario.HELP_RES)) {

				// check the condition on batteryLevel
				t = query(new Template(new ActualTemplateField("BATTERY_LEVEL"), new FormalTemplateField(Double.class)),
						Self.SELF);

				Double batteryLevel = t.getElementAt(Double.class, 1);
				// test battery level

				if (batteryLevel < Scenario.dechargedBattery) {

					put(new Tuple("stop"), Self.SELF);

					/*
					 * Add the tuple for the ACTUATOR (then also for the
					 * AttributeCollector) of ChargingBattery Note that the
					 * ACTUATOR triggers the starting of re-charging process
					 */

					put(new Tuple("CHARGING", true), Self.SELF);
					/*
					 * Force the underRecharginStatus for creating a good
					 * simulation
					 */
					scenario.setUnderRecharging(robotId, true);

					put(new Tuple("role", Scenario.LOW_BATT), Self.SELF);

					System.out.print("Robot " + robotId + " has become LOW BATTERY\n");

					// wait for recharged battery. The tuple is added by a
					// Sensor
					t = query(new Template(new ActualTemplateField("CHARGED"), new ActualTemplateField(true)),
							Self.SELF);

					// battery recharged

					// move to the EXPLORER state. This triggers re-starting of
					// random exploration
					put(new Tuple("role", Scenario.EXPLORER), Self.SELF);

				}
			}
		}
	}

}
