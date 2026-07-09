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
import java.util.Date;
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
			"r_projectToProductVersions_c_projectId", getId()
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
			"r_routineToProjects_c_projectId", getId()
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

	public long getId() {
		return _jsonObject.getLong("id");
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	public String getName() {
		return _jsonObject.getString("name");
	}

	public TestrayCase getTestrayCase(
		String testCaseName, TestrayCaseType testrayCaseType) {

		return TestrayFactory.newTestrayCase(
			this, testCaseName, testrayCaseType);
	}

	public TestrayCase getTestrayCaseByName(String testCaseName) {
		_initTestrayCases();

		return _testrayCases.get(testCaseName);
	}

	public long getTestrayCaseIdByName(String testCaseName) {
		_initTestrayCaseIds();

		Long testrayCaseId = _testrayCaseIds.get(testCaseName);

		if (testrayCaseId == null) {
			return 0L;
		}

		return testrayCaseId;
	}

	public List<TestrayCase> getTestrayCases() {
		_initTestrayCases();

		return new ArrayList<>(_testrayCases.values());
	}

	public TestrayComponent getTestrayComponentById(long componentId) {
		synchronized (_testrayComponentsId) {
			TestrayComponent testrayComponent = _testrayComponentsId.get(
				componentId);

			if (testrayComponent != null) {
				return testrayComponent;
			}

			String filterString = JenkinsResultsParserUtil.combine(
				"id eq '", String.valueOf(componentId),
				"' and r_projectToComponents_c_projectId eq '",
				String.valueOf(getId()), "'");

			try {
				Set<JSONObject> entityJSONObjects =
					_testrayServer.requestGraphQL(
						"components", TestrayComponent.FIELD_NAMES,
						filterString, null, 1, 1);

				for (JSONObject entityJSONObject : entityJSONObjects) {
					testrayComponent = TestrayFactory.newTestrayComponent(
						this, entityJSONObject);

					_testrayComponentsId.put(
						testrayComponent.getId(), testrayComponent);
					_testrayComponentsName.put(
						testrayComponent.getName(), testrayComponent);

					return testrayComponent;
				}
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}

			return null;
		}
	}

	public TestrayComponent getTestrayComponentByName(String componentName) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(componentName)) {
			return null;
		}

		synchronized (_testrayComponentsId) {
			TestrayComponent testrayComponent = _testrayComponentsName.get(
				componentName);

			if (testrayComponent != null) {
				return testrayComponent;
			}

			String filterString = JenkinsResultsParserUtil.combine(
				"name eq '", componentName,
				"' and r_projectToComponents_c_projectId eq '",
				String.valueOf(getId()), "'");

			try {
				Set<JSONObject> entityJSONObjects =
					_testrayServer.requestGraphQL(
						"components", TestrayComponent.FIELD_NAMES,
						filterString, null, 1, 1);

				for (JSONObject entityJSONObject : entityJSONObjects) {
					testrayComponent = TestrayFactory.newTestrayComponent(
						this, entityJSONObject);

					_testrayComponentsId.put(
						testrayComponent.getId(), testrayComponent);
					_testrayComponentsName.put(componentName, testrayComponent);

					return testrayComponent;
				}
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}

			return null;
		}
	}

	public TestrayProductVersion getTestrayProductVersionById(
		long productVersionId) {

		String filterString = JenkinsResultsParserUtil.combine(
			"id eq '", String.valueOf(productVersionId), "' and ",
			"r_projectToProductVersions_c_projectId eq '",
			String.valueOf(getId()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"productVersions", TestrayProductVersion.FIELD_NAMES,
				filterString, null, 1, 1);

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

		String filterString = JenkinsResultsParserUtil.combine(
			"name eq '", productVersionName, "' and ",
			"r_projectToProductVersions_c_projectId eq '",
			String.valueOf(getId()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"productVersions", TestrayProductVersion.FIELD_NAMES,
				filterString, null, 1, 1);

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

	public TestrayRoutine getTestrayRoutineById(long routineId) {
		TestrayRoutine testrayRoutine = _testrayServer.getTestrayRoutineById(
			routineId);

		if (testrayRoutine != null) {
			return testrayRoutine;
		}

		String filterString = JenkinsResultsParserUtil.combine(
			"id eq '", String.valueOf(routineId), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"routines", TestrayRoutine.FIELD_NAMES, filterString, null, 1,
				1);

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
		String filterString = JenkinsResultsParserUtil.combine(
			"name eq '", routineName, "' and ",
			"r_routineToProjects_c_projectId eq '", String.valueOf(getId()),
			"'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"routines", TestrayRoutine.FIELD_NAMES, filterString, null, 1,
				1);

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

	public TestrayTeam getTestrayTeamById(long componentId) {
		for (TestrayTeam testrayTeam : getTestrayTeams()) {
			if (componentId == testrayTeam.getId()) {
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

	public synchronized List<TestrayTeam> getTestrayTeams() {
		if (_testrayTeams != null) {
			return _testrayTeams;
		}

		_testrayTeams = new ArrayList<>();

		String filterString = JenkinsResultsParserUtil.combine(
			"r_projectToTeams_c_projectId eq '", String.valueOf(getId()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"teams", TestrayTeam.FIELD_NAMES, filterString, null);

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
					String.valueOf(getId()), "/routines"));
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

	private synchronized void _initTestrayCaseIds() {
		if (_testrayCases != null) {
			return;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Gathering test case IDs for project ", getName(), " at ",
				JenkinsResultsParserUtil.toDateString(new Date(start))));

		_testrayCases = new HashMap<>();

		String filterString = JenkinsResultsParserUtil.combine(
			"r_projectToCases_c_projectId eq '", String.valueOf(getId()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"cases", TestrayCase.FIELD_NAMES_CASE_IDS, filterString, null);

			for (JSONObject entityJSONObject : entityJSONObjects) {
				_testrayCaseIds.put(
					entityJSONObject.getString("name"),
					entityJSONObject.getLong("id"));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
		finally {
			long duration =
				JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Gathered test case IDs for project ", getName(), " in ",
					JenkinsResultsParserUtil.toDurationString(duration)));
		}
	}

	private synchronized void _initTestrayCases() {
		if (_testrayCases != null) {
			return;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Gathering test cases for project ", getName(), " at ",
				JenkinsResultsParserUtil.toDateString(new Date(start))));

		_testrayCases = new HashMap<>();

		String filterString = JenkinsResultsParserUtil.combine(
			"r_projectToCases_c_projectId eq '", String.valueOf(getId()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"cases", TestrayCase.FIELD_NAMES, filterString, null, 0, 50);

			for (JSONObject entityJSONObject : entityJSONObjects) {
				TestrayCase testrayCase = TestrayFactory.newTestrayCase(
					this, entityJSONObject);

				_testrayCases.put(testrayCase.getName(), testrayCase);
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
		finally {
			long duration =
				JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Gathered test cases for project ", getName(), " in ",
					JenkinsResultsParserUtil.toDurationString(duration)));
		}
	}

	private final JSONObject _jsonObject;
	private final Map<String, Long> _testrayCaseIds = new HashMap<>();
	private Map<String, TestrayCase> _testrayCases;
	private final Map<Long, TestrayComponent> _testrayComponentsId =
		new HashMap<>();
	private final Map<String, TestrayComponent> _testrayComponentsName =
		new HashMap<>();
	private final TestrayServer _testrayServer;
	private List<TestrayTeam> _testrayTeams;

}