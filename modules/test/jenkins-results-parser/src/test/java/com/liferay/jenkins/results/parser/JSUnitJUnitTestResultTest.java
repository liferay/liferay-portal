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
	public void testGetClassName() {
		testEquals(
			":apps:portal-search:portal-search-web:packageRunTest",
			_getTestResultClassName(
				"liferay-portal.modules.apps.portal-search.portal-search-web." +
					"test.js.index",
				"a b"));
		testEquals(
			"modules/apps/portal-search/portal-search-web/test/js/index.js",
			_getTestResultClassName(
				"modules/apps/portal-search/portal-search-web/test/js/index.js",
				"a > b"));
	}

	@Test
	public void testGetTestNameTestClassFile() {
		testEquals(
			"a > b",
			_getTestResultTestName(
				"modules/apps/portal-search/portal-search-web/test/js/index.js",
				"a > b"));
		testEquals(
			"apps.portal-search.portal-search-web.test.js.index.simpleTestName",
			_getTestResultTestName(
				"liferay-portal.modules.apps.portal-search.portal-search-web." +
					"test.js.index",
				"simpleTestName"));
		testEquals(
			JenkinsResultsParserUtil.combine(
				"dxp.apps.analytics.analytics-reports-web.test.js.components.",
				"TrafficSources displays the traffic sources with buttons to ",
				"view keywords"),
			_getTestResultTestName(
				".opt.dev.projects.github.liferay-portal.modules.dxp.apps." +
					"analytics.analytics-reports-web.test.js.components",
				"TrafficSources displays the traffic sources with buttons to " +
					"view keywords"));
	}

	@Test
	public void testGetTestTaskName() {
		testEquals(
			":apps:frontend-js:frontend-js-clay-web:packageRunTest",
			_getTestResultTaskName(
				"liferay-portal.modules.apps.frontend-js.frontend-js-clay-" +
					"web.clay.clay-button.src.__tests__.index"));
		testEquals(
			":apps:frontend-js:frontend-js-web:packageRunTest",
			_getTestResultTaskName(
				"liferay-portal.modules.apps.frontend-js.frontend-js-web.src." +
					"__tests__.index"));
		testEquals(
			":apps:portal-search:portal-search-web:packageRunTest",
			_getTestResultTaskName(
				"liferay-portal.modules.apps.portal-search.portal-search-" +
					"web.test.js.index"));
	}

	@Test
	public void testGetTestTaskNameTestClassFile() {
		testEquals(
			":apps:frontend-js:frontend-js-clay-web:packageRunTest",
			_getTestResultTaskName(
				"modules/apps/frontend-js/frontend-js-clay-web/clay" +
					"/clay-button/src/__tests__/index.tsx"));
		testEquals(
			":apps:frontend-js:frontend-js-web:packageRunTest",
			_getTestResultTaskName(
				"modules/apps/frontend-js/frontend-js-web/src/__tests__" +
					"/index.js"));
		testEquals(
			":apps:portal-search:portal-search-web:packageRunTest",
			_getTestResultTaskName(
				"modules/apps/portal-search/portal-search-web/test/js" +
					"/index.js"));
	}

	@Test
	public void testGetTestTaskNameWorkspace() throws Exception {
		testEquals(
			"workspaces/liferay-aihub-workspace:client-extensions:" +
				"liferay-aihub-custom-element:packageRunTest",
			_getTestResultTaskName(
				"workspaces/liferay-aihub-workspace/client-extensions" +
					"/liferay-aihub-custom-element/src/tests/api.spec.ts"));
		testEquals(
			"workspaces/liferay-osbfaro-workspace:modules:osb-faro-web:" +
				"packageRunTest",
			_getTestResultTaskName(
				"workspaces/liferay-osbfaro-workspace/modules/osb-faro-web" +
					"/src/main/js/assets/__tests__/dashboards.tsx"));
	}

	private JSUnitJUnitTestResult _getJSUnitJUnitTestResult(
		String className, String name) {

		return new JSUnitJUnitTestResult(
			Mockito.mock(Build.class),
			new JSONObject(
			).put(
				"className", className
			).put(
				"duration", RandomTestUtil.randomDouble()
			).put(
				"name", name
			).put(
				"status", RandomTestUtil.randomString()
			));
	}

	private String _getTestResultClassName(String className, String name) {
		JSUnitJUnitTestResult jsUnitJUnitTestResult = _getJSUnitJUnitTestResult(
			className, name);

		return jsUnitJUnitTestResult.getClassName();
	}

	private String _getTestResultTaskName(String className) {
		JSUnitJUnitTestResult jsUnitJUnitTestResult = _getJSUnitJUnitTestResult(
			className, RandomTestUtil.randomString());

		return jsUnitJUnitTestResult.getTestTaskName();
	}

	private String _getTestResultTestName(String className, String name) {
		JSUnitJUnitTestResult jsUnitJUnitTestResult = _getJSUnitJUnitTestResult(
			className, name);

		return jsUnitJUnitTestResult.getTestName();
	}

}