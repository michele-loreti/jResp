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
import org.cmg.jresp.policy.facpl.Bag;

/**
 * @author Andrea Margheri
 *
 */
public class BooleanComparisonEvaluator implements ComparisonEvaluator {

	private static BooleanComparisonEvaluator instance;

	private BooleanComparisonEvaluator() {

	}

	public static BooleanComparisonEvaluator getInstance() {
		if (instance == null) {
			instance = new BooleanComparisonEvaluator();
		}
		return instance;
	}

	@Override
	public boolean areEquals(Object o1, Object o2) throws Throwable {
		return ((Boolean) o1).equals(o2);
	}

	@Override
	public boolean areNotEquals(Object o1, Object o2) throws Throwable {
		return !((Boolean) o1).equals(o2);
	}

	@Override
	public boolean isLessThan(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Boolean", "Less-Than");
	}

	@Override
	public boolean isLessThanOrEqual(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Boolean", "Less-Than-Or-Equal");
	}

	@Override
	public boolean isGreaterThan(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Boolean", "Greater-Than");
	}

	@Override
	public boolean isGreateThanOrEqual(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Boolean", "Greater-Than-Or-Equal");
	}

	@Override
	public boolean isPatterMatching(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Boolean", "Pattern-Matching");
	}

	@Override
	public boolean and(Object... objs) throws Throwable {
		Boolean result = true;
		for (Object object : objs) {
			if (object instanceof Boolean) {
				result &= ((Boolean) object);
			} else {
				throw new UnsupportedTypeException();
			}
		}
		return result;
	}

	@Override
	public boolean or(Object... objs) throws Throwable {
		Boolean result = false;
		for (Object object : objs) {
			if (object instanceof Boolean) {
				result |= ((Boolean) object);
			} else {
				throw new UnsupportedTypeException();
			}
		}
		return result;
	}

	@Override
	public boolean not(Object o1) throws Throwable {
		return !((Boolean) o1);
	}

	@Override
	public boolean isSubsetOf(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Bag) {
			return ((Bag) o2).contains(o1);
		} else {
			throw new UnsupportedTypeException("Boolean", "Subset");
		}
	}

	@Override
	public boolean isAtLestOneMemberOf(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Bag) {
			return ((Bag) o2).contains(o1);
		} else {
			throw new UnsupportedTypeException("Boolean", "AtLeastOneMemberOf");
		}
	}

}
