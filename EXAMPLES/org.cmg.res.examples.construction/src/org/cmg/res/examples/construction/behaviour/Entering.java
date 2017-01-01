package org.cmg.res.examples.construction.behaviour;

import java.io.IOException;

import org.cmg.resp.behaviour.*;
import org.cmg.resp.knowledge.*;
import org.cmg.resp.simulation.SimulationEnvironment;
import org.cmg.resp.topology.*;

public class Entering extends Agent {
	
	
	private SimulationEnvironment env;
	private AverageTimeCollector collector;

	public Entering(  SimulationEnvironment env , AverageTimeCollector collector ) {
		super("Entering");	
		this.env = env;
		this.collector = collector;
	}
	
	@Override
	protected void doRun() throws IOException, InterruptedException{
		Tuple tuple;
		{
			while ((true)) {
				Integer id ;
				tuple = query( new Template(  new ActualTemplateField(("state")),  new ActualTemplateField(("entering")) ) ,  (Self.SELF));
				double time = env.getCurrentTime();
				tuple = get( new Template(  new ActualTemplateField(("door")),  new FormalTemplateField(Integer.class) ) ,  new Group( new AnyComponent() ));
				id = tuple.getElementAt(Integer.class,1);
				put( new Tuple(  ("direction"),  id ) ,  (Self.SELF));
				collector.store(env.getCurrentTime()-time);
				tuple = get( new Template(  new ActualTemplateField(("state")),  new ActualTemplateField(("entering")) ) ,  (Self.SELF));
				put( new Tuple(  ("state"),  ("innest") ) ,  (Self.SELF));
			}
		}
	}
}
