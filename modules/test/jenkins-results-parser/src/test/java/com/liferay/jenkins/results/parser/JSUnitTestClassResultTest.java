/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class JSUnitTestClassResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestClassResults() {
		testEquals(
			2, _size(_getTestClassResults(_CLASS_NAME, _OTHER_CLASS_NAME)));
	}

	@Test
	public void testGetTestClassResultsSameClassName() {
		testEquals(1, _size(_getTestClassResults(_CLASS_NAME, _CLASS_NAME)));
	}

	@Test
	public void testGetTestResults() {
		TestClassResult testClassResult = _getTestClassResult(
			_CLASS_PATH, _OTHER_CLASS_PATH);

		testEquals(2, _size(testClassResult.getTestResults()));
	}

	@Test
	public void testGetTestResultsSameTestClassFile() {
		TestClassResult testClassResult = _getTestClassResult(
			_CLASS_PATH, _CLASS_PATH);

		testEquals(1, _size(testClassResult.getTestResults()));
	}

	private TestClassResult _getTestClassResult(String... classPaths) {
		JSONArray casesJSONArray = new JSONArray();

		for (String classPath : classPaths) {
			JSONObject caseJSONObject = new JSONObject();

			caseJSONObject.put(
				"className", classPath
			).put(
				"duration", 1
			).put(
				"name", _TEST_NAME
			).put(
				"status", "PASSED"
			);

			casesJSONArray.put(caseJSONObject);
		}

		JSONObject suiteJSONObject = new JSONObject();

		suiteJSONObject.put(
			"cases", casesJSONArray
		).put(
			"duration", 1
		).put(
			"name", "suite"
		);

		DownstreamBuild downstreamBuild = Mockito.mock(DownstreamBuild.class);

		Mockito.when(
			downstreamBuild.getBatchName()
		).thenReturn(
			"js-unit"
		);

		return TestClassResultFactory.newTestClassResult(
			downstreamBuild, suiteJSONObject);
	}

	private List<TestClassResult> _getTestClassResults(String... classNames) {
		JSONArray suitesJSONArray = new JSONArray();

		for (String className : classNames) {
			JSONObject caseJSONObject = new JSONObject();

			caseJSONObject.put(
				"className", className
			).put(
				"duration", 1
			).put(
				"name", className + ".a"
			).put(
				"status", "PASSED"
			);

			JSONObject suiteJSONObject = new JSONObject();

			suiteJSONObject.put(
				"cases",
				new JSONArray(
				).put(
					caseJSONObject
				)
			).put(
				"duration", 1
			).put(
				"name", className
			);

			suitesJSONArray.put(suiteJSONObject);
		}

		JSONObject testReportJSONObject = new JSONObject();

		testReportJSONObject.put("suites", suitesJSONArray);

		JUnitDownstreamBuild jUnitDownstreamBuild = Mockito.mock(
			JUnitDownstreamBuild.class, Mockito.CALLS_REAL_METHODS);

		Mockito.doReturn(
			"js-unit"
		).when(
			jUnitDownstreamBuild
		).getBatchName();

		Mockito.doReturn(
			testReportJSONObject
		).when(
			jUnitDownstreamBuild
		).getTestReportJSONObject(
			Mockito.anyBoolean()
		);

		Mockito.doReturn(
			true
		).when(
			jUnitDownstreamBuild
		).isCompleted();

		return jUnitDownstreamBuild.getTestClassResults();
	}

	private int _size(List<?> results) {
		if (results == null) {
			return 0;
		}

		return results.size();
	}

	private static final String _CLASS_NAME =
		"liferay-portal.modules.apps.a.b.test.js.c";

	private static final String _CLASS_PATH =
		"modules/apps/a/b/test/js/c.test.js";

	private static final String _OTHER_CLASS_NAME =
		"liferay-portal.modules.apps.a.b.test.js.d";

	private static final String _OTHER_CLASS_PATH =
		"modules/apps/a/b/test/js/nested/c.test.js";

	private static final String _TEST_NAME = "a > b";

}