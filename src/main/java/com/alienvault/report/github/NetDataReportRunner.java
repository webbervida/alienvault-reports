package com.alienvault.report.github;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import com.alienvault.report.ReportRunner;
import com.alienvault.report.exception.ReportTerminationException;

public abstract class NetDataReportRunner implements ReportRunner {

	public static final String BAD_AUTH_ERROR_PREFIX = "badAuthErrorPrefix";
	public static final String BAD_END_POINT_ERROR_PREFIX = "badEndPointErrorPrefix";
	public static final String MAX_THREADS_ERROR_PREFIX = "maxThreadsErrorPrefix";
	public static final String MAX_SECONDS_ERROR_PREFIX = "maxSecondsErrorPrefix";
	public static final String AUTH_TOKEN_REQUIRED_ERROR = "authTokenRequiredError";
	public static final String DEFAULT_RUNTIME_SECONDS = "defaultRuntimeSeconds";
	public static final String DEFAULT_WORKER_THREADS = "defaultWorkerThreads";
	public static final String END_POINT = "endPoint";
	public static final String MAX_RUNTIME = "maxRuntime";
	public static final String MAX_THREADS = "maxThreads";
	public static final String AUTH_TOKEN_CHECK = "authTokenCheck";
	public static final String WORKER_THREAD_DIED_ERROR = "workerThreadDiedError";

	public static final String AUTH_ARG_TOKEN = "AUTHTOKEN:";

	public static final String ENDPOINT_ARG_TOKEN = "ENDPOINT:";

	public static final String THREADS_ARG_TOKEN = "THREADS:";

	public static final String MAX_RUNTIME_SECONDS_ARG_TOKEN = "MAXRUNTIME:";

	protected Integer numberWorkerThreads;
	protected Integer allowableRuntimeSeconds;
	protected Integer maxRuntime;
	protected Integer maxThreads;
	protected String authToken;
	protected String endPoint;
	protected String authTokenCheck;

	public static final ResourceBundle resourceBundle = ResourceBundle.getBundle("com.alienvault.report.github.config");

	protected NetDataReportRunner(String[] args) {
	}
	
	protected abstract void validateAndSetRuntimeSettings(String[] args);

	public static void checkWorkersCompleted(List<Future<Boolean>> futures) {
		try {
			for (Future<Boolean> oneResult : futures) {
				if (!oneResult.isDone() || !oneResult.get().equals(true)) {
					throw new ReportTerminationException(resourceBundle.getString(WORKER_THREAD_DIED_ERROR));
				}
			}
		} catch (Exception e) {
			throw new ReportTerminationException(resourceBundle.getString(WORKER_THREAD_DIED_ERROR));
		}
	}

	public void loadDefaultConfiguration() {
		allowableRuntimeSeconds = Integer.parseInt(resourceBundle.getString(DEFAULT_RUNTIME_SECONDS));
		numberWorkerThreads = Integer.parseInt(resourceBundle.getString(DEFAULT_WORKER_THREADS));
		endPoint = resourceBundle.getString(END_POINT);
		maxRuntime = Integer.parseInt(resourceBundle.getString(MAX_RUNTIME));
		maxThreads = Integer.parseInt(resourceBundle.getString(MAX_THREADS));
		authTokenCheck = resourceBundle.getString(AUTH_TOKEN_CHECK);
	}

	protected void testAndSetAuthTokenArg(List<String> badStuff, String arg) {
		if (arg.startsWith(AUTH_ARG_TOKEN)) {
			String tempToken = arg.substring(AUTH_ARG_TOKEN.length());
			if (!tempToken.matches(authTokenCheck)) {
				badStuff.add(resourceBundle.getString(BAD_AUTH_ERROR_PREFIX) + authTokenCheck);
			} else {
				authToken = tempToken;
			}
		}
	}

	protected void testAndSetEndpointArg(List<String> badStuff, String arg) {
		if (arg.startsWith(ENDPOINT_ARG_TOKEN)) {
			String tempEndpoint = arg.substring(ENDPOINT_ARG_TOKEN.length());
			try {
				new URL(tempEndpoint);
				endPoint = tempEndpoint;
			} catch (MalformedURLException e) {
				badStuff.add(resourceBundle.getString(BAD_END_POINT_ERROR_PREFIX) + tempEndpoint);
			}
		}
	}

	protected void testAndSetThreadArg(List<String> badStuff, String arg) {
		if (arg.startsWith(THREADS_ARG_TOKEN)) {
			try {
				int tempNumberWorkerThreads = Integer.parseInt(arg.substring(THREADS_ARG_TOKEN.length()));

				if (tempNumberWorkerThreads < 1 || tempNumberWorkerThreads > maxThreads) {
					badStuff.add(resourceBundle.getString(MAX_THREADS_ERROR_PREFIX) + maxThreads);
				} else {
					numberWorkerThreads = tempNumberWorkerThreads;
				}
			} catch (Exception e) {
				badStuff.add(resourceBundle.getString(MAX_THREADS_ERROR_PREFIX) + maxThreads);
			}
		}
	}

	protected void testRequiredArgsSet(List<String> badStuff) {
		if (authToken == null) {
			badStuff.add(resourceBundle.getString(AUTH_TOKEN_REQUIRED_ERROR));
		}
	}
}
