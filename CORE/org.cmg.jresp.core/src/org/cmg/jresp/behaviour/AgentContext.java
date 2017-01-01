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
package org.cmg.jresp.behaviour;

import java.io.IOException;
import java.util.LinkedList;

import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Target;

/**
 * Identifies a generic context where an agent is executed. A context provides
 * the mean used by agents to perform SCEL actions.
 * 
 * @author Michele Loreti
 *
 */
public interface AgentContext {

	/**
	 * Agent <code>a</code> puts tuple <code>t</code> at <code>l</code>.
	 * 
	 * @param a
	 *            agent executing the action
	 * @param t
	 *            knowledge element to add to knowledge located at l
	 * @param l
	 *            target locality
	 * @return true if the knowledge element has been successfully added at l
	 * @throws InterruptedException
	 *             if any thread interrupted the current thread before or while
	 *             the current thread was waiting for a notification.
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public boolean put(Agent a, Tuple t, Target l) throws InterruptedException, IOException;

	/**
	 * Agent <code>a</code> removes a tuple matching template <code>t</code>
	 * from tuple space locate at l
	 * 
	 * @param a
	 *            agent executing the action
	 * @param t
	 *            template used to select the tuple to remove
	 * @param l
	 *            target locality
	 * @return removed tuple
	 * @throws InterruptedException
	 *             if any thread interrupted the current thread before or while
	 *             the current thread was waiting for a notification.
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public Tuple get(Agent a, Template t, Target l) throws InterruptedException, IOException;;

	/**
	 * Agent <code>a</code> queries a tuple matching template <code>t</code>
	 * from tuple space locate at l
	 * 
	 * @param a
	 *            agent executing the action
	 * @param t
	 *            template used to selecte the tuple to query
	 * @param l
	 *            target locality
	 * @return queried tuple
	 * @throws InterruptedException
	 *             if any thread interrupted the current thread before or while
	 *             the current thread was waiting for a notification.
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public Tuple query(Agent a, Template t, Target l) throws InterruptedException, IOException;;

	/**
	 * Agent a executes agent b.
	 * 
	 * @param a
	 *            agent executing the action
	 * @param b
	 *            activated action
	 * @throws InterruptedException
	 *             if any thread interrupted the current thread before or while
	 *             the current thread was waiting for a notification.
	 */
	public void exec(Agent a, Agent b) throws InterruptedException;

	/**
	 * This method is used to generate a new fresh identifier.
	 * 
	 * @return a new fresh identifier.
	 * @throws InterruptedException
	 */
	public String fresh(Agent a) throws InterruptedException;

	/**
	 * This method is invoked when agent a terminates its computation.
	 * 
	 * @param agent
	 *            the agent that has terminated the computating.
	 */
	public void done(Agent agent);

	/**
	 * Suspends agent execution for time t
	 * 
	 * @param t
	 * @throws InterruptedException
	 */
	public void suspend(long t) throws InterruptedException;

	/**
	 * Returns the list of addresses identifying the node where the context is
	 * operating.
	 * 
	 * @return the list of addresses identifying the node where the context is
	 *         operating.
	 */

	public LinkedList<PointToPoint> getLocalAddresses();

	/**
	 * Read the value of a Node Attribute
	 * 
	 * @param name
	 *            Attribute Name
	 * @return
	 * @throws InterruptedException
	 */
	public Object readAttribute(String name) throws InterruptedException;

	public Tuple getp(Agent agent, Template t);

	public Tuple queryp(Agent agent, Template t);

	public LinkedList<Tuple> getAll(Agent agent, Template t);

	public LinkedList<Tuple> queryAll(Agent agent, Template t);

	/**
	 * Set the value of a Node Attribute
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public boolean updateAttribute(String name, Object value) throws InterruptedException;

	// /**
	// * Read the value of a Node Attribute and assign it to t
	// * @param t
	// * @param name
	// * @return
	// */
	// public Tuple read(Template t, String name);

}
