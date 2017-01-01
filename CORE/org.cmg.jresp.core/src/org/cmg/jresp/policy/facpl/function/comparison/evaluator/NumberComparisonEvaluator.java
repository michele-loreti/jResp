package org.cmg.jresp.policy.facpl.function.comparison.evaluator;

import org.cmg.jresp.exceptions.UnsupportedTypeException;
import org.cmg.jresp.policy.facpl.Bag;

public class NumberComparisonEvaluator implements ComparisonEvaluator {

	private static NumberComparisonEvaluator instance;

	private NumberComparisonEvaluator() {

	}

	public static NumberComparisonEvaluator getInstance() {
		if (instance == null) {
			instance = new NumberComparisonEvaluator();
		}
		return instance;
	}

	@Override
	public boolean areEquals(Object o1, Object o2) throws Throwable {
		if (o1 instanceof Integer) {
			return ((Integer) o1).equals(o2);
		}
		return ((Double) o1).equals(o2);
	}

	@Override
	public boolean areNotEquals(Object o1, Object o2) throws Throwable {
		if (o1 instanceof Integer) {
			return !((Integer) o1).equals(o2);
		}
		return !((Double) o1).equals(o2);
	}

	@Override
	public boolean isLessThan(Object o1, Object o2) throws Throwable {
		if (o1 instanceof Integer) {
			if (o2 instanceof Integer) {
				return ((Integer) o1) < ((Integer) o2);
			} else {
				return ((Integer) o1) < ((Double) o2);
			}
		}
		if (o1 instanceof Double) {
			if (o2 instanceof Integer) {
				return ((Double) o1) < ((Integer) o2);
			} else {
				return ((Double) o1) < ((Double) o2);
			}
		}
		throw new UnsupportedTypeException(o1.getClass().getName(), "Less-Than");
	}

	@Override
	public boolean isLessThanOrEqual(Object o1, Object o2) throws Throwable {
		if (o1 instanceof Integer) {
			if (o2 instanceof Integer) {
				return ((Integer) o1) <= ((Integer) o2);
			} else {
				return ((Integer) o1) <= ((Double) o2);
			}
		}
		if (o1 instanceof Double) {
			if (o2 instanceof Integer) {
				return ((Double) o1) <= ((Integer) o2);
			} else {
				return ((Double) o1) <= ((Double) o2);
			}
		}
		throw new UnsupportedTypeException(o1.getClass().getName(), "Less-Than-Or-Equal");
	}

	@Override
	public boolean isGreaterThan(Object o1, Object o2) throws Throwable {
		if (o1 instanceof Integer) {
			if (o2 instanceof Integer) {
				return ((Integer) o1) > ((Integer) o2);
			} else {
				return ((Integer) o1) > ((Double) o2);
			}
		}
		if (o1 instanceof Double) {
			if (o2 instanceof Integer) {
				return ((Double) o1) > ((Integer) o2);
			} else {
				return ((Double) o1) > ((Double) o2);
			}
		}
		throw new UnsupportedTypeException(o1.getClass().getName(), "Greater-Than");
	}

	@Override
	public boolean isGreateThanOrEqual(Object o1, Object o2) throws Throwable {
		if (o1 instanceof Integer) {
			if (o2 instanceof Integer) {
				return ((Integer) o1) >= ((Integer) o2);
			} else {
				return ((Integer) o1) >= ((Double) o2);
			}
		}
		if (o1 instanceof Double) {
			if (o2 instanceof Integer) {
				return ((Double) o1) >= ((Integer) o2);
			} else {
				return ((Double) o1) >= ((Double) o2);
			}
		}
		throw new UnsupportedTypeException(o1.getClass().getName(), "Greater-Than-Or-Equal");
	}

	@Override
	public boolean isPatterMatching(Object o1, Object o2) throws Throwable {
		throw new UnsupportedTypeException(o1.getClass().getName(), "Pattern-Matching");
	}

	@Override
	public boolean and(Object... objs) throws Throwable {
		throw new UnsupportedTypeException(objs[0].getClass().getName(), "And");
	}

	@Override
	public boolean or(Object... objs) throws Throwable {
		throw new UnsupportedTypeException(objs[0].getClass().getName(), "Or");
	}

	@Override
	public boolean not(Object o1) throws Throwable {
		throw new UnsupportedTypeException(o1.getClass().getName(), "Not");
	}

	@Override
	public boolean isSubsetOf(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Bag) {
			return ((Bag) o2).contains(o1);
		} else {
			throw new UnsupportedTypeException("Number", "Subset");
		}
	}

	@Override
	public boolean isAtLestOneMemberOf(Object o1, Object o2) throws Throwable {
		if (o2 instanceof Bag) {
			return ((Bag) o2).contains(o1);
		} else {
			throw new UnsupportedTypeException("Number", "Subset");
		}
	}

}
