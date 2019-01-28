package com.alienvault.report.exception;

import static org.junit.Assert.fail;

import org.junit.Test;

public class DataSourceConsumptionExceptionTest {

	private final static String MSG = "I failed";
	private final static String INNERMSG = "inner";

	@Test
	public void testDataSourceConsumptionExceptionConstructor() {
		Throwable t = new Throwable(INNERMSG);
		try {
			throw new DataSourceConsumptionException(MSG, t);
		} catch (DataSourceConsumptionException rte) {
			if (!rte.getMessage().equals(MSG)) {
				fail("Constructor issue");
			}
			if (!t.getMessage().equals(INNERMSG)) {
				fail("Inner Throwable issue");
			}
		} catch (Throwable rte) {
			fail("Type issue");
		}
	}
}
