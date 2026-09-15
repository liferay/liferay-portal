/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.JSUnitModulesTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitModulesBatchTestClassGroup;

import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class JSUnitBatchBuildTestrayCaseResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetName() throws Exception {
		_mockWorkspace();

		JSUnitModulesTestClass jsUnitModulesTestClass = _getTestClass();

		testEquals(
			":apps:a:b:packageRunTest",
			_getJSUnitBatchBuildTestrayCaseResult(
				jsUnitModulesTestClass, null
			).getName());
	}

	@Test
	public void testGetNameTestClassFile() throws Exception {
		_mockWorkspace();

		JSUnitModulesTestClass jsUnitModulesTestClass = _getTestClass();

		List<TestClassMethod> testClassMethods =
			jsUnitModulesTestClass.getTestClassMethods();

		testEquals(
			"modules/apps/a/b/test/js/c.js",
			_getJSUnitBatchBuildTestrayCaseResult(
				jsUnitModulesTestClass, testClassMethods.get(0)
			).getName());
	}

	private JSUnitBatchBuildTestrayCaseResult
		_getJSUnitBatchBuildTestrayCaseResult(
			JSUnitModulesTestClass jsUnitModulesTestClass,
			TestClassMethod testClassMethod) {

		return new JSUnitBatchBuildTestrayCaseResult(
			Mockito.mock(JSUnitAxisTestClassGroup.class),
			jsUnitModulesTestClass, testClassMethod,
			Mockito.mock(TestrayBuild.class),
			Mockito.mock(TopLevelBuildReport.class));
	}

	private JSUnitModulesTestClass _getTestClass() {
		JSONObject methodJSONObject = new JSONObject();

		methodJSONObject.put(
			"ignored", false
		).put(
			"name", "modules/apps/a/b/test/js/c.js"
		);

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"file", "/x/modules/apps/a/b"
		).put(
			"methods",
			new JSONArray(
			).put(
				methodJSONObject
			)
		).put(
			"task_name", "packageRunTest"
		);

		return (JSUnitModulesTestClass)TestClassFactory.newTestClass(
			Mockito.mock(JSUnitModulesBatchTestClassGroup.class), jsonObject);
	}

	private void _mockWorkspace() {
		mockEnvironment(Collections.singletonMap("WORKSPACE", "/x"));
	}

}