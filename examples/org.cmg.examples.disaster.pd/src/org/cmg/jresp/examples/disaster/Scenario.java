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
package org.cmg.jresp.examples.disaster;

import java.awt.*;
import java.awt.geom.*;
import java.util.Observable;
import java.util.Random;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
	
	
	public static final int LANDMARK = 0;
	public static final int WORKER = 1;
	protected static final double COMMUNICATION_RANGE = 100.0;

	private Random r = new Random();

	/**
	 * Array of robots: 
	 * - from 0 to numberOfExplorers-1 are explorers
	 * - from numberOfExplorers to numberOfExplorers+numberOfRescuers-1 are rescuers;
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
	
	private Point2D.Double[] victims;
	
	private Rectangle2D.Double nestLocation;

	private int numberOfLandmarks;

	private int numberOfWorkers;

	private int numberOfVictims;
	
	private Polygon[] obstacles;
	
	private int numberOfObstacles;
	
	private Point2D.Double[] victimLocations;

	public Scenario( int numberOfSeekers , int numberOfRescuers , int numberOfVictims , double height , double width ) {
		this.numberOfLandmarks = numberOfSeekers;
		this.numberOfWorkers = numberOfRescuers;
		this.numberOfVictims = numberOfVictims;
		this.height = height;
		this.width = width;
		this.victimLocations = new Point2D.Double[5]; // 5 locazioni possibili della vittima
		this.numberOfObstacles = 9; // 9 fissi nel nuovo scenario
		this.nestLocation = new Rectangle2D.Double(125,400,300,300); // starting zone
	}

	public void init() {
		this.obstacles = new Polygon[numberOfObstacles];
		obstacles[0] = new Polygon(new int[]{120,120,225,225,125,125}, new int[]{700,400,400,405,405,700}, 6); //muro in basso sx
		obstacles[1] = new Polygon(new int[]{430,430,325,325,425,425}, new int[]{700,400,400,405,405,700}, 6); //muro in basso dx
		obstacles[2] = new Polygon(new int[]{125,125,225,225,120,120}, new int[]{0,300,300,305,305,0}, 6); //muro in alto sx
		obstacles[3] = new Polygon(new int[]{425,425,325,325,430,430}, new int[]{0,300,300,305,305,0}, 6); //muro in alto dx
		obstacles[4] = new Polygon(new int[]{0,0,50,50}, new int[]{600,650,650,600}, 4); //ostacolo in basso a sx
		obstacles[5] = new Polygon(new int[]{0,0,50,50}, new int[]{100,200,200,100}, 4); //ostacolo in alto a sx
		obstacles[6] = new Polygon(new int[]{200,200,350,350}, new int[]{150,250,250,150}, 4); //ostacolo in alto al centro
		obstacles[7] = new Polygon(new int[]{500,500,550,550}, new int[]{225,325,325,225}, 4); //ostacolo in alto a dx
		obstacles[8] = new Polygon(new int[]{430,430,480,480}, new int[]{600,650,650,600}, 4); //ostacolo in basso a dx
		
		victimLocations[0] = new Point2D.Double(50,25); // in alto a sx
		victimLocations[1] = new Point2D.Double(500,25); // in alto a dx
		victimLocations[2] = new Point2D.Double(250,50); // in alto centro
		victimLocations[3] = new Point2D.Double(50,675); // in basso sx
		victimLocations[4] = new Point2D.Double(500,675); // in basso a dx
		
		this.victims = new Point2D.Double[numberOfVictims];
		for( int i=0 ; i<numberOfVictims ; i++ ) {
			int roll = 2;//this.r.nextInt(5);
			this.victims[i] = victimLocations[roll];
		}
		robots = new Robot[numberOfLandmarks+numberOfWorkers];
		for( int i=0 ; i<numberOfLandmarks ; i++ ) {
			robots[i] = new Robot(i, 5);
			robots[i].setPosition( width-130-(this.r.nextDouble()*290)  , height-(this.r.nextDouble()*290)  );
		}
		for( int i=+numberOfLandmarks  ; i<numberOfLandmarks+numberOfWorkers ; i++ ) {
			robots[i] = new Robot(i, 5);
			robots[i].setPosition( width-130-(this.r.nextDouble()*290)  , height-(this.r.nextDouble()*290)  );
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
	 * @throws Exception 
	 */
	public void step( double dt ) throws Exception {
		_updatePosition(dt);
		setChanged();
		notifyObservers();
	}

	/**
	 * Methods used to update position of robots.
	 *
	 * @param dt
	 * @throws Exception 
	 */
	private void _updatePosition(double dt) throws Exception {
//		System.out.println("Update positions...");
		for( int i=0 ; i<numberOfLandmarks+numberOfWorkers ; i++ ) {
			if (robots[i].walking) {
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
				int collisionNum=-1;
				int multisampleLevel = 8; // in quanti punti voglio dividere la traiettoria
				Point2D.Double newpos = new Point2D.Double(x,y); // posizione in cui il robot vorrebbe andare
				
				Point2D.Double[] multisampling = multisample (position, newpos, multisampleLevel); // vettore multisampling
				for (int j=1; j<multisampleLevel; j++){ // parte da 1, perch� 0 = pos attuale
					collisionNum = shapesContains(multisampling[j]);
					if (collisionNum>=0) { // se � >=0 ci sono state collisioni
						robots[i].collision=true;
						robots[i].setPosition(multisampling[j-1]); // ultima posizione senza collisioni
						break;
					}
				}
				if (collisionNum==-1) { // se � -1 non ci sono state collisioni
					robots[i].collision=false;
					robots[i].setPosition(newpos);
				}
			}
		}
	}
	
	// Divide una retta in n punti.
	public Point2D.Double[] multisample (Point2D.Double a, Point2D.Double b, int n){
		Point2D.Double[] multi = new Point2D.Double[n];
		multi[0]=a; // iniz primo
		multi[n-1]=b; // iniz ultimo
		double offsetx = (b.x - a.x) / (n-1);
		double offsety = (b.y - a.y) / (n-1);
		for (int i=1; i<n-1; i++){
			multi[i]= new Point2D.Double (multi[i-1].x+offsetx , multi[i-1].y+offsety);
		}
		return multi;
	}
	
	public int shapesContains (Point2D.Double p){ // Controlla se un punto � dentro un ostacolo
		int result=-1;
		for( int i=0 ; i<numberOfObstacles ; i++ ) { // per tutte le shape
			if (obstacles[i].contains(p))
				return i; // ritorna indice della figura
		}
		return result;
	}
	
	public Point2D.Double checkNearestPoint (Point2D.Double orig, Set<Point2D.Double> list){ //agg
		Iterator<Point2D.Double> it = list.iterator(); //iteratore
		Point2D.Double nextP=it.next(); //inizializz
		double minDist=orig.distance(nextP); //
		Point2D.Double result=nextP; //
		while (it.hasNext()) {
			nextP=it.next();
			double dist=orig.distance(nextP);
			if (dist<=minDist) { //cerco il min
				result=nextP;
			}
		}
		return result;
	}

	public Set<Point2D.Double> checkIntersection (Line2D.Double mov) throws Exception{ //agg
		Set<Point2D.Double> intersections = new HashSet<Point2D.Double>(); // lista di intersezioni
		for( int i=0 ; i<numberOfObstacles ; i++ ) { // per tutte le shape
			//intersections = getIntersections (obstacles[i], mov);
			intersections.addAll(getIntersections (obstacles[i], mov)); //prova con addall, se ci sono piu figure intersecate
			//if (intersections.isEmpty()==false){
			//	break; // appena ne trovo una non vuota finisco il ciclo
			//}
		}
	    return intersections;
	}
	
	public Point2D.Double checkIntersection (Line2D.Double mov, int c) throws Exception{
		Set<Point2D.Double> intersections = new HashSet<Point2D.Double>(); // lista di intersezioni
		intersections=getIntersections (obstacles[c], mov);
		Iterator<Point2D.Double> it = intersections.iterator(); //iteratore
		if (it.hasNext()) {
			Point2D.Double nextP=it.next(); //inizializz
			return nextP;
		} else {
			System.out.println("Line: ("+mov.x1+","+mov.y1+"-"+mov.x2+","+mov.y2+")");
			System.out.println("Shape: "+c);
			System.out.println("Contains: "+obstacles[c].contains(mov.getP1()));
			System.exit(-1);
			return null;
		}
	}
	
	 public Set<Point2D.Double> getIntersections(Polygon poly, Line2D.Double line) throws Exception {
	        PathIterator polyIt = poly.getPathIterator(null); //agg
	        double[] coords = new double[6];
	        double[] firstCoords = new double[2];
	        double[] lastCoords = new double[2];
	        Set<Point2D.Double> intersections = new HashSet<Point2D.Double>(); //Lista di inters
	        polyIt.currentSegment(firstCoords);
	        lastCoords[0] = firstCoords[0];
	        lastCoords[1] = firstCoords[1];
	        polyIt.next();
	        while(!polyIt.isDone()) {
	            int type = polyIt.currentSegment(coords);
	            switch(type) {
	                case PathIterator.SEG_LINETO : {
	                    Line2D.Double currentLine = new Line2D.Double(lastCoords[0], lastCoords[1], coords[0], coords[1]);
	                    if(currentLine.intersectsLine(line)){
	                        intersections.add(getIntersection(currentLine, line));}
	                    lastCoords[0] = coords[0];
	                    lastCoords[1] = coords[1];
	                    break;
	                }
	                case PathIterator.SEG_CLOSE : {
	                    Line2D.Double currentLine = new Line2D.Double(coords[0], coords[1], firstCoords[0], firstCoords[1]);
	                    if(currentLine.intersectsLine(line)){
	                        intersections.add(getIntersection(currentLine, line));}
	                    break;
	                }
	                default : {
	                    throw new Exception("Unsupported PathIterator segment type.");
	                }
	            }
	            polyIt.next();
	        }
	        return intersections;
	    }

	    public Point2D.Double getIntersection(Line2D.Double line1, Line2D.Double line2) { //agg
	        double x1,y1, x2,y2, x3,y3, x4,y4;
	        x1 = line1.x1; y1 = line1.y1; x2 = line1.x2; y2 = line1.y2;
	        x3 = line2.x1; y3 = line2.y1; x4 = line2.x2; y4 = line2.y2;
	        double x = ( (x2 - x1)*(x3*y4 - x4*y3) - (x4 - x3)*(x1*y2 - x2*y1))
	        		 / ( (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4) );
	        double y = ( (y3 - y4)*(x1*y2 - x2*y1) - (y1 - y2)*(x3*y4 - x4*y3))
	        		 / ( (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4) );
	        return new Point2D.Double(x, y);
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
				//System.out.println("robot "+i+" New direction received!");
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
		
		private boolean collision; // true se c'� stata una collisione
		
		private static final double VICTIM_SENSOR_RANGE = 15;

		private int i;
		
		private double direction;
		
		private Point2D.Double position;
		
		private double speed;
		
		private boolean walking;
		
		private AbstractSensor victimSensor;
		
		private AbstractSensor collisionSensor;
		
		public Robot( int i , double speed ) {
			this.i = i;
			this.walking = false;
			this.speed = speed;
			this.victimSensor = new AbstractSensor("VictimSensor-"+i ,
					new Template( new ActualTemplateField("VICTIM_PERCEIVED") , new FormalTemplateField(Boolean.class) )) {
			};
			this.collisionSensor = new AbstractSensor("CollisionSensor-"+i,
					new Template( new ActualTemplateField("COLLISION") , new FormalTemplateField(Boolean.class) )) {
			};
			updateCollisionSensor();
			updateVictimSensor();
		}
		
		public void stop() {
			this.walking = false;
		}

		public AbstractSensor getVictimSensor() {
			return victimSensor;
		}

		public AbstractSensor getCollisionSensor() {
			return collisionSensor;
		}

		public synchronized void setDirection(double d) {
			this.direction = d;
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
			updateVictimSensor();
		}
		
		
		public boolean detectVictim() {
			if (position != null) {
				for (Point2D.Double p : victims) {
					if (p.distance(this.position) <= VICTIM_SENSOR_RANGE) {
						return true;
					}
				}
			}
			return false;
		}
		
		
		private void updateVictimSensor() {
			victimSensor.setValue( new Tuple("VICTIM_PERCEIVED" , detectVictim() ));
		}

		private void updateCollisionSensor() {
			if ((position != null)&&((position.x <= 0)||(position.x>=width)||(position.y<=0)||(position.y>=height)||collision)) {
				collisionSensor.setValue( new Tuple( "COLLISION" , true ) );
				walking = false;
			} else {
				collisionSensor.setValue( new Tuple( "COLLISION" , false ));
			}
		}

		public Point2D.Double getPosition() {
			return position;			
		}
				
	}


	public int getSize() {
		return numberOfLandmarks+numberOfWorkers;
	}

	public Color getColor(int i) {
		return Color.BLACK;
	}

	public int getLandmarks() {
		return numberOfLandmarks;
	}

	public int getWorkers() {
		return numberOfWorkers;
	}

	public int getVictims() {
		return numberOfVictims;
	}

	public Point2D.Double getVictimPosition(int i) {
		return victims[i];
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
				try {
					int idSrc = Integer.parseInt(src);
					int idTrg = Integer.parseInt(target);
					Point2D.Double locationSrc = robots[idSrc].getPosition();
					Point2D.Double locationTarget = robots[idTrg].getPosition();
					if ((locationSrc==null)||(locationTarget==null)) {
						return false;
					}
					Line2D.Double mov = new Line2D.Double (locationSrc, locationTarget);
					if (!checkIntersection(mov).isEmpty() ) return false; //agg
					return locationSrc.distance(locationTarget)<COMMUNICATION_RANGE;
				} catch (Exception e) {
					return false;
				}
			}
		};
	}
	
	public Rectangle2D.Double getNest(){
		return nestLocation;
	}

	public int getNumberOfLandmarks() {
		return numberOfLandmarks;
	}
	
	public Polygon getObstacles(int i){
		return obstacles[i];
	}

	public int getNumberOfObstacles(){
		return numberOfObstacles;
	}
	
	public boolean goalReached() {
		for ( int i=numberOfLandmarks ; i<numberOfLandmarks+numberOfWorkers ; i++ ) {
			if (!robots[i].detectVictim()) {
				return false;
			}
		} 
		return true;
	}
	
}
