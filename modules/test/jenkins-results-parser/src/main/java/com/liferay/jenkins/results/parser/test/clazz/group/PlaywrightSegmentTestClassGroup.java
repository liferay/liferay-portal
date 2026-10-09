/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.job.property.JobProperty;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class PlaywrightSegmentTestClassGroup extends SegmentTestClassGroup {

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = super.getJSONObject();

		if (_projectName != null) {
			jsonObject.put("project_name", _projectName);
		}

		return jsonObject;
	}

	public String getProjectName() {
		return _projectName;
	}

	@Override
	public String getTestCasePropertiesContent() {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getTestCasePropertiesContent());

		PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
			(PlaywrightBatchTestClassGroup)getBatchTestClassGroup();

		if (playwrightBatchTestClassGroup.testRelevantChanges) {
			List<JobProperty> jobProperties =
				playwrightBatchTestClassGroup.
					getRelevantPlaywrightJobProperties();

			List<JobProperty> playwrightTestProjectJobProperties =
				_getJobProperties("playwright.test.project", jobProperties);

			String playwrightTestProjectProperty = _concatPropertyValues(
				playwrightTestProjectJobProperties, " ");

			if (playwrightTestProjectProperty != null) {
				sb.append(playwrightTestProjectProperty);
				sb.append("\n");
			}
		}
		else {
			JobProperty jobProperty =
				playwrightBatchTestClassGroup.getJobProperty(
					"playwright.test.project",
					playwrightBatchTestClassGroup.testSuiteName,
					playwrightBatchTestClassGroup.batchName);

			if (jobProperty.getValue() != null) {
				sb.append(jobProperty.getBasePropertyName());
				sb.append("=");
				sb.append(jobProperty.getValue());
				sb.append("\n");
			}
		}

		int axisCount = getAxisCount();

		if (axisCount >= 1) {
			for (int axisIndex = 0; axisIndex < getAxisCount(); axisIndex++) {
				sb.append("PLAYWRIGHT_ARGS_");
				sb.append(axisIndex);
				sb.append("=--shard=");
				sb.append(axisIndex + 1);
				sb.append("/");
				sb.append(axisCount);
				sb.append("\n");
			}
		}

		sb.append("PLAYWRIGHT_PROJECT_NAME=");
		sb.append(getProjectName());

		String workspaceName = getWorkspaceName();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(workspaceName)) {
			sb.append("\nPLAYWRIGHT_WORKSPACE_NAME=");
			sb.append(workspaceName);
		}

		return sb.toString();
	}

	public String getWorkspaceName() {
		for (AxisTestClassGroup axisTestClassGroup : getAxisTestClassGroups()) {
			PlaywrightAxisTestClassGroup playwrightAxisTestClassGroup =
				(PlaywrightAxisTestClassGroup)axisTestClassGroup;

			String workspaceName =
				playwrightAxisTestClassGroup.getWorkspaceName();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(workspaceName)) {
				return workspaceName;
			}
		}

		return null;
	}

	@Override
	public boolean isTestAnalyticsCloud() {
		if (_testAnalyticsCloud != null) {
			return _testAnalyticsCloud;
		}

		for (AxisTestClassGroup axisTestClassGroup : getAxisTestClassGroups()) {
			PlaywrightAxisTestClassGroup playwrightAxisTestClassGroup =
				(PlaywrightAxisTestClassGroup)axisTestClassGroup;

			if (playwrightAxisTestClassGroup.isAnalyticsCloudEnabled()) {
				_testAnalyticsCloud = true;

				break;
			}

			_testAnalyticsCloud = false;
		}

		return _testAnalyticsCloud;
	}

	public void setProjectName(String projectName) {
		_projectName = projectName;
	}

	protected PlaywrightSegmentTestClassGroup(
		BatchTestClassGroup parentBatchTestClassGroup) {

		super(parentBatchTestClassGroup);
	}

	protected PlaywrightSegmentTestClassGroup(
		BatchTestClassGroup parentBatchTestClassGroup, JSONObject jsonObject) {

		super(parentBatchTestClassGroup, jsonObject);

		if (jsonObject.has("project_name")) {
			_projectName = jsonObject.getString("project_name");
		}
	}

	@Override
	protected String getBaseSlaveLabel() {
		if (_baseSlaveLabel != null) {
			return _baseSlaveLabel;
		}

		return super.getBaseSlaveLabel();
	}

	protected void setBaseSlaveLabel(String baseSlaveLabel) {
		_baseSlaveLabel = baseSlaveLabel;
	}

	private String _concatPropertyValues(
		List<JobProperty> jobProperties, String delimiter) {

		if (jobProperties.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (JobProperty jobProperty : jobProperties) {
			if (jobProperty.getValue() == null) {
				continue;
			}

			sb.append(jobProperty.getValue());
			sb.append(delimiter);
		}

		if (sb.length() > 0) {
			JobProperty jobProperty = jobProperties.get(0);

			sb.insert(0, "=");
			sb.insert(0, jobProperty.getBasePropertyName());

			sb.setLength(sb.length() - delimiter.length());
		}

		return sb.toString();
	}

	private List<JobProperty> _getJobProperties(
		String propertyName, List<JobProperty> jobProperties) {

		List<JobProperty> filteredJobProperties = new ArrayList<>();

		for (JobProperty jobProperty : jobProperties) {
			if (!propertyName.equals(jobProperty.getBasePropertyName())) {
				continue;
			}

			filteredJobProperties.add(jobProperty);
		}

		return filteredJobProperties;
	}

	private String _baseSlaveLabel;
	private String _projectName;
	private Boolean _testAnalyticsCloud;

}