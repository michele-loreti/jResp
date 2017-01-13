package org.cmg.resp.knowledge2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TupleSpace {
	private Map<Object, TupleNode> elementsTree;
	private TupleSpaceLock lock;

	public TupleSpace() {
		elementsTree = new HashMap<Object, TupleNode>();
		lock = new MOutSReadSIn();

	}

	public boolean put(Tuple t) {
		return put(t, 0);
	}

	protected boolean put(Tuple t, int idx) {
		lock.outLock();
		boolean result = false;
		try {
			TupleNode tn = getTupleNode(t.get(idx));
			tn.add(t, idx);
			result = true;
		} finally {
			lock.outUnlock();
		}
		return result;
	}

	/*
	 * Restituisce TupleNode presente al livello idx dell'albero. Se non è
	 * presente lo crea e lo aggiunge all'albero al livello idx.
	 */
	private TupleNode getTupleNode(Object object) {
		TupleNode n = elementsTree.get(object);
		if (n == null) {
			n = new TupleNode(object);
			elementsTree.put(object, n); // chiave è una tupla
		}
		return n;
	}

	public Tuple get(Template template) throws InterruptedException {
		return get(template, true);
	}

	public Tuple query(Template template) throws InterruptedException {
		return get(template, false);
	}

	private synchronized Tuple get(Template template, boolean remove) throws InterruptedException {
		Tuple t;
		if (remove) {
			while (((t = getTuple(template, 0, remove)) == null)) {
				// wait
			}
		} else {
			t = getTuple(template, 0, remove);
		}
		return t;
	}

	/*
	 * Crea un array di oggetti di lunghezza uguale a quella del template Se
	 * nell'albero è presente la Tupla ricercata, i suoi elementi saranno
	 * memorizzati in tale array
	 */
	protected Tuple getTuple(Template template, int idx, boolean remove) {
		Object[] fields = new Object[template.lenght()];
		getTupleFields(template, idx, fields, remove);
		if (fields == null || fields[0] == null) {
			return null;
		}
		Tuple t = new Tuple(fields);
		return t;
	}

	/*
	 * Modifica l'array passatogli per memorizzare gli elementi richiesti, se
	 * presenti. Agisce in modi differenti in base al tipo di template, attuale
	 * o formale.
	 */
	protected void getTupleFields(Template template, int idx, Object[] tupleFields, boolean remove) {
		TemplateField tf = (TemplateField) template.get(idx);
		if (tf.isActual()) {
			Object o = ((ActualTemplateField) tf).getValue();
			TupleNode tn = elementsTree.get(o);
			if (tn != null) {
				tn.getTupleFields(template, idx, tupleFields, remove);
			} else {
				tupleFields = null;
			}
		} else {
			for (Entry<Object, TupleNode> keyMap : elementsTree.entrySet()) {
				if (tf.match(keyMap.getKey())) {
					keyMap.getValue().getTupleFields(template, idx, tupleFields, remove);
				} // in questo modo si controlla tutto l'albero in ogni caso. Da
					// migliorare
			}
		}
	}

	/*
	 * Metodi seguenti utilizzati per stampa su console. richiamando il metodo
	 * firstStamp stampa su console l'albero attuale.
	 */

	public int getSize() {
		return elementsTree.size();
	}

	public Collection<TupleNode> getTree() {
		return elementsTree.values();
	}

	public void stampEverything(Collection<TupleNode> nodes) {
		LinkedList<TupleSpace> pass = new LinkedList<TupleSpace>();
		LinkedList<TupleNode> arg = new LinkedList<TupleNode>();
		for (Iterator<TupleNode> i = nodes.iterator(); i.hasNext();) {
			TupleNode prova = i.next();
			// if(prova.getNext().getTree().size()!=0){
			pass.add(prova.getNext());
			// }
		}
		for (Iterator<TupleSpace> i = pass.iterator(); i.hasNext();) {
			TupleSpace prova = i.next();
			Collection<TupleNode> ind = prova.getTree();
			for (Iterator<TupleNode> j = ind.iterator(); j.hasNext();) {
				TupleNode ins = j.next();
				arg.add(ins);
				System.out.print(ins.getField() + " count: " + ins.getCount());

			}
			System.out.print(" | ");

		}
		System.out.println();
		if (arg.size() != 0) {
			stampEverything(arg);
		}
	}

	public void firstStamp() {
		Collection<TupleNode> tree = elementsTree.values();
		for (Iterator<TupleNode> i = tree.iterator(); i.hasNext();) {
			TupleNode tn = i.next();
			System.out.print(tn.getField() + " count: " + tn.getCount() + " | ");
		}
		System.out.println();
		stampEverything(tree);
	}
}
