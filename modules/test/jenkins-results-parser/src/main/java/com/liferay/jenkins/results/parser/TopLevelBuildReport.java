/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.testray.TestrayCloudObject;

import java.net.URL;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public interface TopLevelBuildReport extends BuildReport {

	public void addDownstreamBuildReport(
		DownstreamBuildReport downstreamBuildReport);

	public void addDownstreamBuildReports(
		List<DownstreamBuildReport> downstreamBuildReports);

	public void addTestrayAttachmentURL(URL testrayAttachmentURL);

	public Map<String, String> getBuildParameters();

	public Job.BuildProfile getBuildProfile();

	public URL getBuildReportJSONTestrayURL();

	public URL getBuildReportJSONUserContentURL();

	public TestrayCloudObject getBuildReportTestrayCloudObject();

	public ControllerBuildReport getControllerBuildReport();

	public DownstreamBuildReport getDownstreamBuildReport(String axisName);

	public List<DownstreamBuildReport> getDownstreamBuildReports();

	public URL getJenkinsReportURL();

	public JobReport getJobReport();

	public String getTestrayBuildDateString();

	public URL getTestResultsJSONUserContentURL();

	public String getTestSuiteName();

	public long getTopLevelActiveDuration();

	public long getTopLevelPassiveDuration();

	public long getTotalDuration();

}