/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestClassReport implements TestClassReport {

	@Override
	public void addTestReport(TestReport testReport) {
		_testReports.add(testReport);
	}

	@Override
	public DownstreamBuildReport getDownstreamBuildReport() {
		return _downstreamBuildReport;
	}

	@Override
	public long getDuration() {
		long duration = 0L;

		for (TestReport testReport : getTestReports()) {
			long testReportDuration = testReport.getDuration();

			if (testReportDuration < 0L) {
				continue;
			}

			duration += testReportDuration;
		}

		return duration;
	}

	@Override
	public String getModuleAppPath() {
		List<TestReport> testReports = getTestReports();

		if (testReports.isEmpty()) {
			return null;
		}

		TestReport testReport = testReports.get(0);

		return testReport.getModuleAppPath();
	}

	@Override
	public long getOverheadDuration() {
		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		long overheadDuration = downstreamBuildReport.getDuration();

		for (TestReport testReport : downstreamBuildReport.getTestReports()) {
			overheadDuration -= testReport.getDuration();
		}

		if (overheadDuration <= 0L) {
			return 0L;
		}

		return overheadDuration;
	}

	@Override
	public String getStatus() {
		for (TestReport testReport : getTestReports()) {
			String status = testReport.getStatus();

			if (status.equals("REGRESSION") || status.equals("FAILED")) {
				return "FAILED";
			}

			if (status.equals("FIXED") || status.equals("PASSED") ||
				status.equals("SKIPPED")) {

				continue;
			}

			throw new RuntimeException("Invalid status " + status);
		}

		return "PASSED";
	}

	@Override
	public String getTestClassName() {
		return _testClassName;
	}

	@Override
	public List<TestReport> getTestReports() {
		return _testReports;
	}

	@Override
	public String getTestTaskName() {
		if (_testReports.isEmpty()) {
			return null;
		}

		TestReport testReport = _testReports.get(0);

		if (testReport == null) {
			return null;
		}

		return testReport.getTestTaskName();
	}

	@Override
	public boolean isFailing() {
		String status = getStatus();

		if (Objects.equals(status, "FIXED") ||
			Objects.equals(status, "PASSED") ||
			Objects.equals(status, "SKIPPED")) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isSkipped() {
		return Objects.equals(getStatus(), "SKIPPED");
	}

	protected BaseTestClassReport(
		DownstreamBuildReport downstreamBuildReport, String testClassName) {

		_downstreamBuildReport = downstreamBuildReport;
		_testClassName = testClassName;
	}

	private final DownstreamBuildReport _downstreamBuildReport;
	private final String _testClassName;
	private final List<TestReport> _testReports = new ArrayList<>();

}