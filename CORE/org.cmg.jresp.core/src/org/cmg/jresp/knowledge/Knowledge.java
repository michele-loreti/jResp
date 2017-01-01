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

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * This class handles the knowledge installed in a SCEL component.
 * 
 * 
 * @author Michele Loreti
 *
 */
public class Knowledge extends Observable {

	/**
	 * A reference to a <code>KnowledgeManager</code>.
	 */
	protected KnowledgeManager knowledgeManager;

	/**
	 * A list of <code>KnowledgeAdapter</code>s. These adapters can be used to
	 * include external knowledge repositories in the knowledge.
	 */
	protected LinkedList<KnowledgeAdapter> adapters;

	/**
	 * A list of <code>AbstractSensors</code>. These objects let available
	 * knowledge elements retrieved from external sources.
	 */
	protected LinkedList<AbstractSensor> sensors;

	/**
	 * A lit of <code>AbstractActuators</code>. These objects are used to
	 * transmit knowledge items to actuators so to control external components.
	 */
	protected LinkedList<AbstractActuator> actuators;

	public Knowledge(KnowledgeManager knowledgeMangaer, KnowledgeAdapter... adapters) {
		this.knowledgeManager = knowledgeMangaer;
		this.adapters = new LinkedList<KnowledgeAdapter>();
		for (KnowledgeAdapter knowledgeAdapter : adapters) {
			this.adapters.add(knowledgeAdapter);
		}
		this.sensors = new LinkedList<AbstractSensor>();
		this.actuators = new LinkedList<AbstractActuator>();
		this.adapters = new LinkedList<KnowledgeAdapter>();
	}

	public boolean put(Tuple t) {
		boolean result;
		if (!putToActuator(t)) {
			KnowledgeAdapter knowledgeAdapter = getAdapterFor(t);
			if (knowledgeAdapter != null) {
				result = knowledgeAdapter.put(t);
			} else {
				result = this.knowledgeManager.put(t);
			}
		} else {
			result = true;
		}
		setChanged();
		notifyObservers(t);
		return result;
	}

	private boolean putToActuator(Tuple t) {
		for (AbstractActuator a : this.actuators) {
			if (a.getTemplate().match(t)) {
				a.send(t);
				return true;
			}
		}
		return false;
	}

	public Tuple get(Template t) throws InterruptedException {
		Tuple result;
		KnowledgeAdapter knowledgeAdapter = getAdapterFor(t);
		if (knowledgeAdapter != null) {
			result = knowledgeAdapter.get(t);
		} else {
			result = this.knowledgeManager.get(t);
		}
		setChanged();
		notifyObservers(result);
		return result;
	}

	public Tuple getp(Template t) {
		Tuple result;
		KnowledgeAdapter knowledgeAdapter = getAdapterFor(t);
		if (knowledgeAdapter != null) {
			result = knowledgeAdapter.getp(t);
		} else {
			result = this.knowledgeManager.getp(t);
		}
		if (result != null) {
			setChanged();
			notifyObservers(result);
		}
		return result;
	}

	public Tuple query(Template t) throws InterruptedException {
		Tuple result = queryFromSensors(t, true);
		if (result != null) {
			return result;
		}
		KnowledgeAdapter knowledgeAdapter = getAdapterFor(t);
		if (knowledgeAdapter != null) {
			result = knowledgeAdapter.query(t);
		} else {
			result = this.knowledgeManager.query(t);
		}
		return result;
	}

	public Tuple queryp(Template t) {
		Tuple result = null;
		try {
			result = queryFromSensors(t, false);
		} catch (InterruptedException e) {
			// This exception cannot be raised!
		}
		if (result != null) {
			return result;
		}
		KnowledgeAdapter knowledgeAdapter = getAdapterFor(t);
		if (knowledgeAdapter != null) {
			result = knowledgeAdapter.queryp(t);
		} else {
			result = this.knowledgeManager.queryp(t);
		}
		return result;
	}

	private Tuple queryFromSensors(Template template, boolean blocking) throws InterruptedException {
		for (AbstractSensor s : this.sensors) {
			Tuple result = s.getValue(template, blocking);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	private synchronized KnowledgeAdapter getAdapterFor(Tuple t) {
		for (KnowledgeAdapter knowledgeAdapter : this.adapters) {
			if (knowledgeAdapter.isResponsibleFor(t)) {
				return knowledgeAdapter;
			}
		}
		return null;
	}

	private synchronized KnowledgeAdapter getAdapterFor(Template t) {
		for (KnowledgeAdapter knowledgeAdapter : this.adapters) {
			if (knowledgeAdapter.isResponsibleFor(t)) {
				return knowledgeAdapter;
			}
		}
		return null;
	}

	public synchronized void addSensor(final AbstractSensor s) {
		this.sensors.add(s);
		s.addObserver(new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				setChanged();
				notifyObservers();
			}

		});
	}

	public synchronized void addActuator(AbstractActuator a) {
		this.actuators.add(a);
	}

	public synchronized void addKnowledgeAdapter(KnowledgeAdapter adapter) {
		this.adapters.add(adapter);
	}

	public AbstractActuator[] getActuators() {
		return actuators.toArray(new AbstractActuator[sensors.size()]);
	}

	public AbstractSensor[] getSensors() {
		return sensors.toArray(new AbstractSensor[sensors.size()]);
	}

	public LinkedList<Tuple> queryAll(Template t) {
		return knowledgeManager.queryAll(t);
	}

	public LinkedList<Tuple> getAll(Template t) {
		return knowledgeManager.getAll(t);
	}

}
