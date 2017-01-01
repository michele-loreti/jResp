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
package org.cmg.jresp.policy.facpl.function.comparison.evaluator;

import java.util.Date;
import java.util.HashMap;

import org.cmg.jresp.exceptions.UnsupportedTypeException;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.policy.ActionThisID;
import org.cmg.jresp.policy.facpl.Bag;

/**
 * @author Andrea Margheri
 * 
 */
public class ComparisonEvaluatorFactory {

	private static ComparisonEvaluatorFactory instance;

	private HashMap<Class<?>, ComparisonEvaluator> table;

	private ComparisonEvaluatorFactory() {
		this.table = new HashMap<Class<?>, ComparisonEvaluator>();

		// initialisation
		this.table.put(Integer.class, NumberComparisonEvaluator.getInstance());
		this.table.put(Double.class, NumberComparisonEvaluator.getInstance());
		this.table.put(String.class, StringComparisonEvaluator.getInstance());
		this.table.put(Boolean.class, BooleanComparisonEvaluator.getInstance());
		this.table.put(Date.class, DateComparisonEvaluator.getInstance());
		this.table.put(ActionThisID.class, ActionIDComparisonEvaluator.getInstance());
		this.table.put(Template.class, TemplateComparisonEvaluator.getInstance());
		this.table.put(Tuple.class, TupleComparisonEvaluator.getInstance());
		this.table.put(Bag.class, BagComparisonEvaluator.getInstance());
	}

	public static synchronized ComparisonEvaluatorFactory getInstance() {
		if (instance == null) {
			instance = new ComparisonEvaluatorFactory();
		}
		return instance;
	}

	public ComparisonEvaluator getEvaluator(Object o) throws Exception {

		try {
			ComparisonEvaluator evaluator = table.get(o.getClass());
			if (evaluator == null) {
				// evaluator = new DefaultComparisonEvaluator();
				System.err.println("No comparison fucntion available for data type " + o.getClass().getName());
				throw new Exception("No comparison fucntion available for data type " + o.getClass().getName());
			}
			return evaluator;
		} catch (UnsupportedTypeException e) {
			System.err.println(e.getMessage());
			throw e;
		}
	}

}
