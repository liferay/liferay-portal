/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Charlotte Wong
 */
public class DownstreamTestClassBuildTestrayCaseResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testFindTestReport() {
		TestReport testReport = _mockTestReport(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		DownstreamTestClassBuildTestrayCaseResult
			downstreamTestClassBuildTestrayCaseResult =
				_newDownstreamTestClassBuildTestrayCaseResult(
					Mockito.mock(DownstreamBuildReport.class), testReport);

		Assert.assertSame(
			testReport,
			downstreamTestClassBuildTestrayCaseResult.findTestReport());
	}

	@Test
	public void testGetDuration() {
		TestReport testReport = _mockTestReport(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Mockito.doReturn(
			1234L
		).when(
			testReport
		).getDuration();

		DownstreamTestClassBuildTestrayCaseResult
			downstreamTestClassBuildTestrayCaseResult =
				_newDownstreamTestClassBuildTestrayCaseResult(
					Mockito.mock(DownstreamBuildReport.class), testReport);

		Assert.assertEquals(
			1234, downstreamTestClassBuildTestrayCaseResult.getDuration());
	}

	@Test
	public void testGetName() {
		String testClassName = RandomTestUtil.randomString();
		String testName = RandomTestUtil.randomString();

		TestReport testReport = _mockTestReport(testClassName, testName);

		DownstreamTestClassBuildTestrayCaseResult
			downstreamTestClassBuildTestrayCaseResult =
				_newDownstreamTestClassBuildTestrayCaseResult(
					Mockito.mock(DownstreamBuildReport.class), testReport);

		Assert.assertEquals(
			JenkinsResultsParserUtil.combine(testClassName, "#", testName),
			downstreamTestClassBuildTestrayCaseResult.getName());
	}

	@Test
	public void testGetStatus() {
		Assert.assertEquals(
			TestrayCaseResult.Status.FAILED, _getStatus(true, false));
		Assert.assertEquals(
			TestrayCaseResult.Status.PASSED, _getStatus(false, false));
		Assert.assertEquals(
			TestrayCaseResult.Status.UNTESTED, _getStatus(false, true));
	}

	private TestrayCaseResult.Status _getStatus(
		boolean failing, boolean skipped) {

		TestReport testReport = _mockTestReport(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Mockito.doReturn(
			failing
		).when(
			testReport
		).isFailing();

		Mockito.doReturn(
			skipped
		).when(
			testReport
		).isSkipped();

		DownstreamTestClassBuildTestrayCaseResult
			downstreamTestClassBuildTestrayCaseResult =
				_newDownstreamTestClassBuildTestrayCaseResult(
					Mockito.mock(DownstreamBuildReport.class), testReport);

		return downstreamTestClassBuildTestrayCaseResult.getStatus();
	}

	private AxisTestClassGroup _mockAxisTestClassGroup() {
		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			axisTestClassGroup
		).getAxisName();

		return axisTestClassGroup;
	}

	private TestReport _mockTestReport(String testClassName, String testName) {
		TestReport testReport = Mockito.mock(TestReport.class);

		Mockito.doReturn(
			testClassName
		).when(
			testReport
		).getTestClassName();

		Mockito.doReturn(
			testName
		).when(
			testReport
		).getTestName();

		return testReport;
	}

	private DownstreamTestClassBuildTestrayCaseResult
		_newDownstreamTestClassBuildTestrayCaseResult(
			DownstreamBuildReport downstreamBuildReport,
			TestReport testReport) {

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

		return new DownstreamTestClassBuildTestrayCaseResult(
			_mockAxisTestClassGroup(), testReport,
			Mockito.mock(TestrayBuild.class), topLevelBuildReport);
	}

}