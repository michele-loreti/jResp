package org.cmg.jresp.policy.facpl.algorithm;

import java.util.List;

import org.cmg.jresp.policy.AuthorizationDecision;
import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.AuthorizationResponse;
import org.cmg.jresp.policy.IAuthorisationPolicy;
import org.cmg.jresp.policy.facpl.ICombiningAlgorithm;
import org.cmg.jresp.policy.facpl.MatchDecision;

public class OnlyOneApplicable implements ICombiningAlgorithm {

	@Override
	public AuthorizationResponse evaluate(List<IAuthorisationPolicy> elements, AuthorizationRequest request, String thisValue) {

		Boolean atLeastOne = false;
		IAuthorisationPolicy selectedPolicy = null;
		MatchDecision appResult;

		AuthorizationResponse dr = new AuthorizationResponse();
		for (IAuthorisationPolicy el : elements) {
			appResult = el.getTargetDecision(request, thisValue);

			if (appResult.equals(MatchDecision.INDETERMINATE)) {
				dr.setDecision(AuthorizationDecision.INDETERMINATE);
				return dr;
			}
			if (appResult.equals(MatchDecision.MATCH)) {
				if (atLeastOne) {
					dr.setDecision(AuthorizationDecision.INDETERMINATE);
					return dr;
				} else {
					atLeastOne = true;
					selectedPolicy = el;
				}
			}
			if (appResult.equals(MatchDecision.NO_MATCH)) {
				continue;
			}
		}
		if (atLeastOne) {
			return selectedPolicy.evaluate(request, thisValue);
		} else {
			dr.setDecision(AuthorizationDecision.NOT_APPLICABLE);
			return dr;
		}

	}

}
