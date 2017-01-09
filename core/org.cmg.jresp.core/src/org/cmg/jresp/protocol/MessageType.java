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
package org.cmg.jresp.protocol;

/**
 * Enumeration for message types.
 * 
 * @author Michele Loreti
 *
 */
public enum MessageType {
	ATTRIBUTE_REQUEST(0, "A_REQUEST"), ATTRIBUTE_REPLY(1, "A_REPLY"), PUT_REQUEST(2, "P_REQUEST"), GET_REQUEST(3,
			"G_REQUEST"), QUERY_REQUEST(4, "Q_REQUEST"), TUPLE_REPLY(5, "T_REPLY"), GROUP_PUT_REQUEST(6,
					"GP_REQUEST"), GROUP_GET_REQUEST(7, "GG_REQUEST"), GROUP_QUERY_REQUEST(8,
							"GQ_REQUEST"), GROUP_PUT_REPLY(9, "GP_REPLY"), GROUP_GET_REPLY(10,
									"GG_REPLY"), GROUP_QUERY_REPLY(11, "GQ_REPLY"), ACK(12, "ACK"), FAIL(13, "FAIL");

	private int code;
	private String string;

	MessageType(int code, String string) {
		this.code = code;
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

	public int code() {
		return code;
	}
}