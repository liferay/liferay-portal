/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.WorkspacesCompileTestClass;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Michael Hashimoto
 */
public class WorkspacesCompileBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult
		<WorkspacesCompileTestClass, TestClassMethod> {

	public WorkspacesCompileBatchBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestClass testClass,
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport) {

		super(axisTestClassGroup, testClass, testrayBuild, topLevelBuildReport);
	}

	@Override
	public long getDuration() {
		return getTestResultDuration();
	}

	@Override
	public String getErrors() {
		TestReport testReport = getTestReport();

		if ((testReport == null) || !testReport.isFailing()) {
			return getTestResultErrors();
		}

		String errors = formatErrorMessage(testReport.getErrorStackTrace());

		if (JenkinsResultsParserUtil.isNullOrEmpty(errors)) {
			return getTestResultErrors();
		}

		return errors;
	}

	@Override
	public String getName() {
		WorkspacesCompileTestClass workspacesCompileTestClass = getTestClass();

		if (workspacesCompileTestClass == null) {
			return super.getName();
		}

		return JenkinsResultsParserUtil.combine(
			getBatchName(), "[", workspacesCompileTestClass.getName(), "]");
	}

	@Override
	public Status getStatus() {
		return getTestResultStatus();
	}

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		testrayAttachments.add(getParentTestrayCaseResultTestrayAttachment());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	@Override
	protected TestReport findTestReport() {
		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		if (downstreamBuildReport == null) {
			return null;
		}

		WorkspacesCompileTestClass workspacesCompileTestClass = getTestClass();

		String testClassName = workspacesCompileTestClass.getName();

		testClassName = testClassName.replaceAll("/", ".");

		for (TestClassReport testClassReport :
				downstreamBuildReport.getTestClassReports()) {

			if (!Objects.equals(
					testClassName, testClassReport.getTestClassName())) {

				continue;
			}

			for (TestReport testReport : testClassReport.getTestReports()) {
				return testReport;
			}
		}

		return null;
	}

}