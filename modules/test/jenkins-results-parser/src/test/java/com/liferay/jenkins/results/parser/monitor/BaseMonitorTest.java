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
		BaseMonitor baseMonitor = _newBaseMonitor(60);

		testEquals(20000, baseMonitor.getAttemptTimeoutMillis());
	}

	@Test
	public void testGetAttemptTimeoutMillisMaximum() {
		BaseMonitor baseMonitor = _newBaseMonitor(Long.MAX_VALUE);

		testEquals(715827666, baseMonitor.getAttemptTimeoutMillis());
	}

	@Test
	public void testGetAttemptTimeoutMillisNonPositiveTimeout() {
		BaseMonitor baseMonitor = _newBaseMonitor(0);

		testEquals(20000, baseMonitor.getAttemptTimeoutMillis());
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

	@Test
	public void testGetSingleAttemptTimeoutMillis() {
		BaseMonitor baseMonitor = _newBaseMonitor(10);

		testEquals(4500, baseMonitor.getSingleAttemptTimeoutMillis());
	}

	@Test
	public void testGetSingleAttemptTimeoutMillisMaximum() {
		BaseMonitor baseMonitor = _newBaseMonitor(Long.MAX_VALUE);

		testEquals(966367350, baseMonitor.getSingleAttemptTimeoutMillis());
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