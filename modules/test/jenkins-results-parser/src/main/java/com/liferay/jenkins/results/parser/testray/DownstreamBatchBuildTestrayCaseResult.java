/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.Objects;

/**
 * @author Michael Hashimoto
 */
public class DownstreamBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult<TestClass, TestClassMethod> {

	public DownstreamBatchBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestrayBuild testrayBuild,
		TopLevelBuildReport topLevelBuildReport) {

		super(axisTestClassGroup, testrayBuild, topLevelBuildReport);
	}

	@Override
	public String getErrors() {
		Status status = getStatus();

		if (status == Status.PASSED) {
			return null;
		}

		return super.getErrors();
	}

	@Override
	public Status getStatus() {
		BuildReport buildReport = getBuildReport();

		if ((buildReport != null) &&
			Objects.equals(buildReport.getResult(), "UNSTABLE")) {

			return Status.PASSED;
		}

		return super.getStatus();
	}

}