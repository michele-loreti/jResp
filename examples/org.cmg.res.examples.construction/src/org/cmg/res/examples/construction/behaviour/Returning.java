package org.cmg.res.examples.construction.behaviour;

import java.io.IOException;

import org.cmg.resp.behaviour.*;
import org.cmg.resp.knowledge.*;
import org.cmg.resp.simulation.SimulationEnvironment;
import org.cmg.resp.topology.*;

public class Returning extends Agent {
	
	
	private SimulationEnvironment env;
	private AverageTimeCollector collector;

	public Returning( SimulationEnvironment env , AverageTimeCollector collector ) {
		super("Returning");	
		this.env = env;
		this.collector = collector;
	}
	
	@Override
	protected void doRun() throws IOException, InterruptedException{
		Tuple tuple;
		{
			while ((true)) {
				tuple = query( new Template(  new ActualTemplateField(("state")),  new ActualTemplateField(("returning")) ) ,  (Self.SELF));
				double time = env.getCurrentTime();
				put( new Tuple( "randomdir") , Self.SELF );
				tuple = query( new Template(  new ActualTemplateField(("beacon")),  new ActualTemplateField((true)) ) ,  (Self.SELF));
				collector.store(env.getCurrentTime()-time);
				put( new Tuple(  ("stop") ) ,  (Self.SELF));
				tuple = get( new Template(  new ActualTemplateField(("state")),  new ActualTemplateField(("returning")) ) ,  (Self.SELF));
				put( new Tuple(  ("state"),  ("entering") ) ,  (Self.SELF));
			}
		}
	}
}
