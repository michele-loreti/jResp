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
 * An instance of class <code>Template</code> is used as a pattern to select
 * knowledge items in the repositories.
 * 
 * @author Michele Loreti
 * 
 *
 */
public class Template implements Iterable<TemplateField>, Serializable {

	/**
	 * A list of template fields.
	 */
	private TemplateField[] fields;

	/**
	 * Creates a new template starting from its fields.
	 * 
	 * @param fields
	 */
	public Template(TemplateField... fields) {
		this.fields = fields;
	}

	/**
	 * Check if tuple <code>t</code> matches the tempalte.
	 * 
	 * @param t
	 *            tuple to match
	 * @return <code>true</code> if the tuple matches against this template,
	 *         <code>false</code> otherwise.
	 */
	public boolean match(Tuple t) {
		int size = this.length();
		if (size != t.length()) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (!fields[i].match(t.getElementAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the number of fields in the template.
	 * 
	 * @return number of fields in the template.
	 */
	public int length() {
		return fields.length;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Template) {
			return Arrays.deepEquals(fields, ((Template) obj).fields);
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
	public Iterator<TemplateField> iterator() {
		return new Iterator<TemplateField>() {

			private int current = 0;

			@Override
			public boolean hasNext() {
				return (current < fields.length);
			}

			@Override
			public TemplateField next() {
				return fields[current++];
			}

			@Override
			public void remove() {
			}
		};
	}

	public TemplateField getElementAt(int i) {
		return fields[i];
	}

	public boolean implies(Template t) {
		if (this.fields.length != t.fields.length) {
			return false;
		}
		for (int i = 0; i < this.fields.length; i++) {
			if (!this.fields[i].implies(t.fields[i])) {
				return false;
			}
		}
		return true;
	}

}
