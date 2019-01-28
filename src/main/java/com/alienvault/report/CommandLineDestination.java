package com.alienvault.report;

public class CommandLineDestination implements ReportDestination {
	
	private String[] configArgs;
	
	public CommandLineDestination(String[] configArgs) {
		this.configArgs = configArgs;
	}

	public void handleContent(String content) {
		System.out.println(content);
	}
	
	public String[] getConfigArgs() {
		return configArgs;
	}
}
