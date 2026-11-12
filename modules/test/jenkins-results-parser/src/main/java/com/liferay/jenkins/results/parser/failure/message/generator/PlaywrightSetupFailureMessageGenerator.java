/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.failure.message.generator;

import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;

/**
 * @author Calum Ragan
 */
public class PlaywrightSetupFailureMessageGenerator
	extends BaseFailureMessageGenerator {

	@Override
	public String getMessage(String consoleText) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(consoleText)) {
			return null;
		}

		Matcher matcher = _pattern.matcher(consoleText);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group(1);
	}

	@Override
	public Element getMessageElement(Build build) {
		String jobVariant = build.getJobVariant();

		if (!jobVariant.contains("playwright-js")) {
			return null;
		}

		return getMessageElement(build.getConsoleText());
	}

	@Override
	public Element getMessageElement(String consoleText) {
		String errorMessage = getMessage(consoleText);

		if (errorMessage == null) {
			return null;
		}

		Element messageElement = Dom4JUtil.getNewElement(
			"div", null,
			Dom4JUtil.getNewElement(
				"p", null,
				Dom4JUtil.getNewElement("strong", null, errorMessage)));

		int end = consoleText.indexOf("\n", consoleText.indexOf(errorMessage));

		messageElement.add(
			getConsoleTextSnippetElementByEnd(consoleText, true, end));

		return messageElement;
	}

	private static final Pattern _pattern = Pattern.compile(
		"(Duplicate OSGi modules found for [^\\n]+|Unable to (?:deploy|find) " +
			"OSGi module [^\\n]+)");

}