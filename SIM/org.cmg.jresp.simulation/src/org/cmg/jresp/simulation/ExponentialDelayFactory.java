/**
 * 
 */
package org.cmg.jresp.simulation;

import java.util.Random;

import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.GroupPredicate;

/**
 * @author loreti
 *
 */
public class ExponentialDelayFactory implements IDelayFactory {
	
	private Random random;
	private double rate;
	
	public ExponentialDelayFactory( Random random , double rate ) {
		this.random = random;
		this.rate = rate;
	}
	
	private double sampleExponential( double rate ) {
		return 1/rate*-Math.log(random.nextDouble());
	}

	@Override
	public double getRetryTime(SimulationNode node) {
		return 0.0;
	}

	@Override
	public double getLocalGetTime(SimulationNode node, Template t) {
		return sampleExponential(rate);
	}

	@Override
	public double getRemoteGetTime(SimulationNode src, Template t,
			SimulationNode trg) {
		return sampleExponential(rate);
	}

	@Override
	public double getGroupGetTime(SimulationNode src, Template t,
			GroupPredicate target, SimulationNode trg) {
		return sampleExponential(rate);
	}

	@Override
	public double getLocalPutTime(SimulationNode node, Tuple t) {
		return sampleExponential(rate);
	}

	@Override
	public double getRemotePutTime(SimulationNode src, Tuple t,
			SimulationNode trg) {
		return sampleExponential(rate);
	}

	@Override
	public double getGroupPutTime(SimulationNode src, Tuple t,
			GroupPredicate target) {
		return sampleExponential(rate);
	}

	@Override
	public double getLocalQueryTime(SimulationNode node, Template t) {
		return sampleExponential(rate);
	}

	@Override
	public double getRemoteQueryTime(SimulationNode src, Template t,
			SimulationNode trg) {
		return sampleExponential(rate);
	}

	@Override
	public double getGroupQueryTime(SimulationNode src, Template t,
			GroupPredicate target, SimulationNode trg) {
		return sampleExponential(rate);
	}

}
