package org.cmg.jresp.knowledge2;

import java.util.LinkedList;
import java.util.Map.Entry;

public class RecursiveTupleSpace extends TupleSpace {

	public RecursiveTupleSpace() {
		super();
	}

	public RecursiveTupleSpace(TupleSpaceLock lock) {
		super(lock);
	}

	protected void put(Tuple t, int idx) throws InterruptedException {
		TupleNode tn = getTupleNode(t.get(idx));
		tn.add(t, idx);
	}


	protected Tuple getTuple(Template template, boolean remove) {
		Object[] fields = new Object[template.lenght()];
		getTupleFields(template, 0, fields, remove,false, null);
		if (fields == null || fields[0]==null) {
			return null;
		}
		Tuple t = new Tuple(fields);
		return t;
	}

	protected TupleNode getNewTupleNode(Object obj){
		return new RecursiveTupleNode(obj);
	}

	protected void getTupleFields(Template template, int idx, Object[] tupleFields, boolean remove, boolean all, LinkedList<Tuple> tuples) {
		TemplateField tf = template.get(idx);
		if (tf.isActual()) {
			getTupleWithActualTemplate(template, idx, tupleFields, remove,all,tuples);
		} else {
			getTupleWithFormalTemplate(template, idx, tupleFields, remove,all,tuples);
		}
	}

	private void getTupleWithActualTemplate(Template template, int idx, Object[] tupleFields, boolean remove, boolean all, LinkedList<Tuple> tuples) {
		TupleNode tn = getTupleNodeWithActual((ActualTemplateField)template.get(idx));
		if (tn != null) {
			boolean end = tn.getTupleFields(template, idx, tupleFields, remove,all, tuples);
			if(end && all){
				Object[] field=tupleFields.clone();
				tuples.add(new Tuple(field));
			}
		} else {
			tupleFields = null;
		}
	}
	private void getTupleWithFormalTemplate(Template template, int idx, Object[] tupleFields, boolean remove, boolean all,LinkedList<Tuple> tuples){
		TemplateField tf = template.get(idx);
		boolean end = false;
		for (Entry<Object, TupleNode> keyMap : elementsTree.entrySet()) {
			if (tf.match(keyMap.getKey()) && (!end || all)) {
				TupleNode tn = keyMap.getValue();
				end = tn.getTupleFields(template, idx, tupleFields, remove,all,tuples);
				if(end && all){
					Object[] field=tupleFields.clone();
					tuples.add(new Tuple(field));
				}
			}
		}
	}
	

	protected LinkedList<Tuple> getAllTuple(Template template, boolean remove){
		Object[] fields = new Object[template.lenght()];
		LinkedList<Tuple> tuples = new LinkedList<Tuple>();
		getTupleFields(template, 0, fields, remove, true, tuples);
		return tuples;
		}
	
	protected LinkedList<Tuple> getAll(boolean remove) {
		LinkedList<Object> tupleFields = new LinkedList<Object>();
		LinkedList<Tuple> tupleList = new LinkedList<Tuple>();
		getAllTuple(0,tupleFields,tupleList,remove);
		return tupleList;
	}
	
	protected void getAllTuple(int idx, LinkedList<Object> tupleFields, LinkedList<Tuple> tupleList, boolean remove){
		for (Entry<Object, TupleNode> keyMap : elementsTree.entrySet()) {
			TupleNode tn = keyMap.getValue();
			tn.getAllTuple(idx, tupleFields, tupleList, remove);
		}
	}
	
	protected TupleSpace getNewTupleSpace(){
		return new RecursiveTupleSpace();
	}

	

}
