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
package org.cmg.jresp.comp;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.behaviour.AgentContext;
import org.cmg.jresp.behaviour.ContextState;
import org.cmg.jresp.knowledge.AbstractActuator;
import org.cmg.jresp.knowledge.AbstractSensor;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.Knowledge;
import org.cmg.jresp.knowledge.KnowledgeAdapter;
import org.cmg.jresp.knowledge.KnowledgeManager;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.policy.DefaultPermitPolicy;
import org.cmg.jresp.policy.IPolicy;
import org.cmg.jresp.protocol.Ack;
import org.cmg.jresp.protocol.AttributeReply;
import org.cmg.jresp.protocol.AttributeRequest;
import org.cmg.jresp.protocol.Fail;
import org.cmg.jresp.protocol.GetRequest;
import org.cmg.jresp.protocol.GroupGetReply;
import org.cmg.jresp.protocol.GroupGetRequest;
import org.cmg.jresp.protocol.GroupPutReply;
import org.cmg.jresp.protocol.GroupPutRequest;
import org.cmg.jresp.protocol.GroupQueryReply;
import org.cmg.jresp.protocol.GroupQueryRequest;
import org.cmg.jresp.protocol.MessageHandler;
import org.cmg.jresp.protocol.PutRequest;
import org.cmg.jresp.protocol.QueryRequest;
import org.cmg.jresp.protocol.TupleReply;
import org.cmg.jresp.protocol.jRESPMessage;
import org.cmg.jresp.topology.AbstractPort;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.MessageDispatcher;
import org.cmg.jresp.topology.MessageSender;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Target;

/**
 * 
 * This class implements a generic SCEL components.
 * 
 * @author Michele Loreti
 * 
 *
 */
public class Node extends Observable implements MessageDispatcher, INode {

	/**
	 * A list of Agents that are waiting for the execution.
	 */
	protected LinkedList<Agent> waiting;

	/**
	 * A parameter identifying the time-out for group oriente actions.
	 */
	protected int groupActionWaitingTime = 10000;

	/**
	 * This is the thread that is instantiated when a node receives a new
	 * message.
	 * 
	 * @author Michele Loreti
	 *
	 */
	public class NodeMessageHandler extends MessageHandler implements Runnable {

		/**
		 * The received message.
		 */
		private jRESPMessage m;

		/**
		 * Creates an instance that is devoted to handle message <code>m</code>.
		 * 
		 * @param m
		 *            message to handle.
		 */
		public NodeMessageHandler(jRESPMessage m) {
			this.m = m;
		}

		@Override
		public void handle(Ack msg) {
			synchronized (putPending) {
				Pending<Boolean> pending = putPending.get(msg.getSession());
				if (pending != null) {
					pending.set(true);
				} else {
				}
			}
		}

		@Override
		public void handle(AttributeReply msg) throws IOException, InterruptedException {
			synchronized (pendingAttributeRequests) {
				Pending<Attribute[]> pending = pendingAttributeRequests.get(msg.getSession());
				if (pending == null) {
					sendFail(msg.getSource(), msg.getSession(),
							"Session " + msg.getSession() + " is uknown at " + getName());
				} else {
					pending.set(msg.getValues());
				}
			}
		}

		@Override
		public void handle(AttributeRequest msg) throws IOException, InterruptedException {
			sendAttibutes(msg.getSource(), msg.getSession(), msg.getAttributes());
		}

		@Override
		public void handle(Fail msg) {
			synchronized (putPending) {
				Pending<Boolean> pending = putPending.get(msg.getSession());
				if (pending != null) {
					pending.fail();
					return;
				}
			}
			synchronized (tuplePending) {
				Pending<Tuple> pending = tuplePending.get(msg.getSession());
				if (pending != null) {
					pending.fail();
					return;
				}
			}
			synchronized (pendingAttributeRequests) {
				Pending<Attribute[]> pending = pendingAttributeRequests.get(msg.getSession());
				if (pending != null) {
					pending.fail();
					return;
				}
			}
		}

		@Override
		public void handle(GetRequest msg) throws IOException, InterruptedException {
			try {
				policy.acceptGet(msg.getSource(), msg.getSession(), msg.getTemplate());
			} catch (Exception e) {
				sendFail(msg.getSource(), msg.getSession(), e.getMessage());
			}
		}

		@Override
		public void handle(GroupGetReply msg) throws IOException, InterruptedException {
			synchronized (pendigGroupGet) {
				LinkedList<GroupGetReply> pending = pendigGroupGet.get(msg.getSession());
				if (pending == null) {
					sendFail(msg.getSource(), msg.getTupleSession(),
							"Session " + msg.getSession() + " is unknown at " + getName());
				} else {
					pending.add(msg);
				}
			}
		}

		@Override
		public void handle(GroupGetRequest msg) throws IOException, InterruptedException {
			policy.acceptGroupGet(msg.getSource(), msg.getSession(), msg.getGroupPredicate(), msg.getTemplate());
		}

		@Override
		public void handle(GroupPutReply msg) throws IOException, InterruptedException {
			synchronized (outGroupPutPending) {
				LinkedList<GroupPutReply> pending = outGroupPutPending.get(msg.getSession());
				if (pending == null) {
					sendFail(msg.getSource(), msg.getTupleSession(),
							"Session " + msg.getSession() + " is unknown at " + getName());
				} else {
					pending.add(msg);
				}
			}
		}

		@Override
		public void handle(GroupPutRequest msg) throws IOException, InterruptedException {
			policy.acceptGroupPut(msg.getSource(), msg.getSession(), msg.getGroupPredicate(), msg.getTuple());
		}

		@Override
		public void handle(GroupQueryReply msg) throws IOException, InterruptedException {
			synchronized (pendigGroupQuery) {
				LinkedList<GroupQueryReply> pending = pendigGroupQuery.get(msg.getSession());
				if (pending != null) {
					pending.add(msg);
				} else {
					sendFail(msg.getSource(), msg.getSession(), "Get request completed!");
				}
			}
		}

		@Override
		public void handle(GroupQueryRequest msg) throws IOException, InterruptedException {
			policy.acceptGroupQuery(msg.getSource(), msg.getSession(), msg.getGroupPredicate(), msg.getTemplate());
		}

		@Override
		public void handle(PutRequest msg) throws IOException, InterruptedException {
			try {
				policy.acceptPut(msg.getSource(), msg.getSession(), msg.getTuple());
			} catch (Exception e) {
				sendFail(msg.getSource(), msg.getSession(),
						"Session " + msg.getSession() + " is unknown at " + getName());
			}
		}

		@Override
		public void handle(QueryRequest msg) throws IOException, InterruptedException {
			policy.acceptQuery(msg.getSource(), msg.getSession(), msg.getTemplate());
		}

		@Override
		public void handle(TupleReply msg) throws IOException, InterruptedException {
			synchronized (tuplePending) {
				Pending<Tuple> pending = tuplePending.get(msg.getSession());
				if (pending == null) {
					sendFail(msg.getSource(), msg.getSession(),
							"Session " + msg.getSession() + " is unknown at " + getName());
				} else {
					pending.set(msg.getTuple());
					tuplePending.remove(msg.getSession());
					sendAck(msg.getSource(), msg.getSession());
				}
			}
		}

		@Override
		public void run() {
			try {
				m.accept(this);
			} catch (IOException e) {
				// TODO Manage error handling!
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * This thread is used to remove messages from the entry queue and to
	 * activate the corresponding handler.
	 * 
	 * @author Michele Loreti
	 *
	 */
	public class NodeThread implements Runnable {

		@Override
		public void run() {
			try {
				while (isRunning()) {
					jRESPMessage m = getNextMessage();
					executor.execute(new NodeMessageHandler(m));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static final String ID_ATTRIBUTE_NAME = "ID";

	/**
	 * Local knowledge
	 */
	protected Knowledge knowledge;

	/**
	 * Node policy
	 */
	protected IPolicy policy;

	/**
	 * Port used to perform group-based interactions
	 */
	protected LinkedList<AbstractPort> ports;

	protected Queue<jRESPMessage> pendingMessages = new LinkedList<jRESPMessage>();

	/**
	 * Counter used to associate id to agents.
	 */
	protected int agentCounter = 0;

	/**
	 * Session counter
	 */
	protected int sessionCounter = 0;

	/**
	 * Node name
	 */
	protected String name;

	/**
	 * An hash-table containing the attribute requests that have been sent from
	 * this node and that have not yet received a reply.
	 */
	protected Hashtable<Integer, Pending<Attribute[]>> pendingAttributeRequests = new Hashtable<Integer, Pending<Attribute[]>>();

	/**
	 * An hash-table containing the tuple requests that have been sent from this
	 * node and that have not yet received a reply.
	 */
	protected Hashtable<Integer, Pending<Tuple>> tuplePending = new Hashtable<Integer, Pending<Tuple>>();

	/**
	 * An hash-table containing the put requests that have been sent from this
	 * node and that have not yet received a reply.
	 */
	protected Hashtable<Integer, Pending<Boolean>> putPending = new Hashtable<Integer, Pending<Boolean>>();

	/**
	 * An hash-table containing the group oriented put requests that have been
	 * sent from this node and that have not yet received a reply.
	 */
	protected Hashtable<Integer, Pending<Boolean>> inGroupPutPending = new Hashtable<Integer, Pending<Boolean>>();

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

	/**
	 * The list of running agent.
	 */
	protected LinkedList<Agent> agents;

	/**
	 * The status of the node.
	 */
	private ContextState state;

	/**
	 * Executor used to instantiate threads.
	 */
	protected Executor executor = Executors.newCachedThreadPool();

	/**
	 * An hash-table containing the message sent to accept group oriented put
	 * requests and that have not yet received a reply.
	 */
	private Hashtable<Integer, LinkedList<GroupPutReply>> outGroupPutPending = new Hashtable<Integer, LinkedList<GroupPutReply>>();

	/**
	 * An hash-table containing the group get requests that have been sent from
	 * this node and that have not yet received a reply.
	 */
	private Hashtable<Integer, LinkedList<GroupGetReply>> pendigGroupGet = new Hashtable<Integer, LinkedList<GroupGetReply>>();

	/**
	 * An hash-table containing the group query requests that have been sent
	 * from this node and that have not yet received a reply.
	 */
	private Hashtable<Integer, LinkedList<GroupQueryReply>> pendigGroupQuery = new Hashtable<Integer, LinkedList<GroupQueryReply>>();

	/**
	 * Counts new generated names.
	 */
	private int nameCounter = 0;

	private HashMap<String, Attribute> interfaze;

	private boolean updateInterfaceFlag = false;

	private Object nodeLock = new Object();

	/**
	 * Creates a new instance of a nome named <code>name</code> with knowledge
	 * repository <code>knowledge</code>. The policy, in this case, is the
	 * default-permit one, i.e. all actions are authorized
	 * 
	 * @param name
	 *            node name
	 * @param knowledge
	 *            knowledge repository
	 */
	public Node(String name, KnowledgeManager knowledge, KnowledgeAdapter... adapters) {
		this.name = name;
		this.knowledge = new Knowledge(knowledge, adapters);
		this.agents = new LinkedList<Agent>();
		this.policy = new DefaultPermitPolicy(this);
		this.state = ContextState.READY;
		this.ports = new LinkedList<AbstractPort>();
		this.waiting = new LinkedList<Agent>();
		this.interfaze = new HashMap<String, Attribute>();
		this.attributes = new Hashtable<String, AttributeCollector>();
		this.attributePSCEL = new Hashtable<String, Attribute>();
		this.knowledge.addObserver(new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				updateInterface(true);
			}

		});
	}

	/**
	 * Add policy manager. As default it is added a permit-all policy. This
	 * method can be used e.g. for adding the PolicyAutomaton. All other classes
	 * implementing IPolicy are allowed.
	 * 
	 * @param policy
	 */
	public void setPolicy(IPolicy policy) {
		this.policy = policy;
		this.policy.setNode(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#addActuator(org.cmg.resp.comp.NodeActuator)
	 */
	@Override
	public synchronized void addActuator(AbstractActuator actuator) {
		this.knowledge.addActuator(actuator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#addAgent(org.cmg.resp.behaviour.Agent)
	 */
	@Override
	public void addAgent(Agent a) {
		if (isRunning()) {
			_addAgent(a);
		} else {
			waiting.add(a);
		}
	}

	protected void _addAgent(Agent a) {
		a.setContext(getAgentId(), new AgentContext() {

			@Override
			public void exec(Agent a, Agent b) throws InterruptedException {
				policy.exec(a, b);
			}

			@Override
			public Tuple get(Agent a, Template t, Target l) throws InterruptedException, IOException {
				return policy.get(a, t, l);
			}

			@Override
			public boolean put(Agent a, Tuple t, Target l) throws InterruptedException, IOException {
				return policy.put(a, t, l);
			}

			@Override
			public Tuple query(Agent a, Template t, Target l) throws InterruptedException, IOException {
				return policy.query(a, t, l);
			}

			@Override
			public String fresh(Agent a) throws InterruptedException {
				return policy.fresh(a);
			}

			@Override
			public void done(Agent agent) {
			}

			@Override
			public void suspend(long t) throws InterruptedException {
				Thread.sleep(t);
			}

			@Override
			public LinkedList<PointToPoint> getLocalAddresses() {
				return Node.this.getLocalAddresses();
			}

			@Override
			public Object readAttribute(String name) throws InterruptedException {
				return policy.readAttribute(name);
				// Attribute a = getAttribute(name);
				// if (a == null) {
				// return null;
				// }
				// return a.getValue();
			}

			@Override
			public boolean updateAttribute(String name, Object value) throws InterruptedException {
				return policy.updateAttribute(name, value);
				// return Node.this.setAttribute(name, value);
			}

			@Override
			public Tuple getp(Agent agent, Template t) {
				return Node.this.getp(t);
			}

			@Override
			public Tuple queryp(Agent agent, Template t) {
				return Node.this.queryp(t);
			}

			@Override
			public LinkedList<Tuple> getAll(Agent agent, Template t) {
				return Node.this.getAll(t);
			}

			@Override
			public LinkedList<Tuple> queryAll(Agent agent, Template t) {
				return Node.this.queryAll(t);
			}

		});
		agents.add(a);
		executor.execute(a);
	}

	protected synchronized LinkedList<Tuple> queryAll(Template t) {
		return knowledge.queryAll(t);
	}

	protected LinkedList<Tuple> getAll(Template t) {
		return knowledge.getAll(t);
	}

	/**
	 * Returns the list of node's addresses.
	 * 
	 * @return the list of node's addresses.
	 */
	protected synchronized LinkedList<PointToPoint> getLocalAddresses() {
		LinkedList<PointToPoint> addresses = new LinkedList<PointToPoint>();
		for (AbstractPort p : this.ports) {
			addresses.add(new PointToPoint(this.name, p.getAddress()));
		}

		return addresses;
	}

	/**
	 * This method is used to generate a new fresh identifier.
	 * 
	 * @return a new fresh identifier.
	 */
	@Override
	public synchronized String fresh() {
		return super.toString() + ":" + name + ":" + (nameCounter++);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#addAttributeCollector(org.cmg.resp.comp.
	 * AttributeCollector)
	 */
	@Override
	public synchronized void addAttributeCollector(AttributeCollector ac) {
		ac.setNode(this);
		attributes.put(ac.getName(), ac);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmg.scel.topology.MessageDispatcher#addMessage(org.cmg.scel.protocol.
	 * Message)
	 */
	@Override
	public synchronized void addMessage(jRESPMessage msg) {
		pendingMessages.add(msg);
		notifyAll();
	}

	/**
	 * Add a port to the node.
	 * 
	 * @param p
	 *            the port to add
	 */
	public synchronized void addPort(AbstractPort p) {
		p.register(this);
		ports.add(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#addSensor(org.cmg.resp.comp.NodeSensor)
	 */
	@Override
	public synchronized void addSensor(AbstractSensor sensor) {
		knowledge.addSensor(sensor);
	}

	/**
	 * Terminates node
	 */
	private void doStop() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#get(org.cmg.resp.knowledge.Template)
	 */
	@Override
	public Tuple get(Template template) throws InterruptedException {
		return knowledge.get(template);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#get(org.cmg.resp.knowledge.Template,
	 * org.cmg.resp.topology.Target)
	 */
	@Override
	public Tuple get(Template t, Target l) throws InterruptedException, IOException {
		if (l.isSelf()) {
			return get(t);
		}
		if (l.isAGroup()) {
			return sendGroupGetRequest((Group) l, t);
		}
		return sendGetRequest((PointToPoint) l, t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#getActuators()
	 */
	@Override
	public AbstractActuator[] getActuators() {
		return knowledge.getActuators();
	}

	/**
	 * Computes next agent id.
	 * 
	 * @return next agent id.
	 */
	protected synchronized int getAgentId() {
		return agentCounter++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#getAttribute(java.lang.String)
	 */
	@Override
	public synchronized Attribute getAttribute(String name) {
		if (ID_ATTRIBUTE_NAME.equals(name)) {
			return new Attribute(name, getName());
		}
		if (interfaze == null) {
			return null;
		}
		return interfaze.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#getAttributes(java.lang.String[])
	 */
	@Override
	public synchronized Attribute[] getAttributes(String[] attributes) {
		Attribute[] toReturn = new Attribute[attributes.length];
		for (int i = 0; i < attributes.length; i++) {
			toReturn[i] = getAttribute(attributes[i]);
		}
		return toReturn;
	}

	public synchronized Attribute[] getAttributes() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.scel.topology.MessageDispatcher#getName()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Retrieves next message from the message queue.
	 * 
	 * @return next message in the incoming message queue.
	 * 
	 * @throws InterruptedException
	 *             when another thread interrupts current thread computation
	 *             while action is under execution.
	 */
	private synchronized jRESPMessage getNextMessage() throws InterruptedException {
		while (pendingMessages.isEmpty()) {
			wait();
		}
		return pendingMessages.poll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#getSensors()
	 */
	@Override
	public AbstractSensor[] getSensors() {
		return knowledge.getSensors();
	}

	/**
	 * Creates a new session id.
	 * 
	 * @return a new session id.
	 */
	private synchronized int getSession() {
		return sessionCounter++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#getState()
	 */
	@Override
	public synchronized ContextState getState() {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#isRunning()
	 */
	@Override
	public synchronized boolean isRunning() {
		return state == ContextState.RUNNING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#put(org.cmg.resp.knowledge.Tuple)
	 */
	@Override
	public void put(Tuple tuple) {
		knowledge.put(tuple);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#put(org.cmg.resp.knowledge.Tuple,
	 * org.cmg.resp.topology.Target)
	 */
	@Override
	public boolean put(Tuple t, Target l) throws InterruptedException, IOException {
		if (l.isSelf()) {
			put(t);
			return true;
		}
		if (l.isAGroup()) {
			return sendGroupPutRequest((Group) l, t);
		}
		return sendPutRequest((PointToPoint) l, t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#query(org.cmg.resp.knowledge.Template)
	 */
	@Override
	public Tuple query(Template template) throws InterruptedException {
		return knowledge.query(template);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#query(org.cmg.resp.knowledge.Template,
	 * org.cmg.resp.topology.Target)
	 */
	@Override
	public Tuple query(Template t, Target l) throws InterruptedException, IOException {
		if (l.isSelf()) {
			return query(t);
		}
		if (l.isAGroup()) {
			return sendGroupQueryRequest((Group) l, t);
		}
		return sendQueryRequest((PointToPoint) l, t);
	}

	/**
	 * Queries a tuple matching template <code>template</code> from the
	 * knowledge repository of a node belonging to group <code>l</code>. This is
	 * a blocking: the thread invoking this method is blocked until a matching
	 * tuple is found.
	 *
	 * @param l
	 *            a group identifying the target node
	 * @param t
	 *            action template
	 * @return a matching tuple
	 * @throws IOException
	 *             when an I/O error occurs
	 * @throws InterruptedException
	 *             when another thread interrupts current thread computation
	 *             while action is under execution.
	 */
	private Tuple sendGroupQueryRequest(Group l, Template t) throws IOException, InterruptedException {
		Tuple result = null;
		while (result == null) {
			int session = getSession();
			LinkedList<GroupQueryReply> received = new LinkedList<GroupQueryReply>();
			synchronized (pendigGroupQuery) {
				pendigGroupQuery.put(session, received);
			}
			broadCastQueryRequest(session, l.getPredicate(), t);
			Pending<Tuple> pending = new Pending<Tuple>();
			executor.execute(new GroupQueryHandler(l, session, pending));
			result = pending.get();
		}
		return result;
	}

	/**
	 * Sends an acknowledgment message to <code>to</code> with id
	 * <code>session</code>.
	 * 
	 * @param to
	 *            target of the acknowledgment
	 * @param session
	 *            message session
	 * @throws IOException
	 *             when an I/O error occurs
	 * @throws InterruptedException
	 *             when another thread interrupts current thread computation
	 *             while action is under execution.
	 */
	public void sendAck(PointToPoint to, int session) throws IOException, InterruptedException {
		for (MessageSender p : ports) {
			if (p.canSendTo(to)) {
				p.sendAck(to, getName(), session);
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmg.resp.comp.INode#sendAttibutes(org.cmg.resp.topology.PointToPoint,
	 * int, java.lang.String[])
	 */
	@Override
	public void sendAttibutes(PointToPoint to, int session, String[] attributes)
			throws IOException, InterruptedException {
		for (MessageSender p : ports) {
			if (p.canSendTo(to)) {
				if (attributes.length > 0) {
					p.sendAttributes(to, getName(), session, getAttributes(attributes));
				} else {
					p.sendAttributes(to, getName(), session, getAttributes());
				}
				return;
			}
		}
	}

	@Override
	public Attribute[] sendAttributeRequest(PointToPoint l) throws IOException, InterruptedException {
		for (MessageSender p : ports) {
			if (p.canSendTo(l)) {
				int session = getSession();
				Pending<Attribute[]> pending = new Pending<Attribute[]>();
				synchronized (pendingAttributeRequests) {
					pendingAttributeRequests.put(session, pending);
				}
				Attribute[] result = null;
				while (result == null) {
					p.sendAttributeRequest(l, getName(), session, new String[] {});
					result = pending.get();
				}
				return result;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#sendFail(org.cmg.resp.topology.PointToPoint,
	 * int, java.lang.String)
	 */
	@Override
	public void sendFail(PointToPoint to, int session, String message) throws IOException, InterruptedException {
		for (MessageSender p : ports) {
			if (p.canSendTo(to)) {
				p.sendFail(to, getName(), session, message);
				return;
			}
		}
	}

	private Tuple sendGetRequest(PointToPoint l, Template t) throws InterruptedException, IOException {
		for (MessageSender p : ports) {
			if (p.canSendTo(l)) {
				int session = getSession();
				Pending<Tuple> pending = new Pending<Tuple>();
				synchronized (tuplePending) {
					tuplePending.put(session, pending);
				}
				Tuple result = null;
				while (result == null) {
					p.sendGetRequest(l, getName(), session, t);
					result = pending.get();
				}
				return result;
			}
		}
		// TODO: Handle the case when no port is able to deliver message at l!
		return null;
	}

	private boolean sendGroupPutRequest(Group l, Tuple t) throws IOException, InterruptedException {
		int session = getSession();
		LinkedList<GroupPutReply> received = new LinkedList<GroupPutReply>();
		outGroupPutPending.put(session, received);
		for (MessageSender p : ports) {
			p.sendGroupPutRequest(getName(), session, l.getPredicate(), t);
		}
		executor.execute(new GroupPutHandler(l, session, t));
		return true;
	}

	private Tuple sendGroupGetRequest(Group l, Template t) throws IOException, InterruptedException {
		Tuple result = null;
		while (result == null) {
			int session = getSession();
			LinkedList<GroupGetReply> received = new LinkedList<GroupGetReply>();
			synchronized (pendigGroupGet) {
				pendigGroupGet.put(session, received);
			}
			broadCastGetRequest(session, l.getPredicate(), t);
			Pending<Tuple> pending = new Pending<Tuple>();
			executor.execute(new GroupGetHandler(l, session, pending));
			result = pending.get();
		}
		return result;
	}

	private void broadCastGetRequest(int session, GroupPredicate groupPredicate, Template t)
			throws IOException, InterruptedException {
		for (MessageSender p : ports) {
			p.sendGroupGetRequest(getName(), session, groupPredicate, t);
		}
	}

	private void broadCastQueryRequest(int session, GroupPredicate groupPredicate, Template t)
			throws IOException, InterruptedException {
		for (MessageSender p : ports) {
			p.sendGroupQueryRequest(getName(), session, groupPredicate, t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#sendGroupPutReply(org.cmg.resp.topology.
	 * PointToPoint, int, org.cmg.resp.knowledge.Attribute[])
	 */
	@Override
	public void sendGroupPutReply(PointToPoint source, int session, Attribute[] attributes2) {
		// IPort p = getPort( source );
	}

	private boolean sendPutRequest(PointToPoint l, Tuple t) throws InterruptedException, IOException {
		for (MessageSender p : ports) {
			if (p.canSendTo(l)) {
				int session = getSession();
				Pending<Boolean> pending = new Pending<Boolean>();
				putPending.put(session, pending);
				p.sendPutRequest(l, getName(), session, t);
				// Alberto: is this a bug? it causes some deadlock with policies
				//return true; // temporary patch, makes put async
				return pending.get(); //bug?
			}
		}
		return false;// TODO: Probably an exception should be raised here!
	}

	private Tuple sendQueryRequest(PointToPoint l, Template t) throws InterruptedException, IOException {
		for (MessageSender p : ports) {
			if (p.canSendTo(l)) {
				int session = getSession();
				Pending<Tuple> pending = new Pending<Tuple>();
				synchronized (tuplePending) {
					tuplePending.put(session, pending);
				}
				Tuple result = null;
				while (result == null) {
					p.sendQueryRequest(l, getName(), session, t);
					result = pending.get();
				}
				return result;
			}
		}
		// TODO: Handle the case when no port is able to deliver message at l!
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmg.resp.comp.INode#sendTuple(org.cmg.resp.topology.PointToPoint,
	 * int, org.cmg.resp.knowledge.Tuple)
	 */
	@Override
	public void sendTuple(PointToPoint to, int session, Tuple tuple) throws IOException, InterruptedException {
		for (MessageSender p : ports) {
			if (p.canSendTo(to)) {
				p.sendTuple(to, getName(), session, tuple);
				return;
			}
		}
	}

	public synchronized void start() {
		recomputeInterface();
		for (Agent a : waiting) {
			_addAgent(a);
		}
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {

					while (isRunning()) {
						synchronized (nodeLock) {
							while (!updateInterface()) {
								nodeLock.wait();
							}
							Node.this.updateInterfaceFlag = false;
						}
						recomputeInterface();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		});
		executor.execute(new NodeThread());
		state = ContextState.RUNNING;
	}

	protected boolean updateInterface() {
		synchronized (nodeLock) {
			return updateInterfaceFlag;
		}
	}

	protected void updateInterface(boolean flag) {
		if (flag) {
			synchronized (nodeLock) {
				this.updateInterfaceFlag = flag;
				nodeLock.notifyAll();
			}
		}
	}

	public synchronized void stop() {
		if (state != ContextState.RUNNING) {
			throw new IllegalStateException();
		}
		state = ContextState.HALT;
		notifyAll();
		// for (Agent a : agents) {
		// a.stop();
		// }
		doStop();
	}

	protected synchronized void waitState(ContextState state) throws InterruptedException {
		while (getState() != state) {
			wait();
		}
	}

	public class GroupPutHandler implements Runnable {

		private Group group;
		private int session;

		public GroupPutHandler(Group group, int session, Tuple tuple) {
			this.group = group;
			this.session = session;
		}

		@Override
		public void run() {
			LinkedList<GroupPutReply> received = null;
			try {
				long current = System.currentTimeMillis();
				long deadline = current + groupActionWaitingTime;
				while (current < deadline) {
					Thread.sleep(deadline - current);
					current = System.currentTimeMillis();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			synchronized (Node.this.outGroupPutPending) {
				received = Node.this.outGroupPutPending.get(session);
				Node.this.outGroupPutPending.remove(session);
				if (received != null) {
					try {
						doGroupPut(group, received);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public class GroupGetHandler implements Runnable {

		private Group group;
		private int session;
		private Pending<Tuple> pending;

		public GroupGetHandler(Group group, int session, Pending<Tuple> pending) {
			this.group = group;
			this.session = session;
			this.pending = pending;
		}

		@Override
		public void run() {
			LinkedList<GroupGetReply> received = null;
			try {
				long current = System.currentTimeMillis();
				long deadline = current + groupActionWaitingTime;
				while (current < deadline) {
					Thread.sleep(deadline - current);
					current = System.currentTimeMillis();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			synchronized (Node.this.pendigGroupGet) {
				received = Node.this.pendigGroupGet.get(session);
				if (received != null) {
					Node.this.outGroupPutPending.remove(session);
					try {
						doGroupGet(group, received, pending);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public class GroupQueryHandler implements Runnable {

		private Group group;
		private int session;
		private Pending<Tuple> pending;

		public GroupQueryHandler(Group group, int session, Pending<Tuple> pending) {
			this.group = group;
			this.session = session;
			this.pending = pending;
		}

		@Override
		public void run() {
			LinkedList<GroupQueryReply> received = null;
			try {
				long current = System.currentTimeMillis();
				long deadline = current + groupActionWaitingTime;
				while (current < deadline) {
					Thread.sleep(deadline - current);
					current = System.currentTimeMillis();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			synchronized (Node.this.pendigGroupQuery) {
				received = Node.this.pendigGroupQuery.get(session);
				Node.this.pendigGroupQuery.remove(session);
				if (received != null) {
					try {
						doGroupQuery(group, received, pending);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public int getGroupActionWaitingTime() {
		return groupActionWaitingTime;
	}

	public void doGroupGet(Group group, LinkedList<GroupGetReply> received, Pending<Tuple> pending)
			throws InterruptedException {
		boolean flag = true;

		for (GroupGetReply reply : received) {
			try {
				if (flag) {
					sendAck(reply.getSource(), reply.getTupleSession());
					flag = false;
					pending.set(reply.getTuple());
				} else {
					sendFail(reply.getSource(), reply.getTupleSession(), "Group get completed!");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (flag) {
			pending.fail();
		}

	}

	public void doGroupQuery(Group group, LinkedList<GroupQueryReply> received, Pending<Tuple> pending)
			throws InterruptedException {
		boolean flag = true;

		for (GroupQueryReply reply : received) {
			if (flag) {
				flag = false;
				pending.set(reply.getTuple());
			}
		}

		if (flag) {
			pending.fail();
		}

	}

	public void doGroupPut(Group group, LinkedList<GroupPutReply> received) throws InterruptedException {
		for (GroupPutReply reply : received) {
			try {
				// if (group.getPredicate().evaluate(reply.getValues())) {
				sendAck(reply.getSource(), reply.getTupleSession());
				// } else {
				// sendFail(reply.getSource(),
				// reply.getTupleSession(),"Attribute predicate is not
				// satisfied!");
				// }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setGroupActionWaitingTime(int groupActionWaitingTime) {
		this.groupActionWaitingTime = groupActionWaitingTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#put(org.cmg.resp.topology.PointToPoint, int,
	 * org.cmg.resp.knowledge.Tuple)
	 */
	@Override
	public void put(PointToPoint from, int session, Tuple tuple) throws IOException, InterruptedException {
		put(tuple);
		sendAck(from, session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#gPut(org.cmg.resp.topology.PointToPoint,
	 * int, java.lang.String[], org.cmg.resp.knowledge.Tuple)
	 */
	@Override
	public void gPut(PointToPoint from, int session, GroupPredicate groupPredicate, Tuple tuple)
			throws IOException, InterruptedException {
		MessageSender p = getPort(from);
		if (p != null) {
			if (groupPredicate.evaluate(getInterface())) {
				int tupleSession = getSession();
				Pending<Boolean> pending = new Pending<Boolean>();
				putPending.put(tupleSession, pending);
				p.sendGroupPutReply(from, getName(), session, tupleSession);
				if (pending.get()) {
					knowledge.put(tuple);
				}
			}
		}
	}

	private synchronized MessageSender getPort(Target l) {
		for (MessageSender p : ports) {
			if (p.canSendTo(l)) {
				return p;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#gGet(org.cmg.resp.topology.PointToPoint,
	 * int, java.lang.String[], org.cmg.resp.knowledge.Template)
	 */
	@Override
	public Tuple gGet(PointToPoint from, int session, GroupPredicate groupPredicate, Template template) {
		MessageSender p = getPort(from);
		Tuple t = null;
		if (p != null) {
			if (groupPredicate.evaluate(getInterface())) {
				t = knowledge.getp(template);
				if (t != null) {
					int tupleSession = getSession();
					Pending<Boolean> pending = new Pending<Boolean>();
					putPending.put(tupleSession, pending);
					try {
						p.sendGroupGetReply(from, getName(), session, tupleSession, new Attribute[0], t);
						if (pending.get() == null) {
							knowledge.put(t);
						}
					} catch (Exception e) {
						e.printStackTrace();
						knowledge.put(t);
					}
				}
			}
		}
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.resp.comp.INode#gQuery(org.cmg.resp.topology.PointToPoint,
	 * int, java.lang.String[], org.cmg.resp.knowledge.Template)
	 */
	@Override
	public Tuple gQuery(PointToPoint from, int session, GroupPredicate groupPredicate, Template template) {
		Tuple t = null;
		MessageSender p = getPort(from);
		if (p != null) {
			HashMap<String, Attribute> nodeInterface = getInterface();
			if (groupPredicate.evaluate(nodeInterface)) {
				// System.out.println(getName()+": "+groupPredicate+" SATISFIED
				// with "+nodeInterface);
				t = knowledge.queryp(template);
				if (t != null) {
					try {
						p.sendGroupQueryReply(from, getName(), session, t);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				// System.out.println(getName()+": "+groupPredicate+"
				// UNSATISFIED with "+nodeInterface);
			}
			// p.sendGroupPutReply(from, getName(), session,tupleSession,
			// getAttributes(attributes));
		}
		return t;
	}

	@Override
	public Tuple queryp(Template template) {
		return knowledge.queryp(template);
	}

	@Override
	public Tuple getp(Template template) {
		return knowledge.getp(template);
	}

	@Override
	public synchronized HashMap<String, Attribute> getInterface() {
		return interfaze;
	}

	@Override
	public void updateAttribute(String name, Object value) {
		this.getAttribute(name).setValue(value);
		// policy.updateAttribute(name, value);
	}

	@Override
	public Object readAttribute(String name) {
		return this.getAttribute(name).getValue();
	}

	protected synchronized void recomputeInterface() {
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
		if (changed) {
			setChanged();
			notifyObservers();
		}
		interfaze = values;
	}

}
