/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class BaseMonitorTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetAttemptTimeoutMillis() {
		_testGetAttemptTimeoutMillis(13500, 1, 60);
		_testGetAttemptTimeoutMillis(2250, 1, 10);
		_testGetAttemptTimeoutMillis(27000, 0, 60);
		_testGetAttemptTimeoutMillis(4500, 0, 10);
	}

	@Test
	public void testGetAttemptTimeoutMillisMaximum() {
		_testGetAttemptTimeoutMillis(483183675, 1, Long.MAX_VALUE);
		_testGetAttemptTimeoutMillis(966367350, 0, Long.MAX_VALUE);
	}

	@Test
	public void testGetAttemptTimeoutMillisNonPositiveTimeout() {
		_testGetAttemptTimeoutMillis(13500, 1, 0);
		_testGetAttemptTimeoutMillis(27000, 0, 0);
	}

	@Test
	public void testGetAttemptTimeoutMillisWithinBudget() {
		_testGetAttemptTimeoutMillisWithinBudget(0, 10);
		_testGetAttemptTimeoutMillisWithinBudget(0, 60);
		_testGetAttemptTimeoutMillisWithinBudget(1, 10);
		_testGetAttemptTimeoutMillisWithinBudget(1, 60);
	}

	@Test
	public void testGetOverdueGraceSeconds() {
		BaseMonitor baseMonitor = _newBaseMonitor(60);

		testEquals(
			1800L,
			baseMonitor.getOverdueGraceSeconds(
				600, Collections.<String, String>emptyMap()));
		testEquals(
			3600L,
			baseMonitor.getOverdueGraceSeconds(
				14400, Collections.<String, String>emptyMap()));
		testEquals(
			60L,
			baseMonitor.getOverdueGraceSeconds(
				600, Collections.singletonMap("overdue.grace", "60")));
	}

	@Test
	public void testGetRequiredURLParameter() {
		String fileURL = "file:///" + RandomTestUtil.randomString();

		testEquals(fileURL, _getRequiredURLParameter(fileURL, "file:///"));

		String httpsURL = "https://" + RandomTestUtil.randomString();

		testEquals(
			httpsURL,
			_getRequiredURLParameter(httpsURL, "http://", "https://"));

		_getRequiredURLParameterFailureMessage(
			RandomTestUtil.randomString(), "file:///", "http://", "https://");
		_getRequiredURLParameterFailureMessage(fileURL, "http://", "https://");
		_getRequiredURLParameterFailureMessage(httpsURL, "file:///");
	}

	@Test
	public void testGetRequiredURLParameterHost() {
		_getRequiredURLParameterFailureMessage(
			JenkinsResultsParserUtil.combine(
				"file://", RandomTestUtil.randomString(), "/",
				RandomTestUtil.randomString()),
			"file:///");
	}

	@Test
	public void testGetRequiredURLParameterInvalidPrefix() {
		_getRequiredURLParameterFailureMessage(
			"file:///" + RandomTestUtil.randomString(), "file:");
		_getRequiredURLParameterFailureMessage(
			"https://" + RandomTestUtil.randomString(), "https");
	}

	@Test
	public void testGetRequiredURLParameterPrefixOnly() {
		_getRequiredURLParameterFailureMessage("file:///", "file:///");
		_getRequiredURLParameterFailureMessage(
			"https://", "http://", "https://");
	}

	@Test
	public void testGetRequiredURLParameterUserInfo() {
		_testGetRequiredURLParameterUserInfo("file://");
		_testGetRequiredURLParameterUserInfo("https://");
	}

	private String _getRequiredURLParameter(String url, String... urlPrefixes) {
		BaseMonitor baseMonitor = _newBaseMonitor(60);

		return baseMonitor.getRequiredURLParameter(
			"url", Collections.singletonMap("url", url), urlPrefixes);
	}

	private String _getRequiredURLParameterFailureMessage(
		String url, String... urlPrefixes) {

		try {
			_getRequiredURLParameter(url, urlPrefixes);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
			return illegalArgumentException.getMessage();
		}

		return null;
	}

	private BaseMonitor _newBaseMonitor(long timeoutSeconds) {
		return new BaseMonitor(
			new MonitorConfig(
				RandomTestUtil.randomString(), 0, null,
				MonitorConfig.Severity.MEDIUM, null, timeoutSeconds,
				RandomTestUtil.randomString())) {

			@Override
			public MonitorResult execute() {
				return null;
			}

		};
	}

	private void _testGetAttemptTimeoutMillis(
		int expected, int maxRetries, long timeoutSeconds) {

		BaseMonitor baseMonitor = _newBaseMonitor(timeoutSeconds);

		testEquals(expected, baseMonitor.getAttemptTimeoutMillis(maxRetries));
	}

	private void _testGetAttemptTimeoutMillisWithinBudget(
		int maxRetries, long timeoutSeconds) {

		BaseMonitor baseMonitor = _newBaseMonitor(timeoutSeconds);

		long worstCaseMillis =
			(long)baseMonitor.getAttemptTimeoutMillis(maxRetries) * 2 *
				(maxRetries + 1);

		Assert.assertTrue(worstCaseMillis < (timeoutSeconds * 1000));
	}

	private void _testGetRequiredURLParameterUserInfo(String urlPrefix) {
		String password = RandomTestUtil.randomString();

		String message = _getRequiredURLParameterFailureMessage(
			JenkinsResultsParserUtil.combine(
				urlPrefix, RandomTestUtil.randomString(), ":", password, "@",
				RandomTestUtil.randomString()),
			"file:///", "http://", "https://");

		Assert.assertFalse(message.contains(password));
		Assert.assertTrue(message.contains("[REDACTED]"));
	}

}