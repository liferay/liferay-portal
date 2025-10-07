/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.BatchHistory;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.TestHistory;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.SegmentTestClassGroup;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestClass implements TestClass {

	@Override
	public void addTestClassMethod(TestClassMethod testClassMethod) {
		_testClassMethods.add(testClassMethod);
	}

	@Override
	public int compareTo(TestClass testClass) {
		if (testClass == null) {
			throw new NullPointerException("Test class is null");
		}

		return _testClassFile.compareTo(testClass.getTestClassFile());
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TestClass)) {
			return false;
		}

		return Objects.equals(hashCode(), object.hashCode());
	}

	@Override
	public long getAverageDuration() {
		if (_averageDuration != null) {
			return _averageDuration;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		_averageDuration = batchTestClassGroup.getAverageTestDuration(
			getTestName());

		return _averageDuration;
	}

	@Override
	public long getAverageOverheadDuration() {
		if (_averageOverheadDuration != null) {
			return _averageOverheadDuration;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		_averageOverheadDuration =
			batchTestClassGroup.getAverageTestOverheadDuration(getTestName());

		return _averageOverheadDuration;
	}

	@Override
	public long getAverageTestTaskDuration() {
		if (_averageTestTaskDuration != null) {
			return _averageTestTaskDuration;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		_averageTestTaskDuration =
			batchTestClassGroup.getAverageTestTaskDuration(getTestName());

		return _averageTestTaskDuration;
	}

	@Override
	public AxisTestClassGroup getAxisTestClassGroup() {
		return _axisTestClassGroup;
	}

	@Override
	public BatchTestClassGroup getBatchTestClassGroup() {
		return _batchTestClassGroup;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"average_duration", getAverageDuration()
		).put(
			"average_overhead_duration", getAverageOverheadDuration()
		).put(
			"file", getTestClassFile()
		).put(
			"ignored", isIgnored()
		);

		JSONArray methodsJSONArray = new JSONArray();

		for (TestClassMethod testClassMethod : getTestClassMethods()) {
			methodsJSONArray.put(testClassMethod.getJSONObject());
		}

		jsonObject.put(
			"methods", methodsJSONArray
		).put(
			"name", getName()
		);

		return jsonObject;
	}

	@Override
	public String getName() {
		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		return JenkinsResultsParserUtil.getPathRelativeTo(
			getTestClassFile(),
			portalGitWorkingDirectory.getWorkingDirectory());
	}

	@Override
	public long getOverheadWeight() {
		return getAverageOverheadDuration();
	}

	@Override
	public SegmentTestClassGroup getSegmentTestClassGroup() {
		return _segmentTestClassGroup;
	}

	@Override
	public long getSharedWeight() {
		return 0L;
	}

	@Override
	public String getSharedWeightName() {
		return null;
	}

	@Override
	public File getTestClassFile() {
		return _testClassFile;
	}

	@Override
	public List<TestClassMethod> getTestClassMethods() {
		return _testClassMethods;
	}

	@Override
	public String getTestClassName() {
		return getName();
	}

	@Override
	public TestHistory getTestHistory() {
		if (_testHistory != null) {
			return _testHistory;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		BatchHistory batchHistory = batchTestClassGroup.getBatchHistory();

		if (batchHistory == null) {
			return null;
		}

		_testHistory = batchHistory.getTestHistory(getTestName());

		return _testHistory;
	}

	@Override
	public String getTestTaskName() {
		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		return batchTestClassGroup.getTestTaskName(getTestName());
	}

	@Override
	public long getWeight() {
		return getAverageDuration();
	}

	@Override
	public int hashCode() {
		JSONObject jsonObject = getJSONObject();

		return jsonObject.hashCode();
	}

	@Override
	public boolean hasTestClassMethods() {
		List<TestClassMethod> testClassMethods = getTestClassMethods();

		if ((testClassMethods == null) || testClassMethods.isEmpty()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isBuildCachingEnabled() {
		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		return batchTestClassGroup.isBuildCachingEnabled();
	}

	@Override
	public boolean isIgnored() {
		return false;
	}

	@Override
	public void setAxisTestClassGroup(AxisTestClassGroup axisTestClassGroup) {
		_axisTestClassGroup = axisTestClassGroup;
	}

	@Override
	public void setBatchTestClassGroup(
		BatchTestClassGroup batchTestClassGroup) {

		_batchTestClassGroup = batchTestClassGroup;
	}

	@Override
	public void setSegmentTestClassGroup(
		SegmentTestClassGroup segmentTestClassGroup) {

		_segmentTestClassGroup = segmentTestClassGroup;
	}

	protected BaseTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile) {

		_batchTestClassGroup = batchTestClassGroup;
		_testClassFile = testClassFile;
	}

	protected BaseTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		_batchTestClassGroup = batchTestClassGroup;

		_testClassFile = new File(jsonObject.getString("file"));

		JSONArray methodsJSONArray = jsonObject.getJSONArray("methods");

		if ((methodsJSONArray != null) && !methodsJSONArray.isEmpty()) {
			for (int i = 0; i < methodsJSONArray.length(); i++) {
				JSONObject methodJSONObject = methodsJSONArray.getJSONObject(i);

				if (methodJSONObject == null) {
					continue;
				}

				_testClassMethods.add(
					TestClassFactory.newTestClassMethod(
						methodJSONObject, this));
			}
		}
	}

	protected void addTestClassMethod(
		boolean methodIgnored, String methodName) {

		addTestClassMethod(
			TestClassFactory.newTestClassMethod(
				methodIgnored, methodName, this));
	}

	protected void addTestClassMethod(
		boolean methodIgnored, String methodName, String issues) {

		addTestClassMethod(
			TestClassFactory.newTestClassMethod(
				methodIgnored, methodName, issues, this));
	}

	protected void addTestClassMethod(String methodName) {
		addTestClassMethod(false, methodName);
	}

	protected PortalGitWorkingDirectory getPortalGitWorkingDirectory() {
		return _batchTestClassGroup.getPortalGitWorkingDirectory();
	}

	protected File getPortalWorkingDirectory() {
		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		return portalGitWorkingDirectory.getWorkingDirectory();
	}

	protected String getTestName() {
		return getName();
	}

	protected File getTestPropertiesBaseDir(File file) {
		if (file == null) {
			return null;
		}

		File canonicalFile = JenkinsResultsParserUtil.getCanonicalFile(file);

		File parentFile = canonicalFile.getParentFile();

		if ((parentFile == null) || !parentFile.exists()) {
			return file;
		}

		if (!canonicalFile.isDirectory()) {
			return getTestPropertiesBaseDir(parentFile);
		}

		File testPropertiesFile = new File(canonicalFile, "test.properties");

		if (!testPropertiesFile.exists()) {
			return getTestPropertiesBaseDir(parentFile);
		}

		return canonicalFile;
	}

	private Long _averageDuration;
	private Long _averageOverheadDuration;
	private Long _averageTestTaskDuration;
	private AxisTestClassGroup _axisTestClassGroup;
	private BatchTestClassGroup _batchTestClassGroup;
	private SegmentTestClassGroup _segmentTestClassGroup;
	private final File _testClassFile;
	private final List<TestClassMethod> _testClassMethods = new ArrayList<>();
	private TestHistory _testHistory;

}