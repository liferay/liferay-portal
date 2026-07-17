/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JobReport {

	public static JobReport getInstance(String jenkinsJobURLString) {
		try {
			return getInstance(new URL(jenkinsJobURLString));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	public static JobReport getInstance(URL jenkinsJobURL) {
		return _jobReports.computeIfAbsent(
			JenkinsResultsParserUtil.getRemoteURL(jenkinsJobURL.toString()),
			key -> new JobReport(jenkinsJobURL));
	}

	public JenkinsMaster getJenkinsMaster() {
		if (_jenkinsMaster != null) {
			return _jenkinsMaster;
		}

		Matcher matcher = _jobURLPattern.matcher(String.valueOf(getJobURL()));

		if (!matcher.find()) {
			throw new RuntimeException("Invalid Job URL");
		}

		_jenkinsMaster = JenkinsMaster.getInstance(
			matcher.group("masterHostname"));

		return _jenkinsMaster;
	}

	public String getJobName() {
		Matcher matcher = _jobURLPattern.matcher(String.valueOf(getJobURL()));

		if (!matcher.find()) {
			throw new RuntimeException("Invalid Job URL");
		}

		return matcher.group("jobName");
	}

	public URL getJobURL() {
		return _jobURL;
	}

	public List<TopLevelBuildReport> getTopLevelBuildReports(
		int maxBuildCount) {

		if (_topLevelBuildReports != null) {
			return _topLevelBuildReports;
		}

		_topLevelBuildReports = new ArrayList<>();

		try {
			JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.combine(
					String.valueOf(getJobURL()), "/api/json?tree=",
					"builds[actions[parameters[name,value]],*]"));

			if (jsonObject == null) {
				return _topLevelBuildReports;
			}

			JSONArray buildsJSONArray = jsonObject.optJSONArray("builds");

			if (buildsJSONArray == JSONObject.NULL) {
				return _topLevelBuildReports;
			}

			for (int i = 0; i < buildsJSONArray.length(); i++) {
				if (_topLevelBuildReports.size() >= maxBuildCount) {
					break;
				}

				JSONObject buildJSONObject = buildsJSONArray.getJSONObject(i);

				if (buildJSONObject == JSONObject.NULL) {
					continue;
				}

				_topLevelBuildReports.add(
					BuildReportFactory.newTopLevelBuildReport(
						buildJSONObject, this));
			}

			return _topLevelBuildReports;
		}
		catch (IOException ioException) {
			return _topLevelBuildReports;
		}
	}

	private JobReport(URL jobURL) {
		Matcher matcher = _jobURLPattern.matcher(String.valueOf(jobURL));

		if (!matcher.find()) {
			throw new RuntimeException("Invalid Job URL");
		}

		try {
			_jobURL = new URL(
				JenkinsResultsParserUtil.combine(
					"https://", matcher.group("masterHostname"),
					".liferay.com/job/", matcher.group("jobName")));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private static final Map<String, JobReport> _jobReports = new HashMap<>();
	private static final Pattern _jobURLPattern = Pattern.compile(
		"https?://(?<masterHostname>test-\\d+-\\d+)(\\.liferay\\.com)?/job/" +
			"(?<jobName>[^/]+)/?");

	private JenkinsMaster _jenkinsMaster;
	private final URL _jobURL;
	private List<TopLevelBuildReport> _topLevelBuildReports;

}