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
package org.cmg.jresp.simulation;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.behaviour.ContextState;
import org.cmg.jresp.comp.AttributeCollector;
import org.cmg.jresp.comp.INode;
import org.cmg.jresp.knowledge.AbstractActuator;
import org.cmg.jresp.knowledge.AbstractSensor;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.KnowledgeManager;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.policy.DefaultPermitPolicy;
import org.cmg.jresp.policy.IPolicy;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Target;

/**
 * @author loreti
 *
 */
public class SimulationNode extends Observable implements INode {

	/**
	 * Node name.
	 */
	protected String name;

	protected SimulationEnvironment environment;

	protected LinkedList<AbstractActuator> actuators;

	protected LinkedList<Agent> agents;

	/**
	 * The collection of attributes exposed by the node updated by an Attribute
	 * Collector
	 */
	protected Hashtable<String, AttributeCollector> attributes = new Hashtable<String, AttributeCollector>();

	/**
	 * The collection of attributes exposed by the node that are not updated by
	 * an Attribute Collector
	 */
	protected Hashtable<String, Attribute> attributePSCEL = new Hashtable<String, Attribute>();

	protected HashMap<String, Attribute> interfaze;

	protected LinkedList<AbstractSensor> sensors;

	protected KnowledgeManager knowledgeManager;

	protected IPolicy policy;

	public SimulationNode(String name, SimulationEnvironment environment) {
		this(name, environment, new SimulationTupleSpace(environment.getElementSelector()));
	}

	public SimulationNode(String name, SimulationEnvironment environment, KnowledgeManager knowledgeManager) {
		this.name = name;
		this.environment = environment;
		this.environment.register(this);
		this.actuators = new LinkedList<AbstractActuator>();
		this.agents = new LinkedList<Agent>();
		this.sensors = new LinkedList<AbstractSensor>();
		this.knowledgeManager = knowledgeManager;
		this.attributes = new Hashtable<String, AttributeCollector>();
		this.attributePSCEL = new Hashtable<String, Attribute>();
		// by default the policy is all equal true
		this.policy = new DefaultPermitPolicy(this);
	}

	// -------------------------------------------------------------

	// SETTERs

	/**
	 * To use when a new policy respect the permit-all one is needed
	 * 
	 * @param policy
	 */
	public void setPolicy(IPolicy policy) {
		this.policy = policy;
		this.policy.setNode(this);
	}

	@Override
	public void addActuator(AbstractActuator actuator) {
		this.actuators.add(actuator);
	}

	@Override
	public void addAgent(Agent a) {
		this.agents.add(a);
		this.environment.execute(this, a);
	}

	// public void execAgent(Agent a) {
	// this.environment.execute(this,a);
	// }

	@Override
	public void addAttributeCollector(AttributeCollector ac) {
		ac.setNode(this);
		this.attributes.put(ac.getName(), ac);
		recomputeInterface();
	}

	@Override
	public void addSensor(AbstractSensor sensor) {
		this.sensors.add(sensor);
		sensor.addObserver(new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				recomputeInterface();
			}
		});
	}

	// GETTERs

	@Override
	public AbstractActuator[] getActuators() {
		return actuators.toArray(new AbstractActuator[actuators.size()]);
	}

	@Override
	public Attribute getAttribute(String name) {
		if (interfaze == null) {
			recomputeInterface();
		}
		return interfaze.get(name);
	}

	@Override
	public Attribute[] getAttributes(String[] attributes) {
		Attribute[] values = new Attribute[attributes.length];
		for (int i = 0; i < attributes.length; i++) {
			values[i] = getAttribute(attributes[i]);
		}
		return values;
	}

	public Attribute[] getAttributes() {
		Collection<Attribute> values = interfaze.values();
		Attribute[] toReturn = new Attribute[values.size()];
		int i = 0;
		for (Attribute a : values) {
			toReturn[i] = a;
			i++;
		}
		return toReturn;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public AbstractSensor[] getSensors() {
		return sensors.toArray(new AbstractSensor[sensors.size()]);
	}

	@Override
	public ContextState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public HashMap<String, Attribute> getInterface() {
		// if (interfaze == null) {
		recomputeInterface();
		// }
		return interfaze;
	}

	// -------------------------------------------------------------

	/*
	 * Methods called by agent in order to execute an action. The methods
	 * delegate the execution to the (simulated) policy
	 */

	// -------------------------------------------------------------

	/**
	 * Method that invokes the execution in the simulation environment of an
	 * action by also considering policy
	 * 
	 * @param a
	 * @param t
	 * @param l
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void executeExecue(Agent a, Agent b) throws InterruptedException {
		policy.exec(a, b);
	}

	/**
	 * Method that invokes the execution in the simulation environment of an
	 * action by also considering policy
	 * 
	 * @param a
	 * @param t
	 * @param l
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public Tuple executeQuery(Agent a, Template t, Target l) throws InterruptedException, IOException {
		return policy.query(a, t, l);
	}

	/**
	 * Method that invokes the execution in the simulation environment of an
	 * action by also considering policy
	 * 
	 * @param a
	 * @param t
	 * @param l
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public Tuple executeGet(Agent a, Template t, Target l) throws InterruptedException, IOException {
		return policy.get(a, t, l);
	}

	/**
	 * Method that invokes the execution in the simulation environment of an
	 * action by also considering policy
	 * 
	 * @param a
	 * @param t
	 * @param l
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean executePut(Agent a, Tuple t, Target l) throws InterruptedException, IOException {
		return policy.put(a, t, l);
	}

	/**
	 * Method that invokes the execution in the simulation environment of an
	 * action by also considering policy
	 * 
	 * @param name
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public Object executeReadAttribute(String name) throws InterruptedException {
		return policy.readAttribute(name);
	}

	/**
	 * Method that invokes the execution in the simulation environment of an
	 * action by also considering policy
	 * 
	 * @param name
	 * @param value
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void executeUpdateAttribute(String name, Object value) throws InterruptedException {
		policy.updateAttribute(name, value);
	}

	/*
	 * -------------------------------------------------------------- SIMULATION
	 * ON GROUP-ORIENTED AND POINT-TO-POINT SIMULATION to be called for
	 * simulating policy evaluation
	 * --------------------------------------------------------------
	 */

	public Tuple acceptGet(SimulationNode simulationNode, Template t, PointToPoint l)
			throws InterruptedException, IOException {
		return policy.acceptGet(new PointToPoint(simulationNode.name, null), 0, t);
	}

	public Tuple acceptGroupGet(SimulationNode simulationNode, Template t, Group l)
			throws IOException, InterruptedException {
		return policy.acceptGroupGet(new PointToPoint(simulationNode.name, null), 0, l.getPredicate(), t);
	}

	public void acceptPut(SimulationNode simulationNode, Tuple t) throws InterruptedException, IOException {
		policy.acceptPut(new PointToPoint(simulationNode.name, null), 0, t);
	}

	public void acceptGroupPut(SimulationNode simulationNode, Group l, Tuple t)
			throws IOException, InterruptedException {
		policy.acceptGroupPut(new PointToPoint(simulationNode.name, null), 0, l.getPredicate(), t);
	}

	public Tuple acceptQuery(SimulationNode simulationNode, Template t) throws InterruptedException, IOException {
		return policy.acceptQuery(new PointToPoint(simulationNode.name, null), 0, t);
	}

	public Tuple accetGroupQuery(SimulationNode simulationNode, Group l, Template t)
			throws IOException, InterruptedException {
		return policy.acceptGroupQuery(new PointToPoint(simulationNode.name, null), 0, l.getPredicate(), t);
	}

	// -------------------------------------------------------------

	/*
	 * Methods called by the simulated policy in order to enforce the action in
	 * the simulated node.
	 */

	// -------------------------------------------------------------

	@Override
	public Tuple get(Template template) throws InterruptedException {
		Tuple t = knowledgeManager.get(template);
		recomputeInterface();
		return t;
	}

	@Override
	public Tuple get(Template t, Target l) throws InterruptedException, IOException {
		return this.environment.get(this, t, l);
	}

	@Override
	public void put(Tuple tuple) {
		if (!putToActuators(tuple)) {
			knowledgeManager.put(tuple);
		}
		recomputeInterface();
	}

	private boolean putToActuators(Tuple tuple) {
		for (AbstractActuator actuator : actuators) {
			if (actuator.getTemplate().match(tuple)) {
				actuator.send(tuple);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean put(Tuple t, Target l) throws InterruptedException {
		return environment.put(this, t, l);
	}

	@Override
	public Tuple query(Template template) {
		for (AbstractSensor sensor : sensors) {
			try {
				Tuple value;
				value = sensor.getValue(template, false);
				if (value != null) {
					return value;
				}
			} catch (InterruptedException e) {
			}
		}
		try {
			return knowledgeManager.query(template);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Tuple query(Template t, Target l) throws InterruptedException {
		return environment.query(this, t, l);
	}

	@Override
	public void updateAttribute(String name, Object value) {
		// environment.
		this.getAttribute(name).setValue(value);
	}

	@Override
	public Object readAttribute(String name) {

		return this.getAttribute(name).getValue();
	}

	// ----------------------------------------------------------------------

	/**
	 * Execute the simulation on component interface, i.e. update of attribute
	 * collectors
	 */
	protected synchronized void recomputeInterface() {
		if (interfaze == null) {
			interfaze = new HashMap<String, Attribute>();
		}
		boolean changed = false;
		HashMap<String, Attribute> values = new HashMap<String, Attribute>();
		values.put("ID", new Attribute("ID", getName()));
		// Update Interface with value collected by Attribute Collector
		for (String attributeName : attributes.keySet()) {
			Attribute a = attributes.get(attributeName).eval();
			if (!a.equals(interfaze.get(attributeName))) {
				changed = true;
			}
			values.put(attributeName, a);
		}
		// ADD fixed attributes
		for (String attributeName : attributePSCEL.keySet()) {
			Attribute a = attributePSCEL.get(attributeName);
			if (!a.equals(interfaze.get(attributeName))) {
				changed = true;
			}
			values.put(attributeName, a);
		}
		interfaze = values;
		if (changed) {
			setChanged();
			notifyObservers();
		}
	}

	@Override
	public void sendAttibutes(PointToPoint to, int session, String[] attributes)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
	}

	@Override
	public Attribute[] sendAttributeRequest(PointToPoint l) throws IOException, InterruptedException {
		return environment.sendAttributeRequest(l);
	}

	@Override
	public void sendFail(PointToPoint to, int session, String message) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendGroupPutReply(PointToPoint source, int session, Attribute[] attributes2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendTuple(PointToPoint to, int session, Tuple tuple) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void put(PointToPoint from, int session, Tuple tuple) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void gPut(PointToPoint from, int session, GroupPredicate groupPredicate, Tuple tuple)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public Tuple gGet(PointToPoint from, int session, GroupPredicate groupPredicate, Template template) {
		try {
			return get(template);
		} catch (InterruptedException e) {
			return null;
		}
	}

	@Override
	public Tuple gQuery(PointToPoint from, int session, GroupPredicate groupPredicate, Template template) {
		return query(template);
	}

	@Override
	public String fresh() {
		return environment.fresh(this);
	}

	@Override
	public Tuple queryp(Template template) {
		return knowledgeManager.queryp(template);
	}

	public LinkedList<PointToPoint> getLocalAddresses() {
		LinkedList<PointToPoint> l = new LinkedList<PointToPoint>();
		l.add(getLocalAddress());
		return l;
	}

	public PointToPoint getLocalAddress() {
		return new PointToPoint(this.name, SimulationNodeAddress.getInstance());
	}

	public void refreshInterface() {
		recomputeInterface();
	}

	public int getNumberOfTuplesMatching(Template template) {
		return knowledgeManager.queryAll(template).size();
	}

	@Override
	public Tuple getp(Template template) {
		Tuple t = knowledgeManager.getp(template);
		if (t != null) {
			recomputeInterface();
		}
		return t;
	}

	public LinkedList<Tuple> getAll(Template t) {
		return knowledgeManager.getAll(t);
	}

	public LinkedList<Tuple> queryAll(Template t) {
		// TODO Auto-generated method stub
		return knowledgeManager.queryAll(t);
	}

	@Override
	public synchronized boolean setAttribute(String name, Object value) {
		Attribute a = this.getAttribute(name);
		if (a != null) {
			a.setValue(value);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public synchronized void addAttribute(Attribute a) {
		this.attributePSCEL.put(a.getName(), a);
		this.recomputeInterface();
	}

}
