/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public interface TopLevelBuild extends ParentBuild {

	public String getAcceptanceUpstreamJobName();

	public String getAcceptanceUpstreamJobURL();

	public long getAverageDelayTime();

	public Map<String, String> getBaseGitRepositoryDetailsTempMap();

	public Build getControllerBuild();

	public AxisBuild getDownstreamAxisBuild(String axisName);

	public List<AxisBuild> getDownstreamAxisBuilds();

	public BatchBuild getDownstreamBatchBuild(String jobVariant);

	public List<BatchBuild> getDownstreamBatchBuilds();

	public DownstreamBuild getDownstreamBuild(String axisName);

	public String getJenkinsReport();

	public Element getJenkinsReportElement();

	public String getJenkinsReportURL();

	public File getJobSummaryDir();

	public int getJobVariantsDownstreamBuildCount(
		List<String> jobVariants, String result, String status);

	public List<Build> getJobVariantsDownstreamBuilds(
		Iterable<String> jobVariants, String result, String status);

	public List<String> getProjectNames();

	public String getStatusSummary();

	public TopLevelBuildReport getTopLevelBuildReport();

	public Element getValidationGitHubMessageElement();

}