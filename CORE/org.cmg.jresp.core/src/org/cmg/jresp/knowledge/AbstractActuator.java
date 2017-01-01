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

/**
 * This abstract class identifies an actuator that, associated to a node, can be
 * used to control external components. These components can be either physical
 * devices (like, for instance, the wells of a robot) or software components
 * (like, a software service). Each <code>Actuator</code> is identified by a
 * <i>name</i> and by a <i>template</i>. The former is a mnemonic identifier
 * while the latter identifies the structure of the messages that can be sent to
 * the external component.
 * 
 * 
 * @author Michele Loreti
 *
 */
public abstract class AbstractActuator {

	/**
	 * Actuator name
	 */
	protected String name;

	/**
	 * Creates an instance with name <code>name</code>
	 * 
	 * @param name
	 *            actuator name
	 */
	public AbstractActuator(String name) {
		this.name = name;
	}

	/**
	 * Returns the template used to identify the structure of the messages that
	 * can be sent to the external component.
	 * 
	 * @return the template used to identify the structure of the messages that
	 *         can be sent to the external component.
	 */
	public abstract Template getTemplate();

	/**
	 * Sends tuple <code>t</code> to the external component. Concrete
	 * sub-classes should provide an implementation of this method that
	 * translates tuple <code>t</code> into a valid message for the external
	 * components.
	 * 
	 * @param t
	 *            a tuple containing the data to send to the external component
	 *            associated to the actuator.
	 */
	public abstract void send(Tuple t);

	/**
	 * Returns the name of the actuator
	 * 
	 * @return the name of the actuator
	 */
	public String getName() {
		return name;
	}

}
