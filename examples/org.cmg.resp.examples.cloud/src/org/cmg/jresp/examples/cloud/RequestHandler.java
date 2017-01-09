/**
 * 
 */
package org.cmg.jresp.examples.cloud;

import java.util.HashMap;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Address;
import org.cmg.jresp.topology.And;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.IsGreaterOrEqualThan;
import org.cmg.jresp.topology.IsLessThan;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.Target;


/**
 * @author Giorgio
 *
 */
/**
 * Questa classe rappresenta l'agente che gestisce le richieste
 */
public class RequestHandler extends Agent{
	
	//private Address localAddress;
	
	private HashMap<Integer,Object> sessions;
	
	private int counter;
	
	public RequestHandler() {
		super("Request Handler");
		//this.localAddress = localAddress;
	}

	@Override
	protected void doRun() throws Exception {
		System.out.println("REQUEST HANDLER ACTIVATED!");
		while (true) {
		Tuple t = get(
				new Template(
						new ActualTemplateField("REQUEST") , 
						new FormalTemplateField(Integer.class) ,
						new FormalTemplateField(CloudService.class), 
						new FormalTemplateField(PointToPoint.class) 
				)
				,
				Self.SELF
		);

		int sessionId = t.getElementAt(Integer.class , 1 );
		CloudService service = t.getElementAt(CloudService.class, 2);
		Target clientAddress = t.getElementAt(Target.class , 3);
		
		System.out.println("RECEIVED REQUEST: "+service.getName());
		
		Tuple mem_inf = query( CloudComponent.MEMORY_SENSOR_TEMPLATE , Self.SELF);
		Tuple cpu_inf = query( CloudComponent.CPU_SENSOR_TEMPLATE , Self.SELF);
		
		int memValue = mem_inf.getElementAt(Integer.class, 1);
		
		double cpuValue = cpu_inf.getElementAt(Double.class, 1);
		
		
		if((memValue>=500)&&(cpuValue<60.0)){
			exec( new ServiceCaller(sessionId, service, clientAddress));
		}else{
			System.out.println("I'M NOT ABLE TO HANDLE THIS REQUEST!");
			GroupPredicate ot1 = new IsGreaterOrEqualThan("MEMORY", service.getMemory());
			GroupPredicate ot2 = new IsLessThan("CPU_LOAD", 60.0);
			Tuple remoteServer = query( 
				new Template( new ActualTemplateField("LOCATION") , new FormalTemplateField(PointToPoint.class) )
				, new Group(new And(ot1, ot2) )
			);
			put(new Tuple("FORWARD", sessionId , service , clientAddress), remoteServer.getElementAt(PointToPoint.class, 1) );
//			put(new Tuple("FORWARD", sessionId , service , clientAddress), new Group(new And(ot1, ot2) ));
		}
	}
  }
}
