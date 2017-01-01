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
import java.util.Observable;
import java.util.Random;

import org.cmg.jresp.comp.NodeConnection;
import org.cmg.jresp.knowledge.AbstractActuator;
import org.cmg.jresp.knowledge.AbstractSensor;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;


/**
 * @author loreti
 *
 */
public class Scenario extends Observable {
	
	
	public Point2D.Double[] target;
	
	private Random r = new Random();
	
	/**
	 * Number of robots in the considered scenario
	 */
	private int size;

	/**
	 * Array of robots;
	 */
	private Robot[] robots;
		
	/**
	 * Arena width
	 */
	private double width;
	
	/**
	 * Arena height
	 */
	private double height;

	private BatteryConsumptionFunction batteryDischargingFunction;

	protected double COMMUNICATION_RANGE = 100;

	public Scenario( int size , double height , double width , BatteryConsumptionFunction batteryDischargingFunction ) {
		this.size = size;
		this.height = height;
		this.width = width;
		this.batteryDischargingFunction = batteryDischargingFunction;
		System.out.println("SIZE: "+size+" HEIGHT: "+height+" WIDTH: "+width);
		init();
	}

	void init() {
		target = new Point2D.Double[2];
//		target[0] = new Point2D.Double( 20+r.nextDouble()*(width-40) , 20+r.nextDouble()*(height-40) );
//		target[1] = new Point2D.Double( 20+r.nextDouble()*(width-40) , 20+r.nextDouble()*(height-40) );
		target[0] = new Point2D.Double( 20+(width/4.0)-40 , 20+(height/2.0)-40 );
		target[1] = new Point2D.Double( 20+(3*width/4.0)-40 , 20+(height/2.0)-40 );
		robots = new Robot[size];
		for( int i=0 ; i<size ; i++ ) {
			robots[i] = new Robot(i, r.nextDouble(), new Point2D.Double(r.nextDouble()*width, r.nextDouble()*height) , r.nextDouble(), 1.0);
		}
		
	}
	
	/**
	 * Returns the speed of the robot with index i
	 * 
	 * @param i robot index
	 * @return the speed of the robot with index i
	 */
	public double getSpeed(int i) {
		return robots[i].getSpeed();
	}

	/**
	 * Set the speed of the robot with index i
	 * 
	 * @param i robot index
	 * @param s robot speed
	 */
	public void setSpeed(int i,double s) {
		robots[i].setSpeed(s);
	}

	/**
	 * Returns current direction of robot with index i
	 * 
	 * @param i robot index
	 * @return direction of robot with index i
	 */
	public double getDirection(int i) {
		return robots[i].getDirection();
	}

	/**
	 * Set direction of robot with index i
	 * 
	 * @param i robot index
	 * @param d robot direction
	 */
	public void setDirection(int i,double d) {
		robots[i].setDirection(d);
	}

	public void setDirection(int i , double x , double y ) {
		Point2D.Double position = getPosition(i);
		
	}
	
	/**
	 * Returns the level of battery of robot with index i
	 * 
	 * @param i robot index
	 * @return robot position
	 */
	public double getBatteryLevel( int i ) {
		return robots[i].getBatteryLevel();
	}
	
	/**
	 * Returns the position of robot  with index i
	 * 
	 * @param i robot index
	 * @return robot position
	 */
	public Point2D.Double getPosition( int i ) {
		return robots[i].getPosition();
	}
	
	/**
	 * Performs a simulation step. 
	 * 
	 * @param dt a simulation step in milliseconds.
	 */
	public void step( double dt ) {
		_updatePosition(dt);
		_updateBattery(dt);
		setChanged();
		notifyObservers();
	}

	/**
	 * Methods used to update levels of robots' batteries.
	 *
	 * @param dt
	 */
	private void _updateBattery( double dt ) {
		for( int i=0 ; i<size ; i++ ) {
			robots[i].setBatteryLevel( batteryDischargingFunction.nextBatteryLevel(dt, robots[i].getBatteryLevel(), robots[i].getSpeed() )); 
		}
	}

	/**
	 * Methods used to update position of robots.
	 *
	 * @param dt
	 */
	private void _updatePosition(double dt) {
		for( int i=0 ; i<size ; i++ ) {
			if (robots[i].getBatteryLevel()>0.0) {
				if (robots[i].getPosition().distance(target[i%2])>20) {
					Point2D.Double position = robots[i].getPosition();
					double x = position.getX()+((robots[i].getSpeed()*dt)*Math.cos(robots[i].getDirection()));
					double y = position.getY()+((robots[i].getSpeed()*dt)*Math.sin(robots[i].getDirection()));
					if (x<0.0) {
						x=0.0;
					}
					if (y<0.0) {
						y=0.0;
					}
					if (x>width) {
						x=width;
					}
					if (y>height) {
						y=height;
					}
					robots[i].setPosition(x,y);
				}
			}
		}
	}

	/**
	 * Returns the number of robots in the scenario.
	 * 
	 * @return the number of robots in the scenario.
	 */
	public int getSize() {
		return size;
	}

	public int getBarreryPercentage(int arg0) {
		return (int) (getBatteryLevel(arg0)*100);
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public AbstractActuator getDirectionActuator(final int i) {
		return new AbstractActuator("direction") {
			
			@Override
			public void send(Tuple t) {
				double dir = t.getElementAt(Double.class, 1);
				setDirection(i, dir);
			}
			
			@Override
			public Template getTemplate() {
				return new Template(
						new ActualTemplateField("direction") ,
						new FormalTemplateField(Double.class)
				);
			}
		};
	}

	public AbstractSensor getLocationSensor(final int i) {
		return robots[i].getLocationSensor();
	}

	public AbstractActuator getStopActuator(final int i) {
		return new AbstractActuator("stop") {
			
			@Override
			public void send(Tuple t) {
				setSpeed(i, 0.0);
			}
			
			@Override
			public Template getTemplate() {
				return new Template(
						new ActualTemplateField("stop")
				);
			}
		};
	}

	public AbstractSensor getBatterySensor(final int i) {
		return robots[i].getBatterySensor();
	}

	public AbstractSensor getTargetSensor(final int i) {
		return robots[i].getTargetSensor();
	}

	public Point2D.Double[] getTarget() {
		return target;
	}
	
	public class Robot {
		
		private int i;
		
		private double direction;
		
		private Point2D.Double position;
		
		private double speed;
		
		private double batteryLevel;
		
		private AbstractSensor targetSensor;
		
		private AbstractSensor batterySensor;
		
		private AbstractSensor locationSensor;
		
		public Robot( int i , double direction , Point2D.Double position , double batteryLevel , double speed ) {
			this.i = i;
			this.direction = direction;
			this.position = position;
			this.batteryLevel = batteryLevel;
			this.speed = speed;
			this.targetSensor = new AbstractSensor(
					"TargetSensor-"+i ,
					new Template( new ActualTemplateField("target") , new FormalTemplateField(Boolean.class) )) {
			};
			this.batterySensor = new AbstractSensor("BatterySensor-"+i,
					new Template( new ActualTemplateField("batteryLevel") , new FormalTemplateField(Integer.class))) {
			};
			this.locationSensor = new AbstractSensor("LocationSensor-"+i,
					new Template( new ActualTemplateField("gps") , new FormalTemplateField(Double.class) , new FormalTemplateField(Double.class) )) {
			};
		}
		
		public AbstractSensor getTargetSensor() {
			return targetSensor;
		}

		public AbstractSensor getBatterySensor() {
			return batterySensor;
		}

		public AbstractSensor getLocationSensor() {
			return locationSensor;
		}

		public void setPosition(double x, double y) {
			this.setPosition(new Point2D.Double(x, y));
		}

		public double getBatteryLevel() {
			return batteryLevel;
		}

		public void setDirection(double d) {
			this.direction = d;
		}

		public double getDirection() {
			return direction;
		}

		public void setSpeed(double speed) {
			this.speed = speed;
		}

		public double getSpeed() {
			return speed;
		}

		public void setPosition( Point2D.Double point ) {
			position = point;
			targetSensor.setValue( new Tuple( "target" , target[i%2].distance(position)<20 ) );
			locationSensor.setValue( new Tuple( "gps" , position.getX() , position.getY() ) );
		}
		
		public Point2D.Double getPosition() {
			return position;			
		}
		
		public void setBatteryLevel( double batteryLevel ) {
			this.batteryLevel = batteryLevel;
			batterySensor.setValue( new Tuple( "batteryLevel" , getBarreryPercentage(i) ) );
		}
		
	}

	public NodeConnection getNodeConnection() {
		return new NodeConnection() {
			
			@Override
			public void waitInTouch(String src, String target)
					throws InterruptedException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean areInTouch(String src, String target) {
				int srcIdx = Integer.parseInt(src);
				int srcTrg = Integer.parseInt(target);
				return robots[srcIdx].position.distance(robots[srcTrg].position)<COMMUNICATION_RANGE ;
			}
		};
	}

	public boolean goalReached() {
		double count = 0.0;
		for ( int i=0 ; i<robots.length ; i++ ) {
			if ((robots[i].getPosition().distance(target[i%2])) < 20) {
				count = count + 1.0;
			}
		}
		//System.out.println("Reached: "+count);
		return (count/robots.length)>=0.25;
	}
	
}
