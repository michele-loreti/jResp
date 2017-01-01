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
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Self;

/**
 * @author loreti
 *
 */
public class TargetSeeker extends Agent {

	public TargetSeeker() {
		super("DataSeeker");
	}

	@Override
	protected void doRun() throws IOException, InterruptedException{
		boolean found = false;
		while (!found) {
			Tuple t = query( new Template(
					 	new ActualTemplateField("lowBattery") , 
					 	new FormalTemplateField(Boolean.class)) , 
				   Self.SELF );
			boolean low = t.getElementAt(Boolean.class,1);
			if (low) {
		        get( new Template( 
	                     new ActualTemplateField( "controlStep" ) ,
	                     new FormalTemplateField( Agent.class ) ) ,
	           Self.SELF );
				put( new Tuple( "controlStep" , new LowBattery() ) , Self.SELF );
				query( new Template( 
					 	new ActualTemplateField("lowBattery") , 
					 	new ActualTemplateField(false)) ,
					 Self.SELF );
			} else {
		        get( new Template( 
	                     new ActualTemplateField( "controlStep" ) ,
	                     new FormalTemplateField( Agent.class ) ) ,
	           Self.SELF );
				t = query( new Template(
				 		new ActualTemplateField("target") , 
				 		new FormalTemplateField(Boolean.class)) , 
				 	  Self.SELF );
				found = t.getElementAt(Boolean.class, 1);
				if (found) {
					put( new Tuple( "stop" ) , Self.SELF );
					put( new Tuple( "controlStep" , new Found() ) , Self.SELF );
                    doTask();
				} else {
					t = query( new Template(
					 		new ActualTemplateField("informed") , 
					 		new FormalTemplateField(Boolean.class)) , 
					 	  Self.SELF );
					boolean informed = t.getElementAt(Boolean.class, 1);
					if (informed) {
						put( new Tuple( "controlStep" , new Informed() ) , Self.SELF );
						get( new Template( new ActualTemplateField("seek") ) , Self.SELF );
					} else {
						put( new Tuple( "controlStep" , new RandomWalk() ) , Self.SELF );
						get( new Template( new ActualTemplateField("seek") ) , Self.SELF );
					}
				}				
			}
		}
	}	
	private void doTask() {
		//This method execute task i...
	}
}
