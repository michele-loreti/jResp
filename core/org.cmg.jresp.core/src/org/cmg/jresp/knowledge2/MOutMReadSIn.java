package org.cmg.jresp.knowledge2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * Implementa TupleSpaceLock in modo da avere un accesso esclusivo per l'in. 
 * Permette threads multipli per l'out e il read.
 */
public class MOutMReadSIn implements TupleSpaceLock {
	private ReadWriteLock lock;
	private Lock r;
	private Lock w;

	public MOutMReadSIn() {
			lock = new ReentrantReadWriteLock();
			r = lock.readLock();
			w = lock.writeLock();
		}

	public void readLock() {
		r.lock();
	}

	public void inLock() {
		w.lock();
	}

	public void outLock() {
		r.lock();
	}

	public void readUnlock() {
		r.unlock();
	}

	public void inUnlock() {
		w.unlock();
	}

	public void outUnlock() {
		r.unlock();
	}
}
