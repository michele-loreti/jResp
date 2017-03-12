package org.cmg.jresp.knowledge2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class TemplateIndexedTupleSpace implements Knowledge {
	private Map<Tuple, TupleContainer> containerMap;
	private Map<Template, LinkedList<TupleContainer>> tupleMap;
	private TupleSpaceLock lock;

	public TemplateIndexedTupleSpace() {
		containerMap = new HashMap<Tuple, TupleContainer>();
		tupleMap = new HashMap<Template, LinkedList<TupleContainer>>();
		lock = new EsclusiveLock();
	}

	public TemplateIndexedTupleSpace(TupleSpaceLock lock) {
		containerMap = new HashMap<Tuple, TupleContainer>();
		tupleMap = new HashMap<Template, LinkedList<TupleContainer>>();
		this.lock = lock;
	}

	@Override
	public boolean put(Tuple t) throws InterruptedException {
		boolean result;
		lock.outLock();
		try {
			TupleContainer container = containerMap.get(t);
			result = addTuple(container, t);
		} finally {
			lock.outUnlock();
		}
		return result;
	}

	private boolean addTuple(TupleContainer container, Tuple t) {
		if (container == null) {
			container = getTupleContainer(t);
			return putTupleContainer(t, container);
		} else {
			return container.doIncrementContainerCounter();
		}
	}

	private TupleContainer getTupleContainer(Tuple t) {
		TupleContainer container = new TupleContainer(t);
		containerMap.put(t, container);
		return container;
	}

	private boolean putTupleContainer(Tuple t, TupleContainer container) {
		boolean result;
		Template template = t.getFormalTemplate();
		LinkedList<TupleContainer> tc = tupleMap.get(template);
		if (tc == null) {
			tc = new LinkedList<TupleContainer>();
			result = tc.add(container);
			tupleMap.put(template, tc);
		} else {
			result = tc.add(container);
		}
		return result;
	}

	@Override
	public Tuple get(Template template) throws InterruptedException {
		Template templateFormal = template.getFormalTemplate();
		LinkedList<TupleContainer> tc =tupleMap.get(templateFormal);
		Tuple t;
		lock.inLock();
		try {
			while (((t = getTuple(tc, template, true)) == null)) {
				lock.getWaitTuple();
			}
		} finally {
			lock.inUnlock();
		}
		return t;
	}
	
	private Tuple getTuple(LinkedList<TupleContainer> tc, Template template, boolean remove) {
		if (tc == null) {
			return null;
		}
		for (TupleContainer container : tc) {
			if (checkContainer(template, container) && ((!remove && container.isPossibleToDecrement()) || container.doDecrementContainerCounter())) {
				Tuple t = container.getTuple();
				return t;
			}
		}
		return null;
	}

	private LinkedList<Tuple> getTupleList(LinkedList<TupleContainer> tc, boolean remove, Template template) {
		LinkedList<Tuple> tupleList = new LinkedList<Tuple>();
		if (tc == null) {
			return null;
		}
		for (TupleContainer container : tc) {
			if (checkContainer(template, container) && ((!remove && container.isPossibleToDecrement()) || container.doDecrementContainerCounter())) {
				Tuple t = container.getTuple();
				tupleList.add(t);
			}
		}
		return tupleList;
	}
	protected boolean checkContainer(Template template, TupleContainer container) {
		return container.matchWithTemplate(template);
	}

	@Override
	public LinkedList<Tuple> getAll(Template template) throws InterruptedException {
		LinkedList<Tuple> t;
		Template templateFormal = template.getFormalTemplate();
		LinkedList<TupleContainer> tc;
		lock.inLock();
		try {
			tc = tupleMap.get(templateFormal);
			t = getTupleList(tc, true, template);
		} finally {
			lock.inUnlock();
		}
		return t;
	}

	@Override
	public Tuple query(Template template) throws InterruptedException {
		Template templateFormal = template.getFormalTemplate();
		LinkedList<TupleContainer> tc =tupleMap.get(templateFormal);
		Tuple t;
		lock.inLock();
		try {
			while (((t = getTuple(tc, template, false)) == null)) {
				lock.getWaitTuple();
			}
		} finally {
			lock.inUnlock();
		}
		return t;
	}

	@Override
	public LinkedList<Tuple> queryAll(Template template) throws InterruptedException {
		LinkedList<Tuple> t;
		lock.readLock();
		try {
			LinkedList<TupleContainer> tc = tupleMap.get(template);
			t = getTupleList(tc, false, template);
		} finally {
			lock.readUnlock();
		}
		return t;
	}

	@Override
	public Tuple getp(Template template) throws InterruptedException {
		Template templateFormal = template.getFormalTemplate();
		LinkedList<TupleContainer> tc =tupleMap.get(templateFormal);
		Tuple t;
		lock.inLock();
		try {
			t = getTuple(tc, template, true);
		} finally {
			lock.inUnlock();
		}
		return t;
	}

	@Override
	public Tuple queryp(Template template) throws InterruptedException {
		Template templateFormal = template.getFormalTemplate();
		LinkedList<TupleContainer> tc =tupleMap.get(templateFormal);
		Tuple t;
		lock.readLock();
		try {
			t = getTuple(tc, template, true);
		} finally {
			lock.readUnlock();
		}
		return t;
	}

	@Override
	public LinkedList<Tuple> getAll() throws InterruptedException {
		LinkedList<Tuple> tuples = new LinkedList<Tuple>();
		try {
			lock.inLock();
			for (Entry<Template, LinkedList<TupleContainer>> keyMap : tupleMap.entrySet()) {
				getAllTupleList(keyMap.getValue(), true, tuples);
			}
		} finally {
			lock.inUnlock();
		}
		return tuples;
	}

	@Override
	public LinkedList<Tuple> queryAll() throws InterruptedException {
		LinkedList<Tuple> tuples = new LinkedList<Tuple>();
		try {
			lock.readLock();
			for (Entry<Template, LinkedList<TupleContainer>> keyMap : tupleMap.entrySet()) {
				getAllTupleList(keyMap.getValue(), false, tuples);
			}
		} finally {
			lock.readUnlock();
		}
		return tuples;
	}

	protected void getAllTupleList(LinkedList<TupleContainer> tc, boolean remove, LinkedList<Tuple> tuples) {
		if (tc != null) {
			for (TupleContainer container : tc) {
				if ((!remove && container.isPossibleToDecrement()) || container.doDecrementContainerCounter()) {
					Tuple t = container.getTuple();
					tuples.add(t);
				}
			}
		}
	}
}
