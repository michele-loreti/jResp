package org.cmg.jresp.policy.facpl.elements;

import java.util.ArrayList;
import java.util.LinkedList;

import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.TemplateField;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.policy.ActionThisID;
import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.RequestAttributeName;
import org.cmg.jresp.policy.facpl.FulfilledObligation;
import org.cmg.jresp.policy.facpl.ObligationType;
import org.cmg.jresp.policy.facpl.RuleEffect;
import org.cmg.jresp.policy.facpl.elements.util.FormalObligationVariable;

/**
 *
 * @author Andrea Margheri
 *
 */
public class ScelObligationExpression {

	private RuleEffect evaluatedOn;
	private ActionThisID scelAction;
	private LinkedList<Object> argsFunction;
	private ObligationType type;

	/*
	 * At position i, if present, there is the name and type of the formal
	 * variable
	 */
	private ArrayList<FormalObligationVariable> formalVariables;

	public ScelObligationExpression(RuleEffect evaluatedOn, ActionThisID scelAction, ObligationType type,
			Object... args) {
		this.scelAction = scelAction;
		this.evaluatedOn = evaluatedOn;
		this.type = type;
		this.argsFunction = new LinkedList<Object>();
		if (args != null) {
			for (Object ob : args) {
				argsFunction.add(ob);
			}
		}
		this.formalVariables = new ArrayList<FormalObligationVariable>();
		calculateFormalVariables(args);
	}

	public ScelObligationExpression(RuleEffect evaluatedOn, ObligationType type, ActionThisID scelAction,
			Object... args) {
		this.scelAction = scelAction;
		this.evaluatedOn = evaluatedOn;
		this.type = type;
		this.argsFunction = new LinkedList<Object>();
		if (args != null) {
			for (Object ob : args) {
				argsFunction.add(ob);
			}
		}
		this.formalVariables = new ArrayList<FormalObligationVariable>();
		calculateFormalVariables(args);
	}

	private void calculateFormalVariables(Object[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Tuple) {
				checkVariable((Tuple) args[i]);
			} else if (args[i] instanceof Template) {
				checkVariable((Template) args[i]);
			}
		}
	}

	private void checkVariable(Template template) {
		for (int i = 0; i < template.length(); i++) {
			if (template.getElementAt(i) instanceof FormalObligationVariable) {
				// Add the variable to the list
				this.formalVariables.add(i, (FormalObligationVariable<?>) template.getElementAt(i));
			} else if (template.getElementAt(i) instanceof FormalTemplateField) {
				FormalObligationVariable var = new FormalObligationVariable(
						((FormalTemplateField) template.getElementAt(i)).getVarName());
				var.setType(((FormalTemplateField) template.getElementAt(i)).getClass());
				this.formalVariables.add(i, var);
			} else {
				this.formalVariables.add(i, null);
			}
		}
	}

	private void checkVariable(Tuple tuple) {
		for (int i = 0; i < tuple.length(); i++) {
			if (tuple.getElementAt(i) instanceof FormalObligationVariable) {
				// Add the variable to the list
				this.formalVariables.add(i, (FormalObligationVariable) tuple.getElementAt(i));
			} else if (tuple.getElementAt(i) instanceof ExpressionItem) {
				this.formalVariables.addAll(i, ((ExpressionItem) tuple.getElementAt(i)).getFormalVariables());
			} else {
				this.formalVariables.add(i, null);
			}
		}
	}

	/*
	 * EVALUATION
	 */
	public FulfilledObligation evalObligation(AuthorizationRequest request) throws Throwable {
		FulfilledObligation obl = new FulfilledObligation(this.evaluatedOn, this.type, this.scelAction,
				this.formalVariables);
		for (Object a : argsFunction) {
			if (a instanceof ExpressionItem) {
				// expression
				obl.addArg(((ExpressionItem) a).getValue(request));
			} else if (a instanceof RequestAttributeName) {
				// attribute
				obl.addArg(request.getAttributeValue((RequestAttributeName) a));
			} else if (a instanceof Tuple) {
				/*
				 * Check if the Tuple has as argument an Attribute Name ->
				 * create a new tuple with the retrieved value
				 */
				if (hasAttributeName((Tuple) a)) {
					ArrayList<Object> ar = new ArrayList<Object>();
					for (int i = 0; i < ((Tuple) a).length(); i++) {
						Object el = ((Tuple) a).getElementAt(i);
						if (el instanceof RequestAttributeName) {
							Object t = request.getAttributeValue((RequestAttributeName) el);
							if (t instanceof Tuple) {
								// Unfold the element of a tuple
								for (int j = 0; j < ((Tuple) t).length(); j++) {
									ar.add(((Tuple) t).getElementAt(j));
								}
							} else if (t instanceof Template) {
								// Unfold the element of a template
								for (int j = 0; j < ((Template) t).length(); j++) {
									ar.add(((Template) t).getElementAt(j));
								}
							} else {
								ar.add(t);
							}
						} else {
							ar.add(el);
						}
					}
					// Create a Tuple from the array list
					obl.addArg(new Tuple(ar.toArray()));
				} else {
					// No attribute names
					obl.addArg(a);
				}
			} else if (a instanceof Template) {
				// Likewise the case of Tuple
				/*
				 * RequestAttributeName -> it is inside the ActualTemplateField
				 */
				if (hasAttributeName((Template) a)) {
					ArrayList<TemplateField> ar = new ArrayList<TemplateField>();
					for (int i = 0; i < ((Template) a).length(); i++) {
						TemplateField el = ((Template) a).getElementAt(i);
						if (el instanceof ActualTemplateField) {
							if (((ActualTemplateField) el).getValue() instanceof RequestAttributeName) {
								// Add values referred to by the requests
								Object ob = request.getAttributeValue(
										(RequestAttributeName) ((ActualTemplateField) el).getValue());
								if (ob instanceof Tuple) {
									// Unfold the element of a tuple
									for (int j = 0; j < ((Tuple) ob).length(); j++) {
										ar.add(new ActualTemplateField(((Tuple) ob).getElementAt(j)));
									}
								} else if (ob instanceof Template) {
									// Unfold the element of a template
									for (int j = 0; j < ((Template) ob).length(); j++) {
										ar.add(new ActualTemplateField(((Tuple) ob).getElementAt(j)));
									}
								} else {
									ar.add(new ActualTemplateField(ob));
								}
							} else {
								ar.add(i, el);
							}
						} else {
							ar.add(i, el);
						}
					}
					// Create a Template from the array list
					TemplateField[] els = new TemplateField[ar.size()];
					obl.addArg(new Template(ar.toArray(els)));
				} else {
					// No attribute names
					obl.addArg(a);
				}

			} else {
				// literal or bag
				obl.addArg(a);
			}
		}
		return obl;
	}

	private boolean hasAttributeName(Tuple a) {
		for (int i = 0; i < a.length(); i++) {
			if (a.getElementAt(i) instanceof RequestAttributeName)
				return true;
		}
		return false;
	}

	private boolean hasAttributeName(Template a) {
		for (int i = 0; i < a.length(); i++) {
			TemplateField el = ((Template) a).getElementAt(i);
			if (el instanceof ActualTemplateField) {
				if (((ActualTemplateField) el).getValue() instanceof RequestAttributeName) {
					return true;
				}
			}
		}
		return false;
	}

	public RuleEffect getEvaluatedOn() {
		return evaluatedOn;
	}

	public void setEvaluatedOn(RuleEffect evaluatedOn) {
		this.evaluatedOn = evaluatedOn;
	}

	public ActionThisID getScelAction() {
		return scelAction;
	}

	public void setScelAction(ActionThisID scelAction) {
		this.scelAction = scelAction;
	}

	public LinkedList<Object> getArgsFunction() {
		return argsFunction;
	}

	public void setArgsFunction(LinkedList<Object> argsFunction) {
		this.argsFunction = argsFunction;
	}

}
