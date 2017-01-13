package org.cmg.resp.knowledge2;


public interface TupleSpaceLock {
	void readLock();
	void inLock();
	void outLock();
	void readUnlock();
	void inUnlock();
	void outUnlock();
	void waitTuple();
}
