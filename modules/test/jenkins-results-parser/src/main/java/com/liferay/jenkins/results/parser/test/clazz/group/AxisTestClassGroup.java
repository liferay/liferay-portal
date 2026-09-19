/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.history.BatchHistory;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;

import java.io.File;

import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class AxisTestClassGroup extends BaseTestClassGroup {

	public long getAverageDuration() {
		if (_averageDuration != null) {
			return _averageDuration;
		}

		_averageDuration =
			getAverageOverheadDuration() + getAverageTotalTestDuration();

		if (_averageDuration <= 0L) {
			BatchHistory batchHistory = getBatchHistory();

			if (batchHistory != null) {
				_averageDuration = batchHistory.getAverageDuration();
			}
		}

		return _averageDuration;
	}

	public long getAverageOverheadDuration() {
		if (_averageOverheadDuration != null) {
			return _averageOverheadDuration;
		}

		List<TestClass> testClasses = getTestClasses();

		if (testClasses.isEmpty()) {
			return 0L;
		}

		long totalAverageOverheadDuration = 0L;

		for (TestClass testClass : testClasses) {
			totalAverageOverheadDuration +=
				testClass.getAverageOverheadDuration();
		}

		_averageOverheadDuration =
			totalAverageOverheadDuration / testClasses.size();

		return _averageOverheadDuration;
	}

	public long getAverageTotalTestDuration() {
		if (_averageTotalTestDuration != null) {
			return _averageTotalTestDuration;
		}

		_averageTotalTestDuration = 0L;

		for (TestClass testClass : getTestClasses()) {
			_averageTotalTestDuration += testClass.getAverageDuration();
		}

		return _averageTotalTestDuration;
	}

	public String getAxisName() {
		if (_segmentTestClassGroup != null) {
			List<AxisTestClassGroup> axisTestClassGroups =
				_segmentTestClassGroup.getAxisTestClassGroups();

			return JenkinsResultsParserUtil.combine(
				_segmentTestClassGroup.getSegmentName(), "/",
				String.valueOf(axisTestClassGroups.indexOf(this)));
		}

		List<AxisTestClassGroup> axisTestClassGroups =
			_batchTestClassGroup.getAxisTestClassGroups();

		return JenkinsResultsParserUtil.combine(
			_batchTestClassGroup.getBatchName(), "/",
			String.valueOf(axisTestClassGroups.indexOf(this)));
	}

	public BatchHistory getBatchHistory() {
		return _batchTestClassGroup.getBatchHistory();
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

	public List<DownstreamBuildReport> getCachedDownstreamBuildReports() {
		if (!isBuildCachingEnabled() || !isResultsCached()) {
			return null;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		List<DownstreamBuildReport> cachedDownstreamBuildReports =
			batchTestClassGroup.getCachedDownstreamBuildReports(getAxisName());

		if ((cachedDownstreamBuildReports == null) ||
			cachedDownstreamBuildReports.isEmpty()) {

			return null;
		}

		for (DownstreamBuildReport cachedDownstreamBuildReport :
				cachedDownstreamBuildReports) {

			if ((cachedDownstreamBuildReport != null) &&
				!cachedDownstreamBuildReport.isFailing()) {

				return Collections.singletonList(cachedDownstreamBuildReport);
			}
		}

		return null;
	}

	public String getDownstreamJobName() {
		return _batchTestClassGroup.getDownstreamJobName();
	}

	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"average_duration", getAverageDuration()
		).put(
			"axis_name", getAxisName()
		).put(
			"grouping_strategy", String.valueOf(getGroupingStrategy())
		);

		JSONArray testClassesJSONArray = new JSONArray();

		jsonObject.put("test_classes", testClassesJSONArray);

		for (TestClass testClass : getTestClasses()) {
			if (testClass == null) {
				throw new RuntimeException(
					"Unable to not find test class in " + getAxisName());
			}

			testClassesJSONArray.put(testClass.getJSONObject());
		}

		return jsonObject;
	}

	@Override
	public Job getJob() {
		return _batchTestClassGroup.getJob();
	}

	@Override
	public Integer getMinimumSlaveRAM() {
		if (_segmentTestClassGroup != null) {
			return _segmentTestClassGroup.getMinimumSlaveRAM();
		}

		return _batchTestClassGroup.getMinimumSlaveRAM();
	}

	@Override
	public String getOSArchitecture() {
		if (_segmentTestClassGroup != null) {
			return _segmentTestClassGroup.getOSArchitecture();
		}

		return _batchTestClassGroup.getOSArchitecture();
	}

	public String getSegmentName() {
		if (_segmentTestClassGroup != null) {
			return _segmentTestClassGroup.getSegmentName();
		}

		return null;
	}

	public SegmentTestClassGroup getSegmentTestClassGroup() {
		return _segmentTestClassGroup;
	}

	public File getTestBaseDir() {
		return null;
	}

	public boolean isBuildCachingEnabled() {
		return _batchTestClassGroup.isBuildCachingEnabled();
	}

	public boolean isResultsCached() {
		if (!isBuildCachingEnabled()) {
			return false;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		List<DownstreamBuildReport> cachedDownstreamBuildReports =
			batchTestClassGroup.getCachedDownstreamBuildReports(getAxisName());

		if ((cachedDownstreamBuildReports != null) &&
			!cachedDownstreamBuildReports.isEmpty()) {

			return true;
		}

		return false;
	}

	protected AxisTestClassGroup(BatchTestClassGroup batchTestClassGroup) {
		setBatchTestClassGroup(batchTestClassGroup);
	}

	protected AxisTestClassGroup(
		JSONObject jsonObject, SegmentTestClassGroup segmentTestClassGroup) {

		BatchTestClassGroup batchTestClassGroup =
			segmentTestClassGroup.getBatchTestClassGroup();

		setBatchTestClassGroup(batchTestClassGroup);

		setSegmentTestClassGroup(segmentTestClassGroup);

		JSONArray testClassesJSONArray = jsonObject.optJSONArray(
			"test_classes");

		if ((testClassesJSONArray == null) || testClassesJSONArray.isEmpty()) {
			return;
		}

		for (int i = 0; i < testClassesJSONArray.length(); i++) {
			JSONObject testClassJSONObject = testClassesJSONArray.getJSONObject(
				i);

			if (testClassJSONObject == null) {
				continue;
			}

			addTestClass(
				TestClassFactory.newTestClass(
					batchTestClassGroup, testClassJSONObject));
		}
	}

	@Override
	protected void addTestClass(TestClass testClass) {
		super.addTestClass(testClass);

		testClass.setAxisTestClassGroup(this);
	}

	protected GroupingStrategy getGroupingStrategy() {
		return _batchTestClassGroup.getGroupingStrategy();
	}

	@Override
	protected String getParentBaseSlaveLabel() {
		if (_segmentTestClassGroup != null) {
			return _segmentTestClassGroup.getBaseSlaveLabel();
		}

		return _batchTestClassGroup.getBaseSlaveLabel();
	}

	protected void setBatchTestClassGroup(
		BatchTestClassGroup batchTestClassGroup) {

		_batchTestClassGroup = batchTestClassGroup;
	}

	protected void setSegmentTestClassGroup(
		SegmentTestClassGroup segmentTestClassGroup) {

		_segmentTestClassGroup = segmentTestClassGroup;
	}

	private Long _averageDuration;
	private Long _averageOverheadDuration;
	private Long _averageTotalTestDuration;
	private BatchTestClassGroup _batchTestClassGroup;
	private SegmentTestClassGroup _segmentTestClassGroup;

}