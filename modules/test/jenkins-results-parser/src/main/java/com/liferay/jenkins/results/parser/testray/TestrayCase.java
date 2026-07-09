/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayCase {

	public static final String[] FIELD_NAMES = {
		"caseTypeToCases", "dateCreated", "dateModified", "id", "name"
	};

	public static final String[] FIELD_NAMES_CASE_IDS = {
		"dateCreated", "dateModified", "id", "name"
	};

	public String getComponent() {
		return null;
	}

	public long getId() {
		return _jsonObject.getLong("id");
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	public String getName() {
		return _jsonObject.getString("name");
	}

	public int getPriority() {
		return _jsonObject.getInt("priority");
	}

	public String getTeamName() {
		return null;
	}

	public TestrayCaseType getTestrayCaseType() {
		return _testrayCaseType;
	}

	public long getTestrayCaseTypeId() {
		return _testrayCaseType.getId();
	}

	public TestrayProject getTestrayProject() {
		return _testrayProject;
	}

	public String getType() {
		return null;
	}

	protected TestrayCase(
		TestrayProject testrayProject, JSONObject jsonObject) {

		_testrayProject = testrayProject;
		_jsonObject = jsonObject;

		TestrayServer testrayServer = testrayProject.getTestrayServer();

		if (jsonObject.has("r_caseTypeToCases_c_caseTypeId")) {
			_testrayCaseType = testrayServer.getTestrayCaseTypeById(
				jsonObject.getLong("r_caseTypeToCases_c_caseTypeId"));
		}
		else {
			JSONObject caseTypeJSONObject = jsonObject.getJSONObject(
				"caseTypeToCases");

			_testrayCaseType = testrayServer.getTestrayCaseTypeById(
				caseTypeJSONObject.getLong("id"));
		}
	}

	private final JSONObject _jsonObject;
	private final TestrayCaseType _testrayCaseType;
	private final TestrayProject _testrayProject;

}