/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.NotificationUtil;
import com.liferay.jenkins.results.parser.TestrayResultsParserUtil;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayServer {

	public TestrayProject createTestrayProject(String projectName) {
		TestrayProject testrayProject = getTestrayProjectByName(projectName);

		if (testrayProject != null) {
			return testrayProject;
		}

		JSONObject requestJSONObject = new JSONObject();

		requestJSONObject.put("name", projectName);

		try {
			JSONObject responseJSONObject = new JSONObject(
				requestPost("/o/c/projects", requestJSONObject.toString()));

			return getTestrayProjectByID(responseJSONObject.getLong("id"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				requestJSONObject.toString(), ioException);
		}
	}

	public JenkinsResultsParserUtil.HTTPAuthorization getHTTPAuthorization() {
		return _httpAuthorization;
	}

	public TestrayBuild getTestrayBuildByID(long buildID) {
		if (_testrayBuilds.containsKey(buildID)) {
			return _testrayBuilds.get(buildID);
		}

		try {
			List<JSONObject> entityJSONObjects = requestGraphQL(
				"builds", TestrayBuild.FIELD_NAMES, "id eq '" + buildID + "'",
				null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			JSONObject entityJSONObject = entityJSONObjects.get(0);

			JSONObject projectJSONObject = entityJSONObject.getJSONObject(
				"projectToBuilds");

			TestrayProject testrayProject = getTestrayProjectByID(
				projectJSONObject.getLong("id"));

			JSONObject routineJSONObject = entityJSONObject.getJSONObject(
				"routineToBuilds");

			TestrayRoutine testrayRoutine =
				testrayProject.getTestrayRoutineByID(
					routineJSONObject.getLong("id"));

			TestrayBuild testrayBuild = TestrayFactory.newTestrayBuild(
				testrayRoutine, entityJSONObject);

			_testrayBuilds.put(testrayBuild.getID(), testrayBuild);

			return testrayBuild;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public TestrayCaseType getTestrayCaseTypeByID(long testrayCaseTypeID) {
		TestrayCaseType testrayCaseType = _testrayCaseTypesID.get(
			testrayCaseTypeID);

		if (testrayCaseType != null) {
			return testrayCaseType;
		}

		try {
			List<JSONObject> entityJSONObjects = requestGraphQL(
				"caseTypes", TestrayCaseType.FIELD_NAMES,
				"id eq '" + testrayCaseTypeID + "'", null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			testrayCaseType = TestrayFactory.newTestrayCaseType(
				this, entityJSONObjects.get(0));

			_testrayCaseTypesID.put(testrayCaseType.getID(), testrayCaseType);
			_testrayCaseTypesName.put(
				testrayCaseType.getName(), testrayCaseType);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return _testrayCaseTypesID.get(testrayCaseTypeID);
	}

	public TestrayCaseType getTestrayCaseTypeByName(
		String testrayCaseTypeName) {

		TestrayCaseType testrayCaseType = _testrayCaseTypesName.get(
			testrayCaseTypeName);

		if (testrayCaseType != null) {
			return testrayCaseType;
		}

		try {
			List<JSONObject> entityJSONObjects = requestGraphQL(
				"caseTypes", TestrayCaseType.FIELD_NAMES,
				"name eq '" + testrayCaseTypeName + "'", null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			testrayCaseType = TestrayFactory.newTestrayCaseType(
				this, entityJSONObjects.get(0));

			_testrayCaseTypesID.put(testrayCaseType.getID(), testrayCaseType);
			_testrayCaseTypesName.put(
				testrayCaseType.getName(), testrayCaseType);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return _testrayCaseTypesName.get(testrayCaseTypeName);
	}

	public TestrayProject getTestrayProjectByID(long projectID) {
		if (_testrayProjects.containsKey(projectID)) {
			return _testrayProjects.get(projectID);
		}

		try {
			List<JSONObject> entityJSONObjects = requestGraphQL(
				"projects", TestrayProject.FIELD_NAMES,
				"id eq '" + projectID + "'", null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			TestrayProject testrayProject = TestrayFactory.newTestrayProject(
				this, entityJSONObjects.get(0));

			_testrayProjects.put(testrayProject.getID(), testrayProject);

			return testrayProject;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public TestrayProject getTestrayProjectByName(String projectName) {
		for (TestrayProject testrayProject : _testrayProjects.values()) {
			if (Objects.equals(testrayProject.getName(), projectName)) {
				return testrayProject;
			}
		}

		try {
			List<JSONObject> entityJSONObjects = requestGraphQL(
				"projects", TestrayProject.FIELD_NAMES,
				"name eq '" + projectName + "'", null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			TestrayProject testrayProject = TestrayFactory.newTestrayProject(
				this, entityJSONObjects.get(0));

			_testrayProjects.put(testrayProject.getID(), testrayProject);

			return testrayProject;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public List<TestrayProject> getTestrayProjects() {
		List<TestrayProject> testrayProjects = new ArrayList<>();

		try {
			for (JSONObject entityJSONObject :
					requestGraphQL(
						"projects", TestrayProject.FIELD_NAMES, null, null)) {

				TestrayProject testrayProject =
					TestrayFactory.newTestrayProject(this, entityJSONObject);

				_testrayProjects.put(testrayProject.getID(), testrayProject);

				testrayProjects.add(testrayProject);
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return testrayProjects;
	}

	public TestrayRoutine getTestrayRoutineByID(long routineId) {
		if (_testrayRoutines.containsKey(routineId)) {
			return _testrayRoutines.get(routineId);
		}

		try {
			List<JSONObject> entityJSONObjects = requestGraphQL(
				"routines", TestrayRoutine.FIELD_NAMES,
				"id eq '" + routineId + "'", null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			JSONObject entityJSONObject = entityJSONObjects.get(0);

			JSONObject projectJSONObject = entityJSONObject.getJSONObject(
				"routineToProjects");

			TestrayProject testrayProject = getTestrayProjectByID(
				projectJSONObject.getLong("id"));

			TestrayRoutine testrayRoutine = TestrayFactory.newTestrayRoutine(
				testrayProject, entityJSONObject);

			_testrayRoutines.put(testrayRoutine.getID(), testrayRoutine);

			return testrayRoutine;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public URL getURL() {
		return _url;
	}

	public void importCaseResults(TopLevelBuildReport topLevelBuildReport) {
		TestrayResultsParserUtil.processTestrayResultFiles(getResultsDir());

		if (TestrayS3Bucket.hasGoogleApplicationCredentials()) {
			_importCaseResultsToGCP(topLevelBuildReport);
		}
	}

	public String requestGet(String urlPath) throws IOException {
		try {
			return JenkinsResultsParserUtil.toString(
				getTestrayURL(urlPath), true, 2,
				JenkinsResultsParserUtil.HttpRequestMethod.GET, null, 5,
				_MILLIS_REQUEST_TIMEOUT_DEFAULT, getHTTPAuthorization(), true);
		}
		catch (IOException ioException) {
			_sendCommunicationFailureNotification(ioException.getMessage());

			throw ioException;
		}
	}

	public String requestPost(
			boolean checkCache, String urlPath, String requestData)
		throws IOException {

		try {
			return JenkinsResultsParserUtil.toString(
				getTestrayURL(urlPath), checkCache, 2,
				JenkinsResultsParserUtil.HttpRequestMethod.POST, requestData, 5,
				_MILLIS_REQUEST_TIMEOUT_DEFAULT, getHTTPAuthorization(), false);
		}
		catch (IOException ioException) {
			_sendCommunicationFailureNotification(ioException.getMessage());

			throw ioException;
		}
	}

	public String requestPost(String urlPath, String requestData)
		throws IOException {

		return requestPost(false, urlPath, requestData);
	}

	public void setHTTPAuthorization(
		JenkinsResultsParserUtil.HTTPAuthorization httpAuthorization) {

		_httpAuthorization = httpAuthorization;
	}

	public void writeCaseResult(String fileName, String fileContent) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(fileName) ||
			JenkinsResultsParserUtil.isNullOrEmpty(fileContent)) {

			return;
		}

		try {
			JenkinsResultsParserUtil.write(
				new File(getResultsDir(), fileName), fileContent);
		}
		catch (IOException ioException) {
		}
	}

	protected TestrayServer(String urlString) {
		try {
			Matcher matcher = _urlPattern.matcher(urlString);

			if (matcher.find()) {
				urlString = matcher.group("url");
			}

			_url = new URL(urlString);
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(
				"Invalid Testray server URL " + urlString,
				malformedURLException);
		}
	}

	protected File getResultsDir() {
		String workspace = System.getenv("WORKSPACE");

		if (JenkinsResultsParserUtil.isNullOrEmpty(workspace)) {
			throw new RuntimeException("Please set WORKSPACE");
		}

		return new File(workspace, "testray/results");
	}

	protected String getTestrayURL(String urlPath) {
		Matcher matcher = _urlPathPattern.matcher(urlPath);

		if (matcher.find()) {
			urlPath = matcher.group("urlPath");
		}

		return getURL() + "/" + urlPath;
	}

	protected List<JSONObject> requestGraphQL(
			boolean checkCache, String entityName, String[] entityFields,
			String filter, String sort, long maxCount, int pageSize)
		throws IOException {

		if (maxCount <= 0) {
			maxCount = Long.MAX_VALUE;
		}

		if (pageSize <= 0) {
			pageSize = 200;
		}

		if (pageSize >= maxCount) {
			pageSize = (int)maxCount;
		}

		List<JSONObject> entityJSONObjects = new ArrayList<>();

		int page = 0;

		while (true) {
			page++;

			StringBuilder sb = new StringBuilder();

			sb.append("{");
			sb.append("c {");

			sb.append(entityName);
			sb.append(" (page: ");
			sb.append(page);
			sb.append(", pageSize: ");
			sb.append(pageSize);

			if (!JenkinsResultsParserUtil.isNullOrEmpty(filter)) {
				sb.append(", filter: \"");
				sb.append(filter);
				sb.append("\"");
			}

			if (!JenkinsResultsParserUtil.isNullOrEmpty(sort)) {
				sb.append(", sort: \"");
				sb.append(sort);
				sb.append("\"");
			}

			sb.append(") {items {");

			for (String entityField : entityFields) {
				sb.append(entityField);
				sb.append(" ");
			}

			sb.append("} page pageSize lastPage}}}");

			JSONObject requestJSONObject = new JSONObject();

			requestJSONObject.put("query", sb.toString());

			long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

			JSONObject responseJSONObject = new JSONObject(
				requestPost(
					checkCache, "/o/graphql", requestJSONObject.toString()));

			long duration =
				JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

			if (duration > 180000) {
				_sendCommunicationFailureNotification(
					"Slow response time: Testray GraphQL query took " +
						duration + " ms");
			}

			System.out.println(
				JenkinsResultsParserUtil.combine(
					String.valueOf(getURL()), "/o/graphql query: ",
					sb.toString(), " in ",
					JenkinsResultsParserUtil.toDurationString(duration)));

			try {
				JSONObject dataJSONObject = responseJSONObject.getJSONObject(
					"data");

				JSONObject cJSONObject = dataJSONObject.getJSONObject("c");

				JSONObject entityJSONObject = cJSONObject.getJSONObject(
					entityName);

				int lastPage = entityJSONObject.getInt("lastPage");

				JSONArray itemsJSONArray = entityJSONObject.getJSONArray(
					"items");

				for (int i = 0; i < itemsJSONArray.length(); i++) {
					entityJSONObjects.add(itemsJSONArray.getJSONObject(i));
				}

				if ((page == lastPage) ||
					(entityJSONObjects.size() >= maxCount)) {

					break;
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(
					responseJSONObject.toString(), exception);
			}
		}

		return entityJSONObjects;
	}

	protected List<JSONObject> requestGraphQL(
			String entityName, String[] entityFields, String filter,
			String sort)
		throws IOException {

		return requestGraphQL(entityName, entityFields, filter, sort, 0, 0);
	}

	protected List<JSONObject> requestGraphQL(
			String entityName, String[] entityFields, String filter,
			String sort, long maxCount, int pageSize)
		throws IOException {

		return requestGraphQL(
			false, entityName, entityFields, filter, sort, maxCount, pageSize);
	}

	private void _importCaseResultsToGCP(
		TopLevelBuildReport topLevelBuildReport) {

		if (!TestrayS3Bucket.hasGoogleApplicationCredentials()) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		JenkinsMaster jenkinsMaster = topLevelBuildReport.getJenkinsMaster();

		sb.append(jenkinsMaster.getName());

		sb.append("-");

		String jobName = topLevelBuildReport.getJobName();

		sb.append(jobName.replaceAll("[\\(\\)]", "_"));

		sb.append("-");
		sb.append(topLevelBuildReport.getBuildNumber());
		sb.append("-results.tar.gz");

		File resultsDir = getResultsDir();

		File gcpResultsDir = new File(
			resultsDir.getParentFile(), "gcp-results");

		try {
			JenkinsResultsParserUtil.copy(resultsDir, gcpResultsDir);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		TestrayS3Bucket testrayS3Bucket = TestrayS3Bucket.getInstance();

		for (File gcpResultFile :
				JenkinsResultsParserUtil.findFiles(gcpResultsDir, ".*.xml")) {

			try {
				Document document = Dom4JUtil.parse(
					JenkinsResultsParserUtil.read(gcpResultFile));

				Element rootElement = document.getRootElement();

				for (Element testcaseElement :
						rootElement.elements("testcase")) {

					Element propertiesElement = testcaseElement.element(
						"properties");

					for (Element propertyElement :
							propertiesElement.elements("property")) {

						String propertyName = propertyElement.attributeValue(
							"name");

						if ((propertyName == null) ||
							!propertyName.equals("testray.testcase.warnings")) {

							continue;
						}

						for (Element element : propertyElement.elements()) {
							propertyElement.remove(element);
						}
					}
				}

				String gcpResultFileContent = Dom4JUtil.format(
					rootElement, false);

				gcpResultFileContent = gcpResultFileContent.replaceAll(
					"(<property name=\"testray.testcase.warnings\" " +
						"value=\"\\d+\")>\\s+<\\/property>",
					"$1/>");
				gcpResultFileContent = gcpResultFileContent.replaceAll(
					getURL() + "/?reports/production/logs",
					testrayS3Bucket.getTestrayS3BaseURL());

				JenkinsResultsParserUtil.write(
					gcpResultFile, gcpResultFileContent);
			}
			catch (DocumentException | IOException exception) {
			}
		}

		File resultsTarGzFile = new File(
			gcpResultsDir.getParentFile(), sb.toString());

		JenkinsResultsParserUtil.tarGzip(gcpResultsDir, resultsTarGzFile);

		testrayS3Bucket.createTestrayS3Object(
			"inbox/" + resultsTarGzFile.getName(), resultsTarGzFile);
	}

	private void _sendCommunicationFailureNotification(String message) {
		StringBuilder sb = new StringBuilder();

		sb.append(message);
		sb.append("\n");
		sb.append(System.getenv("TOP_LEVEL_BUILD_URL"));

		NotificationUtil.sendSlackNotification(
			sb.toString(), "#ci-notifications", ":testray:",
			"Testray communication failure", "Liferay CI");
	}

	private static final int _MILLIS_REQUEST_TIMEOUT_DEFAULT = 60000;

	private static final Map<Long, TestrayBuild> _testrayBuilds =
		new HashMap<>();
	private static final Map<Long, TestrayProject> _testrayProjects =
		new HashMap<>();
	private static final Map<Long, TestrayRoutine> _testrayRoutines =
		new HashMap<>();
	private static final Pattern _urlPathPattern = Pattern.compile(
		"/+(?<urlPath>.*)");
	private static final Pattern _urlPattern = Pattern.compile(
		"(?<url>https?://.*)/+");

	private JenkinsResultsParserUtil.HTTPAuthorization _httpAuthorization;
	private final Map<Long, TestrayCaseType> _testrayCaseTypesID =
		new HashMap<>();
	private final Map<String, TestrayCaseType> _testrayCaseTypesName =
		new HashMap<>();
	private final URL _url;

}