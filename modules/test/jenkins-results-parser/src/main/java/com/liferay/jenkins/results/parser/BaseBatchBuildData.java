/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseBatchBuildData
	extends BaseBuildData implements BatchBuildData {

	@Override
	public String getBatchName() {
		return getString("batch_name");
	}

	@Override
	public List<String> getTestList() {
		return getList("test_list");
	}

	@Override
	public TopLevelBuildData getTopLevelBuildData() {
		if (_topLevelBuildData != null) {
			return _topLevelBuildData;
		}

		String topLevelJobName = getJobName();

		topLevelJobName = topLevelJobName.replace("-batch", "");

		TopLevelBuildData topLevelBuildData =
			BuildDataFactory.newTopLevelBuildData(
				getTopLevelRunId(), topLevelJobName, null);

		_topLevelBuildData = topLevelBuildData;

		return _topLevelBuildData;
	}

	@Override
	public Integer getTopLevelBuildNumber() {
		TopLevelBuildData topLevelBuildData = getTopLevelBuildData();

		return topLevelBuildData.getBuildNumber();
	}

	@Override
	public Map<String, String> getTopLevelBuildParameters() {
		TopLevelBuildData topLevelBuildData = getTopLevelBuildData();

		return topLevelBuildData.getBuildParameters();
	}

	@Override
	public String getTopLevelJobName() {
		TopLevelBuildData topLevelBuildData = getTopLevelBuildData();

		return topLevelBuildData.getJobName();
	}

	@Override
	public String getTopLevelMasterHostname() {
		TopLevelBuildData topLevelBuildData = getTopLevelBuildData();

		return topLevelBuildData.getMasterHostname();
	}

	@Override
	public String getTopLevelRunId() {
		return optString("top_level_run_id");
	}

	@Override
	public void setBatchName(String batchName) {
		put("batch_name", batchName);
	}

	@Override
	public void setTestList(List<String> testList) {
		put("test_list", testList);
	}

	protected BaseBatchBuildData(
		String runId, String jobName, String buildURL) {

		super(_getDefaultRunId(runId), jobName, buildURL);

		if (buildURL == null) {
			return;
		}

		_setTopLevelRunId();

		validateKeys(_KEYS_REQUIRED);
	}

	private static String _getDefaultRunId(String runId) {
		if (runId != null) {
			return runId;
		}

		return "batch_" + JenkinsResultsParserUtil.getDistinctTimeStamp();
	}

	private void _setTopLevelRunId() {
		String topLevelRunId = Environment.get("TOP_LEVEL_RUN_ID");

		if (topLevelRunId == null) {
			throw new RuntimeException("Please set TOP_LEVEL_RUN_ID");
		}

		put("top_level_run_id", topLevelRunId);
	}

	private static final String[] _KEYS_REQUIRED = {
		"batch_name", "top_level_run_id", "test_list"
	};

	private TopLevelBuildData _topLevelBuildData;

}