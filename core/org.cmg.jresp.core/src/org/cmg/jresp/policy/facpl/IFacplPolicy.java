/**
 * 
 */
package org.cmg.jresp.policy.facpl;

import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.AuthorizationResponse;

/**
 * Interface for the FACPL element, i.e. policy, policy set or rule, that could
 * be evaluate a combining algorithm
 * 
 * @author Andrea Margheri
 */
public interface IFacplPolicy {

	/**
	 * The method for retrieving an authorization decision for a request
	 * 
	 * @param request
	 * @return
	 */
	AuthorizationResponse evaluate(AuthorizationRequest request, String thisValue);

	MatchDecision getTargetDecision(AuthorizationRequest request, String thisValue);

}
