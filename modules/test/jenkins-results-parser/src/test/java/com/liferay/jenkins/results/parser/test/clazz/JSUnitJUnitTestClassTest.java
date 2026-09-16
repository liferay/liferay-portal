/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.RandomTestUtil;
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
public class JSUnitJUnitTestClassTest
	extends com.liferay.jenkins.results.parser.Test {

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

	private JSUnitJUnitTestClass _getTestClass(JSONObject jsonObject) {
		if (jsonObject == null) {
			jsonObject = new JSONObject();

			jsonObject.put(
				"file", RandomTestUtil.randomString()
			).put(
				"ignored", false
			).put(
				"methods", new JSONArray()
			);
		}

		return (JSUnitJUnitTestClass)TestClassFactory.newTestClass(
			_getBatchTestClassGroup(), jsonObject);
	}

	private JSONObject _serialize(JSUnitJUnitTestClass jsUnitJUnitTestClass) {
		JSONObject jsonObject = jsUnitJUnitTestClass.getJSONObject();

		return new JSONObject(jsonObject.toString());
	}

}