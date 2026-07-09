/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public interface BuildData {

	public static final File DIR_WORKSPACE_DEFAULT = new File(".");

	public File getArtifactDir();

	public String getBuildDescription();

	public Long getBuildDuration();

	public String getBuildDurationString();

	public Integer getBuildNumber();

	public String getBuildParameter(String key);

	public Map<String, String> getBuildParameters();

	public String getBuildResult();

	public String getBuildStatus();

	public String getBuildURL();

	public String getCohortName();

	public Host getHost();

	public String getHostname();

	public String getJenkinsGitHubBranchName();

	public String getJenkinsGitHubRepositoryName();

	public String getJenkinsGitHubURL();

	public String getJenkinsGitHubUsername();

	public String getJobName();

	public String getJobURL();

	public JSONObject getJSONObject();

	public String getMasterHostname();

	public String getRunId();

	public Long getStartTime();

	public String getStartTimeString();

	public TopLevelBuildData getTopLevelBuildData();

	public Integer getTopLevelBuildNumber();

	public Map<String, String> getTopLevelBuildParameters();

	public String getTopLevelJobName();

	public String getTopLevelMasterHostname();

	public String getTopLevelRunId();

	public String getUserContentRelativePath();

	public File getWorkspaceDir();

	public void setBuildDescription(String buildDescription);

	public void setBuildDuration(Long buildDuration);

	public void setBuildResult(String buildResult);

	public void setBuildStatus(String buildStatus);

	public void setBuildURL(String buildURL);

	public void setInvocationTime(Long invocationTime);

	public void setJenkinsGitHubURL(String jenkinsGitHubURL);

	public void setWorkspaceDir(File workspaceDir);

}