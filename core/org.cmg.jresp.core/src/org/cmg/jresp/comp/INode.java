package org.cmg.jresp.comp;

import java.io.IOException;
import java.util.HashMap;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.behaviour.ContextState;
import org.cmg.jresp.knowledge.AbstractActuator;
import org.cmg.jresp.knowledge.AbstractSensor;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Target;

public interface INode {

	/**
	 * Adds an actuator to the current node.
	 * 
	 * @param actuator
	 *            the actuator to add to the node.
	 */
	public abstract void addActuator(AbstractActuator actuator);

	/**
	 * Add and Executes agent <code>a</code>
	 * 
	 * @param a
	 *            agent to execute
	 */
	public abstract void addAgent(Agent a);

	/**
	 * Adds an attribute collector to the node
	 * 
	 * @param ac
	 *            the attribute collector to add to the node
	 */
	public abstract void addAttributeCollector(AttributeCollector ac);

	/**
	 * Adds a sensor to the current node
	 * 
	 * @param sensor
	 *            the sensor to add
	 */
	public abstract void addSensor(AbstractSensor sensor);

	/**
	 * Gets a tuple matching the parameter from the local knowledge repository.
	 * This action is blocking if a matching is not found.
	 * 
	 * @param template
	 *            get template
	 * @return a tuple matching the parameter
	 * @throws InterruptedException
	 *             when another thread interrupt current thread computation
	 *             while action is under execution.
	 */
	public abstract Tuple get(Template template) throws InterruptedException;

	/**
	 * Gets a tuple matching the parameter from the knowledge repository located
	 * at <code>l</code>. This action is blocking if a matching is not found.
	 * 
	 * @param template
	 *            get template
	 * @return a tuple matching the parameter
	 * @throws InterruptedException
	 *             when another thread interrupt current thread computation
	 *             while action is under execution.
	 */
	public abstract Tuple get(Template t, Target l) throws InterruptedException, IOException;

	/**
	 * Returns an array containing node's actuators.
	 * 
	 * @return an array containing node's actuators.
	 */
	public abstract AbstractActuator[] getActuators();

	/**
	 * Returns attribute named <code>name</code>
	 * 
	 * @param name
	 *            attribute name
	 * @return attribute named <code>name</code>
	 */
	public abstract Attribute getAttribute(String name);

	/**
	 * Returns an array of attributes whose names are in array
	 * <code>attributes</code>
	 *
	 * @param attributes
	 *            attributes' names
	 * @return an array of attributes.
	 */
	public abstract Attribute[] getAttributes(String[] attributes);

	/**
	 * Set the value of an Attribute
	 * 
	 * @param name
	 * @return
	 */
	public abstract boolean setAttribute(String name, Object value);

	/**
	 * Add an Attribute to the Node Interface
	 * 
	 * @param a
	 */
	public abstract void addAttribute(Attribute a);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.scel.topology.MessageDispatcher#getName()
	 */
	public abstract String getName();

	/**
	 * Returns the array of sensors attached to the node.
	 * 
	 * @return the array of sensors attached to the node.
	 */
	public abstract AbstractSensor[] getSensors();

	/**
	 * Returns node status.
	 * 
	 * @return node status.
	 */
	public abstract ContextState getState();

	/**
	 * Checks if the node is running.
	 * 
	 * @return true if the node is running.
	 */
	public abstract boolean isRunning();

	/**
	 * Adds tuple <code>tuple</code> to the local knowledge repository.
	 * 
	 * @param tuple
	 *            the tuple to add
	 */
	public abstract void put(Tuple tuple);

	/**
	 * Adds tuple <code>tuple</code> to the knowledge repository located at
	 * <code>l</code>
	 * 
	 * @param t
	 *            tuple to add
	 * @param l
	 *            target locality
	 * @return true if the action has been correctrly executed.
	 * @throws InterruptedException
	 *             when another thread interrupts current thread computation
	 *             while action is under execution.
	 * @throws IOException
	 *             when an I/O error occurs
	 */
	public abstract boolean put(Tuple t, Target l) throws InterruptedException, IOException;

	/**
	 * Queries a tuple matching template <code>template</code> from the local
	 * knowledge repository. This is a blocking: the thread invoking this method
	 * is blocked until a matching tuple is found.
	 * 
	 * @param template
	 *            action template
	 * @return a matching tuple
	 * @throws InterruptedException
	 *             when another thread interrupts current thread computation
	 *             while action is under execution.
	 */
	public abstract Tuple query(Template template) throws InterruptedException;

	/**
	 * Queries a tuple matching template <code>template</code> from the
	 * knowledge repository located at <code>l</code>. This is a blocking: the
	 * thread invoking this method is blocked until a matching tuple is found.
	 * 
	 * @param template
	 *            action template
	 * @param l
	 *            target locality
	 * @return a matching tuple
	 * @throws InterruptedException
	 *             when another thread interrupts current thread computation
	 *             while action is under execution.
	 */
	public abstract Tuple query(Template t, Target l) throws InterruptedException, IOException;

	/**
	 * Returns the node interface. This is rendered as an hash-table associating
	 * to each name the corresponding attribute.
	 * 
	 * @return an hash map with the node interface.
	 */
	public abstract HashMap<String, Attribute> getInterface();

	/**
	 * 
	 * @param to
	 * @param session
	 * @param attributes
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract void sendAttibutes(PointToPoint to, int session, String[] attributes)
			throws IOException, InterruptedException;

	/**
	 * Request the attribute interface of a Node and return them
	 * 
	 * @param l
	 * @param session
	 * @return The retrieved attribtues
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract Attribute[] sendAttributeRequest(PointToPoint l) throws IOException, InterruptedException;

	public abstract void sendFail(PointToPoint to, int session, String message)
			throws IOException, InterruptedException;

	public abstract void sendGroupPutReply(PointToPoint source, int session, Attribute[] attributes2);

	public abstract void sendTuple(PointToPoint to, int session, Tuple tuple) throws IOException, InterruptedException;

	public abstract void put(PointToPoint from, int session, Tuple tuple) throws IOException, InterruptedException;

	public abstract void gPut(PointToPoint from, int session, GroupPredicate groupPredicate, Tuple tuple)
			throws IOException, InterruptedException;

	public abstract Tuple gGet(PointToPoint from, int session, GroupPredicate groupPredicate, Template template);

	public abstract Tuple gQuery(PointToPoint from, int session, GroupPredicate groupPredicate, Template template);

	public abstract String fresh();

	public abstract Tuple queryp(Template template);

	public abstract Tuple getp(Template template);

	/**
	 * Agent action updating the value of a node attribute
	 * 
	 * @param name
	 * @param value
	 */
	public abstract void updateAttribute(String name, Object value);

	/**
	 * Agent action reading the value of a node attribute
	 * 
	 * @param name
	 * @return
	 */
	public abstract Object readAttribute(String name);
}
