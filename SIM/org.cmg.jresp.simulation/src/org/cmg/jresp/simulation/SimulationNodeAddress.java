/**
 * 
 */
package org.cmg.jresp.simulation;

import org.cmg.jresp.topology.Address;

/**
 * @author loreti
 *
 */
public class SimulationNodeAddress extends Address {

	public static final String ADDRESS_CODE = "simulation";

	private static SimulationNodeAddress _instance = null;
	
	private SimulationNodeAddress() {
		super(ADDRESS_CODE);
	}
	
	public static Address getInstance() {
		if (_instance==null) {
			_instance = new SimulationNodeAddress();
		}
		return _instance;
	}
	

}
