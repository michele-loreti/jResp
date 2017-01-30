/**
 * Copyright (c) 2014 Concurrency and Mobility Group.
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
package org.cmg.jresp.policy.facpl;

import java.io.IOException;
import java.util.List;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Target;

/**
 * @author Andrea Margheri
 * @author Michele Loreti
 *
 */
public class FACPLObligationExecutor extends Agent {

	private List<FulfilledObligation> obls;

	private String id;

	public FACPLObligationExecutor(List<FulfilledObligation> obls, String id) {
		super("ObligationExecutor");
		this.obls = obls;
		this.id = id;
	}

	@Override
	protected void doRun() throws Exception {
		for (FulfilledObligation o : obls) {
			execute(o);
		}
	}

	public String getId() {
		return id;
	}

	/**
	 * Given a Fulfilled Obligation execute the corresponding action
	 * 
	 * @param o
	 *            FulfilledObligation returned by the policy evaluation
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void execute(FulfilledObligation o) throws Exception {
		switch (o.getActionId()) {
		case GET:
			// System.out.println("Robot "+ this.id +"Obligation - Eseguita
			// azione GET");
			get((Template) o.getArguments().get(0), (Target) o.getArguments().get(1));
			break;
		case QRY:
			// System.out.println("Robot "+ this.id +"Obligation - Eseguita
			// azione QUERY");
			query((Template) o.getArguments().get(0), (Target) o.getArguments().get(1));
			break;
		case PUT:
			// System.out.println("Robot "+ this.id +"Obligation - Eseguita
			// azione PUT :" + ((Tuple)o.getArguments().get(0)).toString() );
			put((Tuple) o.getArguments().get(0), (Target) o.getArguments().get(1));
			break;
		case UPD:
			updateAttribute((String) o.getArguments().get(0), o.getArguments().get(1));
			break;
		case READ:
			readAttribute((String) o.getArguments().get(0));
			break;
		case EXEC:

			/*
			 * TODO
			 */

		default:
			// System.out.println("Obligation cannot be executed");
			System.err.println("Obligation " + o.getActionId() + " Unsupported");
			// throw new Exception("Obligation cannot be executed");
			break;
		}

	}

}
