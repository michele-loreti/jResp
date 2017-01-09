package org.cmg.jresp.policy.facpl.algorithm;

import java.util.List;

import org.cmg.jresp.policy.AuthorizationDecision;
import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.AuthorizationResponse;
import org.cmg.jresp.policy.facpl.ICombiningAlgorithm;
import org.cmg.jresp.policy.facpl.IFacplPolicy;

public class FirstApplicable implements ICombiningAlgorithm {

	@Override
	public AuthorizationResponse evaluate(List<IFacplPolicy> elements, AuthorizationRequest request, String thisValue) {

		AuthorizationResponse dr = new AuthorizationResponse();

		for (IFacplPolicy el : elements) {
			dr = el.evaluate(request, thisValue);
			if (dr.getDecision().equals(AuthorizationDecision.DENY)) {
				return dr;
			}
			if (dr.getDecision().equals(AuthorizationDecision.PERMIT)) {
				return dr;
			}
			if (dr.getDecision().equals(AuthorizationDecision.NOT_APPLICABLE)) {
				continue;
			}
			if (dr.getDecision().equals(AuthorizationDecision.INDETERMINATE)) {
				return dr;
			}
		}
		dr.setDecision(AuthorizationDecision.NOT_APPLICABLE);
		return dr;
	}

}
