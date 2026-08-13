/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

import org.json.JSONObject;

/**
 * @author Kevin Yen
 */
public class JenkinsStopBuildUtil {

	public static AbortResult abortBuild(String buildURL) throws Exception {
		String normalizedBuildURL = JenkinsResultsParserUtil.fixURL(
			JenkinsResultsParserUtil.getLocalURL(buildURL));

		if (!_isBuilding(normalizedBuildURL)) {
			return AbortResult.ALREADY_FINISHED;
		}

		int responseCode = _post(normalizedBuildURL + "/stop");

		if (responseCode >= 400) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to stop ", normalizedBuildURL,
					", received response ", String.valueOf(responseCode)));
		}

		for (int i = 0; i < _ABORT_VERIFICATION_RETRIES; i++) {
			JenkinsResultsParserUtil.sleep(_ABORT_VERIFICATION_PERIOD);

			if (!_isBuilding(normalizedBuildURL)) {
				return AbortResult.STOPPED;
			}
		}

		long abortVerificationDuration =
			_ABORT_VERIFICATION_PERIOD * _ABORT_VERIFICATION_RETRIES;

		System.out.println(
			JenkinsResultsParserUtil.combine(
				normalizedBuildURL, " was still running ",
				JenkinsResultsParserUtil.toDurationString(
					abortVerificationDuration),
				" after being interrupted"));

		return AbortResult.STILL_RUNNING;
	}

	public static void cancelQueueItem(
			JenkinsMaster jenkinsMaster, long queueId)
		throws Exception {

		String normalizedURL = JenkinsResultsParserUtil.fixURL(
			JenkinsResultsParserUtil.getLocalURL(jenkinsMaster.getURL()));

		_post(normalizedURL + "/queue/cancelItem?id=" + queueId);
	}

	public static void stopBuild(String buildURL) throws Exception {
		_stopDownstreamBuilds(buildURL);

		_stopBuild(buildURL);
	}

	public static void stopBuild(TopLevelBuild topLevelBuild) throws Exception {
		stopDownstreamBuilds(topLevelBuild);

		_stopBuild(topLevelBuild);
	}

	public static void stopDownstreamBuilds(TopLevelBuild topLevelBuild)
		throws Exception {

		List<Build> downstreamBuilds = topLevelBuild.getDownstreamBuilds(
			"running");

		for (Build downstreamBuild : downstreamBuilds) {
			_stopBuild(downstreamBuild);
		}
	}

	public static enum AbortResult {

		ALREADY_FINISHED, STILL_RUNNING, STOPPED

	}

	protected static String encodeAuthorizationFields(
		String username, String password) {

		String authorizationString = username + ":" + password;

		return new String(Base64.encodeBase64(authorizationString.getBytes()));
	}

	private static List<String> _getDownstreamURLs(String buildURL)
		throws Exception {

		List<String> downstreamURLs = new ArrayList<>();

		String consoleOutput = JenkinsResultsParserUtil.toString(
			JenkinsResultsParserUtil.getLocalURL(
				buildURL + "/logText/progressiveText"),
			true, true);

		Matcher progressiveTextMatcher = _progressiveTextPattern.matcher(
			consoleOutput);

		while (progressiveTextMatcher.find()) {
			String urlString = progressiveTextMatcher.group("url");

			Matcher buildURLMatcher = _buildURLPattern.matcher(urlString);

			if (buildURLMatcher.find()) {
				downstreamURLs.add(urlString);
			}
		}

		return downstreamURLs;
	}

	private static boolean _isBuilding(String normalizedBuildURL)
		throws Exception {

		JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
			normalizedBuildURL + "/api/json?tree=result", false);

		if (!jsonObject.has("result")) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to determine whether ", normalizedBuildURL,
					" is running, no result was reported"));
		}

		return jsonObject.isNull("result");
	}

	private static int _post(String urlString) throws Exception {
		URL urlObject = new URL(urlString);

		HttpURLConnection httpConnection =
			(HttpURLConnection)urlObject.openConnection();

		httpConnection.setRequestMethod("POST");

		_setAuthorization(httpConnection);

		int responseCode = httpConnection.getResponseCode();

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Response from ", urlString, ": ", String.valueOf(responseCode),
				" ", httpConnection.getResponseMessage()));

		return responseCode;
	}

	private static void _setAuthorization(HttpURLConnection httpConnection)
		throws Exception {

		String username = JenkinsResultsParserUtil.getBuildProperty(
			"jenkins.admin.user.name");

		if (JenkinsResultsParserUtil.isNullOrEmpty(username)) {
			throw new RuntimeException(
				"Unable to find property \"jenkins.admin.user.name\"");
		}

		String password = JenkinsResultsParserUtil.getBuildProperty(
			"jenkins.admin.user.token");

		if (JenkinsResultsParserUtil.isNullOrEmpty(password)) {
			throw new RuntimeException(
				"Unable to find property \"jenkins.admin.user.token\"");
		}

		httpConnection.setRequestProperty(
			"Authorization",
			"Basic " + encodeAuthorizationFields(username, password));
	}

	private static void _stopBuild(Build build) throws Exception {
		_stopBuild(build.getBuildURL());
	}

	private static void _stopBuild(String buildURL) throws Exception {
		String normalizedBuildURL = JenkinsResultsParserUtil.fixURL(
			JenkinsResultsParserUtil.getLocalURL(buildURL));

		if (!_isBuilding(normalizedBuildURL)) {
			return;
		}

		_post(normalizedBuildURL + "/stop");
	}

	private static void _stopDownstreamBuilds(String buildURL)
		throws Exception {

		List<String> downstreamURLs = _getDownstreamURLs(buildURL);

		for (String downstreamURL : downstreamURLs) {
			_stopBuild(downstreamURL);
		}
	}

	private static final long _ABORT_VERIFICATION_PERIOD = 5 * 1000L;

	private static final int _ABORT_VERIFICATION_RETRIES = 6;

	private static final Pattern _buildURLPattern = Pattern.compile(
		".+://(?<hostName>[^.]+)(.liferay.com)?/job/(?<jobName>[^/]+).*/" +
			"(?<buildNumber>\\d+)/");
	private static final Pattern _progressiveTextPattern = Pattern.compile(
		"Build \\'.*\\' started at (?<url>.+)\\.");

}