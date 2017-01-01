package org.cmg.jresp.policy.facpl.algorithm;

import java.util.LinkedList;
import java.util.List;

import org.cmg.jresp.policy.AuthorizationDecision;
import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.AuthorizationResponse;
import org.cmg.jresp.policy.facpl.FulfilledObligation;
import org.cmg.jresp.policy.facpl.ICombiningAlgorithm;
import org.cmg.jresp.policy.facpl.IFacplPolicy;

public class PermitOverrides implements ICombiningAlgorithm {

	@Override
	public AuthorizationResponse evaluate(List<IFacplPolicy> elements, AuthorizationRequest request, String thisValue) {

		Boolean atLeastOneError = false;
		Boolean atLeastOneDeny = false;

		LinkedList<FulfilledObligation> obligationDeny = new LinkedList<FulfilledObligation>();

		AuthorizationResponse dr = new AuthorizationResponse();
		for (IFacplPolicy el : elements) {
			AuthorizationResponse d = el.evaluate(request, thisValue);

			if (AuthorizationDecision.PERMIT.equals(d.getDecision())) {
				dr.setDecision(AuthorizationDecision.PERMIT);
				dr.addObligations(d.getAllObligations());
				return dr;
			}

			if (AuthorizationDecision.DENY.equals(d.getDecision())) {
				atLeastOneDeny = true;
				// add Obligation_Deny
				obligationDeny.addAll(d.getAllObligations());
				continue;
			}

			if (AuthorizationDecision.NOT_APPLICABLE.equals(d.getDecision())) {
				continue;
			}
			if (AuthorizationDecision.INDETERMINATE.equals(d.getDecision())) {
				atLeastOneError = true;
				continue;
			}
		}
		if (atLeastOneError) {
			dr.setDecision(AuthorizationDecision.INDETERMINATE);
			return dr;
		}
		if (atLeastOneDeny) {
			dr.setDecision(AuthorizationDecision.DENY);
			dr.addObligations(obligationDeny);
			return dr;
		}
		// otherwise return not app
		dr.setDecision(AuthorizationDecision.NOT_APPLICABLE);
		return dr;
	}
}
