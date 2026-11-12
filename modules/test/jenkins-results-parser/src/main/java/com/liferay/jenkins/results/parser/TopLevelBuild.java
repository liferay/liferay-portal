/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.testray.TestrayContext;

import java.io.File;

import java.net.URL;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public interface TopLevelBuild extends ParentBuild {

	public void addCachedDownstreamBuildReport(
		DownstreamBuildReport downstreamBuildReport);

	public String getAcceptanceUpstreamJobName();

	public String getAcceptanceUpstreamJobURL();

	public long getAverageDelayTime();

	public Map<String, String> getBaseGitRepositoryDetailsTempMap();

	public Build getControllerBuild();

	public DownstreamBuild getDownstreamBuild(String axisName);

	public String getJenkinsReport();

	public Element getJenkinsReportElement();

	public String getJenkinsReportURL();

	public File getJobSummaryDir();

	public List<Build> getJobVariantsDownstreamBuilds(
		Iterable<String> jobVariants, String result, String status);

	public List<String> getProjectNames();

	public String getStatusSummary();

	public URL getTestrayCaseResultURL();

	public TestrayContext getTestrayContext();

	public TopLevelBuildReport getTopLevelBuildReport();

	public long getTotalActualDuration();

	public int getTotalActualSlavesUsedCount();

	public long getTotalCachedDuration();

	public int getTotalCachedSlavesUsedCount();

	public Element getValidationGitHubMessageElement();

}