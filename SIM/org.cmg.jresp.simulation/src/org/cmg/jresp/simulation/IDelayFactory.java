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
package org.cmg.jresp.simulation;

import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.GroupPredicate;

/**
 * @author loreti
 *
 */
public interface IDelayFactory {

	double getRetryTime(SimulationNode node);

	double getLocalGetTime(SimulationNode node , Template t );

	double getRemoteGetTime(SimulationNode src, Template t, SimulationNode trg);

	double getGroupGetTime(SimulationNode src, Template t, GroupPredicate target , SimulationNode trg );

	double getLocalPutTime(SimulationNode node, Tuple t );

	double getRemotePutTime(SimulationNode src, Tuple t, SimulationNode trg);

	double getGroupPutTime(SimulationNode src, Tuple t, GroupPredicate target );

	double getLocalQueryTime(SimulationNode node , Template t );

	double getRemoteQueryTime(SimulationNode src, Template t, SimulationNode trg);

	double getGroupQueryTime(SimulationNode src, Template t, GroupPredicate target, SimulationNode trg);

}
