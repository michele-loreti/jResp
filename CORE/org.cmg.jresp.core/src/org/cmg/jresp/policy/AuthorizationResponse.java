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
package org.cmg.jresp.policy;

import java.util.LinkedList;
import java.util.List;

import org.cmg.jresp.policy.facpl.FulfilledObligation;
import org.cmg.jresp.policy.facpl.ObligationType;

/**
 * @author Michele Loreti
 * @author Andrea Margheri
 * 
 */
public class AuthorizationResponse {

	private AuthorizationDecision decision;

	/**
	 * Notably, we avoid here to use the Agent, the list of ObligationValue is
	 * used as returning value of policy evaluation
	 */
	private List<FulfilledObligation> obligations;

	public AuthorizationResponse(AuthorizationDecision decision, List<FulfilledObligation> agent) {
		this.decision = decision;
		this.obligations = agent;
	}

	public AuthorizationResponse(AuthorizationDecision decision) {
		this.decision = decision;
		this.obligations = new LinkedList<FulfilledObligation>();
	}

	public AuthorizationResponse() {
		this.obligations = new LinkedList<FulfilledObligation>();
	}

	public AuthorizationDecision getDecision() {
		return decision;
	}

	public List<FulfilledObligation> getAllObligations() {
		return obligations;
	}

	public List<FulfilledObligation> getObligations(ObligationType t) {
		LinkedList<FulfilledObligation> l = new LinkedList<FulfilledObligation>();
		for (FulfilledObligation ob : this.obligations) {
			if (ob.getType().equals(t)) {
				l.add(ob);
			}
		}
		return l;
	}

	public void setDecision(AuthorizationDecision decision) {
		this.decision = decision;
	}

	public void addObligations(List<FulfilledObligation> obls) {
		for (FulfilledObligation o : obls) {
			obligations.add(o);
		}
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append(this.decision.toString() + " \n ");
		for (FulfilledObligation o : this.obligations) {
			str.append(o.toString() + "\n");
		}
		return str.toString();
	}

}
