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

import java.util.Observable;

/**
 * @author Michele Loreti
 *
 */
public abstract class AbstractSensor extends Observable {

	protected String name;

	protected Tuple value;

	protected Template template;

	public AbstractSensor(String name, Template template) {
		this.name = name;
		this.template = template;
	}

	public String getName() {
		return name;
	}

	public synchronized final Tuple getValue(Template t) throws InterruptedException {
		return getValue(t, true);
	}

	public synchronized final Tuple getValue(Template t, boolean blocking) throws InterruptedException {
		if ((value != null) && (t.match(value))) {
			return value;
		}
		if (!blocking) {
			return null;
		}
		if (template.implies(t)) {
			while ((value == null) || (!t.match(value))) {
				wait();
			}
			return value;
		}
		return null;
	}

	public synchronized final void setValue(Tuple t) {
		if (!template.match(t)) {
			throw new IllegalArgumentException();
		}
		if (((value == null) && (t != null)) || (!value.equals(t))) {
			this.value = t;
			this.setChanged();
			this.notifyObservers(t);
		}
		this.notifyAll();
	}

	public Template getTemplate() {
		return template;
	}

}
