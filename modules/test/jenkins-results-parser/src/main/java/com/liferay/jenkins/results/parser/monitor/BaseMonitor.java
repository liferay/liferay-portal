/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Node;

/**
 * @author Brittney Nguyen
 */
public abstract class BaseMonitor implements Monitor {

	@Override
	public String getId() {
		return _monitorConfig.getId();
	}

	@Override
	public List<String> getLinkURLs() {
		if (!_linkURLs.isEmpty()) {
			return _linkURLs;
		}

		String defaultLinkURL = getDefaultLinkURL();

		if (JenkinsResultsParserUtil.isNullOrEmpty(defaultLinkURL)) {
			return Collections.emptyList();
		}

		return Collections.singletonList(defaultLinkURL);
	}

	@Override
	public MonitorConfig getMonitorConfig() {
		return _monitorConfig;
	}

	@Override
	public void prepareCycle() {
	}

	protected BaseMonitor(MonitorConfig monitorConfig) {
		_monitorConfig = monitorConfig;

		_linkURLs = _getLinkURLs(monitorConfig.getParameters());
	}

	protected int getAttemptTimeoutMillis(int maxRetries) {
		long timeoutMillis = _getTimeoutMillis();

		return (int)((timeoutMillis - (timeoutMillis / 10)) /
			(getReadsCount() * (maxRetries + 1)));
	}

	protected boolean getBooleanValue(
		String category, boolean defaultValue, String name,
		Map<String, String> values) {

		String value = values.get(name);

		if (JenkinsResultsParserUtil.isNullOrEmpty(value)) {
			return defaultValue;
		}

		if (!value.equals("false") && !value.equals("true")) {
			throw new IllegalArgumentException(
				getInvalidValueMessage(category, name, value));
		}

		return Boolean.parseBoolean(value);
	}

	protected CronSchedule getCronSchedule(String jobName, String masterName)
		throws Exception {

		if (_cronScheduleLoaded) {
			return _cronSchedule;
		}

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(masterName);

		Document document = masterResourceReader.getJobConfigDocument(
			jobName,
			getAttemptTimeoutMillis(MasterResourceReader.RETRIES_SIZE_MAX));

		Node specNode = Dom4JUtil.getNodeByXPath(
			document, "//hudson.triggers.TimerTrigger/spec");

		if (specNode != null) {
			String spec = specNode.getText();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(spec)) {
				_cronSchedule = new CronSchedule(spec);
			}
		}

		_cronScheduleLoaded = true;

		return _cronSchedule;
	}

	protected String getDefaultLinkURL() {
		return null;
	}

	protected String getInvalidValueMessage(
		String category, String name, String value) {

		return JenkinsResultsParserUtil.combine(
			"Invalid ", name, " for ", _getKey(category, name), ": ", value);
	}

	protected long getLongValue(
		String category, long defaultValue, String name,
		Map<String, String> values) {

		String value = values.get(name);

		if (JenkinsResultsParserUtil.isNullOrEmpty(value)) {
			return defaultValue;
		}

		long longValue = 0;

		try {
			longValue = Long.parseLong(value);
		}
		catch (NumberFormatException numberFormatException) {
			throw new IllegalArgumentException(
				getInvalidValueMessage(category, name, value),
				numberFormatException);
		}

		if (longValue < 0) {
			throw new IllegalArgumentException(
				getInvalidValueMessage(category, name, value));
		}

		return longValue;
	}

	protected long getOverdueDeadlineTimestamp(
		CronSchedule cronSchedule, long currentTimeMillis,
		long overdueGraceSeconds) {

		long toleranceSeconds =
			overdueGraceSeconds + cronSchedule.getHashSpanSeconds();

		return cronSchedule.getPreviousFireTimestamp(
			currentTimeMillis - (toleranceSeconds * 1000));
	}

	protected long getOverdueGraceSeconds(
		CronSchedule cronSchedule, long currentTimeMillis,
		Map<String, String> thresholds) {

		return getOverdueGraceSeconds(
			cronSchedule.getPeriodSeconds(currentTimeMillis), thresholds);
	}

	protected long getOverdueGraceSeconds(
		long cadenceSeconds, Map<String, String> thresholds) {

		return getLongValue(
			"threshold",
			Math.max(_SECONDS_OVERDUE_GRACE_MINIMUM, cadenceSeconds / 4),
			"overdue.grace", thresholds);
	}

	protected int getReadsCount() {
		return 2;
	}

	protected String getRemoteJobURL(
		JenkinsMaster jenkinsMaster, String jobName) {

		String jobURL = JenkinsResultsParserUtil.combine(
			jenkinsMaster.getRemoteURL(), "job/", jobName);

		try {
			return JenkinsResultsParserUtil.encode(jobURL);
		}
		catch (MalformedURLException | URISyntaxException exception) {
			throw new RuntimeException(
				"Unable to encode job URL " + jobURL, exception);
		}
	}

	protected String getRequiredParameter(
		String name, Map<String, String> parameters) {

		String value = parameters.get(name);

		if (JenkinsResultsParserUtil.isNullOrEmpty(value)) {
			throw new IllegalArgumentException(
				"Missing required property " + _getKey("parameter", name));
		}

		return value;
	}

	protected String getRequiredURLParameter(
		String name, Map<String, String> parameters, String... urlPrefixes) {

		for (String urlPrefix : urlPrefixes) {
			if (!urlPrefix.contains("://")) {
				throw new IllegalArgumentException(
					"Invalid URL prefix: " + urlPrefix);
			}
		}

		return _getURL(
			name, getRequiredParameter(name, parameters), urlPrefixes);
	}

	private String _getKey(String category, String name) {
		return JenkinsResultsParserUtil.combine(
			"monitor[", _monitorConfig.getId(), "].", category, "[", name, "]");
	}

	private List<String> _getLinkURLs(Map<String, String> parameters) {
		String links = parameters.get("links");

		if (JenkinsResultsParserUtil.isNullOrEmpty(links)) {
			return Collections.emptyList();
		}

		List<String> linkURLs = new ArrayList<>();

		for (String linkURL : links.split(",")) {
			linkURLs.add(
				_getURL("links", linkURL.trim(), "http://", "https://"));
		}

		return Collections.unmodifiableList(linkURLs);
	}

	private long _getTimeoutMillis() {
		long timeoutSeconds = _monitorConfig.getTimeoutSeconds();

		if (timeoutSeconds <= 0) {
			timeoutSeconds = MonitorConfig.SECONDS_TIMEOUT_DEFAULT;
		}

		return timeoutSeconds * 1000;
	}

	private String _getURL(String name, String url, String... urlPrefixes) {
		Matcher matcher = _userInfoPattern.matcher(url);

		if (matcher.matches()) {
			throw new IllegalArgumentException(
				getInvalidValueMessage("parameter", name, "[REDACTED]"));
		}

		for (String urlPrefix : urlPrefixes) {
			if (url.startsWith(urlPrefix) &&
				(url.length() > urlPrefix.length())) {

				return url;
			}
		}

		throw new IllegalArgumentException(
			getInvalidValueMessage("parameter", name, url));
	}

	private static final long _SECONDS_OVERDUE_GRACE_MINIMUM = 30 * 60;

	private static final Pattern _userInfoPattern = Pattern.compile(
		"(//|[^/?#]*://)?[^/?#]*@.*");

	private CronSchedule _cronSchedule;
	private boolean _cronScheduleLoaded;
	private final List<String> _linkURLs;
	private final MonitorConfig _monitorConfig;

}