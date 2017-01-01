package org.cmg.jresp.policy.facpl.function.arithmetic;

import java.util.List;

import org.cmg.jresp.policy.facpl.IExpressionFunction;
import org.cmg.jresp.policy.facpl.function.arithmetic.evaluator.ArithmeticEvaluator;
import org.cmg.jresp.policy.facpl.function.arithmetic.evaluator.ArithmeticEvaluatorFactory;

public class Divide implements IExpressionFunction {

	@Override
	public Object evaluateFunction(List<Object> args) throws Throwable {

		if (args.size() == 2) {
			Object o1 = args.get(0);
			Object o2 = args.get(1);

			ArithmeticEvaluator evaluator = ArithmeticEvaluatorFactory.getInstance().getEvaluator(o1);
			return evaluator.divide(o1, o2);

		} else {
			throw new Exception("Illegal number of arguments");
		}

	}

}
