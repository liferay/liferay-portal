/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.BatchHistory;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class SegmentTestClassGroup extends BaseTestClassGroup {

	public void addAxisTestClassGroup(AxisTestClassGroup axisTestClassGroup) {
		_axisTestClassGroups.add(axisTestClassGroup);

		axisTestClassGroup.setSegmentTestClassGroup(this);
	}

	public int getAxisCount() {
		return _axisTestClassGroups.size();
	}

	public AxisTestClassGroup getAxisTestClassGroup(int segmentIndex) {
		return _axisTestClassGroups.get(segmentIndex);
	}

	public List<AxisTestClassGroup> getAxisTestClassGroups() {
		return new ArrayList<>(_axisTestClassGroups);
	}

	public BatchHistory getBatchHistory() {
		return _batchTestClassGroup.getBatchHistory();
	}

	public int getBatchIndex() {
		List<SegmentTestClassGroup> segmentTestClassGroups =
			_batchTestClassGroup.getSegmentTestClassGroups();

		return segmentTestClassGroups.indexOf(this);
	}

	public String getBatchJobName() {
		return _batchTestClassGroup.getBatchJobName();
	}

	public String getBatchName() {
		return _batchTestClassGroup.getBatchName();
	}

	public BatchTestClassGroup getBatchTestClassGroup() {
		return _batchTestClassGroup;
	}

	public String getCohortName() {
		return _batchTestClassGroup.getCohortName();
	}

	public String getDownstreamJobName() {
		return _batchTestClassGroup.getDownstreamJobName();
	}

	@Override
	public Job getJob() {
		return _batchTestClassGroup.getJob();
	}

	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();

		JSONArray axesJSONArray = new JSONArray();

		for (AxisTestClassGroup axisTestClassGroup : getAxisTestClassGroups()) {
			axesJSONArray.put(axisTestClassGroup.getJSONObject());
		}

		jsonObject.put(
			"axes", axesJSONArray
		).put(
			"segment_name", getSegmentName()
		);

		return jsonObject;
	}

	public Integer getMaximumSlavesPerHost() {
		return _batchTestClassGroup.getMaximumSlavesPerHost();
	}

	public Integer getMinimumSlaveRAM() {
		return _batchTestClassGroup.getMinimumSlaveRAM();
	}

	public String getSegmentName() {
		return JenkinsResultsParserUtil.combine(
			getBatchName(), "/", String.valueOf(getBatchIndex()));
	}

	public String getSlaveLabel() {
		if (!JenkinsResultsParserUtil.isCloudCINode()) {
			return _getSlaveLabel();
		}

		String slaveLabel = null;

		try {
			slaveLabel = JenkinsResultsParserUtil.getBuildProperty(
				"jenkins.osb.jenkins.web.slave.label.minimum.ram",
				String.valueOf(getMinimumSlaveRAM()));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(slaveLabel)) {
			return slaveLabel;
		}

		return _getSlaveLabel();
	}

	public File getTestBaseDir() {
		List<AxisTestClassGroup> axisTestClassGroups = getAxisTestClassGroups();

		if ((axisTestClassGroups == null) || axisTestClassGroups.isEmpty()) {
			return null;
		}

		AxisTestClassGroup axisTestClassGroup = axisTestClassGroups.get(0);

		return axisTestClassGroup.getTestBaseDir();
	}

	public String getTestCasePropertiesContent() {
		StringBuilder sb = new StringBuilder();

		if (isTestAnalyticsCloud()) {
			sb.append("TEST_ANALYTICS_CLOUD=true\n");
		}

		File testBaseDir = getTestBaseDir();

		if ((testBaseDir != null) && testBaseDir.exists()) {
			sb.append("TEST_BASE_DIR_NAME=");
			sb.append(JenkinsResultsParserUtil.getCanonicalPath(testBaseDir));
			sb.append("\n");
		}

		Job job = getJob();

		String companyDefaultLocale = job.getCompanyDefaultLocale();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(companyDefaultLocale)) {
			sb.append("TEST_COMPANY_DEFAULT_LOCALE=");
			sb.append(companyDefaultLocale);
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public List<TestClass> getTestClasses() {
		List<TestClass> testClasses = new ArrayList<>();

		for (AxisTestClassGroup axisTestClassGroup : getAxisTestClassGroups()) {
			testClasses.addAll(axisTestClassGroup.getTestClasses());
		}

		return testClasses;
	}

	public boolean isTestAnalyticsCloud() {
		return false;
	}

	protected SegmentTestClassGroup(
		BatchTestClassGroup parentBatchTestClassGroup) {

		_batchTestClassGroup = parentBatchTestClassGroup;
	}

	protected SegmentTestClassGroup(
		BatchTestClassGroup parentBatchTestClassGroup, JSONObject jsonObject) {

		_batchTestClassGroup = parentBatchTestClassGroup;

		JSONArray axesJSONArray = jsonObject.getJSONArray("axes");

		if ((axesJSONArray == null) || axesJSONArray.isEmpty()) {
			return;
		}

		for (int i = 0; i < axesJSONArray.length(); i++) {
			JSONObject axisJSONObject = axesJSONArray.getJSONObject(i);

			if (axisJSONObject == null) {
				continue;
			}

			AxisTestClassGroup axisTestClassGroup =
				TestClassGroupFactory.newAxisTestClassGroup(
					axisJSONObject, this);

			_axisTestClassGroups.add(axisTestClassGroup);

			_batchTestClassGroup.addAxisTestClassGroup(axisTestClassGroup);
		}
	}

	@Override
	protected void addTestClass(TestClass testClass) {
		super.addTestClass(testClass);

		testClass.setSegmentTestClassGroup(this);
	}

	private String _getSlaveLabel() {
		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		return batchTestClassGroup.getSlaveLabel();
	}

	private final List<AxisTestClassGroup> _axisTestClassGroups =
		new ArrayList<>();
	private final BatchTestClassGroup _batchTestClassGroup;

}