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
package org.cmg.jresp.examples.bikes;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.And;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.IsGreaterOrEqualThan;
import org.cmg.jresp.topology.IsLessOrEqualThan;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.Target;

/**
 * @author loreti
 *
 */
public class UserProcess extends Agent {

	private Scenario scenario;
	private int currentX;
	private int currentY;

	public UserProcess(String name , Scenario scenario , int currentX , int currentY ) {
		super(name);
		this.scenario = scenario;
		this.currentX = currentX;
		this.currentY = currentY;
	}

	@Override
	protected void doRun() throws Exception {
		Tuple t;
		while( true ) {
			t = get( Scenario.NEXT_LOCATION , Self.SELF );
			int targetX = t.getElementAt(Integer.class, 0);
			int targetY = t.getElementAt(Integer.class, 1);
//			int targetX = (currentX==0?2:0);//t.getElementAt(Integer.class, 0);
//			int targetY = (currentY==0?2:0);//t.getElementAt(Integer.class, 1);
			t = get( Scenario.RESERVE_BIKE , closeTo( currentX , currentY ) );
			put( new Tuple( "WALK" , t.getElementAt(Integer.class, 0) , t.getElementAt(Integer.class, 1) ) , Self.SELF );
			get( Scenario.GET_BIKE , scenario.stationAt( t.getElementAt(Integer.class, 0) , t.getElementAt(Integer.class, 1) ) );
			put( new Tuple( "RIDE" , targetX , targetY ) , Self.SELF );
			t = get( Scenario.RESERVE_SLOT , closeTo( targetX , targetY ) );
			put( new Tuple( "RIDE" , t.getElementAt(Integer.class, 0) , t.getElementAt(Integer.class, 1) ) , Self.SELF );
			put( Scenario.LEAVE_BIKE , scenario.stationAt( t.getElementAt(Integer.class, 0) , t.getElementAt(Integer.class, 1) ) );			
			put( new Tuple( "WALK" , targetX , targetY ) , Self.SELF );
			currentX = targetX;
			currentY = targetY;
		}
	}
	
	
	protected Target closeTo( int x , int y ) {
		return new Group( new And(  
				new And( 
						new IsLessOrEqualThan("X", x+1) , 
						new IsGreaterOrEqualThan("X", x-1)
				) , 
				new And(						
						new IsLessOrEqualThan("Y", y+1) , 
						new IsGreaterOrEqualThan("Y", y-1)
				) 
		));
	}

	/* (non-Javadoc)
	 * @see org.cmg.jresp.behaviour.Agent#doHandle(java.lang.Exception)
	 */
	@Override
	protected void doHandle(Exception e) {
//		e.printStackTrace();
	}

}
