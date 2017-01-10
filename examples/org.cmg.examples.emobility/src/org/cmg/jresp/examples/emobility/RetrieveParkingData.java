/**
 * Copyright (c) 2012 Sysma Group.
 * IMT Institute for Advanced Studies Lucca
 * Universit� di Firenze
 * 
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 *      Francesco Tiezzi
 *      Andrea Vandin
 */
package org.cmg.jresp.examples.emobility;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.Target;

/**
 * @author loreti
 *
 */
public class RetrieveParkingData extends Agent {

	public RetrieveParkingData(String name) {
		super(name);
	}

	@Override
	protected void doRun() throws Exception {
		Tuple t = query(new Template( new ActualTemplateField("CALENDAR_SIZE") , new FormalTemplateField(Integer.class)), Self.SELF);
		int calendarSize = t.getElementAt(Integer.class, 1);
		for( int i=0 ; i<calendarSize ; i++ ) {
			Tuple calendarEntry = query( 
					new Template( 
							new ActualTemplateField("CALENDAR") , 
							new ActualTemplateField(i) , // entry id
							new FormalTemplateField(Double.class) , // POI x coordinate
							new FormalTemplateField(Double.class) , // POI y coordinate
							new FormalTemplateField(Double.class) , // when
							new FormalTemplateField(Double.class) ) , // how long
					Self.SELF);
			double x = calendarEntry.getElementAt(Double.class, 2);
			double y = calendarEntry.getElementAt(Double.class, 3);
			put( new Tuple( "SEARCH_PLOT" , Self.SELF , i ) , new Group( new GroupPredi) );
		}
	}
	
	

}
