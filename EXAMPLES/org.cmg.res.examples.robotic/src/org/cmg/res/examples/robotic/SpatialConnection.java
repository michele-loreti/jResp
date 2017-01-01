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

import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Random;
import java.util.Set;

import org.cmg.jresp.comp.NodeConnection;
import org.cmg.jresp.knowledge.AbstractActuator;
import org.cmg.jresp.knowledge.AbstractSensor;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;

/**
 * @author Michele Loreti
 *
 */
public class SpatialConnection extends Observable implements NodeConnection {

	protected Random r = new Random();
	protected Hashtable<String, RobotLocation> locations;	
	
	protected Hashtable<String, Boolean> green;
	protected Hashtable<String, Boolean> moving;
	protected Hashtable<String, Double> directions;
	protected double maxX = 640;
	protected double maxY = 480;
	
	
	public static String NODE_PREFIX = "n";
	
	protected double speed = 10;
	protected double range = 100;
	protected double greenTargetX;
	protected double greenTargetY;
	protected double redTargetX;
	protected double redTargetY;
	protected double targetSize = 100;
	
	public SpatialConnection( int size , double range ) {
		this.range = range;
		this.locations = new Hashtable<String, RobotLocation>();
		this.directions = new Hashtable<String, Double>();
		this.moving = new Hashtable<String, Boolean>();
		this.greenTargetX = 75+(maxX/2-150)*r.nextDouble();
		this.greenTargetY = 75+(maxY-150)*r.nextDouble();
		this.redTargetX = 75+maxX/2+(maxX/2-150)*r.nextDouble();
		this.redTargetY = 75+(maxY-150)*r.nextDouble();
		this.green = new Hashtable<String, Boolean>();
		this.init(size);
	}
	
	private void init(int size) {
		for( int i=0 ; i<size ; i++ ) {
			String node = NODE_PREFIX+i;
			this.green.put(node,i%2==0);
			this.locations.put(node, new RobotLocation(node, new Point2D.Double(maxX*r.nextDouble(),maxY*r.nextDouble())));	
			this.directions.put(node, r.nextDouble()*Math.PI*2);
			this.moving.put(node, true);
		}
	}
	
	@Override
	public synchronized void waitInTouch(String src, String target)
			throws InterruptedException {
		while (!areInTouch(src, target)) {
			wait();
		}
	}

	@Override
	public synchronized boolean areInTouch(String src, String target) {
		Point2D.Double pSrc = locations.get(src).getPoint();
		if (pSrc == null) {
			return false;
		}
		Point2D.Double pTrg = locations.get(target).getPoint();
		if (pTrg == null) {
			return false;
		}
		boolean result = pSrc.distance(pTrg)<=range;
		return result;
	}

	public synchronized void move( ) {
		for (String n : locations.keySet()) {
			if (moving.get(n)) {
				RobotLocation location = locations.get(n);
				Point2D.Double p = location.getPoint();
				Double d = directions.get(n);
				double newX = p.x+speed*Math.cos(d);
				double newY = p.y+speed*Math.sin(d);
				if (newX<0) {
					newX=0;
				}
				if (newX>maxX) {
					newX = maxX;
				}
				if (newY<0) {
					newY=0;
				}
				if (newY>maxY) {
					newY = maxY;
				}
				Point2D.Double np = new Point2D.Double(newX,newY);
				location.setPoint(np);			
	//			if ((newX==0)||(newX==maxX)||(newY==0)||(newY==maxY)) {
	//				directions.put(n, r.nextDouble()*Math.PI*2);
	//			}
			}
		}
		setChanged();
		notifyObservers();
		notifyAll();
	}

	public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public Set<String> getNodes() {
		return locations.keySet();
	}

	public Point2D.Double getLocation(String node) {
		return locations.get(node).getPoint();
	}
	
	public Point2D.Double getGreenTargetLocation( ) {
		return new Point2D.Double(greenTargetX,greenTargetY);
	}

	public Point2D.Double getRedTargetLocation( ) {
		return new Point2D.Double(redTargetX,redTargetY);
	}

	public AbstractSensor getLocationSensor( final String n ) {
		return locations.get(n).getLocationSensor();
	}
	
	public AbstractSensor getTargetSensor( final String n ) {
		return locations.get(n).getTargetSensor();
	}

	public AbstractActuator getDirectionActuator( final String n ) {
		return new AbstractActuator("direction") {
			
			@Override
			public void send(Tuple t) {
				directions.put(n, (t.getElementAt(Double.class,1)) );
			}
			
			@Override
			public Template getTemplate() {
				return new Template( 
					new ActualTemplateField( ("DIR") ) ,
					new FormalTemplateField( Double.class )
				);
			}
		};
	}
	
	public AbstractActuator getStopActuator( final String n ) {
		return new AbstractActuator("stop") {
			
			@Override
			public void send(Tuple t) {
				moving.put(n, false);
			}
			
			@Override
			public Template getTemplate() {
				return new Template( 
					new ActualTemplateField( "STOP" )
				);
			}
		};
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}
	
	public boolean isGreen( String n ) {
		return this.green.get(n);
	}
	
	public class RobotLocation {
		
		private java.awt.geom.Point2D.Double point;
		private String name;
		
		private AbstractSensor locationSensor;
		
		private AbstractSensor targetSensor;

		public RobotLocation(String name, Point2D.Double point) {
			this.name = name;
//			this.locationSensor = new AbstractSensor(name+"-LocationSensor") {};
//			this.targetSensor = new AbstractSensor(name+"-TargetSensor") {};
			setPoint(point);
		}

		public AbstractSensor getTargetSensor() {
			return targetSensor;
		}

		public AbstractSensor getLocationSensor() {
			return locationSensor;
		}

		public void setPoint(Point2D.Double point) {
			this.point = point;
			if (point == null) {
				locationSensor.setValue(null);
				targetSensor.setValue(null);
			} else {
				locationSensor.setValue(new Tuple( 
						("GPS"),
						(point.x),
						(point.y)
					)
				);
				targetSensor.setValue(new Tuple( 
					("TARGET"),
					(
						(isGreen(name)?
							point.distance(greenTargetX, greenTargetY)<targetSize/2-5:
							point.distance(redTargetX, redTargetY)<targetSize/2-5
						)
					)
				));
			}
		}		
		
		public Point2D.Double getPoint() {
			return point;
		}
		
	}
}
