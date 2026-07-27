/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

/**
 * @author Kenji Heigel
 */
public class BasePortalControllerBuildRunnerTest
	extends com.liferay.jenkins.results.parser.Test {

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

		Mockito.doCallRealMethod(
		).when(
			basePortalControllerBuildRunner
		).run();

		Mockito.doReturn(
			false
		).when(
			basePortalControllerBuildRunner
		).previousBuildHasCurrentSHA();

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