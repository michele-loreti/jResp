/**
 * Copyright (c) 2013 Concurrency and Mobility Group.
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
package org.cmg.jresp.simulation;

import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author loreti
 *
 */
public class SimulationScheduler {

	private ExecutorService threadExecutor;

	private PriorityQueue<SchedulableEvent> eventQueue;

	private volatile Thread scheduledThread;

	private double currentTime;

	private boolean doSchedule = true;

	private volatile boolean stop = false;

	private boolean done = false;

	public SimulationScheduler() {
		this(null);
	}

	public SimulationScheduler(SimulationGuard guard) {
		this.eventQueue = new PriorityQueue<SchedulableEvent>();
		this.currentTime = 0.0;
		this.threadExecutor = Executors.newCachedThreadPool();
	}

	public void schedule(double t) throws InterruptedException {
		schedule(t, 0);
	}

	public void schedule(double t, int priority) throws InterruptedException {
		LockEvent sl = new LockEvent(t, priority);
		schedule(sl, (scheduledThread == Thread.currentThread()));
		sl.waitSchedulingTime();
		scheduledThread = Thread.currentThread();
	}

	public synchronized void schedule(SchedulableEvent event, boolean reschedule) {
		synchronized (eventQueue) {
			event.shift(currentTime);
			this.eventQueue.add(event);
			if (reschedule) {
				reschedule();
			}
		}
	}

	private void reschedule() {
		synchronized (eventQueue) {
			doSchedule = true;
			scheduledThread = null;
			eventQueue.notify();
		}
	}

	protected void scheduleNext() throws InterruptedException {
		SchedulableEvent event = null;
		synchronized (eventQueue) {
			while ((eventQueue.isEmpty() || (!doSchedule)) && !stop) {
				eventQueue.wait();
			}
			if (!stop) {
				event = eventQueue.remove();
				doSchedule = false;
				this.currentTime = event.getTime();
				event.execute();
			}
		}
	}

	private synchronized void doStop() {
		for (SchedulableEvent e : eventQueue) {
			e.cancelled();
		}
	}

	protected void releaseOnExit() {
		synchronized (eventQueue) {
			if (scheduledThread == Thread.currentThread()) {
				reschedule();
			}
		}
	}

	public void start() {
		threadExecutor.execute(new Runnable() {

			@Override
			public void run() {
				while (!stop) {
					try {
						scheduleNext();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				doStop();
				System.out.println("SIMULATION COMPLETED!");
				threadExecutor.shutdownNow();
				setDone();
			}
		});
	}

	protected synchronized void setDone() {
		done = true;
		notifyAll();
	}

	public synchronized void join() throws InterruptedException {
		while (!done) {
			wait();
		}
	}

	public void execute(final Runnable r) {
		threadExecutor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					schedule(0.0);
					r.run();
					releaseOnExit();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
	}

	public void schedulePeriodicAction(SimulationAction action, double from, double interval) {
		schedulePeriodicAction(action, from, interval, 0);
	}

	public void schedulePeriodicAction(SimulationAction action, double from, double interval, int priority) {
		PeriodicAction pa = new PeriodicAction(action, from, interval, priority);
		schedule(pa, false);
	}

	public void schedule(SimulationAction action, double time) {
		schedule(action, time, 0);
	}

	public void schedule(SimulationAction action, double time, int priority) {
		ScheduledAction sa = new ScheduledAction(action, time, priority);
		schedule(sa, false);
	}

	public void stopSimulation() {
		stop = true;
	}

	public class ScheduledAction extends SchedulableEvent {

		private SimulationAction action;

		public ScheduledAction(SimulationAction action, double time, int priority) {
			super(time, priority);
			this.action = action;
		}

		@Override
		public void execute() {
			action.doAction(getTime());
			reschedule();
		}

		@Override
		public void cancelled() {
		}

	}

	public class PeriodicAction extends SchedulableEvent {

		private SimulationAction action;

		private double period;

		public PeriodicAction(SimulationAction action, double time, double period, int priority) {
			super(time, priority);
			this.action = action;
			this.period = period;
		}

		@Override
		public void execute() {
			action.doAction(getTime());
			schedulePeriodicAction(action, period, period);
			reschedule();
		}

		@Override
		public void cancelled() {
		}

	}

	public double getCurrentTime() {
		return currentTime;
	}

}
