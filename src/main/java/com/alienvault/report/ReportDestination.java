package com.alienvault.report;

public interface ReportDestination {

	void handleContent(String content);

	String[] getConfigArgs();
}
