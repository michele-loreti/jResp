package simulation;
	
import org.cmg.jresp.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.simulation.*;
import behaviour.*;
import policy.*;
	
@SuppressWarnings("unused")
public class SimulationNode_s1 extends SimulationNode {
	
		public SimulationNode_s1( SimulationEnvironment environment) {
			this( null , environment);
		}
	
		public SimulationNode_s1( RESPElementFactory elementFactory , SimulationEnvironment environment ) {
			super( "s1" , environment );
	
	//Interface Features	
	this.addAttribute(new Attribute("id", ("s1")));
	this.addAttribute(new Attribute("role", ("server")));
	this.addAttribute(new Attribute("level", 1));
	this.addAttribute(new Attribute("locality", ("UNIFI")));
	this.addAttribute(new Attribute("load", 60));
	//Knowledge	
	this.addAgent( new Server( ) );
	//Policy
	this.setPolicy(PServer.getSimulationAutomaton());
	}
}
