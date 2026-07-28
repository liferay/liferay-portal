/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Arrays;

import org.dom4j.Element;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class BaseDownstreamBuildTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetAxisName() {
		String axisVariable = RandomTestUtil.randomString();
		String jobVariant = RandomTestUtil.randomString();

		_testGetAxisName(
			axisVariable, jobVariant + "/" + axisVariable, jobVariant);

		_testGetAxisName(axisVariable, null, null);
		_testGetAxisName(null, null, jobVariant);
	}

	@Test
	public void testGetGitHubMessageElement() {
		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.when(
			baseDownstreamBuild.getBatchName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			baseDownstreamBuild.getBuildURL()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			baseDownstreamBuild.getDisplayName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			baseDownstreamBuild.getResult()
		).thenReturn(
			"UNSTABLE"
		);

		Mockito.when(
			baseDownstreamBuild.getStatus()
		).thenReturn(
			"completed"
		);

		String uniqueFailureMarker = RandomTestUtil.randomString();
		String upstreamJobFailureMarker = RandomTestUtil.randomString();

		Element uniqueFailureElement = Dom4JUtil.getNewElement(
			"code", null, uniqueFailureMarker);
		Element upstreamJobFailureElement = Dom4JUtil.getNewElement(
			"code", null, upstreamJobFailureMarker);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getGitHubMessageElement();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getGitHubMessageUpstreamJobFailureElement();

		Mockito.when(
			baseDownstreamBuild.getTestResultGitHubElements(
				Mockito.anyList(), Mockito.eq(false))
		).thenReturn(
			Arrays.asList(upstreamJobFailureElement)
		);

		Mockito.when(
			baseDownstreamBuild.getTestResultGitHubElements(
				Mockito.anyList(), Mockito.eq(true))
		).thenReturn(
			Arrays.asList(uniqueFailureElement)
		);

		Element gitHubMessageElement =
			baseDownstreamBuild.getGitHubMessageElement();

		String gitHubMessageXML = gitHubMessageElement.asXML();

		Assert.assertFalse(gitHubMessageXML.contains(upstreamJobFailureMarker));
		Assert.assertTrue(gitHubMessageXML.contains(uniqueFailureMarker));

		Element gitHubMessageUpstreamJobFailureElement =
			baseDownstreamBuild.getGitHubMessageUpstreamJobFailureElement();

		String gitHubMessageUpstreamJobFailureXML =
			gitHubMessageUpstreamJobFailureElement.asXML();

		Assert.assertFalse(
			gitHubMessageUpstreamJobFailureXML.contains(uniqueFailureMarker));
		Assert.assertTrue(
			gitHubMessageUpstreamJobFailureXML.contains(
				upstreamJobFailureMarker));
	}

	@Test
	public void testSaveBuildURLInBuildDatabase() {
		_testSaveBuildURLInBuildDatabase(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true);
		_testSaveBuildURLInBuildDatabase(
			RandomTestUtil.randomString(), null, false);
		_testSaveBuildURLInBuildDatabase(
			null, RandomTestUtil.randomString(), false);
	}

	private void _testGetAxisName(
		String axisVariable, String expectedAxisName, String jobVariant) {

		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getAxisName();

		Mockito.when(
			baseDownstreamBuild.getAxisVariable()
		).thenReturn(
			axisVariable
		);

		Mockito.when(
			baseDownstreamBuild.getJobVariant()
		).thenReturn(
			jobVariant
		);

		Assert.assertEquals(
			expectedAxisName, baseDownstreamBuild.getAxisName());
	}

	private void _testSaveBuildURLInBuildDatabase(
		String buildURL, String jobVariant, boolean saved) {

		String axisVariable = RandomTestUtil.randomString();

		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);
		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getAxisName();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).saveBuildURLInBuildDatabase();

		Mockito.when(
			baseDownstreamBuild.getAxisVariable()
		).thenReturn(
			axisVariable
		);

		Mockito.when(
			baseDownstreamBuild.getBuildDatabase()
		).thenReturn(
			buildDatabase
		);

		Mockito.when(
			baseDownstreamBuild.getBuildURL()
		).thenReturn(
			buildURL
		);

		Mockito.when(
			baseDownstreamBuild.getJobVariant()
		).thenReturn(
			jobVariant
		);

		baseDownstreamBuild.saveBuildURLInBuildDatabase();

		if (saved) {
			Mockito.verify(
				buildDatabase
			).putProperty(
				BaseBuild.BUILD_URLS_PROPERTIES_KEY,
				jobVariant + "/" + axisVariable, buildURL, false
			);
		}
		else {
			Mockito.verifyNoInteractions(buildDatabase);
		}
	}

}