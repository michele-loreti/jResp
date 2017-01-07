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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//import org.cmg.resp.behaviour.Action;
import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.INode;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.policy.ActionThisID;
import org.cmg.jresp.policy.AuthorizationDecision;
import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.AuthorizationResponse;
import org.cmg.jresp.policy.IPolicy;
import org.cmg.jresp.policy.facpl.FulfilledObligation;
import org.cmg.jresp.policy.facpl.ObligationType;
import org.cmg.jresp.policy.facpl.elements.ExpressionItem;
import org.cmg.jresp.policy.facpl.elements.util.FormalObligationVariable;
import org.cmg.jresp.policy.facpl.elements.util.TargetTreeRepresentation;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Loreti
 * @author Andrea Margheri
 * 
 */
public class PolicyAutomaton implements IPolicy {

	private ArrayList<IPolicyAutomatonState> policyStates;

	private ArrayList<LinkedList<PATransition>> rules;

	private int currentState;

	private INode node;

	private Lock lock = new ReentrantLock();

	public PolicyAutomaton() {
		this.policyStates = new ArrayList<IPolicyAutomatonState>();
		this.rules = new ArrayList<LinkedList<PATransition>>();
		this.currentState = 0;
	}

	public PolicyAutomaton(IPolicyAutomatonState... states) {
		this.policyStates = new ArrayList<IPolicyAutomatonState>();
		for (int i = 0; i < states.length; i++) {
			this.policyStates.add(states[i]);
		}
		this.rules = new ArrayList<LinkedList<PATransition>>(states.length);
		// initialise list of transition for each state
		for (int i = 0; i < states.length; i++) {
			this.rules.add(i, new LinkedList<PATransition>());
		}
		this.currentState = 0;
	}

	public void addState(int index, IPolicyAutomatonState state) {
		this.policyStates.add(index, state);
	}

	public void addState(IPolicyAutomatonState state) {
		this.policyStates.add(state);
	}

	public void setCurrentState(int idx) {
		this.currentState = idx;
	}

	/**
	 * Return the number of automaton's state
	 * 
	 * @return
	 */
	public int size() {
		return policyStates.size();
	}

	/**
	 * Add a transition in the automaton transition function
	 * 
	 * @param source
	 *            state
	 * @param TransitionCondition
	 * @param target
	 *            state
	 */
	public void addTransitionRule(int src, int trg, TargetTreeRepresentation condition) {
		_addTransitionRule(src, new PATransition(condition, trg));
	}

	/**
	 * Add all the transitions to the automaton
	 * 
	 * @param rules
	 */
	public void addTransitionRules(ArrayList<LinkedList<PATransition>> rules) {
		this.rules = rules;
	}

	/**
	 * Add Transition to the Automaton
	 * 
	 * @param src
	 * @param paTransition
	 */
	private void _addTransitionRule(int src, PATransition paTransition) {
		LinkedList<PATransition> stateRules = rules.get(src);
		if (stateRules == null) {
			stateRules = new LinkedList<PATransition>();
			rules.set(src, stateRules);
		}
		stateRules.add(paTransition);
	}

	/**
	 * Authorize the AuthorizationRequest created by the methods implementing
	 * the authorization predicates
	 * 
	 * @param req
	 *            AuthorizationRequest
	 * @return AuthorizationDecion and possibly a sequence of actions
	 */
	private AuthorizationResponse evaluateRequestOnState(AuthorizationRequest req) {
		return policyStates.get(currentState).evaluate(req, this.node.getName());
	}

	/**
	 * Evaluate the transition function in order to calculate the new
	 * automaton's state
	 * 
	 * @param req
	 */
	private void updatePolicState(AuthorizationRequest req) {
		if (rules.size() > 0) {
			LinkedList<PATransition> stateTransitions = rules.get(currentState);
			for (PATransition paTransition : stateTransitions) {
				// ADD policy Name
				if (paTransition.isEnabled(req, node.getName())) {
					System.out.println("Automaton state updated to: " + paTransition.nextState);
					currentState = paTransition.nextState;
					return;
				}
			}
		}
	}

	/*
	 * ##################################################### -> Implementation
	 * of AUTHORIZATION PREDICATEs ->-> These are the entry points invoked by
	 * Node in order to authorize the action ->-> (and then execute it)
	 * #####################################################
	 */

	/*
	 * ---> Point-To-Point remote authorization predicate
	 */

	@Override
	public boolean acceptPut(PointToPoint from, int session, Tuple tuple) throws InterruptedException, IOException {
		this.lock.lock();

		Attribute[] subAttributes = node.sendAttributeRequest(from);
		AuthorizationRequest req = new AuthorizationRequest(
				// Subject
				from.getName(),
				// Object
				this.node.getName(), ActionThisID.ACCEPT_PUT, tuple, from,
				// Subject
				subAttributes,
				// Object
				getAttributes(node.getInterface()));

		AuthorizationResponse res = evaluateRequestOnState(req);
		updatePolicState(req);
		this.lock.unlock();

		if (res.getDecision() == AuthorizationDecision.PERMIT) {
			// Action Authorised
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			System.out.println("Permitting put action with argument "+ tuple.toString());
			node.put(from, session, tuple);
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		} else {
			// action not authorised, obligation actions executed
			System.out.println("Denying put action with argument "+ tuple.toString());
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
			node.sendFail(from, session, "Denying put action with argument "+ tuple.toString());
		}
		return true;
	}

	@Override
	public Tuple acceptGet(PointToPoint from, int session, Template template) throws InterruptedException, IOException {
		Tuple t = null;
		this.lock.lock();

		Attribute[] subAttributes = node.sendAttributeRequest(from);
		AuthorizationRequest req = new AuthorizationRequest(
				// Subject
				from.getName(),
				// Object
				this.node.getName(), ActionThisID.ACCEPT_GET, template, from,
				// Subject
				subAttributes,
				// Object
				getAttributes(node.getInterface()));

		AuthorizationResponse res = evaluateRequestOnState(req);
		updatePolicState(req);
		this.lock.unlock();

		if (res.getDecision() == AuthorizationDecision.PERMIT) {
			// Action Authorised
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			t = node.get(template);
			System.out.println("Permitting get action with template "+ template.toString());
			if (t != null) {
				node.sendTuple(from, session, t);
			} else {
				node.sendFail(from, session, "Tuple not found!");
			}

			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		} else {
			// action not authorised, obligation actions executed
			System.out.println("Denying get action with template "+ template.toString());
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		}
		return t;
	}

	@Override
	public Tuple acceptQuery(PointToPoint from, int session, Template template)
			throws InterruptedException, IOException {
		Tuple t = null;
		this.lock.lock();

		Attribute[] subAttributes = node.sendAttributeRequest(from);
		AuthorizationRequest req = new AuthorizationRequest(
				// Subject
				from.getName(),
				// Object
				this.node.getName(), ActionThisID.ACCEPT_QRY, template, from,
				// Subject
				subAttributes,
				// Object
				getAttributes(node.getInterface()));

		AuthorizationResponse res = evaluateRequestOnState(req);
		updatePolicState(req);
		this.lock.unlock();

		if (res.getDecision() == AuthorizationDecision.PERMIT) {
			// Action Authorised
			// Execution of before actions
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			// execution of action
			t = node.query(template);

			if (t != null) {
				node.sendTuple(from, session, t);
			} else {
				node.sendFail(from, session, "Tuple not found!");
			}

			// execution of after actions
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		} else {
			// action not authorised, obligation actions executed
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		}
		return t;
	}

	/*
	 * ---> authorization for local, pointToPoint or group actions
	 */

	@Override
	public boolean put(Agent a, Tuple t, Target l) throws InterruptedException, IOException {

		AuthorizationResponse res = null;
		AuthorizationRequest req = null;
		boolean isAuthorised = false;

		while (!isAuthorised) {

			this.lock.lock();
			// Create authorisation request
			req = createRequest(ActionThisID.PUT, l, t);
			// Evaluation of request
			res = evaluateRequestOnState(req);
			// Update Automaton State
			updatePolicState(req);
			this.lock.unlock();

			if (res.getDecision() == AuthorizationDecision.PERMIT) {
				isAuthorised = true;
			} else {
				System.out.println(a.getName() + " Decision DENY - PUT " + t.toString());
				// action not authorised, executing obligation actions
				if (res.getObligations(ObligationType.BEFORE).size() > 0) {
					executeActions(node, res.getObligations(ObligationType.BEFORE));
				}
				if (res.getObligations(ObligationType.AFTER).size() > 0) {
					executeActions(node, res.getObligations(ObligationType.AFTER));
				}
				Thread.sleep(100);
			}
		}

		// Action Authorised
		if (res.getObligations(ObligationType.BEFORE).size() > 0) {
			executeActions(node, res.getObligations(ObligationType.BEFORE));
		}
		// execution of action put
		node.put(t, l);

		if (res.getObligations(ObligationType.AFTER).size() > 0) {
			executeActions(node, res.getObligations(ObligationType.AFTER));
		}

		return true;
	}

	@Override
	public Tuple get(Agent a, Template t, Target l) throws InterruptedException, IOException {
		AuthorizationResponse res = null;
		AuthorizationRequest req = null;
		boolean isAuthorised = false;

		while (!isAuthorised) {

			this.lock.lock();
			// Create authorisation request
			req = createRequest(ActionThisID.GET, l, t);
			// Evaluation of request
			res = evaluateRequestOnState(req);
			// Update Automaton State
			updatePolicState(req);
			this.lock.unlock();

			if (res.getDecision() == AuthorizationDecision.PERMIT) {
				isAuthorised = true;
			} else {
				System.out.println(a.getName() + " Decision DENY - GET " + t.toString());
				// action not authorised, executing obligation actions
				if (res.getObligations(ObligationType.BEFORE).size() > 0) {
					executeActions(node, res.getObligations(ObligationType.BEFORE));
				}
				if (res.getObligations(ObligationType.AFTER).size() > 0) {
					executeActions(node, res.getObligations(ObligationType.AFTER));
				}
				Thread.sleep(100);
			}
		}

		Tuple result = null;

		if (res.getObligations(ObligationType.BEFORE).size() > 0) {
			executeActions(node, res.getObligations(ObligationType.BEFORE));
		}
		// execution of action get
		result = node.get(t, l);
		if (res.getObligations(ObligationType.AFTER).size() > 0) {
			executeActions(node, res.getObligations(ObligationType.AFTER));
		}

		return result;
	}

	@Override
	public Tuple query(Agent a, Template t, Target l) throws InterruptedException, IOException {

		AuthorizationResponse res = null;
		AuthorizationRequest req = null;
		boolean isAuthorised = false;

		while (!isAuthorised) {

			this.lock.lock();
			// Create authorisation request
			req = createRequest(ActionThisID.QRY, l, t);
			// Evaluation of request
			res = evaluateRequestOnState(req);
			// Update Automaton State
			updatePolicState(req);
			this.lock.unlock();

			if (res.getDecision() == AuthorizationDecision.PERMIT) {
				isAuthorised = true;
			} else {
				System.out.println(a.getName() + " Decision DENY - QUERY " + t.toString());
				// action not authorised, executing obligation actions
				if (res.getObligations(ObligationType.BEFORE).size() > 0) {
					executeActions(node, res.getObligations(ObligationType.BEFORE));
				}
				if (res.getObligations(ObligationType.AFTER).size() > 0) {
					executeActions(node, res.getObligations(ObligationType.AFTER));
				}
				Thread.sleep(100);
			}
		}

		Tuple result = null;
		if (res.getObligations(ObligationType.BEFORE).size() > 0) {
			executeActions(node, res.getObligations(ObligationType.BEFORE));
		}
		// execution of action get
		result = node.query(t, l);
		if (res.getObligations(ObligationType.AFTER).size() > 0) {
			executeActions(node, res.getObligations(ObligationType.AFTER));
		}

		return result;
	}

	@Override
	public void exec(Agent a, Agent b) throws InterruptedException {
		AuthorizationResponse res = null;
		AuthorizationRequest req = null;
		boolean isAuthorised = false;

		try {

			while (!isAuthorised) {

				this.lock.lock();
				/*
				 * Subject and Object identifiers and interfaces are equal in
				 * the case of EXEC actions
				 */
				req = new AuthorizationRequest(this.node.getName(), this.node.getName(), ActionThisID.EXEC, b, null,
						getAttributes(node.getInterface()), getAttributes(node.getInterface()));
				res = evaluateRequestOnState(req);
				updatePolicState(req);
				this.lock.unlock();

				if (res.getDecision() == AuthorizationDecision.PERMIT) {
					isAuthorised = true;
				} else {
					System.out.println("Decision DENY - EXEC");
					// action not authorised, executing obligation actions
					if (res.getObligations(ObligationType.BEFORE).size() > 0) {
						executeActions(node, res.getObligations(ObligationType.BEFORE));
					}
					if (res.getObligations(ObligationType.AFTER).size() > 0) {
						executeActions(node, res.getObligations(ObligationType.AFTER));
					}
					Thread.sleep(100);
				}

			}

			// Action Authorised
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			// execution of agent
			a.exec(b);
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new InterruptedException();
		}

	}

	@Override
	public String fresh(Agent a) throws InterruptedException {
		AuthorizationResponse res = null;
		AuthorizationRequest req = null;
		boolean isAuthorised = false;

		try {

			while (!isAuthorised) {

				this.lock.lock();
				/*
				 * Subject and Object identifiers and interfaces are equal in
				 * the case of FRESH actions
				 */
				req = new AuthorizationRequest(this.node.getName(), this.node.getName(), ActionThisID.FRESH, null, null,
						getAttributes(node.getInterface()), getAttributes(node.getInterface()));
				res = evaluateRequestOnState(req);
				updatePolicState(req);
				this.lock.unlock();

				if (res.getDecision() == AuthorizationDecision.PERMIT) {
					isAuthorised = true;
				} else {
					System.out.println(a.getName() + " Decision DENY - FRESH");
					// action not authorised, executing obligation actions
					if (res.getObligations(ObligationType.BEFORE).size() > 0) {
						executeActions(node, res.getObligations(ObligationType.BEFORE));
					}
					if (res.getObligations(ObligationType.AFTER).size() > 0) {
						executeActions(node, res.getObligations(ObligationType.AFTER));
					}
					Thread.sleep(100);
				}
			}

			String result = "";
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			// execution of action fresh
			result = node.fresh();
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
			return result;

		} catch (IOException e) {
			e.printStackTrace();
			throw new InterruptedException();
		}
	}

	@Override
	public Object readAttribute(String name) throws InterruptedException {
		AuthorizationResponse res = null;
		boolean isAuthorised = false;
		try {
			while (!isAuthorised) {

				this.lock.lock();
				/*
				 * Subject and Object identifiers and interfaces are the same
				 */
				AuthorizationRequest req = new AuthorizationRequest(this.node.getName(), this.node.getName(),
						ActionThisID.READ, null, null, getAttributes(node.getInterface()),
						getAttributes(node.getInterface()));
				res = evaluateRequestOnState(req);
				updatePolicState(req);
				this.lock.unlock();

				if (res.getDecision() == AuthorizationDecision.PERMIT) {
					isAuthorised = true;
				} else {
					System.out.println("Decision DENY - READAttr");
					// action not authorised, executing obligation actions
					if (res.getObligations(ObligationType.BEFORE).size() > 0) {
						executeActions(node, res.getObligations(ObligationType.BEFORE));
					}
					if (res.getObligations(ObligationType.AFTER).size() > 0) {
						executeActions(node, res.getObligations(ObligationType.AFTER));
					}
					Thread.sleep(100);
				}
			}

			Object result = null;
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}

			// execution of action read
			result = node.readAttribute(name);

			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}

			return result;

		} catch (IOException e) {
			e.printStackTrace();
			throw new InterruptedException();
		}
	}

	@Override
	public boolean updateAttribute(String name, Object value) throws InterruptedException {
		AuthorizationResponse res = null;
		boolean isAuthorised = false;
		try {
			while (!isAuthorised) {

				this.lock.lock();
				/*
				 * Subject and Object identifiers and interfaces are the same
				 */
				AuthorizationRequest req = new AuthorizationRequest(this.node.getName(), this.node.getName(),
						ActionThisID.UPD, null, null, getAttributes(node.getInterface()),
						getAttributes(node.getInterface()));
				res = evaluateRequestOnState(req);
				updatePolicState(req);
				this.lock.unlock();

				if (res.getDecision() == AuthorizationDecision.PERMIT) {
					isAuthorised = true;
				} else {
					System.out.println("Decision DENY - UPDATEAttr");
					// action not authorised, executing obligation actions
					if (res.getObligations(ObligationType.BEFORE).size() > 0) {
						executeActions(node, res.getObligations(ObligationType.BEFORE));
					}
					if (res.getObligations(ObligationType.AFTER).size() > 0) {
						executeActions(node, res.getObligations(ObligationType.AFTER));
					}
					Thread.sleep(100);
				}
			}

			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}

			// execution of action upd
			node.updateAttribute(name, value);

			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			throw new InterruptedException();
		}
	}

	/* ################################################################# */

	/*
	 * ---> Group-oriented remote authorization predicate
	 */

	@Override
	public void acceptGroupPut(PointToPoint from, int session, GroupPredicate groupPredicate, Tuple tuple)
			throws IOException, InterruptedException {
		this.lock.lock();
		Attribute[] subAttributes = node.sendAttributeRequest(from);
		AuthorizationRequest req = new AuthorizationRequest(
				// Subject
				from.getName(),
				// Object
				this.node.getName(), ActionThisID.ACCEPT_PUT, tuple, from,
				// Subject
				subAttributes,
				// Object
				getAttributes(node.getInterface()));
		AuthorizationResponse res = evaluateRequestOnState(req);
		updatePolicState(req);
		this.lock.unlock();
		if (res.getDecision() == AuthorizationDecision.PERMIT) {
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			// execution of action fresh
			node.gPut(from, session, groupPredicate, tuple);
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		} else {
			// action not authorised, obligation actions executed
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		}
	}

	@Override
	public Tuple acceptGroupGet(PointToPoint from, int session, GroupPredicate groupPredicate, Template template)
			throws IOException, InterruptedException {
		this.lock.lock();
		Attribute[] subAttributes = node.sendAttributeRequest(from);
		AuthorizationRequest req = new AuthorizationRequest(
				// Subject
				from.getName(),
				// Object
				this.node.getName(), ActionThisID.ACCEPT_GET, template, from,
				// Subject
				subAttributes,
				// Object
				getAttributes(node.getInterface()));
		AuthorizationResponse res = evaluateRequestOnState(req);
		updatePolicState(req);
		this.lock.unlock();
		Tuple t = new Tuple();
		if (res.getDecision() == AuthorizationDecision.PERMIT) {
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			// execution of action fresh
			t = node.gGet(from, session, groupPredicate, template);
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		} else {
			// action not authorised, obligation actions executed
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		}
		return t;
	}

	@Override
	public Tuple acceptGroupQuery(PointToPoint from, int session, GroupPredicate groupPredicate, Template template)
			throws IOException, InterruptedException {
		this.lock.lock();
		Attribute[] subAttributes = node.sendAttributeRequest(from);
		AuthorizationRequest req = new AuthorizationRequest(
				// Subject
				from.getName(),
				// Object
				this.node.getName(), ActionThisID.ACCEPT_QRY, template, from,
				// Subject
				subAttributes,
				// Object
				getAttributes(node.getInterface()));
		AuthorizationResponse res = evaluateRequestOnState(req);
		updatePolicState(req);
		this.lock.unlock();
		Tuple t = new Tuple();
		if (res.getDecision() == AuthorizationDecision.PERMIT) {
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			// execution of action fresh
			t = node.gQuery(from, session, groupPredicate, template);
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		} else {
			// action not authorised, obligation actions executed
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		}
		return t;
	}

	/* ################################################################# */

	/**
	 * Handle for executing Obligation Actions
	 * 
	 * @param node
	 * @param obligations
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void executeActions(INode node, List<FulfilledObligation> obligations)
			throws InterruptedException, IOException {
		Logger l = LoggerFactory.getLogger(PolicyAutomaton.class);

		HashMap<String, FormalObligationVariable<?>> obl_vars = new HashMap<String, FormalObligationVariable<?>>();
		try {
			for (FulfilledObligation o : obligations) {
				switch (o.getActionId()) {
				case GET:
					// System.out.println("Robot "+ this.id +"Obligation -
					// Eseguita
					// azione GET");
					Tuple tg = node.get((Template) o.getArguments().get(0), (Target) o.getArguments().get(1));
					/*
					 * Calculate Variable values and add it to the
					 * FormalObligationValue
					 */
					ArrayList<FormalObligationVariable> l_vars = o.getFormalVariables();
					if (l_vars.size() > 0) {
						l.info("GET obligation action with formal variable");
					}
					for (int i = 0; i < l_vars.size(); i++) {
						if (l_vars.get(i) != null) {
							try {
								l_vars.get(i).setValue(tg.getElementAt(i));
								obl_vars.put(l_vars.get(i).getVarName(), l_vars.get(i));
							} catch (ClassCastException e) {
								e.printStackTrace();
								throw e;
							}
						}
					}
					break;
				case QRY:
					// System.out.println("Robot "+ this.id +"Obligation -
					// Eseguita
					// azione QUERY");
					Tuple tq = node.query((Template) o.getArguments().get(0), (Target) o.getArguments().get(1));
					/*
					 * Calculate Variable values and add it to the
					 * FormalObligationValue
					 */
					ArrayList<FormalObligationVariable> l_vars_q = o.getFormalVariables();
					if (l_vars_q.size() > 0) {
						l.info("QRY obligation action with formal variable");
					}
					for (int i = 0; i < l_vars_q.size(); i++) {
						if (l_vars_q.get(i) != null) {
							try {
								l_vars_q.get(i).setValue(tq.getElementAt(i));
								obl_vars.put(l_vars_q.get(i).getVarName(), l_vars_q.get(i));
							} catch (ClassCastException e) {
								e.printStackTrace();
								throw e;
							}
						}
					}
					break;
				case PUT:
					// System.out.println("Robot "+ this.id +"Obligation -
					// Eseguita
					// azione PUT :" +
					// ((Tuple)o.getArguments().get(0)).toString()
					// );
					node.put((Tuple) o.getArguments().get(0), (Target) o.getArguments().get(1));
					/*
					 * Check and fulfill if a variable occur
					 */
					Tuple tp = (Tuple) o.getArguments().get(0);
					if (!hasVariable(tp)) {
						/*
						 * No variable
						 */
						l.info("No Variable to Replace");

						node.put(tp, (Target) o.getArguments().get(1));
					} else {
						/*
						 * Variables occur -> Replace values
						 */
						l.info("Tuple with variables to Replace: " + tp.toString());

						/*
						 * List of arguments that will form the tuple argument
						 * of the obligation actions
						 */
						Object[] args_T = new Object[tp.length()];

						for (int i = 0; i < tp.length(); i++) {
							if (tp.getElementAt(i) instanceof FormalObligationVariable) {
								args_T[i] = obl_vars.get(((FormalObligationVariable) tp.getElementAt(i)).getVarName())
										.getValue();
							} else if (tp.getElementAt(i) instanceof ExpressionItem) {
								/*
								 * Replace variables with values and evaluate
								 * expressions
								 */
								((ExpressionItem) tp.getElementAt(i)).updateVariable(obl_vars);
								args_T[i] = ((ExpressionItem) tp.getElementAt(i)).getValue(new AuthorizationRequest());

							} else {
								args_T[i] = tp.getElementAt(i);
							}
						}

						l.info("Variables Replaced: " + Arrays.toString(args_T));

						/*
						 * Execute the action
						 */
						node.put(new Tuple(args_T), (Target) o.getArguments().get(1));
					}
					break;
				case UPD:
					node.updateAttribute((String) o.getArguments().get(0), o.getArguments().get(1));
					break;
				case READ:
					node.readAttribute((String) o.getArguments().get(0));
				case EXEC:

					/*
					 * TODO
					 */

				default:
					// System.out.println("Obligation cannot be executed");
					l.info("ERR: -> Obligation " + o.getActionId() + " Unsupported");
					throw new IOException("Obligation cannot be executed");
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private boolean hasVariable(Tuple tuple) {
		for (int i = 0; i < tuple.length(); i++) {
			if (tuple.getElementAt(i) instanceof FormalObligationVariable) {
				return true;
			} else if (tuple.getElementAt(i) instanceof ExpressionItem) {
				if (((ExpressionItem) tuple.getElementAt(i)).getFormalVariables().size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/* ################################################################# */

	private Attribute[] getAttributes(HashMap<String, Attribute> inf) {
		Attribute[] toReturn = new Attribute[inf.values().size()];
		int i = 0;
		for (Attribute e : inf.values()) {
			toReturn[i] = e;
			i++;
		}
		return toReturn;
	}

	/**
	 * Create AuthorisationRequest
	 * 
	 * @param actionId
	 *            Action ID
	 * @param l
	 *            Action Target
	 * @param t
	 *            Action Argument
	 * @return Authorisation Request
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private AuthorizationRequest createRequest(ActionThisID actionId, Target l, Object t)
			throws IOException, InterruptedException {
		AuthorizationRequest req;

		if (l.isSelf()) {
			// SELF action
			req = new AuthorizationRequest(this.node.getName(), this.node.getName(), actionId, t, l,
					getAttributes(node.getInterface()), getAttributes(node.getInterface()));
		} else {
			if (l.isAGroup()) {
				/*
				 * Group authorisation depends on local actions
				 */
				// TODO AUTHORISATION BASED ON DYNAMICALLY CHECKED COMPONENTS

				req = new AuthorizationRequest(this.node.getName(), this.node.getName(), actionId, t, l,
						getAttributes(node.getInterface()), getAttributes(node.getInterface()));
			} else {
				// Point to Point Authorisation
				String objId = ((PointToPoint) l).getName();
				Attribute[] objInterface = node.sendAttributeRequest((PointToPoint) l);

				req = new AuthorizationRequest(this.node.getName(), objId, actionId, t, l,
						getAttributes(node.getInterface()), objInterface);
			}
		}

		return req;
	}

	@Override
	public void setNode(INode node) {
		this.node = node;
	}

	// /**
	// * Return the Agent corresponding to the Obligation returned by the
	// * authorisation
	// *
	// * @param obls
	// * @param id
	// * @return
	// */
	// private Agent getAgentFromObligations(List<FulfilledObligation> obls,
	// String id) {
	// return new FACPLObligationExecutor(obls, id);
	// }

}
