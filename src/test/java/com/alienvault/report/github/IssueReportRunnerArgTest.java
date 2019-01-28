package com.alienvault.report.github;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class IssueReportRunnerArgTest {

	@Test
	public void testNoArguments() {
		try {
			new IssuesReportRunner(new String[] { "" });
			fail("No args to report util failed");
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("authTokenRequiredError")) > -1);
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("atLeastOneRepoError")) > -1);
		}
	}

	@Test
	public void testNoReposButHaveAuth() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.AUTH_ARG_TOKEN + "ZZZ" });
			fail("No auth to report util failed");
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("authTokenRequiredError")) == -1);
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("atLeastOneRepoError")) > -1);
		}
	}

	@Test
	public void testGoodRepoArg() {
		try {
			new IssuesReportRunner(new String[] { "someUser/someRepo" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("atLeastOneRepoError")) == -1);
		}
	}

	@Test
	public void testMalformedAuthArg() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.AUTH_ARG_TOKEN + "*&^%$#@!" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("badAuthErrorPrefix")) > -1);
		}
	}

	@Test
	public void testBadEndPointArg() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.ENDPOINT_ARG_TOKEN + "garbage" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("badEndPointErrorPrefix")) > -1);
		}
	}

	@Test
	public void testGoodEndPointArg() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.ENDPOINT_ARG_TOKEN + "https://foobar.com" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("badEndPointErrorPrefix")) == -1);
		}
	}

	@Test
	public void testBadRepoArg() {
		try {
			new IssuesReportRunner(new String[] { "/" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("smallUserRepoError")) > -1);
		}
	}

	@Test
	public void testBadMaxRuntimeArg() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.MAX_RUNTIME_SECONDS_ARG_TOKEN + "9999999999999" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("maxSecondsErrorPrefix")) > -1);
		}
	}

	@Test
	public void testBadOverDefaultRuntimeArg() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.MAX_RUNTIME_SECONDS_ARG_TOKEN + "9999" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("maxSecondsErrorPrefix")) > -1);
		}
	}

	@Test
	public void testBadMaxRuntimeArgNegVal() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.MAX_RUNTIME_SECONDS_ARG_TOKEN + "-1" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("maxSecondsErrorPrefix")) > -1);
		}
	}

	@Test
	public void testMalformedBadMaxRuntimeArg() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.MAX_RUNTIME_SECONDS_ARG_TOKEN + "XXX" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("maxSecondsErrorPrefix")) > -1);
		}
	}

	@Test
	public void testGoodMaxRuntimeArg() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.MAX_RUNTIME_SECONDS_ARG_TOKEN + "10" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("maxSecondsErrorPrefix")) == -1);
		}
	}

	@Test
	public void testLargeNumThreadsArg() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.THREADS_ARG_TOKEN + "9999999999999" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("maxThreadsErrorPrefix")) > -1);
		}
	}

	@Test
	public void testLargeNumThreadsArgNegVal() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.THREADS_ARG_TOKEN + "-1" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("maxThreadsErrorPrefix")) > -1);
		}
	}

	@Test
	public void testLargeNumThreadsArgOverDefault() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.THREADS_ARG_TOKEN + "9999" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("maxThreadsErrorPrefix")) > -1);
		}
	}

	@Test
	public void testMalformedThreadsArg() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.THREADS_ARG_TOKEN + "XXX" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("maxThreadsErrorPrefix")) > -1);
		}
	}

	@Test
	public void testGoodThreadsArg() {
		try {
			new IssuesReportRunner(new String[] { IssuesReportRunner.THREADS_ARG_TOKEN + "10" });
		} catch (IllegalArgumentException expectedException) {
			assertTrue(expectedException.getMessage()
					.indexOf(IssuesReportRunner.resourceBundle.getString("maxThreadsErrorPrefix")) == -1);
		}
	}
}
