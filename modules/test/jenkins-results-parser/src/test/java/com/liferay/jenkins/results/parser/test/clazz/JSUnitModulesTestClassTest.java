/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitModulesBatchTestClassGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class JSUnitModulesTestClassTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testIsTestClassFileReported() throws Exception {
		JSUnitModulesTestClass jsUnitModulesTestClass = _getTestClass(null);

		Assert.assertFalse(jsUnitModulesTestClass.isTestClassFileReported());

		jsUnitModulesTestClass.setTestClassFileReported(true);

		Assert.assertTrue(jsUnitModulesTestClass.isTestClassFileReported());
	}

	@Test
	public void testIsTestClassFileReportedJSONObject() throws Exception {
		JSUnitModulesTestClass jsUnitModulesTestClass = _getTestClass(null);

		jsUnitModulesTestClass.setTestClassFileReported(true);

		JSUnitModulesTestClass rebuiltJSUnitModulesTestClass = _getTestClass(
			_serialize(jsUnitModulesTestClass));

		Assert.assertTrue(
			rebuiltJSUnitModulesTestClass.isTestClassFileReported());
	}

	@Test
	public void testIsTestClassFileReportedJSONObjectDefault()
		throws Exception {

		JSUnitModulesTestClass jsUnitModulesTestClass = _getTestClass(null);

		JSONObject jsonObject = _serialize(jsUnitModulesTestClass);

		Assert.assertFalse(jsonObject.has("test_class_file_reported"));

		JSUnitModulesTestClass rebuiltJSUnitModulesTestClass = _getTestClass(
			jsonObject);

		Assert.assertFalse(
			rebuiltJSUnitModulesTestClass.isTestClassFileReported());
	}

	private JSUnitModulesTestClass _getTestClass(JSONObject jsonObject) {
		if (jsonObject == null) {
			jsonObject = new JSONObject();

			jsonObject.put(
				"file", RandomTestUtil.randomString()
			).put(
				"methods", new JSONArray()
			).put(
				"task_name", "packageRunTest"
			);
		}

		return (JSUnitModulesTestClass)TestClassFactory.newTestClass(
			Mockito.mock(JSUnitModulesBatchTestClassGroup.class), jsonObject);
	}

	private JSONObject _serialize(
		JSUnitModulesTestClass jsUnitModulesTestClass) {

		JSONObject jsonObject = jsUnitModulesTestClass.getJSONObject();

		return new JSONObject(jsonObject.toString());
	}

}