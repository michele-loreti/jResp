package org.cmg.jresp.knowledge2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EsclusiveLock implements TupleSpaceLock {
	/*
	 * Implementazione del TupleSpaceLock.
	 * Accesso esclusivo per tutte le operazioni
	 */
	private ReentrantLock lock;
	private Condition waitTuple;

	public EsclusiveLock() {
		lock = new ReentrantLock();	
		waitTuple = lock.newCondition();
	}

	@Override
	public void readLock() throws InterruptedException {
		lock.lock();
	}

	@Override
	public void inLock() throws InterruptedException {
		lock.lock();
	}

	@Override
	public void outLock() throws InterruptedException {
		lock.lock();
	}

	@Override
	public void readUnlock() {
		lock.unlock();
	}

	@Override
	public void inUnlock() {
		lock.unlock();
	}

	@Override
	public void outUnlock() {
		waitTuple.signalAll();
		lock.unlock();
	}

	@Override
	public void readWaitTuple() throws InterruptedException {
		waitTuple.await();
	}

	@Override
	public void getWaitTuple() throws InterruptedException {
		waitTuple.await();
	}

}
