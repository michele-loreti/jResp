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

import org.cmg.jresp.exceptions.UnsupportedTypeException;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;

/**
 * @author Andrea Margheri
 * 
 */
public class TemplateComparisonEvaluator implements ComparisonEvaluator {

	private static TemplateComparisonEvaluator instance;

	private TemplateComparisonEvaluator() {

	}

	public static TemplateComparisonEvaluator getInstance() {
		if (instance == null) {
			instance = new TemplateComparisonEvaluator();
		}
		return instance;
	}

	@Override
	public boolean areEquals(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Template", "Equals");
	}

	@Override
	public boolean areNotEquals(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Template", "Not-Equals");
	}

	@Override
	public boolean isLessThan(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Template", "Less-Than");
	}

	@Override
	public boolean isLessThanOrEqual(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Template", "Less-Than-Or-Equal");
	}

	@Override
	public boolean isGreaterThan(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Template", "Greater-Than");
	}

	@Override
	public boolean isGreateThanOrEqual(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Template", "Greater-Than-Or-Equal");
	}

	@Override
	public boolean isPatterMatching(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Template)
			return ((Template) o1).implies((Template) o2);
		if (o2 instanceof Tuple)
			return ((Template) o1).match((Tuple) o2);
		// return false;
		// throw new UnsupportedTypeException(o2.getClass().getName(),
		// "Pattern-Matching");
		throw new Exception("Uncomparable types");
	}

	@Override
	public boolean and(Object... objs) throws Throwable {
		throw new UnsupportedTypeException("Template", "And");
	}

	@Override
	public boolean or(Object... objs) throws Throwable {
		throw new UnsupportedTypeException("Template", "Or");
	}

	@Override
	public boolean not(Object o1) throws Throwable {
		throw new UnsupportedTypeException("Template", "Not");
	}

	@Override
	public boolean isSubsetOf(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Template", "Subset");
	}

	@Override
	public boolean isAtLestOneMemberOf(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Template", "At-Least-One-Member");
	}

}
