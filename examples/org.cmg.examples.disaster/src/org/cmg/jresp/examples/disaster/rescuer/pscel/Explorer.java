package org.cmg.jresp.examples.disaster.rescuer.pscel;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.policy.ActionThisID;
import org.cmg.jresp.policy.RequestAttributeName;
import org.cmg.jresp.policy.IAuthorisationPolicy;
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
import org.cmg.jresp.policy.facpl.function.comparison.GreaterThan;
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

		// updateAttribute("name", Scenario.LOW_BATT);
		//
		// System.out.println(readAttribute("name"));
		//

		boolean found = false;
		while (!found) {
			Tuple t = query(new Template(new ActualTemplateField("VICTIM_PERCEIVED"), new ActualTemplateField(true),
					// no other robot on the perceived victim
					new ActualTemplateField(0)), Self.SELF);

			found = t.getElementAt(Boolean.class, 1);
			if (found) {

				System.out.println("Robot " + robotId + " has role " + scenario.getRole(robotId));

				Object x = readAttribute("xRobot");
				Object y = readAttribute("yRobot");

				put(new Tuple("victim", x, y, scenario.getRescuersSwarmSize() - 1), Self.SELF);

				put(new Tuple("rescue", x, y), Self.SELF);
			}
		}
		System.out.println("END Explorer");
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
	public IAuthorisationPolicy getPolicyExplorer() {
		return new Policy_Explorer();
	}

	/**
	 * Return the policy in force in the rescuer state
	 * 
	 * @return
	 */

	public IAuthorisationPolicy getPolicyRescuer() {
		return new Policy_Rescuer();
	}

	private class Policy_Explorer extends PolicySet {

		public Policy_Explorer() {

			addId("Explorer");

			addCombiningAlg(PermitUnlessDeny.class);

			addTarget(true);

			addElement(new Rule_VictimPerceived());

			addElement(new Rule_HelpRescuer());

			addElement(new Rule_BatteryLevel());

			addObligation(null);
		}

		/*
		 * RULE 1
		 */
		class Rule_VictimPerceived extends Rule {

			Rule_VictimPerceived() {
				addId("E1");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("action", "id"), ActionThisID.QRY)),
						new TargetTreeRepresentation(
								new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
										new Template(new ActualTemplateField("VICTIM_PERCEIVED"),
												new ActualTemplateField(true),
												new FormalTemplateField(Integer.class))))));
				// Stop the robot
				addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ActionThisID.PUT, ObligationType.AFTER,
						new Tuple("stop")
						// new Tuple(new
						// RequestAttributeName("subject","action"))
						, Self.SELF));
				// Change role to RESCUER
				addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ActionThisID.UPD, ObligationType.AFTER,
						"role", Scenario.RESCUER));
				// Role changed made also with PUT (it is needed due to
				// simulation matter)
				addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ActionThisID.PUT, ObligationType.AFTER,
						new Tuple("role", Scenario.RESCUER), Self.SELF));

			}
		}

		/*
		 * RULE 2
		 */
		class Rule_HelpRescuer extends Rule {

			Rule_HelpRescuer() {
				addId("E2");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("action", "id"), ActionThisID.GET)),
						new TargetTreeRepresentation(new TargetExpression(PatternMatch.class,
								new RequestAttributeName("action", "arg"),
								new Template(new ActualTemplateField("victim"), new FormalTemplateField(Double.class),
										new FormalTemplateField(Double.class),
										new FormalTemplateField(Integer.class))))));

				// Change role to HELP-RESCUER
				addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ActionThisID.UPD, ObligationType.AFTER,
						"role", Scenario.HELP_RES));
				// Role changed made also with PUT (it is needed due to
				// simulation matter)
				addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ActionThisID.PUT, ObligationType.AFTER,
						new Tuple("role", Scenario.HELP_RES), Self.SELF));

			}
		}

		/*
		 * RULE 3
		 */
		class Rule_BatteryLevel extends Rule {

			Rule_BatteryLevel() {
				addId("E3");

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

				addObligation(new ScelObligationExpression(RuleEffect.DENY, ActionThisID.PUT, ObligationType.BEFORE,
						new Tuple("stop"), Self.SELF));

				// Change role to LOW-BATTERY
				addObligation(new ScelObligationExpression(RuleEffect.DENY, ActionThisID.UPD, ObligationType.BEFORE,
						"role", Scenario.LOW_BATT));
				// Role changed made also with PUT (it is needed due to
				// simulation matter)
				addObligation(new ScelObligationExpression(RuleEffect.DENY, ActionThisID.PUT, ObligationType.BEFORE,
						new Tuple("role", Scenario.LOW_BATT), Self.SELF));

				// triggers the charing of the battery
				addObligation(new ScelObligationExpression(RuleEffect.DENY, ActionThisID.PUT, ObligationType.BEFORE,
						new Tuple("CHARGING", true), Self.SELF));

				// waiting for charged battery
				addObligation(new ScelObligationExpression(RuleEffect.DENY, ActionThisID.QRY, ObligationType.BEFORE,
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
						new TargetTreeRepresentation(new TargetExpression(GreaterThan.class,
								new RequestAttributeName("object", "battery_level"), 40))));

				addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ActionThisID.PUT, ObligationType.AFTER,
						new Tuple("cameraOn", new RequestAttributeName("action", "aciotn")), Self.SELF));

			}
		}

	}

}
