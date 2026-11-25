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
	public void testInitBuildReport() {
		String axisName = RandomTestUtil.randomString();
		String testClassName = RandomTestUtil.randomString();

		DownstreamBuildReport downstreamBuildReport = _getDownstreamBuildReport(
			axisName, "com." + testClassName);

		TopLevelBuildReport topLevelBuildReport = _getTopLevelBuildReport(
			axisName, downstreamBuildReport);

		JUnitBatchBuildTestrayCaseResult jUnitBatchBuildTestrayCaseResult =
			_getJUnitBatchBuildTestrayCaseResult(
				axisName, testClassName, topLevelBuildReport);

		testEquals(
			downstreamBuildReport,
			jUnitBatchBuildTestrayCaseResult.getDownstreamBuildReport());

		Mockito.verify(
			topLevelBuildReport, Mockito.never()
		).getDownstreamBuildReports();
	}

	@Test
	public void testInitBuildReportFallback() {
		String axisName = RandomTestUtil.randomString();
		String testClassName = RandomTestUtil.randomString();

		DownstreamBuildReport downstreamBuildReport1 =
			_getDownstreamBuildReport(axisName, RandomTestUtil.randomString());
		DownstreamBuildReport downstreamBuildReport2 =
			_getDownstreamBuildReport(axisName, "com." + testClassName);

		TopLevelBuildReport topLevelBuildReport = _getTopLevelBuildReport(
			axisName, downstreamBuildReport1);

		Mockito.doReturn(
			Arrays.asList(
				_getDownstreamBuildReport(
					RandomTestUtil.randomString(), "com." + testClassName),
				downstreamBuildReport1, downstreamBuildReport2)
		).when(
			topLevelBuildReport
		).getDownstreamBuildReports();

		JUnitBatchBuildTestrayCaseResult jUnitBatchBuildTestrayCaseResult =
			_getJUnitBatchBuildTestrayCaseResult(
				axisName, testClassName, topLevelBuildReport);

		testEquals(
			downstreamBuildReport2,
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

		Mockito.doReturn(
			Collections.singletonList(Mockito.mock(TestClassReport.class))
		).when(
			downstreamBuildReport
		).getTestClassReports(
			testClassName
		);

		return downstreamBuildReport;
	}

	private JUnitBatchBuildTestrayCaseResult
		_getJUnitBatchBuildTestrayCaseResult(
			String axisName, String testClassName,
			TopLevelBuildReport topLevelBuildReport) {

		mockEnvironment(Collections.singletonMap("WORKSPACE", "/x"));

		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		Mockito.doReturn(
			axisName
		).when(
			axisTestClassGroup
		).getAxisName();

		JUnitTestClass jUnitTestClass = Mockito.mock(JUnitTestClass.class);

		Mockito.doReturn(
			new File("/x/com/" + testClassName + ".java")
		).when(
			jUnitTestClass
		).getTestClassFile();

		return new JUnitBatchBuildTestrayCaseResult(
			axisTestClassGroup, jUnitTestClass,
			Mockito.mock(TestrayBuild.class), topLevelBuildReport);
	}

	private TopLevelBuildReport _getTopLevelBuildReport(
		String axisName, DownstreamBuildReport downstreamBuildReport) {

		TopLevelBuildReport topLevelBuildReport = Mockito.mock(
			TopLevelBuildReport.class);

		Mockito.doReturn(
			downstreamBuildReport
		).when(
			topLevelBuildReport
		).getDownstreamBuildReport(
			axisName
		);

		return topLevelBuildReport;
	}

}