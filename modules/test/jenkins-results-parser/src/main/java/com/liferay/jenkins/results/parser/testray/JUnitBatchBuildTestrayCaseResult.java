/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.JUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public class JUnitBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult {

	public JUnitBatchBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport,
		AxisTestClassGroup axisTestClassGroup, TestClass testClass) {

		super(testrayBuild, topLevelBuildReport, axisTestClassGroup);

		_jUnitTestClass = (JUnitTestClass)testClass;
	}

	@Override
	public String getComponentName() {
		String componentName = _jUnitTestClass.getTestrayMainComponentName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(componentName)) {
			return super.getComponentName();
		}

		return componentName;
	}

	@Override
	public long getDuration() {
		List<TestClassReport> testClassReports = getTestClassReports();

		if (testClassReports == null) {
			return 0;
		}

		long duration = 0;

		for (TestClassReport testClassReport : testClassReports) {
			duration += testClassReport.getDuration();
		}

		return duration;
	}

	@Override
	public String getErrors() {
		List<TestClassReport> testClassReports = getTestClassReports();

		if ((testClassReports == null) || testClassReports.isEmpty()) {
			BuildReport buildReport = getBuildReport();

			if (buildReport == null) {
				return "Unable to run build on CI";
			}

			String result = buildReport.getResult();

			if (result == null) {
				return "Unable to finish build on CI";
			}

			if (result.equals("ABORTED")) {
				return buildReport.getJobName() + " timed out after 2 hours";
			}

			if (result.equals("SUCCESS") || result.equals("UNSTABLE")) {
				return "Unable to run test on CI";
			}

			return "Failed prior to running test";
		}

		if (!_isTestClassReportsFailing() && !_isTestClassReportsSkipped()) {
			return null;
		}

		Map<String, String> errorMessages = new HashMap<>();
		List<String> skippedTestNames = new ArrayList<>();

		for (TestReport testReport : getTestReports()) {
			if ((testReport == null) ||
				(!testReport.isFailing() && !testReport.isSkipped())) {

				continue;
			}

			String errorMessage = testReport.getErrorDetails();

			if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
				errorMessage = null; //buildReport.getFailureMessage();
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
				errorMessage = "Failed for unknown reason";
			}

			if (errorMessage.contains("\n")) {
				errorMessage = errorMessage.substring(
					0, errorMessage.indexOf("\n"));
			}

			errorMessage = errorMessage.trim();

			if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
				errorMessage = "Failed for unknown reason";
			}

			String testName = testReport.getTestName();

			if (testReport.isSkipped()) {
				skippedTestNames.add(testName);
			}
			else {
				errorMessages.put(
					testName,
					JenkinsResultsParserUtil.combine(
						testName, ": ", errorMessage));
			}
		}

		StringBuilder sb = new StringBuilder();

		if (!skippedTestNames.isEmpty()) {
			sb.append(skippedTestNames.size());
			sb.append(" Skipped ");
			sb.append(
				JenkinsResultsParserUtil.getNounForm(
					skippedTestNames.size(), "tests", "test"));
			sb.append("\n    ");
			sb.append(
				JenkinsResultsParserUtil.join("\n    ", skippedTestNames));
		}

		if (!errorMessages.isEmpty()) {
			if (sb.length() > 0) {
				sb.append("\n\n");
			}

			if (errorMessages.size() == 1) {
				List<String> values = new ArrayList<>(errorMessages.values());

				sb.append(values.get(0));
			}
			else {
				sb.append(errorMessages.size());
				sb.append(" Failed tests");
				sb.append("\n    ");
				sb.append(
					JenkinsResultsParserUtil.join(
						"\n     ", new ArrayList<>(errorMessages.keySet())));
			}
		}

		if (sb.length() > 0) {
			return sb.toString();
		}

		return "Failed for unknown reason";
	}

	@Override
	public String getName() {
		String testClassName = JenkinsResultsParserUtil.getCanonicalPath(
			_jUnitTestClass.getTestClassFile());

		testClassName = testClassName.replaceAll(".*/(com/.*)\\.java", "$1");

		return testClassName.replace("/", ".");
	}

	@Override
	public Status getStatus() {
		BuildReport buildReport = getBuildReport();

		if (buildReport == null) {
			return Status.UNTESTED;
		}

		List<TestClassReport> testClassReports = getTestClassReports();

		if ((testClassReports == null) || testClassReports.isEmpty()) {
			String result = buildReport.getResult();

			if ((result == null) || result.equals("ABORTED") ||
				result.equals("FAILURE") || result.equals("SUCCESS") ||
				result.equals("UNSTABLE")) {

				return Status.UNTESTED;
			}

			return Status.FAILED;
		}

		if (_isTestClassReportsSkipped() && _isTestClassReportsFailing()) {
			return Status.INCOMPLETE;
		}

		if (_isTestClassReportsSkipped()) {
			return Status.UNTESTED;
		}

		if (_isTestClassReportsFailing()) {
			return Status.FAILED;
		}

		return Status.PASSED;
	}

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments =
			super.getTestrayAttachments();

		testrayAttachments.add(getFailureMessagesTestrayAttachment());
		testrayAttachments.addAll(getLiferayLogTestrayAttachments());
		testrayAttachments.addAll(getLiferayOSGiLogTestrayAttachments());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	protected TestrayAttachment getFailureMessagesTestrayAttachment() {
		List<TestClassReport> testClassReports = getTestClassReports();

		if ((testClassReports == null) || testClassReports.isEmpty()) {
			return null;
		}

		TestrayAttachment testrayAttachment = getTestrayAttachment(
			getBuildReport(), "Failure Messages",
			getAxisBuildURLPath() + "/" + getName() + ".txt.gz");

		if (testrayAttachment == null) {
			return null;
		}

		return testrayAttachment;
	}

	@Override
	protected List<TestrayAttachment> getLiferayLogTestrayAttachments() {
		List<TestClassReport> testClassReports = getTestClassReports();

		if ((testClassReports == null) || testClassReports.isEmpty()) {
			return new ArrayList<>();
		}

		return super.getLiferayLogTestrayAttachments();
	}

	@Override
	protected List<TestrayAttachment> getLiferayOSGiLogTestrayAttachments() {
		List<TestClassReport> testClassReports = getTestClassReports();

		if ((testClassReports == null) || testClassReports.isEmpty()) {
			return new ArrayList<>();
		}

		return super.getLiferayOSGiLogTestrayAttachments();
	}

	protected List<TestClassReport> getTestClassReports() {
		if (_testClassReports != null) {
			return _testClassReports;
		}

		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		if (downstreamBuildReport == null) {
			return null;
		}

		_testClassReports = new ArrayList<>();

		for (TestClassReport testClassReport :
				downstreamBuildReport.getTestClassReports()) {

			String testClassName = testClassReport.getTestClassName();

			if (testClassName.equals(getName()) ||
				testClassName.startsWith(getName() + "$")) {

				_testClassReports.add(testClassReport);

				continue;
			}

			if (testClassName.equals("junit.framework.TestSuite")) {
				for (TestReport testReport : testClassReport.getTestReports()) {
					String testName = testReport.getTestName();

					if (testName.equals(getName())) {
						_testClassReports.add(testClassReport);

						break;
					}
				}
			}
		}

		return _testClassReports;
	}

	protected List<TestReport> getTestReports() {
		List<TestReport> testReports = new ArrayList<>();

		for (TestClassReport testClassReport : getTestClassReports()) {
			String testClassName = testClassReport.getTestClassName();

			if (!testClassName.equals("junit.framework.TestSuite")) {
				testReports.addAll(testClassReport.getTestReports());

				continue;
			}

			for (TestReport testReport : testClassReport.getTestReports()) {
				String testName = testReport.getTestName();

				if (testName.equals(getName())) {
					testReports.add(testReport);
				}
			}
		}

		return testReports;
	}

	private boolean _isTestClassReportsFailing() {
		for (TestClassReport testClassReport : getTestClassReports()) {
			if (testClassReport.isFailing()) {
				return true;
			}
		}

		return false;
	}

	private boolean _isTestClassReportsSkipped() {
		for (TestClassReport testClassReport : getTestClassReports()) {
			if (testClassReport.isSkipped()) {
				return true;
			}
		}

		return false;
	}

	private final JUnitTestClass _jUnitTestClass;
	private List<TestClassReport> _testClassReports;

}