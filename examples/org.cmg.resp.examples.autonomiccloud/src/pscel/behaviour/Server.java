package pscel.behaviour;

import java.io.IOException;
import org.cmg.jresp.behaviour.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.topology.*;

@SuppressWarnings("unused")
public class Server extends Agent {
	
	
	public Server(  ) {
		super("Server");	
	}
	
	@Override
	protected void doRun() throws IOException, InterruptedException{
		Tuple tuple;
		{
			while ((true)) {
				String dest ;
				Integer pId ;
				Integer res ;
				tuple = get( new Template(  new ActualTemplateField(("task")),  new FormalTemplateField(String.class),  new FormalTemplateField(Integer.class) ) ,  new Group(new HasValue( "locality" , ("UNIFI")))
				);
				dest = tuple.getElementAt(String.class,1);
				pId = tuple.getElementAt(Integer.class,2);
				put( new Tuple(  ("result"),  (pId) ) ,  (Self.SELF)
				);
				tuple = get( new Template(  new ActualTemplateField(("result")),  new FormalTemplateField(Integer.class) ) ,  (Self.SELF)
				);
				res = tuple.getElementAt(Integer.class,1);
				put( new Tuple(  ("result"),  (pId),  (res) ) ,  new Group(new HasValue( "id" , (dest)))
				);
			}
		}
	}
}
