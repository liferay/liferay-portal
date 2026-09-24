/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BaseDownstreamBuildReport;
import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TestReportFactory;
import com.liferay.jenkins.results.parser.TestResult;
import com.liferay.jenkins.results.parser.TestResultFactory;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.group.WorkspacesCompileAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.WorkspacesCompileBatchTestClassGroup;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class WorkspacesCompileBatchBuildTestrayCaseResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	public void setUpEnvironment() {
		mockEnvironment(
			Collections.singletonMap(
				"WORKSPACE", "/" + RandomTestUtil.randomString()));
	}

	@Test
	public void testGetDuration() {
		String workspaceName = RandomTestUtil.randomString();

		_testGetDuration(
			_getDownstreamBuildReport(
				null, null,
				_getCaseJSONObject(null, null, "PASSED", workspaceName)),
			1500, workspaceName);
		_testGetDuration(null, 0, workspaceName);
	}

	@Test
	public void testGetErrors() {
		String jobName = RandomTestUtil.randomString();
		String workspaceName = RandomTestUtil.randomString();

		_testGetErrors(
			_getDownstreamBuildReport(jobName, "ABORTED"),
			jobName + " timed out after 2 hours", workspaceName);
		_testGetErrors(
			_getDownstreamBuildReport(null, "FAILURE"),
			"Failed prior to running test", workspaceName);
		_testGetErrors(
			_getDownstreamBuildReport(null, "SUCCESS"),
			"Unable to run test on CI", workspaceName);
		_testGetErrors(
			_getDownstreamBuildReport(
				null, "SUCCESS",
				_getCaseJSONObject(null, null, "PASSED", workspaceName)),
			null, workspaceName);
		_testGetErrors(
			_getDownstreamBuildReport(null, "UNSTABLE"),
			"Unable to run test on CI", workspaceName);

		String errorDetails =
			"workspaces/" + workspaceName +
				" build failed. Please check the logs for details.";

		String errorStackTrace = JenkinsResultsParserUtil.combine(
			"Execute failed: java.io.IOException: Cannot run program\n",
			"\tat org.apache.tools.ant.taskdefs.Execute.execute(",
			"Execute.java:1)");

		_testGetErrors(
			_getDownstreamBuildReport(
				null, "UNSTABLE",
				_getCaseJSONObject(
					errorDetails, errorStackTrace, "FAILED", workspaceName)),
			"Execute failed: java.io.IOException: Cannot run program",
			workspaceName);

		_testGetErrors(
			_getDownstreamBuildReport(
				null, "UNSTABLE",
				_getCaseJSONObject(
					errorDetails, null, "FAILED", workspaceName)),
			errorDetails, workspaceName);
		_testGetErrors(
			_getDownstreamBuildReport(null, null),
			"Unable to finish build on CI", workspaceName);
		_testGetErrors(null, "Unable to run build on CI", workspaceName);
	}

	@Test
	public void testGetName() throws Exception {
		Map<String, Set<String>> workspacesNamesMap = _getWorkspacesNamesMap();

		Assert.assertFalse(workspacesNamesMap.isEmpty());

		for (Map.Entry<String, Set<String>> entry :
				workspacesNamesMap.entrySet()) {

			String batchName = entry.getKey();

			for (String workspaceName : entry.getValue()) {
				for (File workspaceDir : _getWorkspaceDirs(workspaceName)) {
					WorkspacesCompileBatchBuildTestrayCaseResult
						workspacesCompileBatchBuildTestrayCaseResult =
							_getWorkspacesCompileBatchBuildTestrayCaseResult(
								batchName, null, workspaceDir);

					testEquals(
						batchName + "[workspaces/" + workspaceName + "]",
						workspacesCompileBatchBuildTestrayCaseResult.getName());
				}
			}
		}
	}

	@Test
	public void testGetStatus() {
		String workspaceName = RandomTestUtil.randomString();

		_testGetStatus(
			_getDownstreamBuildReport(null, "ABORTED"),
			TestrayCaseResult.Status.FAILED, workspaceName);
		_testGetStatus(
			_getDownstreamBuildReport(null, "FAILURE"),
			TestrayCaseResult.Status.FAILED, workspaceName);
		_testGetStatus(
			_getDownstreamBuildReport(null, "SUCCESS"),
			TestrayCaseResult.Status.UNTESTED, workspaceName);
		_testGetStatus(
			_getDownstreamBuildReport(
				null, "SUCCESS",
				_getCaseJSONObject(
					null, null, "FAILED",
					workspaceName + "-" + RandomTestUtil.randomString()),
				_getCaseJSONObject(
					null, null, "FAILED",
					RandomTestUtil.randomString() + "-" + workspaceName)),
			TestrayCaseResult.Status.UNTESTED, workspaceName);
		_testGetStatus(
			_getDownstreamBuildReport(
				null, "SUCCESS",
				_getCaseJSONObject(null, null, "PASSED", workspaceName)),
			TestrayCaseResult.Status.PASSED, workspaceName);
		_testGetStatus(
			_getDownstreamBuildReport(null, "UNSTABLE"),
			TestrayCaseResult.Status.UNTESTED, workspaceName);
		_testGetStatus(
			_getDownstreamBuildReport(
				null, "UNSTABLE",
				_getCaseJSONObject(null, null, "FAILED", workspaceName)),
			TestrayCaseResult.Status.FAILED, workspaceName);
		_testGetStatus(
			_getDownstreamBuildReport(null, null),
			TestrayCaseResult.Status.UNTESTED, workspaceName);
		_testGetStatus(null, TestrayCaseResult.Status.UNTESTED, workspaceName);
	}

	@Test
	public void testGetStatusListedWorkspaces() throws Exception {
		Map<String, Set<String>> workspacesNamesMap = _getWorkspacesNamesMap();

		Assert.assertFalse(workspacesNamesMap.isEmpty());

		for (Set<String> workspaceNames : workspacesNamesMap.values()) {
			for (String workspaceName : workspaceNames) {
				BaseDownstreamBuildReport baseDownstreamBuildReport =
					_getDownstreamBuildReport(
						null, "UNSTABLE",
						_getCaseJSONObject(
							null, null, "FAILED",
							RandomTestUtil.randomString()),
						_getCaseJSONObject(
							null, null, "PASSED", workspaceName));

				for (File workspaceDir : _getWorkspaceDirs(workspaceName)) {
					WorkspacesCompileBatchBuildTestrayCaseResult
						workspacesCompileBatchBuildTestrayCaseResult =
							_getWorkspacesCompileBatchBuildTestrayCaseResult(
								_BATCH_NAME, baseDownstreamBuildReport,
								workspaceDir);

					testEquals(
						TestrayCaseResult.Status.PASSED,
						workspacesCompileBatchBuildTestrayCaseResult.
							getStatus());
				}
			}
		}
	}

	@Test
	public void testGetTestrayAttachments() throws Exception {
		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		TestrayServer testrayServer = Mockito.mock(TestrayServer.class);

		String testrayServerURLString =
			"https://" + RandomTestUtil.randomString();

		Mockito.doReturn(
			new URL(testrayServerURLString)
		).when(
			testrayServer
		).getURL();

		Mockito.doReturn(
			testrayServer
		).when(
			testrayBuild
		).getTestrayServer();

		WorkspacesCompileBatchBuildTestrayCaseResult
			workspacesCompileBatchBuildTestrayCaseResult =
				new WorkspacesCompileBatchBuildTestrayCaseResult(
					Mockito.mock(WorkspacesCompileAxisTestClassGroup.class),
					_getTestClass(
						new File(
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString())),
					testrayBuild, Mockito.mock(TopLevelBuildReport.class));

		List<TestrayAttachment> testrayAttachments =
			workspacesCompileBatchBuildTestrayCaseResult.
				getTestrayAttachments();

		Assert.assertTrue(testrayAttachments.isEmpty());

		TestrayCaseResult parentTestrayCaseResult = Mockito.mock(
			TestrayCaseResult.class);

		String parentTestrayCaseResultName = RandomTestUtil.randomString();

		Mockito.doReturn(
			parentTestrayCaseResultName
		).when(
			parentTestrayCaseResult
		).getName();

		URL parentTestrayCaseResultURL = new URL(
			testrayServerURLString + "/" + RandomTestUtil.randomString());

		Mockito.doReturn(
			parentTestrayCaseResultURL
		).when(
			parentTestrayCaseResult
		).getTestrayCaseResultURL();

		workspacesCompileBatchBuildTestrayCaseResult.setParentTestrayCaseResult(
			parentTestrayCaseResult);

		testrayAttachments =
			workspacesCompileBatchBuildTestrayCaseResult.
				getTestrayAttachments();

		testEquals(1, testrayAttachments.size());

		TestrayAttachment testrayAttachment = testrayAttachments.get(0);

		testEquals(parentTestrayCaseResultName, testrayAttachment.getName());
		testEquals(parentTestrayCaseResultURL, testrayAttachment.getURL());
	}

	private JSONObject _getCaseJSONObject(
		String errorDetails, String errorStackTrace, String status,
		String workspaceName) {

		return new JSONObject(
		).put(
			"className", "workspaces." + workspaceName
		).put(
			"duration", 1.5
		).put(
			"errorDetails", errorDetails
		).put(
			"errorStackTrace", errorStackTrace
		).put(
			"name", "build"
		).put(
			"status", status
		);
	}

	private BaseDownstreamBuildReport _getDownstreamBuildReport(
		String jobName, String result, JSONObject... caseJSONObjects) {

		BaseDownstreamBuildReport baseDownstreamBuildReport = Mockito.mock(
			BaseDownstreamBuildReport.class, Mockito.CALLS_REAL_METHODS);

		Mockito.doReturn(
			_BATCH_NAME
		).when(
			baseDownstreamBuildReport
		).getBatchName();

		if (result != null) {
			Mockito.doReturn(
				new JSONObject(
				).put(
					"result", result
				)
			).when(
				baseDownstreamBuildReport
			).getBuildReportJSONObject();
		}

		if (jobName != null) {
			Mockito.doReturn(
				jobName
			).when(
				baseDownstreamBuildReport
			).getJobName();
		}

		List<TestReport> testReports = new ArrayList<>();

		for (JSONObject caseJSONObject : caseJSONObjects) {
			TestResult testResult = TestResultFactory.newTestResult(
				Mockito.mock(Build.class), caseJSONObject);

			testReports.add(
				TestReportFactory.newTestReport(
					baseDownstreamBuildReport,
					testResult.getTestReportJSONObject()));
		}

		Mockito.doReturn(
			testReports
		).when(
			baseDownstreamBuildReport
		).getTestReports();

		return baseDownstreamBuildReport;
	}

	private TestClass _getTestClass(File workspaceDir) {
		return TestClassFactory.newTestClass(
			Mockito.mock(WorkspacesCompileBatchTestClassGroup.class),
			workspaceDir);
	}

	private List<File> _getWorkspaceDirs(String workspaceName) {
		return Arrays.asList(
			new File(
				"/" + RandomTestUtil.randomString(),
				"workspaces/" + workspaceName),
			new File(
				JenkinsResultsParserUtil.getGitWorkingDir(new File(".")),
				"workspaces/" + workspaceName));
	}

	private WorkspacesCompileBatchBuildTestrayCaseResult
		_getWorkspacesCompileBatchBuildTestrayCaseResult(
			String batchName, BuildReport buildReport, File workspaceDir) {

		WorkspacesCompileAxisTestClassGroup
			workspacesCompileAxisTestClassGroup = Mockito.mock(
				WorkspacesCompileAxisTestClassGroup.class);

		Mockito.doReturn(
			batchName
		).when(
			workspacesCompileAxisTestClassGroup
		).getBatchName();

		WorkspacesCompileBatchBuildTestrayCaseResult
			workspacesCompileBatchBuildTestrayCaseResult =
				new WorkspacesCompileBatchBuildTestrayCaseResult(
					workspacesCompileAxisTestClassGroup,
					_getTestClass(workspaceDir),
					Mockito.mock(TestrayBuild.class),
					Mockito.mock(TopLevelBuildReport.class));

		if (buildReport != null) {
			ReflectionTestUtil.invoke(
				workspacesCompileBatchBuildTestrayCaseResult, "setBuildReport",
				new Class<?>[] {BuildReport.class}, buildReport);
		}

		return workspacesCompileBatchBuildTestrayCaseResult;
	}

	private Map<String, Set<String>> _getWorkspacesNamesMap() {
		Map<String, Set<String>> workspacesNamesMap = new TreeMap<>();

		Properties properties = JenkinsResultsParserUtil.getProperties(
			new File(
				JenkinsResultsParserUtil.getGitWorkingDir(new File(".")),
				"test.properties"));

		for (String propertyName : properties.stringPropertyNames()) {
			Matcher matcher = _workspacesNamesPropertyNamePattern.matcher(
				propertyName);

			if (!matcher.matches()) {
				continue;
			}

			Set<String> workspaceNames = workspacesNamesMap.computeIfAbsent(
				matcher.group("batchName"), batchName -> new TreeSet<>());

			String propertyValue = JenkinsResultsParserUtil.getProperty(
				properties, propertyName);

			for (String workspaceName : propertyValue.split(",")) {
				workspaceName = workspaceName.trim();

				if (workspaceName.isEmpty() || workspaceName.startsWith("#")) {
					continue;
				}

				workspaceNames.add(workspaceName);
			}
		}

		return workspacesNamesMap;
	}

	private void _testGetDuration(
		BuildReport buildReport, long expectedDuration, String workspaceName) {

		WorkspacesCompileBatchBuildTestrayCaseResult
			workspacesCompileBatchBuildTestrayCaseResult =
				_getWorkspacesCompileBatchBuildTestrayCaseResult(
					_BATCH_NAME, buildReport,
					new File(
						RandomTestUtil.randomString(),
						"workspaces/" + workspaceName));

		testEquals(
			expectedDuration,
			workspacesCompileBatchBuildTestrayCaseResult.getDuration());
	}

	private void _testGetErrors(
		BuildReport buildReport, String expectedErrors, String workspaceName) {

		WorkspacesCompileBatchBuildTestrayCaseResult
			workspacesCompileBatchBuildTestrayCaseResult =
				_getWorkspacesCompileBatchBuildTestrayCaseResult(
					_BATCH_NAME, buildReport,
					new File(
						RandomTestUtil.randomString(),
						"workspaces/" + workspaceName));

		testEquals(
			expectedErrors,
			workspacesCompileBatchBuildTestrayCaseResult.getErrors());
	}

	private void _testGetStatus(
		BuildReport buildReport, TestrayCaseResult.Status expectedStatus,
		String workspaceName) {

		WorkspacesCompileBatchBuildTestrayCaseResult
			workspacesCompileBatchBuildTestrayCaseResult =
				_getWorkspacesCompileBatchBuildTestrayCaseResult(
					_BATCH_NAME, buildReport,
					new File(
						RandomTestUtil.randomString(),
						"workspaces/" + workspaceName));

		testEquals(
			expectedStatus,
			workspacesCompileBatchBuildTestrayCaseResult.getStatus());
	}

	private static final String _BATCH_NAME = "workspaces-compile";

	private static final Pattern _workspacesNamesPropertyNamePattern =
		Pattern.compile(
			"workspaces\\.names\\[(?<batchName>workspaces-compile[^\\]]*)\\]" +
				".*");

}