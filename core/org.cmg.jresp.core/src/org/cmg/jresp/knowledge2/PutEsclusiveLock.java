package org.cmg.jresp.knowledge2;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PutEsclusiveLock implements TupleSpaceLock{
	/*
	 * Implementazione del TupleSpaceLock Permette di eseguire le istruzioni get
	 * put read del TupleSpace in maniera atomica con un lock esclusivo interno.
	 * Regolamenta la richiesta al lock sopracitato: Il lock interno può essere
	 * richesto da più get e read contemporaneamente finchè non ci sono put. Il
	 * lock interno può essere richiesto soltanto da un put alla volta.
	 */
	private ReentrantLock lock;
	private ReentrantLock lockWait;
	private ReentrantReadWriteLock esternalLock;
	private Lock wEsternalLock;
	private Lock rEsternalLock;
	private Condition waitTuple;

	public PutEsclusiveLock() {
		lock = new ReentrantLock();
		lockWait = new ReentrantLock();
		esternalLock = new ReentrantReadWriteLock();
		wEsternalLock = esternalLock.writeLock();
		rEsternalLock = esternalLock.readLock();
		waitTuple = lockWait.newCondition();
	}

	public void readLock() throws InterruptedException {
		rEsternalLock.lock();
		lock.lock();
	}

	public void inLock() throws InterruptedException {
		rEsternalLock.lock();
		lock.lock();
	}

	public void outLock() throws InterruptedException {
		wEsternalLock.lock();
		lock.lock();
	}

	public void readUnlock() {
		lock.unlock();
		rEsternalLock.unlock();		
	}

	public void inUnlock() {
		lock.unlock();
		rEsternalLock.unlock();
	}

	public void outUnlock() {
		lock.unlock();
		lockWait.lock();
		waitTuple.signalAll();
		lockWait.unlock();
		wEsternalLock.unlock();

	}

	public void readWaitTuple() throws InterruptedException {
		lock.unlock();
		lockWait.lock();
		rEsternalLock.unlock();
		waitTuple.await();
		lockWait.unlock();
		rEsternalLock.lock();
		lock.lock();
	}

	public void getWaitTuple() throws InterruptedException {
		lock.unlock();
		lockWait.lock();
		rEsternalLock.unlock();
		waitTuple.await();
		lockWait.unlock();
		rEsternalLock.lock();
		lock.lock();
	}
}
