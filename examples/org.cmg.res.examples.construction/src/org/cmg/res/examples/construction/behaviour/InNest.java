package org.cmg.res.examples.construction.behaviour;

import java.io.IOException;

import org.cmg.resp.behaviour.*;
import org.cmg.resp.knowledge.*;
import org.cmg.resp.simulation.SimulationEnvironment;
import org.cmg.resp.topology.*;

public class InNest extends Agent {
	
	
	private SimulationEnvironment environment;
	private AverageTimeCollector collector;

	public InNest(SimulationEnvironment environment, AverageTimeCollector inOutTime  ) {
		super("InNest");	
		this.environment = environment;
		this.collector = inOutTime;
	}
	
	@Override
	protected void doRun() throws IOException, InterruptedException{
		Tuple tuple;
		{
			while ((true)) {
				tuple = query( new Template(  new ActualTemplateField(("state")),  new ActualTemplateField(("innest")) ) ,  (Self.SELF));
				double time = environment.getCurrentTime();
				tuple = query( new Template(  new ActualTemplateField(("nest")),  new ActualTemplateField((true)) ) ,  (Self.SELF));
				put( new Tuple(  ("stop") ) ,  (Self.SELF));
				put( new Tuple(  ("release") ) ,  (Self.SELF));
				put( new Tuple( ("exit") ) , (Self.SELF) );
				collector.store(environment.getCurrentTime()-time);
				tuple = get( new Template(  new ActualTemplateField(("state")),  new ActualTemplateField(("innest")) ) ,  (Self.SELF));
				put( new Tuple(  ("state"),  ("foraging") ) ,  (Self.SELF));
			}
		}
	}
}
