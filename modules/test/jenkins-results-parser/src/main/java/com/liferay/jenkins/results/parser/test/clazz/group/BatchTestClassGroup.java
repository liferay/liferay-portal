/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.google.common.collect.Lists;

import com.liferay.jenkins.results.parser.BatchHistory;
import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.BuildDatabaseUtil;
import com.liferay.jenkins.results.parser.BuildReportFactory;
import com.liferay.jenkins.results.parser.CloudBucketUtil;
import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.GitWorkingDirectory;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.JobHistory;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.RootCauseAnalysisToolJob;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestHistory;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TestSuiteJob;
import com.liferay.jenkins.results.parser.TestTaskHistory;
import com.liferay.jenkins.results.parser.Workspace;
import com.liferay.jenkins.results.parser.WorkspaceGitRepository;
import com.liferay.jenkins.results.parser.job.property.GlobJobProperty;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.io.File;
import java.io.IOException;

import java.nio.file.PathMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BatchTestClassGroup extends BaseTestClassGroup {

	public void addAxisTestClassGroup(AxisTestClassGroup axisTestClassGroup) {
		axisTestClassGroups.add(axisTestClassGroup);
	}

	public void addSegmentTestClassGroup(
		SegmentTestClassGroup segmentTestClassGroup) {

		_segmentTestClassGroups.add(segmentTestClassGroup);
	}

	public long getAverageTestDuration(String testName) {
		if (_averageTestDurations.containsKey(testName)) {
			return _averageTestDurations.get(testName);
		}

		long averageTestDuration = _getDefaultTestDuration();

		BatchHistory batchHistory = getBatchHistory();

		if (batchHistory != null) {
			TestHistory testHistory = batchHistory.getTestHistory(testName);

			if (testHistory != null) {
				averageTestDuration = testHistory.getAverageDuration();
			}
		}

		_averageTestDurations.put(testName, averageTestDuration);

		return averageTestDuration;
	}

	public long getAverageTestOverheadDuration(String testName) {
		if (_averageTestOverheadDurations.containsKey(testName)) {
			return _averageTestOverheadDurations.get(testName);
		}

		long averageTestOverheadDuration = _getDefaultTestOverheadDuration();

		BatchHistory batchHistory = getBatchHistory();

		if (batchHistory != null) {
			TestHistory testHistory = batchHistory.getTestHistory(testName);

			if (testHistory != null) {
				averageTestOverheadDuration =
					testHistory.getAverageOverheadDuration();
			}
		}

		_averageTestOverheadDurations.put(
			testName, averageTestOverheadDuration);

		return averageTestOverheadDuration;
	}

	public long getAverageTestTaskDuration(String testName) {
		TestTaskHistory testTaskHistory = _getTestTaskHistory(testName);

		if (testTaskHistory == null) {
			return _getDefaultTestTaskDuration();
		}

		long averageDuration = testTaskHistory.getAverageDuration();

		if (averageDuration == 0) {
			return _getDefaultTestTaskDuration();
		}

		return averageDuration;
	}

	public int getAxisCount() {
		if (ignore()) {
			return 0;
		}

		JobProperty jobProperty = getJobProperty("test.batch.axis.count");

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isInteger(jobPropertyValue)) {
			recordJobProperty(jobProperty);

			return Integer.parseInt(jobPropertyValue);
		}

		if (!containsTestClasses()) {
			return 0;
		}

		int axisMaxSize = getAxisMaxSize();

		if (axisMaxSize <= 0) {
			throw new RuntimeException(
				"'test.batch.axis.max.size' cannot be 0 or less");
		}

		return (int)Math.ceil((double)getTestClassCount() / axisMaxSize);
	}

	public AxisTestClassGroup getAxisTestClassGroup(int axisId) {
		return axisTestClassGroups.get(axisId);
	}

	public List<AxisTestClassGroup> getAxisTestClassGroups() {
		return axisTestClassGroups;
	}

	public BatchHistory getBatchHistory() {
		if (_batchHistory != null) {
			return _batchHistory;
		}

		Job job = getJob();

		JobHistory jobHistory = job.getJobHistory();

		if (jobHistory == null) {
			return null;
		}

		_batchHistory = jobHistory.getBatchHistory(getBatchName());

		return _batchHistory;
	}

	public String getBatchJobName() {
		String topLevelJobName = portalTestClassJob.getJobName();

		Matcher jobNameMatcher = _jobNamePattern.matcher(topLevelJobName);

		String batchJobSuffix = "-batch";

		if (jobNameMatcher.find()) {
			return JenkinsResultsParserUtil.combine(
				jobNameMatcher.group("jobBaseName"), batchJobSuffix,
				jobNameMatcher.group("jobVariant"));
		}

		return topLevelJobName + batchJobSuffix;
	}

	public String getBatchName() {
		return batchName;
	}

	public DownstreamBuildReport getCachedDownstreamBuildReport(
		String axisName) {

		if (!isBuildCachingEnabled() ||
			!JenkinsResultsParserUtil.isCloudCINode()) {

			return null;
		}

		if (!_cachedReportsInitialized) {
			_initializeCachedReports();
		}

		if (_cachedDownstreamBuildReportsMap.containsKey(axisName)) {
			return _cachedDownstreamBuildReportsMap.get(axisName);
		}

		return null;
	}

	public TestClassReport getCachedTestClassReport(String testName) {
		if (!isBuildCachingEnabled() ||
			!JenkinsResultsParserUtil.isCloudCINode()) {

			return null;
		}

		if (!_cachedReportsInitialized) {
			_initializeCachedReports();
		}

		if (_cachedTestClassReportsMap.containsKey(testName)) {
			return _cachedTestClassReportsMap.get(testName);
		}

		return null;
	}

	public List<TestClassReport> getCachedTestClassReportByPrefix(
		String testClassName) {

		TreeMap<String, TestClassReport> testClassReportsMap =
			(TreeMap)_cachedTestClassReportsMap;

		SortedMap<String, TestClassReport> prefixedTestClassReportsMap =
			testClassReportsMap.subMap(
				testClassName, testClassName + Character.MAX_VALUE);

		return new ArrayList<>(prefixedTestClassReportsMap.values());
	}

	public TestReport getCachedTestReport(String testName) {
		if (!isBuildCachingEnabled() ||
			!JenkinsResultsParserUtil.isCloudCINode()) {

			return null;
		}

		if (!_cachedReportsInitialized) {
			_initializeCachedReports();
		}

		if (_cachedTestReportsMap.containsKey(testName)) {
			return _cachedTestReportsMap.get(testName);
		}

		return null;
	}

	public String getCohortName() {
		JobProperty jobProperty = getJobProperty("test.batch.cohort.name");

		String jobPropertyValue = jobProperty.getValue();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue)) {
			recordJobProperty(jobProperty);

			return jobPropertyValue;
		}

		jobPropertyValue = JenkinsResultsParserUtil.getCohortName();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue)) {
			return jobPropertyValue;
		}

		return "test-1";
	}

	public String getDownstreamJobName() {
		String topLevelJobName = portalTestClassJob.getJobName();

		Matcher jobNameMatcher = _jobNamePattern.matcher(topLevelJobName);

		String batchJobSuffix = "-downstream";

		String slaveLabel = getSlaveLabel();

		if (slaveLabel.contains("win")) {
			batchJobSuffix = "-windows-downstream";
		}

		if (jobNameMatcher.find()) {
			return JenkinsResultsParserUtil.combine(
				jobNameMatcher.group("jobBaseName"), batchJobSuffix,
				jobNameMatcher.group("jobVariant"));
		}

		return topLevelJobName + batchJobSuffix;
	}

	public Map<String, List<String>> getGlobTestClassMethodNamesMap() {
		return _globTestClassMethodNamesMap;
	}

	@Override
	public Job getJob() {
		return portalTestClassJob;
	}

	public JSONObject getJSONObject() {
		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = new JSONObject();

		jsonObject.put(
			"batch_name", getBatchName()
		).put(
			"include_stable_test_suite", includeStableTestSuite
		).put(
			"job_properties", _getJobPropertiesMap()
		);

		JSONArray segmentJSONArray = new JSONArray();

		for (SegmentTestClassGroup segmentTestClassGroup :
				getSegmentTestClassGroups()) {

			segmentJSONArray.put(segmentTestClassGroup.getJSONObject());
		}

		jsonObject.put(
			"segments", segmentJSONArray
		).put(
			"test_hotfix_changes", testHotfixChanges
		).put(
			"test_release_bundle", testReleaseBundle
		).put(
			"test_relevant_changes", testRelevantChanges
		).put(
			"test_relevant_changes_in_stable", testRelevantChangesInStable
		).put(
			"test_relevant_junit_tests_only", testRelevantJUnitTestsOnly
		).put(
			"test_relevant_junit_tests_only_in_stable",
			testRelevantJUnitTestsOnlyInStable
		);

		return jsonObject;
	}

	public Integer getMaximumSlavesPerHost() {
		JobProperty jobProperty = getJobProperty(
			"test.batch.maximum.slaves.per.host");

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isInteger(jobPropertyValue)) {
			recordJobProperty(jobProperty);

			return Integer.valueOf(jobPropertyValue);
		}

		return JenkinsMaster.getSlavesPerHostDefault();
	}

	public Integer getMinimumSlaveRAM() {
		JobProperty jobProperty = getJobProperty(
			"test.batch.minimum.slave.ram");

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isInteger(jobPropertyValue)) {
			recordJobProperty(jobProperty);

			return Integer.valueOf(jobPropertyValue);
		}

		return JenkinsMaster.getSlaveRAMMinimumDefault();
	}

	public PortalGitWorkingDirectory getPortalGitWorkingDirectory() {
		return portalGitWorkingDirectory;
	}

	public int getSegmentCount() {
		return _segmentTestClassGroups.size();
	}

	public SegmentTestClassGroup getSegmentTestClassGroup(int segmentId) {
		if ((_segmentTestClassGroups.size() - 1) < segmentId) {
			return null;
		}

		return _segmentTestClassGroups.get(segmentId);
	}

	public List<SegmentTestClassGroup> getSegmentTestClassGroups() {
		return _segmentTestClassGroups;
	}

	public String getSlaveLabel() {
		JobProperty jobProperty = getJobProperty("test.batch.slave.label");

		String jobPropertyValue = jobProperty.getValue();

		if (jobPropertyValue != null) {
			recordJobProperty(jobProperty);

			return jobPropertyValue;
		}

		if (!JenkinsResultsParserUtil.isCloudCINode()) {
			return SLAVE_LABEL_DEFAULT;
		}

		String slaveLabel = null;

		try {
			slaveLabel = JenkinsResultsParserUtil.getBuildProperty(
				"jenkins.osb.jenkins.web.slave.label", getBatchJobName(),
				getTestSuiteName());

			if (JenkinsResultsParserUtil.isNullOrEmpty(slaveLabel)) {
				slaveLabel = JenkinsResultsParserUtil.getBuildProperty(
					"jenkins.osb.jenkins.web.slave.label.minimum.ram",
					String.valueOf(getMinimumSlaveRAM()));
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(slaveLabel)) {
				slaveLabel = JenkinsResultsParserUtil.getBuildProperty(
					"cloud.fleet.primary.label");
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(slaveLabel)) {
				slaveLabel = JenkinsResultsParserUtil.getBuildProperty(
					"master.auto.scaling.group.name");
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(slaveLabel)) {
			return slaveLabel;
		}

		return SLAVE_LABEL_DEFAULT;
	}

	public String getTestCasePropertiesContent() {
		StringBuilder sb = new StringBuilder();

		for (SegmentTestClassGroup segmentTestClassGroup :
				getSegmentTestClassGroups()) {

			sb.append(segmentTestClassGroup.getTestCasePropertiesContent());
			sb.append("\n");
		}

		return sb.toString();
	}

	public String getTestTaskName(String testName) {
		TestTaskHistory testTaskHistory = _getTestTaskHistory(testName);

		if (testTaskHistory == null) {
			return null;
		}

		return testTaskHistory.getTestTaskName();
	}

	public boolean isBuildCachingEnabled() {
		Job job = getJob();

		return job.isBuildCachingEnabled();
	}

	public boolean isTestAnalyticsCloud() {
		if (_testAnalyticsCloud != null) {
			return _testAnalyticsCloud;
		}

		for (SegmentTestClassGroup segmentTestClassGroup :
				getSegmentTestClassGroups()) {

			if (segmentTestClassGroup.isTestAnalyticsCloud()) {
				_testAnalyticsCloud = true;

				return _testAnalyticsCloud;
			}
		}

		_testAnalyticsCloud = false;

		return _testAnalyticsCloud;
	}

	protected BatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		this.jsonObject = jsonObject;
		this.portalTestClassJob = portalTestClassJob;

		batchName = jsonObject.getString("batch_name");

		includeStableTestSuite = jsonObject.getBoolean(
			"include_stable_test_suite");

		portalGitWorkingDirectory =
			portalTestClassJob.getPortalGitWorkingDirectory();

		JSONArray segmentsJSONArray = jsonObject.optJSONArray("segments");

		if ((segmentsJSONArray != null) && !segmentsJSONArray.isEmpty()) {
			for (int i = 0; i < segmentsJSONArray.length(); i++) {
				JSONObject segmentJSONObject = segmentsJSONArray.getJSONObject(
					i);

				_segmentTestClassGroups.add(
					TestClassGroupFactory.newSegmentTestClassGroup(
						this, segmentJSONObject));
			}
		}

		testHotfixChanges = jsonObject.optBoolean("test_hotfix_changes");
		testRelevantChanges = jsonObject.optBoolean("test_relevant_changes");
		testReleaseBundle = jsonObject.optBoolean("test_release_bundle");
		testRelevantJUnitTestsOnly = jsonObject.optBoolean(
			"test_relevant_junit_tests_only");
		testRelevantJUnitTestsOnlyInStable = jsonObject.optBoolean(
			"test_relevant_junit_tests_only_in_stable");

		if (portalTestClassJob instanceof TestSuiteJob) {
			TestSuiteJob testSuiteJob = (TestSuiteJob)portalTestClassJob;

			testSuiteName = testSuiteJob.getTestSuiteName();
		}
		else {
			testSuiteName = null;
		}
	}

	protected BatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		this.batchName = batchName;
		this.portalTestClassJob = portalTestClassJob;

		portalGitWorkingDirectory =
			portalTestClassJob.getPortalGitWorkingDirectory();

		if (portalTestClassJob instanceof TestSuiteJob) {
			TestSuiteJob testSuiteJob = (TestSuiteJob)portalTestClassJob;

			testSuiteName = testSuiteJob.getTestSuiteName();
		}
		else {
			testSuiteName = null;
		}

		_setTestHotfixChanges();
		_setTestReleaseBundle();
		_setTestRelevantChanges();
		_setTestRelevantChangesInStable();

		_setTestRelevantJUnitTestsOnly();
		_setTestRelevantJUnitTestsOnlyInStable();

		_setIncludeStableTestSuite();
	}

	@Override
	protected void addTestClass(TestClass testClass) {
		super.addTestClass(testClass);

		testClass.setBatchTestClassGroup(this);
	}

	protected int getAxisMaxSize() {
		JobProperty jobProperty = getJobProperty("test.batch.axis.max.size");

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isInteger(jobPropertyValue)) {
			recordJobProperty(jobProperty);

			return Integer.parseInt(jobPropertyValue);
		}

		return AXES_SIZE_MAX_DEFAULT;
	}

	protected List<String> getGlobs(List<JobProperty> jobProperties) {
		List<String> globs = new ArrayList<>();

		for (JobProperty jobProperty : jobProperties) {
			if (!(jobProperty instanceof GlobJobProperty)) {
				continue;
			}

			GlobJobProperty globJobProperty = (GlobJobProperty)jobProperty;

			for (String relativeGlob : globJobProperty.getRelativeGlobs()) {
				if ((relativeGlob == null) || globs.contains(relativeGlob)) {
					continue;
				}

				globs.add(relativeGlob);
			}
		}

		Collections.sort(globs);

		return globs;
	}

	protected List<PathMatcher> getIncludePathMatchers(
		List<JobProperty> jobProperties) {

		List<PathMatcher> pathMatchers = new ArrayList<>();

		for (JobProperty jobProperty : jobProperties) {
			if (!(jobProperty instanceof GlobJobProperty)) {
				continue;
			}

			GlobJobProperty globJobProperty = (GlobJobProperty)jobProperty;

			List<PathMatcher> globPathMatchers =
				globJobProperty.getPathMatchers();

			if (globPathMatchers == null) {
				continue;
			}

			Map<String, List<String>> globTestClassMethodNamesMap =
				globJobProperty.getGlobTestClassMethodNamesMap();

			if ((globTestClassMethodNamesMap != null) &&
				!globTestClassMethodNamesMap.isEmpty()) {

				_globTestClassMethodNamesMap.putAll(
					globTestClassMethodNamesMap);
			}

			for (PathMatcher globPathMatcher : globPathMatchers) {
				if ((globPathMatcher == null) ||
					pathMatchers.contains(globPathMatcher)) {

					continue;
				}

				pathMatchers.add(globPathMatcher);
			}
		}

		return pathMatchers;
	}

	protected List<JobProperty> getJobProperties(
		File file, String basePropertyName, JobProperty.Type jobType,
		Set<File> traversedPropertyFileSet) {

		List<JobProperty> jobPropertiesList = new ArrayList<>();

		File modulesBaseDir = new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "modules");

		if ((file == null) || file.equals(modulesBaseDir) ||
			JenkinsResultsParserUtil.isPoshiFile(file)) {

			return jobPropertiesList;
		}

		if (!file.isDirectory()) {
			file = file.getParentFile();
		}

		File testPropertiesFile = new File(file, "test.properties");

		if (traversedPropertyFileSet == null) {
			traversedPropertyFileSet = new HashSet<>();
		}

		if (testPropertiesFile.exists() &&
			!traversedPropertyFileSet.contains(testPropertiesFile)) {

			JobProperty jobProperty = getJobProperty(
				basePropertyName, file, jobType);

			String jobPropertyValue = jobProperty.getValue();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue) &&
				!jobPropertiesList.contains(jobProperty)) {

				jobPropertiesList.add(jobProperty);
			}

			traversedPropertyFileSet.add(testPropertiesFile);
		}

		JobProperty ignoreParentsJobProperty = getJobProperty(
			"ignoreParents[" + getTestSuiteName() + "]", file,
			JobProperty.Type.MODULE_TEST_DIR);

		boolean ignoreParents = Boolean.valueOf(
			ignoreParentsJobProperty.getValue());

		if (ignoreParents) {
			return jobPropertiesList;
		}

		jobPropertiesList.addAll(
			getJobProperties(
				file.getParentFile(), basePropertyName, jobType,
				traversedPropertyFileSet));

		return jobPropertiesList;
	}

	protected JobProperty getJobProperty(String basePropertyName) {
		return _getJobProperty(basePropertyName, null, null, null, null, true);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, File testBaseDir, JobProperty.Type type) {

		return _getJobProperty(
			basePropertyName, null, null, testBaseDir, type, true);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, File testBaseDir, JobProperty.Type type,
		boolean useBasePropertyName) {

		return _getJobProperty(
			basePropertyName, null, null, testBaseDir, type,
			useBasePropertyName);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, JobProperty.Type type) {

		return _getJobProperty(basePropertyName, null, null, null, type, true);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, String testSuiteName, File testBaseDir,
		JobProperty.Type type) {

		return _getJobProperty(
			basePropertyName, testSuiteName, null, testBaseDir, type, true);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, String testSuiteName, String testBatchName) {

		return _getJobProperty(
			basePropertyName, testSuiteName, testBatchName, null, null, true);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, String testSuiteName, String testBatchName,
		File testBaseDir, JobProperty.Type type) {

		return _getJobProperty(
			basePropertyName, testSuiteName, testBatchName, testBaseDir, type,
			true);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, String testSuiteName, String testBatchName,
		JobProperty.Type type) {

		return _getJobProperty(
			basePropertyName, testSuiteName, testBatchName, null, type, true);
	}

	protected JobProperty getJobProperty(
		String basePropertyName, String testSuiteName, String testBatchName,
		String ruleName, File testBaseDir, JobProperty.Type type) {

		return _getJobProperty(
			basePropertyName, testSuiteName, testBatchName, ruleName,
			testBaseDir, type, true);
	}

	protected List<PathMatcher> getPathMatchers(
		List<JobProperty> jobProperties) {

		List<PathMatcher> pathMatchers = new ArrayList<>();

		for (JobProperty jobProperty : jobProperties) {
			if (!(jobProperty instanceof GlobJobProperty)) {
				continue;
			}

			GlobJobProperty globJobProperty = (GlobJobProperty)jobProperty;

			List<PathMatcher> globPathMatchers =
				globJobProperty.getPathMatchers();

			if (globPathMatchers == null) {
				continue;
			}

			for (PathMatcher globPathMatcher : globPathMatchers) {
				if ((globPathMatcher == null) ||
					pathMatchers.contains(globPathMatcher)) {

					continue;
				}

				pathMatchers.add(globPathMatcher);
			}
		}

		return pathMatchers;
	}

	protected List<PathMatcher> getPathMatchers(
		String relativeGlobs, File workingDirectory) {

		if ((relativeGlobs == null) || relativeGlobs.isEmpty()) {
			return Collections.emptyList();
		}

		return JenkinsResultsParserUtil.toPathMatchers(
			JenkinsResultsParserUtil.getCanonicalPath(workingDirectory) +
				File.separator,
			JenkinsResultsParserUtil.getGlobsFromProperty(relativeGlobs));
	}

	protected List<String> getRelevantIntegrationUnitBatchNames() {
		List<String> relevantIntegrationUnitBatchNames = new ArrayList<>();

		if (!testSuiteName.equals("relevant")) {
			return relevantIntegrationUnitBatchNames;
		}

		JobProperty jobProperty = getJobProperty("test.batch.names");

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue)) {
			return relevantIntegrationUnitBatchNames;
		}

		for (String relevantTestBatchName : jobPropertyValue.split(",")) {
			if (relevantTestBatchName.startsWith("integration-") ||
				relevantTestBatchName.startsWith("modules-integration") ||
				relevantTestBatchName.startsWith("modules-unit") ||
				relevantTestBatchName.startsWith("unit-")) {

				relevantIntegrationUnitBatchNames.add(relevantTestBatchName);
			}
		}

		return relevantIntegrationUnitBatchNames;
	}

	protected List<File> getRequiredModuleDirs(List<File> moduleDirs) {
		return _getRequiredModuleDirs(moduleDirs, new ArrayList<>(moduleDirs));
	}

	protected int getSegmentMaxChildren() {
		JobProperty jobProperty = getJobProperty(
			"test.batch.segment.max.children");

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isInteger(jobPropertyValue)) {
			recordJobProperty(jobProperty);

			return Integer.valueOf(jobPropertyValue);
		}

		return _SEGMENT_MAX_CHILDREN_DEFAULT;
	}

	protected long getTargetAxisDuration() {
		if (_isIgnoreTargetAxisDuration()) {
			return 0;
		}

		GitWorkingDirectory gitWorkingDirectory =
			getPortalGitWorkingDirectory();

		String upstreamBranchName = gitWorkingDirectory.getUpstreamBranchName();

		if (!upstreamBranchName.equals("master")) {
			return 0;
		}

		JobProperty targetAxisDurationJobProperty = getJobProperty(
			"test.batch.target.axis.duration");

		String targetAxisDurationString =
			targetAxisDurationJobProperty.getValue();

		if (!JenkinsResultsParserUtil.isInteger(targetAxisDurationString)) {
			return 0;
		}

		recordJobProperty(targetAxisDurationJobProperty);

		long targetAxisDuration = Long.parseLong(targetAxisDurationString);

		JobProperty performanceModifierJobProperty = getJobProperty(
			"test.batch.performance.modifier");

		String performanceModifier = performanceModifierJobProperty.getValue();

		if (JenkinsResultsParserUtil.isDouble(performanceModifier)) {
			targetAxisDuration = Math.round(
				targetAxisDuration * Double.parseDouble(performanceModifier));

			recordJobProperty(performanceModifierJobProperty);
		}

		return targetAxisDuration;
	}

	protected String getTestSuiteName() {
		return testSuiteName;
	}

	protected boolean ignore() {
		if (!isStableTestSuiteBatch() && testRelevantJUnitTestsOnly) {
			return true;
		}

		if (isStableTestSuiteBatch() && testRelevantJUnitTestsOnlyInStable) {
			return true;
		}

		return false;
	}

	protected boolean isRootCauseAnalysis() {
		return getJob() instanceof RootCauseAnalysisToolJob;
	}

	protected boolean isStableTestSuiteBatch() {
		return isStableTestSuiteBatch(batchName);
	}

	protected boolean isStableTestSuiteBatch(String batchName) {
		List<String> testBatchNames = new ArrayList<>();

		JobProperty jobProperty = getJobProperty("test.batch.names[stable]");

		String jobPropertyValue = jobProperty.getValue();

		if (jobPropertyValue != null) {
			Collections.addAll(testBatchNames, jobPropertyValue.split(","));
		}

		return testBatchNames.contains(batchName);
	}

	protected void recordJobProperties(List<JobProperty> jobProperties) {
		for (JobProperty jobProperty : jobProperties) {
			recordJobProperty(jobProperty);
		}
	}

	protected void recordJobProperty(JobProperty jobProperty) {
		if ((jobProperty == null) || (jobProperty.getValue() == null) ||
			_jobProperties.contains(jobProperty)) {

			return;
		}

		_jobProperties.add(jobProperty);
	}

	protected void setAxisTestClassGroups() {
		if (!containsTestClasses()) {
			return;
		}

		int axisCount = getAxisCount();

		int axisSize = (int)Math.ceil((double)getTestClassCount() / axisCount);

		List<TestClass> testClasses = getTestClasses();

		for (List<TestClass> axisTestClasses :
				Lists.partition(testClasses, axisSize)) {

			AxisTestClassGroup axisTestClassGroup =
				TestClassGroupFactory.newAxisTestClassGroup(this);

			for (TestClass axisTestClass : axisTestClasses) {
				axisTestClassGroup.addTestClass(axisTestClass);
			}

			axisTestClassGroups.add(axisTestClassGroup);
		}
	}

	protected void setSegmentTestClassGroups() {
		if (!_segmentTestClassGroups.isEmpty() ||
			axisTestClassGroups.isEmpty()) {

			return;
		}

		List<List<AxisTestClassGroup>> axisTestClassGroupsList =
			new ArrayList<>();

		axisTestClassGroupsList.add(axisTestClassGroups);

		axisTestClassGroupsList = _partitionByMinimumSlaveRAM(
			axisTestClassGroupsList);
		axisTestClassGroupsList = _partitionBySlaveLabel(
			axisTestClassGroupsList);
		axisTestClassGroupsList = _partitionByTestBaseDir(
			axisTestClassGroupsList);

		axisTestClassGroupsList = _partitionByMaxChildren(
			axisTestClassGroupsList);

		for (List<AxisTestClassGroup> axisTestClassGroups :
				axisTestClassGroupsList) {

			SegmentTestClassGroup segmentTestClassGroup =
				TestClassGroupFactory.newSegmentTestClassGroup(this);

			for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
				segmentTestClassGroup.addAxisTestClassGroup(axisTestClassGroup);
			}

			_segmentTestClassGroups.add(segmentTestClassGroup);
		}
	}

	protected static final int AXES_SIZE_MAX_DEFAULT = 5000;

	protected static final String NAME_STABLE_TEST_SUITE = "stable";

	protected static final String SLAVE_LABEL_DEFAULT = "!master";

	protected final List<AxisTestClassGroup> axisTestClassGroups =
		new ArrayList<>();
	protected final String batchName;
	protected boolean includeStableTestSuite;
	protected JSONObject jsonObject;
	protected final PortalGitWorkingDirectory portalGitWorkingDirectory;
	protected final PortalTestClassJob portalTestClassJob;
	protected boolean testHotfixChanges;
	protected boolean testReleaseBundle;
	protected boolean testRelevantChanges;
	protected boolean testRelevantChangesInStable;
	protected boolean testRelevantJUnitTestsOnly;
	protected boolean testRelevantJUnitTestsOnlyInStable;
	protected final String testSuiteName;

	protected static final class CSVReport {

		public CSVReport(Row headerRow) {
			if (headerRow == null) {
				throw new IllegalArgumentException("headerRow is null");
			}

			_csvReportRows.add(headerRow);
		}

		public void addRow(Row csvReportRow) {
			Row headerRow = _csvReportRows.get(0);

			if (csvReportRow.size() != headerRow.size()) {
				throw new IllegalArgumentException(
					"Row length does not match headers length");
			}

			if (_csvReportRows.contains(csvReportRow)) {
				return;
			}

			_csvReportRows.add(csvReportRow);
		}

		@Override
		public String toString() {
			StringBuilder sb = null;

			for (Row csvReportRow : _csvReportRows) {
				if (sb == null) {
					sb = new StringBuilder();
				}
				else {
					sb.append("\n");
				}

				sb.append(csvReportRow.toString());
			}

			return sb.toString();
		}

		protected static final class Row extends ArrayList<String> {

			public Row() {
			}

			public Row(String... strings) {
				for (String string : strings) {
					add(string);
				}
			}

			@Override
			public boolean equals(Object object) {
				if (this == object) {
					return true;
				}

				if (!(object instanceof Row) ||
					!Objects.equals(toString(), object.toString())) {

					return false;
				}

				return true;
			}

			@Override
			public int hashCode() {
				String string = toString();

				return string.hashCode();
			}

			@Override
			public String toString() {
				return StringUtils.join(iterator(), ",");
			}

		}

		private List<Row> _csvReportRows = new ArrayList<>();

	}

	protected static class TestClassDurationComparator
		implements Comparator<TestClass> {

		@Override
		public int compare(TestClass testClass1, TestClass testClass2) {
			Long duration1 =
				testClass1.getAverageDuration() +
					testClass1.getAverageOverheadDuration();
			Long duration2 =
				testClass2.getAverageDuration() +
					testClass2.getAverageOverheadDuration();

			return duration2.compareTo(duration1);
		}

	}

	private synchronized void _downloadBuildReports() {
		if (_buildReportsDownloaded.get()) {
			return;
		}

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		List<Workspace> workspaces = buildDatabase.getWorkspaces();

		Workspace workspace = workspaces.get(0);

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		String path = JenkinsResultsParserUtil.combine(
			workspaceGitRepository.getName(), "/",
			workspaceGitRepository.getBaseBranchSHA(), "/",
			workspaceGitRepository.getSenderBranchSHA());

		File baseDir = new File(
			System.getProperty("java.io.tmpdir"),
			"cached-build-report-files/" + path);

		if (!baseDir.exists()) {
			baseDir.mkdirs();

			StringBuilder sb = new StringBuilder();

			try {
				sb.append(
					JenkinsResultsParserUtil.getBuildProperty(
						"cloud.ci.s3.bucket.build.reports.path"));

				sb.append("/");
				sb.append(path);

				long start = System.currentTimeMillis();

				CloudBucketUtil.syncS3Files(
					JenkinsResultsParserUtil.getCanonicalPath(baseDir),
					sb.toString());

				long duration = System.currentTimeMillis() - start;

				System.out.println(
					"Downloaded build reports from AWS S3 bucket in " +
						JenkinsResultsParserUtil.toDurationString(duration));
			}
			catch (IOException | TimeoutException exception) {
				System.out.println(
					"Unable to sync cached build reports for " + path + ":" +
						exception.getMessage());
			}
		}

		_buildReportsDownloaded.set(true);
	}

	private long _getDefaultTestDuration() {
		JobProperty jobProperty = getJobProperty(
			"test.batch.default.test.duration");

		if (jobProperty == null) {
			return 0;
		}

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue)) {
			return 0;
		}

		recordJobProperty(jobProperty);

		return Long.valueOf(jobPropertyValue);
	}

	private long _getDefaultTestOverheadDuration() {
		JobProperty jobProperty = getJobProperty(
			"test.batch.default.test.overhead.duration");

		if (jobProperty == null) {
			return 0;
		}

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue)) {
			return 0;
		}

		recordJobProperty(jobProperty);

		return Long.valueOf(jobPropertyValue);
	}

	private long _getDefaultTestTaskDuration() {
		JobProperty jobProperty = getJobProperty(
			"test.batch.default.test.task.duration");

		if (jobProperty == null) {
			return 0;
		}

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue)) {
			return 0;
		}

		recordJobProperty(jobProperty);

		return Long.valueOf(jobPropertyValue);
	}

	private Map<String, Properties> _getJobPropertiesMap() {
		Map<String, Properties> batchProperties = new TreeMap<>();

		for (JobProperty jobProperty : _jobProperties) {
			String jobPropertyValue = jobProperty.getValue();

			if (jobPropertyValue == null) {
				continue;
			}

			String propertiesFilePath = jobProperty.getPropertiesFilePath();

			Properties properties = batchProperties.get(propertiesFilePath);

			if (properties == null) {
				properties = new Properties();
			}

			properties.setProperty(jobProperty.getName(), jobPropertyValue);

			batchProperties.put(propertiesFilePath, properties);
		}

		return batchProperties;
	}

	private JobProperty _getJobProperty(
		String basePropertyName, String testSuiteName, String testBatchName,
		File testBaseDir, JobProperty.Type type, boolean useBasePropertyName) {

		if (testBatchName == null) {
			testBatchName = getBatchName();
		}

		return JobPropertyFactory.newJobProperty(
			basePropertyName, testSuiteName, testBatchName, getJob(),
			testBaseDir, type, useBasePropertyName);
	}

	private JobProperty _getJobProperty(
		String basePropertyName, String testSuiteName, String testBatchName,
		String ruleName, File testBaseDir, JobProperty.Type type,
		boolean useBasePropertyName) {

		if (testBatchName == null) {
			testBatchName = getBatchName();
		}

		return JobPropertyFactory.newJobProperty(
			basePropertyName, testSuiteName, testBatchName, ruleName, getJob(),
			testBaseDir, type, useBasePropertyName);
	}

	private List<File> _getRequiredModuleDirs(
		List<File> moduleDirs, List<File> requiredModuleDirs) {

		List<File> modifiedPoshiModulesList = new ArrayList<>();
		List<File> modifiedNonposhiModulesList = new ArrayList<>();

		try {
			modifiedPoshiModulesList =
				portalGitWorkingDirectory.getModifiedPoshiModules();
			modifiedNonposhiModulesList =
				portalGitWorkingDirectory.getModifiedNonposhiModules();
		}
		catch (IOException ioException) {
			File workingDirectory =
				portalGitWorkingDirectory.getWorkingDirectory();

			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to get modified modules with non poshi and poshi " +
						"changes directories in ",
					workingDirectory.getPath()),
				ioException);
		}

		File modulesBaseDir = new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "modules");

		for (File moduleDir : moduleDirs) {
			if (testRelevantChanges &&
				modifiedPoshiModulesList.contains(moduleDir) &&
				!modifiedNonposhiModulesList.contains(moduleDir)) {

				continue;
			}

			JobProperty jobProperty = getJobProperty(
				"modules.includes.required[" + getTestSuiteName() + "]",
				moduleDir, JobProperty.Type.MODULE_TEST_DIR);

			String jobPropertyValue = jobProperty.getValue();

			if (jobPropertyValue == null) {
				continue;
			}

			recordJobProperty(jobProperty);

			for (String requiredModuleDirPath : jobPropertyValue.split(",")) {
				File requiredModuleDir = new File(
					modulesBaseDir, requiredModuleDirPath);

				if (!requiredModuleDir.exists() ||
					requiredModuleDirs.contains(requiredModuleDir)) {

					continue;
				}

				requiredModuleDirs.add(requiredModuleDir);
			}
		}

		return new ArrayList<>(requiredModuleDirs);
	}

	private TestTaskHistory _getTestTaskHistory(String testName) {
		if (_testTaskHistories.containsKey(testName)) {
			return _testTaskHistories.get(testName);
		}

		BatchHistory batchHistory = getBatchHistory();

		if (batchHistory == null) {
			return null;
		}

		TestHistory testHistory = batchHistory.getTestHistory(testName);

		if (testHistory == null) {
			return null;
		}

		_testTaskHistories.put(testName, testHistory.getTestTaskHistory());

		return _testTaskHistories.get(testName);
	}

	private void _initializeCachedReports() {
		if (_cachedReportsInitialized) {
			return;
		}

		_downloadBuildReports();

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		List<Workspace> workspaces = buildDatabase.getWorkspaces();

		if (workspaces.isEmpty()) {
			_cachedReportsInitialized = true;

			return;
		}

		Workspace workspace = workspaces.get(0);

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		String path = JenkinsResultsParserUtil.combine(
			workspaceGitRepository.getName(), "/",
			workspaceGitRepository.getBaseBranchSHA(), "/",
			workspaceGitRepository.getSenderBranchSHA(), "/", getBatchName());

		File baseDir = new File(
			System.getProperty("java.io.tmpdir"),
			"cached-build-report-files/" + path);

		if (!baseDir.exists()) {
			_cachedReportsInitialized = true;

			return;
		}

		File[] buildReportFiles = baseDir.listFiles();

		if (buildReportFiles == null) {
			_cachedReportsInitialized = true;

			return;
		}

		for (File buildReportFile : buildReportFiles) {
			try {
				String buildReportFileName = buildReportFile.getName();

				if (buildReportFileName.endsWith(".sha512")) {
					continue;
				}

				String buildReportFileContent = JenkinsResultsParserUtil.read(
					buildReportFile);

				if (JenkinsResultsParserUtil.isNullOrEmpty(
						buildReportFileContent)) {

					continue;
				}

				DownstreamBuildReport downstreamBuildReport =
					BuildReportFactory.newDownstreamBuildReport(
						getBatchName(), new JSONObject(buildReportFileContent),
						null);

				_cachedDownstreamBuildReportsMap.put(
					downstreamBuildReport.getAxisName(), downstreamBuildReport);

				for (TestReport testReport :
						downstreamBuildReport.getTestReports()) {

					_cachedTestReportsMap.put(
						testReport.getTestName(), testReport);
				}

				for (TestClassReport testClassReport :
						downstreamBuildReport.getTestClassReports()) {

					String testClassName = testClassReport.getTestClassName();

					if (testClassName.equals("junit.framework.TestSuite")) {
						for (TestReport testReport :
								testClassReport.getTestReports()) {

							_cachedTestClassReportsMap.put(
								testReport.getTestName(), testClassReport);
						}

						continue;
					}

					_cachedTestClassReportsMap.put(
						testClassReport.getTestClassName(), testClassReport);
				}
			}
			catch (IOException | JSONException exception) {
				System.out.println("WARNING: " + exception.getMessage());
			}
		}

		_cachedReportsInitialized = true;
	}

	private boolean _isIgnoreTargetAxisDuration() {
		JobProperty jobProperty = getJobProperty(
			"test.batch.ignore.target.axis.duration");

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue)) {
			return false;
		}

		recordJobProperty(jobProperty);

		return Boolean.valueOf(jobPropertyValue);
	}

	private List<List<AxisTestClassGroup>> _partitionByMaxChildren(
		List<List<AxisTestClassGroup>> axisTestClassGroupsList) {

		List<List<AxisTestClassGroup>> partitionedAxisTestClassGroupsList =
			new ArrayList<>();

		for (List<AxisTestClassGroup> axisTestClassGroups :
				axisTestClassGroupsList) {

			partitionedAxisTestClassGroupsList.addAll(
				Lists.partition(axisTestClassGroups, getSegmentMaxChildren()));
		}

		return partitionedAxisTestClassGroupsList;
	}

	private List<List<AxisTestClassGroup>> _partitionByMinimumSlaveRAM(
		List<List<AxisTestClassGroup>> axisTestClassGroupsList) {

		List<List<AxisTestClassGroup>> partitionedAxisTestClassGroupsList =
			new ArrayList<>();

		for (List<AxisTestClassGroup> axisTestClassGroups :
				axisTestClassGroupsList) {

			Map<Integer, List<AxisTestClassGroup>> axisTestClassGroupsMap =
				new HashMap<>();

			for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
				Integer minimumSlaveRAM =
					axisTestClassGroup.getMinimumSlaveRAM();

				List<AxisTestClassGroup> minimumSlaveRAMAxisTestClassGroups =
					axisTestClassGroupsMap.get(minimumSlaveRAM);

				if (minimumSlaveRAMAxisTestClassGroups == null) {
					minimumSlaveRAMAxisTestClassGroups = new ArrayList<>();
				}

				minimumSlaveRAMAxisTestClassGroups.add(axisTestClassGroup);

				axisTestClassGroupsMap.put(
					minimumSlaveRAM, minimumSlaveRAMAxisTestClassGroups);
			}

			partitionedAxisTestClassGroupsList.addAll(
				axisTestClassGroupsMap.values());
		}

		return partitionedAxisTestClassGroupsList;
	}

	private List<List<AxisTestClassGroup>> _partitionBySlaveLabel(
		List<List<AxisTestClassGroup>> axisTestClassGroupsList) {

		List<List<AxisTestClassGroup>> partitionedAxisTestClassGroupsList =
			new ArrayList<>();

		for (List<AxisTestClassGroup> axisTestClassGroups :
				axisTestClassGroupsList) {

			Map<String, List<AxisTestClassGroup>> axisTestClassGroupsMap =
				new HashMap<>();

			for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
				String slaveLabel = axisTestClassGroup.getSlaveLabel();

				List<AxisTestClassGroup> slaveLabelAxisTestClassGroups =
					axisTestClassGroupsMap.get(slaveLabel);

				if (slaveLabelAxisTestClassGroups == null) {
					slaveLabelAxisTestClassGroups = new ArrayList<>();
				}

				slaveLabelAxisTestClassGroups.add(axisTestClassGroup);

				axisTestClassGroupsMap.put(
					slaveLabel, slaveLabelAxisTestClassGroups);
			}

			partitionedAxisTestClassGroupsList.addAll(
				axisTestClassGroupsMap.values());
		}

		return partitionedAxisTestClassGroupsList;
	}

	private List<List<AxisTestClassGroup>> _partitionByTestBaseDir(
		List<List<AxisTestClassGroup>> axisTestClassGroupsList) {

		List<List<AxisTestClassGroup>> partitionedAxisTestClassGroupsList =
			new ArrayList<>();

		for (List<AxisTestClassGroup> axisTestClassGroups :
				axisTestClassGroupsList) {

			Map<File, List<AxisTestClassGroup>> axisTestClassGroupsMap =
				new HashMap<>();

			for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
				File testBaseDir = axisTestClassGroup.getTestBaseDir();

				List<AxisTestClassGroup> testBaseDirAxisTestClassGroups =
					axisTestClassGroupsMap.get(testBaseDir);

				if (testBaseDirAxisTestClassGroups == null) {
					testBaseDirAxisTestClassGroups = new ArrayList<>();
				}

				testBaseDirAxisTestClassGroups.add(axisTestClassGroup);

				axisTestClassGroupsMap.put(
					testBaseDir, testBaseDirAxisTestClassGroups);
			}

			partitionedAxisTestClassGroupsList.addAll(
				axisTestClassGroupsMap.values());
		}

		return partitionedAxisTestClassGroupsList;
	}

	private void _setIncludeStableTestSuite() {
		includeStableTestSuite = testRelevantChanges;
	}

	private void _setTestHotfixChanges() {
		Job job = getJob();

		testHotfixChanges = job.isTestHotfixChanges();
	}

	private void _setTestReleaseBundle() {
		Job job = getJob();

		testReleaseBundle = job.isTestReleaseBundle();
	}

	private void _setTestRelevantChanges() {
		Job job = getJob();

		testRelevantChanges = job.isTestRelevantChanges();
	}

	private void _setTestRelevantChangesInStable() {
		Job job = getJob();

		testRelevantChangesInStable = job.isTestRelevantChangesInStable();
	}

	private void _setTestRelevantJUnitTestsOnly() {
		Job job = getJob();

		if (testRelevantChanges && job.isJUnitTestsModifiedOnly()) {
			testRelevantJUnitTestsOnly = true;

			return;
		}

		testRelevantJUnitTestsOnly = false;
	}

	private void _setTestRelevantJUnitTestsOnlyInStable() {
		Job job = getJob();

		if (testRelevantChangesInStable && job.isJUnitTestsModifiedOnly()) {
			testRelevantJUnitTestsOnlyInStable = true;

			return;
		}

		testRelevantJUnitTestsOnlyInStable = false;
	}

	private static final int _SEGMENT_MAX_CHILDREN_DEFAULT = 25;

	private static final AtomicBoolean _buildReportsDownloaded =
		new AtomicBoolean();
	private static final Pattern _jobNamePattern = Pattern.compile(
		"(?<jobBaseName>.*)(?<jobVariant>\\([^\\)]+\\))");

	private final Map<String, Long> _averageTestDurations = new HashMap<>();
	private final Map<String, Long> _averageTestOverheadDurations =
		new HashMap<>();
	private BatchHistory _batchHistory;
	private final Map<String, DownstreamBuildReport>
		_cachedDownstreamBuildReportsMap = new TreeMap<>();
	private boolean _cachedReportsInitialized;
	private final Map<String, TestClassReport> _cachedTestClassReportsMap =
		new TreeMap<>();
	private final Map<String, TestReport> _cachedTestReportsMap =
		new TreeMap<>();
	private final Map<String, List<String>> _globTestClassMethodNamesMap =
		new HashMap<>();
	private final List<JobProperty> _jobProperties = new ArrayList<>();
	private final List<SegmentTestClassGroup> _segmentTestClassGroups =
		new ArrayList<>();
	private Boolean _testAnalyticsCloud;
	private final Map<String, TestTaskHistory> _testTaskHistories =
		new HashMap<>();

}