package com.alienvault.report.github;

import java.util.Arrays;

import org.json.JSONObject;

abstract public class BasicTest {

	public static String getStringContentsAsSortedChars(JSONObject jsonObject) {
		String temp = jsonObject.toString(0).replace(" ", "");
		char tempArray[] = temp.toCharArray();
		Arrays.sort(tempArray);
		return new String(tempArray);
	}

	public static String getStringContentsAsSortedChars(String jsonStr) {
		return getStringContentsAsSortedChars(new JSONObject(jsonStr));
	}

	public static String getStringContentsAsSortedChars(char[] jsonChars) {
		return getStringContentsAsSortedChars(new String(jsonChars));
	}
}
