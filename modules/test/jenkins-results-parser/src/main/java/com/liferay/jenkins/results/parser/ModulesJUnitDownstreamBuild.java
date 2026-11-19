/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.ModulesJUnitAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.task.TestTask;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class ModulesJUnitDownstreamBuild extends JUnitDownstreamBuild {

	@Override
	public JSONObject getBuildReportJSONObject() {
		JSONObject buildReportJSONObject = super.getBuildReportJSONObject();

		AxisTestClassGroup axisTestClassGroup = getAxisTestClassGroup();

		if (!(axisTestClassGroup instanceof ModulesJUnitAxisTestClassGroup)) {
			return buildReportJSONObject;
		}

		StopWatchRecordsGroup stopWatchRecordsGroup =
			getStopWatchRecordsGroup();

		ModulesJUnitAxisTestClassGroup modulesJUnitAxisTestClassGroup =
			(ModulesJUnitAxisTestClassGroup)axisTestClassGroup;

		JSONArray testTasksJSONArray = new JSONArray();

		for (TestTask testTask :
				modulesJUnitAxisTestClassGroup.getTestTasks()) {

			String testTaskName = testTask.getName();

			StopWatchRecord stopWatchRecord = stopWatchRecordsGroup.get(
				"test.execution.duration" +
					_getStopWatchTestTaskName(testTaskName));

			long duration = 0L;

			if (stopWatchRecord != null) {
				Long stopWatchRecordDuration = stopWatchRecord.getDuration();

				if (stopWatchRecordDuration != null) {
					duration = stopWatchRecordDuration;
				}
			}

			JSONObject testTaskJSONObject = new JSONObject();

			testTaskJSONObject.put(
				"duration", duration
			).put(
				"name", testTaskName
			);

			testTasksJSONArray.put(testTaskJSONObject);
		}

		buildReportJSONObject.put("testTasks", testTasksJSONArray);

		return buildReportJSONObject;
	}

	protected ModulesJUnitDownstreamBuild(
		String buildURL, DownstreamBuildReport cachedDownstreamBuildReport,
		TopLevelBuild topLevelBuild) {

		super(buildURL, cachedDownstreamBuildReport, topLevelBuild);
	}

	private String _getStopWatchTestTaskName(String testTaskName) {
		int index = testTaskName.indexOf(":");

		if (index > 0) {
			testTaskName = testTaskName.substring(index);
		}

		return testTaskName.replaceAll(":", ".");
	}

}