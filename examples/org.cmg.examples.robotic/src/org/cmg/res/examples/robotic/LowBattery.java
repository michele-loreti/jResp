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

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.Self;

/**
 * @author loreti
 *
 */
public class LowBattery extends Agent {

	public LowBattery() {
		super("LowBattery");
	}

	@Override
	protected void doRun() throws IOException, InterruptedException{
		put( new Tuple( "stop" ) , Self.SELF );
		Tuple t = query(new Template( 
				new ActualTemplateField("gps") , 
				new FormalTemplateField(Double.class) , 
				new FormalTemplateField(Double.class)) , 
			Self.SELF );
		double x = t.getElementAt(Double.class, 1);
		double y = t.getElementAt(Double.class, 2);
		put( new Tuple( "sos" , x , y ) , Self.SELF );// new Group(new HasValue( "task" , 1 ) ) );	
	}
}
	
