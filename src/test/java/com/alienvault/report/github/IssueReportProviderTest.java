package com.alienvault.report.github;

import static com.alienvault.github.data.IssueFixtures.control;
import static com.alienvault.github.data.IssueFixtures.control2;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.alienvault.github.data.Issue;

public class IssueReportProviderTest extends BasicTest {

	private static String SORTED_CONTENT = "\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\",,,,,,,,,,,,------////0000000000000000011111111111111111223899999999::::::::::::::::::::RRRRTTTTUUUUZZ[]___aaaaaaaacccccddddddeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeiiiiiiiiillllnnnooooooooooooppppppppprrrrrrrrrrrrsssssssssssssssssssssstttttttttttttttttttttttttttttttttttttuuyyyy{{{{{}}}}}";

	@Test
	public void runReport() {
		IssueDataSink mockSink = mock(IssueDataSink.class);

		List<Issue> issues = new ArrayList<>();
		issues.add(control);
		issues.add(control2);
		when(mockSink.getIssues()).thenReturn(issues);

		Set<String> userRepos = new HashSet<>();
		userRepos.add(control.getUser() + "/" + control.getRepository());
		userRepos.add(control2.getUser() + "/" + control2.getRepository());
		when(mockSink.getUserRepos()).thenReturn(userRepos);

		IssueReportProvider provider = new IssueReportProvider(mockSink);
		assertEquals(SORTED_CONTENT, getStringContentsAsSortedChars(provider.getGHIssueReport()));
	}

}
