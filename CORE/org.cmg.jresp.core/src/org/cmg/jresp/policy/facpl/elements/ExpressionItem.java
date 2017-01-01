package org.cmg.jresp.policy.facpl.elements;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.RequestAttributeName;
import org.cmg.jresp.policy.facpl.IExpressionFunction;
import org.cmg.jresp.policy.facpl.elements.util.FormalObligationVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrea Margheri
 *
 */

public class ExpressionItem {

	private Class<? extends IExpressionFunction> functionCond;

	// struct_name, literal, ConditionExpressionArgument, list<Object> (bag),...
	private LinkedList<Object> arguments;

	public ExpressionItem() {
	}

	public ExpressionItem(Class<? extends IExpressionFunction> function, Object... args) {
		this.functionCond = function;
		// Arguments=StructName, Value o ConditionItem
		this.arguments = new LinkedList<Object>();
		for (Object object : args) {
			this.arguments.add(object);
		}
	}

	/**
	 * Return the evaluation of the function on the argument's list
	 * 
	 * @param context
	 *            to get value from request
	 * @return
	 */
	public Object getValue(AuthorizationRequest request) throws Throwable {

		Logger l = LoggerFactory.getLogger(ExpressionItem.class);
		l.debug("Evaluate Expression Arguments");

		// returned value
		Object value = null;

		LinkedList<Object> values = new LinkedList<Object>();
		for (Object obj : this.arguments) {
			// structName
			if (obj instanceof RequestAttributeName) {
				l.debug("arg = attr name");
				l.debug(request.getAttributeValue((RequestAttributeName) obj).toString());

				values.add(request.getAttributeValue((RequestAttributeName) obj));
			}
			// condition item
			if (obj instanceof ExpressionItem) {
				l.debug("arg = ExprItem");
				l.debug(((ExpressionItem) obj).getValue(request).toString());

				values.add(((ExpressionItem) obj).getValue(request));
			}

			if (obj instanceof FormalObligationVariable) {
				l.debug("arg = FormalObligationVariable");
				l.debug(((FormalObligationVariable<?>) obj).getValue().toString());

				values.add(((FormalObligationVariable<?>) obj).getValue());
			}
			// letterali
			if (obj instanceof Integer || obj instanceof String || obj instanceof Boolean || obj instanceof Double) {
				l.debug("arg = Literal");
				l.debug(obj.toString());

				values.add(obj);
			}
		}

		l.debug(values.toString());

		Class<?> params[] = new Class[1];
		params[0] = List.class;
		// params[1] = List.class;

		Method eval;
		eval = functionCond.getDeclaredMethod("evaluateFunction", params);

		Object alg = functionCond.newInstance();
		value = eval.invoke(alg, values);

		return value;
	}

	/**
	 * Recursively search for formal obligation variables and return them 
	 * @return
	 */
	public ArrayList<FormalObligationVariable<?>> getFormalVariables() {
		ArrayList<FormalObligationVariable<?>> ar = new ArrayList<FormalObligationVariable<?>>();
		for (Object ob : arguments) {
			if (ob instanceof FormalObligationVariable) {
				ar.add((FormalObligationVariable<?>) ob);
				return ar;
			}
			if (ob instanceof ExpressionItem){
				ar.addAll(((ExpressionItem) ob).getFormalVariables());
			}
		}
		return ar;
	}

	/**
	 * Recursively replace FormalObligationVariable with their values 
	 * 
	 * @param values The substitution of all the variables occurring in the obligation
	 */
	public void updateVariable(HashMap<String, FormalObligationVariable<?>> values) {

		for (Object ob : arguments) {
			if (ob instanceof FormalObligationVariable) {
				((FormalObligationVariable) ob)
						.setValue(values.get(((FormalObligationVariable) ob).getVarName()).getValue());
			}
			if (ob instanceof ExpressionItem){
				((ExpressionItem) ob).updateVariable(values);
			}
		}
	}

}
