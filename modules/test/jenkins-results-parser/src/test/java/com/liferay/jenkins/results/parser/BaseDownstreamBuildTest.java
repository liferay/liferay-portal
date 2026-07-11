/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Arrays;
import java.util.Collections;

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

		Mockito.when(
			baseDownstreamBuild.getUniqueFailureTestResults()
		).thenReturn(
			Collections.<TestResult>emptyList()
		);

		Mockito.when(
			baseDownstreamBuild.getUpstreamJobFailureTestResults()
		).thenReturn(
			Collections.<TestResult>emptyList()
		);

		Element uniqueFailureElement = Dom4JUtil.getNewElement(
			"code", null, _FAILURE_MARKER_UNIQUE);
		Element upstreamJobFailureElement = Dom4JUtil.getNewElement(
			"code", null, _FAILURE_MARKER_UPSTREAM_JOB);

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

		Assert.assertTrue(gitHubMessageXML.contains(_FAILURE_MARKER_UNIQUE));
		Assert.assertFalse(
			gitHubMessageXML.contains(_FAILURE_MARKER_UPSTREAM_JOB));

		Element gitHubMessageUpstreamJobFailureElement =
			baseDownstreamBuild.getGitHubMessageUpstreamJobFailureElement();

		String gitHubMessageUpstreamJobFailureXML =
			gitHubMessageUpstreamJobFailureElement.asXML();

		Assert.assertTrue(
			gitHubMessageUpstreamJobFailureXML.contains(
				_FAILURE_MARKER_UPSTREAM_JOB));
		Assert.assertFalse(
			gitHubMessageUpstreamJobFailureXML.contains(
				_FAILURE_MARKER_UNIQUE));
	}

	private static final String _FAILURE_MARKER_UNIQUE =
		"FAILURE_MARKER_UNIQUE";

	private static final String _FAILURE_MARKER_UPSTREAM_JOB =
		"FAILURE_MARKER_UPSTREAM_JOB";

}