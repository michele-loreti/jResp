/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universita' di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.res.examples.robotic;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Self;

/**
 * @author loreti
 *
 */
public class Informed extends Agent {

	public Informed() {
		super("Informed");
	}

	@Override
	protected void doRun() throws IOException, InterruptedException{
		Tuple t = query(new Template( 
								new ActualTemplateField("targetLocation") , 
								new FormalTemplateField(Double.class) , 
								new FormalTemplateField(Double.class)) , 
						Self.SELF );
		double x = t.getElementAt(Double.class, 1);
		double y = t.getElementAt(Double.class, 2);
		put( new Tuple( "direction" , towards(x , y ) ) , Self.SELF );
		put( new Tuple( "seek" ) , Self.SELF );
	}

	private double towards(double x, double y) throws InterruptedException, IOException {
		Tuple t = query(new Template( 
				new ActualTemplateField("gps") , 
				new FormalTemplateField(Double.class) , 
				new FormalTemplateField(Double.class)) , 
			Self.SELF );
		double xl = t.getElementAt(Double.class, 1);
		double yl = t.getElementAt(Double.class, 2);
		return getAngle(xl,yl,x,y);
	}

	private double getAngle(double x, double y, double xTarget, double yTarget) {
		double dX = (xTarget - x);
		double dY = (yTarget - y);
		if (dX == 0) {
			return (dY>0?Math.PI/2:-Math.PI/2);
		}
		if (dY == 0) {
			return (dX>0?0:Math.PI);			
		}
		
		return (dX<0?Math.PI:0.0)+Math.atan(dY/dX);
	}


}
