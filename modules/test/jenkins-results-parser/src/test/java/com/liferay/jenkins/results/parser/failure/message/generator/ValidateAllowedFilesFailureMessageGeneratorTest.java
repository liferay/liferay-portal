/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.failure.message.generator;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class ValidateAllowedFilesFailureMessageGeneratorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetMessage() {
		String failureLine = _getFailureLine();
		String filePathLine1 = "> " + RandomTestUtil.randomString();
		String filePathLine2 = "> " + RandomTestUtil.randomString();
		String trailingLine = RandomTestUtil.randomString();

		Assert.assertEquals(
			JenkinsResultsParserUtil.combine(
				failureLine, "\n", filePathLine1, "\n", filePathLine2),
			_validateAllowedFilesFailureMessageGenerator.getMessage(
				JenkinsResultsParserUtil.combine(
					RandomTestUtil.randomString(), "\n", failureLine, "\n",
					filePathLine1, "\n", filePathLine2, "\n", trailingLine,
					"\n")));
	}

	@Test
	public void testGetMessageAtEndOfConsoleText() {
		String failureLine = _getFailureLine();
		String filePathLine = "> " + RandomTestUtil.randomString();

		Assert.assertEquals(
			JenkinsResultsParserUtil.combine(failureLine, "\n", filePathLine),
			_validateAllowedFilesFailureMessageGenerator.getMessage(
				JenkinsResultsParserUtil.combine(
					RandomTestUtil.randomString(), "\n", failureLine, "\n",
					filePathLine)));

		Assert.assertEquals(
			failureLine,
			_validateAllowedFilesFailureMessageGenerator.getMessage(
				JenkinsResultsParserUtil.combine(
					RandomTestUtil.randomString(), "\n", failureLine)));
	}

	@Test
	public void testGetMessageLastFailure() {
		String firstFailureLine = _getFailureLine();
		String firstFilePathLine = "> " + RandomTestUtil.randomString();
		String lastFailureLine = _getFailureLine();
		String lastFilePathLine = "> " + RandomTestUtil.randomString();

		String message =
			_validateAllowedFilesFailureMessageGenerator.getMessage(
				JenkinsResultsParserUtil.combine(
					firstFailureLine, "\n", firstFilePathLine, "\n",
					RandomTestUtil.randomString(), "\n", lastFailureLine, "\n",
					lastFilePathLine, "\n"));

		Assert.assertEquals(
			JenkinsResultsParserUtil.combine(
				lastFailureLine, "\n", lastFilePathLine),
			message);
		Assert.assertFalse(message.contains(firstFilePathLine));
	}

	@Test
	public void testGetMessageNoFailure() {
		Assert.assertNull(
			_validateAllowedFilesFailureMessageGenerator.getMessage(""));
		Assert.assertNull(
			_validateAllowedFilesFailureMessageGenerator.getMessage(
				JenkinsResultsParserUtil.combine(
					RandomTestUtil.randomString(), "\n> ",
					RandomTestUtil.randomString(), "\n")));
	}

	private String _getFailureLine() {
		return JenkinsResultsParserUtil.combine(
			"FAILURE: These files are not allowed in ",
			RandomTestUtil.randomString(), ":");
	}

	private final ValidateAllowedFilesFailureMessageGenerator
		_validateAllowedFilesFailureMessageGenerator =
			new ValidateAllowedFilesFailureMessageGenerator();

}