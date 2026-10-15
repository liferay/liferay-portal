/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.net.URL;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class DownstreamBatchBuildTestrayCaseResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockEnvironment(
			Collections.singletonMap(
				"WORKSPACE", RandomTestUtil.randomString()));
	}

	@Test
	public void testGetTestrayCaseResultURL() throws Exception {
		DownstreamBuildReport downstreamBuildReport = Mockito.mock(
			DownstreamBuildReport.class);

		URL testrayCaseResultURL = new URL(
			"https://" + RandomTestUtil.randomString());

		Mockito.when(
			downstreamBuildReport.getTestrayCaseResultURL()
		).thenReturn(
			testrayCaseResultURL
		);

		DownstreamBatchBuildTestrayCaseResult
			downstreamBatchBuildTestrayCaseResult =
				_newDownstreamBatchBuildTestrayCaseResult(
					downstreamBuildReport);

		testSame(
			testrayCaseResultURL,
			downstreamBatchBuildTestrayCaseResult.getTestrayCaseResultURL());

		downstreamBatchBuildTestrayCaseResult =
			_newDownstreamBatchBuildTestrayCaseResult(downstreamBuildReport);

		URL setTestrayCaseResultURL = new URL(
			"https://" + RandomTestUtil.randomString());

		downstreamBatchBuildTestrayCaseResult.setTestrayCaseResultURL(
			setTestrayCaseResultURL);

		testSame(
			setTestrayCaseResultURL,
			downstreamBatchBuildTestrayCaseResult.getTestrayCaseResultURL());

		downstreamBatchBuildTestrayCaseResult =
			_newDownstreamBatchBuildTestrayCaseResult(
				Mockito.mock(DownstreamBuildReport.class));

		testEquals(
			null,
			downstreamBatchBuildTestrayCaseResult.getTestrayCaseResultURL());

		downstreamBatchBuildTestrayCaseResult =
			_newDownstreamBatchBuildTestrayCaseResult(null);

		testEquals(
			null,
			downstreamBatchBuildTestrayCaseResult.getTestrayCaseResultURL());
	}

	@Test
	public void testGetTestrayCaseResultURLSharedReport() throws Exception {
		DownstreamBuildReport downstreamBuildReport = Mockito.mock(
			DownstreamBuildReport.class);

		URL testrayCaseResultURL = new URL(
			"https://" + RandomTestUtil.randomString());

		Mockito.when(
			downstreamBuildReport.getTestrayCaseResultURL()
		).thenReturn(
			testrayCaseResultURL
		);

		TopLevelBuildReport topLevelBuildReport = _mockTopLevelBuildReport(
			downstreamBuildReport);

		Mockito.when(
			topLevelBuildReport.getTestrayCaseResultURL()
		).thenReturn(
			testrayCaseResultURL
		);

		PortalLogBatchBuildTestrayCaseResult
			portalLogBatchBuildTestrayCaseResult =
				new PortalLogBatchBuildTestrayCaseResult(
					_mockAxisTestClassGroup(), _mockTestrayBuild(),
					topLevelBuildReport);

		testEquals(
			null,
			portalLogBatchBuildTestrayCaseResult.getTestrayCaseResultURL());

		SFBatchBuildTestrayCaseResult sfBatchBuildTestrayCaseResult =
			new SFBatchBuildTestrayCaseResult(
				_mockAxisTestClassGroup(), _mockTestrayBuild(),
				topLevelBuildReport);

		testEquals(
			null, sfBatchBuildTestrayCaseResult.getTestrayCaseResultURL());
	}

	private AxisTestClassGroup _mockAxisTestClassGroup() {
		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		Mockito.when(
			axisTestClassGroup.getAxisName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return axisTestClassGroup;
	}

	private TestrayBuild _mockTestrayBuild() {
		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		Mockito.when(
			testrayBuild.getTestrayServer()
		).thenReturn(
			Mockito.mock(TestrayServer.class)
		);

		return testrayBuild;
	}

	private TopLevelBuildReport _mockTopLevelBuildReport(
		DownstreamBuildReport downstreamBuildReport) {

		TopLevelBuildReport topLevelBuildReport = Mockito.mock(
			TopLevelBuildReport.class);

		Mockito.when(
			topLevelBuildReport.getDownstreamBuildReport(Mockito.any())
		).thenReturn(
			downstreamBuildReport
		);

		return topLevelBuildReport;
	}

	private DownstreamBatchBuildTestrayCaseResult
		_newDownstreamBatchBuildTestrayCaseResult(
			DownstreamBuildReport downstreamBuildReport) {

		return new DownstreamBatchBuildTestrayCaseResult(
			_mockAxisTestClassGroup(), _mockTestrayBuild(),
			_mockTopLevelBuildReport(downstreamBuildReport));
	}

}