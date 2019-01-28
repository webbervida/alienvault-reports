package com.alienvault.github.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;

public class Issue implements Comparable<Issue> {

	private final LocalDateTime createdAt;

	private final String state;
	private final String title;
	private final String repository;
	private final String user;

	private final Integer issueNumber;

	public static final String USER_REPO_DELIMITER = "/";

	public static final DateTimeFormatter PRETTY_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public Issue(final LocalDateTime createdAt, final Integer issueNumber, final String state, final String title,
			final String repository, final String user) {
		this.createdAt = createdAt;
		this.issueNumber = issueNumber;
		this.state = state;
		this.title = title;
		this.repository = repository;
		this.user = user;
	}

	@JSONPropertyName("created_at")
	public String getCreatedAtPretty() {
		return PRETTY_TIME_FORMAT.format(createdAt);
	}

	@JSONPropertyIgnore
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	@JSONPropertyName("id")
	public Integer getIssueNumber() {
		return issueNumber;
	}

	@JSONPropertyName("state")
	public String getStatePretty() {
		return (state == null) ? "" : state.toLowerCase();
	}

	@JSONPropertyIgnore
	public String getState() {
		return state;
	}

	public String getTitle() {
		return title;
	}

	@JSONPropertyIgnore
	public String getRepository() {
		return repository;
	}

	@JSONPropertyName("repository")
	public String getFullRepositoryPath() {
		return user + USER_REPO_DELIMITER + repository;
	}

	@JSONPropertyIgnore
	public String getUser() {
		return user;
	}

	@Override
	public int compareTo(Issue o) {
		return this.getCreatedAt().compareTo(o.getCreatedAt());
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(createdAt).append(state).append(title).append(state).append(repository)
				.append(user).append(issueNumber).toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
