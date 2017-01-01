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
package org.cmg.jresp.simulation;

import java.util.LinkedList;
import java.util.Random;

/**
 * @author loreti
 *
 */
public class RaceCondition<T> {
	
	private LinkedList<WeightedElement> elements;
	
	private double total;

	private Random r;

	private T selectedElement;

	private double time;
	
	public RaceCondition( Random r ) {
		this.elements = new LinkedList<WeightedElement>();
		this.r = r;
	}

	
	public void add( T element , double weight ) {
		add( new WeightedElement(element, weight) );
	}
	
	

	private void add(WeightedElement weightedElement) {
		total += weightedElement.weigth;
		elements.add(weightedElement);
	}

	public synchronized void select() {
		selectedElement = _select();		
		time = (1/total)*Math.log(r.nextDouble());
	}

	private T _select() {
		double u = r.nextDouble();
		double value = total*u;
		for (WeightedElement we : elements) {
			if (we.weigth<value) {
				return we.element; 
			} else {
				value -= we.weigth;
			}
		}
		return null;
	}

	public class WeightedElement {
		
		private double weigth;
		
		private T element;
		
		public WeightedElement( T element , double weight ) {
			this.element = element;
			this.weigth = weight;
		}
		
	}
	
}
