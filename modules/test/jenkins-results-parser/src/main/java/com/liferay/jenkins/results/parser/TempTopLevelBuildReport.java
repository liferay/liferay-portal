/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Map;

import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public class TempTopLevelBuildReport extends BaseTopLevelBuildReport {

	@Override
	public JSONObject getBuildReportJSONObject() {
		if (_buildReportJSONObject != null) {
			return _buildReportJSONObject;
		}

		return null;
	}

	protected TempTopLevelBuildReport(TopLevelBuild topLevelBuild) {
		super(topLevelBuild.getBuildURL());

		_buildJSONObject = JenkinsAPIUtil.getAPIJSONObject(
			String.valueOf(topLevelBuild.getBuildURL()));

		_topLevelBuild = topLevelBuild;

		initializeBuildReportJSONObject();
	}

	protected void initializeBuildReportJSONObject() {
		if (_buildReportJSONObject != null) {
			return;
		}

		Map<String, String> buildParameters = _topLevelBuild.getParameters();

		JSONObject buildParametersJSONObject = new JSONObject();

		for (Map.Entry<String, String> buildParameter :
				buildParameters.entrySet()) {

			buildParametersJSONObject.put(
				buildParameter.getKey(), buildParameter.getValue());
		}

		_buildReportJSONObject = new JSONObject();

		_buildReportJSONObject.put(
			"buildParameters", buildParametersJSONObject
		).put(
			"buildURL", _topLevelBuild.getBuildURL()
		).put(
			"startTime", _topLevelBuild.getStartTime()
		).put(
			"testSuiteName", _topLevelBuild.getTestSuiteName()
		);
	}

	private final JSONObject _buildJSONObject;
	private JSONObject _buildReportJSONObject;
	private final Build _topLevelBuild;

}