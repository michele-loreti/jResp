package org.cmg.jresp.policy.facpl;

import java.util.List;

import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.AuthorizationResponse;

public interface ICombiningAlgorithm {

	/**
	 * Given a set of IFacplElement the authorization request given as parameter
	 * is evaluated. We do not consider the extended indeterminate values.
	 * 
	 * @param elements
	 * @param request
	 * @param thisValue
	 *            name of the this node
	 * @return
	 */
	AuthorizationResponse evaluate(List<IFacplPolicy> elements, AuthorizationRequest request, String thisValue);
}
