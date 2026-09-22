/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.failure.message.generator;

/**
 * @author Peter Yoo
 * @author Yi-Chen Tsai
 */
public class SourceFormatFailureMessageGenerator
	extends BaseFailureMessageGenerator {

	@Override
	public String getMessage(String consoleText) {
		if (!consoleText.contains(_TOKEN_SOURCE_FORMAT)) {
			return null;
		}

		int start = consoleText.lastIndexOf("format-source-files:");

		if (consoleText.contains(_TOKEN_FORMATTING_ISSUES) && (start == -1)) {
			start = consoleText.lastIndexOf(_TOKEN_FORMATTING_ISSUES);
		}

		start = consoleText.lastIndexOf("\n", start);

		int end = start + CHARS_CONSOLE_TEXT_SNIPPET_SIZE_MAX;

		end = consoleText.lastIndexOf("\n", end);

		return getConsoleTextSnippet(consoleText, false, start, end);
	}

	private static final String _TOKEN_FORMATTING_ISSUES = "formatting issues:";

	private static final String _TOKEN_SOURCE_FORMAT =
		"at com.liferay.source.formatter";

}