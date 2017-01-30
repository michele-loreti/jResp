/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universit? di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.jresp.examples.disaster.rescuer.pscel;

import java.io.IOException;

import org.cmg.jresp.examples.disaster.rescuer.Scenario;
import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
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
import org.cmg.jresp.policy.facpl.function.comparison.PatternMatch;
import org.cmg.jresp.topology.Self;

/**
 *
 * @author Andrea Margheri
 */
public class IsMoving extends Agent {

	@SuppressWarnings("unused")
	private int robotId;

	public IsMoving(int robotId) {
		super("isMoving");
		this.robotId = robotId;
	}

	@Override
	protected void doRun() throws IOException, InterruptedException {
		while (true) {

			query(new Template(new ActualTemplateField("WALKING"), new ActualTemplateField(true)), Self.SELF);

		}
	}

	/**
	 * Return the policy in force in the Help_Rescuer state
	 * 
	 * @return
	 */
	public IAuthorisationPolicy getPolicyLowBattery() {
		return new Policy_LowBattery();
	}

	private class Policy_LowBattery extends PolicySet {

		public Policy_LowBattery() {

			addCombiningAlg(PermitUnlessDeny.class);

			addTarget(true);

			addElement(new Rule_BatteryLevel());

			addObligation(null);
		}

	}

	class Rule_BatteryLevel extends Rule {

		Rule_BatteryLevel() {
			addEffect(RuleEffect.PERMIT);

			addTarget(new TargetTreeRepresentation(TargetConnector.AND,
					new TargetTreeRepresentation(new TargetExpression(Equal.class,
							new RequestAttributeName("action", "id"), ActionThisID.QRY)),
					new TargetTreeRepresentation(
							new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
									new Template(new ActualTemplateField("CHARGED"), new ActualTemplateField(true))))));

			// move to explorer state as soon as the battery is recharged
			addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ActionThisID.UPD, ObligationType.AFTER,
					"role", Scenario.EXPLORER));
			// Role changed made also with PUT (it is needed due to simulation
			// matter)
			addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ActionThisID.PUT, ObligationType.AFTER,
					new Tuple("role", Scenario.EXPLORER), Self.SELF));

		}

	}

}
