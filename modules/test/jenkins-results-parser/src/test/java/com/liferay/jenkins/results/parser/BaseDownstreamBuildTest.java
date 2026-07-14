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
	public void testGetGitHubMessageElement() {
		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.when(
			baseDownstreamBuild.getStatus()
		).thenReturn(
			"completed"
		);

		Mockito.when(
			baseDownstreamBuild.getResult()
		).thenReturn(
			"UNSTABLE"
		);

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

		String uniqueFailureMarker = RandomTestUtil.randomString();
		String upstreamJobFailureMarker = RandomTestUtil.randomString();

		Element uniqueFailureElement = Dom4JUtil.getNewElement(
			"code", null, uniqueFailureMarker);
		Element upstreamJobFailureElement = Dom4JUtil.getNewElement(
			"code", null, upstreamJobFailureMarker);

		Mockito.when(
			baseDownstreamBuild.getTestResultGitHubElements(
				Mockito.anyList(), Mockito.eq(true))
		).thenReturn(
			Arrays.asList(uniqueFailureElement)
		);

		Mockito.when(
			baseDownstreamBuild.getTestResultGitHubElements(
				Mockito.anyList(), Mockito.eq(false))
		).thenReturn(
			Arrays.asList(upstreamJobFailureElement)
		);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getGitHubMessageElement();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getGitHubMessageUpstreamJobFailureElement();

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

}