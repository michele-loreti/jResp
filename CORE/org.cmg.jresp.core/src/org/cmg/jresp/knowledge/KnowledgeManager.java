/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This interface identifies a generic <em>knowledge</em> and provides the high
 * level primitives to manage pieces of relevant information coming from
 * different sources.
 * 
 * @author Michele Loreti
 *
 */
public interface KnowledgeManager {

	/**
	 * Adds
	 * <code>element<code> to the knowledge. This method returns <code>true</code>
	 * if the new element has been successfully added to the knowledge and
	 * <code>false</code> otherwise.
	 * 
	 * @param element
	 *            the element to add to the knowledge.
	 * @return returns <code>true</code> if the tuple has been correctly added
	 *         to the knowledge.
	 * 
	 */
	public boolean put(Tuple t);

	/**
	 * Removes from the knowledge a tuple matching the template. This is a
	 * blocking operation. If the knowledge does not contain a matching tuple,
	 * the thread is blocked.
	 * 
	 * @param template
	 *            template used to retrieve tuple
	 * @return a tuple matching template <code>template</code>
	 * 
	 * @throws InterruptedException
	 *             if another thread has interrupted the current thread.
	 */
	public Tuple get(Template template) throws InterruptedException;

	/**
	 * Removes from the knowledge a tuple matching the template. Differently
	 * from <code>get</code>, this is a no-blocking operation. If the knowledge
	 * does not contain a matching tuple, value <code>null</code> is returned.
	 * 
	 * @param template
	 *            template used to retrieve tuple
	 * @return a tuple matching template <code>template</code>
	 * 
	 */
	public Tuple getp(Template template);

	/**
	 * Removes from the knowledge all the tuple matching the template.
	 * 
	 * @param template
	 *            template used to retrieve tuple
	 * @return the list of tuples matching template <code>template</code>
	 * 
	 */
	public LinkedList<Tuple> getAll(Template template);

	/**
	 * Checks if the knowledge contains (or can infer) a tuple matching the
	 * template. This is a blocking operation. The matching tuple is not removed
	 * from the knowledge. If the knowledge does not contain a matching tuple,
	 * the thread is blocked.
	 * 
	 * @param template
	 *            template used to retrieve tuple
	 * @return a tuple matching template <code>template</code>
	 * 
	 * @throws InterruptedException
	 *             if another thread has interrupted the current thread.
	 */
	public Tuple query(Template template) throws InterruptedException;

	/**
	 * Checks if the knowledge contains (or can infer) a tuple matching the
	 * template. Differently from <code>query</code>, this is a no-blocking
	 * operation. If the knowledge does not contain a matching tuple, value
	 * <code>null</code> is returned.
	 * 
	 * @param template
	 *            template used to retrieve tuple
	 * @return a tuple matching template <code>template</code>
	 * 
	 */
	public Tuple queryp(Template template);

	/**
	 * Returns the list of tuples matching <code>template</code> that can be
	 * inferred or that are contained in the knowledge.
	 * 
	 * @param template
	 *            template used to retrieve tuple
	 * @return the list of tuples matching template <code>template</code>
	 * 
	 */
	public LinkedList<Tuple> queryAll(Template template);

	/**
	 * Returns an {@link Iterator} with all the items in the knowledge.
	 * 
	 * @return an {@link Iterator} with all the items in the knowledge.
	 */
	public Tuple[] getKnowledgeItems();

}
