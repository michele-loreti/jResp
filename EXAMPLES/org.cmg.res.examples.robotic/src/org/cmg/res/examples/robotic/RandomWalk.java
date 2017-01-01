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
import java.util.Random;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Self;

/**
 * @author loreti
 *
 */
public class RandomWalk extends Agent {

	Random r = new Random();
	
	public RandomWalk() {
		super("RandomWalk");
	}

	@Override
	protected void doRun() throws IOException, InterruptedException{
		put( new Tuple( "direction" , r.nextDouble()*2*Math.PI ) , Self.SELF );
		put( new Tuple( "seek" ) , Self.SELF );
	}

}
