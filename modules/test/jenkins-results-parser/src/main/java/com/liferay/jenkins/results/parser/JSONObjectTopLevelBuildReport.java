/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSONObjectTopLevelBuildReport extends BaseTopLevelBuildReport {

	@Override
	public JSONObject getBuildReportJSONObject() {
		return _buildReportJSONObject;
	}

	protected JSONObjectTopLevelBuildReport(JSONObject buildReportJSONObject) {
		super(buildReportJSONObject.getString("buildURL"));

		_buildReportJSONObject = buildReportJSONObject;

		initialize(_buildReportJSONObject);
	}

	private final JSONObject _buildReportJSONObject;

}