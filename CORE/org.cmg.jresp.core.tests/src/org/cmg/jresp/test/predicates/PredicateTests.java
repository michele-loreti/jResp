package org.cmg.jresp.test.predicates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.topology.And;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.IsGreaterOrEqualThan;
import org.cmg.jresp.topology.IsGreaterThan;
import org.cmg.jresp.topology.IsLessOrEqualThan;
import org.cmg.jresp.topology.IsLessThan;
import org.cmg.jresp.topology.Not;
import org.cmg.jresp.topology.Or;
import org.junit.Test;

public class PredicateTests {

	@Test
	public void testHasValueSat() {
		HasValue predicate = new HasValue("colored", true);
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("colored", new Attribute("colored", true));
		assertTrue(predicate.evaluate(data));
	}

	@Test
	public void testHasValueUnSat() {
		HasValue predicate = new HasValue("colored", true);
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("colored", new Attribute("colored", false));
		assertFalse(predicate.evaluate(data));
	}

	@Test
	public void testIsGreaterThanSat() {
		IsGreaterThan predicate = new IsGreaterThan("value", 45);
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value", new Attribute("value", 54));
		assertTrue(predicate.evaluate(data));
		data.put("value", new Attribute("value", 45));
		assertFalse(predicate.evaluate(data));
	}

	@Test
	public void testIsGreaterThanUnSat() {
		IsGreaterThan predicate = new IsGreaterThan("value", 45);
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value", new Attribute("value", 32));
		assertFalse(predicate.evaluate(data));

	}

	@Test
	public void testIsGreaterOrEqualThanSat() {
		IsGreaterOrEqualThan predicate = new IsGreaterOrEqualThan("value", 45);
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value", new Attribute("value", 54));
		assertTrue(predicate.evaluate(data));
		data.put("value", new Attribute("value", 45));
		assertTrue(predicate.evaluate(data));

	}

	@Test
	public void testIsGreaterOrEqualThanUnSat() {
		IsGreaterOrEqualThan predicate = new IsGreaterOrEqualThan("value", 45);
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value", new Attribute("value", 32));
		assertFalse(predicate.evaluate(data));
	}

	@Test
	public void testIsLessOrEqualThanSat() {
		IsLessOrEqualThan predicate = new IsLessOrEqualThan("value", 45);
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value", new Attribute("value", 23));
		assertTrue(predicate.evaluate(data));
		data.put("value", new Attribute("value", 45));
		assertTrue(predicate.evaluate(data));

	}

	@Test
	public void testIsLessOrEqualThanUnSat() {
		IsLessOrEqualThan predicate = new IsLessOrEqualThan("value", 45);
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value", new Attribute("value", 55));
		assertFalse(predicate.evaluate(data));
	}

	@Test
	public void testIsLessThanSat() {
		IsLessThan predicate = new IsLessThan("value", 45);
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value", new Attribute("value", 23));
		assertTrue(predicate.evaluate(data));
		data.put("value", new Attribute("value", 45));
		assertFalse(predicate.evaluate(data));

	}

	@Test
	public void testIsLessThanUnSat() {
		IsLessThan predicate = new IsLessThan("value", 45);
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value", new Attribute("value", 55));
		assertFalse(predicate.evaluate(data));
	}

	@Test
	public void testAndSat() {
		And predicate = new And(new IsLessThan("value", 45), new IsLessThan("value", 45));
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value", new Attribute("value", 35));
		assertTrue(predicate.evaluate(data));
	}

	@Test
	public void testAndUnSat() {
		And predicate = new And(new IsLessThan("value1", 45), new IsLessThan("value2", 45));
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value1", new Attribute("value1", 55));
		assertFalse(predicate.evaluate(data));
		data = new HashMap<String, Attribute>();
		data.put("value1", new Attribute("value1", 35));
		data.put("value2", new Attribute("value1", 55));
		assertFalse(predicate.evaluate(data));
	}

	@Test
	public void testOrUnSat() {
		Or predicate = new Or(new IsLessThan("value1", 45), new IsLessThan("value2", 45));
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value1", new Attribute("value1", 55));
		data.put("value2", new Attribute("value1", 55));
		assertFalse(predicate.evaluate(data));
	}

	@Test
	public void testOrSat() {
		Or predicate = new Or(new IsLessThan("value1", 45), new IsLessThan("value2", 45));
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("value1", new Attribute("value1", 35));
		data.put("value2", new Attribute("value1", 55));
		assertTrue(predicate.evaluate(data));
		data = new HashMap<String, Attribute>();
		data.put("value1", new Attribute("value1", 55));
		data.put("value2", new Attribute("value1", 35));
		assertTrue(predicate.evaluate(data));
	}

	@Test
	public void testNotSat() {
		Not predicate = new Not(new HasValue("colored", true));
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("colored", new Attribute("colored", false));
		assertTrue(predicate.evaluate(data));
	}

	@Test
	public void testNotUnSat() {
		Not predicate = new Not(new HasValue("colored", true));
		HashMap<String, Attribute> data = new HashMap<String, Attribute>();
		data.put("colored", new Attribute("colored", true));
		assertFalse(predicate.evaluate(data));
	}

}
