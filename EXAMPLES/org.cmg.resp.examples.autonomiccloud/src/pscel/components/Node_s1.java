package pscel.components;

import java.io.IOException;
import org.cmg.jresp.*;
import org.cmg.jresp.behaviour.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.knowledge.ts.*;
import org.cmg.jresp.topology.*;
import org.cmg.jresp.comp.*;
import pscel.behaviour.*;
import pscel.policy.*;
import pscel.*;

@SuppressWarnings("unused")
public class Node_s1 extends Node {

	public Node_s1( RESPElementFactory elementFactory ) {
		super( "s1", new TupleSpace() );
//Interface Features	
this.addAttribute(new Attribute("id", ("s1")));
this.addAttribute(new Attribute("role", ("server")));
this.addAttribute(new Attribute("level", 1));
this.addAttribute(new Attribute("locality", ("UNIFI")));
this.addAttribute(new Attribute("load", 60));
//Knowledge	
this.addAgent( new Server( ) );
	//Policy
	this.setPolicy(PServer.getAutomaton());
	}
}
