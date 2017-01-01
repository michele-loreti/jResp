package org.cmg.jresp.examples.disaster.rescuer.facpl;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.policy.ActionThisID;
import org.cmg.jresp.policy.RequestAttributeName;
import org.cmg.jresp.policy.facpl.IFacplPolicy;
import org.cmg.jresp.policy.facpl.ObligationType;
import org.cmg.jresp.policy.facpl.RuleEffect;
import org.cmg.jresp.policy.facpl.algorithm.PermitUnlessDeny;
import org.cmg.jresp.policy.facpl.elements.PolicySet;
import org.cmg.jresp.policy.facpl.elements.Rule;
import org.cmg.jresp.policy.facpl.elements.ScelObligationExpression;
import org.cmg.jresp.policy.facpl.elements.TargetConnector;
import org.cmg.jresp.policy.facpl.elements.TargetExpression;
import org.cmg.jresp.policy.facpl.elements.util.TargetTreeRepresentation;
import org.cmg.jresp.policy.facpl.function.comparison.Equal;
import org.cmg.jresp.policy.facpl.function.comparison.LessThan;
import org.cmg.jresp.policy.facpl.function.comparison.PatternMatch;
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

			// if (this.getName() != "1"){

			Tuple t = query(new Template(new ActualTemplateField("VICTIM_PERCEIVED"), new ActualTemplateField(true),
					// no other robot on the perceived victim
					new ActualTemplateField(0)), Self.SELF);

			found = t.getElementAt(Boolean.class, 1);
			if (found) {

				// Pass to RESCUER state
				put(new Tuple("role", Scenario.RESCUER), Self.SELF);

				// System.out.print("Robot " + robotId + " has become
				// RESCUER\n");

				found();
				put(new Tuple("rescue", scenario.getPosition(robotId).getX(), scenario.getPosition(robotId).getY()),
						Self.SELF);
			}
		}
	}
	// System.out.println("Fine Explorer");
	// }

	private void found() throws InterruptedException, IOException {
		put(new Tuple("victim", scenario.getPosition(robotId).getX(), scenario.getPosition(robotId).getY(),
				scenario.getRescuersSwarmSize() - 1), Self.SELF);
	}

	/*-----------------------------------
	 *POLICIES 
	 *-----------------------------------
	 */

	/**
	 * Return the policy in force in the explorer state
	 * 
	 * @return
	 */
	public IFacplPolicy getPolicyExplorer() {
		return new Policy_Explorer();
	}

	/**
	 * Return the policy in force in the rescuer state
	 * 
	 * @return
	 */

	public IFacplPolicy getPolicyRescuer() {
		return new Policy_Rescuer();
	}

	private class Policy_Explorer extends PolicySet {

		public Policy_Explorer() {

			addId("Explorer");

			addCombiningAlg(PermitUnlessDeny.class);

			addTarget(true);

			addElement(new Rule_VictimPerceived());

			addElement(new Rule_BatteryLevel());

			addObligation(null);
		}

		class Rule_VictimPerceived extends Rule {

			Rule_VictimPerceived() {
				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("action", "id"), ActionThisID.QRY)),
						new TargetTreeRepresentation(
								new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
										new Template(new ActualTemplateField("VICTIM_PERCEIVED"),
												new ActualTemplateField(true),
												new FormalTemplateField(Integer.class))))));

				addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ActionThisID.PUT, ObligationType.AFTER,
						new Tuple("stop"), Self.SELF));
			}
		}

		class Rule_BatteryLevel extends Rule {

			Rule_BatteryLevel() {
				addEffect(RuleEffect.DENY);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("action", "id"), ActionThisID.QRY)),
						new TargetTreeRepresentation(
								new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
										new Template(new ActualTemplateField("WALKING"),
												new ActualTemplateField(true)))),
						new TargetTreeRepresentation(new TargetExpression(LessThan.class,
								new RequestAttributeName("object", "battery_level"), Scenario.dechargedBattery))));

				/*
				 * All these obligations will be authorized by the
				 * <<low_battery>> STATE of the Policy Automaton
				 */

				addObligation(new ScelObligationExpression(RuleEffect.DENY, ActionThisID.PUT, ObligationType.AFTER,
						new Tuple("stop"), Self.SELF));

				// move to low-battery role
				addObligation(new ScelObligationExpression(RuleEffect.DENY, ActionThisID.PUT, ObligationType.AFTER,
						new Tuple("role", Scenario.LOW_BATT), Self.SELF));

				// triggers the charing of the battery
				addObligation(new ScelObligationExpression(RuleEffect.DENY, ActionThisID.PUT, ObligationType.AFTER,
						new Tuple("CHARGING", true), Self.SELF));

				// waiting for charged battery
				addObligation(new ScelObligationExpression(RuleEffect.DENY, ActionThisID.QRY, ObligationType.AFTER,
						new Template(new ActualTemplateField("CHARGED"), new ActualTemplateField(true)), Self.SELF));

			}

		}

	}

	private class Policy_Rescuer extends PolicySet {

		public Policy_Rescuer() {

			addCombiningAlg(PermitUnlessDeny.class);

			addTarget(true);

			addElement(new RuleCameraOn());

			addObligation(null);

		}

		class RuleCameraOn extends Rule {

			RuleCameraOn() {

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("action", "id"), ActionThisID.PUT)),
						new TargetTreeRepresentation(new TargetExpression(PatternMatch.class,
								new RequestAttributeName("action", "arg"),
								new Template(new ActualTemplateField("rescue"), new FormalTemplateField(Double.class),
										new FormalTemplateField(Double.class)))),
						new TargetTreeRepresentation(new TargetExpression(LessThan.class,
								new RequestAttributeName("object", "battery_level"), 40))));

				addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ActionThisID.PUT, ObligationType.AFTER,
						new Tuple("cameraOn"),

						Self.SELF));

			}
		}

	}

}
