package org.cmg.res.examples.construction.behaviour;

import java.io.IOException;

import org.cmg.resp.behaviour.*;
import org.cmg.resp.knowledge.*;
import org.cmg.resp.simulation.SimulationEnvironment;
import org.cmg.resp.topology.*;

public class Foraging extends Agent {
	
	
	private SimulationEnvironment env;
	private AverageTimeCollector collector;
	
	public Foraging( SimulationEnvironment env , AverageTimeCollector collector) {
		super("Foraging");	
		this.env = env;
		this.collector = collector;
	}
	
	@Override
	protected void doRun() throws IOException, InterruptedException{
		Tuple tuple;
		{
			while ((true)) {
				tuple = query( new Template(  new ActualTemplateField(("state")),  new ActualTemplateField(("foraging")) ) ,  (Self.SELF));
				double time = env.getCurrentTime();
				tuple = query( new Template(  new ActualTemplateField(("food")),  new ActualTemplateField((true)) ) ,  (Self.SELF));
				collector.store(env.getCurrentTime()-time);
//				System.out.println("FOUND!");
				put( new Tuple(  ("stop") ) ,  (Self.SELF));
				put( new Tuple(  ("collect") ) ,  (Self.SELF));
				tuple = get( new Template(  new ActualTemplateField(("state")),  new ActualTemplateField(("foraging")) ) ,  (Self.SELF));
				put( new Tuple(  ("state"),  ("returning") ) ,  (Self.SELF));
			}
		}
	}
}
