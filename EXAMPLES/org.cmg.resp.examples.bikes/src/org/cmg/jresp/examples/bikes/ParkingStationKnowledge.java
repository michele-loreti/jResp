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

import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.KnowledgeManager;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.TemplateField;
import org.cmg.jresp.knowledge.Tuple;

/**
 * @author loreti
 *
 */
public class ParkingStationKnowledge implements KnowledgeManager {
	
	protected int bikes_available;
	
	protected int bikes_reserved;
	
	protected int slots_available;
	
	protected int slots_reserved;
	
	public ParkingStationKnowledge( int bikes_abailable , int bikes_reserved , int slots_available , int slots_reserved ) {
		this.bikes_available = bikes_abailable;
		this.bikes_reserved = bikes_reserved;
		this.slots_available = slots_available;
		this.slots_reserved = slots_reserved;
	}

	@Override
	public boolean put(Tuple t) {
		if (isAddBikeTuple(t)&&this.slots_reserved>0) {
			this.bikes_available++;
			this.slots_reserved--;
			return true;
		}
		return false;
	}

	private boolean isAddBikeTuple(Tuple t) {
		return (t.length()==1)&&("BIKE".equals(t.getElementAt(0)));
	}

	@Override
	public Tuple get(Template template) {
		if (isGetBikeTemplate(template)&&(this.bikes_reserved>0)) {
			this.bikes_reserved--;
			this.slots_available++;
			return new Tuple();
		}
		if (isReserveBikeTemplate(template)&&(this.bikes_available>0)) {
			this.bikes_available--;
			this.bikes_reserved++;
			return new Tuple();
		}
		if (isReserveSlotTemplate(template)&&(this.slots_available>0)) {
			this.bikes_available--;
			this.bikes_reserved++;
			return new Tuple();
		}
		return null;
	}

	private boolean isReserveBikeTemplate(Template template) {
		if (template.length()!=1) {
			return false;
		}
		TemplateField field = template.getElementAt(0);
		if (!(field instanceof ActualTemplateField)) {
			return false;
		}
		return "RESERVE_BIKE".equals(((ActualTemplateField) field).getValue());
	}

	private boolean isReserveSlotTemplate(Template template) {
		if (template.length()!=1) {
			return false;
		}
		TemplateField field = template.getElementAt(0);
		if (!(field instanceof ActualTemplateField)) {
			return false;
		}
		return "RESERVE_SLOT".equals(((ActualTemplateField) field).getValue());
	}

	private boolean isGetBikeTemplate(Template template) {
		if (template.length()!=1) {
			return false;
		}
		TemplateField field = template.getElementAt(0);
		if (!(field instanceof ActualTemplateField)) {
			return false;
		}
		return "BIKE".equals(((ActualTemplateField) field).getValue());
	}

	@Override
	public Tuple getp(Template template) {
		return get(template);
	}

	@Override
	public LinkedList<Tuple> getAll(Template template) {
		LinkedList<Tuple> tuples = new LinkedList<Tuple>();
		tuples.add(get(template));
		return tuples;
	}

	@Override
	public Tuple query(Template template) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tuple queryp(Template template) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedList<Tuple> queryAll(Template template) {
		return new LinkedList<Tuple>();
	}

	@Override
	public Tuple[] getKnowledgeItems() {
		return null;
	}

}
