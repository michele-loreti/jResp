/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universita' di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.jresp.knowledge;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A tuple identifies the basic information item. It consists of a sequence of
 * values that can be collected into a knowledge repository.
 * 
 * @author Michele Loreti
 * 
 *
 */
public class Tuple implements Iterable<Object>, Serializable {

	/**
	 * Tuple fields.
	 */
	protected Object[] fields;

	/**
	 * Creates a new tuple.
	 * 
	 * @param fields
	 *            fields of new created tuple.
	 */
	public Tuple(Object... fields) {
		this.fields = fields;
	}

	/**
	 * Returns the tuple length.
	 * 
	 * @return the tuple length.
	 */
	public int length() {
		return fields.length;
	}

	/**
	 * Returns the element at index <code>i</code>.
	 * 
	 * @param i
	 *            element index.
	 * @return the element at index <code>i</code>.
	 */
	public Object getElementAt(int i) {
		return fields[i];
	}

	/**
	 * Returns the class <code>c</code> of the element with index <code>i</code>
	 * .
	 * 
	 * @param i
	 *            element index
	 * @return the class <code>c</code> of the element with index <code>i</code>
	 *         .
	 */
	public Class<?> getTypeAt(int i) {
		return fields[i].getClass();
	}

	/**
	 * Returns the instance of class <code>c</code> at element <code>i</code>.
	 * This method is equivalent to <code>c.cast(getElementAt(i))</code>. A
	 * <code>ClassCastException</code> is thrown if the <code>i</code>-th
	 * element of the tuple is not an instance if <code>c</code>.
	 * 
	 * @param c
	 *            expected class
	 * @param i
	 *            element index
	 * @return the instance of class <code>c</code> at element <code>i</code>.
	 */
	public <T> T getElementAt(Class<T> c, int i) {
		Object o = getElementAt(i);
		if (o == null) {
			return null;
		}
		if (c.isInstance(o)) {
			return c.cast(o);
		}
		throw new ClassCastException();
	}

	public boolean hasType(Class<?> c, int i) {
		return c.isInstance(fields[i]);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tuple) {
			return Arrays.deepEquals(fields, ((Tuple) obj).fields);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(fields);
	}

	@Override
	public String toString() {
		return Arrays.deepToString(fields);
	}

	@Override
	public Iterator<Object> iterator() {
		return new Iterator<Object>() {

			private int current = 0;

			@Override
			public boolean hasNext() {
				return current < fields.length;
			}

			@Override
			public Object next() {
				return fields[current++];
			}

			@Override
			public void remove() {
			}
		};
	}

	public Template toTemplate() {
		TemplateField[] fields = new TemplateField[length()];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new ActualTemplateField(getElementAt(i));
		}
		return new Template(fields);
	}

}
