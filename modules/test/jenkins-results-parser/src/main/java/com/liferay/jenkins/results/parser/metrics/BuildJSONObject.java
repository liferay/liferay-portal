/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.metrics;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class BuildJSONObject extends JSONObject {

	public BuildJSONObject(JSONObject jsonObject) {
		this(jsonObject.toString());
	}

	public BuildJSONObject(String source) {
		super(source);

		_topLevelBuildURL = _getTopLevelBuildURL();
	}

	public long getDuration() {
		return optLong("duration");
	}

	public String getJobName() {
		return _getJobName(getURL());
	}

	public Map<String, String> getParameters() {
		Map<String, String> parameters = new HashMap<>();

		JSONArray parametersJSONArray = optJSONArray("parameters");

		for (int i = 0; i < parametersJSONArray.length(); i++) {
			JSONObject jsonObject = parametersJSONArray.getJSONObject(i);

			parameters.put(
				jsonObject.optString("name"), jsonObject.optString("value"));
		}

		return parameters;
	}

	public long getQueueDuration() {
		return optLong("queueDuration");
	}

	public String getStartDateString() {
		if (_startDateString == null) {
			LocalDate startDate = JenkinsResultsParserUtil.getLocalDate(
				getStartTime());

			_startDateString = startDate.format(
				DateTimeFormatter.ofPattern("yyyyMMdd"));
		}

		return _startDateString;
	}

	public long getStartTime() {
		String jobName = getJobName();

		if (jobName.equals("maintenance-daily")) {
			return optLong("startTime") + optLong("queueDuration");
		}

		return optLong("startTime");
	}

	public String getTestrayBuildURL() {
		return optString("testrayBuildURL");
	}

	public String getTopLevelBuildURL() {
		return _topLevelBuildURL;
	}

	public String getURL() {
		return optString("url");
	}

	public boolean isTopLevelBuild() {
		String url = getURL();

		if (url.contains("-batch") || url.contains("-downstream") ||
			url.contains("maintenance") || url.contains("-validation")) {

			return false;
		}

		return true;
	}

	private String _getJobName(String buildURL) {
		if (buildURL == null) {
			return null;
		}

		Matcher matcher = _buildURLPattern.matcher(buildURL);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("jobName");
	}

	private String _getTopLevelBuildURL() {
		if (isTopLevelBuild()) {
			return null;
		}

		Map<String, String> parameters = getParameters();

		if (!parameters.containsKey("DIST_PATH")) {
			return null;
		}

		Matcher distPathMatcher = _distPathPattern.matcher(
			parameters.get("DIST_PATH"));

		if (!distPathMatcher.find()) {
			return null;
		}

		return JenkinsResultsParserUtil.combine(
			"https://", distPathMatcher.group("masterName"),
			".liferay.com/job/", distPathMatcher.group("jobName"), "/",
			distPathMatcher.group("buildNumber"), "/");
	}

	private static final Pattern _buildURLPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"(?<jobURL>https?://(?<masterHostname>",
			"(?<cohortName>test-\\d+)-\\d+)(\\.liferay\\.com)?/job/",
			"(?<jobName>[^/]+)/(.*/)?)(?<buildNumber>\\d+)/?"));
	private static final Pattern _distPathPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"[\\w\\/]+(?<masterName>test-[\\d]+-[\\d]+)\\/",
			"(?<jobName>[\\w\\-\\(\\)]+)\\/(?<buildNumber>[\\d]+)"));

	private String _startDateString;
	private final String _topLevelBuildURL;

}