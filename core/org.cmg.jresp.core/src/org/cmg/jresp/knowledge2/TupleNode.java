package org.cmg.jresp.knowledge2;

import java.util.LinkedList;

public abstract class TupleNode {
	protected Object field;
	protected int count;
	protected TupleSpace next;

	public TupleNode(){
		count = 0;
		field = new Object();
	}

	public TupleNode(Object field) {
		count=0;
		this.field = field;
	}

	public void increaseCount() {
		count++;
	}

	public int getCount() {
		return count;
	}

	public TupleSpace getNext() {
		return next;
	}
	public void setNext(TupleSpace next){
		this.next = next;
	}

	public void add(Tuple t, int idx) throws InterruptedException {
		int next = idx + 1;
		if (next == t.getLength()) {
			doIncrementNodeCounter();
		} else {
			this.next.put(t, next);
		}
	}


	protected void doIncrementNodeCounter() {
		this.count++;
	}

	public Object getField() {
		return field;
	}

	/*
	 * Memorizza alla posizione idx, dell'array passatogli, il field
	 * dell'istanza del tuplenode. Controlla se è l'ultima posizione dell'array,
	 * in caso di risposta affermativa controlla il count del nodo, altrimenti
	 * richiama ricorsivamente il metodo getTupleFields nel suo sottoalbero. Se
	 * la linked list è diversa da null, una volta arrivato ad una foglia
	 * dell'albero del tupleSpace, agginge la tupla ad una lista.
	 */
	// troppi parametri da migliorare
	public boolean getTupleFields(Template template, int idx, Object[] tupleFields, boolean remove,boolean all,LinkedList<Tuple> tuples) {
		tupleFields[idx] = field;
		int lenghtTemplate = template.lenght() - 1;
		if (idx == lenghtTemplate) {
			return checkCount(remove, tupleFields);
		} else {
			next.getTupleFields(template, idx + 1, tupleFields, remove,all, tuples);
		}
		return false;
	}

	public boolean checkCount(Boolean remove, Object[] tupleFields) {
		if (count > 0 && remove) {
			count--;
		} else if (remove) {
			if(tupleFields!=null){
			tupleFields[0]= null;
			}
			return false;
		}
		return true;
	}
	
	private void addTupleToLinkedList(Object[] tupleFields, LinkedList<Tuple> tupleList) {
		Tuple t = new Tuple(tupleFields);
		tupleList.add(t);
	}

	/*
	 * Funzionamento analogo al metodo getTupleFields. Unica differenza invece
	 * di un array ho una lista per memorizzare gli elementi della tupla, in
	 * quanto le dimensioni della tupla saranno variabili
	 */
	// troppi parametri da migliorare
	public void getAllTuple(int idx, LinkedList<Object> tupleFields, LinkedList<Tuple> tupleList, boolean remove) {
		tupleFields.add(field);
		if (next.isEmpty()) {
			addTupleToLinkedList(tupleFields, tupleList, remove);
		} else {
			next.getAllTuple(idx + 1, tupleFields, tupleList, remove);
			tupleFields.removeLast();
		}
	}

	protected void addTupleToLinkedList(LinkedList<Object> tupleFields, LinkedList<Tuple> tupleList, boolean remove) {
		Tuple t = new Tuple(doArray(tupleFields));
		for (int i = 0; i < count; i++) {
			tupleList.add(t);
		}
		tupleFields.removeLast();
		if (remove) {
			count = 0;
		}
	}

	protected Object[] doArray(LinkedList<Object> tupleFields) {
		Object[] t = new Object[tupleFields.size()];
		int i = 0;
		for (Object field : tupleFields) {
			t[i] = field;
			i++;
		}
		return t;
	}

	public String toString() {
		return ("(" + field.toString() + "(" + next.toString() + ")" + ")");
	}
}
