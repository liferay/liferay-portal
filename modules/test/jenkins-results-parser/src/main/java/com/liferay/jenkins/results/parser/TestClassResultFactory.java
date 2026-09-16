/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestClassResultFactory {

	public static TestClassResult newTestClassResult(
		Build build, JSONObject suiteJSONObject) {

		if (build instanceof DownstreamBuild) {
			DownstreamBuild downstreamBuild = (DownstreamBuild)build;

			String batchName = downstreamBuild.getBatchName();

			if (batchName.startsWith("js-unit") ||
				batchName.startsWith("workspaces-js-unit")) {

				return new JSUnitTestClassResult(build, suiteJSONObject);
			}
		}

		return new DefaultTestClassResult(build, suiteJSONObject);
	}

}