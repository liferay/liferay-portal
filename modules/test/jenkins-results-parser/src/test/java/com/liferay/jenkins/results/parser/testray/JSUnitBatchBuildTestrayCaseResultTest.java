/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BaseDownstreamBuildReport;
import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.DownstreamBuild;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.TestReportFactory;
import com.liferay.jenkins.results.parser.TestResult;
import com.liferay.jenkins.results.parser.TestResultFactory;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.JSUnitJUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitModulesBatchTestClassGroup;

import java.io.File;

import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class JSUnitBatchBuildTestrayCaseResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetName() throws Exception {
		_mockWorkspace();

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(_CLASS_PATH);

		testEquals(
			":apps:a:b:packageRunTest",
			_getJSUnitBatchBuildTestrayCaseResult(
				jsUnitJUnitTestClass, null
			).getName());
	}

	@Test
	public void testGetNameLongTestClassFile() throws Exception {
		_mockWorkspace();

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(
			_LONG_CLASS_PATH);

		List<TestClassMethod> testClassMethods =
			jsUnitJUnitTestClass.getTestClassMethods();

		testEquals(
			_LONG_CLASS_PATH,
			_getJSUnitBatchBuildTestrayCaseResult(
				jsUnitJUnitTestClass, testClassMethods.get(0)
			).getName());
	}

	@Test
	public void testGetNameTestClassFile() throws Exception {
		_mockWorkspace();

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(_CLASS_PATH);

		List<TestClassMethod> testClassMethods =
			jsUnitJUnitTestClass.getTestClassMethods();

		testEquals(
			_CLASS_PATH,
			_getJSUnitBatchBuildTestrayCaseResult(
				jsUnitJUnitTestClass, testClassMethods.get(0)
			).getName());
	}

	@Test
	public void testGetStatusLongTestClassFile() throws Exception {
		_mockWorkspace();

		testEquals(
			TestrayCaseResult.Status.PASSED,
			_getStatus(_LONG_CLASS_PATH, _LONG_CLASS_PATH));
	}

	@Test
	public void testGetStatusTestClassFile() throws Exception {
		_mockWorkspace();

		testEquals(
			TestrayCaseResult.Status.PASSED,
			_getStatus(_CLASS_PATH, _CLASS_PATH));
	}

	@Test
	public void testGetStatusTestClassFileOther() throws Exception {
		_mockWorkspace();

		testEquals(
			TestrayCaseResult.Status.UNTESTED,
			_getStatus(_OTHER_CLASS_PATH, _CLASS_PATH));
	}

	private JSUnitModulesBatchTestClassGroup _getBatchTestClassGroup() {
		JSUnitModulesBatchTestClassGroup jsUnitModulesBatchTestClassGroup =
			Mockito.mock(JSUnitModulesBatchTestClassGroup.class);

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doReturn(
			new File("/x")
		).when(
			portalGitWorkingDirectory
		).getWorkingDirectory();

		Mockito.doReturn(
			portalGitWorkingDirectory
		).when(
			jsUnitModulesBatchTestClassGroup
		).getPortalGitWorkingDirectory();

		return jsUnitModulesBatchTestClassGroup;
	}

	private JSUnitBatchBuildTestrayCaseResult
		_getJSUnitBatchBuildTestrayCaseResult(
			JSUnitJUnitTestClass jsUnitJUnitTestClass,
			TestClassMethod testClassMethod) {

		return new JSUnitBatchBuildTestrayCaseResult(
			Mockito.mock(JSUnitAxisTestClassGroup.class), jsUnitJUnitTestClass,
			testClassMethod, Mockito.mock(TestrayBuild.class),
			Mockito.mock(TopLevelBuildReport.class));
	}

	private TestrayCaseResult.Status _getStatus(
			String reportedClassPath, String testClassMethodName)
		throws Exception {

		JSONObject caseJSONObject = new JSONObject(
			"className", reportedClassPath
		).put(
			"duration", 1
		).put(
			"name", "a > b"
		).put(
			"status", "PASSED"
		);

		DownstreamBuild downstreamBuild = Mockito.mock(DownstreamBuild.class);

		Mockito.when(
			downstreamBuild.getBatchName()
		).thenReturn(
			_BATCH_NAME
		);

		TestResult testResult = TestResultFactory.newTestResult(
			downstreamBuild, caseJSONObject);

		BaseDownstreamBuildReport baseDownstreamBuildReport = Mockito.mock(
			BaseDownstreamBuildReport.class, Mockito.CALLS_REAL_METHODS);

		Mockito.doReturn(
			_BATCH_NAME
		).when(
			baseDownstreamBuildReport
		).getBatchName();

		Mockito.doReturn(
			Collections.singletonList(
				TestReportFactory.newTestReport(
					baseDownstreamBuildReport,
					testResult.getTestReportJSONObject()))
		).when(
			baseDownstreamBuildReport
		).getTestReports();

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(
			testClassMethodName);

		List<TestClassMethod> testClassMethods =
			jsUnitJUnitTestClass.getTestClassMethods();

		JSUnitBatchBuildTestrayCaseResult jsUnitBatchBuildTestrayCaseResult =
			_getJSUnitBatchBuildTestrayCaseResult(
				jsUnitJUnitTestClass, testClassMethods.get(0));

		ReflectionTestUtil.invoke(
			jsUnitBatchBuildTestrayCaseResult, "setBuildReport",
			new Class<?>[] {BuildReport.class}, baseDownstreamBuildReport);

		return jsUnitBatchBuildTestrayCaseResult.getStatus();
	}

	private JSUnitJUnitTestClass _getTestClass(String methodName) {
		JSONObject methodJSONObject = new JSONObject(
			"ignored", false
		).put(
			"name", methodName
		);

		JSONObject jsonObject = new JSONObject(
		).put(
			"file", "/x/modules/apps/a/b"
		).put(
			"ignored", false
		).put(
			"methods",
			new JSONArray(
			).put(
				methodJSONObject
			)
		);

		return (JSUnitJUnitTestClass)TestClassFactory.newTestClass(
			_getBatchTestClassGroup(), jsonObject);
	}

	private void _mockWorkspace() {
		mockEnvironment(Collections.singletonMap("WORKSPACE", "/x"));
	}

	private static final String _BATCH_NAME = "workspaces-js-unit";

	private static final String _CLASS_PATH = "modules/apps/a/b/test/js/c.js";

	private static final String _LONG_CLASS_PATH =
		"modules/apps/layout/layout-content-page-editor-web/test/page_editor" +
			"/app/plugins/browser/components/page_structure/components" +
				"/item_configuration_panels/collection_general_panel" +
					"/CollectionGeneralPanel.test.js";

	private static final String _OTHER_CLASS_PATH =
		"modules/apps/a/b/test/js/d.js";

}