/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class SingleUpstreamPortalControllerBuildRunnerTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetPortalBaseRemoteGitRef() {
		SingleUpstreamPortalControllerBuildRunner<?>
			singleUpstreamPortalControllerBuildRunner = Mockito.mock(
				SingleUpstreamPortalControllerBuildRunner.class);

		Mockito.doCallRealMethod(
		).when(
			singleUpstreamPortalControllerBuildRunner
		).getPortalBaseRemoteGitRef();

		try (MockedStatic<GitUtil> gitUtilMockedStatic = Mockito.mockStatic(
				GitUtil.class)) {

			Assert.assertNull(
				singleUpstreamPortalControllerBuildRunner.
					getPortalBaseRemoteGitRef());

			gitUtilMockedStatic.verifyNoInteractions();

			RemoteGitRef remoteGitRef = Mockito.mock(RemoteGitRef.class);

			gitUtilMockedStatic.when(
				() -> GitUtil.getRemoteGitRef(_PORTAL_BASE_GITHUB_URL)
			).thenReturn(
				remoteGitRef
			);

			mockEnvironment(
				Collections.singletonMap(
					"PORTAL_BASE_GITHUB_URL", _PORTAL_BASE_GITHUB_URL));

			testSame(
				remoteGitRef,
				singleUpstreamPortalControllerBuildRunner.
					getPortalBaseRemoteGitRef());
			testSame(
				remoteGitRef,
				singleUpstreamPortalControllerBuildRunner.
					getPortalBaseRemoteGitRef());

			gitUtilMockedStatic.verify(
				() -> GitUtil.getRemoteGitRef(_PORTAL_BASE_GITHUB_URL));
		}
	}

	@Test
	public void testInvokeBuild() {
		String portalBaseBranchSHA = RandomTestUtil.randomSHA();
		String portalBranchSHA = RandomTestUtil.randomSHA();

		Map<String, String> invocationParameters = _invokeBuild(
			portalBaseBranchSHA, portalBranchSHA);

		testEquals(
			portalBaseBranchSHA,
			invocationParameters.get("PORTAL_BASE_GIT_COMMIT"));
		testEquals(
			_PORTAL_BASE_GITHUB_URL,
			invocationParameters.get("PORTAL_BASE_GITHUB_URL"));

		String buildDescription = _getBuildDescription();

		testEquals(
			portalBaseBranchSHA,
			_singleUpstreamPortalControllerBuildRunner.
				getDescriptionPortalBaseBranchSHA(buildDescription));
		testEquals(
			portalBranchSHA,
			_singleUpstreamPortalControllerBuildRunner.
				getDescriptionPortalBranchSHA(buildDescription));

		invocationParameters = _invokeBuild(null, portalBranchSHA);

		Assert.assertFalse(
			invocationParameters.containsKey("PORTAL_BASE_GIT_COMMIT"));
		Assert.assertFalse(
			invocationParameters.containsKey("PORTAL_BASE_GITHUB_URL"));

		buildDescription = _getBuildDescription();

		Assert.assertNull(
			_singleUpstreamPortalControllerBuildRunner.
				getDescriptionPortalBaseBranchSHA(buildDescription));
		testEquals(
			portalBranchSHA,
			_singleUpstreamPortalControllerBuildRunner.
				getDescriptionPortalBranchSHA(buildDescription));
	}

	@Test
	public void testPreviousBuildHasCurrentSHA() {
		String otherSHA = RandomTestUtil.randomSHA();
		String portalBaseBranchSHA = RandomTestUtil.randomSHA();
		String portalBranchSHA = RandomTestUtil.randomSHA();

		_testPreviousBuildHasCurrentSHA(
			portalBaseBranchSHA, portalBranchSHA,
			_getDescription(portalBranchSHA, portalBaseBranchSHA), true);
		_testPreviousBuildHasCurrentSHA(
			portalBaseBranchSHA, portalBranchSHA,
			_getDescription(portalBranchSHA, otherSHA), false);
		_testPreviousBuildHasCurrentSHA(
			portalBaseBranchSHA, portalBranchSHA,
			_getDescription(otherSHA, portalBaseBranchSHA), false);
		_testPreviousBuildHasCurrentSHA(
			portalBaseBranchSHA, portalBranchSHA,
			_getDescription(portalBranchSHA, null), false);
		_testPreviousBuildHasCurrentSHA(
			portalBaseBranchSHA, portalBranchSHA,
			"EXPIRE" + _getDescription(portalBranchSHA, portalBaseBranchSHA),
			false);
		_testPreviousBuildHasCurrentSHA(
			portalBaseBranchSHA, portalBranchSHA,
			"SKIPPED" + _getDescription(portalBranchSHA, portalBaseBranchSHA),
			false);
	}

	@Test
	public void testPreviousBuildHasCurrentSHAWithoutPortalBase() {
		String portalBranchSHA = RandomTestUtil.randomSHA();

		_mockSingleUpstreamPortalControllerBuildRunner(null, portalBranchSHA);

		Mockito.doCallRealMethod(
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).getPortalBranchAbbreviatedSHA();

		_setPreviousBuildDescription(
			_getDescription(portalBranchSHA, RandomTestUtil.randomSHA()));

		Assert.assertTrue(
			_singleUpstreamPortalControllerBuildRunner.
				previousBuildHasCurrentSHA());
	}

	private String _getBuildDescription() {
		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			_buildData
		).setBuildDescription(
			argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private String _getDescription(
		String portalBranchSHA, String portalBaseBranchSHA) {

		StringBuilder sb = new StringBuilder();

		sb.append("<strong>IN QUEUE</strong><ul><li>");
		sb.append("<strong>Git ID:</strong> ");
		sb.append("<a href=\"https://github.com/brianchandotcom/");
		sb.append("liferay-portal-ee/commit/");
		sb.append(portalBranchSHA);
		sb.append("\">");
		sb.append(portalBranchSHA.substring(0, 7));
		sb.append("</a></li>");

		if (portalBaseBranchSHA != null) {
			sb.append("<li><strong>Base Git ID:</strong> ");
			sb.append("<a href=\"https://github.com/liferay/liferay-portal/");
			sb.append("commit/");
			sb.append(portalBaseBranchSHA);
			sb.append("\">");
			sb.append(portalBaseBranchSHA.substring(0, 7));
			sb.append("</a></li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	private Map<String, String> _invokeBuild(
		String portalBaseBranchSHA, String portalBranchSHA) {

		if (portalBaseBranchSHA != null) {
			mockEnvironment(
				Collections.singletonMap(
					"PORTAL_BASE_GITHUB_URL", _PORTAL_BASE_GITHUB_URL));
		}

		_mockSingleUpstreamPortalControllerBuildRunner(
			portalBaseBranchSHA, portalBranchSHA);

		Mockito.doCallRealMethod(
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).getPortalBranchAbbreviatedSHA();

		Mockito.doCallRealMethod(
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).invokeBuild();

		Mockito.doReturn(
			_INVOCATION_JOB_URL
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).getInvocationJobURL(
			Mockito.any()
		);

		Mockito.doReturn(
			new HashMap<>()
		).when(
			_buildData
		).getBuildParameters();

		Mockito.doReturn(
			"brianchandotcom"
		).when(
			_buildData
		).getPortalGitHubUsername();

		Mockito.doReturn(
			"liferay-portal-ee"
		).when(
			_buildData
		).getPortalGitHubRepositoryName();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.getRemoteURL(_INVOCATION_JOB_URL)
			).thenReturn(
				_INVOCATION_JOB_URL
			);

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.invokeJenkinsBuild(
					Mockito.eq(_INVOCATION_JOB_URL), Mockito.anyMap())
			).thenReturn(
				1L
			);

			_singleUpstreamPortalControllerBuildRunner.invokeBuild();

			ArgumentCaptor<Map<String, String>> argumentCaptor =
				ArgumentCaptor.forClass(Map.class);

			jenkinsResultsParserUtilMockedStatic.verify(
				() -> JenkinsResultsParserUtil.invokeJenkinsBuild(
					Mockito.eq(_INVOCATION_JOB_URL), argumentCaptor.capture()));

			return argumentCaptor.getValue();
		}
	}

	private void _mockSingleUpstreamPortalControllerBuildRunner(
		String portalBaseBranchSHA, String portalBranchSHA) {

		_buildData = Mockito.mock(ControllerPortalTopLevelBuildData.class);

		Mockito.doReturn(
			portalBranchSHA
		).when(
			_buildData
		).getPortalBranchSHA();

		_singleUpstreamPortalControllerBuildRunner = Mockito.mock(
			SingleUpstreamPortalControllerBuildRunner.class);

		Mockito.doReturn(
			_buildData
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).getBuildData();

		Mockito.doCallRealMethod(
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).getDescriptionPortalBaseBranchSHA(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).getDescriptionPortalBranchSHA(
			Mockito.any()
		);

		RemoteGitRef remoteGitRef = null;

		if (portalBaseBranchSHA != null) {
			remoteGitRef = Mockito.mock(RemoteGitRef.class);

			Mockito.doReturn(
				portalBaseBranchSHA
			).when(
				remoteGitRef
			).getSHA();

			Mockito.doReturn(
				"liferay-portal"
			).when(
				remoteGitRef
			).getRepositoryName();

			Mockito.doReturn(
				"liferay"
			).when(
				remoteGitRef
			).getUsername();
		}

		Mockito.doReturn(
			remoteGitRef
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).getPortalBaseRemoteGitRef();

		Mockito.doCallRealMethod(
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).previousBuildHasCurrentSHA();
	}

	private void _setPreviousBuildDescription(String description) {
		Mockito.doReturn(
			Collections.singletonList(
				new JSONObject(
				).put(
					"description", description
				))
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).getPreviousBuildJSONObjects();
	}

	private void _testPreviousBuildHasCurrentSHA(
		String portalBaseBranchSHA, String portalBranchSHA, String description,
		boolean expected) {

		_mockSingleUpstreamPortalControllerBuildRunner(
			portalBaseBranchSHA, portalBranchSHA);

		_setPreviousBuildDescription(description);

		Assert.assertEquals(
			description, expected,
			_singleUpstreamPortalControllerBuildRunner.
				previousBuildHasCurrentSHA());
	}

	private static final String _INVOCATION_JOB_URL =
		"https://test-1-1.liferay.com/job/test-portal-testsuite-upstream" +
			"(master-private)";

	private static final String _PORTAL_BASE_GITHUB_URL =
		"https://github.com/liferay/liferay-portal/tree/master";

	private ControllerPortalTopLevelBuildData _buildData;
	private SingleUpstreamPortalControllerBuildRunner<?>
		_singleUpstreamPortalControllerBuildRunner;

}