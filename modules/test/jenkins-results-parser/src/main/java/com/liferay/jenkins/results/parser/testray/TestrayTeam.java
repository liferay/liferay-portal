/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayTeam {

	public static final String[] FIELD_NAMES = {
		"dateCreated", "dateModified", "id", "name"
	};

	public void addTestrayComponent(TestrayComponent testrayComponent) {
		_testrayComponents.add(testrayComponent);
	}

	public Long getId() {
		return _jsonObject.getLong("id");
	}

	public String getName() {
		return _jsonObject.getString("name");
	}

	public TestrayProject getTestrayProject() {
		return _testrayProject;
	}

	protected TestrayTeam(
		TestrayProject testrayProject, JSONObject jsonObject) {

		_testrayProject = testrayProject;
		_jsonObject = jsonObject;
	}

	private final JSONObject _jsonObject;
	private final Set<TestrayComponent> _testrayComponents = new HashSet<>();
	private final TestrayProject _testrayProject;

}