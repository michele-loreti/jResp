package org.cmg.jresp.examples.construction.behaviour;

import java.io.IOException;
import org.cmg.jresp.behaviour.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.topology.*;

public class Door extends Agent {
	
	private Integer id;
	
	public Door( Integer id ) {
		super("Door");	
		this.id = id;
	}
	
	@Override
	protected void doRun() throws IOException, InterruptedException{
		Tuple tuple;
		{
			while ((true)) {
				put( new Tuple(  ("door"),  (id) ) ,  (Self.SELF));
				tuple = query( new Template(  new ActualTemplateField(("door")),  new ActualTemplateField((true)) ) ,  (Self.SELF));
				tuple = query( new Template(  new ActualTemplateField(("door")),  new ActualTemplateField((false)) ) ,  (Self.SELF));
			}
		}
	}
}
