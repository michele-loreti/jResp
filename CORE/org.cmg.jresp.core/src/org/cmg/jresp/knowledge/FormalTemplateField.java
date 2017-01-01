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
 * Identifies a formal template field.
 * 
 * @author Michele Loreti
 *
 */
public class FormalTemplateField implements TemplateField {

	/**
	 * Type of matching value.
	 */
	private Class<?> type;

	/**
	 * Name of the corresponding variable
	 */
	private String varName;

	/**
	 * Creates a matching template field matching any value of type
	 * <code>type</code>.
	 * 
	 * @param type
	 */
	public FormalTemplateField(Class<?> type) {
		this.type = type;
	}

	/**
	 * A new template filed carrying the name of the corresponding variable
	 * 
	 * @param type
	 * @param name
	 */
	public FormalTemplateField(Class<?> type, String name) {
		this.type = type;
		this.varName = name;
	}

	/***
	 * A SCELValue o matches this formal field if and only if o has type
	 * <code>this.type</code>.
	 * 
	 * @see org.cmg.jresp.knowledge.TemplateField#match(java.lang.Object)
	 */
	@Override
	public boolean match(Object o) {
		return (o == null) || (type.isInstance(o));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FormalTemplateField) {
			return type.equals(((FormalTemplateField) obj).type);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return "{" + type.getName() + "}";
	}

	public Class<?> getFormalFieldType() {
		return type;
	}

	@Override
	public boolean implies(TemplateField templateField) {
		if (this.equals(templateField)) {
			return true;
		}
		if (templateField instanceof ActualTemplateField) {
			return this.match(((ActualTemplateField) templateField).getValue());
		}
		return false;
	}

	public String getVarName() {
		return varName;
	}

}
