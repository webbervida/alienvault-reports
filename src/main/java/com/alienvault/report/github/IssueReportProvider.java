package com.alienvault.report.github;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.alienvault.github.data.Issue;

public class IssueReportProvider {

	private final IssueDataSink dataSink;

	private static final DateTimeFormatter PRETTY_DAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public IssueReportProvider(final IssueDataSink dataSink) {
		this.dataSink = dataSink;
	}

	public JSONObject getGHIssueReport() {

		JSONObject report = new JSONObject();
		report.put("top_day", getTopDayForIssuesReport(getIssuesForTopDay()));
		report.put("issues", getIssuesSortedForReport());
		return report;
	}

	private Map.Entry<LocalDateTime, Set<Issue>> getIssuesForTopDay() {

		Map<LocalDateTime, Set<Issue>> issuesByDay = new HashMap<>();
		Integer largestCountOfIssuesForDay = 0;
		LocalDateTime dayWithLargestCount = LocalDateTime.MIN.truncatedTo(ChronoUnit.DAYS);

		for (Issue oneIssue : dataSink.getIssues()) {
			LocalDateTime truncatedToDay = oneIssue.getCreatedAt().truncatedTo(ChronoUnit.DAYS);
			Set<Issue> issuesForTheDay = issuesByDay.get(truncatedToDay);
			issuesForTheDay = (issuesForTheDay == null) ? new HashSet<>() : issuesForTheDay;
			issuesForTheDay.add(oneIssue);
			int numIssuesForThisDay = issuesForTheDay.size();

			// The 2 reasons a day can become interesting are:
			// 1) It has the highest count of issues
			// 2) It ties for the highest, but it is a more recent day
			if ((numIssuesForThisDay > largestCountOfIssuesForDay) || (dayWithLargestCount.isBefore(truncatedToDay)
					&& numIssuesForThisDay >= largestCountOfIssuesForDay)) {
				largestCountOfIssuesForDay = numIssuesForThisDay;
				dayWithLargestCount = truncatedToDay;
			}
			issuesByDay.put(truncatedToDay, issuesForTheDay);
		}

		return new AbstractMap.SimpleEntry<>(dayWithLargestCount, issuesByDay.get(dayWithLargestCount));
	}

	private JSONObject getTopDayForIssuesReport(Map.Entry<LocalDateTime, Set<Issue>> issuesForOneDay) {

		JSONObject topDayStats = new JSONObject();
		topDayStats.put("day", PRETTY_DAY_FORMAT.format(issuesForOneDay.getKey()));

		JSONObject numberIssuesPerRepoJson = new JSONObject();
		Integer numPerRepo = null;

		for (Issue oneIssue : issuesForOneDay.getValue()) {
			String userRepoKey = oneIssue.getUser() + Issue.USER_REPO_DELIMITER + oneIssue.getRepository();
			try {
				numPerRepo = numberIssuesPerRepoJson.getInt(userRepoKey);
				numberIssuesPerRepoJson.put(userRepoKey, ++numPerRepo);
			} catch (JSONException ignore) {
				// This will happen first time stat is placed into JSONObject
				numberIssuesPerRepoJson.put(userRepoKey, 1);
			}
		}

		// Per the requirements, we mention even those user repos where no issues
		// happened on the day where we had the most issues overall across all repos
		for (String oneUserRepo : dataSink.getUserRepos()) {
			if (!numberIssuesPerRepoJson.has(oneUserRepo)) {
				numberIssuesPerRepoJson.put(oneUserRepo, 0);
			}
		}
		topDayStats.put("occurrences", numberIssuesPerRepoJson);
		return topDayStats;
	}

	private List<Issue> getIssuesSortedForReport() {
		// The 'natural' sort for dates is oldest to newest. We want the opposite of
		// that.
		List<Issue> sortedIssues = new ArrayList<>(dataSink.getIssues());
		Collections.sort(sortedIssues, Collections.reverseOrder());
		return sortedIssues;
	}
}
