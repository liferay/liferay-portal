/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Michael Hashimoto
 */
public class BuildDataFactory {

	public static BatchBuildData newBatchBuildData(
		String runId, String jobName, String buildURL) {

		if (jobName.contains("portal") ||
			jobName.contains("root-cause-analysis-tool")) {

			return new PortalBatchBuildData(runId, jobName, buildURL);
		}

		return new DefaultBatchBuildData(runId, jobName, buildURL);
	}

	public static BuildData newBuildData(
		String runId, String jobName, String buildURL) {

		if (jobName.endsWith("-batch")) {
			return newBatchBuildData(runId, jobName, buildURL);
		}

		return newTopLevelBuildData(runId, jobName, buildURL);
	}

	public static TopLevelBuildData newTopLevelBuildData(
		String runId, String jobName, String buildURL) {

		if (jobName.startsWith("archive-binaries-cache") ||
			jobName.contains("portal") ||
			jobName.contains("root-cause-analysis-tool") ||
			jobName.equals("test-poshi-release")) {

			if (jobName.contains("test-portal-testsuite-upstream-controller") ||
				jobName.contains("test-portal-upstream-controller")) {

				return new ControllerPortalTopLevelBuildData(
					runId, jobName, buildURL);
			}

			return new PortalTopLevelBuildData(runId, jobName, buildURL);
		}

		return new DefaultTopLevelBuildData(runId, jobName, buildURL);
	}

}