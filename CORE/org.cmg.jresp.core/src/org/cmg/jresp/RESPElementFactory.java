/**
 * Copyright (c) 2015 Concurrency and Mobility Group.
 * Università di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.jresp;

import org.cmg.jresp.knowledge.AbstractActuator;
import org.cmg.jresp.knowledge.AbstractSensor;
import org.cmg.jresp.knowledge.Template;

/**
 * @author Michele Loreti
 *
 */
public interface RESPElementFactory {

	public AbstractSensor getSensor(String nodeName, String sensorName, Template pattern);

	public AbstractActuator getActuator(String nodeName, String attributeName, Template pattern);

}
