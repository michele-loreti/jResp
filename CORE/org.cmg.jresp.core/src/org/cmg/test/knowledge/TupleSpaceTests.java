package org.cmg.jresp.test.knowledge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.junit.Test;

public class TupleSpaceTests {

	@Test
	public void testPut() {
		TupleSpace ts = new TupleSpace();
		assertTrue(ts.put(new Tuple("PONG")));
	}

	@Test
	public void testPutGet() throws InterruptedException {
		TupleSpace ts = new TupleSpace();
		Tuple t = new Tuple("PONG");
		ts.put(t);
		Template temp = new Template(new ActualTemplateField("PONG"));
		Tuple t2 = ts.get(temp);
		assertEquals(t, t2);
	}

}
