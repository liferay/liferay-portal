/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.lang.reflect.Method;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

/**
 * @author Kenji Heigel
 */
public class BasePortalControllerBuildRunnerTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testExpirePreviousBuild() throws Exception {
		JenkinsMasterTestUtil.getJenkinsMaster("test-1-48", "http://test-1-48");

		String controllerBuildURL =
			"https://test-1-0-aws.liferay.com/job/test-portal-testsuite-" +
				"upstream-controller(master_content-management)/339/";
		String invocationJobName = "test-portal-testsuite-upstream(master)";

		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			new JSONObject(
			).put(
				"items",
				new JSONArray(
				).put(
					new JSONObject(
					).put(
						"actions",
						new JSONArray(
						).put(
							new JSONObject(
							).put(
								"_class", "hudson.model.ParametersAction"
							).put(
								"parameters",
								new JSONArray(
								).put(
									new JSONObject(
									).put(
										"name", "CONTROLLER_BUILD_URL"
									).put(
										"value", controllerBuildURL
									)
								)
							)
						)
					).put(
						"task",
						new JSONObject(
						).put(
							"url",
							"http://test-1-48/job/" + invocationJobName + "/"
						)
					)
				)
			).toString(),
			"queue/api/json", urlReader);

		BasePortalControllerBuildRunner<?> basePortalControllerBuildRunner =
			Mockito.mock(BasePortalControllerBuildRunner.class);

		Mockito.doCallRealMethod(
		).when(
			basePortalControllerBuildRunner
		).expirePreviousBuild();

		Mockito.doReturn(
			Arrays.asList(
				new JSONObject(
				).put(
					"description",
					"<a href=\"https://test-1-48.liferay.com/job/" +
						invocationJobName + "\"><strong>IN QUEUE</strong></a>"
				).put(
					"url", controllerBuildURL
				))
		).when(
			basePortalControllerBuildRunner
		).getPreviousBuildJSONObjects();

		Assert.assertFalse(
			basePortalControllerBuildRunner.expirePreviousBuild());

		Mockito.verify(
			urlReader
		).doRead(
			Mockito.anyBoolean(), Mockito.any(), Mockito.any(),
			Mockito.anyInt(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.contains("queue/api/json")
		);
	}

	@Test
	public void testGetCommitLink() {
		BasePortalControllerBuildRunner<?> basePortalControllerBuildRunner =
			Mockito.mock(BasePortalControllerBuildRunner.class);

		Mockito.doCallRealMethod(
		).when(
			basePortalControllerBuildRunner
		).getCommitLink(
			Mockito.any()
		);

		RemoteGitRef remoteGitRef = Mockito.mock(RemoteGitRef.class);

		Mockito.doReturn(
			"liferay-portal"
		).when(
			remoteGitRef
		).getRepositoryName();

		String sha = RandomTestUtil.randomSHA();

		Mockito.doReturn(
			sha
		).when(
			remoteGitRef
		).getSHA();

		Mockito.doReturn(
			"liferay"
		).when(
			remoteGitRef
		).getUsername();

		testEquals(
			JenkinsResultsParserUtil.combine(
				"<a href=\"https://github.com/liferay/liferay-portal/commit/",
				sha, "\">", sha.substring(0, 7), "</a>"),
			basePortalControllerBuildRunner.getCommitLink(remoteGitRef));
	}

	@Test
	public void testPreviousBuildHasRunningInvocation() throws Exception {
		BasePortalControllerBuildRunner<?> basePortalControllerBuildRunner =
			Mockito.mock(BasePortalControllerBuildRunner.class);

		Mockito.doCallRealMethod(
		).when(
			basePortalControllerBuildRunner
		).previousBuildHasRunningInvocation();

		String controllerBuildURL =
			"https://test-1-0-aws.liferay.com/job/test-portal-testsuite-" +
				"upstream-controller(master-private_stable)/12/";
		String invocationBuildURL =
			"https://test-1-41.liferay.com/job/test-portal-testsuite-" +
				"upstream(master-private)/34/";

		String portalBaseBranchSHA = RandomTestUtil.randomSHA();

		String portalBaseBranchSHAItem = JenkinsResultsParserUtil.combine(
			"<strong>Base Git ID:</strong> <a href=\"https://github.com/",
			"liferay/liferay-portal/commit/", portalBaseBranchSHA, "\">",
			portalBaseBranchSHA.substring(0, 7), "</a>");

		String portalBranchSHA = RandomTestUtil.randomSHA();

		String portalBranchSHAItem = JenkinsResultsParserUtil.combine(
			"<strong>Git ID:</strong> <a href=\"https://github.com/",
			"brianchandotcom/liferay-portal-ee/commit/", portalBranchSHA, "\">",
			portalBranchSHA.substring(0, 7), "</a>");

		String portalGitHubCompareURLItem = JenkinsResultsParserUtil.combine(
			"<strong>Git Compare:</strong> <a href=\"https://github.com/",
			"brianchandotcom/liferay-portal-ee/compare/a...b\">3 commits</a>");

		Mockito.doReturn(
			Arrays.asList(
				new JSONObject(
				).put(
					"description",
					JenkinsResultsParserUtil.combine(
						"<strong>IN PROGRESS</strong> - <a href=\"",
						invocationBuildURL, "\">Build URL</a><ul><li>",
						portalBranchSHAItem, "</li><li>",
						portalGitHubCompareURLItem, "</li><li>",
						portalBaseBranchSHAItem, "</li></ul>")
				).put(
					"url", controllerBuildURL
				))
		).when(
			basePortalControllerBuildRunner
		).getPreviousBuildJSONObjects();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					invocation -> {
						Method method = invocation.getMethod();

						String methodName = method.getName();

						if (methodName.equals("getLocalURL")) {
							return invocation.getArgument(0);
						}

						if (methodName.equals("toJSONObject")) {
							String url = invocation.getArgument(0);

							if (url.equals(
									controllerBuildURL +
										"/injectedEnvVars/api/json")) {

								return new JSONObject(
								).put(
									"envMap",
									new JSONObject(
									).put(
										"BUILD_NUMBER", "12"
									).put(
										"HOSTNAME", "test-1-0-aws"
									).put(
										"JOB_NAME",
										"test-portal-testsuite-upstream-" +
											"controller(master-private_stable)"
									)
								);
							}

							if (url.equals(
									invocationBuildURL +
										"/api/json?tree=result")) {

								return new JSONObject(
								).put(
									"result", "FAILURE"
								);
							}

							throw new AssertionError(
								"No output set for URL: " + url);
						}

						if (methodName.equals("updateBuildDescription")) {
							return null;
						}

						return invocation.callRealMethod();
					})) {

			Assert.assertFalse(
				basePortalControllerBuildRunner.
					previousBuildHasRunningInvocation());

			ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
				String.class);

			jenkinsResultsParserUtilMockedStatic.verify(
				() -> JenkinsResultsParserUtil.updateBuildDescription(
					argumentCaptor.capture(), Mockito.eq(12),
					Mockito.eq(
						"test-portal-testsuite-upstream-controller" +
							"(master-private_stable)"),
					Mockito.eq("test-1-0-aws")));

			testEquals(
				JenkinsResultsParserUtil.combine(
					"<strong style=\"color: red\">FAILURE</strong> - ",
					invocationBuildURL, "<ul><li>", portalBranchSHAItem,
					"</li><li>", portalGitHubCompareURLItem, "</li><li>",
					portalBaseBranchSHAItem, "</li></ul>"),
				argumentCaptor.getValue());
		}
	}

	@Test
	public void testRun() {
		_testRun("false");
		_testRun("true");
	}

	private void _testRun(String allowConcurrentBuildsUniqueSHAString) {
		Environment environment = Mockito.mock(Environment.class);

		Environment.setInstance(environment);

		Mockito.when(
			environment.doGet("ALLOW_CONCURRENT_BUILDS_UNIQUE_SHA")
		).thenReturn(
			allowConcurrentBuildsUniqueSHAString
		);

		BasePortalControllerBuildRunner<?> basePortalControllerBuildRunner =
			Mockito.mock(BasePortalControllerBuildRunner.class);

		Mockito.doCallRealMethod(
		).when(
			basePortalControllerBuildRunner
		).allowConcurrentBuildsUniqueSHA();

		Mockito.doReturn(
			false
		).when(
			basePortalControllerBuildRunner
		).previousBuildHasCurrentSHA();

		Mockito.doCallRealMethod(
		).when(
			basePortalControllerBuildRunner
		).run();

		basePortalControllerBuildRunner.run();

		VerificationMode verificationMode = Mockito.times(1);

		if (allowConcurrentBuildsUniqueSHAString.equals("true")) {
			verificationMode = Mockito.never();
		}

		Mockito.verify(
			basePortalControllerBuildRunner, verificationMode
		).allowConcurrentBuilds();

		Mockito.verify(
			basePortalControllerBuildRunner
		).invokeBuild();
	}

}