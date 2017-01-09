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

/**
 * @author Andrea Margheri
 * @author Michele Loreti
 *
 */
public interface ComparisonEvaluator {

	/*
	 * Boolean Comparison
	 */

	public boolean areEquals(Object o1, Object o2) throws Throwable;

	public boolean areNotEquals(Object o1, Object o2) throws Throwable;

	public boolean isLessThan(Object o1, Object o2) throws Throwable;

	public boolean isLessThanOrEqual(Object o1, Object o2) throws Throwable;

	public boolean isGreaterThan(Object o1, Object o2) throws Throwable;

	public boolean isGreateThanOrEqual(Object o1, Object o2) throws Throwable;

	/*
	 * Template and/or Tuple matching
	 */

	public boolean isPatterMatching(Object o1, Object o2) throws Throwable;

	/*
	 * Logic operators
	 */

	public boolean and(Object... objs) throws Throwable;

	public boolean or(Object... objs) throws Throwable;

	public boolean not(Object o1) throws Throwable;

	/*
	 * Set operators
	 */

	public boolean isSubsetOf(Object o1, Object o2) throws Throwable;

	public boolean isAtLestOneMemberOf(Object o1, Object o2) throws Throwable;

}
