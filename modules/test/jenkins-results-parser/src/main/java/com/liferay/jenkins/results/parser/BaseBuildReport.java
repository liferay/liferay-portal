/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseBuildReport implements BuildReport {

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}

		if (!(object instanceof BuildReport)) {
			return false;
		}

		BuildReport buildReport = (BuildReport)object;

		return Objects.equals(buildReport.getBuildURL(), getBuildURL());
	}

	@Override
	public int getBuildNumber() {
		Matcher matcher = _buildURLPattern.matcher(
			String.valueOf(getBuildURL()));

		if (!matcher.find()) {
			throw new RuntimeException("Invalid Build URL: " + getBuildURL());
		}

		return Integer.parseInt(matcher.group("buildNumber"));
	}

	@Override
	public Map<String, String> getBuildParameters() {
		Map<String, String> buildParameters = new HashMap<>();

		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if ((buildReportJSONObject == null) ||
			!buildReportJSONObject.has("buildParameters")) {

			return buildParameters;
		}

		JSONObject buildParametersJSONObject =
			buildReportJSONObject.getJSONObject("buildParameters");

		for (String key : buildParametersJSONObject.keySet()) {
			buildParameters.put(key, buildParametersJSONObject.getString(key));
		}

		return buildParameters;
	}

	@Override
	public URL getBuildURL() {
		return _buildURL;
	}

	@Override
	public long getDuration() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return 0L;
		}

		return buildReportJSONObject.getLong("duration");
	}

	@Override
	public String getFailureMessage() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return null;
		}

		return buildReportJSONObject.optString("failureMessage");
	}

	@Override
	public List<FailureReport> getFailureReports() {
		List<FailureReport> failureReports = new ArrayList<>();

		if (!isFailing()) {
			return failureReports;
		}

		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return failureReports;
		}

		JSONArray failureReportsJSONArray = buildReportJSONObject.optJSONArray(
			"failureReports");

		if (failureReportsJSONArray == null) {
			return failureReports;
		}

		for (int i = 0; i < failureReportsJSONArray.length(); i++) {
			JSONObject failureReportJSONObject =
				failureReportsJSONArray.getJSONObject(i);

			failureReports.add(
				FailureReportFactory.newFailureReport(
					this, failureReportJSONObject.getString("message"), null));
		}

		return failureReports;
	}

	@Override
	public synchronized JenkinsMaster getJenkinsMaster() {
		if (_jenkinsMaster != null) {
			return _jenkinsMaster;
		}

		Matcher matcher = _buildURLPattern.matcher(
			String.valueOf(getBuildURL()));

		if (!matcher.find()) {
			throw new RuntimeException("Invalid Build URL: " + getBuildURL());
		}

		_jenkinsMaster = JenkinsMaster.getInstance(
			matcher.group("masterHostname"));

		return _jenkinsMaster;
	}

	@Override
	public String getJobName() {
		Matcher matcher = _buildURLPattern.matcher(
			String.valueOf(getBuildURL()));

		if (!matcher.find()) {
			throw new RuntimeException("Invalid Build URL: " + getBuildURL());
		}

		return matcher.group("jobName");
	}

	@Override
	public String getResult() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return null;
		}

		return buildReportJSONObject.optString("result");
	}

	@Override
	public Date getStartDate() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return null;
		}

		return new Date(buildReportJSONObject.getLong("startTime"));
	}

	@Override
	public StopWatchRecordsGroup getStopWatchRecordsGroup() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return null;
		}

		return new StopWatchRecordsGroup(buildReportJSONObject);
	}

	@Override
	public synchronized URL getTestrayAttachmentURLBySuffix(String suffix) {
		URL url = _testrayAttachmentURLsBySuffix.get(suffix);

		if (url != null) {
			return url;
		}

		URL matchedURL = null;

		for (URL testrayAttachmentURL : getTestrayAttachmentURLs()) {
			String testrayAttachmentURLString = String.valueOf(
				testrayAttachmentURL);

			if (testrayAttachmentURLString.endsWith(suffix)) {
				matchedURL = testrayAttachmentURL;

				break;
			}
		}

		_testrayAttachmentURLsBySuffix.put(suffix, matchedURL);

		return matchedURL;
	}

	@Override
	public synchronized List<URL> getTestrayAttachmentURLs() {
		if (_testrayAttachmentURLs != null) {
			return _testrayAttachmentURLs;
		}

		List<URL> testrayAttachmentURLs = new ArrayList<>();

		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			_testrayAttachmentURLs = testrayAttachmentURLs;

			return _testrayAttachmentURLs;
		}

		JSONArray testrayAttachmentURLsJSONArray =
			buildReportJSONObject.optJSONArray("testrayAttachmentURLs");

		if (testrayAttachmentURLsJSONArray == null) {
			_testrayAttachmentURLs = testrayAttachmentURLs;

			return _testrayAttachmentURLs;
		}

		for (int i = 0; i < testrayAttachmentURLsJSONArray.length(); i++) {
			try {
				testrayAttachmentURLs.add(
					new URL(testrayAttachmentURLsJSONArray.getString(i)));
			}
			catch (MalformedURLException malformedURLException) {
				throw new RuntimeException(malformedURLException);
			}
		}

		_testrayAttachmentURLs = testrayAttachmentURLs;

		return _testrayAttachmentURLs;
	}

	@Override
	public int hashCode() {
		String buildURL = String.valueOf(getBuildURL());

		return buildURL.hashCode();
	}

	@Override
	public boolean isFailing() {
		String result = getResult();

		if (result.equals("FAILURE") || result.equals("REGRESSION") ||
			result.equals("UNSTABLE")) {

			return true;
		}

		return false;
	}

	protected BaseBuildReport(String buildURLString) {
		Matcher matcher = _buildURLPattern.matcher(buildURLString);

		if (!matcher.find()) {
			throw new RuntimeException("Invalid Build URL: " + buildURLString);
		}

		try {
			_buildURL = new URL(
				JenkinsResultsParserUtil.combine(
					"https://", matcher.group("masterHostname"),
					".liferay.com/job/", matcher.group("jobName"), "/",
					matcher.group("buildNumber")));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	protected synchronized void clearTestrayAttachmentURLCaches() {
		_testrayAttachmentURLs = null;

		_testrayAttachmentURLsBySuffix.clear();
	}

	private static final Pattern _buildURLPattern = Pattern.compile(
		"(?<jobURL>https?://(?<masterHostname>test-\\d+-\\d+(-aws)?)" +
			"(\\.liferay\\.com)?/job/(?<jobName>[^/]+))" +
				"(/AXIS_VARIABLE=(?<axisVariable>\\d+))?/(?<buildNumber>\\d+)");

	private final URL _buildURL;
	private JenkinsMaster _jenkinsMaster;
	private List<URL> _testrayAttachmentURLs;
	private final Map<String, URL> _testrayAttachmentURLsBySuffix =
		new HashMap<>();

}