/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

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