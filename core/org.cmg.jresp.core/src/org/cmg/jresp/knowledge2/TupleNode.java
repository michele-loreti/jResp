package org.cmg.jresp.knowledge2;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

	private synchronized void doIncrementNodeCounter() {
		this.count++;
//		this.notifyAll();
	}

	public String getField() {
		return field.toString();
	}

	/*
	 * Memorizza alla posizione idx, dell'array passatogli, il field dell'istanza del tuplenode.
	 * Controlla se è l'ultima posizione dell'array, in caso di risposta affermativa controlla il count del nodo,
	 * altrimenti richiama ricorsivamente il metodo getTupleFields nel suo sottoalbero.
	 */
	public void getTupleFields(Template template, int idx, Object[] tupleFields, boolean remove) {
		tupleFields[idx] = field;
		int lenghtTemplate = template.lenght() - 1;
		if (idx == lenghtTemplate) {
			checkCount(remove, tupleFields);
		} else {
			next.getTupleFields(template, idx + 1, tupleFields, remove);
		}
	}

	private void checkCount(Boolean remove, Object[] tupleFields) {
		if (count > 0) {
			if (remove) {
				count--;
			}
		} else if (remove) {
			tupleFields[0] = null;
		}
	}

	public String toString() {
		return ("(" + field.toString() + "(" + next.toString() + ")" + ")");
	}
}
