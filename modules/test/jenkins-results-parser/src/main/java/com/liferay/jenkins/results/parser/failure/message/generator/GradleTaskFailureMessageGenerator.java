/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.failure.message.generator;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;

/**
 * @author Yi-Chen Tsai
 */
public class GradleTaskFailureMessageGenerator
	extends BaseFailureMessageGenerator {

	@Override
	public String getMessage(String consoleText) {
		Element errorMessageElement = getMessageElement(consoleText);

		if (errorMessageElement == null) {
			return null;
		}

		try {
			return Dom4JUtil.format(errorMessageElement);
		}
		catch (IOException ioException) {
			return ioException.getMessage();
		}
	}

	@Override
	public Element getMessageElement(String consoleText) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(consoleText)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		int start = consoleText.length();

		Matcher javaErrorMatcher = _javaErrorPattern.matcher(consoleText);

		int regionStart = _getRegionStart(consoleText);

		javaErrorMatcher.region(regionStart, consoleText.length());

		if (javaErrorMatcher.find()) {
			String snippet = javaErrorMatcher.group();

			int snippetStart = consoleText.lastIndexOf(snippet);

			if (snippetStart < start) {
				start = snippetStart;
			}
		}

		Matcher taskFailedMatcher = _taskFailedPattern.matcher(consoleText);

		taskFailedMatcher.region(regionStart, consoleText.length());

		if (taskFailedMatcher.find()) {
			String snippet = taskFailedMatcher.group(1);

			int snippetStart = consoleText.lastIndexOf(snippet);

			if (snippetStart < start) {
				start = snippetStart;
			}
		}

		if (start != consoleText.length()) {
			sb.append(getConsoleTextSnippetByStart(consoleText, start));

			sb.append("\n\n");
		}

		if (consoleText.contains(_TOKEN_WHAT_WENT_WRONG)) {
			int snippetStart = consoleText.lastIndexOf(_TOKEN_WHAT_WENT_WRONG);

			int whereIndex = consoleText.lastIndexOf(
				_TOKEN_WHERE, snippetStart);

			if (whereIndex != -1) {
				snippetStart = whereIndex;
			}

			snippetStart = consoleText.lastIndexOf("\n", snippetStart);

			sb.append(getConsoleTextSnippetByStart(consoleText, snippetStart));
		}

		if (sb.length() == 0) {
			return null;
		}

		return Dom4JUtil.toCodeSnippetElement(sb.toString());
	}

	protected static final int MAXIMUM_REGION_SIZE = 1024 * 1024 * 5;

	private int _getRegionStart(String consoleText) {
		int start = consoleText.length() - MAXIMUM_REGION_SIZE;

		if (start <= 0) {
			return 0;
		}

		int index = consoleText.indexOf("\n", start);

		if (index != -1) {
			return index;
		}

		return start;
	}

	private static final String _TOKEN_WHAT_WENT_WRONG = "* What went wrong:";

	private static final String _TOKEN_WHERE = "* Where:";

	private static final Pattern _javaErrorPattern = Pattern.compile(
		"^[^\\n]+?\\.java:\\d+: error:[^\\n]+",
		Pattern.MULTILINE | Pattern.UNIX_LINES);
	private static final Pattern _taskFailedPattern = Pattern.compile(
		"\\n([ \\t]+\\[exec\\] > Task :[^ ]+ FAILED)");

}