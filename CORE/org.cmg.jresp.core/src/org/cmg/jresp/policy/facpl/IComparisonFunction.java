package org.cmg.jresp.policy.facpl;

import java.util.List;

/**
 * All type of function for conditional eval or obligation expression
 * 
 * @author Andrea Margheri
 *
 */
public interface IComparisonFunction {

	Boolean evaluateFunction(List<Object> args) throws Throwable;

}
