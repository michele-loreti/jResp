/**
 * 
 */
package org.cmg.jresp.policy;

import org.cmg.jresp.policy.facpl.MatchDecision;

/**
 * Interface for the FACPL element, i.e. policy, policy set or rule, that could
 * be evaluate a combining algorithm
 * 
 * @author Andrea Margheri
 */
public interface IAuthorisationPolicy {

	/**
	 * The method for retrieving an authorization decision for a request
	 * 
	 * @param request
	 * @return
	 */
	AuthorizationResponse evaluate(AuthorizationRequest request, String thisValue);

	MatchDecision getTargetDecision(AuthorizationRequest request, String thisValue);

}
