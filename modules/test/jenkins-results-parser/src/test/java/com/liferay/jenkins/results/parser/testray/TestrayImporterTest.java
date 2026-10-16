/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.JSUnitJUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitModulesBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.PlaywrightAxisTestClassGroup;

import java.io.File;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class TestrayImporterTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testIsTestClassFileReported() throws Exception {
		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(
			"modules/apps/a/b/test/js/c.js");

		jsUnitJUnitTestClass.setTestClassFileReported(true);

		Assert.assertTrue(_isTestClassFileReported(jsUnitJUnitTestClass));
	}

	@Test
	public void testIsTestClassFileReportedNoTestClassMethods()
		throws Exception {

		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass();

		jsUnitJUnitTestClass.setTestClassFileReported(true);

		Assert.assertFalse(_isTestClassFileReported(jsUnitJUnitTestClass));
	}

	@Test
	public void testIsTestClassFileReportedOtherTestClass() throws Exception {
		Assert.assertFalse(
			_isTestClassFileReported(Mockito.mock(TestClass.class)));
	}

	@Test
	public void testIsTestClassFileReportedTestTask() throws Exception {
		JSUnitJUnitTestClass jsUnitJUnitTestClass = _getTestClass(
			"modules/apps/a/b/test/js/c.js");

		Assert.assertFalse(_isTestClassFileReported(jsUnitJUnitTestClass));
	}

	@Test
	public void testRecordAxisTestClassGroupPlaywrightPortalLogFailure()
		throws Exception {

		String portalLogErrors = RandomTestUtil.randomString();

		String xmlString = _recordAxisTestClassGroup(
			Mockito.mock(PlaywrightAxisTestClassGroup.class), portalLogErrors);

		Assert.assertTrue(xmlString, xmlString.contains(portalLogErrors));
	}

	@Test
	public void testRecordAxisTestClassGroupPlaywrightPortalLogPassing()
		throws Exception {

		String xmlString = _recordAxisTestClassGroup(
			Mockito.mock(PlaywrightAxisTestClassGroup.class), null);

		Assert.assertFalse(xmlString, xmlString.contains("<failure"));
	}

	@Test
	public void testRecordAxisTestClassGroupPortalLogFailure()
		throws Exception {

		String portalLogErrors = RandomTestUtil.randomString();

		String xmlString = _recordAxisTestClassGroup(
			Mockito.mock(AxisTestClassGroup.class), portalLogErrors);

		Assert.assertTrue(xmlString, xmlString.contains(portalLogErrors));
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

	private JSUnitJUnitTestClass _getTestClass(String... methodNames) {
		JSONArray methodsJSONArray = new JSONArray();

		for (String methodName : methodNames) {
			methodsJSONArray.put(
				new JSONObject(
				).put(
					"ignored", false
				).put(
					"name", methodName
				));
		}

		JSONObject jsonObject = new JSONObject(
		).put(
			"file", RandomTestUtil.randomString()
		).put(
			"ignored", false
		).put(
			"methods", methodsJSONArray
		);

		return (JSUnitJUnitTestClass)TestClassFactory.newTestClass(
			_getBatchTestClassGroup(), jsonObject);
	}

	private boolean _isTestClassFileReported(TestClass testClass) {
		return ReflectionTestUtil.invoke(
			Mockito.mock(TestrayImporter.class), "_isTestClassFileReported",
			new Class<?>[] {TestClass.class}, testClass);
	}

	private <T extends TestrayCaseResult> T _mockTestrayCaseResult(
		Class<T> clazz, String errors, TestrayCaseResult.Status status) {

		T testrayCaseResult = Mockito.mock(clazz);

		Mockito.doReturn(
			errors
		).when(
			testrayCaseResult
		).getErrors();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			testrayCaseResult
		).getName();

		Mockito.doReturn(
			status
		).when(
			testrayCaseResult
		).getStatus();

		return testrayCaseResult;
	}

	private String _recordAxisTestClassGroup(
			AxisTestClassGroup axisTestClassGroup, String portalLogErrors)
		throws Exception {

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			axisTestClassGroup
		).getAxisName();

		Mockito.doReturn(
			Collections.emptyList()
		).when(
			axisTestClassGroup
		).getTestClasses();

		TestrayServer testrayServer = Mockito.mock(TestrayServer.class);

		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			testrayBuild
		).getName();

		Mockito.doReturn(
			Mockito.mock(TestrayProject.class)
		).when(
			testrayBuild
		).getTestrayProject();

		Mockito.doReturn(
			Mockito.mock(TestrayRoutine.class)
		).when(
			testrayBuild
		).getTestrayRoutine();

		Mockito.doReturn(
			testrayServer
		).when(
			testrayBuild
		).getTestrayServer();

		TestrayRun testrayRun = Mockito.mock(TestrayRun.class);

		Mockito.doReturn(
			Dom4JUtil.getNewElement("environments")
		).when(
			testrayRun
		).getEnvironmentsElement();

		Mockito.doReturn(
			testrayBuild
		).when(
			testrayRun
		).getTestrayBuild();

		Mockito.doReturn(
			testrayRun
		).when(
			axisTestClassGroup
		).getTestrayRun();

		TopLevelBuildReport topLevelBuildReport = Mockito.mock(
			TopLevelBuildReport.class);

		Mockito.doReturn(
			Mockito.mock(JenkinsMaster.class)
		).when(
			topLevelBuildReport
		).getJenkinsMaster();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			topLevelBuildReport
		).getJobName();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			topLevelBuildReport
		).getTestSuiteName();

		TestrayImporter testrayImporter = Mockito.mock(TestrayImporter.class);

		ReflectionTestUtil.setFieldValue(
			testrayImporter, "_topLevelBuildReport", topLevelBuildReport);
		ReflectionTestUtil.setFieldValue(
			testrayImporter, "_uncreatedTestrayCaseResultsCount",
			new AtomicInteger());

		TestrayCaseResult testrayCaseResult = _mockTestrayCaseResult(
			TestrayCaseResult.class, null, TestrayCaseResult.Status.PASSED);

		PortalLogBatchBuildTestrayCaseResult
			portalLogBatchBuildTestrayCaseResult = _mockTestrayCaseResult(
				PortalLogBatchBuildTestrayCaseResult.class, portalLogErrors,
				TestrayCaseResult.Status.FAILED);

		try (MockedStatic<TestrayFactory> testrayFactoryMockedStatic =
				Mockito.mockStatic(TestrayFactory.class)) {

			testrayFactoryMockedStatic.when(
				() -> TestrayFactory.newBuildTestrayCaseResult(
					Mockito.any(AxisTestClassGroup.class),
					Mockito.any(TestrayBuild.class),
					Mockito.any(TopLevelBuildReport.class))
			).thenReturn(
				testrayCaseResult
			);

			testrayFactoryMockedStatic.when(
				() -> TestrayFactory.newPortalLogTestrayCaseResult(
					Mockito.any(AxisTestClassGroup.class),
					Mockito.any(TestrayBuild.class),
					Mockito.any(TopLevelBuildReport.class))
			).thenReturn(
				portalLogBatchBuildTestrayCaseResult
			);

			ReflectionTestUtil.invoke(
				testrayImporter, "_recordAxisTestClassGroup",
				new Class<?>[] {
					AxisTestClassGroup.class, TestrayCaseResult.class
				},
				axisTestClassGroup, Mockito.mock(TestrayCaseResult.class));
		}

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			testrayServer
		).writeCaseResult(
			Mockito.anyString(), argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

}