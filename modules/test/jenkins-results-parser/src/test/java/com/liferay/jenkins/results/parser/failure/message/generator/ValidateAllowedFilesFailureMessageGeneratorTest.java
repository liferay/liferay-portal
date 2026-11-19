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
		String failureMessage = JenkinsResultsParserUtil.combine(
			_getFailureLine(), "\n> ", RandomTestUtil.randomString(), "\n> ",
			RandomTestUtil.randomString());

		Assert.assertEquals(
			failureMessage,
			_validateAllowedFilesFailureMessageGenerator.getMessage(
				JenkinsResultsParserUtil.combine(
					RandomTestUtil.randomString(), "\n", failureMessage, "\n",
					RandomTestUtil.randomString(), "\n")));
	}

	@Test
	public void testGetMessageAtEndOfConsoleText() {
		String failureLine = _getFailureLine();

		Assert.assertEquals(
			failureLine,
			_validateAllowedFilesFailureMessageGenerator.getMessage(
				JenkinsResultsParserUtil.combine(
					RandomTestUtil.randomString(), "\n", failureLine)));

		String failureMessage = JenkinsResultsParserUtil.combine(
			_getFailureLine(), "\n> ", RandomTestUtil.randomString(), "\n> ",
			RandomTestUtil.randomString());

		Assert.assertEquals(
			failureMessage,
			_validateAllowedFilesFailureMessageGenerator.getMessage(
				JenkinsResultsParserUtil.combine(
					RandomTestUtil.randomString(), "\n", failureMessage)));
	}

	@Test
	public void testGetMessageLastFailure() {
		String failureMessage1 = JenkinsResultsParserUtil.combine(
			_getFailureLine(), "\n> ", RandomTestUtil.randomString());
		String failureMessage2 = JenkinsResultsParserUtil.combine(
			_getFailureLine(), "\n> ", RandomTestUtil.randomString());

		String multipleFailureMessage =
			_validateAllowedFilesFailureMessageGenerator.getMessage(
				JenkinsResultsParserUtil.combine(
					RandomTestUtil.randomString(), "\n", failureMessage1, "\n",
					RandomTestUtil.randomString(), "\n", failureMessage2, "\n",
					RandomTestUtil.randomString()));

		Assert.assertEquals(failureMessage2, multipleFailureMessage);

		Assert.assertFalse(multipleFailureMessage.contains(failureMessage1));
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