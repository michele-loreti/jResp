package org.cmg.jresp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.PriorityQueue;

import org.cmg.jresp.simulation.LockEvent;
import org.cmg.jresp.simulation.SchedulableEvent;
import org.junit.Test;

public class PriorityQueueTest {

	@Test
	public void testQueueOfIntegers() {
		PriorityQueue<Integer> queue = new PriorityQueue<Integer>();
		queue.add(32);
		queue.add(12);
		assertEquals( 12 , queue.remove().intValue() );
	}

	@Test
	public void testQueueOfComparables() {
		PriorityQueue<MyOrderedTest> queue = new PriorityQueue<MyOrderedTest>();
		queue.add(new MyOrderedTest(1, 2));
		queue.add(new MyOrderedTest(1, 1));
		queue.add(new MyOrderedTest(2, 3));
		assertEquals( new MyOrderedTest(1, 1) , queue.remove() );
		assertEquals( new MyOrderedTest(1, 2) , queue.remove() );
		assertEquals( new MyOrderedTest(2, 3) , queue.remove() );
	}
	
	@Test
	public void testEventOrder() {
		LockEvent event1 = new LockEvent(1.0,1);
		LockEvent event2 = new LockEvent(0.0,1);
		assertTrue(event1.compareTo(event2)>0);
		PriorityQueue<SchedulableEvent> queue = new PriorityQueue<SchedulableEvent>();
		queue.add(event2);
		queue.add(event1);
		assertEquals( event2 , queue.remove() );
	}

	
	public class MyOrderedTest implements Comparable<MyOrderedTest> {
		
		private int x;
		private int y;
		
		public MyOrderedTest( int x , int y ) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int compareTo(MyOrderedTest o) {
			int foo = this.x - o.x;
			if (foo == 0) {
				return this.y-o.y;
			}
			return foo;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MyOrderedTest) {
				MyOrderedTest m = (MyOrderedTest) obj;
				return (this.x == m.x)&&(this.y == m.y);
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return x^y;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "( "+x+" , "+y+" )";
		}
		
		
		
	}
}
