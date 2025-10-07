/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestClassMethod {

	public String getIssues() {
		return _issues;
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	public String getName() {
		return _name;
	}

	public TestClass getTestClass() {
		return _testClass;
	}

	public boolean isBuildCachingEnabled() {
		return _testClass.isBuildCachingEnabled();
	}

	public boolean isIgnored() {
		return _ignored;
	}

	protected TestClassMethod(
		boolean ignored, String name, String issues, TestClass testClass) {

		_ignored = ignored;
		_name = name;
		_issues = issues;
		_testClass = testClass;

		_jsonObject = new JSONObject(
		).put(
			"ignored", ignored
		).put(
			"issues", issues
		).put(
			"name", name
		);
	}

	protected TestClassMethod(
		boolean ignored, String name, TestClass testClass) {

		_ignored = ignored;
		_name = name;
		_testClass = testClass;

		_issues = null;

		_jsonObject = new JSONObject(
		).put(
			"ignored", ignored
		).put(
			"issues", ""
		).put(
			"name", name
		);
	}

	protected TestClassMethod(JSONObject jsonObject, TestClass testClass) {
		_jsonObject = jsonObject;
		_testClass = testClass;

		_ignored = jsonObject.getBoolean("ignored");
		_issues = jsonObject.optString("issues");
		_name = jsonObject.getString("name");
	}

	private final boolean _ignored;
	private final String _issues;
	private final JSONObject _jsonObject;
	private final String _name;
	private final TestClass _testClass;

}