/**
 * Copyright (c) 2014 Concurrency and Mobility Group.
 * Universita' di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.jresp.policy.facpl;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.cmg.jresp.policy.AuthorizationDecision;
import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.AuthorizationResponse;
import org.cmg.jresp.policy.automaton.IPolicyAutomatonState;

/**
 * @author Andrea Margheri
 * 
 */
public class FacplPolicyState implements IPolicyAutomatonState {

	// private PolicyAutomaton automata;

	private Class<? extends ICombiningAlgorithm> algorithm;
	private List<IFacplPolicy> policies;

	public FacplPolicyState(Class<? extends ICombiningAlgorithm> algorithm, IFacplPolicy... policy) {
		this.algorithm = algorithm;
		this.policies = new LinkedList<IFacplPolicy>();
		for (IFacplPolicy p : policy) {
			this.policies.add(p);
		}
	}

	public FacplPolicyState(IFacplPolicy policy) {
		this.policies = new LinkedList<IFacplPolicy>();
		this.policies.add(policy);
	}

	/**
	 * Invoke the authorization of the request on the policy of the current
	 * PolicyAutomaton state
	 */
	@Override
	public AuthorizationResponse evaluate(AuthorizationRequest r, String thisValue) {

		if (this.policies.size() == 1) {
			// Single policy in the state
			return this.policies.get(0).evaluate(r, thisValue);
		} else {
			// Multiple Policies in the state
			// System.out
			// .println("Mutliple policies in the state. Evaluation according to
			// the chosen combining algorithm");

			Class<?> params[] = new Class[2];
			params[0] = List.class;
			params[1] = AuthorizationRequest.class;

			try {
				Method eval = algorithm.getDeclaredMethod("evaluate", params);
				Object alg = algorithm.newInstance();
				return (AuthorizationResponse) eval.invoke(alg, policies, r);
			} catch (Exception e) {
				e.printStackTrace();
				// returns error
				return new AuthorizationResponse(AuthorizationDecision.INDETERMINATE);
			}
		}
	}

}
