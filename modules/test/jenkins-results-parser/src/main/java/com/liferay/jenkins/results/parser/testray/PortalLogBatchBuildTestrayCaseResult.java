/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.io.IOException;

/**
 * @author Michael Hashimoto
 */
public class PortalLogBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult {

	public PortalLogBatchBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport,
		AxisTestClassGroup axisTestClassGroup) {

		super(testrayBuild, topLevelBuildReport, axisTestClassGroup);
	}

	@Override
	public String getComponentName() {
		try {
			return JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"testray.case.component");
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public long getDuration() {
		TestClassReport testClassReport = _getTestClassReport();

		if (testClassReport == null) {
			return 0;
		}

		return testClassReport.getDuration();
	}

	@Override
	public String getErrors() {
		TestClassReport testClassReport = _getTestClassReport();

		if (testClassReport == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (TestReport testReport : testClassReport.getTestReports()) {
			if (!testReport.isFailing()) {
				continue;
			}

			sb.append("PortalLogAssertorTest#");
			sb.append(testReport.getTestName());
			sb.append(": ");

			String errorDetails = testReport.getErrorDetails();

			if (JenkinsResultsParserUtil.isNullOrEmpty(errorDetails)) {
				sb.append("Failed for unknown reason | ");
			}
			else {
				errorDetails = errorDetails.replace(
					"Portal log assert failure, see above log for more " +
						"information:",
					"");

				errorDetails = errorDetails.trim();

				if (errorDetails.length() > 1000) {
					errorDetails = errorDetails.substring(0, 1000);

					errorDetails += "...";
				}

				sb.append(errorDetails);
				sb.append(" | ");
			}
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 3);

			return sb.toString();
		}

		return null;
	}

	@Override
	public String getName() {
		return "PortalLogAssertorTest-" + getAxisName();
	}

	private TestClassReport _getTestClassReport() {
		if (_testClassReport != null) {
			return _testClassReport;
		}

		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		if ((downstreamBuildReport == null) ||
			!downstreamBuildReport.isFailing()) {

			return null;
		}

		String result = downstreamBuildReport.getResult();

		if (result == null) {
			return null;
		}

		for (TestClassReport testClassReport :
				downstreamBuildReport.getTestClassReports()) {

			String className = testClassReport.getTestClassName();

			if (!className.equals(
					"com.liferay.portal.log.assertor.PortalLogAssertorTest")) {

				continue;
			}

			_testClassReport = testClassReport;

			return _testClassReport;
		}

		return null;
	}

	private TestClassReport _testClassReport;

}