package com.alienvault.report.github;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alienvault.github.data.Issue;

public class IssuesReportRunner extends NetDataReportRunner {

	private static final String SMALL_USER_REPO_ERROR = "smallUserRepoError";
	private static final String AT_LEAST_ONE_REPO_ERROR = "atLeastOneRepoError";
	private static final String REPORT_SPACING = "reportSpacing";
	private static final String GIT_HUB_ISSUE_REPORT_USAGE = "gitHubIssueReportUsage";

	protected Set<String> userRepositories = new HashSet<>();

	protected Integer reportSpacing;

	private static final Logger logger = LoggerFactory.getLogger(IssuesReportRunner.class);

	public IssuesReportRunner(String[] args) {
		super(args);
		loadDefaultConfiguration();
		validateAndSetRuntimeSettings(args);
	}

	public void validateAndSetRuntimeSettings(String[] args) {

		// Below design is 'tell me all the things that are bad',
		// as opposed to blowing up on first bad thing.
		List<String> badArgStuff = new ArrayList<>();
		for (String arg : args) {
			testAndSetAuthTokenArg(badArgStuff, arg);
			testAndSetThreadArg(badArgStuff, arg);
			testAndSetRepositoryArg(badArgStuff, arg);
			testAndSetAllowableRuntimeArg(badArgStuff, arg);
			testAndSetEndpointArg(badArgStuff, arg);
		}

		testRequiredArgsSet(badArgStuff);
		complainAndThrowExceptionIfAppropriate(badArgStuff);
	}

	private void testAndSetRepositoryArg(List<String> badStuff, String arg) {
		if (arg.chars().filter(ch -> ch == '/').count() == 1) {
			if (arg.length() < 5) {
				badStuff.add(resourceBundle.getString(SMALL_USER_REPO_ERROR));
			} else {
				userRepositories.add(arg);
			}
		}
	}

	private void testAndSetAllowableRuntimeArg(List<String> badStuff, String arg) {
		if (arg.indexOf(MAX_RUNTIME_SECONDS_ARG_TOKEN) > -1) {
			try {
				int tempMaxSeconds = Integer.parseInt(arg.substring(MAX_RUNTIME_SECONDS_ARG_TOKEN.length()));

				if (tempMaxSeconds < 1 || tempMaxSeconds > maxRuntime) {
					badStuff.add(resourceBundle.getString(MAX_SECONDS_ERROR_PREFIX) + maxRuntime);
				} else {
					allowableRuntimeSeconds = tempMaxSeconds;
				}
			} catch (Exception e) {
				badStuff.add(resourceBundle.getString(MAX_SECONDS_ERROR_PREFIX) + maxRuntime);
			}
		}
	}

	public void testRequiredArgsSet(List<String> badStuff) {
		super.testRequiredArgsSet(badStuff);
		if (userRepositories.isEmpty()) {
			badStuff.add(resourceBundle.getString(AT_LEAST_ONE_REPO_ERROR));
		}
	}

	@Override
	public void loadDefaultConfiguration() {
		super.loadDefaultConfiguration();
		reportSpacing = Integer.parseInt(resourceBundle.getString(REPORT_SPACING));
	}

	private void complainAndThrowExceptionIfAppropriate(List<String> badStuff) {

		if (!badStuff.isEmpty()) {
			StringBuilder b = new StringBuilder();
			b.append("Bad command line arguments: \n");
			for (String oneGripe : badStuff) {
				b.append(oneGripe + "\n");
			}
			printUsage();
			throw new IllegalArgumentException(b.toString());
		}
	}

	public void printUsage() {
		System.out.println(resourceBundle.getString(GIT_HUB_ISSUE_REPORT_USAGE));
	}

	public String getReport() {
		StringBuilder report = new StringBuilder();
		long startTime = System.currentTimeMillis();
		List<Future<Boolean>> futures = null;

		if (logger.isDebugEnabled()) {
			logger.debug("starting");
		}

		// Create a data sink that all workers share
		IssueDataSink sharedDataSink = new IssueDataSink(userRepositories);

		// Setup pool of threads so we can get better performance via parallelism
		ExecutorService customSizedThreadPool = Executors.newFixedThreadPool(numberWorkerThreads);

		try {
			futures = customSizedThreadPool.invokeAll(getCallableDataSources(sharedDataSink), allowableRuntimeSeconds,
					TimeUnit.SECONDS);
			// State of workers is determined below
			customSizedThreadPool.shutdownNow();
			checkWorkersCompleted(futures);
			report.append(new IssueReportProvider(sharedDataSink).getGHIssueReport().toString(reportSpacing));
		} catch (Exception e) {
			// Command line feedback regardless of logging threshold.
			report.append(e.getMessage());
			if (e.getCause() != null) {
				report.append("\n" + e.getCause().getMessage());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Finished in " + (System.currentTimeMillis() - startTime));
		}
		return report.toString();
	}

	private List<Callable<Boolean>> getCallableDataSources(IssueDataSink sharedDataSink) {
		List<Callable<Boolean>> callables = new ArrayList<>();
		for (String userRepo : sharedDataSink.getUserRepos()) {
			String[] nameAndProject = userRepo.split(Issue.USER_REPO_DELIMITER);
			callables.add(
					new IssuesDataSource(endPoint, nameAndProject[0], nameAndProject[1], authToken, sharedDataSink));
		}
		return callables;
	}
}