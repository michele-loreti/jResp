/**
 * 
 */
package org.cmg.jresp.examples.cloud;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Address;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.Target;


/**
 * @author Giorgio
 *
 */
public class OfferAgent extends Agent{

	public OfferAgent() {
		super("OFFER AGENT");
	}

	@Override
	protected void doRun() throws Exception {
		while(true){
			Tuple sos=get(new Template(new ActualTemplateField("FORWARD"), new FormalTemplateField(Integer.class), new FormalTemplateField(CloudService.class), new FormalTemplateField(Target.class)), Self.SELF);
			CloudService service=sos.getElementAt(CloudService.class, 2);
			int sessionId= sos.getElementAt(Integer.class, 1);
			Target clientAddress=sos.getElementAt(Target.class, 3);
			//put(new Tuple("OFFER", localAddress), Self.SELF);
			//Tuple answer= get(new Template(new ActualTemplateField("REPLY"), new FormalTemplateField(Address.class), new FormalTemplateField(Integer.class)), Self.SELF);
			//if((Integer)answer.getElementAt(1)==1){
				exec(new ServiceCaller(sessionId, service, clientAddress));
			//}
		}
	}

}
