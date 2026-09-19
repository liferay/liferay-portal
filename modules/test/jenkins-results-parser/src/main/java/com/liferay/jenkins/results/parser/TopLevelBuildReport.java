/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.testray.TestrayCloudObject;

import java.net.URL;

import java.util.List;

/**
 * @author Michael Hashimoto
 */
public interface TopLevelBuildReport extends BuildReport {

	public void addDownstreamBuildReport(
		DownstreamBuildReport downstreamBuildReport);

	public void addDownstreamBuildReports(
		List<DownstreamBuildReport> downstreamBuildReports);

	public void addTestrayAttachmentURL(URL testrayAttachmentURL);

	public Job.BuildProfile getBuildProfile();

	public URL getBuildReportJSONTestrayURL();

	public URL getBuildReportJSONUserContentURL();

	public TestrayCloudObject getBuildReportTestrayCloudObject();

	public ControllerBuildReport getControllerBuildReport();

	public List<FailureReport> getDistinctFailureReports();

	public DownstreamBuildReport getDownstreamBuildReport(String axisName);

	public List<DownstreamBuildReport> getDownstreamBuildReports();

	public URL getJenkinsReportURL();

	public JobReport getJobReport();

	public TopLevelBuildReport getPreviousTopLevelBuildReport();

	public URL getTestResultsJSONUserContentURL();

	public String getTestSuiteName();

	public String getTestrayBuildDateString();

	public long getTopLevelActiveDuration();

	public long getTopLevelPassiveDuration();

	public long getTotalActualDuration();

	public long getTotalCachedDuration();

	public long getTotalDuration();

	public List<FailureReport> getUniqueFailureReports();

}