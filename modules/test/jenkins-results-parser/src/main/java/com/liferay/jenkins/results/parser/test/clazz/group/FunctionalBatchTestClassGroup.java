/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.AntException;
import com.liferay.jenkins.results.parser.AntUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalHotfixReleaseJob;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.batch.PoshiTestBatch;
import com.liferay.jenkins.results.parser.test.batch.PoshiTestSelector;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassBalancedListSplitter;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.poshi.core.PoshiContext;
import com.liferay.poshi.core.util.PropsUtil;

import java.io.File;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Yi-Chen Tsai
 */
public class FunctionalBatchTestClassGroup extends BatchTestClassGroup {

	@Override
	public int getAxisCount() {
		if (ignore()) {
			return 0;
		}

		return axisTestClassGroups.size();
	}

	@Override
	public JSONObject getJSONObject() {
		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = super.getJSONObject();

		StringBuilder sb = new StringBuilder();

		sb.append("(");
		sb.append(getTestBatchRunPropertyQuery());
		sb.append(") AND (ignored == null)");

		String testRunEnvironment = PropsUtil.get("test.run.environment");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testRunEnvironment)) {
			sb.append(" AND (test.run.environment == \"");
			sb.append(testRunEnvironment);
			sb.append("\" OR test.run.environment == null)");
		}

		jsonObject.put("pql_query", sb.toString());

		jsonObject.put("target_duration", getTargetAxisDuration());
		jsonObject.put(
			"test_batch_run_property_queries", _testBatchRunPropertyQueries);

		return jsonObject;
	}

	public List<File> getTestBaseDirs() {
		String testBaseDirPath = _getTestBaseDirPath();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testBaseDirPath)) {
			return Arrays.asList(new File(testBaseDirPath));
		}

		return Arrays.asList(
			new File(
				portalGitWorkingDirectory.getWorkingDirectory(),
				"portal-web/test/functional/portalweb"));
	}

	public String getTestBatchRunPropertyQuery() {
		List<File> testBaseDirs = getTestBaseDirs();

		if (testBaseDirs.isEmpty()) {
			return null;
		}

		return getTestBatchRunPropertyQuery(testBaseDirs.get(0));
	}

	public String getTestBatchRunPropertyQuery(File testBaseDir) {
		return _testBatchRunPropertyQueries.get(testBaseDir);
	}

	@Override
	public List<TestClass> getTestClasses() {
		List<TestClass> testClasses = new ArrayList<>();

		for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
			testClasses.addAll(axisTestClassGroup.getTestClasses());
		}

		return testClasses;
	}

	public String getWorkspaceName() {
		JobProperty jobProperty = getJobProperty(
			"test.workspace.name", testSuiteName, batchName);

		if ((jobProperty == null) ||
			JenkinsResultsParserUtil.isNullOrEmpty(jobProperty.getValue())) {

			return null;
		}

		recordJobProperty(jobProperty);

		return jobProperty.getValue();
	}

	public boolean isUpgradeFile(File file) {
		Matcher matcher = _upgradeFileNamePattern.matcher(file.toString());

		return matcher.find();
	}

	protected FunctionalBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);

		JSONObject testBatchRunPropertyQueriesJSONObject =
			jsonObject.optJSONObject("test_batch_run_property_queries");

		if (testBatchRunPropertyQueriesJSONObject == null) {
			return;
		}

		for (String key : testBatchRunPropertyQueriesJSONObject.keySet()) {
			_testBatchRunPropertyQueries.put(
				new File(key),
				testBatchRunPropertyQueriesJSONObject.getString(key));
		}
	}

	protected FunctionalBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);

		if (ignore()) {
			return;
		}

		_setTestBatchRunPropertyQueries();

		setAxisTestClassGroups();

		setSegmentTestClassGroups();
	}

	protected FunctionalBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob,
		PoshiTestBatch poshiTestBatch) {

		super(batchName, portalTestClassJob);

		if (ignore()) {
			return;
		}

		_setTestBatchRunPropertyQueries(poshiTestBatch.getTestSelector());

		setAxisTestClassGroups();

		setSegmentTestClassGroups();
	}

	@Override
	protected int getAxisMaxSize() {
		long targetAxisDuration = getTargetAxisDuration();

		if (targetAxisDuration > 0) {
			return AXES_SIZE_MAX_DEFAULT;
		}

		return super.getAxisMaxSize();
	}

	protected String getDefaultTestBatchRunPropertyQuery(
		File testBaseDir, String testSuiteName) {

		String query = System.getenv("TEST_BATCH_RUN_PROPERTY_QUERY");

		if (JenkinsResultsParserUtil.isNullOrEmpty(query)) {
			query = getBuildStartProperty("TEST_BATCH_RUN_PROPERTY_QUERY");
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(query)) {
			return query;
		}

		JobProperty jobProperty = getJobProperty(
			"test.batch.run.property.query", testSuiteName, batchName);

		File testPropertiesFile = new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "test.properties");

		JenkinsResultsParserUtil.validatePQL(
			jobProperty.getValue(), testPropertiesFile);

		recordJobProperty(jobProperty);

		return jobProperty.getValue();
	}

	protected List<File> getModifiedFiles() {
		synchronized (_poshiTestCasePattern) {
			if (_modifiedFiles != null) {
				return _modifiedFiles;
			}

			_modifiedFiles = new ArrayList<>();

			if (portalTestClassJob instanceof PortalHotfixReleaseJob) {
				PortalHotfixReleaseJob portalHotfixReleaseJob =
					(PortalHotfixReleaseJob)portalTestClassJob;

				_modifiedFiles = portalHotfixReleaseJob.getModifiedFiles();
			}
			else {
				_modifiedFiles =
					portalGitWorkingDirectory.getModifiedFilesList();
			}

			return _modifiedFiles;
		}
	}

	protected List<List<TestClass>> getPoshiTestClassGroups(File testBaseDir) {
		String query = getTestBatchRunPropertyQuery(testBaseDir);

		if (JenkinsResultsParserUtil.isNullOrEmpty(query)) {
			return new ArrayList<>();
		}

		synchronized (_poshiTestCasePattern) {
			File cachedTestBaseDir = _testBaseDirAtomicReference.get();

			if ((cachedTestBaseDir == null) ||
				!cachedTestBaseDir.equals(testBaseDir)) {

				PortalGitWorkingDirectory portalGitWorkingDirectory =
					portalTestClassJob.getPortalGitWorkingDirectory();

				File portalWorkingDirectory =
					portalGitWorkingDirectory.getWorkingDirectory();

				Map<String, String> parameters = new HashMap<>();

				String testBaseDirPath = null;

				if ((testBaseDir != null) && testBaseDir.exists()) {
					testBaseDirPath = JenkinsResultsParserUtil.getCanonicalPath(
						testBaseDir);

					parameters.put("test.base.dir.name", testBaseDirPath);
				}

				try {
					AntUtil.callTarget(
						portalWorkingDirectory, "build-test.xml",
						"prepare-poshi-runner-properties", parameters);
				}
				catch (AntException antException) {
					throw new RuntimeException(antException);
				}

				Properties properties = JenkinsResultsParserUtil.getProperties(
					new File(
						portalWorkingDirectory, "portal-web/poshi.properties"),
					new File(
						portalWorkingDirectory,
						"portal-web/poshi-ext.properties"));

				File poshiPropertiesFile = new File(
					testBaseDirPath, "poshi.properties");

				if (poshiPropertiesFile.exists()) {
					properties = JenkinsResultsParserUtil.getProperties(
						poshiPropertiesFile,
						new File(testBaseDirPath, "poshi-ext.properties"));
				}

				if (!JenkinsResultsParserUtil.isNullOrEmpty(testBaseDirPath)) {
					properties.setProperty(
						"test.base.dir.name", testBaseDirPath);
				}

				properties.setProperty("poshi.file.read.thread.pool", "8");
				properties.setProperty("poshi.file.read.timeout", "30");

				PropsUtil.clear();

				PropsUtil.setProperties(properties);

				try {
					PoshiContext.clear();

					PoshiContext.readFiles();
				}
				catch (Exception exception) {
					throw new RuntimeException(exception);
				}

				_testBaseDirAtomicReference.set(testBaseDir);
			}

			try {
				return getTestClassGroups(
					PoshiContext.getTestBatchGroups(query, getAxisMaxSize()));
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}
	}

	protected List<List<TestClass>> getTestClassGroups(
		List<List<String>> testBatchGroups) {

		List<List<TestClass>> testClassGroups = new ArrayList<>();

		if (testBatchGroups.isEmpty()) {
			return testClassGroups;
		}

		for (List<String> testBatchGroup : testBatchGroups) {
			List<TestClass> testClassGroup = new ArrayList<>();

			for (String testClassMethodName : testBatchGroup) {
				testClassGroup.add(
					TestClassFactory.newTestClass(this, testClassMethodName));
			}

			testClassGroups.add(testClassGroup);
		}

		return testClassGroups;
	}

	@Override
	protected void setAxisTestClassGroups() {
		if (!axisTestClassGroups.isEmpty()) {
			return;
		}

		for (File testBaseDir : getTestBaseDirs()) {
			String query = getTestBatchRunPropertyQuery(testBaseDir);

			if (query == null) {
				continue;
			}

			List<List<TestClass>> poshiTestClassGroups =
				getPoshiTestClassGroups(testBaseDir);

			long targetAxisDuration = getTargetAxisDuration();

			for (List<TestClass> poshiTestClassGroup : poshiTestClassGroups) {
				if (poshiTestClassGroup.isEmpty()) {
					continue;
				}

				if (targetAxisDuration > 0) {
					TestClassBalancedListSplitter
						testClassBalancedListSplitter =
							new TestClassBalancedListSplitter(
								targetAxisDuration);

					List<List<TestClass>> testClassLists =
						testClassBalancedListSplitter.split(
							poshiTestClassGroup);

					for (List<TestClass> testClassList : testClassLists) {
						AxisTestClassGroup axisTestClassGroup =
							TestClassGroupFactory.newAxisTestClassGroup(
								this, testBaseDir);

						axisTestClassGroup.addTestClasses(testClassList);

						axisTestClassGroups.add(axisTestClassGroup);
					}
				}
				else {
					AxisTestClassGroup axisTestClassGroup =
						TestClassGroupFactory.newAxisTestClassGroup(
							this, testBaseDir);

					for (TestClass testClass : poshiTestClassGroup) {
						axisTestClassGroup.addTestClass(testClass);
					}

					axisTestClassGroups.add(axisTestClassGroup);
				}
			}
		}
	}

	private String _concatPQL(
		File file, String testSuiteName, String concatedPQL) {

		if (file == null) {
			return null;
		}

		if (JenkinsResultsParserUtil.isPoshiFile(file)) {
			return "";
		}

		File canonicalFile = JenkinsResultsParserUtil.getCanonicalFile(file);

		if (canonicalFile.equals(
				portalGitWorkingDirectory.getWorkingDirectory())) {

			return concatedPQL;
		}

		File parentFile = canonicalFile.getParentFile();

		if ((parentFile == null) || !parentFile.exists()) {
			return "";
		}

		File modulesBaseDir = new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "modules");

		Path modulesBaseDirPath = modulesBaseDir.toPath();

		Path parentFilePath = parentFile.toPath();

		File testPropertiesFile = new File(canonicalFile, "test.properties");

		if (modulesBaseDirPath.equals(parentFilePath) &&
			!testPropertiesFile.exists()) {

			return concatedPQL;
		}

		if (!canonicalFile.isDirectory() || !testPropertiesFile.exists()) {
			return _concatPQL(parentFile, testSuiteName, concatedPQL);
		}

		if ((_traversedPropertyFiles.contains(testPropertiesFile) &&
			 testSuiteName.equals("relevant")) ||
			_traversedUpgradeFiles.contains(testPropertiesFile)) {

			return concatedPQL;
		}

		if (testSuiteName.equals("upgrades-relevant") &&
			!_traversedUpgradeFiles.contains(testPropertiesFile)) {

			_traversedUpgradeFiles.add(testPropertiesFile);
		}

		_traversedPropertyFiles.add(testPropertiesFile);

		JobProperty jobProperty = getJobProperty(
			"test.batch.run.property.query", testSuiteName, batchName,
			canonicalFile, JobProperty.Type.MODULE_TEST_DIR);

		String testBatchPropertyQuery = jobProperty.getValue();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testBatchPropertyQuery) &&
			!testBatchPropertyQuery.equals("false") &&
			!concatedPQL.contains(testBatchPropertyQuery)) {

			JenkinsResultsParserUtil.validatePQL(
				testBatchPropertyQuery, testPropertiesFile);

			recordJobProperty(jobProperty);

			if (!concatedPQL.isEmpty()) {
				concatedPQL += JenkinsResultsParserUtil.combine(
					" OR (", testBatchPropertyQuery, ")");
			}
			else {
				concatedPQL += testBatchPropertyQuery;
			}
		}

		Properties testProperties = JenkinsResultsParserUtil.getProperties(
			testPropertiesFile);

		boolean ignoreParents = Boolean.valueOf(
			JenkinsResultsParserUtil.getProperty(
				testProperties, "ignoreParents", false, testSuiteName));

		if (ignoreParents ||
			parentFile.equals(
				portalGitWorkingDirectory.getWorkingDirectory())) {

			return concatedPQL;
		}

		if (!parentFilePath.equals(modulesBaseDirPath)) {
			return _concatPQL(parentFile, testSuiteName, concatedPQL);
		}

		return concatedPQL;
	}

	private String _getTestBaseDirPath() {
		JobProperty jobProperty = getJobProperty(
			"test.base.dir", testSuiteName, batchName);

		if ((jobProperty == null) ||
			JenkinsResultsParserUtil.isNullOrEmpty(jobProperty.getValue())) {

			String workspaceName = getWorkspaceName();

			if (JenkinsResultsParserUtil.isNullOrEmpty(workspaceName)) {
				return null;
			}

			File testBaseDir = new File(
				portalGitWorkingDirectory.getWorkingDirectory(),
				JenkinsResultsParserUtil.combine(
					"workspaces/", workspaceName, "/poshi"));

			if (!testBaseDir.exists()) {
				return null;
			}

			return JenkinsResultsParserUtil.getCanonicalPath(testBaseDir);
		}

		File testBaseDir = new File(
			portalGitWorkingDirectory.getWorkingDirectory(),
			jobProperty.getValue());

		if (!testBaseDir.exists()) {
			return null;
		}

		recordJobProperty(jobProperty);

		return JenkinsResultsParserUtil.getCanonicalPath(testBaseDir);
	}

	private String _getTestBatchRunPropertyQuery(File testBaseDir) {
		if (!testRelevantChanges && !testHotfixChanges) {
			String defaultPQL = getDefaultTestBatchRunPropertyQuery(
				testBaseDir, testSuiteName);

			JobProperty globalJobProperty = getJobProperty(
				"test.batch.run.property.global.query", testSuiteName,
				batchName);

			String globalJobPropertyValue = globalJobProperty.getValue();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					globalJobPropertyValue)) {

				JenkinsResultsParserUtil.validatePQL(
					globalJobPropertyValue, testBaseDir);

				recordJobProperty(globalJobProperty);

				return JenkinsResultsParserUtil.combine(
					"(", globalJobPropertyValue, ") AND (", defaultPQL, ")");
			}

			return defaultPQL;
		}

		File testPropertiesFile = new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "test.properties");

		StringBuilder sb = new StringBuilder();

		for (File modifiedFile : getModifiedFiles()) {
			String testBatchPQL = _concatPQL(
				modifiedFile, getTestSuiteName(), "");

			if (JenkinsResultsParserUtil.isNullOrEmpty(testBatchPQL) ||
				testBatchPQL.equals("false")) {

				continue;
			}

			if (sb.indexOf(testBatchPQL) == -1) {
				if (!JenkinsResultsParserUtil.isNullOrEmpty(sb.toString())) {
					sb.append(" OR ");
				}

				sb.append("(");
				sb.append(testBatchPQL);
				sb.append(")");
			}

			if (isUpgradeFile(modifiedFile)) {
				String upgradePQL = _concatPQL(
					modifiedFile, "upgrades-relevant", "");

				if (!JenkinsResultsParserUtil.isNullOrEmpty(upgradePQL)) {
					if (sb.length() > 0) {
						sb.append(" OR ");
					}

					sb.append("(");
					sb.append(upgradePQL);
					sb.append(")");
				}
			}
		}

		String defaultPQL = getDefaultTestBatchRunPropertyQuery(
			testBaseDir, testSuiteName);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(defaultPQL) &&
			(sb.indexOf(defaultPQL) == -1)) {

			JenkinsResultsParserUtil.validatePQL(defaultPQL, testBaseDir);

			if (sb.length() > 0) {
				sb.append(" OR ");
			}

			sb.append("(");
			sb.append(defaultPQL);
			sb.append(")");
		}

		if (!NAME_STABLE_TEST_SUITE.equals(getTestSuiteName())) {
			String batchName = getBatchName();

			if (!batchName.endsWith("_stable")) {
				batchName += "_stable";
			}

			JobProperty jobProperty = getJobProperty(
				"test.batch.run.property.query", NAME_STABLE_TEST_SUITE,
				batchName);

			String jobPropertyValue = jobProperty.getValue();

			if ((jobPropertyValue != null) && includeStableTestSuite &&
				isStableTestSuiteBatch(batchName) &&
				(sb.indexOf(jobPropertyValue) == -1)) {

				JenkinsResultsParserUtil.validatePQL(
					jobPropertyValue, testPropertiesFile);

				recordJobProperty(jobProperty);

				if (sb.length() > 0) {
					sb.append(" OR ");
				}

				sb.append("(");
				sb.append(jobPropertyValue);
				sb.append(")");
			}
		}

		String testBatchRunPropertyQuery = sb.toString();

		JobProperty jobProperty = getJobProperty(
			"test.batch.run.property.global.query", testSuiteName, batchName);

		String jobPropertyValue = jobProperty.getValue();

		if (jobPropertyValue != null) {
			JenkinsResultsParserUtil.validatePQL(
				jobPropertyValue, testPropertiesFile);

			recordJobProperty(jobProperty);

			testBatchRunPropertyQuery = JenkinsResultsParserUtil.combine(
				"(", jobPropertyValue, ") AND (", testBatchRunPropertyQuery,
				")");
		}

		return testBatchRunPropertyQuery;
	}

	private void _setTestBatchRunPropertyQueries() {
		if (isRootCauseAnalysis()) {
			String portalBatchTestSelector = System.getenv(
				"PORTAL_BATCH_TEST_SELECTOR");

			if (JenkinsResultsParserUtil.isNullOrEmpty(
					portalBatchTestSelector)) {

				portalBatchTestSelector = getBuildStartProperty(
					"PORTAL_BATCH_TEST_SELECTOR");
			}

			if ((portalBatchTestSelector != null) &&
				portalBatchTestSelector.startsWith("LocalFile.")) {

				portalBatchTestSelector = portalBatchTestSelector.replace(
					"LocalFile.", "");
			}

			_testBatchRunPropertyQueries.put(
				new File(
					portalGitWorkingDirectory.getWorkingDirectory(),
					"portal-web/test/functional/portalweb"),
				"test.class.method.name == " + portalBatchTestSelector);

			return;
		}

		for (File testBaseDir : getTestBaseDirs()) {
			String testBatchRunPropertyQuery = _getTestBatchRunPropertyQuery(
				testBaseDir);

			if (JenkinsResultsParserUtil.isNullOrEmpty(
					testBatchRunPropertyQuery)) {

				continue;
			}

			_testBatchRunPropertyQueries.put(
				testBaseDir, testBatchRunPropertyQuery);
		}
	}

	private void _setTestBatchRunPropertyQueries(
		PoshiTestSelector poshiTestSelector) {

		recordJobProperties(poshiTestSelector.getPoshiJobProperties());

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			portalTestClassJob.getPortalGitWorkingDirectory();

		_testBatchRunPropertyQueries.put(
			new File(
				portalGitWorkingDirectory.getWorkingDirectory(),
				"portal-web/test/functional/portalweb"),
			poshiTestSelector.getPoshiQuery());
	}

	private static List<File> _modifiedFiles;
	private static final Pattern _poshiTestCasePattern = Pattern.compile(
		"(?<namespace>[^\\.]+)\\.(?<className>[^\\#]+)\\#(?<methodName>.*)");
	private static final AtomicReference<File> _testBaseDirAtomicReference =
		new AtomicReference<>();
	private static final Pattern _upgradeFileNamePattern = Pattern.compile(
		"(.*\\/verify\\/.*|.*\\/upgrade\\/.*|.*\\.sql)");

	private final Map<File, String> _testBatchRunPropertyQueries =
		new HashMap<>();
	private final Set<File> _traversedPropertyFiles = new HashSet<>();
	private final Set<File> _traversedUpgradeFiles = new HashSet<>();

}