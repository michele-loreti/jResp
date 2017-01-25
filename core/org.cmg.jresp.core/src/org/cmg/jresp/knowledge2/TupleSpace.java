package org.cmg.jresp.knowledge2;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.*;

//quando faccio get di una tupla non presente?
//waitTuple in TupleSpaceLock?
public class TupleSpace {
	private Map<Object, TupleNode> elementsTree;
	private TupleSpaceLock lock;

	public TupleSpace() {
		elementsTree = new HashMap<Object, TupleNode>();
		lock = new MOutSReadSIn();
	}

	public TupleSpace(TupleSpaceLock lock) {
		elementsTree = new HashMap<Object, TupleNode>();
		this.lock = lock;
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

	public Tuple get(Template template) throws Exception {
		return get(template, true);
	}

	public Tuple query(Template template) throws Exception {
		return get(template, false);
	}

	// Dove va interrupted exception?????
	private Tuple get(Template template, boolean remove) throws Exception {
		Tuple t;
		if (remove) {
			while (((t = getTuple(template, 0, remove)) == null)) {
				// da capire come fare wait
				throw new Exception("tupla non presente");
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
		getTupleFields(template, idx, fields, null, remove);
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
	protected void getTupleFields(Template template, int idx, Object[] tupleFields, LinkedList<Tuple> tupleList,
			boolean remove) {
		TemplateField tf = (TemplateField) template.get(idx);
		if (tf.isActual()) {
			getTupleWithActualTemplate(template, idx, tupleFields, remove, (ActualTemplateField) tf);
		} else {
			for (Entry<Object, TupleNode> keyMap : elementsTree.entrySet()) {
				if (tf.match(keyMap.getKey())) {
					keyMap.getValue().getTupleFields(template, idx, tupleFields, tupleList, remove);
				}
			}
		}
	}

	private void getTupleWithActualTemplate(Template template, int idx, Object[] tupleFields, boolean remove,
			ActualTemplateField tf) {
		Object o = tf.getValue();
		TupleNode tn = elementsTree.get(o);
		if (tn != null) {
			tn.getTupleFields(template, idx, tupleFields, null, remove);
		} else {
			tupleFields = null;
		}
	}

	public LinkedList<Tuple> getAll(Template template) {
		return getAllTuple(template, 0, true);
	}

	public LinkedList<Tuple> queryAll(Template template) {
		return getAllTuple(template, 0, false);
	}

	protected LinkedList<Tuple> getAllTuple(Template template, int idx, boolean remove) {
		LinkedList<Tuple> tupleList = new LinkedList<Tuple>();
		Object[] tupleFields = new Object[template.lenght()];
		getTupleFields(template, idx, tupleFields, tupleList, remove);
		if (tupleList.isEmpty()) {
			return null;
		}
		return tupleList;
	}

	public TupleSpace map(Function<Tuple, Tuple> f) {
		TupleSpace mapped = new TupleSpace();
		LinkedList<Tuple> tupleList = new LinkedList<Tuple>();
		LinkedList<Object> tupleFields = new LinkedList<Object>();
		LinkedList<Tuple> t = getAll(0, tupleFields, tupleList, false);
		for (Tuple tuple : t) {
			mapped.put(f.apply(tuple));
		}
		return mapped;
	}

	public TupleSpace map(Template template, Function<Tuple, Tuple> f) {
		TupleSpace mapped = new TupleSpace();
		LinkedList<Tuple> t = getAll(template);
		for (Tuple tuple : t) {
			mapped.put(f.apply(tuple));
		}
		return mapped;

	}

	protected LinkedList<Tuple> getAll(int idx, LinkedList<Object> tupleFields, LinkedList<Tuple> tupleList,
			boolean remove) {
		for (Entry<Object, TupleNode> keyMap : elementsTree.entrySet()) {
			keyMap.getValue().getAllTuple(idx, tupleFields, tupleList, remove);
		}
		return tupleList;
	}

	public LinkedList<Tuple> getAll() {
		LinkedList<Tuple> tupleList = new LinkedList<Tuple>();
		LinkedList<Object> tupleFields = new LinkedList<Object>();
		LinkedList<Tuple> t = getAll(0, tupleFields, tupleList, true);
		return t;
	}

	public LinkedList<Tuple> queryAll() {
		LinkedList<Tuple> tupleList = new LinkedList<Tuple>();
		LinkedList<Object> tupleFields = new LinkedList<Object>();
		LinkedList<Tuple> t = getAll(0, tupleFields, tupleList, false);
		return t;
	}

	public boolean isEmpty() {
		return elementsTree.isEmpty();
	}

	public <T1> T1 reduce(BiFunction<Tuple, T1, T1> f, Comparator<Tuple> comp, T1 v) {
		LinkedList<Tuple> tuple = queryAll();
		tuple.sort(comp);
		for (int i = 0; i < tuple.size(); i++) {
			v = f.apply(tuple.get(i), v);
		}

		return v;
	}

	public LinkedList<Tuple> queryAll(Template template, Comparator<Tuple> comp) {
		LinkedList<Tuple> tuple = queryAll(template);
		tuple.sort(comp);
		return tuple;
	}

	public LinkedList<Tuple> queryAll(Comparator<Tuple> comp) {
		LinkedList<Tuple> tuple = queryAll();
		tuple.sort(comp);
		return tuple;
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
