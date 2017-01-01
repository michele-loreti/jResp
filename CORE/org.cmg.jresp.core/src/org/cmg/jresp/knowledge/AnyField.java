/**
 * Copyright (c) 2016 Concurrency and Mobility Group.
 * Università di Firenze
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
 * @author Michele Loreti
 *
 */
public class AnyField implements TemplateField {

	@Override
	public boolean match(Object o) {
		return true;
	}

	@Override
	public boolean implies(TemplateField templateField) {
		return false;
	}

}
