/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.testray.TestrayBuild;

import java.io.File;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class BuildReportFactory {

	public static ControllerBuildReport newControllerBuildReport(
		JSONObject buildReportJSONObject,
		TopLevelBuildReport topLevelBuildReport) {

		if (!buildReportJSONObject.has("buildURL")) {
			return null;
		}

		return new DefaultControllerBuildReport(
			buildReportJSONObject, topLevelBuildReport);
	}

	public static DownstreamBuildReport newDownstreamBuildReport(
		DownstreamBuild downstreamBuild) {

		return new DefaultDownstreamBuildReport(downstreamBuild);
	}

	public static DownstreamBuildReport newDownstreamBuildReport(
		String batchName, JSONObject buildReportJSONObject,
		TopLevelBuildReport topLevelBuildReport) {

		if (!buildReportJSONObject.has("buildURL")) {
			return null;
		}

		return new DefaultDownstreamBuildReport(
			batchName, buildReportJSONObject, topLevelBuildReport);
	}

	public static TopLevelBuildReport newTopLevelBuildReport(
		File jenkinsConsoleFile) {

		if ((jenkinsConsoleFile == null) || !jenkinsConsoleFile.exists()) {
			return null;
		}

		return new FileTopLevelBuildReport(jenkinsConsoleFile);
	}

	public static TopLevelBuildReport newTopLevelBuildReport(
		JSONObject buildReportJSONObject) {

		if (buildReportJSONObject == null) {
			return null;
		}

		return new FileTopLevelBuildReport(buildReportJSONObject);
	}

	public static TopLevelBuildReport newTopLevelBuildReport(
		JSONObject buildJSONObject, JobReport jobReport) {

		String buildURLString = JenkinsResultsParserUtil.getRemoteURL(
			buildJSONObject.getString("url"));

		if (!_topLevelBuildReports.containsKey(buildURLString)) {
			_topLevelBuildReports.put(
				buildURLString,
				new URLTopLevelBuildReport(buildJSONObject, jobReport));
		}

		return _topLevelBuildReports.get(buildURLString);
	}

	public static TopLevelBuildReport newTopLevelBuildReport(
		TestrayBuild testrayBuild) {

		URL topLevelBuildURL = testrayBuild.getTopLevelBuildURL();

		if (topLevelBuildURL == null) {
			return null;
		}

		String buildURLString = String.valueOf(topLevelBuildURL);

		if (!_topLevelBuildReports.containsKey(buildURLString)) {
			_topLevelBuildReports.put(
				buildURLString, new TestrayTopLevelBuildReport(testrayBuild));
		}

		return _topLevelBuildReports.get(buildURLString);
	}

	public static TopLevelBuildReport newTopLevelBuildReport(
		TopLevelBuild topLevelBuild) {

		String buildURLString = topLevelBuild.getBuildURL();

		if (!_topLevelBuildReports.containsKey(buildURLString)) {
			_topLevelBuildReports.put(
				buildURLString, new DefaultTopLevelBuildReport(topLevelBuild));
		}

		return _topLevelBuildReports.get(buildURLString);
	}

	public static TopLevelBuildReport newTopLevelBuildReport(URL buildURL) {
		String buildURLString = JenkinsResultsParserUtil.getRemoteURL(
			buildURL.toString());

		if (!_topLevelBuildReports.containsKey(buildURLString)) {
			_topLevelBuildReports.put(
				buildURLString, new URLTopLevelBuildReport(buildURL));
		}

		return _topLevelBuildReports.get(buildURLString);
	}

	private static final Map<String, TopLevelBuildReport>
		_topLevelBuildReports = new HashMap<>();

}