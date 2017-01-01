/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universit? di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.jresp.examples.disaster.rescuer.pscel;

import java.io.IOException;
import java.util.Random;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Self;

/**
 * @author Michele Loreti
 * @author Andrea Margheri
 */
public class RandomWalk extends Agent {

	Random r = new Random();

	@SuppressWarnings("unused")
	private int robotId;

	public RandomWalk(int robotId) {
		super("RandomWalk");
		this.robotId = robotId;
	}

	@Override
	protected void doRun() throws IOException, InterruptedException {
		while (true) {

			double dir = r.nextDouble() * 2 * Math.PI;
			put(new Tuple("direction", dir), Self.SELF);

			query(new Template(new ActualTemplateField("COLLISION"), new ActualTemplateField(true)), Self.SELF);

		}
	}

}
