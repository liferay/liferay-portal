/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class JSUnitJUnitTestResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestTaskName() {
		testEquals(
			":apps:frontend-js:frontend-js-clay-web:packageRunTest",
			_getTestTaskName(
				"liferay-portal.modules.apps.frontend-js.frontend-js-clay-" +
					"web.clay.clay-button.src.__tests__.index"));
		testEquals(
			":apps:frontend-js:frontend-js-web:packageRunTest",
			_getTestTaskName(
				"liferay-portal.modules.apps.frontend-js.frontend-js-web.src." +
					"__tests__.index"));
		testEquals(
			":apps:portal-search:portal-search-web:packageRunTest",
			_getTestTaskName(
				"liferay-portal.modules.apps.portal-search.portal-search-" +
					"web.test.js.index"));
	}

	private String _getTestTaskName(String className) {
		JSUnitJUnitTestResult jsUnitJUnitTestResult = new JSUnitJUnitTestResult(
			Mockito.mock(Build.class),
			new JSONObject(
			).put(
				"className", className
			).put(
				"duration", RandomTestUtil.randomDouble()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"status", RandomTestUtil.randomString()
			));

		return jsUnitJUnitTestResult.getTestTaskName();
	}

}