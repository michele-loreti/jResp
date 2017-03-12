package org.cmg.jresp.knowledge2;

public interface TupleSpaceLock {
	void readLock() throws InterruptedException;

	void inLock() throws InterruptedException;

	void outLock() throws InterruptedException;

	void readUnlock();

	void inUnlock();

	void outUnlock();
	
	void readWaitTuple() throws InterruptedException;
	
	void getWaitTuple() throws InterruptedException;
}
