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
import org.cmg.jresp.policy.facpl.function.comparison.PatternMatch;
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
		// new PointToPoint("1", new VirtualPortAddress(10))

		if (scenario.getRole(robotId).equals(Scenario.RESCUER)) {

			// In case of rescuer completing this action, I must add again the
			// value in the knowledge
			double x = t.getElementAt(Double.class, 1);
			double y = t.getElementAt(Double.class, 2);
			int dimRescuerSwarm = t.getElementAt(Integer.class, 3);

			put(new Tuple("victim", x, y, dimRescuerSwarm), Self.SELF);

			System.out.println("Rescuer - fatta query del HelpRescuer - Riaggiunta");
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
				put(new Tuple("victim", x, y, f), Self.SELF);
			}
			// go to victim positions received
			gotoVictim(x, y);

			boolean found = false;
			while (!found) {
				// reaching the victim and halts for helping the other rescuers
				t = query(new Template(new ActualTemplateField("VICTIM_PERCEIVED"), new ActualTemplateField(true),
						// get the victim with the expected number of robots
						new ActualTemplateField(scenario.getRescuersSwarmSize() - dimRescuerSwarm)), Self.SELF);

				found = t.getElementAt(Boolean.class, 1);
				if (found) {

					// Pass to RESCUER state
					put(new Tuple("role", Scenario.RESCUER), Self.SELF);

					// System.out.print("Robot " + robotId + " has become
					// RESCUER\n");

					put(new Tuple("rescue", scenario.getPosition(robotId).getX(), scenario.getPosition(robotId).getY()),
							Self.SELF);
				}
			}
		}
		// System.out.println("Fine HelpExplorer");
	}

	private void gotoVictim(double x, double y) throws InterruptedException, IOException {
		put(new Tuple("pointDirection", x, y), Self.SELF);
	}

	/*-----------------------------------
	 *POLICIES 
	 *-----------------------------------
	 */

	/**
	 * Return the policy in force in the Help_Rescuer state
	 * 
	 * @return
	 */
	public IFacplPolicy getPolicyHelpRescuer() {
		return new Policy_HelpRescuer();
	}

	private class Policy_HelpRescuer extends PolicySet {

		public Policy_HelpRescuer() {

			addId("HelpRescuer");

			addCombiningAlg(PermitUnlessDeny.class);

			addTarget(true);

			addElement(new Rule_VictimPerceived());

			// addElement(new Rule_DisableRandom());

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

		// class Rule_DisableRandom extends Rule {
		//
		// Rule_DisableRandom() {
		// addEffect(RuleEffect.DENY);
		//
		// addTarget(new TargetTreeRepresentation(TargetConnector.AND,
		// new TargetTreeRepresentation(new TargetExpression(Equal.class,
		// new RequestAttributeName("action", "id"), ActionThisID.PUT)),
		// new TargetTreeRepresentation(
		// new TargetExpression(PatternMatch.class, new
		// RequestAttributeName("action", "arg"),
		// new Template(new ActualTemplateField("direction"),
		// new FormalTemplateField(Double.class))))));
		//
		// addObligation(null);
		// }
		// }

	}

	@Override
	protected void doHandle(Exception e) {
	}

}
