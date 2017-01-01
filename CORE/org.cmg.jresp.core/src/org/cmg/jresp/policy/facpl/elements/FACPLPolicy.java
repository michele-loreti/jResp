package org.cmg.jresp.policy.facpl.elements;

import java.util.LinkedList;

import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.facpl.FulfilledObligation;
import org.cmg.jresp.policy.facpl.IFacplPolicy;
import org.cmg.jresp.policy.facpl.MatchDecision;
import org.cmg.jresp.policy.facpl.RuleEffect;
import org.cmg.jresp.policy.facpl.elements.util.TargetTreeRepresentation;

/**
 *
 * @author Andrea Margheri
 *
 */

public abstract class FACPLPolicy implements IFacplPolicy {

	protected TargetTreeRepresentation target;
	protected LinkedList<ScelObligationExpression> obligations;
	protected String idElement;

	protected void addId(String id) {
		this.idElement = id;
	}

	protected void addTarget(TargetTreeRepresentation target) {
		if (target != null) {
			this.target = target;
		}
	}

	protected void addTarget(TargetExpression target) {
		if (target != null) {
			this.target = new TargetTreeRepresentation(target);
		}
	}

	protected void addTarget(Boolean b) {
		if (target != null) {
			this.target = new TargetTreeRepresentation(b);
		}
	}

	protected void addObligation(ScelObligationExpression obl) {
		if (obl != null) {
			if (this.obligations == null) {
				this.obligations = new LinkedList<ScelObligationExpression>();
			}
			this.obligations.add(obl);
		}
	}

	/**
	 * Target evaluation
	 * 
	 * @param request
	 * @return
	 */
	@Override
	public MatchDecision getTargetDecision(AuthorizationRequest request, String thisValue) {
		MatchDecision decision = null;
		if (this.target == null) {
			// empty target
			return MatchDecision.MATCH;
		}
		decision = this.target.getDecisionValue(request, thisValue);
		return decision;
	}

	protected LinkedList<FulfilledObligation> evaluateObl(RuleEffect effect, AuthorizationRequest request)
			throws Throwable {
		LinkedList<FulfilledObligation> oblVal = new LinkedList<FulfilledObligation>();
		if (this.obligations != null) {
			for (ScelObligationExpression oblExpr : this.obligations) {
				// applicability
				if (oblExpr.getEvaluatedOn().name().equals(effect.name())) {
					try {
						// the same aplicability effect
						oblVal.add(oblExpr.evalObligation(request));
					} catch (Throwable t) {
						// some exception in the evaluation on the obligation's
						// arguments
						throw t;
					}
				}
			}
		}
		return oblVal;
	}

}
