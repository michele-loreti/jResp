package org.cmg.jresp.policy.facpl.algorithm;

import java.util.LinkedList;
import java.util.List;

import org.cmg.jresp.policy.AuthorizationDecision;
import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.AuthorizationResponse;
import org.cmg.jresp.policy.facpl.FulfilledObligation;
import org.cmg.jresp.policy.facpl.ICombiningAlgorithm;
import org.cmg.jresp.policy.facpl.IFacplPolicy;

public class PermitUnlessDeny implements ICombiningAlgorithm {

	@Override
	public AuthorizationResponse evaluate(List<IFacplPolicy> elements, AuthorizationRequest request, String thisValue) {

		LinkedList<FulfilledObligation> obls_permit = new LinkedList<FulfilledObligation>();

		AuthorizationResponse dr = new AuthorizationResponse();
		for (IFacplPolicy el : elements) {
			AuthorizationResponse d = el.evaluate(request, thisValue);
			if (d.getDecision().equals(AuthorizationDecision.DENY)) {
				dr.setDecision(AuthorizationDecision.DENY);
				dr.addObligations(d.getAllObligations());
				return dr;
			} else {
				if (d.getDecision().equals(AuthorizationDecision.PERMIT)) {
					obls_permit.addAll(d.getAllObligations());
				}
			}
		}

		dr.setDecision(AuthorizationDecision.PERMIT);
		dr.addObligations(obls_permit);
		return dr;
	}

}
