/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.test.clazz.JSUnitJUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitModulesBatchTestClassGroup;

import java.io.File;

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
		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(
			"modules/apps/a/b/test/js/c.js");

		jsUnitJUnitTestClass.setTestClassFileReported(true);

		Assert.assertTrue(_isTestClassFileReported(jsUnitJUnitTestClass));
	}

	@Test
	public void testIsTestClassFileReportedNoTestClassMethods()
		throws Exception {

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass();

		jsUnitJUnitTestClass.setTestClassFileReported(true);

		Assert.assertFalse(_isTestClassFileReported(jsUnitJUnitTestClass));
	}

	@Test
	public void testIsTestClassFileReportedOtherTestClass() throws Exception {
		Assert.assertFalse(
			_isTestClassFileReported(Mockito.mock(TestClass.class)));
	}

	@Test
	public void testIsTestClassFileReportedTestTask() throws Exception {
		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(
			"modules/apps/a/b/test/js/c.js");

		Assert.assertFalse(_isTestClassFileReported(jsUnitJUnitTestClass));
	}

	private JSUnitModulesBatchTestClassGroup _getBatchTestClassGroup() {
		JSUnitModulesBatchTestClassGroup jsUnitModulesBatchTestClassGroup =
			Mockito.mock(JSUnitModulesBatchTestClassGroup.class);

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doReturn(
			new File("/x")
		).when(
			portalGitWorkingDirectory
		).getWorkingDirectory();

		Mockito.doReturn(
			portalGitWorkingDirectory
		).when(
			jsUnitModulesBatchTestClassGroup
		).getPortalGitWorkingDirectory();

		return jsUnitModulesBatchTestClassGroup;
	}

	private JSUnitJUnitTestClass _getTestClass(String... methodNames) {
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
			"ignored", false
		).put(
			"methods", methodsJSONArray
		);

		return (JSUnitJUnitTestClass)TestClassFactory.newTestClass(
			_getBatchTestClassGroup(), jsonObject);
	}

	private boolean _isTestClassFileReported(TestClass testClass) {
		return ReflectionTestUtil.invoke(
			Mockito.mock(TestrayImporter.class), "_isTestClassFileReported",
			new Class<?>[] {TestClass.class}, testClass);
	}

}