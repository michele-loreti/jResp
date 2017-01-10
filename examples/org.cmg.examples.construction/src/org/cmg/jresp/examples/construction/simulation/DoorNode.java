package org.cmg.jresp.examples.construction.simulation;

import java.io.IOException;

import org.cmg.jresp.*;
import org.cmg.jresp.behaviour.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.knowledge.ts.*;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.topology.*;
import org.cmg.jresp.comp.*;
import org.cmg.jresp.examples.construction.behaviour.*;

public class DoorNode extends SimulationNode {

	public DoorNode( SimulationEnvironment environment  , Integer id ) {
		this( null , environment  , id  );
	}

	public DoorNode( RESPElementFactory elementFactory , SimulationEnvironment environment  , Integer id ) {
		super( "D"+id , environment );
		
		//SENSOR: door
		this.addSensor( 
			elementFactory.getSensor( 				
				this.name , 
				"door"  , 
				new Template( new ActualTemplateField(("occupied")),  new FormalTemplateField(Boolean.class) )
			)
		);
		//END SENSOR: door	
			

		
		
		
		this.addAgent( new Door(  (id) ) );
		
	}
				
}
