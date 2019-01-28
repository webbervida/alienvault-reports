package com.alienvault.report.github;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.alienvault.github.data.Issue;
import com.alienvault.github.data.IssueFactory;

public class IssueDataSink {

	private Set<Issue> issues;

	private Set<String> userRepos;

	public IssueDataSink(Set<String> userRepositories) {
		userRepos = userRepositories;
		issues = new HashSet<>();
	}

	public void updateDataFromJson(String user, String repository, JSONObject jsondata) {
		issues.addAll(IssueFactory.getIssuesFromJson(user, repository, jsondata));
	}

	public List<Issue> getIssues() {
		return new ArrayList<>(issues);
	}

	public Set<String> getUserRepos() {
		return new HashSet<>(userRepos);
	}
}
