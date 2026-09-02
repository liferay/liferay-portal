/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

import org.junit.Test;

/**
 * @author Calum Ragan
 */
public class ModulesTestReportTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestClassName() {
		testEquals(
			"modules.apps.commerce",
			_getTestClassName("modules.apps.commerce.assemble"));
		testEquals(
			"modules.apps.portal-language",
			_getTestClassName("modules.apps.portal-language.assemble"));
	}

	@Test
	public void testGetTestClassNameWithoutQualifier() {
		testEquals("assemble", _getTestClassName("assemble"));
	}

	@Test
	public void testGetTestName() {
		testEquals("assemble", _getTestName("modules.apps.commerce.assemble"));
		testEquals(
			"compileTestIntegrationJava",
			_getTestName("modules.apps.commerce.compileTestIntegrationJava"));
	}

	@Test
	public void testGetTestNameWithoutQualifier() {
		testEquals("assemble", _getTestName("assemble"));
	}

	private ModulesTestReport _getModulesTestReport(String testName) {
		return new ModulesTestReport(
			null,
			new JSONObject(
			).put(
				"name", testName
			));
	}

	private String _getTestClassName(String testName) {
		ModulesTestReport modulesTestReport = _getModulesTestReport(testName);

		return modulesTestReport.getTestClassName();
	}

	private String _getTestName(String testName) {
		ModulesTestReport modulesTestReport = _getModulesTestReport(testName);

		return modulesTestReport.getTestName();
	}

}