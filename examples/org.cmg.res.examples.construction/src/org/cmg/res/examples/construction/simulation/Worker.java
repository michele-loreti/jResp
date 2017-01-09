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

public class Worker extends SimulationNode {

	public Worker( SimulationEnvironment environment  , String n ,
			AverageTimeCollector foodTime , 
			AverageTimeCollector toRestTime ,
			AverageTimeCollector waitingTime ,
			AverageTimeCollector inOutTime ) {
		this( null , environment  , n , foodTime , toRestTime , waitingTime , inOutTime );
	}

	public Worker( RESPElementFactory elementFactory , 
			SimulationEnvironment environment  , 
			String n ,
			AverageTimeCollector foodTime , 
			AverageTimeCollector toRestTime ,
			AverageTimeCollector waitingTime ,
			AverageTimeCollector inOutTime 
		) {
		super( (n).toString() , environment );
		
		
		//SENSOR: food
		this.addSensor( 
			elementFactory.getSensor( 				
				this.name , 
				"food"  , 
				new Template( new ActualTemplateField(("food")),  new FormalTemplateField(Boolean.class) )
			)
		);
		//END SENSOR: food	
			
		
		//SENSOR: collision
		this.addSensor( 
			elementFactory.getSensor( 				
				this.name , 
				"collision"  , 
				new Template( new ActualTemplateField(("collision")),  new FormalTemplateField(Boolean.class) )
			)
		);
		//END SENSOR: collision	
			
		
		//SENSOR: beacon
		this.addSensor( 
			elementFactory.getSensor( 				
				this.name , 
				"beacon"  , 
				new Template( new ActualTemplateField(("beacon")),  new FormalTemplateField(Boolean.class) )
			)
		);
		//END SENSOR: beacon	
			
		//SENSOR: nest
		this.addSensor( 
			elementFactory.getSensor( 				
				this.name , 
				"nest"  , 
				new Template( new ActualTemplateField(("nest")),  new FormalTemplateField(Boolean.class) )
			)
		);
		//END SENSOR: nest	

		
		//ACTUATOR: direction
		this.addActuator( 
			elementFactory.getActuator( 				
				this.name , 
				"direction"  , 
				new Template( new ActualTemplateField(("direction")),  new FormalTemplateField(Double.class) )
			)
		);
		//END ACTUATOR: direction	
			
		
		//ACTUATOR: grip
		this.addActuator( 
			elementFactory.getActuator( 				
				this.name , 
				"grip"  , 
				new Template( new ActualTemplateField(("collect")) )
			)
		);
		//END ACTUATOR: grip	
			
		
		//ACTUATOR: release
		this.addActuator( 
			elementFactory.getActuator( 				
				this.name , 
				"release"  , 
				new Template( new ActualTemplateField(("release")) )
			)
		);
		//END ACTUATOR: release	
			
		
		//ACTUATOR: stop
		this.addActuator( 
			elementFactory.getActuator( 				
				this.name , 
				"stop"  , 
				new Template( new ActualTemplateField(("stop")) )
			)
		);
		//END ACTUATOR: stop	
			
		
		//ACTUATOR: randomdir
		this.addActuator( 
			elementFactory.getActuator( 				
				this.name , 
				"randomdir"  , 
				new Template( new ActualTemplateField(("randomdir")) )
			)
		);
		//END ACTUATOR: randomdir	

		//ACTUATOR: exit
		this.addActuator( 
			elementFactory.getActuator( 				
				this.name , 
				"exit"  , 
				new Template( new ActualTemplateField(("exit")) )
			)
		);
		//END ACTUATOR: exit	

		
		
		this.put( new Tuple(  ("state"),  ("foraging") ) );
		
		
		this.addAgent(new Behaviour(environment, foodTime, toRestTime, inOutTime));
//		this.addAgent( new Foraging( environment , foodTime ) );
//		this.addAgent( new Returning( environment , toRestTime ) );
//		this.addAgent( new Entering( environment , waitingTime ) );
//		this.addAgent( new InNest( environment , inOutTime ) );
		this.addAgent( new RandomWalk( ) );
	}
				
}
