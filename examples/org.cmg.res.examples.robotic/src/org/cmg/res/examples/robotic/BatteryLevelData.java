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
package org.cmg.res.examples.robotic;

import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

/**
 * @author loreti
 *
 */
public class BatteryLevelData extends AbstractTableModel implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2941232255008293748L;

	private Scenario scenario;

	public BatteryLevelData( Scenario scenario ) {
		this.scenario = scenario;
		this.scenario.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return scenario.getSize();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		if (arg1 == 0) {
			return "Robot "+(arg0+1);
		}
		if (arg1 == 1) {
			return scenario.getBarreryPercentage(arg0);
		}
		return null;
	}

}
