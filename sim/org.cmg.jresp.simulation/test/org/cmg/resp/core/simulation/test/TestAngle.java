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
package org.cmg.resp.core.simulation.test;

import java.awt.geom.Point2D;

/**
 * @author loreti
 *
 */
public class TestAngle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//Robot 1 target: Point2D.Double[50.965977721463496, 70.71081948599047]position: Point2D.Double[31.396315583754596, 23.911831894816956] direction: 5.338305282544699 distance: 53.04555202951541 state: INFORMED
//1.0217221308096494
		
		double targetX = 42.15159925028639;
		double targetY = 23.911831894816956;
		double x = 50.965977721463496;
		double y = 70.71081948599047;
		double angle = getAngle(x,y,targetX,targetY);
		System.out.println("ANGLE: "+angle);
		System.out.println("Distance: "+Point2D.distance(x, y, targetX, targetY));
		System.out.println("NEXT:"+(x+2*0.1*Math.cos(angle))+","+(y+2*0.1*Math.sin(angle)));
		System.out.println("New distance: "+Point2D.distance(x+(2*0.1*Math.cos(angle)), x+(2*0.1*Math.sin(angle)), targetX, targetY));
	}

	public static double getAngle(double x, double y, double xTarget, double yTarget) {
		double dX = Math.abs(xTarget - x);
		double dY = Math.abs(yTarget - y);
		if (dX == 0) {
			return (yTarget>y?Math.PI/2:3*Math.PI/2);
		}
		if (dY == 0) {
			return (xTarget>x?0:Math.PI);			
		}
		double angle = Math.atan(dY/dX);
		if ((xTarget>x)&&(yTarget>y)) {
			return angle;
		}
		if ((xTarget<x)&&(yTarget>y)) {
			return Math.PI-angle;
		}
		if ((xTarget<x)&&(yTarget<y)) {
			return angle+Math.PI;
		}
		return (2*Math.PI)-angle;
	}
}
