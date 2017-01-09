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

/**
 * Instances of this class identifies actual template fields.
 * 
 * @author Michele Loreti
 *
 */
public class ActualTemplateField implements TemplateField {

	/**
	 * Field value.
	 */
	private Object value;

	/**
	 * Create an actual field with value value
	 * 
	 * @param value
	 *            field value
	 */
	public ActualTemplateField(Object value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.scel.knowledge.TemplateField#match(java.lang.Object)
	 */
	@Override
	public boolean match(Object o) {
		return (value == o) || ((value != null) && value.equals(o));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ActualTemplateField) {
			return value.equals(((ActualTemplateField) obj).value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public Object getValue() {
		return value;
	}

	@Override
	public boolean implies(TemplateField templateField) {
		if (templateField instanceof FormalTemplateField){
			return ((FormalTemplateField) templateField).match(this.value);
		}else{
			return this.equals(templateField);
		}
	}

}
