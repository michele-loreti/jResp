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
package org.cmg.jresp.examples.construction;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Observable;
import java.util.Random;

import org.cmg.jresp.RESPElementFactory;
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
public class Scenario extends Observable implements RESPElementFactory {
	
	
	protected static final double COMMUNICATION_RANGE = 1000.0;

	private Random r = new Random();

	private int elementIn1 = 0;
	
	private int elementIn2 = 0;
	
	/**
	 * Array of robots: 
	 * - from 0 to numberOfExplorers-1 are explorers
	 * - from numberOfExplorers to numberOfExplorers+numberOfRescuers-1 are rescuers;
	 */
	private Robot[] robots;

	private HashMap<String, Robot> table = new HashMap<String, Robot>();
	
	/**
	 * Arena width
	 */
	private double width;
	
	/**
	 * Arena height
	 */
	private double height;
	
	private Point2D.Double[] food;
	
	private boolean[] food_detected;
	
	private double nestLocation;

	private int numberOfRobots;

	private int numberOfPiecesOfFood;

	private AbstractSensor doorSensor1;

	private AbstractSensor doorSensor2;
	
	public Scenario( int numberOfRobots , int numberOfPiecesOfFood , double height , double width ) {
		this.numberOfPiecesOfFood = numberOfPiecesOfFood;
		this.numberOfRobots = numberOfRobots;
		this.height = height;
		this.width = width;
	}

	

	public void init() {
		elementIn1 = 0;
		elementIn2 = 0;
		nestLocation = 100.0;
		this.food = new Point2D.Double[numberOfPiecesOfFood];
		this.food_detected = new boolean[numberOfPiecesOfFood];
		for( int i=0 ; i<numberOfPiecesOfFood ; i++ ) {
			this.food[i] = placeFoodRandomly();
			this.food_detected[i] = false;
		}
		doorSensor1 = new AbstractSensor("DoorSensor-"+1 ,
				new Template( new ActualTemplateField("door") , new FormalTemplateField(Boolean.class) )) {
		};
		doorSensor2 = new AbstractSensor("DoorSensor-"+2 ,
				new Template( new ActualTemplateField("door") , new FormalTemplateField(Boolean.class) )) {
		};
		robots = new Robot[numberOfRobots];
		for( int i=0 ; i<numberOfRobots ; i++ ) {
			robots[i] = new Robot(i, 0.1);
			table.put("W"+i, robots[i]);
//			robots[i].setPosition( 20 + this.r.nextDouble()*(width - 20) , 20 + this.nestLocation + r.nextDouble()*(height-30-this.nestLocation)   );
			robots[i].setPosition( 20 + i*(width - 40)/numberOfRobots , height-20 );
		}
		
	}
	
	private Point2D.Double placeFoodRandomly() {
		return new Point2D.Double( 10 + this.r.nextDouble()*(width - 20) , 20 + this.getBeaconPosition() + r.nextDouble()*(height-30-getBeaconPosition()) );	
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
		setChanged();
		notifyObservers();
	}

	/**
	 * Methods used to update position of robots.
	 *
	 * @param dt
	 */
	private void _updatePosition(double dt) {
		for( int i=0 ; i<numberOfRobots ; i++ ) {
			if (robots[i].walking) {
				Point2D.Double position = robots[i].getPosition();
				double x = position.getX()+((robots[i].getSpeed()*dt)*Math.cos(robots[i].getDirection()));
				double y = position.getY()+((robots[i].getSpeed()*dt)*Math.sin(robots[i].getDirection()));
				if (x<0.0) {
					x=0.0;
				}
				if ((y<0.0)&&(robots[i].food_index>=0)) {
					y=0.0;
				}				
				if ((y<getBeaconPosition())&&(robots[i].food_index<0)&&(!robots[i].exiting)) {
					y=getBeaconPosition();
				}				
				if (x>width) {
					x=width;
				}
				if (y>height) {
					y=height;
				}
				robots[i].setPosition(x,y);
			} else {
//				System.out.println("Robot "+i+" is stopped!");
			}
		}
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
//				System.out.println("New direction received!");
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

	public AbstractActuator getStopActuator(final int i) {
		return new AbstractActuator("stop") {
			
			@Override
			public void send(Tuple t) {
				stop( i );
			}
			
			@Override
			public Template getTemplate() {
				return new Template(
						new ActualTemplateField("stop")
				);
			}
		};
	}

	protected void stop(int i) {
		robots[i].stop();
	}

	public AbstractSensor getVictimSensor(final int i) {
		return robots[i].getVictimSensor();
	}

	public AbstractSensor getCollisionSensor(final int i) {
		return robots[i].getCollisionSensor();
	}
	

	public class Robot {
		
		private static final double VICTIM_SENSOR_RANGE = 10;

		private int i;
		
		private double direction;
		
		private Point2D.Double position;
		
		private double speed;
		
		private boolean walking;
		
		private AbstractSensor foodSensor;
		
		private AbstractSensor collisionSensor;

		private AbstractSensor beaconSensor;

		private int food_index = -1;

		private boolean isSelected;

		public AbstractSensor nestSensor;

		protected int doorId;

		private boolean exiting = false;

		public Robot( int i , double speed ) {
			this.i = i;
			this.position = new Point2D.Double(0,0);
			this.walking = false;
			this.speed = speed;
			this.foodSensor = new AbstractSensor("VictimSensor-"+i ,
					new Template( new ActualTemplateField("food") , new FormalTemplateField(Boolean.class) )) {
			};
			this.collisionSensor = new AbstractSensor("CollisionSensor-"+i,
					new Template( new ActualTemplateField("collision") , new FormalTemplateField(Boolean.class) )) {
			};
			this.beaconSensor = new AbstractSensor("BeaconSensor-"+i,
					new Template( new ActualTemplateField("beacon") , new FormalTemplateField(Boolean.class) )) {
			}; 
			this.nestSensor = new AbstractSensor("NestSensor-"+i,
					new Template( new ActualTemplateField("nest") , new FormalTemplateField(Boolean.class) )) {
			}; 
			updateCollisionSensor();
			updateFoodSensor();
			updateBeaconSensor();
			updateNestSensor();
		}
		
		private void updateNestSensor() {
			boolean inNest = this.position.y<= (getNestSize()-20);
			nestSensor.setValue(new Tuple( "nest" , inNest ));
			if ((inNest)&&(isSelected)) {
				this.walking = false;
				this.exiting = true;
			}
		}

		private void updateBeaconSensor() {
			boolean beaconPerceived = this.position.y<= getBeaconPosition();
			beaconSensor.setValue(new Tuple( "beacon" , beaconPerceived ));
			if ((food_index >= 0)&&( beaconPerceived)&&(!isSelected)&&(!exiting)) {
				this.walking = false;
			}
			if (!beaconPerceived) {
				this.exiting = false;
			}
		}

		public void stop() {
			this.walking = false;
		}

		public AbstractSensor getVictimSensor() {
			return foodSensor;
		}

		public AbstractSensor getCollisionSensor() {
			return collisionSensor;
		}

		public synchronized void setDirection(double d) {
			this.direction = d;
//			detectCollisions();
//			detectVictims();
			this.walking = true;
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

		public void setPosition( double x , double y ) {
			setPosition( new Point2D.Double(x,y));
		}
		
		public void setPosition( Point2D.Double point ) {
			position = point;
			updateCollisionSensor();
			updateFoodSensor();
			updateBeaconSensor();
			updateDoorSensor();
			updateNestSensor();
		}
		
		
		private void updateDoorSensor() {
			//TODO:
		}

		public int detectFood() {
			if (position != null) {
				for (int i=0 ; i<food.length ; i++ ) {
					if ((food[i].distance(this.position) <= VICTIM_SENSOR_RANGE)&&(!food_detected[i])) {
							return i;
					}
				}
			}
			return -1;
		}
		
		
		private void updateFoodSensor() {
			int detected = detectFood();
			if (detected < 0) {
				foodSensor.setValue( new Tuple("food" , false ));
			} else {
				foodSensor.setValue( new Tuple("food" , true ));
				if (food_index < 0) {
					food_detected[detected] = true;
					food_index = detected;
					walking = false;
				}
			}
		}

		private void updateCollisionSensor() {
			if ((position != null)&&((position.x <= 0)
					||(position.x>=width)
					||(position.y<=0)
					||((position.y<=getBeaconPosition())&&(food_index<0)&&(!exiting))
					||(position.y>=height))) {
				collisionSensor.setValue( new Tuple( "collision" , true ) );
				walking = false;
			} else {
				collisionSensor.setValue( new Tuple( "collision" , false ));
			}
		}

		public Point2D.Double getPosition() {
			return position;			
		}

		public void collect() {
			if (food_index >= 0) {
				Scenario.this.removeFood(food_index);
			}
		}

		public void release() {
			food_index = -1;
			isSelected = false;
			if (doorId == 1) {
				doorSensor1.setValue(new Tuple( "door" , false ) );
				elementIn1++;
			}
			if (doorId == 2) {
				doorSensor2.setValue(new Tuple( "door" , false ) );
				elementIn2++;
			}
			doorId = -1;
		}
				
	}


	public int getSize() {
		return numberOfRobots;
	}

	public void removeFood(int food_index) {
		food[food_index] = placeFoodRandomly();
		food_detected[food_index] = false;
	}



	public Color getColor(int i) {
		return Color.BLACK;
	}

	public int getFood() {
		return numberOfPiecesOfFood;
	}

	public Point2D.Double getFoodPosition(int i) {
		return food[i];
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
				return true;
			}
		};
	}

	protected boolean areInTouch(int idSrc, int idTrg) {
		Point2D.Double locationSrc = robots[idSrc].getPosition();
		Point2D.Double locationTarget = robots[idTrg].getPosition();
		if ((locationSrc == null)||(locationTarget==null)) {
			return false;
		}
		if (!(locationSrc.distance(locationTarget)<COMMUNICATION_RANGE)) {
			return false;
		}
		return true; 
	}



	@Override
	public AbstractSensor getSensor(String nodeName, String sensorName,
			Template pattern) {
		Robot r = table.get(nodeName);
		if ( r != null) {
			if (sensorName.equals("food")) {
				return r.foodSensor;
			}
	
			if (sensorName.equals("collision")) {
				return r.collisionSensor;
			}
			
			if (sensorName.equals("beacon")) {
				return r.beaconSensor;
			}
			
			if (sensorName.equals("nest")) {
				return r.nestSensor;
			}

			return null;
		}
		if (nodeName.equals("D1")&&sensorName.equals("door")) {
			doorSensor1.setValue(new Tuple("door",false));
			return doorSensor1;
		}

		if (nodeName.equals("D2")&&sensorName.equals("door")) {
			doorSensor2.setValue(new Tuple("door",false));
			return doorSensor2;
		}
		return null;
	}



	@Override
	public AbstractActuator getActuator(String nodeName, String attributeName,
			Template pattern) {
		final Robot r = table.get(nodeName);
		if (attributeName.equals("direction")) {
			return new AbstractActuator("direction") {
				
				@Override
				public void send(Tuple t) {
					int doorId = t.getElementAt(Integer.class, 1);
//					System.out.println("Door: "+ doorId);
					double dy = getNestSize();
					double dx = width/4;
					if (doorId == 2) {
						dx += width/2;
					}										
					dx = dx - r.position.x;
			        dy = dy - r.position.y;
					r.setDirection(Math.atan2(dy, dx));
					r.isSelected = true;
					if (doorId == 1) {
						doorSensor1.setValue(new Tuple( "door" , true ) );
						r.doorId = 1;
					} 
					if (doorId == 2) {
						doorSensor2.setValue(new Tuple( "door" , true ) );
						r.doorId = 2;
					} 
				}
				
				@Override
				public Template getTemplate() {
					return new Template( 
						new ActualTemplateField("direction") ,
						new FormalTemplateField(Integer.class)
					);
				}
			};
		}
		

		if (attributeName.equals("grip")) {
			return new AbstractActuator("grip") {
				
				@Override
				public void send(Tuple t) {
					r.collect();
				}
				
				@Override
				public Template getTemplate() {
					return new Template( 
						new ActualTemplateField("collect") 
					);
				}
			};
		}

		if (attributeName.equals("release")) {
			return new AbstractActuator("release") {
				
				@Override
				public void send(Tuple t) {
					r.release();
				}
				
				@Override
				public Template getTemplate() {
					return new Template( 
						new ActualTemplateField("release") 
					);
				}
			};
		}

		if (attributeName.equals("stop")) {
			return new AbstractActuator("stop") {
				
				@Override
				public void send(Tuple t) {
					r.walking = false;
				}
				
				@Override
				public Template getTemplate() {
					return new Template( 
						new ActualTemplateField("stop") 
					);
				}
			};
		}


		if (attributeName.equals("randomdir")) {
			return new AbstractActuator("randomdir") {
				
				@Override
				public void send(Tuple t) {
					r.setDirection(Scenario.this.r.nextDouble()*2*Math.PI);
				}
				
				@Override
				public Template getTemplate() {
					return new Template( 
						new ActualTemplateField("randomdir") 
					);
				}
			};
		}

		if (attributeName.equals("exit")) {
			return new AbstractActuator("exit") {
				
				@Override
				public void send(Tuple t) {
					r.setDirection(Math.PI/2);
				}
				
				@Override
				public Template getTemplate() {
					return new Template( 
						new ActualTemplateField("exit") 
					);
				}
			};
		}

		return null;
	}



	public double getBeaconPosition() {
		return getNestSize()+50;
	}



	public double getNestSize() {
		return 80;
	}

	public int getElements() {
		return elementIn1+elementIn2;
	}


	public int getElementsIn1() {
		return elementIn1;
	}
	
	public int getElementsIn2() {
		return elementIn2;
	}

}
