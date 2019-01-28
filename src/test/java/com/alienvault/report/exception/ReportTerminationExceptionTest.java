package com.alienvault.report.exception;

import static org.junit.Assert.fail;

import org.junit.Test;

public class ReportTerminationExceptionTest {

	@Test
	public void testReportTerminationExceptionConstructor() {
		String msg = "I failed";
		try {
			throw new ReportTerminationException("I failed");
		} catch (ReportTerminationException rte) {
			if (!rte.getMessage().equals(msg)) {
				fail("Constructor issue");
			}
		} catch (Throwable rte) {
			fail("Type issue");
		}
	}
}
