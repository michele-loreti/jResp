/**
 * 
 */
package org.cmg.res.examples.construction.behaviour;

/**
 * @author loreti
 *
 */
public class AverageTimeCollector {

	private int count = 0;
	private double value = 0.0;
	
	public AverageTimeCollector() {
		
	}
	
	public void store( double value ) {
		this.value += value;
		this.count++;
	}
	
	public double average() {
		return value/count;
	}
	
}
