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
package org.cmg.jresp.examples.cloudscenario;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.IsGreaterOrEqualThan;
import org.cmg.jresp.topology.Self;

/**
 * @author loreti
 *
 */
public class ServiceAgent extends Agent {

	public static double BASE_LEVEL = (9.0/18.0);
	public static double STANDARD_LEVEL = (6.0/18.0);
	public static double PREMIUM_LEVEL = (2.0/18.0);	
	public static double SPREMIUM_LEVEL = (1.0/18.0);
	
	private int idx;
	private Scenario scenario;
	private int ensemble = 0;
	
	public ServiceAgent( Scenario scenario , int idx ) {
		this( 0 , scenario , idx );
	}
	
	public ServiceAgent(int ensemble, Scenario scenario, int idx) {
		super( "Agent_"+idx);
		this.idx = idx;
		this.scenario = scenario;
		this.ensemble = ensemble;
	}

	@Override
	protected void doRun() throws Exception {
		
		while (true) {
//			while (scenario.getLoad(idx) == 0) {
////				System.out.println("AGENT "+getName()+" SUSPENDED!");
//				suspend(1);
//			}
			Tuple t = get( 
					new Template( 
							new ActualTemplateField("TASK") , 
							new FormalTemplateField(Integer.class)
					) , 
					getTarget()
//					new Group( new HasValue("LEVEL", scenario.getServiceLevel(idx)) ) 
//					new Group( new IsGreaterOrEqualThan("LEVEL", getLevel()) ) 
			);
//			System.out.println("AGENT "+getName()+": TASK RECEIVED!");
//			final int level = t.getElementAt(Integer.class, 1);
			scenario.beginTaskAt(idx);
			put( new Tuple( "EXECUTE_TASK" ) , Self.SELF );
//			suspend(scenario.getServiceExecutionTime(level));
			scenario.endTaskAt(idx);
			
//			exec( new Agent("Batch_Executor") {
//	
//				@Override
//				protected void doRun() throws Exception {
//					suspend(scenario.getServiceExecutionTime(level));
//					scenario.endTaskAt(idx);
//					System.out.println("AGENT "+getName()+": TASK DONE!");
//				}
//				
//			});
		}
		
	}
	
	
	private Group getTarget() {
		if (ensemble == 0) {//The agent only handles task for the assigned level
			return new Group( new HasValue("LEVEL", scenario.getServiceLevel(idx)) );  
		} else {
			return new Group( new IsGreaterOrEqualThan("LEVEL", getLevel()) ); 
		}
	}
	
	private int getLevel() {
		if (ensemble==2) {
			double load = scenario.getLoad(idx);
			double max_load = scenario.getMaxLoad();
			double p_load = (load-max_load)/max_load;
			if (p_load>=SPREMIUM_LEVEL) {
				return 3;
			}
			if (p_load >= PREMIUM_LEVEL ) {
				return 2;
			}
			if (p_load >= STANDARD_LEVEL) {
				return 1;
			}
			return 0;
		} else {
			return scenario.getServiceLevel(idx);
		}
	}
	

}
