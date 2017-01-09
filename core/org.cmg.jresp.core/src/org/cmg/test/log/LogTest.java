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
package org.cmg.test.log;

import java.util.logging.Logger;

/**
 * @author Michele Loreti
 *
 */
public class LogTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger loggerA = Logger.getLogger("Pippo");
		loggerA.info("info!!");

	}

}
