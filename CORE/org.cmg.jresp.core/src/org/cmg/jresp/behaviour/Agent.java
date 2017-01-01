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
import java.util.Observable;

import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Target;

/**
 * This class identifies a SCEL agent.
 * 
 * 
 * @author Michele Loreti
 *
 */
public abstract class Agent extends Observable implements Runnable {

	/**
	 * An enumeration identifying possible agent states
	 * 
	 * @author Michele Loreti
	 *
	 */
	public enum State {
		/**
		 * Agent waiting for an operating context.
		 */
		AWAIT,

		/**
		 * Agent ready to start the computation.
		 */
		READY,

		/**
		 * Agent computation is suspended
		 */
		SLEEP,

		/**
		 * Agent computation is forced to terminate
		 */
		HALT,

		/**
		 * Agent computation is terminated in an error state
		 */
		ERROR,

		/**
		 * Agent is running
		 */
		RUNNING,

		/**
		 * Agent computation is successfully terminated
		 */
		DONE
	}

	/**
	 * Agent state
	 */
	// FIXME: private State state;

	/**
	 * Identifies the context where the agent is running.
	 */
	protected AgentContext context;

	/**
	 * Agent name
	 */
	protected String name;

	/**
	 * Agent id
	 */
	protected int id;

	/**
	 * Creates a new agent with a specific name.
	 * 
	 * @param name
	 *            agent name
	 */
	public Agent(String name) {
		this.name = name;
	}

	/**
	 * Returns agent name.
	 * 
	 * @return agent name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Start agent execution.
	 */
	public final void run() {
		try {
			doStart();
			doRun();
			doClose();
		} catch (Exception e) {
			doHandle(e);
		} finally {
			context.done(this);
		}
	}

	protected void doHandle(Exception e) {
		e.printStackTrace();// FIXME!!!!
	}

	/**
	 * A subclass of <code>Agent</code> has to provide an implementation of this
	 * method that identifies agent behaviour.
	 */
	protected abstract void doRun() throws Exception;

	/**
	 * This method is invoked when the agent computation is completed.
	 * Subclasses should override this method to deallocated resources.
	 */
	protected void doClose() {

	}

	/**
	 * A subclass of <code>Agent</code> has to provide an implementation of this
	 * method that identifies agent behaviour.
	 */
	protected void doStart() {
	}

	/**
	 * Adds tupe <code>t</code> to knowledge repository located at
	 * <code>l</code>
	 * 
	 * @param t
	 *            knowledge element
	 * @param l
	 *            target locality
	 * @return <code>true</code> if the tuple t has been successfully added to
	 *         the knowledge located at l
	 * @throws InterruptedException
	 *             - when another thread interrupt agent computation while
	 *             action is under execution.
	 * @throws IOException
	 */
	protected boolean put(Tuple t, Target l) throws InterruptedException, IOException {
		// doStep();
		return context.put(this, t, l);
	}

	/**
	 * Adds tupe <code>t</code> to knowledge repository located at
	 * <code>l</code>
	 * 
	 * @param t
	 *            knowledge element
	 * @param l
	 *            name of target locality
	 * @return <code>true</code> if the tuple t has been successfully added to
	 *         the knowledge located at l
	 * @throws InterruptedException
	 *             - when another thread interrupt agent computation while
	 *             action is under execution.
	 * @throws IOException
	 */
	protected boolean put(Tuple t, String l) throws InterruptedException, IOException {
		// doStep();
		return put(t, getAddress(l));
	}

	private Target getAddress(String l) {
		return new PointToPoint(l, this.getLocalAddresses().getFirst().getAddress());
	}

	/**
	 * Gets a tuple matching template <code>t</code> from the knoledge
	 * repository located at <code>l</code>.
	 * 
	 * @param t
	 *            knowledge template
	 * @param l
	 *            target locality
	 * @return a tuple matching template <code>t</code>
	 * @throws InterruptedException
	 *             - when another thread interrupt agent computation while
	 *             action is under execution.
	 * @throws IOException
	 */
	protected Tuple get(Template t, Target l) throws InterruptedException, IOException {
		return context.get(this, t, l);
	}

	protected Tuple getp(Template t) {
		return context.getp(this, t);
	}

	protected LinkedList<Tuple> getAll(Template t) {
		return context.getAll(this, t);
	}

	protected Tuple queryp(Template t) {
		return context.queryp(this, t);
	}

	protected LinkedList<Tuple> queryAll(Template t) {
		return context.queryAll(this, t);
	}

	/**
	 * Gets a tuple matching template <code>t</code> from the knoledge
	 * repository located at <code>l</code>.
	 * 
	 * @param t
	 *            knowledge template
	 * @param l
	 *            name of target locality
	 * @return a tuple matching template <code>t</code>
	 * @throws InterruptedException
	 *             - when another thread interrupt agent computation while
	 *             action is under execution.
	 * @throws IOException
	 */
	protected Tuple get(Template t, String l) throws InterruptedException, IOException {
		return get(t, getAddress(l));
	}

	/**
	 * Queries a tuple matching template <code>t</code> from the knoledge
	 * repository located at <code>l</code>.
	 * 
	 * @param t
	 *            knowledge template
	 * @param l
	 *            target locality
	 * @return a tuple matching template <code>t</code>
	 * @throws InterruptedException
	 *             - when another thread interrupt agent computation while
	 *             action is under execution.
	 * @throws IOException
	 */
	protected Tuple query(Template t, Target l) throws InterruptedException, IOException {
		return context.query(this, t, l);
	}

	/**
	 * Queries a tuple matching template <code>t</code> from the knoledge
	 * repository located at <code>l</code>.
	 * 
	 * @param t
	 *            knowledge template
	 * @param l
	 *            name of target locality
	 * @return a tuple matching template <code>t</code>
	 * @throws InterruptedException
	 *             - when another thread interrupt agent computation while
	 *             action is under execution.
	 * @throws IOException
	 */
	protected Tuple query(Template t, String l) throws InterruptedException, IOException {
		return query(t, getAddress(l));
	}

	/**
	 * Returns the list of addresses identifying the node where the agent is
	 * running.
	 * 
	 * @return the list of addresses identifying the node where the agent is
	 *         running.
	 */
	protected LinkedList<PointToPoint> getLocalAddresses() {
		return context.getLocalAddresses();
	}

	// /**
	// * Suspends agent computation.
	// */
	// public void sleep( ) {
	// setState( State.SLEEP );
	// }

	// /**
	// * Stops agent computation.
	// */
	// public void stop() {
	// setState( State.HALT );
	// }

	// /**
	// * Set agent state to <code>state</code>
	// *
	// * @param state new agent state
	// */
	// private synchronized void setState(State state) {
	// this.state = state;
	// notifyObservers();
	// notifyAll();
	// }

	// /**
	// * Checks if an action can be executed or, according to
	// * current state, agent computation is suspended or terminated.
	// *
	// * @throws InterruptedException
	// */
	// protected synchronized void doStep() throws InterruptedException {
	// while (this.state==State.SLEEP) {
	// wait();
	// }
	// switch (state) {
	// case HALT:
	// case DONE:
	// case AWAIT:
	// case ERROR:
	// throw new IllegalStateException();
	// default:
	// //Agent can perform requested action!
	// }
	// }

	// /**
	// * Returns <code>true</code> if the agent is in
	// * sleep state.
	// *
	// * @return <code>true</code> if the agent is in sleep state.
	// */
	// public boolean isSleeping() {
	// return this.state==State.SLEEP;
	// }
	//
	// /**
	// * Returns <code>true</code> if the agent computation has been terminated.
	// *
	// * @return <code>true</code> if the agent computation has been terminated.
	// */
	// public boolean isHalted() {
	// return this.state==State.HALT;
	// }

	/**
	 * Sets the container where agent is executed. This method can be invoked
	 * only when the agent is in state <code>State.AWAIT</code>.
	 * 
	 * @param context
	 *            the context where agent is executed.
	 */
	public synchronized void setContext(int id, AgentContext context) {
		// if (state != State.AWAIT) {
		// throw new IllegalStateException();
		// }
		this.context = context;
		this.id = id;
		// setState(State.READY);
	}

	/**
	 * Returns agent status
	 * 
	 * @return agent status
	 */
	// public State getState() {
	// return state;
	// }

	/**
	 * Executes Agent a.
	 */
	public void call() {
		try {
			doRun();
		} catch (Exception e) {
			doHandle(e);
		}
	}

	/**
	 * This method is used to generate a new fresh identifier.
	 * 
	 * @return a new fresh identifier.
	 * @throws InterruptedException
	 */
	public String fresh() throws InterruptedException {
		return context.fresh(this);
	}

	public void call(Agent a) {
		a.context = this.context;
		a.call();
	}

	public void suspend(long time) throws InterruptedException {
		context.suspend(time);
	}

	public void exec(Agent b) throws InterruptedException {
		if (b != null) {
			context.exec(this, b);
		}
	}

	public boolean updateAttribute(String name, Object value) throws InterruptedException {
		return context.updateAttribute(name, value);
	}

	public Object readAttribute(String name) throws InterruptedException {
		return context.readAttribute(name);
	}

	public <T> T readAttribute(Class<T> clazz, String name) throws InterruptedException {
		Object value = context.readAttribute(name);
		if (clazz.isInstance(value)) {
			return clazz.cast(value);
		}
		return null;
	}

}
