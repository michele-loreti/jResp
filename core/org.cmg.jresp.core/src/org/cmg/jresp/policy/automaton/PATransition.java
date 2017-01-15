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
 *      Andrea Margheri
 */
package org.cmg.jresp.policy.automaton;

import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.facpl.MatchDecision;
import org.cmg.jresp.policy.facpl.elements.TargetExpression;
import org.cmg.jresp.policy.facpl.elements.util.TargetTreeRepresentation;

/**
 * @author Andrea Margheri
 *
 */
public class PATransition {

	protected TargetTreeRepresentation target;

	protected int nextState;

	public PATransition(TargetTreeRepresentation cnd, int nextState) {
		this.target = cnd;
		this.nextState = nextState;
	}

	public PATransition(TargetExpression cnd, int nextState) {
		this.target = new TargetTreeRepresentation(cnd);
		this.nextState = nextState;
	}

	/**
	 * Check if the authorization request received as argument matches the
	 * current transition
	 * 
	 * @return
	 */
	public boolean isEnabled(AuthorizationRequest req, String thisValue) {
		if (target.getDecisionValue(req, thisValue).equals(MatchDecision.MATCH))
			return true;
		else
			return false;
	}

	public int nextState() {
		return nextState;
	}

}
