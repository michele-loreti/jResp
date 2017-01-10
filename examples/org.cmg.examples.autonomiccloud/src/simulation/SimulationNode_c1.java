package simulation;
	
import org.cmg.jresp.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.simulation.*;
import behaviour.*;
import policy.*;
	
@SuppressWarnings("unused")
public class SimulationNode_c1 extends SimulationNode {
	
		public SimulationNode_c1( SimulationEnvironment environment) {
			this( null , environment);
		}
	
		public SimulationNode_c1( RESPElementFactory elementFactory , SimulationEnvironment environment ) {
			super( "c1" , environment );
	
	//Interface Features	
	this.addAttribute(new Attribute("id", ("c1")));
	this.addAttribute(new Attribute("role", ("client")));
	this.addAttribute(new Attribute("level", 1));
	this.addAttribute(new Attribute("locality", ("UNIFI")));
	this.addAttribute(new Attribute("load", 60));
	//Knowledge	
	this.put( new Tuple(  ("taskId"),  0 ) );
	this.addAgent( new Client( ) );
	//Policy
	this.setPolicy(PClient.getSimulationAutomaton());
	}
}