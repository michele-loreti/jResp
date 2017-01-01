/**
 * Copyright (c) 2013 Concurrency and Mobility Group.
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
package org.cmg.jresp.examples.cloud;

/**
 * @author loreti
 *
 */
public class CloudService {
	
	private String name;
	private int memory;
	private double cpu;

	public CloudService( String name , int memory , double cpu ) {
		this.name = name;
		this.memory = memory;
		this.cpu = cpu;
	}
	

	public int getMemory() {
		return memory;
	}

	public double getCPULoad() {
		return cpu;
	}
	
	public String getName() {
		return name;
	}

}
