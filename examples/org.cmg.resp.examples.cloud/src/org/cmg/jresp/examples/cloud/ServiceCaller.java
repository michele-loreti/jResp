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
package org.cmg.jresp.examples.cloud;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.Target;

/**
 * @author loreti
 *
 */
public class ServiceCaller extends Agent {
	
	private CloudService service;

	private int sessionId;

	private Target clientAddress;
	
	public ServiceCaller( int sessionId , CloudService service , Target clientAddress ) {
		super("CALLER");
		this.service = service;
		this.sessionId = sessionId;
		this.clientAddress = clientAddress;
	}

	@Override
	protected void doRun() throws Exception {
		System.out.println("Executing: "+service.getName());
		put( new Tuple("SERVICE_CALL" , sessionId , service) , Self.SELF );
		get(
			new Template( 
				new ActualTemplateField("RESULT") , 
				new ActualTemplateField(sessionId)
			) 
			, Self.SELF
		);
		System.out.println("Done: "+service.getName());
		put( new Tuple("DONE" , sessionId) , clientAddress );
	}

}
