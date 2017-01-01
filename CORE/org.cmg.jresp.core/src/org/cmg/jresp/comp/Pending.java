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

/**
 * @author Michele Loreti
 *
 */
public class Pending<T> {

	protected T value;
	protected boolean error = false;

	public synchronized T get() throws InterruptedException {
		return get(true);
	}

	public synchronized T get(boolean waiting) throws InterruptedException {
		while (waiting && (value == null) && (!error)) {
			wait();
		}
		return value;
	}

	public synchronized void set(T value) {
		this.value = value;
		notify();
	}

	public synchronized void fail() {
		this.error = true;
		notify();
	}

	public boolean isError() {
		return error;
	}

}
