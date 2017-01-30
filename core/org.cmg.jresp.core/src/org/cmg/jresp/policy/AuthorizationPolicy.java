/**
 * 
 */
package org.cmg.jresp.policy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cmg.jresp.comp.INode;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.policy.automaton.PolicyAutomaton;
import org.cmg.jresp.policy.facpl.FulfilledObligation;
import org.cmg.jresp.policy.facpl.elements.ExpressionItem;
import org.cmg.jresp.policy.facpl.elements.util.FormalObligationVariable;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Margheri
 *
 */
public abstract class AuthorizationPolicy {

	protected INode node;

	protected Lock lock = new ReentrantLock();

	
	public void setNode(INode node) {
		this.node = node;
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
	protected void executeActions(INode node, List<FulfilledObligation> obligations)
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

	protected Attribute[] getAttributes(HashMap<String, Attribute> inf) {
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
	protected AuthorizationRequest createRequest(ActionThisID actionId, Target l, Object t)
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

	
	
}
