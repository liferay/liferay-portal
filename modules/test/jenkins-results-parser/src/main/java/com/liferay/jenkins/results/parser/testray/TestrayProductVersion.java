/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayProductVersion {

	public static final String[] FIELD_NAMES = {
		"dateCreated", "dateModified", "id", "name"
	};

	public long getId() {
		return _jsonObject.getLong("id");
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	public String getName() {
		return _jsonObject.getString("name");
	}

	public TestrayProject getTestrayProject() {
		return _testrayProject;
	}

	public TestrayServer getTestrayServer() {
		return _testrayServer;
	}

	protected TestrayProductVersion(
		TestrayProject testrayProject, JSONObject jsonObject) {

		_testrayProject = testrayProject;

		_testrayServer = testrayProject.getTestrayServer();

		_jsonObject = jsonObject;
	}

	private final JSONObject _jsonObject;
	private final TestrayProject _testrayProject;
	private final TestrayServer _testrayServer;

}