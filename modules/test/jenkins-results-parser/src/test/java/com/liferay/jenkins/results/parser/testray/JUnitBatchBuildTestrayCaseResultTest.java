/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.JUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.io.File;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class JUnitBatchBuildTestrayCaseResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testInitBuildReport() throws Exception {
		mockEnvironment(Collections.singletonMap("WORKSPACE", "/x"));

		String axisName = RandomTestUtil.randomString();
		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		Mockito.doReturn(
			axisName
		).when(
			axisTestClassGroup
		).getAxisName();

		JUnitTestClass jUnitTestClass = Mockito.mock(JUnitTestClass.class);
		String testClassName = RandomTestUtil.randomString();

		Mockito.doReturn(
			new File("/x/com/" + testClassName + ".java")
		).when(
			jUnitTestClass
		).getTestClassFile();

		DownstreamBuildReport axisDownstreamBuildReport =
			_getDownstreamBuildReport(axisName, RandomTestUtil.randomString());
		TopLevelBuildReport topLevelBuildReport = Mockito.mock(
			TopLevelBuildReport.class);

		Mockito.doReturn(
			axisDownstreamBuildReport
		).when(
			topLevelBuildReport
		).getDownstreamBuildReport(
			axisName
		);

		DownstreamBuildReport downstreamBuildReport = _getDownstreamBuildReport(
			axisName, "com." + testClassName);

		Mockito.doReturn(
			Arrays.asList(axisDownstreamBuildReport, downstreamBuildReport)
		).when(
			topLevelBuildReport
		).getDownstreamBuildReports();

		JUnitBatchBuildTestrayCaseResult jUnitBatchBuildTestrayCaseResult =
			new JUnitBatchBuildTestrayCaseResult(
				axisTestClassGroup, jUnitTestClass,
				Mockito.mock(TestrayBuild.class), topLevelBuildReport);

		testEquals(
			downstreamBuildReport,
			jUnitBatchBuildTestrayCaseResult.getDownstreamBuildReport());
	}

	private DownstreamBuildReport _getDownstreamBuildReport(
		String axisName, String testClassName) {

		DownstreamBuildReport downstreamBuildReport = Mockito.mock(
			DownstreamBuildReport.class);

		Mockito.doReturn(
			axisName
		).when(
			downstreamBuildReport
		).getAxisName();

		TestClassReport testClassReport = Mockito.mock(TestClassReport.class);

		Mockito.doReturn(
			testClassName
		).when(
			testClassReport
		).getTestClassName();

		Mockito.doReturn(
			Collections.singletonList(testClassReport)
		).when(
			downstreamBuildReport
		).getTestClassReports();

		return downstreamBuildReport;
	}

}