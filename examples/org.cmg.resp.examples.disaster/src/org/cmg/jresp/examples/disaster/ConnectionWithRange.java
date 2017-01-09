package org.cmg.jresp.examples.disaster;

import java.util.Observable;

import org.cmg.jresp.comp.NodeConnection;

public class ConnectionWithRange extends Observable implements NodeConnection {

	@Override
	public synchronized void waitInTouch(String src, String target) throws InterruptedException {
		while (!areInTouch(src, target)) {
			wait();
		}
	}

	@Override
	public synchronized boolean areInTouch(String src, String target) {
		// Point2D.Double pSrc = locations.get(src).getPoint();
		// if (pSrc == null) {
		// return false;
		// }
		// Point2D.Double pTrg = locations.get(target).getPoint();
		// if (pTrg == null) {
		// return false;
		// }
		// boolean result = pSrc.distance(pTrg)<=range;
		// return result;
		return true;
	}

}
