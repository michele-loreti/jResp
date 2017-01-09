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
package org.cmg.test.knowledge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.cmg.jresp.knowledge.Tuple;
import org.junit.Test;

/**
 * @author loreti
 * 
 *
 */
public class TestTuple {

	/**
	 * Test method for
	 * {@link org.cmg.jresp.knowledge.Tuple#Tuple(java.lang.Object[])}.
	 */
	@Test
	public void testTuple() {
		Tuple t = new Tuple(2, 3, 2, 3);
		assertNotNull(t);
	}

	/**
	 * Test method for {@link org.cmg.jresp.knowledge.Tuple#length()}.
	 */
	@Test
	public void testLength() {
		Tuple t = new Tuple(2, 3, 2, 3);
		assertEquals(4, t.length());
	}

	/**
	 * Test method for {@link org.cmg.jresp.knowledge.Tuple#getElementAt(int)}.
	 */
	@Test
	public void testGetElementAtInt() {
		Tuple t = new Tuple(1, 2, 3, 4);
		for (int i = 0; i < t.length(); i++) {
			assertEquals(i + 1, t.getElementAt(i));
		}
	}

	/**
	 * Test method for
	 * {@link org.cmg.jresp.knowledge.Tuple#getElementAt(java.lang.Class, int)}.
	 */
	@Test
	public void testGetElementAtClassOfTInt() {
		Tuple t = new Tuple(1, 2, 3, 4);
		assertTrue(t.hasType(Integer.class, 2));
	}

	/**
	 * Test method for {@link org.cmg.jresp.knowledge.Tuple#getTypeAt(int)}.
	 */
	@Test
	public void testGetTypeAt() {
		Tuple t = new Tuple(1, 2, 3, 4);
		assertEquals(Integer.class, t.getTypeAt(2));
	}

}
