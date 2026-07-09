/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTopLevelBuildData
	extends BaseBuildData implements TopLevelBuildData {

	@Override
	public void addDownstreamBuildData(BuildData buildData) {
		String downstreamRunIds = optString("downstream_run_ids");

		if (downstreamRunIds == null) {
			downstreamRunIds = buildData.getRunId();
		}
		else {
			downstreamRunIds += "," + buildData.getRunId();
		}

		put("downstream_run_ids", downstreamRunIds);
	}

	@Override
	public List<String> getDistNodes() {
		String distNodes = optString("dist_nodes");

		if (distNodes == null) {
			return null;
		}

		return Arrays.asList(distNodes.split(","));
	}

	@Override
	public String getDistPath() {
		return optString("dist_path");
	}

	@Override
	public List<BuildData> getDownstreamBuildDataList() {
		List<BuildData> downstreamBuildDataList = new ArrayList<>();

		String downstreamRunIds = optString("downstream_run_ids");

		if (downstreamRunIds == null) {
			return downstreamBuildDataList;
		}

		for (String downstreamRunId : downstreamRunIds.split(",")) {
			if ((downstreamRunId == null) || downstreamRunId.isEmpty()) {
				continue;
			}

			downstreamBuildDataList.add(
				BuildDataFactory.newBatchBuildData(
					downstreamRunId, getJobName() + "-batch", null));
		}

		return downstreamBuildDataList;
	}

	@Override
	public String getS3BucketDistPath() {
		return optString("s3_bucket_dist_path");
	}

	@Override
	public TopLevelBuildData getTopLevelBuildData() {
		return this;
	}

	@Override
	public Integer getTopLevelBuildNumber() {
		return getBuildNumber();
	}

	@Override
	public Map<String, String> getTopLevelBuildParameters() {
		return getBuildParameters();
	}

	@Override
	public String getTopLevelJobName() {
		return getJobName();
	}

	@Override
	public String getTopLevelMasterHostname() {
		return getMasterHostname();
	}

	@Override
	public String getTopLevelRunId() {
		return getRunId();
	}

	@Override
	public void setDistNodes(List<String> distNodes) {
		if (distNodes == null) {
			throw new RuntimeException("Dist nodes is null");
		}

		put("dist_nodes", JenkinsResultsParserUtil.join(",", distNodes));
	}

	protected BaseTopLevelBuildData(
		String runId, String jobName, String buildURL) {

		super(_getDefaultRunId(runId), jobName, buildURL);

		put("dist_nodes", _getDistNodes());
		put("dist_path", _getDistPath());
		put("s3_bucket_dist_path", _getS3BucketDistPath());
		put("top_level_run_id", getRunId());

		validateKeys(_KEYS_REQUIRED);
	}

	private static String _getDefaultRunId(String runId) {
		if (runId != null) {
			return runId;
		}

		return "top_level_" + JenkinsResultsParserUtil.getDistinctTimeStamp();
	}

	private String _getDistNodes() {
		if (!JenkinsResultsParserUtil.isCINode() ||
			JenkinsResultsParserUtil.isCloudCINode()) {

			return "";
		}

		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties(
				false);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		List<JenkinsMaster> jenkinsMasters =
			JenkinsResultsParserUtil.getJenkinsMasters(
				buildProperties, JenkinsMaster.getSlaveRAMMinimumDefault(),
				JenkinsMaster.getSlavesPerHostDefault(), getCohortName());

		List<String> distNodes = new ArrayList<>(jenkinsMasters.size());

		for (JenkinsMaster jenkinsMaster : jenkinsMasters) {
			int retries = 0;

			while (true) {
				if (retries > jenkinsMaster.getOnlineJenkinsSlavesCount()) {
					break;
				}

				JenkinsSlave randomJenkinsSlave =
					jenkinsMaster.getRandomJenkinsSlave();

				if ((randomJenkinsSlave != null) &&
					!randomJenkinsSlave.isOffline() &&
					randomJenkinsSlave.isReachable()) {

					distNodes.add(randomJenkinsSlave.getName());

					break;
				}

				retries++;
			}
		}

		return StringUtils.join(distNodes, ",");
	}

	private String _getDistPath() {
		return JenkinsResultsParserUtil.combine(
			JenkinsResultsParserUtil.getJenkinsDistRootPath(), "/",
			getMasterHostname(), "/", getJobName(), "/",
			String.valueOf(getBuildNumber()), "/dist");
	}

	private String _getS3BucketDistPath() {
		try {
			return JenkinsResultsParserUtil.combine(
				JenkinsResultsParserUtil.getBuildProperty(
					"cloud.ci.s3.bucket.dist.path"),
				"/", getMasterHostname(), "/", getJobName(), "/",
				String.valueOf(getBuildNumber()), "/dist");
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final String[] _KEYS_REQUIRED = {
		"dist_nodes", "dist_path", "top_level_run_id"
	};

}