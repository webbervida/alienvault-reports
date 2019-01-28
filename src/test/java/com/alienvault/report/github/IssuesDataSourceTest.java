package com.alienvault.report.github;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.powermock.api.mockito.PowerMockito;

import com.alienvault.report.exception.DataSourceConsumptionException;

public class IssuesDataSourceTest extends BasicTest {

	private final static String mockGithubResponse = "{\"data\":{\"repository\":{\"issues\":{\"edges\":[{\"node\":{\"number\":1,\"state\":\"OPEN\",\"title\":\"We need some code in the pineapple\",\"createdAt\":\"2019-01-09T22:36:55Z\"}},{\"node\":{\"number\":2,\"state\":\"OPEN\",\"title\":\"Missing project skeleton\",\"createdAt\":\"2019-01-09T22:38:31Z\"}}],\"pageInfo\":{\"hasNextPage\":false,\"endCursor\":\"Y3Vyc29yOnYyOpHOF7LHrA==\"}}}}}";

	private final String cReport = "\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\",,,,,,,,,,,------///000000000111111112222222223335568999999:::::::::::::::::::MTTWZZ[]___aaaaaaaaacccccccddddddddeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeghiiiiiiiiiiijkllllmnnnnnnnnooooooooooooooopppppppppppprrrrrrrrrrrrrrrsssssssssssssssttttttttttttttttttuuuuuyyyy{{{{{}}}}}";

	private final String badQueryResponse = "{\"data\":{\"repository\":null},\"errors\":[{\"type\":\"NOT_FOUND\",\"path\":[\"repository\"],\"locations\":[{\"line\":1,\"column\":7}],\"message\":\"Could not resolve to a Repository with the name 'xxxx'.\"}]}";

	@Test
	public void testValidPost() throws Exception {

		ClientAndServer mockServer = ClientAndServer.startClientAndServer(1080);

		new MockServerClient("127.0.0.1", 1080).when(request().withMethod("POST").withPath("/"))
				.respond(response().withStatusCode(200).withBody(mockGithubResponse).withDelay(TimeUnit.SECONDS, 0));

		Set<String> userRepositories = new HashSet<>();
		userRepositories.add("user/repo");
		IssueDataSink ids = new IssueDataSink(userRepositories);
		IssuesDataSource idr = new IssuesDataSource("http://127.0.0.1:1080", "user", "repo", "auth", ids);
		idr.call();
		new IssueReportProvider(ids).getGHIssueReport().toString(0);
		String report = new IssueReportProvider(ids).getGHIssueReport().toString(0).replace(" ", "");
		assertEquals(cReport, getStringContentsAsSortedChars(report));

		mockServer.stop();
	}

	@Test
	public void testErrorsReturnedPost() throws Exception {
		ClientAndServer mockServer = null;
		try {
			mockServer = ClientAndServer.startClientAndServer(1080);

			new MockServerClient("127.0.0.1", 1080).when(request().withMethod("POST").withPath("/"))
					.respond(response().withStatusCode(200).withBody(badQueryResponse).withDelay(TimeUnit.SECONDS, 0));

			Set<String> userRepositories = new HashSet<>();
			userRepositories.add("user/repo");
			IssueDataSink ids = new IssueDataSink(userRepositories);
			IssuesDataSource idr = new IssuesDataSource("http://127.0.0.1:1080", "user", "repo", "auth", ids);
			idr.consumeDataSource();
			fail("expected DataSourceConsumptionException to be thrown ");
		} catch (DataSourceConsumptionException dsc) {

			if (dsc.getCause().getMessage().indexOf("Could not resolve to a Repository with the name") == -1) {
				fail("wrong fail message");
			}
		} catch (Exception e) {
			fail("Wrong type thrown");
		} finally {
			mockServer.stop();
		}
	}

	@Test
	public void testCallGetsExceptionFromConsumingDataNoInnerCause() throws Exception {

		IssuesDataSource idsMock = PowerMockito.mock(IssuesDataSource.class);
		PowerMockito.whenNew(IssuesDataSource.class).withAnyArguments().thenReturn(idsMock);
		DataSourceConsumptionException d = new DataSourceConsumptionException("Bang", null);
		PowerMockito.doThrow(d).when(idsMock).consumeDataSource();

		assertEquals(new Boolean(false), idsMock.call());
	}

	@Test
	public void testCallGetsExceptionFromConsumingDataInnerCause() throws Exception {

		IssuesDataSource idsMock = PowerMockito.mock(IssuesDataSource.class);
		PowerMockito.whenNew(IssuesDataSource.class).withAnyArguments().thenReturn(idsMock);
		DataSourceConsumptionException d = new DataSourceConsumptionException("Bang",
				new IllegalArgumentException("InnerBang"));
		PowerMockito.doThrow(d).when(idsMock).consumeDataSource();

		assertEquals(new Boolean(false), idsMock.call());
	}

	@Test
	public void testCreateJsonIfValid() throws Exception {
		try {
			IssuesDataSource.createJsonIfValid("garbage");
		} catch (DataSourceConsumptionException ignored) {
			if (ignored.getMessage().indexOf("Malformed response:") == -1) {
				fail("Bad Exception thrown");
			}
		}
	}

	@Test
	public void testConsumeDataSourceExplodes() throws Exception {

		IssuesDataSource idsMock = PowerMockito.mock(IssuesDataSource.class);

		PowerMockito.whenNew(IssuesDataSource.class).withAnyArguments().thenReturn(idsMock);

		PowerMockito.doThrow(new DataSourceConsumptionException("Bang", new IllegalArgumentException("InnerBang")))
				.when(idsMock).consumeDataSource();

	}
}
