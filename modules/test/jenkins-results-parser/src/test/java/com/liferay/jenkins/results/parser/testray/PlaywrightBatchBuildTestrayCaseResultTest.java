/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.PlaywrightJUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.PlaywrightTestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.PlaywrightSegmentTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.SegmentTestClassGroup;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class PlaywrightBatchBuildTestrayCaseResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testFindTestReport() {
		String parentDirName = RandomTestUtil.randomString();

		String specFilePath = JenkinsResultsParserUtil.combine(
			parentDirName, "/", RandomTestUtil.randomString(), "/",
			RandomTestUtil.randomString());

		TestClassReport testClassReport = Mockito.mock(TestClassReport.class);

		Mockito.doReturn(
			specFilePath
		).when(
			testClassReport
		).getTestClassName();

		String testName = RandomTestUtil.randomString();

		TestReport testReport = _mockTestReport(testClassReport, testName);

		DownstreamBuildReport downstreamBuildReport = Mockito.mock(
			DownstreamBuildReport.class);

		Mockito.doReturn(
			testClassReport
		).when(
			downstreamBuildReport
		).getTestClassReport(
			specFilePath
		);

		PlaywrightBatchBuildTestrayCaseResult
			playwrightBatchBuildTestrayCaseResult =
				_getPlaywrightBatchBuildTestrayCaseResult(
					downstreamBuildReport,
					_mockPlaywrightSegmentTestClassGroup(
						JenkinsResultsParserUtil.combine(
							parentDirName, ".", RandomTestUtil.randomString())),
					specFilePath, testName);

		Assert.assertSame(
			testReport, playwrightBatchBuildTestrayCaseResult.findTestReport());
	}

	@Test
	public void testGetName() {
		String sharedDirName = RandomTestUtil.randomString();

		String variantName = JenkinsResultsParserUtil.combine(
			sharedDirName, "-", RandomTestUtil.randomString());

		String parentDirName = RandomTestUtil.randomString();

		PlaywrightSegmentTestClassGroup playwrightSegmentTestClassGroup =
			_mockPlaywrightSegmentTestClassGroup(
				JenkinsResultsParserUtil.combine(
					parentDirName, ".", variantName));

		String testName = RandomTestUtil.randomString();

		_testGetName(
			JenkinsResultsParserUtil.combine("null > ", testName),
			playwrightSegmentTestClassGroup, null, testName);

		String otherDirName = RandomTestUtil.randomString();
		String specFileName = RandomTestUtil.randomString();

		_testGetName(
			JenkinsResultsParserUtil.combine(
				otherDirName, "/", variantName, "/", specFileName, " > ",
				testName),
			playwrightSegmentTestClassGroup,
			JenkinsResultsParserUtil.combine(
				otherDirName, "/", sharedDirName, "/", specFileName),
			testName);

		String parentSubdirName = RandomTestUtil.randomString();

		_testGetName(
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", parentSubdirName, "/", variantName, "/",
				specFileName, " > ", testName),
			_mockPlaywrightSegmentTestClassGroup(
				JenkinsResultsParserUtil.combine(
					parentDirName, ".", parentSubdirName, ".", variantName)),
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", parentSubdirName, "/", sharedDirName, "/",
				specFileName),
			testName);

		_testGetName(
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", sharedDirName, "/", specFileName, " > ",
				testName),
			Mockito.mock(SegmentTestClassGroup.class),
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", sharedDirName, "/", specFileName),
			testName);
		_testGetName(
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", sharedDirName, "/", specFileName, " > ",
				testName),
			_mockPlaywrightSegmentTestClassGroup(null),
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", sharedDirName, "/", specFileName),
			testName);
		_testGetName(
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", variantName, "/", specFileName, " > ",
				testName),
			playwrightSegmentTestClassGroup,
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", sharedDirName, "/", specFileName),
			testName);
		_testGetName(
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", variantName, "/", specFileName, " > ",
				testName),
			playwrightSegmentTestClassGroup,
			JenkinsResultsParserUtil.combine(parentDirName, "/", specFileName),
			testName);
		_testGetName(
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", variantName, "/", specFileName, " > ",
				testName),
			playwrightSegmentTestClassGroup,
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", variantName, "/", specFileName),
			testName);

		String specSubdirName = RandomTestUtil.randomString();

		_testGetName(
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", variantName, "/", specSubdirName, "/",
				specFileName, " > ", testName),
			playwrightSegmentTestClassGroup,
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", sharedDirName, "/", specSubdirName, "/",
				specFileName),
			testName);
		_testGetName(
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", variantName, "/", specSubdirName, "/",
				specFileName, " > ", testName),
			playwrightSegmentTestClassGroup,
			JenkinsResultsParserUtil.combine(
				parentDirName, "/", variantName, "/", specSubdirName, "/",
				specFileName),
			testName);

		_testGetName(
			JenkinsResultsParserUtil.combine(specFileName, " > ", testName),
			playwrightSegmentTestClassGroup, specFileName, testName);
		_testGetName(
			JenkinsResultsParserUtil.combine(
				variantName, "/", specFileName, " > ", testName),
			_mockPlaywrightSegmentTestClassGroup(variantName),
			JenkinsResultsParserUtil.combine(sharedDirName, "/", specFileName),
			testName);
	}

	private PlaywrightBatchBuildTestrayCaseResult
		_getPlaywrightBatchBuildTestrayCaseResult(
			DownstreamBuildReport downstreamBuildReport,
			SegmentTestClassGroup segmentTestClassGroup, String specFilePath,
			String testName) {

		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		Mockito.doReturn(
			segmentTestClassGroup
		).when(
			axisTestClassGroup
		).getSegmentTestClassGroup();

		PlaywrightJUnitTestClass playwrightJUnitTestClass = Mockito.mock(
			PlaywrightJUnitTestClass.class);

		Mockito.doReturn(
			specFilePath
		).when(
			playwrightJUnitTestClass
		).getSpecFilePath();

		PlaywrightTestClassMethod playwrightTestClassMethod = Mockito.mock(
			PlaywrightTestClassMethod.class);

		Mockito.doReturn(
			JenkinsResultsParserUtil.combine(specFilePath, " > ", testName)
		).when(
			playwrightTestClassMethod
		).getName();

		Mockito.doReturn(
			testName
		).when(
			playwrightTestClassMethod
		).getTestName();

		TopLevelBuildReport topLevelBuildReport = Mockito.mock(
			TopLevelBuildReport.class);

		Mockito.doReturn(
			downstreamBuildReport
		).when(
			topLevelBuildReport
		).getDownstreamBuildReport(
			Mockito.any()
		);

		mockEnvironment(
			Collections.singletonMap(
				"WORKSPACE", RandomTestUtil.randomString()));

		return new PlaywrightBatchBuildTestrayCaseResult(
			axisTestClassGroup, playwrightJUnitTestClass,
			playwrightTestClassMethod, Mockito.mock(TestrayBuild.class),
			topLevelBuildReport);
	}

	private PlaywrightSegmentTestClassGroup
		_mockPlaywrightSegmentTestClassGroup(String projectName) {

		PlaywrightSegmentTestClassGroup playwrightSegmentTestClassGroup =
			Mockito.mock(PlaywrightSegmentTestClassGroup.class);

		Mockito.doReturn(
			projectName
		).when(
			playwrightSegmentTestClassGroup
		).getProjectName();

		return playwrightSegmentTestClassGroup;
	}

	private TestReport _mockTestReport(
		TestClassReport testClassReport, String testName) {

		TestReport testReport = Mockito.mock(TestReport.class);

		Mockito.doReturn(
			testReport
		).when(
			testClassReport
		).getTestReport(
			testName
		);

		return testReport;
	}

	private void _testGetName(
		String expectedName, SegmentTestClassGroup segmentTestClassGroup,
		String specFilePath, String testName) {

		PlaywrightBatchBuildTestrayCaseResult
			playwrightBatchBuildTestrayCaseResult =
				_getPlaywrightBatchBuildTestrayCaseResult(
					null, segmentTestClassGroup, specFilePath, testName);

		Assert.assertEquals(
			expectedName, playwrightBatchBuildTestrayCaseResult.getName());
	}

}