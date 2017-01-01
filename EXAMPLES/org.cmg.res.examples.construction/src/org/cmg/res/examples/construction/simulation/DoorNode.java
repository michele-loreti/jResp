package org.cmg.res.examples.construction.simulation;

import java.io.IOException;

import org.cmg.resp.*;
import org.cmg.resp.behaviour.*;
import org.cmg.resp.knowledge.*;
import org.cmg.resp.knowledge.ts.*;
import org.cmg.resp.simulation.SimulationEnvironment;
import org.cmg.resp.simulation.SimulationNode;
import org.cmg.resp.topology.*;
import org.cmg.resp.comp.*;
import org.cmg.res.examples.construction.behaviour.*;

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
