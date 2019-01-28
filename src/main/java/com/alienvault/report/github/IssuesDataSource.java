package com.alienvault.report.github;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alienvault.github.datasource.ReportableHttpDataSource;
import com.alienvault.report.exception.DataSourceConsumptionException;

public class IssuesDataSource extends ReportableHttpDataSource implements Callable<Boolean> {

	private String cursor;
	private String user;
	private String repo;

	private boolean hasNextPage = true;

	private static Logger logger = LoggerFactory.getLogger(IssuesDataSource.class);

	private String queryTemplate;

	public static final ResourceBundle resourceBundle = ResourceBundle.getBundle("com.alienvault.report.github.config");

	public IssuesDataSource(String endPoint, String user, String repo, String authToken, IssueDataSink statsTracker) {
		setConfigDrivenProperties();
		this.endPoint = endPoint;
		this.user = user;
		this.repo = repo;
		this.authToken = authToken;
		this.statsTracker = statsTracker;
	}

	public Boolean call() {
		try {
			consumeDataSource();
			finishedSuccessfully = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			if (e.getCause() != null) {
				logger.error(e.getCause().getMessage());
			}
		}
		return finishedSuccessfully;
	}

	public void consumeDataSource() {
		try {
			while (hasNextPage) {
				JSONObject onePageOfResults = getPageOfAPIResults();

				JSONObject pageInfo = onePageOfResults.getJSONObject("data").getJSONObject("repository")
						.getJSONObject("issues").getJSONObject("pageInfo");

				hasNextPage = pageInfo.getBoolean("hasNextPage");
				cursor = pageInfo.getString("endCursor");
				statsTracker.updateDataFromJson(user, repo, onePageOfResults);
			}
		} catch (Exception e) {
			throw new DataSourceConsumptionException(resourceBundle.getString("dataSourceError"), e);
		}
	}

	private JSONObject getPageOfAPIResults() throws IOException {

		JSONObject response = null;

		configurePostConnection();

		postData(getPageQuery());

		if (connection.getResponseCode() != 200) {
			throw new IOException("Bad resp code " + connection.getResponseCode());
		}
		try (BufferedReader inReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String rawResponse = readResponse(inReader);
			response = createJsonIfValid(rawResponse);
		}
		return response;
	}

	public static JSONObject createJsonIfValid(String rawResponse) {
		JSONObject response = null;
		try {
			response = new JSONObject(rawResponse);

			if (response.has("errors")) {
				throw new IllegalArgumentException(
						"Response from API is not as expected:" + response.getJSONArray("errors"));
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("API returned valid response with no errors");
				}
			}
		} catch (JSONException je) {
			throw new DataSourceConsumptionException("Malformed response:" + je.getMessage(), je);
		}
		return response;
	}

	private String getPageQuery() {
		String resolvedQuery = queryTemplate.replace("USER_TOKEN", user).replace("PROJECT_TOKEN", repo);
		resolvedQuery = (cursor == null) ? resolvedQuery.replace("AFTER_CLAUSE", "")
				: resolvedQuery.replace("AFTER_CLAUSE", ", after:\\\"" + cursor + "\\\"");

		if (logger.isDebugEnabled()) {
			logger.debug(resolvedQuery);
		}
		return resolvedQuery;
	}

	private void setConfigDrivenProperties() {
		try {
			queryTemplate = resourceBundle.getString("gitHubQueryTemplate");
		} catch (Exception e) {
			throw new DataSourceConsumptionException("Unable to load config settings", e);
		}
	}
}
