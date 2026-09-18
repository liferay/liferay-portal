/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.test.clazz.JSUnitModulesTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitModulesBatchTestClassGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class TestrayImporterTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testIsTestClassFileReported() throws Exception {
		JSUnitModulesTestClass jsUnitModulesTestClass = _getTestClass(
			"modules/apps/a/b/test/js/c.js");

		jsUnitModulesTestClass.setTestClassFileReported(true);

		Assert.assertTrue(_isTestClassFileReported(jsUnitModulesTestClass));
	}

	@Test
	public void testIsTestClassFileReportedNoTestClassMethods()
		throws Exception {

		JSUnitModulesTestClass jsUnitModulesTestClass = _getTestClass();

		jsUnitModulesTestClass.setTestClassFileReported(true);

		Assert.assertFalse(_isTestClassFileReported(jsUnitModulesTestClass));
	}

	@Test
	public void testIsTestClassFileReportedOtherTestClass() throws Exception {
		Assert.assertFalse(
			_isTestClassFileReported(Mockito.mock(TestClass.class)));
	}

	@Test
	public void testIsTestClassFileReportedTestTask() throws Exception {
		JSUnitModulesTestClass jsUnitModulesTestClass = _getTestClass(
			"modules/apps/a/b/test/js/c.js");

		Assert.assertFalse(_isTestClassFileReported(jsUnitModulesTestClass));
	}

	private JSUnitModulesTestClass _getTestClass(String... methodNames) {
		JSONArray methodsJSONArray = new JSONArray();

		for (String methodName : methodNames) {
			methodsJSONArray.put(
				new JSONObject(
				).put(
					"ignored", false
				).put(
					"name", methodName
				));
		}

		JSONObject jsonObject = new JSONObject(
		).put(
			"file", RandomTestUtil.randomString()
		).put(
			"methods", methodsJSONArray
		).put(
			"task_name", "packageRunTest"
		);

		return (JSUnitModulesTestClass)TestClassFactory.newTestClass(
			Mockito.mock(JSUnitModulesBatchTestClassGroup.class), jsonObject);
	}

	private boolean _isTestClassFileReported(TestClass testClass) {
		return ReflectionTestUtil.invoke(
			Mockito.mock(TestrayImporter.class), "_isTestClassFileReported",
			new Class<?>[] {TestClass.class}, testClass);
	}

}