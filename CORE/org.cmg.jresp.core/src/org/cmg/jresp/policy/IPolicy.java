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
package org.cmg.jresp.policy;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.INode;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Target;

/**
 * This interface identifies the set of methods that each node policy has to
 * provide.
 * 
 * @author Michele Loreti
 *
 */
public interface IPolicy {

	/**
	 * This method is invoked when a remote node asks for adding a tuple in the
	 * local knowledge repository.
	 * 
	 * @param from
	 *            remote node
	 * @param session
	 *            operation session
	 * @param tuple
	 *            tuple to add
	 * @throws InterruptedException
	 *             is thrown when the current thread is interrupted while it is
	 *             waiting for action execution
	 * @throws IOException
	 *             is thrown when a communication error occurs
	 */
	boolean acceptPut(PointToPoint from, int session, Tuple tuple) throws InterruptedException, IOException;;

	/**
	 * This method is invoked when a remote node asks for getting a tuple in the
	 * local knowledge repository.
	 * 
	 * @param from
	 *            remote node
	 * @param session
	 *            operation session
	 * @param template
	 *            template indicating the tuple to get
	 * @throws InterruptedException
	 *             is thrown when the current thread is interrupted while it is
	 *             waiting for action execution
	 * @throws IOException
	 *             is thrown when a communication error occurs
	 */
	Tuple acceptGet(PointToPoint source, int session, Template template) throws InterruptedException, IOException;

	/**
	 * This method is invoked when a remote node asks for quering a tuple in the
	 * local knowledge repository.
	 * 
	 * @param from
	 *            remote node
	 * @param session
	 *            operation session
	 * @param template
	 *            template indicating the tuple to query
	 * @throws InterruptedException
	 *             is thrown when the current thread is interrupted while it is
	 *             waiting for action execution
	 * @throws IOException
	 *             is thrown when a communication error occurs
	 */
	Tuple acceptQuery(PointToPoint source, int session, Template template) throws InterruptedException, IOException;

	/**
	 * This method is invoked when an agent asks for executing a put.
	 * 
	 * @param a
	 *            agent performing the action
	 * @param t
	 *            tuple to add
	 * @param l
	 *            a reference to the node where the action takes effect
	 * @return true if the operation has been successfully executed.
	 * @throws InterruptedException
	 *             is thrown when the current thread is interrupted while it is
	 *             waiting for action execution
	 * @throws IOException
	 *             is thrown when a communication error occurs
	 */
	public boolean put(Agent a, Tuple t, Target l) throws InterruptedException, IOException;

	/**
	 * This method is invoked when an agent asks for executing a get.
	 * 
	 * @param a
	 *            agent performing the action
	 * @param t
	 *            a template
	 * @param l
	 *            a reference to the node where the action takes effect
	 * @return a tuple matcing template t.
	 * @throws InterruptedException
	 *             is thrown when the current thread is interrupted while it is
	 *             waiting for action execution
	 * @throws IOException
	 *             is thrown when a communication error occurs
	 */
	public Tuple get(Agent a, Template t, Target l) throws InterruptedException, IOException;;

	/**
	 * This method is invoked when an agent asks for executing a query.
	 * 
	 * @param a
	 *            agent performing the action
	 * @param t
	 *            a template
	 * @param l
	 *            a reference to the node where the action takes effect
	 * @return a tuple matcing template t.
	 * @throws InterruptedException
	 *             is thrown when the current thread is interrupted while it is
	 *             waiting for action execution
	 * @throws IOException
	 *             is thrown when a communication error occurs
	 */
	public Tuple query(Agent a, Template t, Target l) throws InterruptedException, IOException;;

	/**
	 * This method is invoked when an agent asks for start the execution of a
	 * new agent.
	 * 
	 * @param a
	 *            agent performing the action
	 * @param b
	 *            the agent to execute
	 * @throws InterruptedException
	 *             is thrown when the current thread is interrupted while it is
	 *             waiting for action execution
	 */
	public void exec(Agent a, Agent b) throws InterruptedException;

	/**
	 * This method is invoked when a group request for a put action is received.
	 * 
	 * @param from
	 *            remote node
	 * @param session
	 *            operation session
	 * @param tuple
	 *            tuple to add
	 * @throws InterruptedException
	 *             is thrown when the current thread is interrupted while it is
	 *             waiting for action execution
	 * @throws IOException
	 *             is thrown when a communication error occurs
	 */
	void acceptGroupPut(PointToPoint from, int session, GroupPredicate groupPredicate, Tuple tuple)
			throws IOException, InterruptedException;

	/**
	 * This method is invoked when a group request for a get action is received.
	 * 
	 * @param from
	 *            remote node
	 * @param session
	 *            operation session
	 * @param template
	 *            a template
	 * @return
	 * @throws InterruptedException
	 *             is thrown when the current thread is interrupted while it is
	 *             waiting for action execution
	 * @throws IOException
	 *             is thrown when a communication error occurs
	 */
	Tuple acceptGroupGet(PointToPoint from, int session, GroupPredicate groupPredicate, Template template)
			throws IOException, InterruptedException;

	/**
	 * This method is invoked when a group request for a query action is
	 * received.
	 * 
	 * @param from
	 *            remote node
	 * @param session
	 *            operation session
	 * @param template
	 *            a template
	 * @throws InterruptedException
	 *             is thrown when the current thread is interrupted while it is
	 *             waiting for action execution
	 * @throws IOException
	 *             is thrown when a communication error occurs
	 */
	Tuple acceptGroupQuery(PointToPoint from, int session, GroupPredicate groupPredicate, Template template)
			throws IOException, InterruptedException;

	/**
	 * This method is invoked when a fresh action is performed.
	 * 
	 * @return a new fresh identifier.
	 * @throws InterruptedException
	 */
	String fresh(Agent a) throws InterruptedException;

	/**
	 * Set the reference to the a node in the policy.
	 * 
	 * @param node
	 *            the reference to the SCEL node.
	 */
	void setNode(INode node);

	/**
	 * This method id invoked when an action willing to read an attribute
	 * interface is performed
	 * 
	 * @param name
	 *            attribute name
	 * @return Attribute value (null if the attribute is undefined)
	 */
	Object readAttribute(String name) throws InterruptedException;

	/**
	 * This method id invoked when an action willing to update an attribute
	 * interface is performed
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            value of the new attribute
	 * @return
	 */
	boolean updateAttribute(String name, Object value) throws InterruptedException;

}
