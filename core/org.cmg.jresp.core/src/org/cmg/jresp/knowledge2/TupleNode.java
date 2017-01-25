package org.cmg.jresp.knowledge2;

import java.util.LinkedList;

public class TupleNode {
	private Object field;
	private int count;
	private TupleSpace next;

	public TupleNode() {
		this.next = new TupleSpace();
		this.field = new Object();
	}

	public TupleNode(Object field) {
		this.next = new TupleSpace();
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

	public void add(Tuple t, int idx) {
		int next = idx + 1;
		if (next == t.length()) {
			doIncrementNodeCounter();
		} else {
			this.next.put(t, next);
		}
	}

	protected void doIncrementNodeCounter() {
		this.count++;
	}

	public String getField() {
		return field.toString();
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
	public void getTupleFields(Template template, int idx, Object[] tupleFields, LinkedList<Tuple> tupleList,
			boolean remove) {
		tupleFields[idx] = field;
		int lenghtTemplate = template.lenght() - 1;
		if (idx == lenghtTemplate) {
			checkCount(remove, tupleFields);
			if (tupleList != null && tupleFields[0] != null) {
				addTupleToLinkedList(tupleFields, tupleList);
			}
		} else {
			next.getTupleFields(template, idx + 1, tupleFields, tupleList, remove);
		}
	}

	protected void addTupleToLinkedList(Object[] tupleFields, LinkedList<Tuple> tupleList) {
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
			next.getAll(idx + 1, tupleFields, tupleList, remove);
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

	protected void checkCount(Boolean remove, Object[] tupleFields) {
		if (count > 0 && remove) {
			count--;
		} else if (remove) {
			tupleFields[0] = null;
		}
	}

	public String toString() {
		return ("(" + field.toString() + "(" + next.toString() + ")" + ")");
	}
}
