package com.alienvault.github.datasource;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alienvault.report.github.IssueDataSink;

public abstract class ReportableHttpDataSource {

	private static final String BEARER = "Bearer ";

	private static final String APPLICATION_JSON = "application/json";

	private static final String CONTENT_TYPE = "Content-Type";

	private static final String AUTHORIZATION = "Authorization";

	private static final String POST = "POST";

	protected String authToken;

	protected String endPoint;

	protected HttpURLConnection connection;

	protected IssueDataSink statsTracker;

	protected Boolean finishedSuccessfully;

	private static Logger logger = LoggerFactory.getLogger(ReportableHttpDataSource.class);

	public abstract void consumeDataSource();

	protected void configurePostConnection() throws IOException {
		if (endPoint.toLowerCase().indexOf("https:") > -1) {
			connection = (HttpsURLConnection) new URL(endPoint).openConnection();
		} else {
			connection = (HttpURLConnection) new URL(endPoint).openConnection();
		}
		connection.setRequestMethod(POST);
		connection.setRequestProperty(AUTHORIZATION, BEARER + authToken);
		connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
		connection.setUseCaches(false);
		connection.setDoOutput(true);
	}

	protected void postData(String postData) throws IOException {
		DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
		outStream.writeBytes(postData);
		outStream.flush();
		outStream.close();
	}

	protected String readResponse(BufferedReader inReader) throws IOException {
		String line = null;
		StringBuilder jsonResponse = new StringBuilder();

		while ((line = inReader.readLine()) != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(line);
			}
			jsonResponse.append(line);
		}
		return jsonResponse.toString();
	}
}
