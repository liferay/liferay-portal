/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.history;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class TestrayJobHistoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetExpectedTestCounts() {
		JSONObject buildDatabaseJSONObject = new JSONObject(
		).put(
			"jobs",
			new JSONObject(
			).put(
				"test-portal-acceptance-upstream",
				new JSONObject(
				).put(
					"batches",
					new JSONArray(
					).put(
						_getBatchJSONObject(
							"functional-tomcat101-postgresql163",
							new JSONObject(
							).put(
								"test_classes",
								new JSONArray(
								).put(
									_getTestClassJSONObject("LocalFile.A#A")
								).put(
									_getTestClassJSONObject("LocalFile.A#B")
								)
							),
							new JSONObject(
							).put(
								"test_classes",
								new JSONArray(
								).put(
									_getTestClassJSONObject("LocalFile.A#A")
								)
							))
					).put(
						_getBatchJSONObject(
							"modules-integration-postgresql163",
							new JSONObject(
							).put(
								"test_tasks",
								new JSONArray(
								).put(
									new JSONObject(
									).put(
										"test_classes",
										new JSONArray(
										).put(
											_getTestClassJSONObject("ATest")
										).put(
											_getTestClassJSONObject("BTest")
										).put(
											_getTestClassJSONObject("CTest")
										)
									)
								)
							))
					).put(
						_getBatchJSONObject("source-format", new JSONObject())
					)
				)
			)
		);

		Map<String, Integer> expectedTestCounts =
			TestrayJobHistory.getExpectedTestCounts(buildDatabaseJSONObject);

		Assert.assertEquals(
			expectedTestCounts.toString(), 3, expectedTestCounts.size());
		Assert.assertEquals(
			expectedTestCounts.get("functional-tomcat101-postgresql163"),
			Integer.valueOf(2));
		Assert.assertEquals(
			expectedTestCounts.get("modules-integration-postgresql163"),
			Integer.valueOf(3));
		Assert.assertEquals(
			expectedTestCounts.get("source-format"), Integer.valueOf(0));
	}

	private JSONObject _getBatchJSONObject(
		String batchName, JSONObject... axisJSONObjects) {

		JSONArray axesJSONArray = new JSONArray();

		for (JSONObject axisJSONObject : axisJSONObjects) {
			axesJSONArray.put(axisJSONObject);
		}

		return new JSONObject(
		).put(
			"batch_name", batchName
		).put(
			"segments",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"axes", axesJSONArray
				)
			)
		);
	}

	private JSONObject _getTestClassJSONObject(String name) {
		return new JSONObject(
		).put(
			"name", name
		);
	}

}