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
package org.cmg.jresp.examples.disaster;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

/**
 * @author loreti
 *
 */
public class SpatialPanel extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4296218138284134582L;

	private Scenario scenario;

	public SpatialPanel(Scenario scenario) {
		this.scenario = scenario;
		this.scenario.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		getRootPane().repaint();
	}

	@Override
	public void paint(Graphics arg0) {
		super.paint(arg0);
		Graphics2D g2 = (Graphics2D) arg0;
		g2.setColor(Color.BLACK);
		Shape[] walls = scenario.getWalls();
		if (walls != null) {
			for (int i = 0; i < walls.length; i++) {
				g2.fill(walls[i]);
			}
		}
		g2.setColor(Color.RED);
		for (int i = 0; i < scenario.getVictims(); i++) {
			Point2D.Double target = scenario.getVictimPosition(i);
			g2.fill(new Ellipse2D.Double(target.x - 20, target.y - 20, 40, 40));
		}
		// g2.setColor(Color.BLUE);
		// g2.fill(new Ellipse2D.Double(target[1].x-20, target[1].y-20, 40,
		// 40));
		for (int i = 0; i < scenario.getSize(); i++) {
			g2.setColor(scenario.getColor(i));
			if (i < scenario.getNumberOfLandmarks()) {
				g2.setColor(Color.BLUE);
			} else {
				g2.setColor(Color.GREEN);
			}
			Point2D.Double p = scenario.getPosition(i);
			g2.fill(new Rectangle2D.Double(p.x - 5, p.y - 5, 10, 10));
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) scenario.getWidth(), (int) scenario.getHeight());
	}

	@Override
	public Dimension getSize(Dimension arg0) {
		arg0.setSize(getPreferredSize());
		return arg0;
	}

}
