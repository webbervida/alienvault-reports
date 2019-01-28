package com.alienvault.github.data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class IssueFactory {

	private static final String CREATED_AT = "createdAt";

	private static final String DATA = "data";

	private static final String EDGES = "edges";

	private static final String NUMBER = "number";

	private static final String ISSUES = "issues";

	private static final String NODE = "node";

	private static final String REPOSITORY = "repository";

	private static final String STATE = "state";

	private static final String TITLE = "title";

	private IssueFactory() {
	}

	public static Set<Issue> getIssuesFromJson(String user, String repository, JSONObject jsonData) {

		Set<Issue> issues = new HashSet<>();

		JSONArray nodes = jsonData.getJSONObject(DATA).getJSONObject(REPOSITORY).getJSONObject(ISSUES)
				.getJSONArray(EDGES);

		for (int i = 0; i < nodes.length(); i++) {
			JSONObject oneIssue = ((JSONObject) nodes.get(i)).getJSONObject(NODE);

			issues.add(new Issue(getLocalDateFromISO8601UTC(oneIssue.getString(CREATED_AT)), oneIssue.getInt(NUMBER),
					oneIssue.getString(STATE), oneIssue.getString(TITLE), repository, user));
		}
		return issues;
	}

	public static LocalDateTime getLocalDateFromISO8601UTC(String dateStr) {
		return LocalDateTime.parse(dateStr, Issue.PRETTY_TIME_FORMAT);
	}
}