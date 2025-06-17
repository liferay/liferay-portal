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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

	public String getCaseID() {
		TestrayComponent testrayComponent = getTestrayComponent();

		if (testrayComponent == null) {
			return null;
		}

		return String.valueOf(testrayComponent.getID());
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
				getTestrayCaseResultHistory(5)) {

			if (Objects.equals(getID(), previousTestrayCaseResult.getID())) {
				continue;
			}

			if (_isSimilarError(previousTestrayCaseResult) &&
				!Objects.equals(
					getPullRequestSenderUsername(),
					previousTestrayCaseResult.getPullRequestSenderUsername())) {

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

	public long getID() {
		return _jsonObject.optLong("id");
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

	public String getTeamName() {
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

	public TestrayBuild getTestrayBuild() {
		if (_testrayBuild != null) {
			return _testrayBuild;
		}

		JSONObject buildJSONObject = _jsonObject.getJSONObject(
			"buildToCaseResult");

		if (buildJSONObject != null) {
			_testrayBuild = _testrayServer.getTestrayBuildByID(
				buildJSONObject.getLong("id"));
		}

		return _testrayBuild;
	}

	public TestrayCase getTestrayCase() {
		if (_testrayCase != null) {
			return _testrayCase;
		}

		JSONObject caseJSONObject = _jsonObject.optJSONObject(
			"caseToCaseResult");

		if (caseJSONObject != null) {
			TestrayBuild testrayBuild = getTestrayBuild();

			_testrayCase = TestrayFactory.newTestrayCase(
				testrayBuild.getTestrayProject(), caseJSONObject);
		}

		return _testrayCase;
	}

	public List<TestrayCaseResult> getTestrayCaseResultHistory(int maxCount) {
		List<TestrayCaseResult> testrayCaseResults = new ArrayList<>();

		StringBuilder sb = new StringBuilder();

		TestrayCase testrayCase = getTestrayCase();

		sb.append("r_caseToCaseResult_c_caseId eq '");
		sb.append(testrayCase.getID());
		sb.append("'");

		TestrayServer testrayServer = getTestrayServer();

		try {
			List<JSONObject> entityJSONObjects = testrayServer.requestGraphQL(
				"caseResults", TestrayCaseResult.FIELD_NAMES, sb.toString(),
				"dateCreated:desc", maxCount, 5);

			for (JSONObject entityJSONObject : entityJSONObjects) {
				testrayCaseResults.add(
					TestrayFactory.newJSONObjectTestrayCaseResult(
						testrayServer, entityJSONObject));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return testrayCaseResults;
	}

	public TestrayComponent getTestrayComponent() {
		if (_testrayComponent != null) {
			return _testrayComponent;
		}

		JSONObject componentJSONObject = _jsonObject.optJSONObject(
			"componentToCaseResult");

		if (componentJSONObject != null) {
			TestrayBuild testrayBuild = getTestrayBuild();

			TestrayProject testrayProject = testrayBuild.getTestrayProject();

			_testrayComponent = testrayProject.getTestrayComponentByID(
				componentJSONObject.getLong("id"));
		}

		return _testrayComponent;
	}

	public TestrayProject getTestrayProject() {
		TestrayBuild testrayBuild = getTestrayBuild();

		return testrayBuild.getTestrayProject();
	}

	public TestrayServer getTestrayServer() {
		return _testrayServer;
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
			return new URL(testrayBuild.getURL() + "/case-result/" + getID());
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	public String[] getWarnings() {
		return null;
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

		public Integer getID() {
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
				_statuses.put(status.getID(), status);
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

	protected synchronized void initTestrayAttachments() {
		if (testrayAttachments != null) {
			return;
		}

		testrayAttachments = new TreeMap<>();

		String attachments = _jsonObject.getString("attachments");

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
	private TestrayBuild _testrayBuild;
	private TestrayCase _testrayCase;
	private TestrayComponent _testrayComponent;
	private final TestrayServer _testrayServer;

}