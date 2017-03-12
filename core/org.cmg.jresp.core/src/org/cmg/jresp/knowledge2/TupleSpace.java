package org.cmg.jresp.knowledge2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.*;

public abstract class TupleSpace implements Knowledge {
	protected TupleSpaceLock lock;
	protected Map<Object, TupleNode> elementsTree;

	public TupleSpace() {
		lock = new EsclusiveLock();
		elementsTree = new HashMap<Object, TupleNode>();
	}

	public TupleSpace(TupleSpaceLock lock) {
		this.lock = lock;
		elementsTree = new HashMap<Object, TupleNode>();
	}

	public boolean put(Tuple t) throws InterruptedException {
		lock.outLock();
		try {
			 put(t, 0);
		}finally {
			lock.outUnlock();
		}
		return true;
	}

	protected abstract void put(Tuple t, int idx) throws InterruptedException;

	/*
	 * Restituisce TupleNode presente al livello idx dell'albero. Se non è
	 * presente lo crea e lo aggiunge all'albero al livello idx.
	 */

	protected TupleNode getTupleNode(Object object) {
		TupleNode n = elementsTree.get(object);
		if (n == null) {
			n = getNewTupleNode(object);
			elementsTree.put(object, n);
		}
		return n;
	}
	
	protected abstract TupleNode getNewTupleNode(Object obj);
	
	public Tuple get(Template template) throws InterruptedException {
		Tuple t;
		lock.inLock();
		try {
			while (((t = getTuple(template, true)) == null)) {
				lock.getWaitTuple();
			}	
		} finally {
			lock.inUnlock();
		}
		return t;
	}

	public Tuple query(Template template) throws InterruptedException {
		Tuple t;
		lock.readLock();
		try {
			while (((t = getTuple(template, true)) == null)) {
				lock.readWaitTuple();
			}	
		} finally {
			lock.readUnlock();
		}
		return t;
	}
	public Tuple getp(Template template) throws InterruptedException{
		Tuple t;
		lock.inLock();
		try {
			t = getTuple(template, true);		
		} finally {
			lock.inUnlock();
		}
		return t;
	}
	public Tuple queryp(Template template) throws InterruptedException{
		Tuple t;
		lock.readLock();
		try {
			t = getTuple(template, false);		
		} finally {
			lock.readUnlock();
		}
		return t;
	}

	/*
	 * Crea un array di oggetti di lunghezza uguale a quella del template Se
	 * nell'albero è presente la Tupla ricercata, i suoi elementi saranno
	 * memorizzati in tale array
	 */
	protected abstract Tuple getTuple(Template template, boolean remove);

	/*
	 * Modifica l'array passatogli per memorizzare gli elementi richiesti, se
	 * presenti. Agisce in modi differenti in base al tipo di template, attuale
	 * o formale.
	 */
	protected abstract void getTupleFields(Template template, int idx, Object[] tupleFields, boolean remove, boolean all,LinkedList<Tuple> tuples);

	public LinkedList<Tuple> getAll(Template template) throws InterruptedException {
		LinkedList<Tuple> tupleList;
		lock.inLock();
		try {
			tupleList = getAllTuple(template,true);	
		} finally {
			lock.inUnlock();
		}
		return tupleList;
	}

	public LinkedList<Tuple> queryAll(Template template) throws InterruptedException {
		LinkedList<Tuple> tupleList;
		lock.readLock();
		try {
			tupleList = getAllTuple(template, false);	
		} finally {
			lock.readUnlock();
		}
		return tupleList;
	}
	protected abstract LinkedList<Tuple> getAllTuple(Template template, boolean remove);
		
	protected abstract LinkedList<Tuple> getAll(boolean remove);

	public LinkedList<Tuple> getAll() throws InterruptedException {
		LinkedList<Tuple> tupleList;
		lock.inLock();
		try {
			tupleList = getAll(true);	
		} finally {
			lock.inUnlock();
		}
		return tupleList;
	}

	public LinkedList<Tuple> queryAll() throws InterruptedException {
		LinkedList<Tuple> tupleList;
		lock.readLock();
		try {
			tupleList = getAll(false);	
		} finally {
			lock.readUnlock();
		}
		return tupleList;
	}	
	
	protected abstract void getAllTuple(int idx, LinkedList<Object> tupleFields, LinkedList<Tuple> tupleList, boolean remove);


	public boolean isEmpty() {
		return elementsTree.isEmpty();
	}
	
	protected TupleNode getTupleNodeWithActual(ActualTemplateField tf){
		Object o = tf.getValue();
		return elementsTree.get(o);
	}
		
	public TupleSpace map(Function<Tuple, Tuple> f) throws InterruptedException {
		TupleSpace mapped = getNewTupleSpace();
		LinkedList<Tuple> t = queryAll();
		for (Tuple tuple : t) {
			mapped.put(f.apply(tuple));
		}
		return mapped;
	}
	
	protected abstract TupleSpace getNewTupleSpace();
	
	public TupleSpace map(Template template, Function<Tuple, Tuple> f) throws InterruptedException {
		TupleSpace mapped = getNewTupleSpace();
		LinkedList<Tuple> t = queryAll(template);
		for (Tuple tuple : t) {
			mapped.put(f.apply(tuple));
		}
		return mapped;
	}
	
	public <T1> T1 reduce(BiFunction<Tuple, T1, T1> f, Comparator<Tuple> comp, T1 v) throws InterruptedException {
		LinkedList<Tuple> tuple = queryAll();
		tuple.sort(comp);
		for (int i = 0; i < tuple.size(); i++) {
			v = f.apply(tuple.get(i), v);
		}
		return v;
	}

	
	public LinkedList<Tuple> queryAll(Template template, Comparator<Tuple> comp) throws InterruptedException {
		LinkedList<Tuple> tuple = queryAll(template);
		tuple.sort(comp);
		return tuple;
	}

	public LinkedList<Tuple> queryAll(Comparator<Tuple> comp) throws InterruptedException {
		LinkedList<Tuple> tuple = queryAll();
		tuple.sort(comp);
		return tuple;
	}

	protected Map<Object, TupleNode>  getMap(){
		return elementsTree;
	}

}
