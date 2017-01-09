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
package org.cmg.jresp.policy.facpl.function.arithmetic.evaluator;

/**
 * @author Andrea Margheri
 *
 */
public interface ArithmeticEvaluator {

	public Number add(Object o1, Object o2) throws Throwable;

	public Number divide(Object o1, Object o2) throws Throwable;

	public Number multiply(Object o1, Object o2) throws Throwable;

	public Number subtract(Object o1, Object o2) throws Throwable;

	public Number mod(Object o1, Object o2) throws Throwable;

	/*
	 * Unary Operators
	 */

	public Number abs(Object o1) throws Throwable;

}
