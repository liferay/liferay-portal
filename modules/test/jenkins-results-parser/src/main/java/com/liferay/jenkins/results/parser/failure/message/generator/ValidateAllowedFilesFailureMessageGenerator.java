/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.failure.message.generator;

/**
 * @author Michael Hashimoto
 */
public class ValidateAllowedFilesFailureMessageGenerator
	extends BaseFailureMessageGenerator {

	@Override
	public String getMessage(String consoleText) {
		int start = consoleText.lastIndexOf(_TOKEN_FILES_NOT_ALLOWED);

		if (start == -1) {
			return null;
		}

		int end = consoleText.indexOf("\n", start);

		while ((end != -1) && consoleText.startsWith(">", end + 1)) {
			end = consoleText.indexOf("\n", end + 1);
		}

		if (end == -1) {
			end = consoleText.length();
		}

		return getConsoleTextSnippet(consoleText, false, start, end);
	}

	private static final String _TOKEN_FILES_NOT_ALLOWED =
		"FAILURE: These files are not allowed in ";

}