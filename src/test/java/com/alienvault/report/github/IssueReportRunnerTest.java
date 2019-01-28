package com.alienvault.report.github;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IssuesReportRunner.class, IssueReportProvider.class, Future.class })
public class IssueReportRunnerTest extends BasicTest {

	private static String SORTED_CONTENT = "\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\",,,,,,----//000000111111222223389999::::::::::::MTZ[]__aaaaacccccddddeeeeeeeeeeeeeeeegiiiiiijkllnnnnooooooooopppppprrrrrrrrrrsssssssssssttttttttttuuuuyyy{{{{}}}}";

	@Test
	public void testCheckThreadWorkCompleted() throws Exception {
		List<Future<Boolean>> futures = new ArrayList<>();
		try {
			// Setup mock Callables to emulate data source
			@SuppressWarnings("unchecked")
			Future<Boolean> futureMock = PowerMockito.mock(Future.class);
			PowerMockito.doReturn(new Boolean(false)).when(futureMock, "isDone");
			futures.add(futureMock);
			NetDataReportRunner.checkWorkersCompleted(futures);
			fail("CheckThreadWorkCompleted should have seen incomplete Future");
		} catch (Exception e) {
			// Ignore
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCheckThreadWorkCompleted2() throws Exception {
		List<Future<Boolean>> futures = new ArrayList<>();
		try {
			// Setup mock Callables to emulate data source
			Future<Boolean> futureMock = (Future<Boolean>) PowerMockito.mock(Future.class);
			PowerMockito.doReturn(new Boolean(true)).when(futureMock, "isDone");
			PowerMockito.doReturn(new Boolean(true)).when(futureMock, "get");

			futures.add(futureMock);
			NetDataReportRunner.checkWorkersCompleted(futures);
		} catch (Exception e) {
			fail("CheckThreadWorkCompleted should have not Thrown Exception:" + e.getClass().getName());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRunSimpleReport() throws Exception {

		String[] args = new String[] { IssuesReportRunner.AUTH_ARG_TOKEN + "XXX", "user/repo" };
		IssuesReportRunner issuesReportRunnerSpy = PowerMockito.spy(new IssuesReportRunner(args));

		IssueReportProvider issueReportProviderMock = PowerMockito.mock(IssueReportProvider.class);
		PowerMockito.whenNew(IssueReportProvider.class).withAnyArguments().thenReturn(issueReportProviderMock);

		// when above called return json report
		JSONObject jsonReport = new JSONObject();
		jsonReport.put("someKey", "someValue");
		PowerMockito.doReturn(jsonReport).when(issueReportProviderMock, "getGHIssueReport");

		// Setup mock Callables to emulate data source
		Callable<Boolean> callableMock = (Callable<Boolean>) PowerMockito.mock(Callable.class);

		PowerMockito.doReturn(new Boolean(true)).when(callableMock, "call");

		List<Callable<Boolean>> callables = new ArrayList<>();
		callables.add(callableMock);
		PowerMockito.doReturn(callables).when(issuesReportRunnerSpy, "getCallableDataSources", anyObject());

		assertEquals("{\"someKey\": \"someValue\"}", issuesReportRunnerSpy.getReport());
	}

	@Test
	public void testRunSimpleReport2() throws Exception {
		String mockGithubResponse = "{\"data\":{\"repository\":{\"issues\":{\"edges\":[{\"node\":{\"number\":2,\"state\":\"OPEN\",\"title\":\"Missing project skeleton\",\"createdAt\":\"2019-01-09T22:38:31Z\"}}],\"pageInfo\":{\"hasNextPage\":false,\"endCursor\":\"Y3Vyc29yOnYyOpHOF7LHrA==\"}}}}}";

		ClientAndServer mockServer = ClientAndServer.startClientAndServer(1080);

		new MockServerClient("127.0.0.1", 1080).when(request().withMethod("POST").withPath("/"))
				.respond(response().withStatusCode(200).withBody(mockGithubResponse).withDelay(TimeUnit.SECONDS, 0));

		IssuesReportRunner irr = new IssuesReportRunner(
				new String[] { "ENDPOINT:http://127.0.0.1:1080", "user/repo", "AUTHTOKEN:someauthtoken" });

		JSONObject tempObj = new JSONObject(irr.getReport());
		mockServer.stop();
		assertEquals(SORTED_CONTENT, getStringContentsAsSortedChars(tempObj));
	}
}