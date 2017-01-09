package org.cmg.jresp.policy.facpl.algorithm;

import java.util.LinkedList;
import java.util.List;

import org.cmg.jresp.policy.AuthorizationDecision;
import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.AuthorizationResponse;
import org.cmg.jresp.policy.facpl.FulfilledObligation;
import org.cmg.jresp.policy.facpl.ICombiningAlgorithm;
import org.cmg.jresp.policy.facpl.IFacplPolicy;

public class DenyOverrides implements ICombiningAlgorithm {

	@Override
	public AuthorizationResponse evaluate(List<IFacplPolicy> elements, AuthorizationRequest request, String thisValue) {

		boolean atLeastOnePermit = false;
		boolean atLeastOneError = false;

		LinkedList<FulfilledObligation> obligationPermit = new LinkedList<FulfilledObligation>();

		AuthorizationResponse authResp = new AuthorizationResponse();

		for (IFacplPolicy el : elements) {
			AuthorizationResponse d = el.evaluate(request, thisValue);

			if (AuthorizationDecision.DENY.equals(d.getDecision())) {
				// only last obligations evaluated are returned
				authResp.setDecision(AuthorizationDecision.DENY);
				authResp.addObligations(authResp.getAllObligations());
				return authResp;
			}
			if (AuthorizationDecision.PERMIT.equals(authResp.getDecision())) {
				atLeastOnePermit = true;
				// manage obligation
				obligationPermit.addAll(authResp.getAllObligations());

				continue;
			}
			if (AuthorizationDecision.NOT_APPLICABLE.equals(authResp.getDecision())) {
				continue;
			}

			if (AuthorizationDecision.INDETERMINATE.equals(authResp.getDecision())) {
				atLeastOneError = true;
				continue;
			}
		}

		if (atLeastOneError) {
			authResp.setDecision(AuthorizationDecision.INDETERMINATE);
			return authResp;
		}

		if (atLeastOnePermit) {
			authResp.setDecision(AuthorizationDecision.PERMIT);
			authResp.addObligations(obligationPermit);
			return authResp;
		}

		// otherwise return not app
		authResp.setDecision(AuthorizationDecision.NOT_APPLICABLE);

		return authResp;
	}

}
