/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.PortalWorkspaceJob;
import com.liferay.jenkins.results.parser.job.property.JobProperty;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class FunctionalSegmentTestClassGroup extends SegmentTestClassGroup {

	public FunctionalAxisTestClassGroup getFunctionalAxisTestClassGroup(
		int segmentIndex) {

		List<FunctionalAxisTestClassGroup> functionalAxisTestClassGroups =
			getFunctionalAxisTestClassGroups();

		return functionalAxisTestClassGroups.get(segmentIndex);
	}

	public List<FunctionalAxisTestClassGroup>
		getFunctionalAxisTestClassGroups() {

		List<FunctionalAxisTestClassGroup> functionalAxisTestClassGroups =
			new ArrayList<>();

		for (AxisTestClassGroup axisTestClassGroup : getAxisTestClassGroups()) {
			if (!(axisTestClassGroup instanceof FunctionalAxisTestClassGroup)) {
				continue;
			}

			FunctionalAxisTestClassGroup functionalAxisTestClassGroup =
				(FunctionalAxisTestClassGroup)axisTestClassGroup;

			functionalAxisTestClassGroups.add(functionalAxisTestClassGroup);
		}

		return functionalAxisTestClassGroups;
	}

	@Override
	public Integer getMinimumSlaveRAM() {
		Properties poshiProperties = getPoshiProperties();

		String minimumSlaveRAM = poshiProperties.getProperty(
			"minimum.slave.ram");

		if ((minimumSlaveRAM != null) && minimumSlaveRAM.matches("\\d+")) {
			return Integer.valueOf(minimumSlaveRAM);
		}

		return super.getMinimumSlaveRAM();
	}

	public Properties getPoshiProperties() {
		List<FunctionalAxisTestClassGroup> functionalAxisTestClassGroups =
			getFunctionalAxisTestClassGroups();

		FunctionalAxisTestClassGroup functionalAxisTestClassGroup =
			functionalAxisTestClassGroups.get(0);

		return functionalAxisTestClassGroup.getPoshiProperties();
	}

	@Override
	public String getSlaveLabel() {
		Properties poshiProperties = getPoshiProperties();

		String slaveLabel = poshiProperties.getProperty("slave.label");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(slaveLabel)) {
			return slaveLabel;
		}

		return super.getSlaveLabel();
	}

	@Override
	public String getTestCasePropertiesContent() {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getTestCasePropertiesContent());

		List<String> axisGroupNames = new ArrayList<>();

		int batchIndex = getBatchIndex();

		for (int axisIndex = 0; axisIndex < getAxisCount(); axisIndex++) {
			axisGroupNames.add(batchIndex + "_" + axisIndex);

			sb.append("RUN_TEST_CASE_METHOD_GROUP_");
			sb.append(batchIndex);
			sb.append("_");
			sb.append(axisIndex);
			sb.append("=");

			FunctionalAxisTestClassGroup functionalAxisTestClassGroup =
				getFunctionalAxisTestClassGroup(axisIndex);

			sb.append(
				JenkinsResultsParserUtil.join(
					",",
					functionalAxisTestClassGroup.getTestClassMethodNames()));

			sb.append("\n");
		}

		sb.append("RUN_TEST_CASE_METHOD_GROUP_");
		sb.append(batchIndex);
		sb.append("=");
		sb.append(JenkinsResultsParserUtil.join(" ", axisGroupNames));
		sb.append("\n");

		String workspacePortalVersion = _getWorkspacePortalVersion();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(workspacePortalVersion)) {
			sb.append("TEST_PORTAL_BUNDLE_VERSION=");
			sb.append(workspacePortalVersion);
			sb.append("\n");
		}

		String workspaceName = _getWorkspaceName();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(workspaceName)) {
			sb.append("TEST_WORKSPACE_NAME=");
			sb.append(workspaceName);
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public boolean isTestAnalyticsCloud() {
		if (_testAnalyticsCloud != null) {
			return _testAnalyticsCloud;
		}

		Properties poshiProperties = getPoshiProperties();

		String analyticsCloudEnabled = poshiProperties.getProperty(
			"analytics.cloud.enabled");

		if ((analyticsCloudEnabled != null) &&
			analyticsCloudEnabled.equals("true")) {

			_testAnalyticsCloud = true;

			return _testAnalyticsCloud;
		}

		_testAnalyticsCloud = false;

		return _testAnalyticsCloud;
	}

	protected FunctionalSegmentTestClassGroup(
		BatchTestClassGroup batchTestClassGroup) {

		super(batchTestClassGroup);

		_batchTestClassGroup = batchTestClassGroup;
	}

	protected FunctionalSegmentTestClassGroup(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);

		_batchTestClassGroup = batchTestClassGroup;
	}

	protected Map.Entry<String, String> getEnvironmentVariableEntry(
		String key, String name) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(key) ||
			JenkinsResultsParserUtil.isNullOrEmpty(name)) {

			return null;
		}

		JobProperty jobProperty = _batchTestClassGroup.getJobProperty(name);

		String value = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(value)) {
			return null;
		}

		return new AbstractMap.SimpleEntry<>(key, value);
	}

	private String _getWorkspaceName() {
		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		if (!(batchTestClassGroup instanceof FunctionalBatchTestClassGroup)) {
			return null;
		}

		FunctionalBatchTestClassGroup functionalBatchTestClassGroup =
			(FunctionalBatchTestClassGroup)batchTestClassGroup;

		return functionalBatchTestClassGroup.getWorkspaceName();
	}

	private String _getWorkspacePortalVersion() {
		String batchName = getBatchName();

		if (!batchName.startsWith("functional-workspaces-")) {
			return null;
		}

		Job job = getJob();

		if (!(job instanceof PortalWorkspaceJob)) {
			return null;
		}

		PortalWorkspaceJob portalWorkspaceJob = (PortalWorkspaceJob)job;

		return portalWorkspaceJob.getWorkspacePortalVersion();
	}

	private final BatchTestClassGroup _batchTestClassGroup;
	private Boolean _testAnalyticsCloud;

}