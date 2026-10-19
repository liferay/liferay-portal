/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsMasterTestUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

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
	public void testGetAttemptTimeoutMillisReadsCount() {
		BaseMonitor baseMonitor = Mockito.spy(_newBaseMonitor(60));

		Mockito.doReturn(
			3
		).when(
			baseMonitor
		).getReadsCount();

		testEquals(18000, baseMonitor.getAttemptTimeoutMillis(0));
		testEquals(9000, baseMonitor.getAttemptTimeoutMillis(1));
	}

	@Test
	public void testGetAttemptTimeoutMillisWithinBudget() {
		_testGetAttemptTimeoutMillisWithinBudget(0, 10);
		_testGetAttemptTimeoutMillisWithinBudget(0, 60);
		_testGetAttemptTimeoutMillisWithinBudget(1, 10);
		_testGetAttemptTimeoutMillisWithinBudget(1, 60);
	}

	@Test
	public void testGetBooleanValue() {
		BaseMonitor baseMonitor = _newBaseMonitor(60);

		testEquals(
			false,
			baseMonitor.getBooleanValue(
				"parameter", true, "expected.green",
				Collections.singletonMap("expected.green", "false")));
		testEquals(
			true,
			baseMonitor.getBooleanValue(
				"parameter", true, "expected.green", Collections.emptyMap()));

		try {
			baseMonitor.getBooleanValue(
				"parameter", true, "expected.green",
				Collections.singletonMap("expected.green", "yes"));

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	@Test
	public void testGetLinkURLs() {
		String defaultLinkURL = "https://" + RandomTestUtil.randomString();

		testEquals(
			Collections.singletonList(defaultLinkURL),
			_getLinkURLs(defaultLinkURL, ""));
		testEquals(
			Collections.singletonList(defaultLinkURL),
			_getLinkURLs(defaultLinkURL, null));

		String httpURL = "http://" + RandomTestUtil.randomString();
		String httpsURL = "https://" + RandomTestUtil.randomString();

		testEquals(
			Arrays.asList(httpsURL, httpURL),
			_getLinkURLs(defaultLinkURL, httpsURL + " , " + httpURL));

		testEquals(Collections.emptyList(), _getLinkURLs(null, null));
	}

	@Test
	public void testGetLinkURLsInvalid() {
		String httpsURL = "https://" + RandomTestUtil.randomString();

		_testGetLinkURLsInvalid("", httpsURL + ",," + httpsURL);

		_testGetLinkURLsInvalid("https://", "https://");

		String fileURL = "file:///" + RandomTestUtil.randomString();

		_testGetLinkURLsInvalid(fileURL, fileURL);

		String randomString = RandomTestUtil.randomString();

		_testGetLinkURLsInvalid(randomString, randomString);

		String password = RandomTestUtil.randomString();

		String message = _getLinkURLsFailureMessage(
			JenkinsResultsParserUtil.combine(
				httpsURL, ",https://", RandomTestUtil.randomString(), ":",
				password, "@", RandomTestUtil.randomString()));

		Assert.assertFalse(message.contains(password));
		Assert.assertTrue(message.contains("[REDACTED]"));
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
	public void testGetRemoteJobURL() {
		String masterName = RandomTestUtil.randomString();

		JenkinsMaster jenkinsMaster = JenkinsMasterTestUtil.getJenkinsMaster(
			masterName, "https://" + masterName + ".liferay.com/",
			"http://" + masterName);

		BaseMonitor baseMonitor = _newBaseMonitor(60);

		String jobName = RandomTestUtil.randomString();

		testEquals(
			JenkinsResultsParserUtil.combine(
				"https://", masterName, ".liferay.com/job/", jobName, "%20(",
				jobName, ")"),
			baseMonitor.getRemoteJobURL(
				jenkinsMaster, jobName + " (" + jobName + ")"));
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

	private List<String> _getLinkURLs(String defaultLinkURL, String links) {
		BaseMonitor baseMonitor = _newBaseMonitor(defaultLinkURL, links);

		return baseMonitor.getLinkURLs();
	}

	private String _getLinkURLsFailureMessage(String links) {
		try {
			_newBaseMonitor(null, links);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			return illegalArgumentException.getMessage();
		}

		return null;
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

			Assert.fail();
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

	private BaseMonitor _newBaseMonitor(String defaultLinkURL, String links) {
		Map<String, String> parameters = null;

		if (links != null) {
			parameters = Collections.singletonMap("links", links);
		}

		return new BaseMonitor(
			new MonitorConfig(
				_MONITOR_ID, 0, parameters, MonitorConfig.Severity.MEDIUM, null,
				60, RandomTestUtil.randomString())) {

			@Override
			public MonitorResult execute() {
				return null;
			}

			@Override
			protected String getDefaultLinkURL() {
				return defaultLinkURL;
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

	private void _testGetLinkURLsInvalid(String entry, String links) {
		testEquals(
			JenkinsResultsParserUtil.combine(
				"Invalid links for monitor[", _MONITOR_ID,
				"].parameter[links]: ", entry),
			_getLinkURLsFailureMessage(links));
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

	private static final String _MONITOR_ID = RandomTestUtil.randomString();

}