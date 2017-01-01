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
package org.cmg.resp.examples.mobility;

import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.AnyComponent;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.GroupPredicate;
import org.cmg.jresp.topology.Self;

/**
 * @author Michele Loreti
 *
 */
public class CommunicationAgent extends Agent {

	public static GroupPredicate any = new AnyComponent();

	private Template informedTemplate = new Template(new ActualTemplateField("INFORMED"),
			new FormalTemplateField(Boolean.class));

	private Template directionTemplate = new Template(new ActualTemplateField("DIRECTION"),
			new FormalTemplateField(Double.class), new FormalTemplateField(Double.class));

	public CommunicationAgent(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.scel.behaviour.Agent#doRun()
	 */
	@Override
	protected void doRun() {
		try {
			Tuple t = query(directionTemplate, new Group(any));
			put(t, Self.SELF);
			get(informedTemplate, Self.SELF);
			put(new Tuple("INFORMED", true), Self.SELF);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
