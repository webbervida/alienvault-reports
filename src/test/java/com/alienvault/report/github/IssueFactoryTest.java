package com.alienvault.report.github;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.junit.Test;

import com.alienvault.github.data.Issue;
import com.alienvault.github.data.IssueFactory;

public class IssueFactoryTest {

	private final static String SIMPLE_PAYLOAD = "{\"data\":{\"repository\":{\"issues\":{\"edges\":[{\"node\":"
			+ "{\"createdAt\":       \"1999-01-01T00:00:00Z\",                           "
			+ "\"number\":             1,                                                "
			+ "\"title\":            \"a bug\",                                          "
			+ "\"state\":            \"OPEN\",                                           "
			+ "\"user\":             \"bob\",                                            "
			+ "\"repository\":      \"repo\"            }}]}}}}";

	@Test
	public void convenienceMethodTest() {
		Set<Issue> oneIssue = IssueFactory.getIssuesFromJson("bob", "repo", new JSONObject(SIMPLE_PAYLOAD));
		assertEquals(1, oneIssue.size());

		List<Issue> myIssueList = new ArrayList<Issue>();
		myIssueList.addAll(oneIssue);
		Issue test = myIssueList.get(0);
		assertEquals("bob", test.getUser());
		assertEquals("repo", test.getRepository());
		assertEquals("OPEN", test.getState());
		assertEquals("a bug", test.getTitle());
		assertEquals("1999-01-01T00:00", "" + test.getCreatedAt());
	}
}
