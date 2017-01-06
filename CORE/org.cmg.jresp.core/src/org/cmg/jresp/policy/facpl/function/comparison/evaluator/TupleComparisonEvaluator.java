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
public class TupleComparisonEvaluator implements ComparisonEvaluator {

	private static TupleComparisonEvaluator instance;

	private TupleComparisonEvaluator() {

	}

	public static TupleComparisonEvaluator getInstance() {
		if (instance == null) {
			instance = new TupleComparisonEvaluator();
		}
		return instance;
	}

	@Override
	public boolean areEquals(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "Equals");
	}

	@Override
	public boolean areNotEquals(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "Not-Equals");
	}

	@Override
	public boolean isLessThan(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "Less-Than");
	}

	@Override
	public boolean isLessThanOrEqual(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "Less-Than-Or-Equal");
	}

	@Override
	public boolean isGreaterThan(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "Greater-Than");
	}

	@Override
	public boolean isGreateThanOrEqual(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "Greater-Than-Or-Equal");
	}

	@Override
	public boolean isPatterMatching(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Template)
			return ((Template) o2).match((Tuple) o1);
		if (o2 instanceof Tuple)
			return ((Tuple) o1).equals(o2);
		throw new UnsupportedTypeException(o2.getClass().getName(), "Pattern-Matching");
	}

	@Override
	public boolean and(Object... objs) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "And");
	}

	@Override
	public boolean or(Object... objs) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "Or");
	}

	@Override
	public boolean not(Object o1) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "Not");
	}

	@Override
	public boolean isSubsetOf(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "Subset");
	}

	@Override
	public boolean isAtLestOneMemberOf(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Tuple", "At-Least-One-Member");
	}
}
