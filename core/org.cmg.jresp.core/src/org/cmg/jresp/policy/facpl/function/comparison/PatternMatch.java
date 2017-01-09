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
package org.cmg.jresp.policy.facpl.function.comparison;

import java.util.List;

import org.cmg.jresp.policy.facpl.IComparisonFunction;
import org.cmg.jresp.policy.facpl.function.comparison.evaluator.ComparisonEvaluator;
import org.cmg.jresp.policy.facpl.function.comparison.evaluator.ComparisonEvaluatorFactory;

/**
 * @author Andrea Margheri
 * 
 */
public class PatternMatch implements IComparisonFunction {

	@Override
	public Boolean evaluateFunction(List<Object> args) throws Throwable {

		if (args.size() == 2) {

			Object o1 = args.get(0);
			Object o2 = args.get(1);

			ComparisonEvaluator evaluator = ComparisonEvaluatorFactory.getInstance().getEvaluator(o1);

			return evaluator.isPatterMatching(o1, o2);

		} else {
			throw new Exception("Illegal number of arguments");
		}
	}

}
