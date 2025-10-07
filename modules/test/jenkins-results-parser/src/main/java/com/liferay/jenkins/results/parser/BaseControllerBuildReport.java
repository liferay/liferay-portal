/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseControllerBuildReport
	extends BaseBuildReport implements ControllerBuildReport {

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
	public JSONObject getBuildReportJSONObject() {
		return _buildReportJSONObject;
	}

	@Override
	public String getTestrayBuildDateString() {
		return JenkinsResultsParserUtil.toDateString(
			getStartDate(), "yyyy-MM-dd HH:mm:ss", "America/Los_Angeles");
	}

	@Override
	public TopLevelBuildReport getTopLevelBuildReport() {
		return _topLevelBuildReport;
	}

	protected BaseControllerBuildReport(
		Build controllerBuild, TopLevelBuildReport topLevelBuildReport) {

		super(controllerBuild.getBuildURL());

		_topLevelBuildReport = topLevelBuildReport;

		_buildReportJSONObject = controllerBuild.getBuildReportJSONObject();
	}

	protected BaseControllerBuildReport(
		JSONObject buildReportJSONObject,
		TopLevelBuildReport topLevelBuildReport) {

		super(buildReportJSONObject.getString("buildURL"));

		_buildReportJSONObject = buildReportJSONObject;
		_topLevelBuildReport = topLevelBuildReport;
	}

	private final JSONObject _buildReportJSONObject;
	private final TopLevelBuildReport _topLevelBuildReport;

}