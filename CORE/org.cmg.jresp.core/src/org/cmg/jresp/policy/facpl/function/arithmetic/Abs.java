package org.cmg.jresp.policy.facpl.function.arithmetic;

import java.util.List;

import org.cmg.jresp.policy.facpl.IExpressionFunction;
import org.cmg.jresp.policy.facpl.function.arithmetic.evaluator.ArithmeticEvaluator;
import org.cmg.jresp.policy.facpl.function.arithmetic.evaluator.ArithmeticEvaluatorFactory;

public class Abs implements IExpressionFunction {

	@Override
	public Object evaluateFunction(List<Object> args) throws Throwable {

		if (args.size() == 1) {
			Object o1 = args.get(0);

			ArithmeticEvaluator evaluator = ArithmeticEvaluatorFactory.getInstance().getEvaluator(o1);
			return evaluator.abs(o1);

		} else {
			throw new Exception("Illegal number of arguments");
		}
	}

}
