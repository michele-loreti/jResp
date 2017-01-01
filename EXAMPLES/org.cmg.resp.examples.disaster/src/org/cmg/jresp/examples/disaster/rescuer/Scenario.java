/**
 * Copyright (c) 2014 Concurrency and Mobility Group.
 * Universit? di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 *      Andrea Margheri
 */
package org.cmg.jresp.examples.disaster.rescuer;

import java.awt.Color;
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

public class Scenario extends Observable {

	public static final String EXPLORER = "explorer";
	public static final String RESCUER = "rescuer";
	public static final String HELP_RES = "help_rescuer";
	public static final String LOW_BATT = "low_battery";

	/*
	 * SimulationSteps For Battery. Be careful in changing number of steps, in
	 * particular for the battery simulation
	 */
	public static final int simulationSteps = 50000;

	// battery decrement for each simulation step
	private static final double batteryDecrement4Step = 0.02; // #of steps to go
																// under the
																// threshold:
	// charged battery
	private static final double chargedBattery = 100.0;
	// threshold for re-charging battery
	public static final double dechargedBattery = 20.0;
	// simulation steps needed for charging the battery
	private static final int chargingSteps = 1000;
	// % of robot that will do the re-charging process
	private static final int rechargingRobots = 25;

	protected static final double COMMUNICATION_RANGE = 100.0; // pixels

	private Random r = new Random();

	/**
	 * Array of robots: - the number of total robot, according to the state
	 * changes the role in the scenario
	 */
	private Robot[] robots;

	private int numberOfRobots;

	/**
	 * Number of Rescuer needed for rescuing a victim
	 */
	private int numberOfRescuersSwarm;

	/**
	 * Arena width
	 */
	private double width;

	/**
	 * Arena height
	 */
	private double height;

	/**
	 * Victims: number, array of victim positions, if each of them has been
	 * rescued
	 */
	private int numberOfVictims;

	private Point2D.Double[] victims;

	// notify to the scenario that a rescuer has arrived to a victim
	private boolean[] victim_anyrescuer;
	/*
	 * Rescuers: -> 0 not discovered -> 1 discovered by rescuer(s) - waiting for
	 * other rescuers (i.e. helpRescuer can perceive the victim) -> till
	 * #numberOfRescuersSwarm (when equal the victim is rescued
	 */
	private int[] rescuers;

	// /**
	// * Position of charging station
	// */
	// private Point2D.Double chargingStationPosition;

	// private Point2D.Double nestLocation;

	public Scenario(int numberOfRobots, int numberOfRescuersSwarm, int numberOfVictims, double height, double width) {
		this.numberOfRobots = numberOfRobots;
		this.numberOfRescuersSwarm = numberOfRescuersSwarm;
		this.numberOfVictims = numberOfVictims;
		this.height = height;
		this.width = width;
	}

	public void init() {
		// nestLocation = new Point2D.Double(width / 2, height - 50);
		this.rescuers = new int[numberOfVictims];
		this.victim_anyrescuer = new boolean[numberOfVictims];
		this.victims = new Point2D.Double[numberOfVictims];
		for (int i = 0; i < numberOfVictims; i++) {
			double x = 0.0;
			double y = 0.0;
			do {
				x = this.r.nextDouble() * width;
				y = r.nextDouble() * (height / 4);
			} while (!isAValidVictimPosition(i, x, y));
			this.victims[i] = new Point2D.Double(x, y);
			// starting with all victims to be rescued (i.e. 0 rescuers)
			this.rescuers[i] = 0;
			// all victims not reached by any rescuer
			this.victim_anyrescuer[i] = false;
		}
		robots = new Robot[numberOfRobots];
		for (int i = 0; i < numberOfRobots; i++) {
			// create <<rechargingRobots>> % of robots with a "de-chargeable"
			// battery
			if (i < (numberOfRobots * rechargingRobots / 100))
				robots[i] = new Robot(i, 0.5, addNoise(chargedBattery));
			else
				robots[i] = new Robot(i, 0.5, chargedBattery * 4);

			robots[i].setPosition(width / 4 + (this.r.nextDouble() * width / 4), height - (this.r.nextDouble() * 100));
		}
		// // position of charging station
		// chargingStationPosition = new Point2D.Double(width / 3, height - 50);
	}

	private double addNoise(double chargedbattery) {
		double epsilon = r.nextInt(30);
		return chargedbattery + epsilon;
	}

	private boolean isAValidVictimPosition(int i, double x, double y) {
		for (int j = 0; j < i; j++) {
			if (this.victims[j].distance(x, y) < 3 * Robot.VICTIM_SENSOR_RANGE) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the speed of the robot with index i
	 * 
	 * @param i
	 *            robot index
	 * @return the speed of the robot with index i
	 */
	public double getSpeed(int i) {
		return robots[i].getSpeed();
	}

	/**
	 * Set the speed of the robot with index i
	 * 
	 * @param i
	 *            robot index
	 * @param s
	 *            robot speed
	 */
	public void setSpeed(int i, double s) {
		robots[i].setSpeed(s);
	}

	/**
	 * Returns current direction of robot with index i
	 * 
	 * @param i
	 *            robot index
	 * @return direction of robot with index i
	 */
	public double getDirection(int i) {
		return robots[i].getDirection();
	}

	/**
	 * Set direction of robot with index i
	 * 
	 * @param i
	 *            robot index
	 * @param d
	 *            robot direction
	 */
	public void setDirection(int i, double d) {
		setDirection(i, false, d);
	}

	/**
	 * Set direction of robot with index i
	 * 
	 * @param i
	 *            robot index
	 * @param d
	 *            robot direction
	 */
	public void setDirection(int i, boolean fixed, double d) {
		robots[i].setDirection(d);
		robots[i].awareOfVictimPosition = fixed;
	}

	/**
	 * Set direction to a (x,y) point of robot i
	 * 
	 * @param i
	 *            robot index
	 * @param x
	 *            arrival position x
	 * @param y
	 *            arrival position y
	 */
	public void setDirection(int i, double x, double y) {
		Point2D.Double position = getPosition(i);
		double dx = x - position.x;
		double dy = y - position.y;
		setDirection(i, true, Math.atan2(dy, dx));
	}

	/**
	 * Set the role of robot i to s
	 * 
	 * @param i
	 *            robot index
	 * @param s
	 *            robot role
	 */
	public void setRole(int i, String s) {
		robots[i].setRole(s);
	}

	/**
	 * Return the role of robot i
	 * 
	 * @param i
	 *            robot index
	 */
	public String getRole(int i) {
		return robots[i].getRole();
	}

	/**
	 * Change the under_recharging status
	 * 
	 * @param i
	 * @param l
	 */
	public void setUnderRecharging(int i, Boolean l) {
		robots[i].setUnderRecharging(l);
	}

	/**
	 * Return the UnderRecharging process
	 * 
	 * @param i
	 * @return
	 */
	public Boolean getUnderRecharging(int i) {
		return robots[i].getUnderRecharging();
	}

	/**
	 * Returns the position of robot with index i
	 * 
	 * @param i
	 *            robot index
	 * @return robot position
	 */
	public Point2D.Double getPosition(int i) {
		return robots[i].getPosition();
	}

	private void setVictimPerceived(int i) {
		victim_anyrescuer[i] = true;
	}

	/**
	 * Performs a simulation step.
	 * 
	 * @param dt
	 *            a simulation step in milliseconds.
	 */
	public void step(double dt) {
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
		// System.out.println("Update positions...");
		for (int i = 0; i < numberOfRobots; i++) {
			if (robots[i].walking && !robots[i].getRole().equals(LOW_BATT)) {
				Point2D.Double position = robots[i].getPosition();
				double x = position.getX() + ((robots[i].getSpeed() * dt) * Math.cos(robots[i].getDirection()));
				double y = position.getY() + ((robots[i].getSpeed() * dt) * Math.sin(robots[i].getDirection()));
				if (x < 0.0) {
					x = 0.0;
				}
				if (y < 0.0) {
					y = 0.0;
				}
				if (x > width) {
					x = width;
				}
				if (y > height) {
					y = height;
				}
				robots[i].setPosition(x, y);
			} else {
				if (robots[i].getRole().equals(LOW_BATT)) {
					if (robots[i].getUnderRecharging() && robots[i].getRechargingSteps() < chargingSteps) {
						robots[i].addReachrgingStep();
					} else {
						// ROBOT CHARGED
						robots[i].setUnderRecharging(false);
					}
				}
			}
		}
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	// -----------------------------------------------------
	// Actuators
	// -----------------------------------------------------

	/**
	 * Set robot direction. Triggered by the tuple <"direction", dir> dir
	 * corresponds to the direction angle
	 * 
	 * @param i
	 *            robot index
	 *
	 */
	public AbstractActuator getDirectionActuator(final int i) {
		return new AbstractActuator("direction") {

			@Override
			public void send(Tuple t) {
				// System.out.println("New direction received!");
				double dir = t.getElementAt(Double.class, 1);
				setDirection(i, dir);
			}

			@Override
			public Template getTemplate() {
				return new Template(new ActualTemplateField("direction"), new FormalTemplateField(Double.class));
			}
		};
	}

	/**
	 * Set robot direction to a (x,y) point. Triggered by the tuple
	 * <"pointDirection", x , y> x position.x of the final point y position.y of
	 * the arrival point
	 * 
	 * @param i
	 *            robot index
	 *
	 */
	public AbstractActuator getPointDirectionActuator(final int i) {
		return new AbstractActuator("pointDirection") {

			@Override
			public void send(Tuple t) {
				double x = t.getElementAt(Double.class, 1);
				double y = t.getElementAt(Double.class, 2);
				setDirection(i, x, y);
			}

			@Override
			public Template getTemplate() {
				return new Template(new ActualTemplateField("pointDirection"), new FormalTemplateField(Double.class),
						new FormalTemplateField(Double.class));
			}
		};
	}

	/**
	 * Halt robot moving. Triggered by the tuple <"stop">
	 * 
	 * @param i
	 * @return
	 */
	public AbstractActuator getStopActuator(final int i) {
		return new AbstractActuator("stop") {

			@Override
			public void send(Tuple t) {
				stop(i);
			}

			@Override
			public Template getTemplate() {
				return new Template(new ActualTemplateField("stop"));
			}
		};
	}

	/**
	 * Change role of robot. Triggered by <"role", s> s role name
	 * 
	 * @param i
	 *            robot index
	 * @return
	 */
	public AbstractActuator getChangeRoleActuator(final int i) {
		return new AbstractActuator("roleChange") {

			@Override
			public void send(Tuple t) {
				String s = t.getElementAt(String.class, 1);
				if (s.equals(EXPLORER)) {
					setRole(i, EXPLORER);
				} else if (s.equals(RESCUER)) {
					setRole(i, RESCUER);
				} else if (s.equals(HELP_RES)) {
					setRole(i, HELP_RES);
				} else {
					setRole(i, LOW_BATT);
				}
			}

			@Override
			public Template getTemplate() {
				return new Template(new ActualTemplateField("role"), new FormalTemplateField(String.class));
			}
		};
	}

	/**
	 * Update the victim status when a robot starts the rescuing process
	 * 
	 * @param i
	 *            the robot number
	 * @return
	 */
	public AbstractActuator getUpdateVictimStateActuator(final int i) {
		return new AbstractActuator("victimUpdate") {

			@Override
			public void send(Tuple t) {
				Double x = t.getElementAt(Double.class, 1);
				Double y = t.getElementAt(Double.class, 2);

				for (int i = 0; i < victims.length; i++) {
					if (Math.abs(x - victims[i].x) <= 40 && Math.abs(y - victims[i].y) <= 40) {
						// update the rescuer number
						rescuers[i]++;
						break;
					}
				}
			}

			@Override
			public Template getTemplate() {
				return new Template(new ActualTemplateField("rescue"), new FormalTemplateField(Double.class),
						new FormalTemplateField(Double.class));
			}
		};
	}

	/**
	 * Triggers the starting of re-charging battery process
	 * 
	 * @param i
	 * @return
	 */
	public AbstractActuator getChargingBatteryActuator(final int i) {
		return new AbstractActuator("chargingBattery") {

			@Override
			public void send(Tuple t) {
				Boolean l = t.getElementAt(Boolean.class, 1);
				// set robot under re-charging
				// the _updatePosition manages the "time" needed for the
				// recharging
				setUnderRecharging(i, l);
			}

			@Override
			public Template getTemplate() {
				return new Template(new ActualTemplateField("CHARGING"), new FormalTemplateField(Boolean.class));
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

	public AbstractSensor getDirectionSensor(final int i) {
		return robots[i].getDirectionSensor();
	}

	public AbstractSensor getPositionSensor(int i) {
		return robots[i].getPositionSensor();
	}

	public AbstractSensor getWalkingSensor(int i) {
		return robots[i].getWalkingSensor();
	}

	public AbstractSensor getBatteryChargedSensor(int i) {
		return robots[i].getBatteryChargedSensor();
	}

	public AbstractSensor getBatteryLevelSensor(int i) {
		return robots[i].getBatterySensor();
	}

	public class Robot {

		private static final double VICTIM_SENSOR_RANGE = 15;

		private int i;

		private double direction;

		private Point2D.Double position;

		private double speed;

		private boolean walking;

		private boolean awareOfVictimPosition;

		/**
		 * RESCUER, HELP_RESCUER, LOW_BATT, EXPLORER (use the constants in the
		 * Scenario class)
		 */
		private String role;

		private boolean victimFound;

		private AbstractSensor victimSensor;

		private AbstractSensor collisionSensor;

		private AbstractSensor directionSensor;

		private AbstractSensor walkingSensor;

		private AbstractSensor positionSensor;

		/*
		 * Battery Management
		 */
		private AbstractSensor batterySensor;

		private AbstractSensor batteryChargedSensor;

		private double batteryLevel;

		private boolean underRecharging;

		private int rechargingSteps;

		// ----------

		public Robot(int i, double speed, double batteryLevel) {
			this.i = i;
			this.position = new Point2D.Double(0, 0);
			this.walking = false;
			this.speed = speed;
			this.batteryLevel = batteryLevel;
			this.rechargingSteps = 0;
			this.role = EXPLORER;
			this.underRecharging = false;
			this.victimFound = false;

			// -----------------------------------------------------
			// Sensors
			// -----------------------------------------------------

			this.victimSensor = new AbstractSensor("VictimSensor-" + i,
					new Template(new ActualTemplateField("VICTIM_PERCEIVED"), new FormalTemplateField(Boolean.class))) {
			};
			this.collisionSensor = new AbstractSensor("CollisionSensor-" + i,
					new Template(new ActualTemplateField("COLLISION"), new FormalTemplateField(Boolean.class))) {
			};
			this.directionSensor = new AbstractSensor("DirectionSensor-" + i,
					new Template(new ActualTemplateField("DIRECTION"), new FormalTemplateField(Double.class))) {
			};
			this.walkingSensor = new AbstractSensor("WalkingSensor-" + i,
					new Template(new ActualTemplateField("WALKING"), new FormalTemplateField(Boolean.class))) {
			};
			this.positionSensor = new AbstractSensor("PositionSensor-" + i,
					new Template(new ActualTemplateField("POSITION"), new FormalTemplateField(Point2D.Double.class))) {
			};
			this.batterySensor = new AbstractSensor("BatterySensor-" + i,
					new Template(new ActualTemplateField("BATTERY_LEVEL"), new FormalTemplateField(Double.class))) {
			};

			this.batteryChargedSensor = new AbstractSensor("BatteryChargedSensor-" + i,
					new Template(new ActualTemplateField("CHARGED"), new FormalTemplateField(Boolean.class))) {
			};

			updateCollisionSensor();
			updateVictimSensor();
			updateWalkingSensor();
			updateDirectionSensor();
			updatePositionSensor();
			updateBatterySensor();
			updateBatteryCharghedSensor();
		}

		public AbstractSensor getPositionSensor() {
			return positionSensor;
		}

		private void updateWalkingSensor() {
			if (walking) {
				// decrease the batteryLevel
				this.decreseBatteryLevel(batteryDecrement4Step);
				updateBatterySensor();
			}
			walkingSensor.setValue(new Tuple("WALKING", walking));
		}

		private void updateDirectionSensor() {
			directionSensor.setValue(new Tuple("DIRECTION", direction));
		}

		private void updatePositionSensor() {
			positionSensor.setValue(new Tuple("POSITION", position));
		}

		public AbstractSensor getDirectionSensor() {
			return directionSensor;
		}

		public AbstractSensor getWalkingSensor() {
			return walkingSensor;
		}

		public void stop() {
			this.walking = false;
			updateWalkingSensor();
		}

		public AbstractSensor getVictimSensor() {
			return victimSensor;
		}

		public AbstractSensor getCollisionSensor() {
			return collisionSensor;
		}

		public synchronized void setDirection(double d) {
			if (!underRecharging) {
				// detectCollisions();
				// detectVictims();
				this.direction = d;
				this.walking = true;
				updateWalkingSensor();
			}
		}

		public int getIndex() {
			return this.i;
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

		public String getRole() {
			return role;
		}

		public void setRole(String s) {
			this.role = s;
		}

		public void setPosition(double x, double y) {
			setPosition(new Point2D.Double(x, y));
		}

		public void setPosition(Point2D.Double point) {
			position = point;
			updateCollisionSensor();
			updateVictimSensor();
			updateWalkingSensor();
			updateDirectionSensor();
		}

		/**
		 * Found Victim
		 * 
		 * @return
		 */
		public boolean detectVictim() {
			if (position != null) {
				// when the robot is a Rescuer or is going to the charging
				// station the sensor is deactivated
				if (!this.role.equals(Scenario.RESCUER) && !this.role.equals(Scenario.LOW_BATT)) {
					for (int i = 0; i < victims.length; i++) {
						/*
						 * Rescuers: -> 0 not discovered -> 1 discovered by
						 * rescuer(s) - waiting for other rescuers (i.e.
						 * helpRescuer can perceive the victim) -> till
						 * #numberOfRescuersSwarm (when equal the victim is
						 * rescued
						 */
						if (victims[i].distance(this.position) <= VICTIM_SENSOR_RANGE
								&& rescuers[i] < numberOfRescuersSwarm) {
							if (this.role.equals(Scenario.EXPLORER) && victim_anyrescuer[i]) { // rescuers[i]
																								// >
																								// 0){
								// an other rescuer has detected the victim, he
								// is waiting for an HelpRescuer
								// no other Explorer must become Rescuer
								return false;
							}
							if (this.role.equals(Scenario.HELP_RES)) {
								// the help_rescuer is going to the victim
								return true;
							}
							// set a variable to render the victim not visible
							// to other explorer
							// (the update or rescuers is done later)
							setVictimPerceived(i);
							// disable the victim sensor
							this.victimFound = true;
							return true;
							// except the previous one, in all the other case
							// the sensor perceives the victim
							// the victim state (hence the rescuers number) is
							// updated by the actuator triggered by the tuple
							// <"rescue">
						}
					}
				}
			}
			return false;
		}

		private void updateVictimSensor() {
			if (!victimFound)
				victimSensor.setValue(new Tuple("VICTIM_PERCEIVED", detectVictim()));
		}

		private void updateCollisionSensor() {
			if ((position != null)
					&& ((position.x <= 0) || (position.x >= width) || (position.y <= 0) || (position.y >= height))) {
				collisionSensor.setValue(new Tuple("COLLISION", true));
				walking = false;
			} else if (collisionWithVictim()) {
				collisionSensor.setValue(new Tuple("COLLISION", true));
				walking = false;
			} else {
				collisionSensor.setValue(new Tuple("COLLISION", false));
			}
		}

		private boolean collisionWithVictim() {
			if (this.role.equals(Scenario.EXPLORER) && !victimFound) {
				for (int i = 0; i < victims.length; i++) {
					if (victims[i].distance(this.position) <= VICTIM_SENSOR_RANGE && victim_anyrescuer[i]) {
						return true;
					}
				}
			}
			return false;
		}

		public Point2D.Double getPosition() {
			return position;
		}

		/*
		 * Methods for Battery Management
		 */

		public AbstractSensor getBatterySensor() {
			return batterySensor;
		}

		public AbstractSensor getBatteryChargedSensor() {
			return batteryChargedSensor;
		}

		/**
		 * Update the battery level. The decrement is invoked by _updatePosition
		 */
		private void updateBatterySensor() {
			batterySensor.setValue(new Tuple("BATTERY_LEVEL", this.batteryLevel));
		}

		/**
		 * Update the sensor on the BatteryCharged
		 */
		private void updateBatteryCharghedSensor() {
			batteryChargedSensor.setValue(new Tuple("CHARGED", this.batteryLevel < chargedBattery ? false : true));
		}

		private void decreseBatteryLevel(double dim) {
			this.batteryLevel = this.batteryLevel - dim;
			updateBatteryCharghedSensor();
		}

		/**
		 * Signal if the robot is underRechargment. When it is set to false, it
		 * means the process is finished. Then, new battery level is set and
		 * sensor for signal the end of re-charging process
		 * 
		 * @param flag
		 */
		public void setUnderRecharging(boolean flag) {
			this.underRecharging = flag;
			if (!flag) {
				/*
				 * false == charging finished
				 */
				// set the battery charged level
				this.batteryLevel = chargedBattery;
				// signal for changing state
				updateBatteryChargedSensor();
				// reset re-charging steps
				this.rechargingSteps = 0;
			}
		}

		public Boolean getUnderRecharging() {
			return this.underRecharging;
		}

		private void updateBatteryChargedSensor() {
			// resume walking and switch underRecharging state
			walking = true;
			batteryChargedSensor.setValue(new Tuple("CHARGED", true));
		}

		/*
		 * Methods for simulating re-charging steps
		 */

		public void addReachrgingStep() {
			this.rechargingSteps += 1;
		}

		public int getRechargingSteps() {
			return this.rechargingSteps;
		}

	} // End Robot Inner-Class

	public int getSize() {
		return numberOfRobots;
	}

	public Color getColor(int i) {
		switch (robots[i].getRole()) {
		case EXPLORER:
			return Color.BLUE;
		case RESCUER:
			return Color.GREEN;
		case HELP_RES:
			return Color.CYAN;
		case LOW_BATT:
			return Color.ORANGE;
		default:
			break;
		}
		// this should never happen
		return Color.BLACK;
	}

	public Color getColorVictim(int i) {
		if (rescuers[i] < numberOfRescuersSwarm)
			return Color.RED;
		else
			return Color.MAGENTA;
	}

	public Color getColorChargingStation() {
		return Color.ORANGE;
	}

	public int getRobots() {
		return numberOfRobots;
	}

	public int getRescuersSwarmSize() {
		return numberOfRescuersSwarm;
	}

	public int getVictims() {
		return numberOfVictims;
	}

	public Point2D.Double getVictimPosition(int i) {
		return victims[i];
	}

	// public Point2D.Double getChargingStationPosition() {
	// return chargingStationPosition;
	// }

	public NodeConnection getNodeConnection() {
		return new NodeConnection() {

			@Override
			public void waitInTouch(String src, String target) throws InterruptedException {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean areInTouch(String src, String target) {
				try {
					int idSrc = Integer.parseInt(src);
					int idTrg = Integer.parseInt(target);
					Point2D.Double locationSrc = robots[idSrc].getPosition();
					Point2D.Double locationTarget = robots[idTrg].getPosition();
					if ((locationSrc == null) || (locationTarget == null)) {
						return false;
					}
					return locationSrc.distance(locationTarget) < COMMUNICATION_RANGE;
				} catch (Exception e) {
					return false;
				}
			}
		};
	}

	public boolean goalReached() {

		// for ( int i=0 ; i<numberOfRobots ; i++ ) {
		// if (this.){
		// if (!robots[i].detectVictim()) {
		// return false;
		// }
		// }
		// }
		return true;
	}
}
