/**
 * Copyright (c) 2014 Concurrency and Mobility Group.
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
package org.cmg.jresp.policy;

import java.util.HashMap;
import java.util.Map.Entry;

import org.cmg.jresp.exceptions.MissingAttributeException;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.topology.Target;

/**
 * @author Michele Loreti
 * @author Andrea Margheri
 * 
 */
public class AuthorizationRequest {

	private String subjectId;

	/**
	 * The value of the attributes organized under the category/identifier name
	 */
	private HashMap<RequestAttributeName, Object> attributes;

	public AuthorizationRequest() {
		this.attributes = new HashMap<RequestAttributeName, Object>();
	}

	public AuthorizationRequest(String subject, String object, ActionThisID action, Object actionArgument,
			Target destSubject, Attribute[] subInterface, Attribute[] objInterface) {
		this.subjectId = subject;
		// this.action = action;
		// this.actionArgument = actionArgument;
		// this.destinationSubject = destSubject;
		/*
		 * Generate the correct representation for attributes by using the
		 * structured-names
		 */
		this.attributes = new HashMap<RequestAttributeName, Object>();
		/*
		 * Action Attributes
		 */
		// this.attributes.put(new StructName("action", "dest"),
		// destinationSubject);
		this.attributes.put(new RequestAttributeName("action", "id"), action);
		this.attributes.put(new RequestAttributeName("action", "arg"), actionArgument);
		/*
		 * Subject and Action identifier
		 */
		this.attributes.put(new RequestAttributeName("subject", "id"), subject);
		this.attributes.put(new RequestAttributeName("object", "id"), object);

		// add all the attributes forming the interface
		/*
		 * Subject Interface Attributes
		 */
		for (int i = 0; i < subInterface.length; i++) {
			this.attributes.put(new RequestAttributeName("subject", subInterface[i].getName()),
					subInterface[i].getValue());
		}
		/*
		 * Object Interface Attributes
		 */
		for (int i = 0; i < objInterface.length; i++) {
			this.attributes.put(new RequestAttributeName("object", objInterface[i].getName()),
					objInterface[i].getValue());
		}
	}

	/**
	 * Given a structured name returns the value of the corresponding attribute
	 * 
	 * @param Attribute
	 *            Name
	 * @return Object attribute value
	 */
	public Object getAttributeValue(RequestAttributeName req) throws MissingAttributeException {
		for (Entry<RequestAttributeName, Object> i : this.attributes.entrySet()) {
			if (i.getKey().equals(req)) {
				return i.getValue();
			}
		}
		throw new MissingAttributeException(req.toString());
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append(this.attributes.get(new RequestAttributeName("action", "id")).toString() + " ("
				+ this.attributes.get(new RequestAttributeName("action", "arg")).toString() + ")@dest"
		// this.destinationSubject.toString()
		);
		return str.toString();
	}

	public String getSubjectId() {
		return subjectId;
	}

}
