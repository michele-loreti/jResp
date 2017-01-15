package org.cmg.jresp.policy.facpl.elements;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.cmg.jresp.policy.ActionThisID;
import org.cmg.jresp.policy.RequestAttributeName;
import org.cmg.jresp.policy.facpl.IComparisonFunction;
import org.cmg.jresp.policy.facpl.MatchDecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Target expression representation
 * 
 * @author Andrea Margheri
 * 
 */
public class TargetExpression {

	private Class<? extends IComparisonFunction> comparisonFunction;

	/*
	 * req(attr_name) << comparison function >> literal
	 */
	
	private RequestAttributeName attr_name;
	private Object literal;


	public TargetExpression(Class<? extends IComparisonFunction> function, RequestAttributeName attr_name,
			Object literal) {

		this.comparisonFunction = function;
		this.attr_name = attr_name;
		this.literal = literal;
	}

	public MatchDecision evaluateTarget(Object reqValue, String thisValue) throws Throwable {

		Logger l = LoggerFactory.getLogger(ExpressionItem.class);
		l.debug("Evaluate Target Arguments");

		// values is Basic Type Object or Bag of values
		if (reqValue == null) {
			return MatchDecision.NO_MATCH;
		}

		Class<?> params[] = new Class[1];
		params[0] = List.class;

		Method eval;
		eval = comparisonFunction.getDeclaredMethod("evaluateFunction", params);

		Object alg = comparisonFunction.newInstance();
		MatchDecision dec;

		/*
		 * Create list of params: -> First: reqValue -> Second: literal
		 */
		List<Object> args = new LinkedList<Object>();

		/*
		 * FIRST
		 */
		args.add(reqValue);

		/*
		 * SECOND
		 */

		// Substitute THIS
		if (literal.equals(ActionThisID.THIS)) {
			args.add(thisValue);
		} else {
			args.add(literal);
		}

		l.debug("args: " + args.toString());

		try {
			Boolean bool = (Boolean) eval.invoke(alg, args);

			l.debug("result: " + bool.toString());

			if (bool == true) {
				dec = MatchDecision.MATCH;
			} else {
				dec = MatchDecision.NO_MATCH;
			}
		} catch (Throwable t) {
			// t.printStackTrace();
			dec = MatchDecision.INDETERMINATE;
		}

		return dec;
	}

	public TargetExpression(Class<? extends IComparisonFunction> functionMatch) {
		this.comparisonFunction = functionMatch;
	}

	public Class<? extends IComparisonFunction> getFunctionMatch() {
		return comparisonFunction;
	}

	public void setFunctionMatch(Class<? extends IComparisonFunction> functionMatch) {
		this.comparisonFunction = functionMatch;
	}

	public RequestAttributeName getStruct_name() {
		return attr_name;
	}

}
