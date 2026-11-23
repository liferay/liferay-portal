/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.task;

import com.liferay.jenkins.results.parser.history.BatchHistory;
import com.liferay.jenkins.results.parser.history.TestTaskHistory;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.TestClassGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class BaseTestTask implements TestTask {

	@Override
	public void addTestClass(TestClass testClass) {
		if (_testClasses.contains(testClass)) {
			return;
		}

		_testClasses.add(testClass);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TestTask)) {
			return false;
		}

		return Objects.equals(hashCode(), object.hashCode());
	}

	@Override
	public long getAverageDuration() {
		TestTaskHistory testTaskHistory = getTestTaskHistory();

		if (testTaskHistory == null) {
			return _batchTestClassGroup.getDefaultTestTaskDuration();
		}

		return testTaskHistory.getAverageDuration();
	}

	@Override
	public long getAverageOverheadDuration() {
		if (_testClasses.isEmpty()) {
			return _batchTestClassGroup.getDefaultTestOverheadDuration();
		}

		TestClass testClass = _testClasses.get(0);

		return testClass.getAverageOverheadDuration();
	}

	@Override
	public long getAverageTotalDuration() {
		TestTaskHistory testTaskHistory = getTestTaskHistory();

		if (testTaskHistory == null) {
			return _batchTestClassGroup.getDefaultTestTaskDuration();
		}

		return testTaskHistory.getAverageTotalDuration();
	}

	@Override
	public BatchHistory getBatchHistory() {
		return _batchTestClassGroup.getBatchHistory();
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"average_duration", getAverageDuration()
		).put(
			"average_overhead_duration", getAverageOverheadDuration()
		).put(
			"average_total_duration", getAverageTotalDuration()
		).put(
			"grouping_strategy", String.valueOf(_groupingStrategy)
		).put(
			"latest_report_missing", isLatestReportMissing()
		).put(
			"longest_duration", getLongestDuration()
		).put(
			"name", getName()
		);

		JSONArray testClassesJSONArray = new JSONArray();

		for (TestClass testClass : getTestClasses()) {
			testClassesJSONArray.put(testClass.getJSONObject());
		}

		jsonObject.put("test_classes", testClassesJSONArray);

		return jsonObject;
	}

	@Override
	public long getLongestDuration() {
		TestTaskHistory testTaskHistory = getTestTaskHistory();

		if (testTaskHistory == null) {
			return _batchTestClassGroup.getDefaultTestTaskDuration();
		}

		return testTaskHistory.getLongestDuration();
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public long getOverheadWeight() {
		return getAverageOverheadDuration();
	}

	@Override
	public List<TestClass> getTestClasses() {
		return _testClasses;
	}

	@Override
	public TestTaskHistory getTestTaskHistory() {
		if (_testTaskHistory != null) {
			return _testTaskHistory;
		}

		BatchHistory batchHistory = getBatchHistory();

		if (batchHistory == null) {
			return null;
		}

		_testTaskHistory = batchHistory.getTestTaskHistory(getName());

		return _testTaskHistory;
	}

	@Override
	public long getWeight() {
		if (_weight != null) {
			return _weight;
		}

		long weight = 0;

		if (_groupingStrategy ==
				TestClassGroup.GroupingStrategy.TEST_TASK_AVERAGE_DURATION) {

			weight = getAverageDuration();
		}
		else if (_groupingStrategy ==
					TestClassGroup.GroupingStrategy.
						TEST_TASK_AVERAGE_TOTAL_DURATION) {

			weight = getAverageTotalDuration();
		}
		else if (_groupingStrategy ==
					TestClassGroup.GroupingStrategy.
						TEST_TASK_LONGEST_DURATION) {

			weight = getLongestDuration();
		}

		if (weight <= 0) {
			weight = 0L;
		}

		_weight = weight;

		return _weight;
	}

	@Override
	public int hashCode() {
		String name = getName();

		return name.hashCode();
	}

	@Override
	public boolean isIsolated() {
		return isLatestReportMissing();
	}

	public boolean isLatestReportMissing() {
		TestTaskHistory testTaskHistory = getTestTaskHistory();

		if (testTaskHistory == null) {
			return true;
		}

		return testTaskHistory.isLatestReportMissing();
	}

	@Override
	public boolean isSplit() {
		return _split;
	}

	@Override
	public void setSplit(boolean split) {
		_split = split;
	}

	protected BaseTestTask(
		BatchTestClassGroup batchTestClassGroup,
		TestClassGroup.GroupingStrategy groupingStrategy, String name) {

		_batchTestClassGroup = batchTestClassGroup;
		_groupingStrategy = groupingStrategy;
		_name = name;
	}

	protected BatchTestClassGroup getBatchTestClassGroup() {
		return _batchTestClassGroup;
	}

	private final BatchTestClassGroup _batchTestClassGroup;
	private final TestClassGroup.GroupingStrategy _groupingStrategy;
	private final String _name;
	private boolean _split;
	private final List<TestClass> _testClasses = new ArrayList<>();
	private TestTaskHistory _testTaskHistory;
	private Long _weight;

}