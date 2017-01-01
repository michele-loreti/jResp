/**
 * Copyright (c) 2013 Concurrency and Mobility Group.
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
 * @author Michele Loreti
 *
 */
public interface KnowledgeAdapter extends KnowledgeManager {

	/**
	 * Return true if the current knowledge manager is responsible for knowledge
	 * item t.
	 * 
	 * @param t
	 *            a knowledge item
	 * @return true if this knowledge manager is responsible for knowledge item
	 *         t.
	 */
	public boolean isResponsibleFor(Tuple t);

	/**
	 * Return true if the current knowledge manager is responsible for knowledge
	 * template t.
	 * 
	 * @param t
	 *            a knowledge template
	 * @return true if this knowledge manager is responsible for knowledge
	 *         template t.
	 */
	public boolean isResponsibleFor(Template t);

}
