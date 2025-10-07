/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayProject {

	public static final String[] FIELD_NAMES = {
		"dateCreated", "dateModified", "description", "id", "name"
	};

	public TestrayProductVersion createTestrayProductVersion(
		String testrayProductVersionName) {

		TestrayProductVersion testrayProductVersion =
			getTestrayProductVersionByName(testrayProductVersionName);

		if (testrayProductVersion != null) {
			return testrayProductVersion;
		}

		JSONObject requestJSONObject = new JSONObject();

		requestJSONObject.put(
			"name", testrayProductVersionName
		).put(
			"r_projectToProductVersions_c_projectId", getID()
		);

		try {
			return TestrayFactory.newTestrayProductVersion(
				this,
				new JSONObject(
					_testrayServer.requestPost(
						"/o/c/productversions", requestJSONObject.toString())));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public TestrayRoutine createTestrayRoutine(String testrayRoutineName) {
		TestrayRoutine testrayRoutine = getTestrayRoutineByName(
			testrayRoutineName);

		if (testrayRoutine != null) {
			return testrayRoutine;
		}

		JSONObject requestJSONObject = new JSONObject();

		requestJSONObject.put(
			"name", testrayRoutineName
		).put(
			"r_routineToProjects_c_projectId", getID()
		);

		try {
			return TestrayFactory.newTestrayRoutine(
				this,
				new JSONObject(
					_testrayServer.requestPost(
						"/o/c/routines", requestJSONObject.toString())));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public String getDescription() {
		return _jsonObject.optString("description");
	}

	public long getID() {
		return _jsonObject.getLong("id");
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	public String getName() {
		return _jsonObject.getString("name");
	}

	public TestrayCase getTestrayCaseByName(String testCaseName) {
		_initTestrayCases();

		return _testrayCases.get(testCaseName);
	}

	public List<TestrayCase> getTestrayCases() {
		_initTestrayCases();

		return new ArrayList<>(_testrayCases.values());
	}

	public TestrayComponent getTestrayComponentByID(long componentID) {
		for (TestrayComponent testrayComponent : getTestrayComponents()) {
			if (Objects.equals(componentID, testrayComponent.getID())) {
				return testrayComponent;
			}
		}

		return null;
	}

	public TestrayComponent getTestrayComponentByName(String componentName) {
		for (TestrayComponent testrayComponent : getTestrayComponents()) {
			if (Objects.equals(componentName, testrayComponent.getName())) {
				return testrayComponent;
			}
		}

		return null;
	}

	public List<TestrayComponent> getTestrayComponents() {
		if (_testrayComponents != null) {
			return _testrayComponents;
		}

		_testrayComponents = new ArrayList<>();

		String filter = JenkinsResultsParserUtil.combine(
			"r_projectToComponents_c_projectId eq '", String.valueOf(getID()),
			"'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"components", TestrayComponent.FIELD_NAMES, filter, null);

			for (JSONObject entityJSONObject : entityJSONObjects) {
				_testrayComponents.add(
					TestrayFactory.newTestrayComponent(this, entityJSONObject));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return _testrayComponents;
	}

	public TestrayProductVersion getTestrayProductVersionByID(
		long productVersionID) {

		String filter = JenkinsResultsParserUtil.combine(
			"id eq '", String.valueOf(productVersionID), "' and ",
			"r_projectToProductVersions_c_projectId eq '",
			String.valueOf(getID()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"productVersions", TestrayProductVersion.FIELD_NAMES, filter,
				null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			Iterator<JSONObject> iterator = entityJSONObjects.iterator();

			return TestrayFactory.newTestrayProductVersion(
				this, iterator.next());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public TestrayProductVersion getTestrayProductVersionByName(
		String productVersionName) {

		String filter = JenkinsResultsParserUtil.combine(
			"name eq '", productVersionName, "' and ",
			"r_projectToProductVersions_c_projectId eq '",
			String.valueOf(getID()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"productVersions", TestrayProductVersion.FIELD_NAMES, filter,
				null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			Iterator<JSONObject> iterator = entityJSONObjects.iterator();

			return TestrayFactory.newTestrayProductVersion(
				this, iterator.next());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public TestrayRoutine getTestrayRoutineByID(long routineID) {
		TestrayRoutine testrayRoutine = _testrayServer.getTestrayRoutineByID(
			routineID);

		if (testrayRoutine != null) {
			return testrayRoutine;
		}

		String filter = JenkinsResultsParserUtil.combine(
			"id eq '", String.valueOf(routineID), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"routines", TestrayRoutine.FIELD_NAMES, filter, null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			Iterator<JSONObject> iterator = entityJSONObjects.iterator();

			return TestrayFactory.newTestrayRoutine(this, iterator.next());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public TestrayRoutine getTestrayRoutineByName(String routineName) {
		String filter = JenkinsResultsParserUtil.combine(
			"name eq '", routineName, "' and ",
			"r_routineToProjects_c_projectId eq '", String.valueOf(getID()),
			"'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"routines", TestrayRoutine.FIELD_NAMES, filter, null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			Iterator<JSONObject> iterator = entityJSONObjects.iterator();

			return TestrayFactory.newTestrayRoutine(this, iterator.next());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public TestrayServer getTestrayServer() {
		return _testrayServer;
	}

	public TestrayTeam getTestrayTeamByID(long componentID) {
		for (TestrayTeam testrayTeam : getTestrayTeams()) {
			if (componentID == testrayTeam.getID()) {
				return testrayTeam;
			}
		}

		return null;
	}

	public TestrayTeam getTestrayTeamByName(String teamName) {
		for (TestrayTeam testrayTeam : getTestrayTeams()) {
			if (Objects.equals(teamName, testrayTeam.getName())) {
				return testrayTeam;
			}
		}

		return null;
	}

	public List<TestrayTeam> getTestrayTeams() {
		if (_testrayTeams != null) {
			return _testrayTeams;
		}

		_testrayTeams = new ArrayList<>();

		String filter = JenkinsResultsParserUtil.combine(
			"r_projectToTeams_c_projectId eq '", String.valueOf(getID()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"teams", TestrayTeam.FIELD_NAMES, filter, null);

			for (JSONObject entityJSONObject : entityJSONObjects) {
				_testrayTeams.add(
					TestrayFactory.newTestrayTeam(this, entityJSONObject));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return _testrayTeams;
	}

	public URL getURL() {
		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					String.valueOf(_testrayServer.getURL()), "/#/project/",
					String.valueOf(getID()), "/routines"));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	protected TestrayProject(
		TestrayServer testrayServer, JSONObject jsonObject) {

		_testrayServer = testrayServer;
		_jsonObject = jsonObject;
	}

	private synchronized void _initTestrayCases() {
		if (_testrayCases != null) {
			return;
		}

		_testrayCases = new HashMap<>();

		String filter = JenkinsResultsParserUtil.combine(
			"r_projectToCases_c_projectId eq '", String.valueOf(getID()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"cases", TestrayCase.FIELD_NAMES, filter, null);

			for (JSONObject entityJSONObject : entityJSONObjects) {
				TestrayCase testrayCase = TestrayFactory.newTestrayCase(
					this, entityJSONObject);

				_testrayCases.put(testrayCase.getName(), testrayCase);
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final JSONObject _jsonObject;
	private Map<String, TestrayCase> _testrayCases;
	private List<TestrayComponent> _testrayComponents;
	private final TestrayServer _testrayServer;
	private List<TestrayTeam> _testrayTeams;

}