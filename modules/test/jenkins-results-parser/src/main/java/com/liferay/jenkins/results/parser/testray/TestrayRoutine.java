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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayRoutine {

	public static final String[] FIELD_NAMES = {
		"dateCreated", "dateModified", "id", "name", "routineToProjects"
	};

	public TestrayBuild createTestrayBuild(
		TestrayProductVersion testrayProductVersion, String buildName) {

		TestrayBuild testrayBuild = getTestrayBuildByName(buildName);

		if (testrayBuild != null) {
			return testrayBuild;
		}

		return createTestrayBuild(
			testrayProductVersion, buildName, null, null, null);
	}

	public TestrayBuild createTestrayBuild(
		TestrayProductVersion testrayProductVersion, String buildName,
		Date buildDate, String buildDescription, String buildSHA) {

		TestrayBuild testrayBuild = getTestrayBuildByName(buildName);

		if (testrayBuild != null) {
			return testrayBuild;
		}

		JSONObject requestJSONObject = new JSONObject();

		if (buildDate == null) {
			buildDate = new Date();
		}

		if ((buildDescription != null) && (buildDescription.length() >= 280)) {
			buildDescription = buildDescription.substring(0, 280);
		}

		requestJSONObject.put(
			"description", buildDescription
		).put(
			"dueDate",
			JenkinsResultsParserUtil.toDateString(
				buildDate, "yyy-MM-dd'T'HH:mm:ss.SSS'Z'", "America/Los_Angeles")
		).put(
			"dueStatus", "ACTIVATED"
		).put(
			"gitHash", buildSHA
		).put(
			"name", buildName
		).put(
			"r_productVersionToBuilds_c_productVersionId",
			testrayProductVersion.getID()
		).put(
			"r_projectToBuilds_c_projectId", _testrayProject.getID()
		).put(
			"r_routineToBuilds_c_routineId", getID()
		);

		try {
			JSONObject responseJSONObject = new JSONObject(
				_testrayServer.requestPost(
					"/o/c/builds", requestJSONObject.toString()));

			return getTestrayBuildByID(responseJSONObject.getLong("id"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				requestJSONObject.toString(), ioException);
		}
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

	public TestrayBuild getTestrayBuildByID(long buildID) {
		TestrayBuild testrayBuild = _testrayServer.getTestrayBuildByID(buildID);

		if (testrayBuild != null) {
			return testrayBuild;
		}

		String filter = JenkinsResultsParserUtil.combine(
			"id eq '", String.valueOf(buildID), "' and ",
			"r_routineToBuilds_c_routineId eq '", String.valueOf(getID()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"builds", TestrayBuild.FIELD_NAMES, filter, null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			Iterator<JSONObject> iterator = entityJSONObjects.iterator();

			return TestrayFactory.newTestrayBuild(this, iterator.next());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public TestrayBuild getTestrayBuildByName(
		String buildName, String... names) {

		String filter = JenkinsResultsParserUtil.combine(
			"name eq '", buildName, "' and ",
			"r_routineToBuilds_c_routineId eq '", String.valueOf(getID()), "'");

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"builds", TestrayBuild.FIELD_NAMES, filter, null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			Iterator<JSONObject> iterator = entityJSONObjects.iterator();

			return TestrayFactory.newTestrayBuild(this, iterator.next());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public List<TestrayBuild> getTestrayBuilds() {
		return getTestrayBuilds(200);
	}

	public List<TestrayBuild> getTestrayBuilds(
		int maxSize, String... nameFilters) {

		List<TestrayBuild> testrayBuilds = new ArrayList<>();

		StringBuilder sb = new StringBuilder();

		sb.append("r_routineToBuilds_c_routineId eq '");
		sb.append(getID());
		sb.append("'");

		if ((nameFilters != null) && (nameFilters.length > 0)) {
			sb.append(" and (");

			for (int i = 0; i < nameFilters.length; i++) {
				sb.append("contains(name, '");
				sb.append(nameFilters[i]);
				sb.append("')");

				if (i < (nameFilters.length - 1)) {
					sb.append(" or ");
				}
			}

			sb.append(")");
		}

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"builds", TestrayBuild.FIELD_NAMES, sb.toString(),
				"dateCreated:desc", maxSize, 0);

			for (JSONObject entityJSONObject : entityJSONObjects) {
				testrayBuilds.add(
					TestrayFactory.newTestrayBuild(this, entityJSONObject));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return testrayBuilds;
	}

	public TestrayProject getTestrayProject() {
		if (_testrayProject != null) {
			return _testrayProject;
		}

		JSONObject projectJSONObject = _jsonObject.getJSONObject(
			"routineToProjects");

		_testrayProject = _testrayServer.getTestrayProjectByID(
			projectJSONObject.getLong("id"));

		return _testrayProject;
	}

	public TestrayServer getTestrayServer() {
		return _testrayServer;
	}

	public URL getURL() {
		if (url != null) {
			return url;
		}

		try {
			url = new URL(
				JenkinsResultsParserUtil.combine(
					String.valueOf(_testrayProject.getURL()), "/",
					String.valueOf(getID())));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}

		return url;
	}

	public void setJSONObject(JSONObject jsonObject) {
		_jsonObject = jsonObject;
	}

	protected TestrayRoutine(
		TestrayProject testrayProject, JSONObject jsonObject) {

		_testrayProject = testrayProject;

		_testrayServer = testrayProject.getTestrayServer();

		_jsonObject = jsonObject;
	}

	protected TestrayRoutine(
		TestrayServer testrayServer, JSONObject jsonObject) {

		_testrayServer = testrayServer;
		_jsonObject = jsonObject;
	}

	protected TestrayRoutine(URL url) {
		setURL(url);
	}

	protected void setTestrayProject(TestrayProject testrayProject) {
		_testrayProject = testrayProject;
	}

	protected void setTestrayServer(TestrayServer testrayServer) {
		_testrayServer = testrayServer;
	}

	protected void setURL(URL url) {
		this.url = url;

		Matcher matcher = _testrayRoutineURLPattern.matcher(url.toString());

		if (!matcher.find()) {
			throw new RuntimeException("Invalid routine URL " + url);
		}

		TestrayServer testrayServer = TestrayFactory.newTestrayServer(
			matcher.group("serverURL"));

		setTestrayServer(testrayServer);

		String filter = JenkinsResultsParserUtil.combine(
			"id eq '", matcher.group("routineID"), "'");

		try {
			Set<JSONObject> entityJSONObjects = testrayServer.requestGraphQL(
				"routines", TestrayRoutine.FIELD_NAMES, filter, null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return;
			}

			Iterator<JSONObject> iterator = entityJSONObjects.iterator();

			setJSONObject(iterator.next());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected URL url;

	private static final Pattern _testrayRoutineURLPattern = Pattern.compile(
		"(?<serverURL>https://[^/]+)/#/project/(?<projectID>\\d+)/routines/" +
			"(?<routineID>\\d+)");

	private JSONObject _jsonObject;
	private TestrayProject _testrayProject;
	private TestrayServer _testrayServer;

}