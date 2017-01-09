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
package org.cmg.resp.examples.mobility2;

import java.io.IOException;
import java.util.Random;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Self;

/**
 * @author Michele Loreti
 *
 */
public class MovementAgent extends Agent {

	private Template gpsTemplate = new Template(new ActualTemplateField(("GPS")), new FormalTemplateField(Double.class),
			new FormalTemplateField(Double.class));

	private Template targetTemplate = new Template(new ActualTemplateField(("TARGET")),
			new FormalTemplateField(Boolean.class));

	private Template foundTemplate = new Template(new ActualTemplateField(("FOUND")),
			new FormalTemplateField(Boolean.class));

	private Template informedTemplate = new Template(new ActualTemplateField(("INFORMED")),
			new FormalTemplateField(Boolean.class));

	private Template directionTemplate = new Template(new ActualTemplateField(("DIRECTION")),
			new FormalTemplateField(Double.class), new FormalTemplateField(Double.class));

	private double maxX;

	private double maxY;

	private Random r = new Random();

	public MovementAgent(String name, double maxX, double maxY) {
		super(name);
		this.maxX = maxX;
		this.maxY = maxY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.scel.behaviour.Agent#doRun()
	 */
	@Override
	protected void doRun() {
		try {
			put(new Tuple(("INFORMED"), (false)), Self.SELF);
			if (!search()) {
				moveTo();
			}
			put(new Tuple(("STOP")), Self.SELF);
			publish();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected boolean search() throws InterruptedException, IOException {
		boolean found = false;
		boolean informed = false;
		double x = 0.0;
		double y = 0.0;
		while (!found && !informed) {
			Tuple t = query(gpsTemplate, Self.SELF);
			x = t.getElementAt(Double.class, 1);
			y = t.getElementAt(Double.class, 2);
			if ((x == 0) || (y == 0) || (x == maxX) || (y == maxY)) {
				put(new Tuple(("DIR"), r.nextDouble() * 2 * Math.PI), Self.SELF);
			}
			t = query(targetTemplate, Self.SELF);
			found = t.getElementAt(Boolean.class, 1);
			t = query(informedTemplate, Self.SELF);
			informed = t.getElementAt(Boolean.class, 1);
			Thread.sleep(100);
		}
		return found;
	}

	protected void publish() throws InterruptedException, IOException {
		Tuple t = query(gpsTemplate, Self.SELF);
		double x = t.getElementAt(Double.class, 1);
		double y = t.getElementAt(Double.class, 2);
		put(new Tuple(("DIRECTION"), x, y), Self.SELF);
	}

	protected void moveTo() throws InterruptedException, IOException {
		boolean found = false;
		Tuple t = query(directionTemplate, Self.SELF);
		double xTarget = t.getElementAt(Double.class, 1);
		double yTarget = t.getElementAt(Double.class, 2);
		t = query(gpsTemplate, Self.SELF);
		double x = t.getElementAt(Double.class, 1);
		double y = t.getElementAt(Double.class, 2);
		double d = getAndle(x, y, xTarget, yTarget);
		put(new Tuple(("DIR"), d), Self.SELF);
		while (!found) {
			t = query(targetTemplate, Self.SELF);
			found = t.getElementAt(Boolean.class, 1);
		}
	}

	private double getAndle(double x, double y, double xTarget, double yTarget) {
		double dX = (xTarget - x);
		double dY = (yTarget - y);
		if (dX == 0) {
			return (dY > 0 ? Math.PI / 2 : -Math.PI / 2);
		}
		if (dY == 0) {
			return (dX > 0 ? 0 : Math.PI);
		}

		return (dX < 0 ? Math.PI : 0.0) + Math.atan(dY / dX);
	}

}
