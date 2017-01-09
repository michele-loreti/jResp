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
package org.cmg.res.examples.robotic;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Self;

/**
 * @author loreti
 *
 */
public class ManagedElement extends Agent {

	public ManagedElement() {
		super("ManagedElement");
	}

	@Override
	protected void doRun() throws Exception {
		while (true) {
//			Thread.sleep(1000);
			Tuple t = query(new Template( 
									new ActualTemplateField("controlStep") , 
									new FormalTemplateField(Agent.class)) , 
							Self.SELF );
			Agent X = t.getElementAt(Agent.class, 1);
			call(X);
		}
	}
		
}
