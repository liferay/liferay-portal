/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.lang.reflect.Method;

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
			_PORTAL_BASE_GITHUB_URL,
			invocationParameters.get("PORTAL_BASE_GITHUB_URL"));
		testEquals(
			portalBaseBranchSHA,
			invocationParameters.get("PORTAL_BASE_GIT_COMMIT"));

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
			invocationParameters.containsKey("PORTAL_BASE_GITHUB_URL"));
		Assert.assertFalse(
			invocationParameters.containsKey("PORTAL_BASE_GIT_COMMIT"));

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
			"EXPIRE" + _getDescription(portalBaseBranchSHA, portalBranchSHA),
			false, portalBaseBranchSHA, portalBranchSHA);
		_testPreviousBuildHasCurrentSHA(
			"SKIPPED" + _getDescription(portalBaseBranchSHA, portalBranchSHA),
			false, portalBaseBranchSHA, portalBranchSHA);
		_testPreviousBuildHasCurrentSHA(
			_getDescription(null, portalBranchSHA), false, portalBaseBranchSHA,
			portalBranchSHA);
		_testPreviousBuildHasCurrentSHA(
			_getDescription(otherSHA, portalBranchSHA), false,
			portalBaseBranchSHA, portalBranchSHA);
		_testPreviousBuildHasCurrentSHA(
			_getDescription(portalBaseBranchSHA, otherSHA), false,
			portalBaseBranchSHA, portalBranchSHA);
		_testPreviousBuildHasCurrentSHA(
			_getDescription(portalBaseBranchSHA, portalBranchSHA), true,
			portalBaseBranchSHA, portalBranchSHA);
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
			_getDescription(RandomTestUtil.randomSHA(), portalBranchSHA));

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
		String portalBaseBranchSHA, String portalBranchSHA) {

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
			"liferay-portal-ee"
		).when(
			_buildData
		).getPortalGitHubRepositoryName();

		Mockito.doReturn(
			"brianchandotcom"
		).when(
			_buildData
		).getPortalGitHubUsername();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					invocation -> {
						Method method = invocation.getMethod();

						String methodName = method.getName();

						if (methodName.equals("getRemoteURL")) {
							return invocation.getArgument(0);
						}

						if (methodName.equals("invokeJenkinsBuild")) {
							return 1L;
						}

						return invocation.callRealMethod();
					})) {

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

		Mockito.doCallRealMethod(
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).previousBuildHasCurrentSHA();

		Mockito.doReturn(
			_buildData
		).when(
			_singleUpstreamPortalControllerBuildRunner
		).getBuildData();

		RemoteGitRef remoteGitRef = null;

		if (portalBaseBranchSHA != null) {
			remoteGitRef = Mockito.mock(RemoteGitRef.class);

			Mockito.doReturn(
				"liferay-portal"
			).when(
				remoteGitRef
			).getRepositoryName();

			Mockito.doReturn(
				portalBaseBranchSHA
			).when(
				remoteGitRef
			).getSHA();

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
		String description, boolean expected, String portalBaseBranchSHA,
		String portalBranchSHA) {

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