/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitModulesBatchTestClassGroup;

import java.io.File;

import java.util.Arrays;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class JSUnitJUnitTestClassTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetCachedTestClassReports() throws Exception {
		JSUnitModulesBatchTestClassGroup jsUnitModulesBatchTestClassGroup =
			_getBatchTestClassGroup();

		TestClassReport testClassReport = Mockito.mock(TestClassReport.class);

		Mockito.doReturn(
			testClassReport
		).when(
			jsUnitModulesBatchTestClassGroup
		).getCachedTestClassReport(
			_TEST_TASK_NAME
		);

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(
			_getJSONObject(_JS_UNIT_FILE), jsUnitModulesBatchTestClassGroup);

		Assert.assertEquals(
			Collections.singletonList(testClassReport),
			jsUnitJUnitTestClass.getCachedTestClassReports());
	}

	@Test
	public void testGetCachedTestClassReportsTestClassFile() throws Exception {
		JSUnitModulesBatchTestClassGroup jsUnitModulesBatchTestClassGroup =
			_getBatchTestClassGroup();

		TestClassReport testClassReport1 = Mockito.mock(TestClassReport.class);

		Mockito.doReturn(
			testClassReport1
		).when(
			jsUnitModulesBatchTestClassGroup
		).getCachedTestClassReport(
			_JS_UNIT_FILE
		);

		TestClassReport testClassReport2 = Mockito.mock(TestClassReport.class);

		Mockito.doReturn(
			testClassReport2
		).when(
			jsUnitModulesBatchTestClassGroup
		).getCachedTestClassReport(
			_JS_UNIT_FILE_OTHER
		);

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(
			_getJSONObject(_JS_UNIT_FILE, _JS_UNIT_FILE_OTHER),
			jsUnitModulesBatchTestClassGroup);

		jsUnitJUnitTestClass.setTestClassFileReported(true);

		Assert.assertEquals(
			Arrays.asList(testClassReport1, testClassReport2),
			jsUnitJUnitTestClass.getCachedTestClassReports());
	}

	@Test
	public void testGetCachedTestClassReportsTestClassFileIncomplete()
		throws Exception {

		JSUnitModulesBatchTestClassGroup jsUnitModulesBatchTestClassGroup =
			_getBatchTestClassGroup();

		Mockito.doReturn(
			Mockito.mock(TestClassReport.class)
		).when(
			jsUnitModulesBatchTestClassGroup
		).getCachedTestClassReport(
			_JS_UNIT_FILE
		);

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(
			_getJSONObject(_JS_UNIT_FILE, _JS_UNIT_FILE_OTHER),
			jsUnitModulesBatchTestClassGroup);

		jsUnitJUnitTestClass.setTestClassFileReported(true);

		Assert.assertNull(jsUnitJUnitTestClass.getCachedTestClassReports());
	}

	@Test
	public void testGetCachedTestClassReportsTestClassFileTaskName()
		throws Exception {

		JSUnitModulesBatchTestClassGroup jsUnitModulesBatchTestClassGroup =
			_getBatchTestClassGroup();

		Mockito.doReturn(
			Mockito.mock(TestClassReport.class)
		).when(
			jsUnitModulesBatchTestClassGroup
		).getCachedTestClassReport(
			_TEST_TASK_NAME
		);

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(
			_getJSONObject(_JS_UNIT_FILE), jsUnitModulesBatchTestClassGroup);

		jsUnitJUnitTestClass.setTestClassFileReported(true);

		Assert.assertNull(jsUnitJUnitTestClass.getCachedTestClassReports());
	}

	@Test
	public void testIsTestClassFileReported() throws Exception {
		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(null);

		Assert.assertFalse(jsUnitJUnitTestClass.isTestClassFileReported());

		jsUnitJUnitTestClass.setTestClassFileReported(true);

		Assert.assertTrue(jsUnitJUnitTestClass.isTestClassFileReported());
	}

	@Test
	public void testIsTestClassFileReportedJSONObject() throws Exception {
		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(null);

		jsUnitJUnitTestClass.setTestClassFileReported(true);

		JSUnitJUnitTestClass rebuiltJSUnitJUnitTestClass = _getTestClass(
			_serialize(jsUnitJUnitTestClass));

		Assert.assertTrue(
			rebuiltJSUnitJUnitTestClass.isTestClassFileReported());
	}

	@Test
	public void testIsTestClassFileReportedJSONObjectDefault()
		throws Exception {

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(null);

		JSONObject jsonObject = _serialize(jsUnitJUnitTestClass);

		Assert.assertFalse(jsonObject.has("test_class_file_reported"));

		JSUnitJUnitTestClass rebuiltJSUnitJUnitTestClass = _getTestClass(
			jsonObject);

		Assert.assertFalse(
			rebuiltJSUnitJUnitTestClass.isTestClassFileReported());
	}

	private JSUnitModulesBatchTestClassGroup _getBatchTestClassGroup() {
		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doReturn(
			new File("/x")
		).when(
			portalGitWorkingDirectory
		).getWorkingDirectory();

		JSUnitModulesBatchTestClassGroup jsUnitModulesBatchTestClassGroup =
			Mockito.mock(JSUnitModulesBatchTestClassGroup.class);

		Mockito.doReturn(
			portalGitWorkingDirectory
		).when(
			jsUnitModulesBatchTestClassGroup
		).getPortalGitWorkingDirectory();

		Mockito.doReturn(
			true
		).when(
			jsUnitModulesBatchTestClassGroup
		).isBuildCachingEnabled();

		return jsUnitModulesBatchTestClassGroup;
	}

	private JSONObject _getJSONObject(String... testClassMethodNames) {
		JSONArray methodsJSONArray = new JSONArray();

		for (String testClassMethodName : testClassMethodNames) {
			methodsJSONArray.put(
				new JSONObject(
				).put(
					"ignored", false
				).put(
					"name", testClassMethodName
				));
		}

		JSONObject jsonObject = new JSONObject();

		return jsonObject.put(
			"file", "/x/modules/apps/a/b"
		).put(
			"ignored", false
		).put(
			"methods", methodsJSONArray
		);
	}

	private JSUnitJUnitTestClass _getTestClass(JSONObject jsonObject) {
		if (jsonObject == null) {
			jsonObject = _getJSONObject();
		}

		return _getTestClass(jsonObject, _getBatchTestClassGroup());
	}

	private JSUnitJUnitTestClass _getTestClass(
		JSONObject jsonObject,
		JSUnitModulesBatchTestClassGroup jsUnitModulesBatchTestClassGroup) {

		return (JSUnitJUnitTestClass)TestClassFactory.newTestClass(
			jsUnitModulesBatchTestClassGroup, jsonObject);
	}

	private JSONObject _serialize(JSUnitJUnitTestClass jsUnitJUnitTestClass) {
		JSONObject jsonObject = jsUnitJUnitTestClass.getJSONObject();

		return new JSONObject(jsonObject.toString());
	}

	private static final String _JS_UNIT_FILE =
		"modules/apps/a/b/test/Foo.test.js";

	private static final String _JS_UNIT_FILE_OTHER =
		"modules/apps/a/b/test/Bar.test.js";

	private static final String _TEST_TASK_NAME = ":apps:a:b:packageRunTest";

}