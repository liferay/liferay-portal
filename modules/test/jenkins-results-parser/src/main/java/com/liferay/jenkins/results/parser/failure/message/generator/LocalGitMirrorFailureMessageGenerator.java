/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.failure.message.generator;

import com.liferay.jenkins.results.parser.Dom4JUtil;

import org.dom4j.Element;

/**
 * @author Peter Yoo
 */
public class LocalGitMirrorFailureMessageGenerator
	extends BaseFailureMessageGenerator {

	@Override
	public String getMessage(String consoleText) {
		if (!consoleText.contains(_TOKEN_LOCAL_GIT_FAILURE_END) ||
			!consoleText.contains(_TOKEN_LOCAL_GIT_FAILURE_START)) {

			return null;
		}

		return "Unable to synchronize with local Git mirror.";
	}

	@Override
	public Element getMessageElement(String consoleText) {
		if (!consoleText.contains(_TOKEN_LOCAL_GIT_FAILURE_END) ||
			!consoleText.contains(_TOKEN_LOCAL_GIT_FAILURE_START)) {

			return null;
		}

		Element messageElement = Dom4JUtil.getNewElement("div");

		Dom4JUtil.getNewElement(
			"p", messageElement,
			Dom4JUtil.getNewElement("strong", null, getMessage(consoleText)));

		int end = consoleText.indexOf(_TOKEN_LOCAL_GIT_FAILURE_END);

		int start = consoleText.lastIndexOf(
			_TOKEN_LOCAL_GIT_FAILURE_START, end);

		consoleText = consoleText.substring(start, end);

		int minIndex = consoleText.length();

		for (String string : new String[] {"error: ", "fatal: "}) {
			int index = consoleText.indexOf(string);

			if ((index == -1) || (index >= minIndex)) {
				continue;
			}

			minIndex = index;
		}

		int gitCommandIndex = consoleText.lastIndexOf("+ git", minIndex);

		if (gitCommandIndex != -1) {
			start = gitCommandIndex;
		}

		start = consoleText.lastIndexOf("\n", start);

		end = consoleText.lastIndexOf("\n");

		messageElement.add(
			getConsoleTextSnippetElement(consoleText, false, start, end));

		return messageElement;
	}

	private static final String _TOKEN_LOCAL_GIT_FAILURE_END = "BUILD FAILED";

	private static final String _TOKEN_LOCAL_GIT_FAILURE_START =
		"Too many retries while synchronizing GitHub pull request.";

}