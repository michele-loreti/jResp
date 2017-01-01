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

import org.cmg.jresp.exceptions.UnsupportedTypeException;
import org.cmg.jresp.policy.facpl.Bag;

/**
 * @author Andrea Margheri
 *
 */
public class DateComparisonEvaluator implements ComparisonEvaluator {

	private static DateComparisonEvaluator instance;

	private DateComparisonEvaluator() {

	}

	public static DateComparisonEvaluator getInstance() {
		if (instance == null) {
			instance = new DateComparisonEvaluator();
		}
		return instance;
	}

	@Override
	public boolean areEquals(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Date) {
			return ((Date) o1).equals((Date) o2);
		} else if (o2 instanceof String) {
			return ((Date) o1).equals(Util.parseDate((String) o2));
		}
		throw new UnsupportedTypeException(o2.getClass().getName(), "Equal");
	}

	@Override
	public boolean areNotEquals(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Date) {
			return !(((Date) o1).equals((Date) o2));
		} else if (o2 instanceof String) {
			return !(((Date) o1).equals(Util.parseDate((String) o2)));
		}
		throw new UnsupportedTypeException(o2.getClass().getName(), "Equal");
	}

	@Override
	public boolean isLessThan(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Date) {
			return ((Date) o1).before((Date) o2);
		} else if (o2 instanceof String) {
			return ((Date) o1).before(Util.parseDate((String) o2));
		}
		throw new UnsupportedTypeException(o2.getClass().getName(), "Less-Than");
	}

	@Override
	public boolean isLessThanOrEqual(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Date) {
			return ((Date) o1).before((Date) o2) || ((Date) o1).equals((Date) o2);
		} else if (o2 instanceof String) {
			return ((Date) o1).before(Util.parseDate((String) o2)) || ((Date) o1).equals(Util.parseDate((String) o2));
		}
		throw new UnsupportedTypeException(o2.getClass().getName(), "Less-Than-Or-Equal");
	}

	@Override
	public boolean isGreaterThan(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Date) {
			return ((Date) o1).after((Date) o2);
		} else if (o2 instanceof String) {
			return ((Date) o1).after(Util.parseDate((String) o2));
		}
		throw new UnsupportedTypeException(o2.getClass().getName(), "Greater-Than");
	}

	@Override
	public boolean isGreateThanOrEqual(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Date) {
			return ((Date) o1).after((Date) o2) || ((Date) o1).equals((Date) o2);
		} else if (o2 instanceof String) {
			return ((Date) o1).after(Util.parseDate((String) o2)) || ((Date) o1).equals(Util.parseDate((String) o2));
		}
		throw new UnsupportedTypeException(o2.getClass().getName(), "Greater-Than-Or-Equal");
	}

	@Override
	public boolean isPatterMatching(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException("Date", "Pattern-Matching");
	}

	@Override
	public boolean and(Object... objs) throws Throwable {
		throw new UnsupportedTypeException("Date", "And");
	}

	@Override
	public boolean or(Object... objs) throws Throwable {
		throw new UnsupportedTypeException("Date", "Or");
	}

	@Override
	public boolean not(Object o1) throws Throwable {
		throw new UnsupportedTypeException("Date", "Not");
	}

	@Override
	public boolean isSubsetOf(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Bag) {
			return ((Bag) o2).contains((Date) o1);
		} else {
			throw new UnsupportedTypeException("Date", "Subset");
		}
	}

	@Override
	public boolean isAtLestOneMemberOf(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Bag) {
			return ((Bag) o2).contains((Date) o1);
		} else {
			throw new UnsupportedTypeException("Date", "AtLeastOneMemberOf");
		}
	}

}
