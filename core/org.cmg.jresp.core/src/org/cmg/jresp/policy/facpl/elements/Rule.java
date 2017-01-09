package org.cmg.jresp.policy.facpl.elements;

import java.util.LinkedList;

import org.cmg.jresp.exceptions.MissingAttributeException;
import org.cmg.jresp.policy.AuthorizationDecision;
import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.AuthorizationResponse;
import org.cmg.jresp.policy.facpl.FulfilledObligation;
import org.cmg.jresp.policy.facpl.MatchDecision;
import org.cmg.jresp.policy.facpl.RuleEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrea Margheri
 *
 */

public abstract class Rule extends FACPLPolicy {

	private RuleEffect effect;

	protected void addEffect(RuleEffect ef) {
		this.effect = ef;
	}

	@Override
	public AuthorizationResponse evaluate(AuthorizationRequest request, String thisValue) {
		// extendedIndeterminate not used in rule evaluation

		Logger l = LoggerFactory.getLogger(Rule.class);
		l.debug(idElement + ": start rule eval");

		AuthorizationResponse dr = new AuthorizationResponse();
		try {
			// Target Evaluation
			MatchDecision match_target = getTargetDecision(request, thisValue);

			l.debug("Target Decision:" + match_target.toString());

			switch (match_target) {
			case NO_MATCH:
				dr.setDecision(AuthorizationDecision.NOT_APPLICABLE);

				l.debug("RULE " + idElement + ": " + dr.toString());

				return dr;

			case MATCH:
				// Obligation Fulfillment
				// l.info("condition true. Eval Obligation");
				try {
					LinkedList<FulfilledObligation> listObl = this.evaluateObl(this.effect, request);
					// all obligations discharge
					// effect
					if (this.effect.equals(RuleEffect.PERMIT)) {
						dr.setDecision(AuthorizationDecision.PERMIT);
					} else {
						dr.setDecision(AuthorizationDecision.DENY);
					}
					// addObligation to decision result
					dr.addObligations(listObl);

					l.debug("RULE " + idElement + ": " + dr.toString());

					return dr;
				} catch (Throwable t) {
					// exception from obligation evaluatedOn rule effect
					// return indeterminate decision
					dr.setDecision(AuthorizationDecision.INDETERMINATE);

					l.debug("RULE " + super.idElement + ": " + dr.toString());

					return dr;
				}

			case INDETERMINATE:
				dr.setDecision(AuthorizationDecision.INDETERMINATE);

				l.debug("RULE " + idElement + ": " + dr.toString());

				return dr;
			default:
				// no way to arrive here
				throw new Exception();
			}
		} catch (Throwable e) {
			// catch exception from condition
			if (e instanceof MissingAttributeException) {
				dr.setDecision(AuthorizationDecision.NOT_APPLICABLE);
			} else {
				dr.setDecision(AuthorizationDecision.INDETERMINATE);
			}

			l.debug("RULE " + idElement + " - exception: " + dr.toString());

			return dr;
		}
	}
}
