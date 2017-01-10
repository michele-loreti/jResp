package org.cmg.jresp.examples.construction.behaviour;

import java.io.IOException;
import org.cmg.jresp.behaviour.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.topology.*;

public class RandomWalk extends Agent {
	
	
	public RandomWalk(  ) {
		super("RandomWalk");	
	}
	
	@Override
	protected void doRun() throws IOException, InterruptedException{
		Tuple tuple;
		{
			while ((true)) {
				put( new Tuple(  ("randomdir") ) ,  (Self.SELF));
				tuple = query( new Template(  new ActualTemplateField(("collision")),  new ActualTemplateField((true)) ) ,  (Self.SELF));
			}
		}
	}
}
