/**
 * Copyright (c) 2013 Concurrency and Mobility Group.
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
package org.cmg.jresp.knowledge;

/**
 * @author Michele Loreti
 *
 */
public interface KnowledgeListener {

	/**
	 * This method is invoked when a new instance of tuple t is inserted in the
	 * observed knowledge repository.
	 * 
	 * @param t
	 *            the new tuple inserted.
	 */
	public void putOfTuple(Tuple t);

	/**
	 * This method is invoked when an instance of tuple t is retrieved from
	 * knowledge repository.
	 * 
	 * @param t
	 *            the retrieved tuple.
	 */
	public void getOfTuple(Tuple t);

}
