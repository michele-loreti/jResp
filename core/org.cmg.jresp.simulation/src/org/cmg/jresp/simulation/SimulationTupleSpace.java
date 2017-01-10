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

import java.util.ArrayList;
import java.util.LinkedList;

import org.cmg.jresp.knowledge.KnowledgeManager;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;

/**
 * @author loreti
 *
 */
public class SimulationTupleSpace implements KnowledgeManager {
	
	private ArrayList<Tuple> tuples;
	private ElementSelector selector;

	public SimulationTupleSpace( ElementSelector selector ) {
		this.tuples = new ArrayList<Tuple>();
		this.selector = selector;
	}
	
	@Override
	public boolean put(Tuple t) {
		this.tuples.add(t);
		return true;
	}

	@Override
	public Tuple get(Template template) {
		ArrayList<Integer> indexes =  getMatchingIndexes( template );
		if (indexes.size()>0) {
			return tuples.remove(selector.select(indexes).intValue());
		} else {
			return null;
		}
	}

	private ArrayList<Integer> getMatchingIndexes(Template template) {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0 ; i<this.tuples.size() ; i++ ) {
			if (template.match( tuples.get(i) )) {
				indexes.add(i);
			}
		}
		return indexes;
	}

	@Override
	public Tuple getp(Template template) {
		return get(template);
	}

	@Override
	public LinkedList<Tuple> getAll(Template template) {
		ArrayList<Tuple> newtuples = new ArrayList<Tuple>();
		LinkedList<Tuple> toReturn = new LinkedList<Tuple>();
		for (Tuple tuple : this.tuples) {
			if (template.match(tuple)) {
				toReturn.add(tuple);
			} else {
				newtuples.add(tuple);
			}
		}
		this.tuples = newtuples;
		return toReturn;

	}

	@Override
	public Tuple query(Template template) {
		ArrayList<Integer> indexes =  getMatchingIndexes( template );
		if (indexes.size()>0) {
			return tuples.get(selector.select(indexes).intValue());
		} else {
			return null;
		}
	}

	@Override
	public Tuple queryp(Template template) {
		return query(template);
	}

	@Override
	public LinkedList<Tuple> queryAll(Template template) {
		LinkedList<Tuple> toReturn = new LinkedList<Tuple>();
		for (Tuple tuple : this.tuples) {
			if (template.match(tuple)) {
				toReturn.add(tuple);
			} 
		}
		return toReturn;
	}

	@Override
	public Tuple[] getKnowledgeItems() {
		return tuples.toArray(new Tuple[tuples.size()]);
	}
	
	

}
