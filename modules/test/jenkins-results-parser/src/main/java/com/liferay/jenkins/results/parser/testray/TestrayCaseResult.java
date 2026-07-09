/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Retryable;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import org.dom4j.Element;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class TestrayCaseResult {

	public static final String[] FIELD_NAMES = {
		"attachments", "buildToCaseResult", "caseToCaseResult",
		"componentToCaseResult", "dateCreated", "dateModified",
		"dueStatus { key name }", "errors", "id", "startDate"
	};

	public TestrayAttachment getBuildResultTestrayAttachment() {
		initTestrayAttachments();

		return testrayAttachments.get("Build Result (Top Level)");
	}

	public String getCaseId() {
		TestrayComponent testrayComponent = getTestrayComponent();

		if (testrayComponent == null) {
			return null;
		}

		return String.valueOf(testrayComponent.getId());
	}

	public String getComponentName() {
		TestrayComponent testrayComponent = getTestrayComponent();

		if (testrayComponent == null) {
			return null;
		}

		return testrayComponent.getName();
	}

	public long getDuration() {
		return _jsonObject.optLong("duration");
	}

	public String getErrors() {
		return _jsonObject.optString("errors");
	}

	public ErrorType getErrorType() {
		if (_errorType != null) {
			return _errorType;
		}

		for (String didNotRunErrors : _DID_NOT_RUN_ERRORS) {
			String errors = getErrors();

			if (errors.contains(didNotRunErrors)) {
				_errorType = ErrorType.DID_NOT_RUN;

				return _errorType;
			}
		}

		for (TestrayCaseResult previousTestrayCaseResult :
				getTestrayCaseResultHistory(5, 5)) {

			if (Objects.equals(getId(), previousTestrayCaseResult.getId())) {
				continue;
			}

			if (_isSimilarError(previousTestrayCaseResult)) {
				_errorType = ErrorType.COMMON;

				return _errorType;
			}
		}

		_errorType = ErrorType.UNIQUE;

		return _errorType;
	}

	public URL getHistoryURL() {
		try {
			return new URL(getURL() + "/history");
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	public long getId() {
		return _jsonObject.optLong("id");
	}

	public String getIssues() {
		return null;
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	public String getName() {
		TestrayCase testrayCase = getTestrayCase();

		if (testrayCase == null) {
			return null;
		}

		return testrayCase.getName();
	}

	public TestrayCaseResult getParentTestrayCaseResult() {
		return _parentTestrayCaseResult;
	}

	public String getPortalSHA() {
		TestrayBuild testrayBuild = getTestrayBuild();

		return testrayBuild.getPortalSHA();
	}

	public int getPriority() {
		TestrayCase testrayCase = getTestrayCase();

		if (testrayCase == null) {
			return 0;
		}

		return testrayCase.getPriority();
	}

	public String getPullRequestSenderUsername() {
		TestrayBuild testrayBuild = getTestrayBuild();

		return testrayBuild.getPullRequestSenderUsername();
	}

	public Status getStatus() {
		JSONObject dueStatusJSONObject = _jsonObject.getJSONObject("dueStatus");

		return Status.valueOf(dueStatusJSONObject.getString("key"));
	}

	public String getSubcomponentNames() {
		return "";
	}

	public synchronized String getTeamName() {
		if (_testrayComponent == null) {
			return null;
		}

		TestrayTeam testrayTeam = _testrayComponent.getTestrayTeam();

		return testrayTeam.getName();
	}

	public List<TestrayAttachment> getTestrayAttachments() {
		initTestrayAttachments();

		return new ArrayList<>(testrayAttachments.values());
	}

	public synchronized TestrayBuild getTestrayBuild() {
		if (_testrayBuild != null) {
			return _testrayBuild;
		}

		JSONObject buildJSONObject = _jsonObject.getJSONObject(
			"buildToCaseResult");

		if (buildJSONObject != null) {
			_testrayBuild = TestrayFactory.newTestrayBuild(
				_testrayServer, buildJSONObject.getLong("id"));
		}

		return _testrayBuild;
	}

	public synchronized TestrayCase getTestrayCase() {
		if (_testrayCase != null) {
			return _testrayCase;
		}

		if (_testrayCaseCached) {
			return null;
		}

		_testrayCaseCached = true;

		try {
			if (_jsonObject != null) {
				JSONObject caseJSONObject = _jsonObject.optJSONObject(
					"caseToCaseResult");

				if (caseJSONObject != null) {
					TestrayBuild testrayBuild = getTestrayBuild();

					_testrayCase = TestrayFactory.newTestrayCase(
						testrayBuild.getTestrayProject(), caseJSONObject);
				}
			}

			if (_testrayCase == null) {
				_testrayCase = _fetchTestrayCase();
			}

			return _testrayCase;
		}
		finally {
			_testrayCaseCached = false;
		}
	}

	public List<TestrayCaseResult> getTestrayCaseResultHistory(
		int maxCount, int pageSize) {

		List<TestrayCaseResult> testrayCaseResults = new ArrayList<>();

		StringBuilder sb = new StringBuilder();

		TestrayCase testrayCase = getTestrayCase();

		sb.append("r_caseToCaseResult_c_caseId eq '");
		sb.append(testrayCase.getId());
		sb.append("'");

		TestrayServer testrayServer = getTestrayServer();

		try {
			Set<JSONObject> entityJSONObjects = testrayServer.requestGraphQL(
				"caseResults", TestrayCaseResult.FIELD_NAMES, sb.toString(),
				"dateCreated:desc", maxCount, pageSize);

			for (JSONObject entityJSONObject : entityJSONObjects) {
				TestrayCaseResult testrayCaseResult =
					TestrayFactory.newJSONObjectTestrayCaseResult(
						testrayServer, entityJSONObject);

				if (!Objects.equals(
						testrayCaseResult.getPortalSHA(), getPortalSHA()) &&
					!Objects.equals(
						testrayCaseResult.getPullRequestSenderUsername(),
						getPullRequestSenderUsername())) {

					testrayCaseResults.add(testrayCaseResult);
				}
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return testrayCaseResults;
	}

	public synchronized URL getTestrayCaseResultURL() {
		if (_testrayCaseResultURL != null) {
			return _testrayCaseResultURL;
		}

		URL cachedTestrayCaseResultURL = _fetchTestrayCaseResultURL();

		if (cachedTestrayCaseResultURL != null) {
			_testrayCaseResultURL = cachedTestrayCaseResultURL;

			return _testrayCaseResultURL;
		}

		_testrayCaseResultURL = _createTestrayCaseResultURL();

		return _testrayCaseResultURL;
	}

	public synchronized TestrayComponent getTestrayComponent() {
		if (_testrayComponent != null) {
			return _testrayComponent;
		}

		if (_testrayComponentCached) {
			return null;
		}

		_testrayComponentCached = true;

		try {
			TestrayBuild testrayBuild = getTestrayBuild();

			if (testrayBuild == null) {
				return null;
			}

			TestrayProject testrayProject = testrayBuild.getTestrayProject();

			JSONObject componentJSONObject = null;

			if (_jsonObject != null) {
				componentJSONObject = _jsonObject.optJSONObject(
					"componentToCaseResult");
			}

			if (componentJSONObject != null) {
				_testrayComponent = testrayProject.getTestrayComponentById(
					componentJSONObject.getLong("id"));
			}
			else {
				_testrayComponent = testrayProject.getTestrayComponentByName(
					getComponentName());
			}

			return _testrayComponent;
		}
		finally {
			_testrayComponentCached = false;
		}
	}

	public TestrayProject getTestrayProject() {
		TestrayBuild testrayBuild = getTestrayBuild();

		return testrayBuild.getTestrayProject();
	}

	public TestrayRun getTestrayRun() {
		return _testrayRun;
	}

	public TestrayServer getTestrayServer() {
		return _testrayServer;
	}

	public TestrayTeam getTestrayTeam() {
		if (_testrayTeam != null) {
			return _testrayTeam;
		}

		String teamName = getTeamName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(teamName)) {
			return null;
		}

		TestrayBuild testrayBuild = getTestrayBuild();

		if (testrayBuild == null) {
			return null;
		}

		TestrayProject testrayProject = testrayBuild.getTestrayProject();

		_testrayTeam = testrayProject.getTestrayTeamByName(teamName);

		return _testrayTeam;
	}

	public String getType() {
		TestrayCase testrayCase = getTestrayCase();

		if (testrayCase == null) {
			return null;
		}

		return testrayCase.getType();
	}

	public URL getURL() {
		TestrayBuild testrayBuild = getTestrayBuild();

		try {
			return new URL(testrayBuild.getURL() + "/case-result/" + getId());
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	public String[] getWarnings() {
		return null;
	}

	public void setParentTestrayCaseResult(
		TestrayCaseResult parentTestrayCaseResult) {

		_parentTestrayCaseResult = parentTestrayCaseResult;
	}

	public void setTestrayCase(TestrayCase testrayCase) {
		_testrayCase = testrayCase;
	}

	public void setTestrayComponent(TestrayComponent testrayComponent) {
		_testrayComponent = testrayComponent;
	}

	public void setTestrayRun(TestrayRun testrayRun) {
		_testrayRun = testrayRun;
	}

	public static enum ErrorType {

		COMMON("Common"), DID_NOT_RUN("Did not run"), UNIQUE("Unique");

		@Override
		public String toString() {
			return _name;
		}

		private ErrorType(String name) {
			_name = name;
		}

		private final String _name;

	}

	public static enum Status {

		BLOCKED(4, "blocked"), DIDNOTRUN(6, "dnr"), FAILED(3, "failed"),
		INCOMPLETE(1, "incomplete"), INPROGRESS(1, "in-progress"),
		PASSED(2, "passed"), TESTFIX(7, "test-fix"), UNTESTED(1, "untested");

		public static Status get(Integer id) {
			return _statuses.get(id);
		}

		public static List<Status> getFailedStatuses() {
			return Arrays.asList(
				BLOCKED, DIDNOTRUN, FAILED, INPROGRESS, TESTFIX, UNTESTED);
		}

		public Integer getId() {
			return _id;
		}

		public String getName() {
			return _name;
		}

		private Status(Integer id, String name) {
			_id = id;
			_name = name;
		}

		private static Map<Integer, Status> _statuses = new HashMap<>();

		static {
			for (Status status : values()) {
				_statuses.put(status.getId(), status);
			}
		}

		private final Integer _id;
		private final String _name;

	}

	protected TestrayCaseResult(
		TestrayBuild testrayBuild, JSONObject jsonObject) {

		_testrayBuild = testrayBuild;
		_jsonObject = jsonObject;

		_testrayServer = testrayBuild.getTestrayServer();
	}

	protected TestrayCaseResult(
		TestrayServer testrayServer, JSONObject jsonObject) {

		_testrayServer = testrayServer;
		_jsonObject = jsonObject;
	}

	protected void addPropertyElements(
		Element propertiesElement, Map<String, String> propertiesMap) {

		for (Map.Entry<String, String> propertyEntry :
				propertiesMap.entrySet()) {

			Element propertyElement = propertiesElement.addElement("property");

			String propertyName = propertyEntry.getKey();
			String propertyValue = propertyEntry.getValue();

			if (JenkinsResultsParserUtil.isNullOrEmpty(propertyName) ||
				JenkinsResultsParserUtil.isNullOrEmpty(propertyValue)) {

				continue;
			}

			propertyElement.addAttribute("name", propertyName);
			propertyElement.addAttribute("value", propertyValue);
		}
	}

	protected void cacheTestrayCaseResultURL() {
		getTestrayCaseResultURL();
	}

	protected synchronized void initTestrayAttachments() {
		if (testrayAttachments != null) {
			return;
		}

		testrayAttachments = new TreeMap<>();

		String attachments = _jsonObject.getString("attachments");

		if (JenkinsResultsParserUtil.isNullOrEmpty(attachments)) {
			return;
		}

		JSONArray attachmentsJSONArray;

		try {
			attachmentsJSONArray = new JSONArray(attachments);
		}
		catch (JSONException jsonException) {
			return;
		}

		for (int i = 0; i < attachmentsJSONArray.length(); i++) {
			JSONObject attachmentJSONObject =
				attachmentsJSONArray.getJSONObject(i);

			URL url;

			try {
				url = new URL(attachmentJSONObject.getString("url"));
			}
			catch (MalformedURLException malformedURLException) {
				url = null;
			}

			TestrayAttachment testrayAttachment =
				TestrayFactory.newTestrayAttachment(
					this, attachmentJSONObject.getString("name"),
					attachmentJSONObject.getString("value"), url);

			testrayAttachments.put(
				testrayAttachment.getName(), testrayAttachment);
		}
	}

	protected Map<String, TestrayAttachment> testrayAttachments;

	private synchronized URL _createTestrayCaseResultURL() {
		final TestrayBuild testrayBuild = getTestrayBuild();

		if (testrayBuild == null) {
			return null;
		}

		TestrayCase testrayCase = getTestrayCase();

		if (testrayCase == null) {
			return null;
		}

		final long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		final JSONObject requestJSONObject = new JSONObject();

		JSONArray testrayAttachmentsJSONArray = new JSONArray();

		for (TestrayAttachment testrayAttachment : getTestrayAttachments()) {
			testrayAttachmentsJSONArray.put(testrayAttachment.getJSONObject());
		}

		if (!testrayAttachmentsJSONArray.isEmpty()) {
			requestJSONObject.put(
				"attachments", String.valueOf(testrayAttachmentsJSONArray));
		}

		Status status = getStatus();

		if (status != null) {
			requestJSONObject.put("dueStatus", status.toString());
		}

		long duration = getDuration();

		if (duration > 0) {
			requestJSONObject.put("duration", duration);
		}

		String errors = getErrors();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(errors)) {
			requestJSONObject.put("errors", errors);
		}

		requestJSONObject.put(
			"r_buildToCaseResult_c_buildId", testrayBuild.getId()
		).put(
			"r_caseToCaseResult_c_caseId", testrayCase.getId()
		);

		TestrayComponent testrayComponent = getTestrayComponent();

		if (testrayComponent != null) {
			requestJSONObject.put(
				"r_componentToCaseResult_c_componentId",
				testrayComponent.getId());
		}

		if (_testrayRun != null) {
			requestJSONObject.put(
				"r_runToCaseResult_c_runId", _testrayRun.getId());
		}

		TestrayTeam testrayTeam = getTestrayTeam();

		if (testrayTeam != null) {
			requestJSONObject.put(
				"r_teamToCaseResult_c_teamId", testrayTeam.getId());
		}

		Retryable<URL> retryable = new Retryable<URL>(true, 3, 5, true) {

			@Override
			public URL execute() {
				URL cachedTestrayCaseResultURL = _fetchTestrayCaseResultURL();

				if (cachedTestrayCaseResultURL != null) {
					return cachedTestrayCaseResultURL;
				}

				try {
					JSONObject responseJSONObject = new JSONObject(
						_testrayServer.requestPost(
							"/o/c/caseresults", requestJSONObject.toString()));

					URL testrayCaseResultURL = new URL(
						JenkinsResultsParserUtil.combine(
							String.valueOf(testrayBuild.getURL()),
							"/case-result/",
							String.valueOf(responseJSONObject.getLong("id"))));

					long end = JenkinsResultsParserUtil.getCurrentTimeMillis();

					System.out.println(
						JenkinsResultsParserUtil.combine(
							"Testray Case Result '", getName(), "' ",
							String.valueOf(testrayCaseResultURL),
							" created in ",
							JenkinsResultsParserUtil.toDurationString(
								end - start)));

					return testrayCaseResultURL;
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}

		};

		return retryable.executeWithRetries();
	}

	private TestrayCase _fetchTestrayCase() {
		String name = getName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(name)) {
			return null;
		}

		String type = getType();

		if (JenkinsResultsParserUtil.isNullOrEmpty(type)) {
			return null;
		}

		TestrayServer testrayServer = getTestrayServer();

		TestrayCaseType testrayCaseType =
			testrayServer.getTestrayCaseTypeByName(type);

		if (testrayCaseType == null) {
			return null;
		}

		TestrayProject testrayProject = getTestrayProject();

		if (testrayProject == null) {
			return null;
		}

		return TestrayFactory.newTestrayCase(
			testrayProject, name, testrayCaseType);
	}

	private URL _fetchTestrayCaseResultURL() {
		TestrayBuild testrayBuild = getTestrayBuild();

		if (testrayBuild == null) {
			return null;
		}

		TestrayCase testrayCase = getTestrayCase();

		if (testrayCase == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("r_buildToCaseResult_c_buildId eq '");
		sb.append(testrayBuild.getId());
		sb.append("' and r_caseToCaseResult_c_caseId eq '");
		sb.append(testrayCase.getId());
		sb.append("'");

		TestrayComponent testrayComponent = getTestrayComponent();

		if (testrayComponent != null) {
			sb.append(" and r_componentToCaseResult_c_componentId eq '");
			sb.append(testrayComponent.getId());
			sb.append("'");
		}

		TestrayRun testrayRun = getTestrayRun();

		if (testrayRun != null) {
			sb.append(" and r_runToCaseResult_c_runId eq '");
			sb.append(testrayRun.getId());
			sb.append("'");
		}

		TestrayTeam testrayTeam = getTestrayTeam();

		if (testrayTeam != null) {
			sb.append(" and r_teamToCaseResult_c_teamId eq '");
			sb.append(testrayTeam.getId());
			sb.append("'");
		}

		try {
			Set<JSONObject> entityJSONObjects = _testrayServer.requestGraphQL(
				"caseResults", FIELD_NAMES, sb.toString(), null, 1, 1);

			if (entityJSONObjects.isEmpty()) {
				return null;
			}

			for (JSONObject entityJSONObject : entityJSONObjects) {
				if (entityJSONObject == null) {
					continue;
				}

				return new URL(
					testrayBuild.getURL() + "/case-result/" +
						entityJSONObject.getLong("id"));
			}

			return null;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private boolean _isSimilarError(
		TestrayCaseResult previousTestrayCaseResult) {

		String thisErrors = getErrors();

		String previousErrors = previousTestrayCaseResult.getErrors();

		try {
			double jaroWinklerDistance = StringUtils.getJaroWinklerDistance(
				thisErrors, previousErrors);

			if (jaroWinklerDistance > _MAX_JARO_WINKLER_DISTANCE) {
				return true;
			}

			return false;
		}
		catch (IllegalArgumentException illegalArgumentException) {
			return Objects.equals(thisErrors, previousErrors);
		}
	}

	private static final String[] _DID_NOT_RUN_ERRORS = {
		"Aborted prior to running test", "Failed prior to running test",
		"Failed for unknown reason", "timed out after 2 hours"
	};

	private static final double _MAX_JARO_WINKLER_DISTANCE = 0.93;

	private ErrorType _errorType;
	private final JSONObject _jsonObject;
	private TestrayCaseResult _parentTestrayCaseResult;
	private TestrayBuild _testrayBuild;
	private TestrayCase _testrayCase;
	private boolean _testrayCaseCached;
	private URL _testrayCaseResultURL;
	private TestrayComponent _testrayComponent;
	private boolean _testrayComponentCached;
	private TestrayRun _testrayRun;
	private final TestrayServer _testrayServer;
	private TestrayTeam _testrayTeam;

}