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
package org.cmg.jresp.test.json;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author loreti
 * 
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ ValueSerializationDeserialization.class, MessageSerializationDeserialization.class,
		AddressSerializationDeserialization.class })
public class JSonTests {
}
