package org.cmg.jresp.policy.facpl;

import java.util.ArrayList;
import java.util.LinkedList;

import org.cmg.jresp.policy.ActionThisID;
import org.cmg.jresp.policy.facpl.elements.util.FormalObligationVariable;

/**
 * The obligation fulfilled by the policy evaluation
 * 
 * @author Andrea Margheri
 * 
 */
public class FulfilledObligation {

	private RuleEffect effect;
	private ActionThisID action;
	private ObligationType type;
	private LinkedList<Object> arguments;
	/*
	 * At position i, if present, there is the name and type of the formal
	 * variable
	 */
	private ArrayList<FormalObligationVariable> formalVariables;

	public FulfilledObligation(RuleEffect evaluatedOn, ObligationType type, ActionThisID action,
			ArrayList<FormalObligationVariable> variables, Object... arguments) {
		this.effect = evaluatedOn;
		this.action = action;
		this.type = type;
		this.formalVariables = variables;
		this.arguments = new LinkedList<Object>();
	}

	public void addArg(Object object) {
		if (this.arguments == null) {
			this.arguments = new LinkedList<Object>();
		}
		this.arguments.add(object);
	}

	public LinkedList<Object> getArguments() {
		return arguments;
	}

	public ArrayList<FormalObligationVariable> getFormalVariables() {
		return formalVariables;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append(action.toString() + "(");
		for (Object o : arguments) {
			str.append(o.toString());
		}
		str.append(")");
		return str.toString();
	}

	public RuleEffect getEvaluatedOn() {
		return effect;
	}

	public void setEvaluatedOn(RuleEffect evaluatedOn) {
		this.effect = evaluatedOn;
	}

	public ActionThisID getActionId() {
		return action;
	}

	public ObligationType getType() {
		return type;
	}

}
