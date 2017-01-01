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
package org.cmg.jresp.simulation;

import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.GroupPredicate;

/**
 * @author loreti
 *
 */
public class DeterministicDelayFactory implements IDelayFactory {

	private double delay;

	public DeterministicDelayFactory(double delay) {
		this.delay = delay;
	}

	@Override
	public double getRetryTime(SimulationNode node) {
		return delay;
	}

	@Override
	public double getLocalGetTime(SimulationNode node,Template t ) {
		return delay;
	}

	@Override
	public double getRemoteGetTime(SimulationNode src, Template t,
			SimulationNode trg) {
		return delay;
	}

	@Override
	public double getGroupGetTime(SimulationNode src, Template t,
			GroupPredicate target, SimulationNode trg) {
		return delay;
	}

	@Override
	public double getLocalPutTime(SimulationNode node, Tuple t) {
		return delay;
	}

	@Override
	public double getRemotePutTime(SimulationNode src, Tuple t,
			SimulationNode trg) {
		return delay;
	}

	@Override
	public double getGroupPutTime(SimulationNode src, Tuple t, GroupPredicate target) {
		return delay;
	}

	@Override
	public double getLocalQueryTime(SimulationNode node, Template t) {
		return delay;
	}

	@Override
	public double getRemoteQueryTime(SimulationNode src, Template t,
			SimulationNode trg) {
		return delay;
	}

	@Override
	public double getGroupQueryTime(SimulationNode src, Template t,
			GroupPredicate target, SimulationNode trg) {
		return delay;
	}

	
}
