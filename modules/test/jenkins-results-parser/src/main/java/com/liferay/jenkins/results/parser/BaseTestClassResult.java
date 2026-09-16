/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.history.TestClassHistory;
import com.liferay.jenkins.results.parser.test.clazz.JUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.dom4j.Element;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestClassResult implements TestClassResult {

	@Override
	public Build getBuild() {
		return _build;
	}

	@Override
	public String getClassName() {
		List<TestResult> testResults = getTestResults();

		if (testResults.isEmpty()) {
			return _suiteJSONObject.getString("name");
		}

		TestResult testResult = testResults.get(0);

		return testResult.getClassName();
	}

	@Override
	public long getDuration() {
		return _duration;
	}

	@Override
	public Element getGitHubElement() {
		return getGitHubElement(null);
	}

	@Override
	public Element getGitHubElement(Boolean uniqueFailures) {
		Element downstreamBuildListItemElement = Dom4JUtil.getNewElement(
			"details", null);

		Element summaryElement = Dom4JUtil.getNewElement(
			"summary", downstreamBuildListItemElement);

		summaryElement.add(
			Dom4JUtil.getNewAnchorElement(
				getTestClassReportURL(), getClassName()));

		TestClassHistory testClassHistory = getTestClassHistory();

		if (testClassHistory != null) {
			URL testrayCaseURL = testClassHistory.getTestrayCaseURL();

			String summaryContent = JenkinsResultsParserUtil.combine(
				"Failed ", String.valueOf(testClassHistory.getFailureCount()),
				" of last ", String.valueOf(testClassHistory.getTestCount()));

			if (testrayCaseURL != null) {
				Dom4JUtil.addToElement(
					downstreamBuildListItemElement, " - ",
					Dom4JUtil.getNewAnchorElement(
						String.valueOf(testrayCaseURL), summaryContent));
			}
			else {
				downstreamBuildListItemElement.addText(" - " + summaryContent);
			}
		}

		List<Element> failureElements = new ArrayList<>();

		for (TestResult testResult : getTestResults()) {
			if (!testResult.isFailing()) {
				continue;
			}

			if ((uniqueFailures == null) ||
				(uniqueFailures && testResult.isUniqueFailure()) ||
				(!uniqueFailures && !testResult.isUniqueFailure())) {

				failureElements.add(
					Dom4JUtil.getNewAnchorElement(
						testResult.getTestReportURL(),
						testResult.getTestName()));
			}
		}

		if (failureElements.isEmpty()) {
			return null;
		}

		Element failuresElement = Dom4JUtil.getNewElement(
			"div", downstreamBuildListItemElement);

		Dom4JUtil.getOrderedListElement(failureElements, failuresElement, 5);

		return downstreamBuildListItemElement;
	}

	@Override
	public String getPackageName() {
		String className = getClassName();

		int x = className.lastIndexOf(".");

		if (x < 0) {
			return "(root)";
		}

		return className.substring(0, x);
	}

	@Override
	public String getSimpleClassName() {
		String className = getClassName();

		int x = className.lastIndexOf(".");

		return className.substring(x + 1);
	}

	@Override
	public String getStatus() {
		if (_status != null) {
			return _status.toString();
		}

		_status = Status.PASSED;

		for (TestResult testResult : getTestResults()) {
			Status status = Status.valueOf(testResult.getStatus());

			if (_status.getPriority() <= status.getPriority()) {
				continue;
			}

			_status = status;
		}

		return _status.toString();
	}

	@Override
	public TestClass getTestClass() {
		if (_testClass != null) {
			return _testClass;
		}

		Build build = getBuild();

		if (!(build instanceof DownstreamBuild)) {
			return null;
		}

		DownstreamBuild downstreamBuild = (DownstreamBuild)build;

		AxisTestClassGroup axisTestClassGroup =
			downstreamBuild.getAxisTestClassGroup();

		if (axisTestClassGroup == null) {
			return null;
		}

		String className = getClassName();

		for (TestClass testClass : axisTestClassGroup.getTestClasses()) {
			if (!(testClass instanceof JUnitTestClass)) {
				continue;
			}

			JUnitTestClass jUnitTestClass = (JUnitTestClass)testClass;

			if (Objects.equals(className, jUnitTestClass.getTestClassName())) {
				_testClass = testClass;

				return _testClass;
			}
		}

		return null;
	}

	@Override
	public TestClassHistory getTestClassHistory() {
		TestClass testClass = getTestClass();

		if (testClass == null) {
			return null;
		}

		return testClass.getTestClassHistory();
	}

	@Override
	public String getTestClassReportURL() {
		StringBuilder sb = new StringBuilder();

		Build build = getBuild();

		sb.append(build.getBuildURL());

		sb.append("testReport/");

		String packageName = getPackageName();

		sb.append(packageName.replaceAll("/", "_"));

		sb.append("/");
		sb.append(getSimpleClassName());

		String testClassReportURL = sb.toString();

		if (testClassReportURL.startsWith("http")) {
			try {
				return JenkinsResultsParserUtil.encode(testClassReportURL);
			}
			catch (MalformedURLException | URISyntaxException exception) {
				System.out.println(
					"Unable to encode the test report " + testClassReportURL);
			}
		}

		return testClassReportURL;
	}

	@Override
	public String getTestClassResultKey() {
		return getClassName();
	}

	@Override
	public TestResult getTestResult(String testName) {
		for (TestResult testResult : _testResults.values()) {
			if (Objects.equals(testName, testResult.getTestName())) {
				return testResult;
			}
		}

		return null;
	}

	@Override
	public List<TestResult> getTestResults() {
		return new ArrayList<>(_testResults.values());
	}

	@Override
	public boolean isFailing() {
		Status status = Status.valueOf(getStatus());

		if ((status == Status.FIXED) || (status == Status.PASSED) ||
			(status == Status.SKIPPED)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isSkipped() {
		Status status = Status.valueOf(getStatus());

		if (status == Status.SKIPPED) {
			return true;
		}

		return false;
	}

	protected BaseTestClassResult(Build build, JSONObject suiteJSONObject) {
		if (suiteJSONObject == null) {
			throw new RuntimeException("Please set suiteJSONObject");
		}

		_build = build;
		_suiteJSONObject = suiteJSONObject;

		_duration = (long)(suiteJSONObject.getDouble("duration") * 1000);

		if (!suiteJSONObject.has("cases")) {
			return;
		}

		JSONArray casesJSONArray = suiteJSONObject.getJSONArray("cases");

		for (int i = 0; i < casesJSONArray.length(); i++) {
			JSONObject caseJSONObject = casesJSONArray.getJSONObject(i);

			TestResult testResult = TestResultFactory.newTestResult(
				build, caseJSONObject);

			_testResults.put(testResult.getTestResultKey(), testResult);
		}
	}

	protected JSONObject getSuiteJSONObject() {
		return _suiteJSONObject;
	}

	private final Build _build;
	private final long _duration;
	private Status _status;
	private final JSONObject _suiteJSONObject;
	private TestClass _testClass;
	private final Map<String, TestResult> _testResults = new TreeMap<>();

	private static enum Status {

		ABORTED(1), FAILED(2), FIXED(6), PASSED(7), REGRESSION(3), SKIPPED(5),
		UNSTABLE(4);

		public Integer getPriority() {
			return _priority;
		}

		private Status(Integer priority) {
			_priority = priority;
		}

		private final Integer _priority;

	}

}