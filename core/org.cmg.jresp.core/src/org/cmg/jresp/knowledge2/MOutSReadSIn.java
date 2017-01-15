package org.cmg.jresp.knowledge2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MOutSReadSIn implements TupleSpaceLock{
private ReentrantReadWriteLock lock;
private Lock r = lock.readLock();
private Lock w = lock.writeLock();

public void readLock(){
	w.lock();
}
public void inLock(){
	w.lock();
}
public void outLock(){
	r.lock();
}
public void waitTuple(){
	
}
public void readUnlock(){
	w.unlock();
}
public void inUnlock(){
	w.unlock();
}
public void outUnlock(){
	r.unlock();
}

}
