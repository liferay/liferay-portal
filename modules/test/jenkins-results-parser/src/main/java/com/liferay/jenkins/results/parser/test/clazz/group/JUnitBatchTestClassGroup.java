/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.google.common.collect.Lists;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalAcceptancePullRequestJob;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.batch.JUnitTestBatch;
import com.liferay.jenkins.results.parser.test.batch.JUnitTestSelector;
import com.liferay.jenkins.results.parser.test.clazz.JUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassBalancedListSplitter;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.TestTaskBalancedListSplitter;
import com.liferay.jenkins.results.parser.test.task.TestTask;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Yi-Chen Tsai
 */
public class JUnitBatchTestClassGroup extends BatchTestClassGroup {

	@Override
	public int getAxisCount() {
		if (ignore()) {
			return 0;
		}

		int axisCount = super.getAxisCount();

		if ((axisCount == 0) && _includeAutoBalanceTests) {
			return 1;
		}

		return axisCount;
	}

	public List<JobProperty> getExcludesJobProperties() {
		if (_jUnitTestBatch != null) {
			List<JobProperty> testBatchJobProperties =
				getTestSelectorExcludesJobProperties();

			recordJobProperties(testBatchJobProperties);

			return testBatchJobProperties;
		}

		List<JobProperty> excludesJobProperties = new ArrayList<>();

		excludesJobProperties.addAll(getRequiredExcludesJobProperties());

		if (testReleaseBundle) {
			excludesJobProperties.addAll(getReleaseExcludesJobProperties());
		}
		else if (testRelevantChanges) {
			excludesJobProperties.addAll(getRelevantExcludesJobProperties());
		}
		else {
			excludesJobProperties.addAll(getDefaultExcludesJobProperties());
		}

		if (includeStableTestSuite && isStableTestSuiteBatch()) {
			excludesJobProperties.addAll(
				getStableDefaultExcludesJobProperties());
			excludesJobProperties.addAll(
				getStableRequiredExcludesJobProperties());
		}

		excludesJobProperties.removeAll(Collections.singleton(null));

		recordJobProperties(excludesJobProperties);

		return excludesJobProperties;
	}

	public List<JobProperty> getFilterJobProperties() {
		List<JobProperty> filterJobProperties = new ArrayList<>();

		filterJobProperties.add(
			getJobProperty(
				"test.batch.class.names.filter", JobProperty.Type.FILTER_GLOB));

		recordJobProperties(filterJobProperties);

		return filterJobProperties;
	}

	@Override
	public Map<String, List<String>> getGlobTestClassMethodNamesMap() {
		if (!isRootCauseAnalysis()) {
			return super.getGlobTestClassMethodNamesMap();
		}

		return _globTestClassMethodNamesMap;
	}

	public List<JobProperty> getIncludesJobProperties() {
		if (_jUnitTestBatch != null) {
			List<JobProperty> testBatchJobProperties =
				getTestSelectorIncludesJobProperties();

			recordJobProperties(testBatchJobProperties);

			return testBatchJobProperties;
		}

		List<JobProperty> includesJobProperties = new ArrayList<>();

		includesJobProperties.addAll(getRequiredIncludesJobProperties());

		if (testReleaseBundle) {
			includesJobProperties.addAll(getReleaseIncludesJobProperties());
		}
		else if (testRelevantChanges) {
			includesJobProperties.addAll(getRelevantIncludesJobProperties());
		}
		else {
			includesJobProperties.addAll(getDefaultIncludesJobProperties());
		}

		if (includeStableTestSuite && isStableTestSuiteBatch()) {
			includesJobProperties.addAll(
				getStableDefaultIncludesJobProperties());
			includesJobProperties.addAll(
				getStableRequiredIncludesJobProperties());
		}

		includesJobProperties.removeAll(Collections.singleton(null));

		recordJobProperties(includesJobProperties);

		return includesJobProperties;
	}

	public File getJavaFileFromFullClassName(String fullClassName) {
		String classFileName =
			fullClassName.replaceAll(".*\\.([^\\.]+)", "$1") + ".java";

		String classPackageName = fullClassName.substring(
			0, fullClassName.lastIndexOf("."));

		String classPackagePath = classPackageName.replaceAll("\\.", "/");

		for (String javaDirPath : _javaDirPathStrings) {
			if (!javaDirPath.contains(classPackagePath)) {
				continue;
			}

			File classFile = new File(javaDirPath, classFileName);

			if (!classFile.exists()) {
				continue;
			}

			String classFilePath = classFile.getPath();

			if (!classFilePath.contains(
					classPackagePath + "/" + classFileName)) {

				continue;
			}

			return classFile;
		}

		return null;
	}

	@Override
	public JSONObject getJSONObject() {
		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = super.getJSONObject();

		jsonObject.put("auto_balance_test_files", _autoBalanceTestFiles);
		jsonObject.put("exclude_globs", getGlobs(getExcludesJobProperties()));
		jsonObject.put("filter_globs", getGlobs(getFilterJobProperties()));
		jsonObject.put("include_auto_balance_tests", _includeAutoBalanceTests);
		jsonObject.put("include_globs", getGlobs(getIncludesJobProperties()));
		jsonObject.put(
			"include_unstaged_test_class_files",
			_includeUnstagedTestClassFiles);
		jsonObject.put("target_duration", getTargetAxisDuration());

		return jsonObject;
	}

	public List<String> getTestClassMethodNames(
		File file, Map<String, List<String>> globTestClassMethodNamesMap) {

		for (Map.Entry<String, List<String>> globTestClassMethodEntry :
				globTestClassMethodNamesMap.entrySet()) {

			String glob = globTestClassMethodEntry.getKey();

			PathMatcher pathMatcher = JenkinsResultsParserUtil.toPathMatcher(
				_getWorkingDirectory() + "/", glob);

			if (pathMatcher.matches(file.toPath())) {
				return globTestClassMethodNamesMap.get(glob);
			}
		}

		return null;
	}

	public List<TestTask> getTestTasks() {
		return new ArrayList<>();
	}

	public void writeTestCSVReportFile() throws Exception {
		CSVReport csvReport = new CSVReport(
			new CSVReport.Row(
				"Class Name", "Method Name", "Ignored", "File Path"));

		for (JUnitTestClass jUnitTestClass :
				TestClassFactory.getJUnitTestClasses()) {

			File testClassFile = jUnitTestClass.getTestClassFile();

			String testClassFileRelativePath =
				JenkinsResultsParserUtil.getPathRelativeTo(
					testClassFile,
					portalGitWorkingDirectory.getWorkingDirectory());

			String className = testClassFile.getName();

			className = className.replace(".class", "");

			List<TestClassMethod> testClassMethods =
				jUnitTestClass.getTestClassMethods();

			for (TestClassMethod testClassMethod : testClassMethods) {
				CSVReport.Row csvReportRow = new CSVReport.Row();

				csvReportRow.add(className);
				csvReportRow.add(testClassMethod.getName());

				if (testClassMethod.isIgnored()) {
					csvReportRow.add("TRUE");
				}
				else {
					csvReportRow.add("");
				}

				csvReportRow.add(testClassFileRelativePath);

				csvReport.addRow(csvReportRow);
			}
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

		File csvReportFile = new File(
			JenkinsResultsParserUtil.combine(
				"Report_junit_", simpleDateFormat.format(new Date()), ".csv"));

		try {
			JenkinsResultsParserUtil.write(csvReportFile, csvReport.toString());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected JUnitBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);

		JSONArray autoBalanceTestFilesJSONArray = jsonObject.getJSONArray(
			"auto_balance_test_files");

		if ((autoBalanceTestFilesJSONArray != null) &&
			!autoBalanceTestFilesJSONArray.isEmpty()) {

			for (int i = 0; i < autoBalanceTestFilesJSONArray.length(); i++) {
				String autoBalanceTestFilePath =
					autoBalanceTestFilesJSONArray.getString(i);

				if (JenkinsResultsParserUtil.isNullOrEmpty(
						autoBalanceTestFilePath)) {

					continue;
				}

				_autoBalanceTestFiles.add(new File(autoBalanceTestFilePath));
			}
		}

		_includeAutoBalanceTests = jsonObject.getBoolean(
			"include_auto_balance_tests");
		_includeUnstagedTestClassFiles = jsonObject.getBoolean(
			"include_unstaged_test_class_files");
	}

	protected JUnitBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);

		if (ignore()) {
			_includeUnstagedTestClassFiles = false;

			return;
		}

		if (portalTestClassJob instanceof PortalAcceptancePullRequestJob) {
			PortalAcceptancePullRequestJob portalAcceptancePullRequestJob =
				(PortalAcceptancePullRequestJob)portalTestClassJob;

			_includeUnstagedTestClassFiles =
				portalAcceptancePullRequestJob.isCentralMergePullRequest();
		}
		else {
			_includeUnstagedTestClassFiles = false;
		}

		_loadJavaFiles(_getWorkingDirectory());

		setTestClasses();

		_setAutoBalanceTestFiles();

		_setIncludeAutoBalanceTests();

		setAxisTestClassGroups();

		setSegmentTestClassGroups();
	}

	protected JUnitBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob,
		JUnitTestBatch jUnitTestBatch) {

		super(batchName, portalTestClassJob);

		if (ignore()) {
			_includeUnstagedTestClassFiles = false;

			return;
		}

		if (portalTestClassJob instanceof PortalAcceptancePullRequestJob) {
			PortalAcceptancePullRequestJob portalAcceptancePullRequestJob =
				(PortalAcceptancePullRequestJob)portalTestClassJob;

			_includeUnstagedTestClassFiles =
				portalAcceptancePullRequestJob.isCentralMergePullRequest();
		}
		else {
			_includeUnstagedTestClassFiles = false;
		}

		_jUnitTestBatch = jUnitTestBatch;

		_loadJavaFiles(_getWorkingDirectory());

		setTestClasses(jUnitTestBatch.getTestSelector());

		_setAutoBalanceTestFiles();

		_setIncludeAutoBalanceTests();

		setAxisTestClassGroups();

		setSegmentTestClassGroups();
	}

	protected List<JobProperty> getDefaultExcludesJobProperties() {
		List<JobProperty> excludesJobProperties = new ArrayList<>();

		excludesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.excludes",
				JobProperty.Type.EXCLUDE_GLOB));

		return excludesJobProperties;
	}

	protected List<JobProperty> getDefaultIncludesJobProperties() {
		List<JobProperty> includesJobProperties = new ArrayList<>();

		includesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.includes",
				JobProperty.Type.INCLUDE_GLOB));

		return includesJobProperties;
	}

	protected List<PathMatcher> getIncludesPathMatchers() {
		if (!isRootCauseAnalysis()) {
			return getIncludePathMatchers(getIncludesJobProperties());
		}

		String portalBatchTestSelector = System.getenv(
			"PORTAL_BATCH_TEST_SELECTOR");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalBatchTestSelector)) {
			portalBatchTestSelector = getBuildStartProperty(
				"PORTAL_BATCH_TEST_SELECTOR");
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalBatchTestSelector)) {
			return getIncludePathMatchers(getIncludesJobProperties());
		}

		List<String> includeGlobs = new ArrayList<>();

		for (String glob : portalBatchTestSelector.split(",(?![^{}]*})")) {
			Matcher matcher = _globClassMethodPattern.matcher(glob);

			if (!matcher.matches()) {
				Collections.addAll(
					includeGlobs,
					JenkinsResultsParserUtil.getGlobsFromProperty(glob));

				continue;
			}

			String testClassGlob = matcher.group("testClassGlob");
			String testClassMethodName = matcher.group("testClassMethodName");

			glob = testClassGlob;

			List<String> testClassMethodNames =
				_globTestClassMethodNamesMap.get(testClassGlob);

			if (testClassMethodNames == null) {
				testClassMethodNames = new ArrayList<>();

				_globTestClassMethodNamesMap.put(
					testClassGlob, testClassMethodNames);
			}

			testClassMethodNames.add(testClassMethodName);

			Collections.addAll(
				includeGlobs,
				JenkinsResultsParserUtil.getGlobsFromProperty(glob));
		}

		return JenkinsResultsParserUtil.toPathMatchers(
			JenkinsResultsParserUtil.combine(
				JenkinsResultsParserUtil.getCanonicalPath(
					portalGitWorkingDirectory.getWorkingDirectory()),
				File.separator),
			includeGlobs.toArray(new String[0]));
	}

	protected List<JobProperty> getReleaseExcludesJobProperties() {
		List<JobProperty> excludesJobProperties = new ArrayList<>();

		excludesJobProperties.addAll(getDefaultExcludesJobProperties());

		excludesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.excludes.release",
				JobProperty.Type.EXCLUDE_GLOB));

		return excludesJobProperties;
	}

	protected List<JobProperty> getReleaseIncludesJobProperties() {
		return getDefaultIncludesJobProperties();
	}

	protected List<JobProperty> getRelevantExcludesJobProperties() {
		List<JobProperty> excludesJobProperties = new ArrayList<>();

		excludesJobProperties.addAll(getDefaultExcludesJobProperties());

		excludesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.excludes.relevant",
				JobProperty.Type.EXCLUDE_GLOB));

		return excludesJobProperties;
	}

	protected List<JobProperty> getRelevantIncludesJobProperties() {
		List<File> moduleDirsList = null;

		try {
			moduleDirsList = portalGitWorkingDirectory.getModuleDirsList();
		}
		catch (IOException ioException) {
			File workingDirectory =
				portalGitWorkingDirectory.getWorkingDirectory();

			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to get module directories in ",
					workingDirectory.getPath()),
				ioException);
		}

		List<JobProperty> includesJobProperties = new ArrayList<>();

		List<File> modifiedFilesList =
			portalGitWorkingDirectory.getModifiedFilesList();

		for (File modifiedFile : modifiedFilesList) {
			boolean foundModuleFile = false;

			for (File moduleDir : moduleDirsList) {
				if (JenkinsResultsParserUtil.isFileInDirectory(
						moduleDir, modifiedFile)) {

					foundModuleFile = true;

					break;
				}
			}

			if (foundModuleFile) {
				continue;
			}

			includesJobProperties.addAll(getDefaultIncludesJobProperties());

			break;
		}

		return includesJobProperties;
	}

	protected List<JobProperty> getRequiredExcludesJobProperties() {
		List<JobProperty> excludesJobProperties = new ArrayList<>();

		excludesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.excludes.required",
				JobProperty.Type.EXCLUDE_GLOB));

		return excludesJobProperties;
	}

	protected List<JobProperty> getRequiredIncludesJobProperties() {
		List<JobProperty> includesJobProperties = new ArrayList<>();

		includesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.includes.required",
				JobProperty.Type.INCLUDE_GLOB));

		return includesJobProperties;
	}

	protected List<JobProperty> getStableDefaultExcludesJobProperties() {
		List<JobProperty> excludesJobProperties = new ArrayList<>();

		String batchName = getBatchName();

		if (!batchName.endsWith("_stable")) {
			batchName += "_stable";
		}

		excludesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.excludes", NAME_STABLE_TEST_SUITE,
				batchName, JobProperty.Type.EXCLUDE_GLOB));

		return excludesJobProperties;
	}

	protected List<JobProperty> getStableDefaultIncludesJobProperties() {
		List<JobProperty> includesJobProperties = new ArrayList<>();

		String batchName = getBatchName();

		if (!batchName.endsWith("_stable")) {
			batchName += "_stable";
		}

		includesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.includes", NAME_STABLE_TEST_SUITE,
				batchName, JobProperty.Type.INCLUDE_GLOB));

		return includesJobProperties;
	}

	protected List<JobProperty> getStableRequiredExcludesJobProperties() {
		List<JobProperty> excludesJobProperties = new ArrayList<>();

		String batchName = getBatchName();

		if (!batchName.endsWith("_stable")) {
			batchName += "_stable";
		}

		excludesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.excludes.required",
				NAME_STABLE_TEST_SUITE, batchName,
				JobProperty.Type.EXCLUDE_GLOB));

		return excludesJobProperties;
	}

	protected List<JobProperty> getStableRequiredIncludesJobProperties() {
		List<JobProperty> includesJobProperties = new ArrayList<>();

		String batchName = getBatchName();

		if (!batchName.endsWith("_stable")) {
			batchName += "_stable";
		}

		includesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.includes.required",
				NAME_STABLE_TEST_SUITE, batchName,
				JobProperty.Type.INCLUDE_GLOB));

		return includesJobProperties;
	}

	protected List<JobProperty> getTestSelectorExcludesJobProperties() {
		JUnitTestSelector jUnitTestSelector = _jUnitTestBatch.getTestSelector();

		return jUnitTestSelector.getExcludesJobProperties();
	}

	protected List<JobProperty> getTestSelectorIncludesJobProperties() {
		JUnitTestSelector jUnitTestSelector = _jUnitTestBatch.getTestSelector();

		return jUnitTestSelector.getIncludesJobProperties();
	}

	@Override
	protected boolean ignore() {
		return false;
	}

	@Override
	protected void setAxisTestClassGroups() {
		long targetAxisDuration = getTargetAxisDuration();

		if (targetAxisDuration > 0) {
			List<TestClass> testClasses = getTestClasses();

			if (testClasses.isEmpty()) {
				if (!_includeAutoBalanceTests) {
					return;
				}

				axisTestClassGroups.add(
					0, TestClassGroupFactory.newAxisTestClassGroup(this));
			}
			else {
				GroupingStrategy groupingStrategy = getGroupingStrategy();

				if ((this instanceof ModulesJUnitBatchTestClassGroup) &&
					((groupingStrategy ==
						GroupingStrategy.TEST_TASK_AVERAGE_DURATION) ||
					 (groupingStrategy ==
						 GroupingStrategy.TEST_TASK_AVERAGE_TOTAL_DURATION) ||
					 (groupingStrategy ==
						 GroupingStrategy.TEST_TASK_LONGEST_DURATION))) {

					TestTaskBalancedListSplitter testTaskBalancedListSplitter =
						new TestTaskBalancedListSplitter(targetAxisDuration);

					List<List<TestTask>> testTasksLists =
						testTaskBalancedListSplitter.split(getTestTasks());

					for (List<TestTask> testTasks : testTasksLists) {
						if (_isIsolatedOversizedTestTask(testTasks)) {
							_splitOversizedTestTask(testTasks.get(0));
						}
						else {
							_createAxisTestClassGroup(testTasks);
						}
					}
				}
				else {
					TestClassBalancedListSplitter
						testClassBalancedListSplitter =
							new TestClassBalancedListSplitter(
								targetAxisDuration);

					List<List<TestClass>> testClassLists =
						testClassBalancedListSplitter.split(
							new ArrayList<>(testClasses));

					for (List<TestClass> testClassList : testClassLists) {
						AxisTestClassGroup axisTestClassGroup =
							TestClassGroupFactory.newAxisTestClassGroup(this);

						axisTestClassGroup.addTestClasses(testClassList);

						axisTestClassGroups.add(axisTestClassGroup);
					}
				}
			}
		}
		else {
			int axisCount = getAxisCount();

			if (axisCount == 0) {
				return;
			}

			if (!containsTestClasses()) {
				if (!_includeAutoBalanceTests) {
					return;
				}

				axisTestClassGroups.add(
					0, TestClassGroupFactory.newAxisTestClassGroup(this));
			}
			else {
				List<TestClass> testClasses = getTestClasses();

				int axisSize = (int)Math.ceil(
					(double)getTestClassCount() / axisCount);

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
		}

		if (!_includeAutoBalanceTests) {
			return;
		}

		for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
			for (File autoBalanceTestFile : _autoBalanceTestFiles) {
				String filePath = autoBalanceTestFile.getPath();

				filePath = filePath.replace(".class", ".java");

				TestClass testClass = TestClassFactory.newTestClass(
					this, new File(filePath));

				if (!testClass.hasTestClassMethods()) {
					continue;
				}

				axisTestClassGroup.addTestClass(testClass);
			}
		}
	}

	protected void setTestClasses() {
		List<PathMatcher> includesPathMatchers = getIncludesPathMatchers();

		if (includesPathMatchers.isEmpty()) {
			return;
		}

		long start = System.currentTimeMillis();

		List<PathMatcher> excludesPathMatchers = getPathMatchers(
			getExcludesJobProperties());
		List<PathMatcher> filterPathMatchers = getPathMatchers(
			getFilterJobProperties());

		for (final File javaTestClassFile : _javaTestClassFiles) {
			if (JenkinsResultsParserUtil.isFileExcluded(
					excludesPathMatchers, javaTestClassFile) ||
				!JenkinsResultsParserUtil.isFileIncluded(
					excludesPathMatchers, includesPathMatchers,
					javaTestClassFile) ||
				!JenkinsResultsParserUtil.isFileIncluded(
					null, filterPathMatchers, javaTestClassFile)) {

				continue;
			}

			TestClass testClass = null;

			List<String> testClassMethodNames = getTestClassMethodNames(
				javaTestClassFile, getGlobTestClassMethodNamesMap());

			if ((testClassMethodNames != null) &&
				!testClassMethodNames.isEmpty()) {

				testClass = TestClassFactory.newTestClass(
					this, javaTestClassFile, testClassMethodNames);
			}
			else {
				testClass = TestClassFactory.newTestClass(
					this, javaTestClassFile);
			}

			if ((testClass != null) && !testClass.isIgnored() &&
				testClass.hasTestClassMethods()) {

				addTestClass(testClass);
			}
		}

		long duration = System.currentTimeMillis() - start;

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"[", getBatchName(), "] Found ",
				String.valueOf(getTestClassCount()), " test classes in ",
				JenkinsResultsParserUtil.toDurationString(duration)));
	}

	protected void setTestClasses(JUnitTestSelector jUnitTestSelector) {
		List<JobProperty> includesJobProperties =
			jUnitTestSelector.getIncludesJobProperties();

		recordJobProperties(includesJobProperties);

		List<PathMatcher> includesPathMatchers = getPathMatchers(
			includesJobProperties);

		if (includesPathMatchers.isEmpty()) {
			return;
		}

		long start = System.currentTimeMillis();

		List<PathMatcher> filterPathMatchers = getPathMatchers(
			getFilterJobProperties());

		List<JobProperty> excludesJobProperties =
			jUnitTestSelector.getExcludesJobProperties();

		List<PathMatcher> excludesPathMatchers = getPathMatchers(
			excludesJobProperties);

		recordJobProperties(excludesJobProperties);

		BatchTestClassGroup batchTestClassGroup = this;

		for (final File javaTestClassFile : _javaTestClassFiles) {
			if (JenkinsResultsParserUtil.isFileExcluded(
					excludesPathMatchers, javaTestClassFile) ||
				!JenkinsResultsParserUtil.isFileIncluded(
					excludesPathMatchers, includesPathMatchers,
					javaTestClassFile) ||
				!JenkinsResultsParserUtil.isFileIncluded(
					null, filterPathMatchers, javaTestClassFile)) {

				continue;
			}

			TestClass testClass = TestClassFactory.newTestClass(
				batchTestClassGroup, javaTestClassFile);

			if ((testClass != null) && !testClass.isIgnored() &&
				testClass.hasTestClassMethods()) {

				addTestClass(testClass);
			}
		}

		long duration = System.currentTimeMillis() - start;

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"[", getBatchName(), "] Found ",
				String.valueOf(getTestClassCount()), " test classes in ",
				JenkinsResultsParserUtil.toDurationString(duration)));
	}

	private void _createAxisTestClassGroup(List<TestTask> testTasks) {
		AxisTestClassGroup axisTestClassGroup =
			TestClassGroupFactory.newAxisTestClassGroup(this);

		for (TestTask testTask : testTasks) {
			axisTestClassGroup.addTestClasses(testTask.getTestClasses());
		}

		if (axisTestClassGroup instanceof ModulesJUnitAxisTestClassGroup) {
			ModulesJUnitAxisTestClassGroup modulesJUnitAxisTestClassGroup =
				(ModulesJUnitAxisTestClassGroup)axisTestClassGroup;

			for (TestTask testTask : testTasks) {
				modulesJUnitAxisTestClassGroup.addTestTask(testTask);
			}
		}

		axisTestClassGroups.add(axisTestClassGroup);
	}

	private File _getWorkingDirectory() {
		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		File workingDirectory = portalGitWorkingDirectory.getWorkingDirectory();

		JobProperty jobProperty = getJobProperty("git.working.directory");

		String jobPropertyValue = jobProperty.getValue();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue)) {
			workingDirectory = new File(jobPropertyValue);
		}

		return workingDirectory;
	}

	private boolean _isIsolatedOversizedTestTask(List<TestTask> testTasks) {
		if (testTasks.size() > 1) {
			return false;
		}

		TestTask testTask = testTasks.get(0);

		long totalTaskDuration =
			testTask.getWeight() + testTask.getOverheadWeight();

		if (totalTaskDuration <= getTargetAxisDuration()) {
			return false;
		}

		return true;
	}

	private void _loadJavaFiles(File workingDirectory) {
		synchronized (_javaFilesLoaded) {
			if (_javaFilesLoaded.get()) {
				return;
			}

			long start = System.currentTimeMillis();

			try {
				Files.walkFileTree(
					workingDirectory.toPath(),
					new SimpleFileVisitor<Path>() {

						@Override
						public FileVisitResult preVisitDirectory(
							Path filePath,
							BasicFileAttributes basicFileAttributes) {

							String filePathString = filePath.toString();

							for (String ignorableDir : _IGNORABLE_DIRS) {
								if (filePathString.endsWith(ignorableDir)) {
									return FileVisitResult.SKIP_SUBTREE;
								}
							}

							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFile(
							Path path,
							BasicFileAttributes basicFileAttributes) {

							_searchedFileCount++;

							String pathString = path.toString();

							if (pathString.endsWith(".java")) {
								Path parentPath = path.getParent();

								_javaDirPathStrings.add(parentPath.toString());
							}

							if (pathString.endsWith("Test.java") ||
								pathString.endsWith("TestCase.java")) {

								_javaTestClassFiles.add(path.toFile());

								return FileVisitResult.CONTINUE;
							}

							return FileVisitResult.CONTINUE;
						}

					});
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to search for test file names in " +
						workingDirectory.toPath(),
					ioException);
			}

			long duration = System.currentTimeMillis() - start;

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Found ", String.valueOf(_javaDirPathStrings.size()),
					" Java directories and ",
					String.valueOf(_javaTestClassFiles.size()),
					" Java test class files in ", workingDirectory.toString(),
					" in ",
					JenkinsResultsParserUtil.toDurationString(duration)));

			_javaFilesLoaded.set(true);
		}
	}

	private void _setAutoBalanceTestFiles() {
		JobProperty jobProperty = getJobProperty(
			"test.class.names.auto.balance");

		String jobPropertyValue = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue)) {
			return;
		}

		recordJobProperty(jobProperty);

		for (String autoBalanceTestName : jobPropertyValue.split(",")) {
			String fullClassName = autoBalanceTestName.replaceAll(
				".*\\/?(com\\/.*)\\.(class|java)", "$1");

			fullClassName = fullClassName.replaceAll("/", "\\.");

			File javaTestClassFile = getJavaFileFromFullClassName(
				fullClassName);

			if (!JenkinsResultsParserUtil.isFileIncluded(
					null, getPathMatchers(getFilterJobProperties()),
					javaTestClassFile)) {

				continue;
			}

			_autoBalanceTestFiles.add(javaTestClassFile);
		}
	}

	private void _setIncludeAutoBalanceTests() {
		if (containsTestClasses()) {
			_includeAutoBalanceTests = true;

			return;
		}

		List<File> modifiedJavaFilesList =
			portalGitWorkingDirectory.getModifiedFilesList(
				_includeUnstagedTestClassFiles, null,
				JenkinsResultsParserUtil.toPathMatchers(
					JenkinsResultsParserUtil.combine(
						"**", File.separator, "*.java")));

		if (!_autoBalanceTestFiles.isEmpty() &&
			!modifiedJavaFilesList.isEmpty()) {

			_includeAutoBalanceTests = true;

			return;
		}

		_includeAutoBalanceTests = false;
	}

	private void _splitOversizedTestTask(TestTask testTask) {
		testTask.setSplit(true);

		long targetAxisDuration = getTargetAxisDuration();

		TestClassBalancedListSplitter testClassBalancedListSplitter =
			new TestClassBalancedListSplitter(targetAxisDuration);

		List<List<TestClass>> testClassesList = new ArrayList<>(
			testClassBalancedListSplitter.split(testTask.getTestClasses()));

		for (List<TestClass> testClasses : testClassesList) {
			AxisTestClassGroup axisTestClassGroup =
				TestClassGroupFactory.newAxisTestClassGroup(this);

			axisTestClassGroup.addTestClasses(testClasses);

			if (axisTestClassGroup instanceof ModulesJUnitAxisTestClassGroup) {
				ModulesJUnitAxisTestClassGroup modulesJUnitAxisTestClassGroup =
					(ModulesJUnitAxisTestClassGroup)axisTestClassGroup;

				modulesJUnitAxisTestClassGroup.addTestTask(testTask);
			}

			axisTestClassGroups.add(axisTestClassGroup);
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Split ", testTask.getName(), " into ",
				String.valueOf(testClassesList.size()), " groups."));
	}

	private static final String[] _IGNORABLE_DIRS = {
		"/.git", "/.gradle", "/.m2", "/.m2-tmp", "/build/node", "/build/tmp",
		"/node_modules"
	};

	private static final Pattern _globClassMethodPattern = Pattern.compile(
		"(?<testClassGlob>[^#]+)#(?<testClassMethodName>.+)");
	private static final Set<String> _javaDirPathStrings =
		ConcurrentHashMap.newKeySet();
	private static final AtomicBoolean _javaFilesLoaded = new AtomicBoolean();
	private static final Set<File> _javaTestClassFiles =
		ConcurrentHashMap.newKeySet();
	private static int _searchedFileCount;

	private final List<File> _autoBalanceTestFiles = new ArrayList<>();
	private final Map<String, List<String>> _globTestClassMethodNamesMap =
		new HashMap<>();
	private boolean _includeAutoBalanceTests;
	private final boolean _includeUnstagedTestClassFiles;
	private JUnitTestBatch _jUnitTestBatch;

}