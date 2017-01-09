package org.cmg.jresp.policy.facpl;

import java.util.List;

/**
 * All type of function for conditional eval or obligation expression
 * 
 * @author Andrea
 *
 */
public interface IExpressionFunction {

	Object evaluateFunction(List<Object> args) throws Throwable;

}
