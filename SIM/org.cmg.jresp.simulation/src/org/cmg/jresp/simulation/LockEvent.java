package org.cmg.jresp.simulation;


public class LockEvent extends SchedulableEvent {
	
	private boolean scheduled = false;
	
	private boolean cancelled = false;
	
	public LockEvent( double schedulingTime , int priority ) {
		super( schedulingTime );
	}
	
	public synchronized void waitSchedulingTime( ) throws InterruptedException {
		while ((!scheduled)&&(!cancelled)) {
			wait();
		}
		if (cancelled) {
			throw new CancelledException();
		}
	}
	
	@Override
	public synchronized void execute() {
		this.scheduled = true;
		notifyAll();
	}

	@Override
	public synchronized void cancelled() {
		this.cancelled = true;
		notifyAll();
	}

}