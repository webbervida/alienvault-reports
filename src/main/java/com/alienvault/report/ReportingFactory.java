package com.alienvault.report;

import java.util.ResourceBundle;

import com.alienvault.report.exception.ReportTerminationException;

public class ReportingFactory {

	public static final String CUSTOM_REPORT = "REPORT:";

	public static final String CUSTOM_DESTINATION = "DESTINATION:";

	private ResourceBundle resourceBundle = null;

	public ReportingFactory() {
		try {
			resourceBundle = ResourceBundle.getBundle("com.alienvault.report.config");
		} catch (Exception e) {
			throw new ReportTerminationException("Unable to load config.properties");
		}
	};

	public ReportRunner getReportImpl(String[] args) {
		return (ReportRunner) getImpl(args, resourceBundle.getString("defaultReport"), CUSTOM_REPORT);
	}

	public ReportDestination getDestinationImpl(String[] args) {
		return (ReportDestination) getImpl(args, resourceBundle.getString("defaultDestination"), CUSTOM_DESTINATION);
	}

	private static Object getImpl(String[] args, String defaultImpl, String customImplToken) {
		Object implInstance = null;
		String implClass = defaultImpl;

		for (String arg : args) {
			if (arg.startsWith(customImplToken)) {
				implClass = arg.substring(customImplToken.length());
			}
		}
		try {
			implInstance = Class.forName(implClass).getConstructor(String[].class).newInstance(new Object[] { args });
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot instantiate " + implClass, e);
		}
		return implInstance;
	}
}
