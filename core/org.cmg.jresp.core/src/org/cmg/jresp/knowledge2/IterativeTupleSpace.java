package org.cmg.jresp.knowledge2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class IterativeTupleSpace extends TupleSpace {

	public IterativeTupleSpace() {
		super();
	}

	public IterativeTupleSpace(TupleSpaceLock lock) {
		super(lock);
	}

	protected void put(Tuple t, int idx) throws InterruptedException {
		TupleNode tn = getTupleNode(t.get(0));
		TupleSpace ts = tn.getNext();
		for (int i = 1; i < t.getLength(); i++) {
			ts = tn.getNext();
			tn = ts.getTupleNode(t.get(i));
		}
		tn.doIncrementNodeCounter();
	}

	protected TupleNode getNewTupleNode(Object obj) {
		return new IterativeTupleNode(obj);
	}

	protected Tuple getTuple(Template template, boolean remove) {
		LinkedList<Tuple> tuples = getTupleFields(template, remove, false);
		if (tuples == null || tuples.isEmpty()) {
			return null;
		}
		return tuples.get(0);
	}

	protected LinkedList<Tuple> getAllTuple(Template template, boolean remove) {
		return getTupleFields(template, remove, true);
	}

	//in fase di miglioramento
	private LinkedList<Tuple> getTupleFields(Template template, boolean remove, boolean all) {
		int length = template.lenght();
		LinkedList<Tuple> tuples = new LinkedList<Tuple>();
		Object[] nodi = new Object[length];
		int[] posizioni = new int[length];
		int lvl = 0;
		TupleNode tn;
		TupleSpace ts = this;
		boolean doTupleNode = true;
		ArrayList<TupleNode> tupleNodes = new ArrayList<TupleNode>();
		while (lvl < length) {
			if (doTupleNode) {
				tupleNodes = getNextTupleNode(template.get(lvl), ts);
			}
			if (tupleNodes.isEmpty() || tupleNodes.get(0) == null) {
				if ((lvl=findNode(lvl, posizioni, nodi)) == -1) {
					return tuples;
				}
				tupleNodes = (ArrayList<TupleNode>) nodi[lvl];
			}
			nodi[lvl] = tupleNodes;
			tn = tupleNodes.get(posizioni[lvl]);
			ts = tn.getNext();
			lvl++;
			doTupleNode = true;
			if (lvl == length) {
				Tuple t = getTuple(nodi, posizioni, remove);
				checkTuple(t, tuples);
				if (all) {
					lvl = findNode(lvl, posizioni, nodi);
					if (lvl == -1) {
						return tuples;
					}
					doTupleNode = false;
				}
			}
		}
		return tuples;
	}
	private void checkTuple(Tuple t, LinkedList<Tuple> tuples){
		if (t != null) {
			tuples.add(t);
		}
	}

	private int findNode(int lvl, int[] posizioni, Object[] nodi) {
		boolean end = false;
		while (!end) {
			if (lvl < posizioni.length) {
				posizioni[lvl] = 0;
			}
			lvl--;
			if (lvl < 0) {
				return -1;
			}
			posizioni[lvl]++;
			if (posizioni[lvl] < ((ArrayList<TupleNode>) nodi[lvl]).size()) {
				end = true;
			}
		}
		return lvl;
	}

	private int findNodeArrayList(int lvl, ArrayList<Integer> posizioni, ArrayList<Object> nodi) {
		boolean end = false;
		boolean moreThanOne = false;
		while (!end) {
			if (moreThanOne) {
				nodi.remove(lvl);
			}
			lvl--;
			if (lvl < 0) {
				return -1;
			}
			posizioni.add(lvl, posizioni.get(lvl) + 1);
			if (posizioni.get(lvl) < ((ArrayList<TupleNode>) nodi.get(lvl)).size()) {
				end = true;
			}
			moreThanOne = true;
		}
		return lvl;
	}

	private Tuple getTuple(Object[] tnArray, int[] posizioni, boolean remove) {
		Object[] field = new Object[tnArray.length];
		int lungh = tnArray.length;
		if (remove) {
			TupleNode tn = ((ArrayList<TupleNode>) tnArray[lungh - 1]).get(posizioni[lungh - 1]);
			if (!(remove = tn.checkCount(remove, null))) {
				return null;
			}
		}
		for (int i = 0; i < tnArray.length; i++) {
			field[i] = ((ArrayList<TupleNode>) tnArray[i]).get(posizioni[i]).getField();
		}
		return new Tuple(field);
	}

	private ArrayList<TupleNode> getNextTupleNode(TemplateField tf, TupleSpace ts) {
		ArrayList<TupleNode> list = new ArrayList<TupleNode>();
		TupleNode tn;
		if (tf.isActual()) {
			tn = ts.getTupleNodeWithActual((ActualTemplateField) tf);
			list.add(tn);
		} else {
			Map<Object, TupleNode> tsMap = ts.getMap();
			for (Entry<Object, TupleNode> keyMap : tsMap.entrySet()) {
				if (tf.match(keyMap.getKey())) {
					tn = keyMap.getValue();
					list.add(tn);
				}

			}
		}
		return list;
	}

	private ArrayList<TupleNode> getNextTupleNodeWithoutTemplate(TupleSpace ts) {
		ArrayList<TupleNode> list = new ArrayList<TupleNode>();
		TupleNode tn;
		Map<Object, TupleNode> tsMap = ts.getMap();
		for (Entry<Object, TupleNode> keyMap : tsMap.entrySet()) {
			tn = keyMap.getValue();
			list.add(tn);
		}
		return list;
	}

	protected LinkedList<Tuple> getAll(boolean remove) {
		LinkedList<Tuple> tuples = new LinkedList<Tuple>();
		ArrayList<Object> nodi = new ArrayList<Object>();
		ArrayList<Integer> posizioni = new ArrayList<Integer>();
		boolean end = true;
		int lvl = 0;
		posizioni.add(lvl, 0);
		TupleNode tn;
		TupleSpace ts = this;
		ArrayList<TupleNode> tupleNodes = new ArrayList<TupleNode>();
		while (end) {
			tupleNodes = getNextTupleNodeWithoutTemplate(ts);
			if (tupleNodes.isEmpty() || tupleNodes.get(0) == null) {
				Tuple t = getTuple(nodi, posizioni, remove);
				checkTuple(t, tuples);
				lvl = findNodeArrayList(lvl, posizioni, nodi);
				if (lvl == -1) {
					return tuples;
				}
				tupleNodes = (ArrayList<TupleNode>) nodi.get(lvl);
				nodi.set(lvl, tupleNodes);
			} else {
				nodi.add(lvl, tupleNodes);
			}
			tn = tupleNodes.get(posizioni.get(lvl));
			ts = tn.getNext();
			lvl++;
			posizioni.add(lvl, 0);
		}
		return tuples;
	}

	private Tuple getTuple(ArrayList<Object> tnArray, ArrayList<Integer> posizioni, boolean remove) {
		Object[] field = new Object[tnArray.size()];
		int lungh = tnArray.size();
		TupleNode tn = ((ArrayList<TupleNode>) tnArray.get(lungh - 1)).get(posizioni.get(lungh - 1));
		if (!(remove = tn.checkCount(remove, null)) || tn.getCount() == 0) {
			return null;
		}
		for (int i = 0; i < lungh; i++) {
			field[i] = ((ArrayList<TupleNode>) tnArray.get(i)).get(posizioni.get(i)).getField();
		}
		return new Tuple(field);
	}

	@Override
	protected void getTupleFields(Template template, int idx, Object[] tupleFields, boolean remove, boolean all,
			LinkedList<Tuple> tuples) {
	}

	@Override
	protected void getAllTuple(int idx, LinkedList<Object> tupleFields, LinkedList<Tuple> tupleList, boolean remove) {
	}

	@Override
	protected TupleSpace getNewTupleSpace() {
		return new IterativeTupleSpace();
	}

}
