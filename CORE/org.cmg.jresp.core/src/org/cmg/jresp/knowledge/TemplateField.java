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

/**
 * Identifies a generic template field. Each implementation of interface
 * <code>Knowedge</code> should provide specific implementation of
 * <code>TemplateField</code>.
 * 
 * 
 * @author Michele Loreti
 * 
 *
 */
public interface TemplateField extends Serializable {

	/**
	 * Checks if the object <code>o</code> matches against this field.
	 *
	 * @param o
	 *            a generic object
	 * @return <code>true</code> if the object <code>o</code> matches against
	 *         this field.
	 */
	public boolean match(Object o);

	public boolean implies(TemplateField templateField);

}
