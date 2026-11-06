/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class BaseTestClassResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestClassReportURL() {
		BaseTestClassResult baseTestClassResult = Mockito.mock(
			BaseTestClassResult.class);

		Build build = Mockito.mock(Build.class);

		Mockito.when(
			build.getBuildURL()
		).thenReturn(
			"https://test-1-1.liferay.com/job/test%20portal(master)/12/"
		);

		Mockito.when(
			baseTestClassResult.getBuild()
		).thenReturn(
			build
		);

		Mockito.when(
			baseTestClassResult.getPackageName()
		).thenReturn(
			"com.liferay.portal"
		);

		Mockito.when(
			baseTestClassResult.getSimpleClassName()
		).thenReturn(
			"FooTest"
		);

		Mockito.doCallRealMethod(
		).when(
			baseTestClassResult
		).getTestClassReportURL();

		testEquals(
			"https://test-1-1.liferay.com/job/test%20portal(master)/12" +
				"/testReport/com.liferay.portal/FooTest",
			baseTestClassResult.getTestClassReportURL());
	}

}