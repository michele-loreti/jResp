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
package org.cmg.jresp.examples.bikes;

import java.util.LinkedList;
import java.util.Random;

import org.cmg.jresp.comp.AttributeCollector;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.KnowledgeManager;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.simulation.SimulationEnvironment;
import org.cmg.jresp.simulation.SimulationNode;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Target;

/**
 * @author loreti
 *
 */
public class Scenario {
	
	public static Template RESERVE_BIKE = new Template( 
		new ActualTemplateField("BIKE_RESERVE") ,
		new FormalTemplateField( PointToPoint.class )
	);
	
	public static Template RESERVE_SLOT = new Template( 
			new ActualTemplateField("SLOT_RESERVE") ,
			new FormalTemplateField( PointToPoint.class )
	);
	
	public static Template GET_BIKE = new Template(
			new ActualTemplateField("BIKE")
	);
	
	public static Tuple LEAVE_BIKE = new Tuple(
			"BIKE"
	);
	
	public static Template NEXT_LOCATION = new Template( 
			new ActualTemplateField("NEXT_LOC")
	);
	
	public static Template WALK_TEMPLATE = new Template( 
			new ActualTemplateField("WALK") ,
			new FormalTemplateField(Integer.class) ,
			new FormalTemplateField(Integer.class)
	);
	
	public static Template RIDE_TEMPLATE = new Template( 
			new ActualTemplateField("RIDE") ,
			new FormalTemplateField(Integer.class) ,
			new FormalTemplateField(Integer.class)
	);
	

	public SimulationNode[][] parkingStationComponents;
	
	public SimulationNode[] userComponents;

	private SimulationEnvironment environment;

	private ParkingStation[][] parkingStations;

	private int numberOfBikes;

	private int width;

	private int height;
	
	private Random random;
	
	public class ParkingStation {
		
		private int x;
		
		private int y;
		
		private int bikes_available;
		
		private int bikes_reserved;
		
		private int slots_available;
		
		private int slots_reserved;

		/**
		 * @param bikes_available
		 * @param bikes_reserved
		 * @param slots_available
		 * @param slots_reserved
		 */
		public ParkingStation(int x , int y , int bikes_available, int bikes_reserved,
				int slots_available, int slots_reserved) {
			super();
			this.x = x;
			this.y = y;
			this.bikes_available = bikes_available;
			this.bikes_reserved = bikes_reserved;
			this.slots_available = slots_available;
			this.slots_reserved = slots_reserved;
		}
		
		
		public boolean reserveBike( ) {
			if (bikes_available>0) {
				bikes_available--;
				bikes_reserved++;
				return true;
			}
			return false;			
		}
		
		public boolean takeBike( ) {
			if (bikes_reserved>0) {
				bikes_reserved--;
				slots_available++;
				return true;
			}
			return false;
		}
		
		public boolean reserveSlot( ) {
			if (slots_available>0) {
				slots_available--;
				slots_reserved++;
				return true;
			}
			return false;
		}
		
		public boolean leaveBike( ) {
			if (slots_reserved>0) {
				slots_reserved--;
				bikes_available++;
				return true;
			}
			return false;
		}


		public PointToPoint getParkingAddress() {
			return parkingStationComponents[x][y].getLocalAddress();
		}
		
	}
	
	
	public class ParkingStationKnowledge implements KnowledgeManager {
		
		private ParkingStation station;
		
		public ParkingStationKnowledge( ParkingStation station ) {
			this.station = station;
		}

		@Override
		public boolean put(Tuple t) {
			if (t == Scenario.LEAVE_BIKE) {
				return station.leaveBike();
			}
			return false;
		}

		@Override
		public Tuple get(Template template) {
			if (template == Scenario.GET_BIKE) {
				if (station.takeBike()) {
					return new Tuple( station.x , station.y );
				}
			}
			if (template == Scenario.RESERVE_BIKE) {
				if (station.reserveBike()) {
					return new Tuple( station.x , station.y );
				}
			}
			if (template == Scenario.RESERVE_SLOT) {
				if (station.reserveSlot()) {
					return new Tuple( station.x , station.y );
				}
			}
			return null;
		}

		@Override
		public Tuple getp(Template template) {
			return get( template );
		}

		@Override
		public LinkedList<Tuple> getAll(Template template) {
			LinkedList<Tuple> results = new LinkedList<Tuple>();
			Tuple t = get( template );
			if (t != null) {
				results.add(t);
			}
			return results;
		}

		@Override
		public Tuple query(Template template) throws InterruptedException {
			return null;
		}

		@Override
		public Tuple queryp(Template template) {
			return null;
		}

		@Override
		public LinkedList<Tuple> queryAll(Template template) {
			return null;
		}

		@Override
		public Tuple[] getKnowledgeItems() {
			return new Tuple[] { new Tuple( station.bikes_available , station.bikes_reserved , station.slots_available , station.slots_reserved ) };
		}
		
	}
	
	
	public Scenario(Random random , SimulationEnvironment environment , int users, int width, int height, int bikes) {
		this.random = random;
		this.environment = environment;
		this.width = width;
		this.height = height;
		this.numberOfBikes = bikes;
		generateParkingStations();
		generateUsers(users);
	}

	public boolean walkingDistance( int x1 , int y1 , int x2 , int y2 ) {
		return (Math.abs(x1-x2)<=1)&&(Math.abs(y1-y2)<=1);
	}
	
	protected void generateParkingStations( ) {
		parkingStationComponents = new SimulationNode[width][height];
		parkingStations = new ParkingStation[width][height];
		for( int i=0 ; i<width ; i++ ) {
			for( int j=0 ; j<height ; j++ ) {
				final int x = i;
				final int y = j;
				parkingStations[i][j] = new ParkingStation(i, j, numberOfBikes , 0, numberOfBikes , 0 );
				parkingStationComponents[i][j] = new SimulationNode(("P["+i+j+"]"), this.environment , new ParkingStationKnowledge( parkingStations[i][j] ));
				parkingStationComponents[i][j].addAttributeCollector( new AttributeCollector( "X" ) {

					@Override
					protected Object doEval(Tuple... t) {
						return x;
					}
					
				});
				parkingStationComponents[i][j].addAttributeCollector( new AttributeCollector( "Y" ) {

					@Override
					protected Object doEval(Tuple... t) {
						return y;
					}
					
				});
				parkingStationComponents[i][j].addAttributeCollector(new AttributeCollector( "BIKES" ) {

					@Override
					protected Object doEval(Tuple... t) {
						return parkingStations[x][y].bikes_available;
					}
					
				});
				parkingStationComponents[i][j].addAttributeCollector(new AttributeCollector( "SLOTS" ) {

					@Override
					protected Object doEval(Tuple... t) {
						return parkingStations[x][y].slots_available;
					}
					
				});
			}
		}
	}
	
	protected void generateUsers( int size ) {
		userComponents = new SimulationNode[size];
		for( int i=0 ; i<size ; i++ ) {
			int userX = random.nextInt(width);
			int userY = random.nextInt(height);
			userComponents[i] = new SimulationNode("U["+i+"]", environment,new UserKnowledge(userX,userY));
			userComponents[i].addAgent( new UserProcess("P", this, userX, userY));
		}
	}

	public class UserKnowledge implements KnowledgeManager {
		
		private int x;
		
		private int y;
		
		public UserKnowledge( int x , int y ) {
			this.x=x;
			this.y=y;
		}

		@Override
		public boolean put(Tuple t) {
			if (Scenario.RIDE_TEMPLATE.match(t)||Scenario.WALK_TEMPLATE.match(t)) {
				this.x = t.getElementAt(Integer.class, 1);
				this.y = t.getElementAt(Integer.class, 2);
			}
			return false;
		}

		@Override
		public Tuple get(Template template) {
			if (template == Scenario.NEXT_LOCATION) {
				return new Tuple( random.nextInt( width ) , random.nextInt( height ));
			}
			return null;
		}

		@Override
		public Tuple getp(Template template) {
			return get(template);
		}

		@Override
		public LinkedList<Tuple> getAll(Template template) {
			LinkedList<Tuple> result = new LinkedList<Tuple>();
			Tuple t = get(template);
			if (t != null) {
				result.add(t);
			}
			return result;
		}

		@Override
		public Tuple query(Template template) throws InterruptedException {
			return null;
		}

		@Override
		public Tuple queryp(Template template) {
			return null;
		}

		@Override
		public LinkedList<Tuple> queryAll(Template template) {
			return null;
		}

		@Override
		public Tuple[] getKnowledgeItems() {
			return null;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
	}


	public Target stationAt(int x, int y) {
		return parkingStationComponents[x][y].getLocalAddress();
	}
	
	public int getMinBikes() {
		int toReturn = 2*this.numberOfBikes;
		for( int i=0 ; i<width ; i++ ) {
			for( int j=0 ; j<height ; j++ ) {
				if (parkingStations[i][j].bikes_available<toReturn) {
					toReturn = parkingStations[i][j].bikes_available;
				}
			}
		}
		return toReturn;
	}

	public int getMaxBikes() {
		int toReturn = 0;
		for( int i=0 ; i<width ; i++ ) {
			for( int j=0 ; j<height ; j++ ) {
				if (parkingStations[i][j].bikes_available>toReturn) {
					toReturn = parkingStations[i][j].bikes_available;
				}
			}
		}
		return toReturn;
	}
	
	public double averageBikes() {
		double total = 0;
		for( int i=0 ; i<width ; i++ ) {
			for( int j=0 ; j<height ; j++ ) {
				total += parkingStations[i][j].bikes_available;
			}
		}
		return total/(width*height);
	}
}
