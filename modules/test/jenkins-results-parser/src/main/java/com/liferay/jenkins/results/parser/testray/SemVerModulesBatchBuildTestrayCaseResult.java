/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TestReportFactory;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.ModulesTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael Hashimoto
 */
public class SemVerModulesBatchBuildTestrayCaseResult
	extends ModulesBatchBuildTestrayCaseResult {

	public SemVerModulesBatchBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestClass testClass,
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport) {

		super(axisTestClassGroup, testClass, testrayBuild, topLevelBuildReport);
	}

	@Override
	public TestClassReport getTestClassReport() {
		if (_testClassReport != null) {
			return _testClassReport;
		}

		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		if (downstreamBuildReport == null) {
			return null;
		}

		TestClassReport testClassReport = null;

		Set<String> modulePaths = _getModulePaths();

		for (TestReport testReport : downstreamBuildReport.getTestReports()) {
			String testName = testReport.getTestName();

			if (!testName.contains(_TEST_NAME_PREFIX)) {
				continue;
			}

			String modulePath = testName.substring(
				testName.lastIndexOf("[") + 1);

			modulePath = modulePath.replace("]", "");

			if (!modulePaths.contains(modulePath)) {
				continue;
			}

			if (testClassReport == null) {
				ModulesTestClass modulesTestClass = getTestClass();

				testClassReport = TestReportFactory.newTestClassReport(
					downstreamBuildReport, modulesTestClass.getTestClassName());
			}

			testClassReport.addTestReport(testReport);
		}

		_testClassReport = testClassReport;

		return _testClassReport;
	}

	private Set<String> _getModulePaths() {
		Set<String> modulePaths = new HashSet<>();

		ModulesTestClass modulesTestClass = getTestClass();

		for (TestClassMethod testClassMethod :
				modulesTestClass.getTestClassMethods()) {

			String modulePath = testClassMethod.getName();

			modulePath = modulePath.replace(":", "/");

			modulePath = modulePath.replace("/baseline", "");

			modulePaths.add(modulePath);
		}

		return modulePaths;
	}

	private static final String _TEST_NAME_PREFIX =
		"SemanticVersioningTest.testSemanticVersioning[";

	private TestClassReport _testClassReport;

}