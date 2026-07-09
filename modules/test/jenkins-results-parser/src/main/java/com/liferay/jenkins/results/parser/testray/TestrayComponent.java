/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayComponent {

	public static final String[] FIELD_NAMES = {
		"dateCreated", "dateModified", "id", "name", "teamToComponents"
	};

	public Long getId() {
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

	public TestrayTeam getTestrayTeam() {
		return _testrayTeam;
	}

	protected TestrayComponent(
		TestrayProject testrayProject, JSONObject jsonObject) {

		_testrayProject = testrayProject;
		_jsonObject = jsonObject;

		JSONObject teamJSONObject = jsonObject.getJSONObject(
			"teamToComponents");

		_testrayTeam = testrayProject.getTestrayTeamById(
			teamJSONObject.getLong("id"));

		_testrayTeam.addTestrayComponent(this);
	}

	private final JSONObject _jsonObject;
	private final TestrayProject _testrayProject;
	private final TestrayTeam _testrayTeam;

}