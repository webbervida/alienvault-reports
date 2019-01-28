package com.alienvault;

import com.alienvault.report.ReportingFactory;

public class Main {

	/**
	 * @param args String array with Github repositories with the format
	 *             "owner/repository"
	 *
	 */
	public static void main(String[] args) {
		// Run report in a way that doesn't require compiling
		// against possible report types or places content could go
		ReportingFactory rf = new ReportingFactory();
		rf.getDestinationImpl(args).handleContent(rf.getReportImpl(args).getReport());
	}
}