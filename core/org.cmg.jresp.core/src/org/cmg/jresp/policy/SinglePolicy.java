package org.cmg.jresp.policy;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.policy.facpl.ObligationType;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Target;

/**
 * 
 * @author Andrea Margheri
 * 
 */
public class SinglePolicy extends AuthorizationPolicy implements INodePolicy {

	private IAuthorisationPolicy authPolicy;

	public SinglePolicy(IAuthorisationPolicy policy) {
		this.authPolicy = policy;
	}

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

		AuthorizationResponse res = this.authPolicy.evaluate(req, this.node.getName());
		this.lock.unlock();

		if (res.getDecision() == AuthorizationDecision.PERMIT) {
			// Action Authorised
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			// System.out.println("Permitting put action by " + from.toString()
			// + " with argument "+ tuple.toString());
			node.put(from, session, tuple);
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		} else {
			// action not authorised, obligation actions executed
			System.out.println("Denying put action by " + from.toString() + "  with argument " + tuple.toString());
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
			node.sendFail(from, session,
					"Denying put action by " + from.toString() + "  with argument " + tuple.toString());
		}
		return true;
	}

	@Override
	public Tuple acceptGet(PointToPoint source, int session, Template template)
			throws InterruptedException, IOException {
		Tuple t = null;
		this.lock.lock();

		Attribute[] subAttributes = node.sendAttributeRequest(source);
		AuthorizationRequest req = new AuthorizationRequest(
				// Subject
				source.getName(),
				// Object
				this.node.getName(), ActionThisID.ACCEPT_GET, template, source,
				// Subject
				subAttributes,
				// Object
				getAttributes(node.getInterface()));

		AuthorizationResponse res = this.authPolicy.evaluate(req, this.node.getName());
		this.lock.unlock();

		if (res.getDecision() == AuthorizationDecision.PERMIT) {
			// Action Authorised
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			t = node.get(template);
			// System.out.println("Permitting get action by " + from + " with
			// template "+ template.toString());
			if (t != null) {
				node.sendTuple(source, session, t);
			} else {
				node.sendFail(source, session, "Tuple not found!");
			}

			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		} else {
			// action not authorised, obligation actions executed
			System.out.println("Denying get action by " + source + " with template " + template.toString());
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
	public Tuple acceptQuery(PointToPoint source, int session, Template template)
			throws InterruptedException, IOException {
		Tuple t = null;
		this.lock.lock();

		Attribute[] subAttributes = node.sendAttributeRequest(source);
		AuthorizationRequest req = new AuthorizationRequest(
				// Subject
				source.getName(),
				// Object
				this.node.getName(), ActionThisID.ACCEPT_QRY, template, source,
				// Subject
				subAttributes,
				// Object
				getAttributes(node.getInterface()));

		AuthorizationResponse res = this.authPolicy.evaluate(req, this.node.getName());
		this.lock.unlock();

		if (res.getDecision() == AuthorizationDecision.PERMIT) {
			// Action Authorised
			// Execution of before actions
			if (res.getObligations(ObligationType.BEFORE).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.BEFORE));
			}
			// execution of action
			t = node.query(template);
			// System.out.println("Permitting query action by " +
			// from.toString() + " with argument "+ t.toString());

			if (t != null) {
				node.sendTuple(source, session, t);
			} else {
				node.sendFail(source, session, "Tuple not found!");
			}

			// execution of after actions
			if (res.getObligations(ObligationType.AFTER).size() > 0) {
				executeActions(node, res.getObligations(ObligationType.AFTER));
			}
		} else {
			System.out.println("Denying query action by " + source + " with template " + template.toString());
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
	public boolean put(Agent a, Tuple t, Target l) throws InterruptedException, IOException {
		AuthorizationResponse res = null;
		AuthorizationRequest req = null;
		boolean isAuthorised = false;

		while (!isAuthorised) {

			this.lock.lock();
			// Create authorisation request
			req = createRequest(ActionThisID.PUT, l, t);
			// Evaluation of request
			res = this.authPolicy.evaluate(req, this.node.getName());
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
			res = this.authPolicy.evaluate(req, this.node.getName());
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
			res = this.authPolicy.evaluate(req, this.node.getName());
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
				res = this.authPolicy.evaluate(req, this.node.getName());
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
		AuthorizationResponse res = this.authPolicy.evaluate(req, this.node.getName());
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
		AuthorizationResponse res = this.authPolicy.evaluate(req, this.node.getName());
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
		AuthorizationResponse res = this.authPolicy.evaluate(req, this.node.getName());
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
				res = this.authPolicy.evaluate(req, this.node.getName());
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
				res = this.authPolicy.evaluate(req, this.node.getName());
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
				res = this.authPolicy.evaluate(req, this.node.getName());
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

}
