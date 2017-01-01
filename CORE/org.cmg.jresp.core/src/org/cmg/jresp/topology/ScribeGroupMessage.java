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
package org.cmg.jresp.topology;

import org.cmg.jresp.protocol.jRESPMessage;

import rice.p2p.commonapi.Id;
import rice.p2p.scribe.ScribeContent;

/**
 * @author Michele Loreti
 *
 */
public class ScribeGroupMessage implements ScribeContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Id from;
	protected jRESPMessage message;

	public ScribeGroupMessage(Id from, jRESPMessage message) {
		this.from = from;
		this.message = message;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the from
	 */
	public Id getFrom() {
		return from;
	}

	/**
	 * @return the message
	 */
	public jRESPMessage getMessage() {
		return message;
	}

}
