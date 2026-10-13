/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.AntException;
import com.liferay.jenkins.results.parser.AntUtil;
import com.liferay.jenkins.results.parser.Environment;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.NotificationUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.Retryable;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.batch.PlaywrightTestBatch;
import com.liferay.jenkins.results.parser.test.batch.PlaywrightTestSelector;
import com.liferay.jenkins.results.parser.test.clazz.PlaywrightJUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.PlaywrightTestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class PlaywrightBatchTestClassGroup extends BatchTestClassGroup {

	public void addDefaultProjectJobProperty(String batchName) {
		if (isRootCauseAnalysis()) {
			String portalBatchTestSelector = Environment.get(
				"PORTAL_BATCH_TEST_SELECTOR");

			if (JenkinsResultsParserUtil.isNullOrEmpty(
					portalBatchTestSelector)) {

				portalBatchTestSelector = getBuildStartProperty(
					"PORTAL_BATCH_TEST_SELECTOR");
			}

			Matcher matcher = _playwrightFileNamePattern.matcher(
				portalBatchTestSelector);

			if (matcher.matches()) {
				String projectName = matcher.group("projectName");

				_addProjectNames(projectName.replaceAll("/", "."));
			}
			else {
				_addProjectNames(portalBatchTestSelector);
			}

			return;
		}

		List<JobProperty> jobProperties = new ArrayList<>();

		JobProperty playwrightProjectsIncludesJobProperty =
			_getPlaywrightProjectsIncludesJobProperty();

		if (playwrightProjectsIncludesJobProperty == null) {
			_projectNames.addAll(_getDefaultProjectNames());
		}
		else {
			_addProjectNames(playwrightProjectsIncludesJobProperty.getValue());

			jobProperties.add(playwrightProjectsIncludesJobProperty);
		}

		JobProperty playwrightProjectsExcludesJobProperty = getJobProperty(
			"playwright.projects.excludes", testSuiteName, batchName);

		String playwrightProjectsExcludesJobPropertyValue =
			playwrightProjectsExcludesJobProperty.getValue();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(
				playwrightProjectsExcludesJobPropertyValue)) {

			removeProjectNames(playwrightProjectsExcludesJobPropertyValue);

			jobProperties.add(playwrightProjectsExcludesJobProperty);
		}

		recordJobProperties(jobProperties);
	}

	public void writeTestCSVReportFile() throws Exception {
		CSVReport csvReport = new CSVReport(
			new CSVReport.Row(
				"File Name", "Test Name", "Ignored", "File Path"));

		for (PlaywrightJUnitTestClass playwrightJUnitTestClass :
				TestClassFactory.getPlaywrightTestClasses()) {

			File testClassFile = playwrightJUnitTestClass.getTestClassFile();

			String testClassFileRelativePath =
				JenkinsResultsParserUtil.getPathRelativeTo(
					testClassFile,
					portalGitWorkingDirectory.getWorkingDirectory());

			List<TestClassMethod> testClassMethods =
				playwrightJUnitTestClass.getTestClassMethods();

			for (TestClassMethod testClassMethod : testClassMethods) {
				CSVReport.Row csvReportRow = new CSVReport.Row();

				csvReportRow.add(testClassFile.getName());
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
				"Report_playwright_", simpleDateFormat.format(new Date()),
				".csv"));

		try {
			JenkinsResultsParserUtil.write(csvReportFile, csvReport.toString());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected PlaywrightBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);
	}

	protected PlaywrightBatchTestClassGroup(
		String batchName, PlaywrightTestBatch playwrightTestBatch,
		PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);

		PlaywrightTestSelector playwrightTestSelector =
			playwrightTestBatch.getTestSelector();

		Set<JobProperty> playwrightJobProperties =
			playwrightTestSelector.getPlaywrightJobProperties();

		playwrightJobProperties.removeAll(Collections.singleton(null));

		for (JobProperty jobProperty : playwrightJobProperties) {
			_addProjectNames(jobProperty.getValue());
		}

		JobProperty excludesJobProperty =
			playwrightTestSelector.getPlaywrightExcludesJobProperty();

		if (excludesJobProperty != null) {
			removeProjectNames(excludesJobProperty.getValue());

			playwrightJobProperties.add(excludesJobProperty);
		}

		recordJobProperties(new ArrayList<>(playwrightJobProperties));

		setTestClasses();
	}

	protected PlaywrightBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);

		if (ignore()) {
			return;
		}

		if (testRelevantChanges) {
			List<JobProperty> relevantPlaywrightJobProperties =
				getRelevantPlaywrightJobProperties();

			if (!relevantPlaywrightJobProperties.isEmpty()) {
				recordJobProperties(relevantPlaywrightJobProperties);
			}
		}
		else {
			addDefaultProjectJobProperty(batchName);
		}

		setTestClasses();
	}

	protected int getAxisCount(List<TestClass> testClasses) {
		long totalDuration = 0L;

		for (TestClass testClass : testClasses) {
			totalDuration += testClass.getAverageDuration();
		}

		if (totalDuration == 0L) {
			long targetAxisClassSize = 20;

			JobProperty targetAxisClassSizeJobProperty = getJobProperty(
				"test.batch.target.axis.class.size");

			String targetAxisClassSizeValue =
				targetAxisClassSizeJobProperty.getValue();

			if (targetAxisClassSizeValue != null) {
				targetAxisClassSize = Long.parseLong(targetAxisClassSizeValue);
			}

			long axisCount = 1;

			axisCount += Math.floorDiv(testClasses.size(), targetAxisClassSize);

			return Math.toIntExact(axisCount);
		}

		JobProperty targetAxisDurationJobProperty = getJobProperty(
			"test.batch.target.axis.duration");

		String targetAxisDurationValue =
			targetAxisDurationJobProperty.getValue();

		if (!JenkinsResultsParserUtil.isInteger(targetAxisDurationValue)) {
			return getAxisCount();
		}

		recordJobProperty(targetAxisDurationJobProperty);

		long targetAxisDuration = Long.parseLong(targetAxisDurationValue);

		JobProperty performanceModifierJobProperty = getJobProperty(
			"test.batch.performance.modifier");

		String performanceModifier = performanceModifierJobProperty.getValue();

		if (JenkinsResultsParserUtil.isDouble(performanceModifier)) {
			targetAxisDuration = Math.round(
				targetAxisDuration * Double.parseDouble(performanceModifier));

			recordJobProperty(performanceModifierJobProperty);
		}

		long axisCount = Math.floorDiv(totalDuration, targetAxisDuration) + 1;

		return Math.toIntExact(axisCount);
	}

	protected File getPlaywrightBaseDir() {
		return new File(
			portalGitWorkingDirectory.getWorkingDirectory(),
			"modules/test/playwright");
	}

	protected List<JobProperty> getRelevantPlaywrightJobProperties() {
		Set<JobProperty> playwrightJobProperties = new HashSet<>();

		for (File modifiedFile :
				portalGitWorkingDirectory.getModifiedFilesList(false)) {

			List<JobProperty> playwrightTestProjectJobProperties =
				getJobProperties(
					modifiedFile, "playwright.test.project",
					JobProperty.Type.MODULE_TEST_DIR, null);

			playwrightTestProjectJobProperties.addAll(
				getJobProperties(
					modifiedFile, "playwright.projects.includes",
					JobProperty.Type.MODULE_TEST_DIR, null));

			for (JobProperty playwrightTestProjectJobProperty :
					playwrightTestProjectJobProperties) {

				if (playwrightTestProjectJobProperty.getValue() == null) {
					continue;
				}

				_addProjectNames(playwrightTestProjectJobProperty.getValue());

				playwrightJobProperties.add(playwrightTestProjectJobProperty);
			}

			List<JobProperty> playwrightExcludeProjectJobProperties =
				getJobProperties(
					modifiedFile, "playwright.projects.excludes",
					JobProperty.Type.MODULE_TEST_DIR, null);

			for (JobProperty playwrightExcludeProjectJobProperty :
					playwrightExcludeProjectJobProperties) {

				if (playwrightExcludeProjectJobProperty.getValue() == null) {
					continue;
				}

				removeProjectNames(
					playwrightExcludeProjectJobProperty.getValue());

				playwrightJobProperties.add(
					playwrightExcludeProjectJobProperty);
			}
		}

		playwrightJobProperties.removeAll(Collections.singleton(null));

		return new ArrayList<>(playwrightJobProperties);
	}

	protected void removeProjectNames(String projectNamesString) {
		for (String projectName : StringUtils.split(projectNamesString, ", ")) {
			_projectNames.remove(projectName);
		}
	}

	protected void setTestClasses() {
		long start = System.currentTimeMillis();

		_loadPlaywrightJSONObjects();

		JSONObject configJSONObject = _playwrightJSONObject.getJSONObject(
			"config");

		File rootDir = new File(configJSONObject.getString("rootDir"));

		Map<String, Map<File, TestClass>> testClassesMaps = new HashMap<>();

		synchronized (_playwrightJSONObjectsLoaded) {
			_parsePlaywrightJSONObjects(
				rootDir, _playwrightJSONObject.optJSONArray("suites"),
				testClassesMaps);
		}

		for (String projectName : _projectNames) {
			if (!_isDatabaseTypeSupported(projectName)) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"[", getBatchName(), "] Skipping Playwright project \"",
						projectName,
						"\" because its database types do not include the ",
						"batch database type"));

				continue;
			}

			List<TestClass> testClasses = _getTestClasses(
				projectName, rootDir, testClassesMaps);

			if (testClasses.isEmpty()) {
				continue;
			}

			SegmentTestClassGroup segmentTestClassGroup =
				TestClassGroupFactory.newSegmentTestClassGroup(this);

			if (!(segmentTestClassGroup instanceof
					PlaywrightSegmentTestClassGroup)) {

				continue;
			}

			PlaywrightSegmentTestClassGroup playwrightSegmentTestClassGroup =
				(PlaywrightSegmentTestClassGroup)segmentTestClassGroup;

			playwrightSegmentTestClassGroup.setProjectName(projectName);

			int axisCount = getAxisCount(testClasses);

			if (axisCount >= 1) {
				for (int axisIndex = 0; axisIndex < axisCount; axisIndex++) {
					AxisTestClassGroup axisTestClassGroup =
						TestClassGroupFactory.newAxisTestClassGroup(this);

					playwrightSegmentTestClassGroup.addAxisTestClassGroup(
						axisTestClassGroup);

					if (axisCount > 1) {
						StringBuilder sb = new StringBuilder();

						sb.append("test --project=");
						sb.append(projectName);
						sb.append(" --shard=");
						sb.append(axisIndex + 1);
						sb.append("/");
						sb.append(axisCount);
						sb.append(" --list");

						String result;

						if (_hasRunPlaywrightGradleTask()) {
							result = _callGradleCommand(
								getPlaywrightBaseDir(),
								JenkinsResultsParserUtil.combine(
									"runPlaywright -Pplaywright.args=\"",
									sb.toString(), "\""),
								null, 1000 * 60 * 10);
						}
						else {
							result = _callNPMCommand(
								getPlaywrightBaseDir(), "npx playwright " + sb);
						}

						for (TestClass testClass : testClasses) {
							if (!result.contains(testClass.getName())) {
								continue;
							}

							axisTestClassGroup.addTestClass(testClass);
						}
					}
					else {
						for (TestClass testClass : testClasses) {
							axisTestClassGroup.addTestClass(testClass);
						}
					}

					addAxisTestClassGroup(axisTestClassGroup);

					playwrightSegmentTestClassGroup.setBaseSlaveLabel(
						axisTestClassGroup.getBaseSlaveLabel());
				}
			}

			for (TestClass testClass : testClasses) {
				addTestClass(testClass);
			}

			addSegmentTestClassGroup(playwrightSegmentTestClassGroup);
		}

		List<TestClass> testClasses = getTestClasses();

		long duration = System.currentTimeMillis() - start;

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"[", getBatchName(), "] ", "Found ",
				String.valueOf(testClasses.size()),
				" Playwright test classes in ",
				JenkinsResultsParserUtil.toDurationString(duration)));
	}

	private void _addProjectNames(String projectNamesString) {
		Collections.addAll(
			_projectNames, StringUtils.split(projectNamesString, ", "));
	}

	private String _callGradleCommand(
		File baseDir, String command, Map<String, String> environmentVariables,
		long timeout) {

		StringBuilder sb = new StringBuilder();

		if (JenkinsResultsParserUtil.isCINode()) {
			sb.append("export CI=true\n");
		}

		sb.append("export npm_config_package_lock=false\n");

		if (environmentVariables != null) {
			for (Map.Entry<String, String> environmentVariable :
					environmentVariables.entrySet()) {

				sb.append("export ");
				sb.append(environmentVariable.getKey());
				sb.append("=");
				sb.append(environmentVariable.getValue());
				sb.append("\n");
			}
		}

		sb.append(
			JenkinsResultsParserUtil.getCanonicalPath(
				new File(
					portalGitWorkingDirectory.getWorkingDirectory(),
					"gradlew")));

		sb.append(" ");
		sb.append(command);

		try {
			Process process = JenkinsResultsParserUtil.executeBashCommands(
				true, baseDir, timeout, sb.toString());

			return JenkinsResultsParserUtil.readInputStream(
				process.getInputStream());
		}
		catch (IOException | TimeoutException exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _callNPMCommand(File baseDir, String npmCommand) {
		StringBuilder sb = new StringBuilder();

		if (JenkinsResultsParserUtil.isCINode()) {
			sb.append("export CI=true\n");
		}

		sb.append("export PATH=");

		String npmHome = _getPortalProperty("npm.home");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(npmHome)) {
			sb.append(npmHome);
			sb.append(":");
		}

		String nodeHome = _getPortalProperty("node.home");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(nodeHome)) {
			sb.append(nodeHome);
			sb.append("/bin:");
		}

		sb.append("${PATH}\n");

		sb.append(npmCommand);

		File npmScriptFile = new File(baseDir, "npm_script.sh");

		try {
			JenkinsResultsParserUtil.write(npmScriptFile, sb.toString());

			Process process = JenkinsResultsParserUtil.executeBashCommands(
				true, baseDir, 1000 * 60 * 10, sb.toString());

			return JenkinsResultsParserUtil.readInputStream(
				process.getInputStream());
		}
		catch (IOException | TimeoutException exception) {
			throw new RuntimeException(exception);
		}
		finally {
			JenkinsResultsParserUtil.delete(npmScriptFile);
		}
	}

	private File _getConfigFile(String projectName) throws IOException {
		if (_configFiles == null) {
			_configFiles = new HashMap<>();

			for (File configFile :
					JenkinsResultsParserUtil.findFiles(
						new File(getPlaywrightBaseDir(), "tests"),
						"config\\.ts")) {

				Matcher matcher = _configProjectNamePattern.matcher(
					JenkinsResultsParserUtil.read(configFile));

				while (matcher.find()) {
					_configFiles.put(matcher.group("projectName"), configFile);
				}
			}
		}

		return _configFiles.get(projectName);
	}

	private List<String> _getDatabaseTypes(File dir) {
		Properties testProperties = JenkinsResultsParserUtil.getProperties(
			new File(dir, "test.properties"));

		String[] databaseTypes = StringUtils.split(
			JenkinsResultsParserUtil.getProperty(
				testProperties, "database.types"),
			", ");

		if (databaseTypes == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(databaseTypes);
	}

	private List<String> _getDatabaseTypes(String projectName)
		throws IOException {

		File configFile = _getConfigFile(projectName);

		if (configFile == null) {
			return Collections.emptyList();
		}

		List<String> databaseTypes = _getDatabaseTypes(
			configFile.getParentFile());

		if (!databaseTypes.isEmpty()) {
			return databaseTypes;
		}

		Matcher matcher = _configTestDirPattern.matcher(
			JenkinsResultsParserUtil.read(configFile));

		if (!matcher.find()) {
			return Collections.emptyList();
		}

		return _getDatabaseTypes(
			new File(getPlaywrightBaseDir(), matcher.group("testDir")));
	}

	private List<String> _getDefaultProjectNames() {
		String playwrightProjectName = Environment.get(
			"PLAYWRIGHT_PROJECT_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(playwrightProjectName)) {
			return Arrays.asList(
				StringUtils.split(playwrightProjectName, ", "));
		}

		_loadPlaywrightJSONObjects();

		List<String> projectNames = new ArrayList<>();

		JSONObject configJSONObject = _playwrightJSONObject.getJSONObject(
			"config");

		JSONArray projectsJSONArray = configJSONObject.optJSONArray("projects");

		for (int i = 0; i < projectsJSONArray.length(); i++) {
			JSONObject projectJSONObject = projectsJSONArray.getJSONObject(i);

			projectNames.add(projectJSONObject.optString("name"));
		}

		return projectNames;
	}

	private File _getModulesDir() {
		PortalTestClassJob portalTestClassJob = (PortalTestClassJob)getJob();

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			portalTestClassJob.getPortalGitWorkingDirectory();

		return new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "modules");
	}

	private JSONObject _getPlaywrightJSONObject(
		String playwrightArgs, File playwrightBaseDir) {

		File playwrightReportFile = null;

		try {
			playwrightReportFile = File.createTempFile(
				"playwright.report.", ".json");

			Map<String, String> environmentVariables = new HashMap<>();

			environmentVariables.put(
				"PLAYWRIGHT_JSON_OUTPUT_NAME",
				JenkinsResultsParserUtil.getCanonicalPath(
					playwrightReportFile));

			_callGradleCommand(
				playwrightBaseDir,
				JenkinsResultsParserUtil.combine(
					"runPlaywright -Pplaywright.args=\"", playwrightArgs, "\""),
				environmentVariables, 1000 * 60 * 30);

			String result = JenkinsResultsParserUtil.read(playwrightReportFile);

			return new JSONObject(result.trim());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
		finally {
			if (playwrightReportFile != null) {
				JenkinsResultsParserUtil.delete(playwrightReportFile);
			}
		}
	}

	private JobProperty _getPlaywrightProjectsIncludesJobProperty() {
		JobProperty playwrightProjectsIncludesJobProperty = getJobProperty(
			"playwright.test.project", testSuiteName, batchName);

		String playwrightProjectsIncludesJobPropertyValue =
			playwrightProjectsIncludesJobProperty.getValue();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(
				playwrightProjectsIncludesJobPropertyValue)) {

			return playwrightProjectsIncludesJobProperty;
		}

		playwrightProjectsIncludesJobProperty = getJobProperty(
			"playwright.projects.includes", testSuiteName, batchName);

		playwrightProjectsIncludesJobPropertyValue =
			playwrightProjectsIncludesJobProperty.getValue();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(
				playwrightProjectsIncludesJobPropertyValue)) {

			return playwrightProjectsIncludesJobProperty;
		}

		return null;
	}

	private String _getPortalProperty(String propertyName) {
		File workingDirectory = JenkinsResultsParserUtil.getCanonicalFile(
			portalGitWorkingDirectory.getWorkingDirectory());

		Properties portalProperties = JenkinsResultsParserUtil.getProperties(
			new File(workingDirectory, "build.properties"),
			new File(workingDirectory, "app.server.properties"),
			new File(workingDirectory, "release.properties"),
			new File(workingDirectory, "test.properties"));

		portalProperties.setProperty(
			"project.dir", workingDirectory.toString());

		return JenkinsResultsParserUtil.getProperty(
			portalProperties, propertyName);
	}

	private List<TestClass> _getTestClasses(
		String projectName, File rootDir,
		Map<String, Map<File, TestClass>> testClassesMaps) {

		if (isRootCauseAnalysis()) {
			String portalBatchTestSelector = Environment.get(
				"PORTAL_BATCH_TEST_SELECTOR");

			if (JenkinsResultsParserUtil.isNullOrEmpty(
					portalBatchTestSelector)) {

				portalBatchTestSelector = getBuildStartProperty(
					"PORTAL_BATCH_TEST_SELECTOR");
			}

			Matcher matcher = _playwrightFileNamePattern.matcher(
				portalBatchTestSelector);

			if (matcher.matches()) {
				Map<File, TestClass> testClassesMap = testClassesMaps.get(
					projectName);

				if (testClassesMap != null) {
					File specFile = new File(
						rootDir, matcher.group("filePath"));

					TestClass testClass = testClassesMap.get(specFile);

					if (testClass != null) {
						return Collections.singletonList(testClass);
					}
				}

				return Collections.emptyList();
			}
		}

		Map<File, TestClass> testClassesMap = testClassesMaps.getOrDefault(
			projectName, Collections.emptyMap());

		return new ArrayList<>(testClassesMap.values());
	}

	private boolean _hasRunPlaywrightGradleTask() {
		if (_hasRunPlaywrightGradleTask != null) {
			return _hasRunPlaywrightGradleTask;
		}

		_hasRunPlaywrightGradleTask = false;

		File buildGradleFile = new File(getPlaywrightBaseDir(), "build.gradle");

		if (!buildGradleFile.exists()) {
			return _hasRunPlaywrightGradleTask;
		}

		try {
			String buildGradle = JenkinsResultsParserUtil.read(buildGradleFile);

			if (JenkinsResultsParserUtil.isNullOrEmpty(buildGradle)) {
				return _hasRunPlaywrightGradleTask;
			}

			Matcher matcher = _runPlaywrightGradleTaskPattern.matcher(
				buildGradle);

			if (matcher.find()) {
				_hasRunPlaywrightGradleTask = true;
			}
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return _hasRunPlaywrightGradleTask;
	}

	private boolean _hasTestClassMethod(TestClass testClass, String testName) {
		for (TestClassMethod testClassMethod :
				testClass.getTestClassMethods()) {

			PlaywrightTestClassMethod playwrightTestClassMethod =
				(PlaywrightTestClassMethod)testClassMethod;

			if (Objects.equals(
					playwrightTestClassMethod.getTestName(), testName)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isDatabaseTypeSupported(String projectName) {
		List<String> databaseTypes = null;

		try {
			databaseTypes = _getDatabaseTypes(projectName);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		if (databaseTypes.isEmpty()) {
			return true;
		}

		Set<String> validDatabaseTypes = new HashSet<>();

		Properties testProperties =
			portalGitWorkingDirectory.getTestProperties();

		for (String propertyName : testProperties.stringPropertyNames()) {
			Matcher matcher = _databaseVersionPropertyNamePattern.matcher(
				propertyName);

			if (!matcher.matches()) {
				continue;
			}

			validDatabaseTypes.add(matcher.group("databaseType"));
		}

		boolean databaseTypeSupported = false;

		for (String databaseType : databaseTypes) {
			if (!validDatabaseTypes.contains(databaseType)) {
				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Invalid database type \"", databaseType,
						"\" in Playwright project \"", projectName, "\""));
			}

			Pattern batchNameDatabaseTypePattern = Pattern.compile(
				JenkinsResultsParserUtil.combine(
					"-", databaseType, "\\d*(?=[-_]|$)"));

			Matcher matcher = batchNameDatabaseTypePattern.matcher(
				getBatchName());

			if (!matcher.find()) {
				continue;
			}

			databaseTypeSupported = true;
		}

		return databaseTypeSupported;
	}

	private boolean _isPlaywrightInYarnWorkspace() throws IOException {
		File packageJSONFile = new File(_getModulesDir(), "package.json");

		if (!packageJSONFile.exists()) {
			return false;
		}

		String packageJSON = JenkinsResultsParserUtil.read(packageJSONFile);

		if (JenkinsResultsParserUtil.isNullOrEmpty(packageJSON)) {
			return false;
		}

		JSONObject packageJSONObject = new JSONObject(packageJSON);

		JSONObject workspacesJSONObject = packageJSONObject.optJSONObject(
			"workspaces");

		if (workspacesJSONObject == null) {
			return false;
		}

		JSONArray packagesJSONArray = workspacesJSONObject.optJSONArray(
			"packages");

		if (packagesJSONArray == null) {
			return false;
		}

		for (int i = 0; i < packagesJSONArray.length(); i++) {
			if (Objects.equals(
					packagesJSONArray.optString(i), "test/playwright")) {

				return true;
			}
		}

		return false;
	}

	private void _loadPlaywrightJSONObjects() {
		synchronized (_playwrightJSONObjectsLoaded) {
			if (_playwrightJSONObjectsLoaded.get()) {
				return;
			}

			File playwrightBaseDir = getPlaywrightBaseDir();

			try {
				AntUtil.callTarget(
					portalGitWorkingDirectory.getWorkingDirectory(),
					"build.xml", "setup-yarn");
			}
			catch (AntException antException) {
				System.out.println(
					"Skipping Playwright class group enumeration because the " +
						"\"setup-yarn\" target failed");

				antException.printStackTrace();

				_playwrightJSONObject = new JSONObject();

				_playwrightJSONObjectsLoaded.set(true);

				return;
			}

			String playwrightArgs = "test --list --reporter=json";

			if (_hasRunPlaywrightGradleTask()) {
				Retryable<JSONObject> retryable = new Retryable<JSONObject>(
					false, 1, 5, true) {

					@Override
					public JSONObject execute() {
						return _getPlaywrightJSONObject(
							playwrightArgs, playwrightBaseDir);
					}

				};

				_playwrightJSONObject = retryable.executeWithRetries();

				if (_playwrightJSONObject == null) {
					_sendNotification("Unable to parse Playwright JSON object");

					_playwrightJSONObject = new JSONObject();
				}
			}
			else {
				try {
					if (!_isPlaywrightInYarnWorkspace()) {
						_callNPMCommand(playwrightBaseDir, "npm install");
					}

					String result = _callNPMCommand(
						playwrightBaseDir,
						"npm run playwright -- " + playwrightArgs);

					Matcher matcher = _npmCommandOutputPattern.matcher(result);

					if (!matcher.find()) {
						throw new RuntimeException(
							"Invalid NPM command output: " + result);
					}

					result = result.substring(matcher.start(1));

					result = result.replace(
						"Finished executing Bash commands.", "");

					_playwrightJSONObject = new JSONObject(result.trim());
				}
				catch (Exception exception) {
					_sendNotification("Unable to parse Playwright JSON object");

					exception.printStackTrace();

					_playwrightJSONObject = new JSONObject();
				}
			}

			JSONArray errorsJSONArray = _playwrightJSONObject.optJSONArray(
				"errors");

			if ((errorsJSONArray != null) && (errorsJSONArray.length() > 0)) {
				StringBuilder sb = new StringBuilder();

				String message = "Errors found in Playwright tests";

				sb.append(message);

				sb.append("\n");

				for (int i = 0; i < errorsJSONArray.length(); i++) {
					JSONObject errorJSONObject = errorsJSONArray.getJSONObject(
						i);

					System.out.println(errorJSONObject);

					String errorMessage = errorJSONObject.optString("message");

					if ((errorMessage != null) && !errorMessage.isEmpty()) {
						sb.append(errorMessage);
					}

					JSONObject errorLocationJSONObject =
						errorJSONObject.optJSONObject("location");

					if (errorLocationJSONObject != null) {
						sb.append(" [file://");
						sb.append(errorLocationJSONObject.opt("file"));
						sb.append(":");
						sb.append(errorLocationJSONObject.opt("line"));
						sb.append(":");
						sb.append(errorLocationJSONObject.opt("column"));
						sb.append("]");
					}

					String errorStack = errorJSONObject.optString("stack");

					if ((errorStack != null) && !errorStack.isEmpty()) {
						sb.append("\n");

						sb.append(errorStack);
					}

					String errorSnippet = errorJSONObject.optString("snippet");

					if ((errorSnippet != null) && !errorSnippet.isEmpty()) {
						sb.append("\n");

						sb.append(StringEscapeUtils.unescapeJava(errorSnippet));
					}

					sb.append("\n");
				}

				System.err.println(sb.toString());

				_sendNotification(message);

				return;
			}

			_playwrightJSONObjectsLoaded.set(true);
		}
	}

	private void _parsePlaywrightJSONObjects(
		File rootDir, JSONArray suitesJSONArray,
		Map<String, Map<File, TestClass>> testClassesMaps) {

		if (suitesJSONArray == null) {
			return;
		}

		for (int i = 0; i < suitesJSONArray.length(); i++) {
			JSONObject suiteJSONObject = suitesJSONArray.getJSONObject(i);

			JSONArray subSuitesJSONArray = suiteJSONObject.optJSONArray(
				"suites");

			if (subSuitesJSONArray != null) {
				_parsePlaywrightJSONObjects(
					rootDir, subSuitesJSONArray, testClassesMaps);
			}

			JSONArray specsJSONArray = suiteJSONObject.optJSONArray("specs");

			if (specsJSONArray == null) {
				continue;
			}

			String file = suiteJSONObject.optString("file");
			String title = suiteJSONObject.optString("title");

			String subSuite = null;

			if (!Objects.equals(title, file) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(title)) {

				subSuite = title;
			}

			for (int j = 0; j < specsJSONArray.length(); j++) {
				JSONObject specJSONObject = specsJSONArray.getJSONObject(j);

				JSONArray testsJSONArray = specJSONObject.optJSONArray("tests");

				if ((testsJSONArray == null) || testsJSONArray.isEmpty()) {
					continue;
				}

				JSONObject testJSONObject = testsJSONArray.getJSONObject(0);

				String specProjectName = testJSONObject.optString(
					"projectName");

				File specFile = new File(
					rootDir, specJSONObject.getString("file"));

				String specTitle = specJSONObject.getString("title");

				if (subSuite != null) {
					specTitle = subSuite + " › " + specTitle;
				}

				Map<File, TestClass> testClassesMap =
					testClassesMaps.computeIfAbsent(
						specProjectName, k -> new LinkedHashMap<>());

				TestClass testClass = testClassesMap.computeIfAbsent(
					specFile, k -> TestClassFactory.newTestClass(this, k));

				if (_hasTestClassMethod(testClass, specTitle)) {
					continue;
				}

				JSONArray tagsJSONArray = specJSONObject.optJSONArray("tags");

				String tags = null;

				if ((tagsJSONArray != null) && !tagsJSONArray.isEmpty()) {
					List<String> tagsList = new ArrayList<>(
						tagsJSONArray.length());

					for (int t = 0; t < tagsJSONArray.length(); t++) {
						tagsList.add(tagsJSONArray.optString(t));
					}

					tags = JenkinsResultsParserUtil.join(",", tagsList);
				}

				boolean ignored = false;

				JSONArray annotationsJSONArray = testJSONObject.optJSONArray(
					"annotations");

				if ((annotationsJSONArray != null) &&
					!annotationsJSONArray.isEmpty()) {

					for (int a = 0; a < annotationsJSONArray.length(); a++) {
						JSONObject annotationJSONObject =
							annotationsJSONArray.optJSONObject(a);

						if ((annotationJSONObject != null) &&
							Objects.equals(
								annotationJSONObject.optString("type"),
								"skip")) {

							ignored = true;

							break;
						}
					}
				}

				if (tags != null) {
					testClass.addTestClassMethod(
						TestClassFactory.newTestClassMethod(
							ignored, specTitle, tags, testClass));
				}
				else {
					testClass.addTestClassMethod(
						TestClassFactory.newTestClassMethod(
							ignored, specTitle, testClass));
				}
			}
		}
	}

	private void _sendNotification(String message) {
		String topLevelBuildURL = Environment.get("TOP_LEVEL_BUILD_URL");

		if (!topLevelBuildURL.contains("(release)") &&
			!topLevelBuildURL.contains(
				"test-portal-acceptance-upstream-dxp(master)") &&
			!topLevelBuildURL.contains(
				"test-portal-testsuite-upstream(master)")) {

			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(message);

		if (topLevelBuildURL.contains("(release)")) {
			sb.append(" <@U04HF3T8M>");
		}

		sb.append(" <@U01EV0V1Y6N>\n");
		sb.append(topLevelBuildURL);

		NotificationUtil.sendSlackNotification(
			sb.toString(), "#ci-notifications", ":playwright:",
			"Playwright batch creation failure", "Liferay Playwright");
	}

	private static final Pattern _configProjectNamePattern = Pattern.compile(
		"name:\\s*'(?<projectName>[^']+)'");
	private static final Pattern _configTestDirPattern = Pattern.compile(
		"testDir:\\s*'(?<testDir>[^']+)'");
	private static final Pattern _databaseVersionPropertyNamePattern =
		Pattern.compile("database\\.(?<databaseType>[^.]+)\\.version");
	private static final Pattern _npmCommandOutputPattern = Pattern.compile(
		"^\\s*(\\{)", Pattern.MULTILINE);
	private static final Pattern _playwrightFileNamePattern = Pattern.compile(
		"tests/(?<filePath>(?<projectName>.+)/[^/]*.spec.ts)");
	private static JSONObject _playwrightJSONObject;
	private static final AtomicBoolean _playwrightJSONObjectsLoaded =
		new AtomicBoolean();
	private static final Pattern _runPlaywrightGradleTaskPattern =
		Pattern.compile(
			"^\\s*(task\\s+runPlaywright\\b|tasks\\.(create|register)\\s*\\(" +
				"\\s*[\"']runPlaywright[\"'])",
			Pattern.MULTILINE);

	private Map<String, File> _configFiles;
	private Boolean _hasRunPlaywrightGradleTask;
	private final Set<String> _projectNames = new HashSet<>();

}