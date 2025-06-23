/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class DefaultDownstreamBuildReport extends BaseDownstreamBuildReport {

	protected DefaultDownstreamBuildReport(DownstreamBuild downstreamBuild) {
		super(downstreamBuild);
	}

	protected DefaultDownstreamBuildReport(
		String batchName, JSONObject buildReportJSONObject,
		TopLevelBuildReport topLevelBuildReport) {

		super(batchName, buildReportJSONObject, topLevelBuildReport);
	}

}