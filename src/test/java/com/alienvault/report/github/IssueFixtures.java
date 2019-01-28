package com.alienvault.report.github;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.alienvault.github.data.Issue;

public interface IssueFixtures {

	static final String cDate = "1999-01-01 00:00:00";

	static final String cDateDefaultToString = "1999-01-01T00:00";

	static final LocalDateTime cCreatedAt = LocalDateTime.parse(cDate,
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

	static final Integer cIssueNumber = 1;
	static final String cState = "OPEN";
	static final String cTitle = "testTitle";
	static final String cRepository = "testRepo";
	static final String cUser = "testUser";
	static final String PRETTY_DATE = "1999-01-01T00:00:00Z";
	static final String FULL_REPO_PATH = cUser + "/" + cRepository;
	static final String STATE_PRETTY = "open";

	static final Issue control = new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser);

	static final int myDiff = 1;
	static final LocalDateTime dCreatedAt = cCreatedAt.minusDays(myDiff);
	static final Integer dIssueNumber = cIssueNumber + myDiff;
	static final String dState = cState + myDiff;
	static final String dTitle = cTitle + myDiff;
	static final String dRepository = cRepository + myDiff;
	static final String dUser = cUser + myDiff;

	static final Issue control2 = new Issue(dCreatedAt, dIssueNumber, dState, dTitle, dRepository, dUser);
}
