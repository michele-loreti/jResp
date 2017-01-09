package org.cmg.jresp.policy.facpl;

import java.util.LinkedList;

public class Bag {

	private LinkedList<Object> bag_values;

	public Bag(Object... elements) {
		this.bag_values = new LinkedList<Object>();
		for (Object object : elements) {
			bag_values.add(object);
		}
	}

	public void addValue(Object value) {
		bag_values.add(value);
	}

	public LinkedList<Object> getBag_values() {
		return bag_values;
	}

	public Boolean isEmpty() {
		if (bag_values != null & bag_values.size() > 0)
			return false;
		else
			return true;
	}

	@Override
	public String toString() {
		return bag_values.toString();
	}

	public boolean contains(Object object) {
		return bag_values.contains(object);
	}

	public boolean contains(Bag bag) {
		Boolean flag = true;
		for (Object el : bag.getBag_values()) {
			flag = flag && bag_values.contains(el);
			if (!flag)
				return flag;
		}
		return flag;
	}

}
