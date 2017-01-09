package org.cmg.res.examples.construction.behaviour;

import java.io.IOException;
import org.cmg.resp.behaviour.*;
import org.cmg.resp.knowledge.*;
import org.cmg.resp.topology.*;

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
