/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

/**
 * @author Charlotte Wong
 */
public class DownstreamTestClassBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult<TestClass, TestClassMethod> {

	public DownstreamTestClassBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestReport testReport,
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport) {

		super(axisTestClassGroup, testrayBuild, topLevelBuildReport);

		_testReport = testReport;
	}

	@Override
	public long getDuration() {
		return getTestResultDuration();
	}

	@Override
	public String getErrors() {
		return getTestResultErrors();
	}

	@Override
	public String getName() {
		return JenkinsResultsParserUtil.combine(
			_testReport.getTestClassName(), "#", _testReport.getTestName());
	}

	@Override
	public Status getStatus() {
		return getTestResultStatus();
	}

	@Override
	protected TestReport findTestReport() {
		return _testReport;
	}

	private final TestReport _testReport;

}