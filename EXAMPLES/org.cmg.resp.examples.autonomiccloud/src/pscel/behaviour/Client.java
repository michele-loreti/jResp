package pscel.behaviour;

import java.io.IOException;
import org.cmg.jresp.behaviour.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.topology.*;

@SuppressWarnings("unused")
public class Client extends Agent {
	
	
	public Client(  ) {
		super("Client");	
	}
	
	@Override
	protected void doRun() throws IOException, InterruptedException{
		Tuple tuple;
		{
			Integer i  = 0;
			while (((i))<(5)) {
				put( new Tuple(  ("task"),  (Self.SELF) ) ,  (Self.SELF)
				);
				System.out.println( ("task") );
				i = ((i))+(1);
			}
		}
	}
}
