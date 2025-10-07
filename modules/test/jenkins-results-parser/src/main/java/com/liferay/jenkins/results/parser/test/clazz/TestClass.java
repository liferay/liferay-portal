/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.TestHistory;
import com.liferay.jenkins.results.parser.WeightedItem;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.SegmentTestClassGroup;

import java.io.File;

import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public interface TestClass extends Comparable<TestClass>, WeightedItem {

	public void addTestClassMethod(TestClassMethod testClassMethod);

	public long getAverageDuration();

	public long getAverageOverheadDuration();

	public long getAverageTestTaskDuration();

	public AxisTestClassGroup getAxisTestClassGroup();

	public BatchTestClassGroup getBatchTestClassGroup();

	public JSONObject getJSONObject();

	public String getName();

	public SegmentTestClassGroup getSegmentTestClassGroup();

	public File getTestClassFile();

	public List<TestClassMethod> getTestClassMethods();

	public String getTestClassName();

	public TestHistory getTestHistory();

	public String getTestTaskName();

	public boolean hasTestClassMethods();

	public boolean isBuildCachingEnabled();

	public boolean isIgnored();

	public void setAxisTestClassGroup(AxisTestClassGroup axisTestClassGroup);

	public void setBatchTestClassGroup(BatchTestClassGroup batchTestClassGroup);

	public void setSegmentTestClassGroup(
		SegmentTestClassGroup axisTestClassGroup);

}