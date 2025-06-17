/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.AntUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.NotificationUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.batch.PlaywrightTestBatch;
import com.liferay.jenkins.results.parser.test.batch.PlaywrightTestSelector;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class PlaywrightBatchTestClassGroup extends BatchTestClassGroup {

	public void addDefaultProjectJobProperty(String batchName) {
		if (isRootCauseAnalysis()) {
			String portalBatchTestSelector = System.getenv(
				"PORTAL_BATCH_TEST_SELECTOR");

			if (JenkinsResultsParserUtil.isNullOrEmpty(
					portalBatchTestSelector)) {

				portalBatchTestSelector = getBuildStartProperty(
					"PORTAL_BATCH_TEST_SELECTOR");
			}

			Matcher matcher = _playwrightFileNamePattern.matcher(
				portalBatchTestSelector);

			if (matcher.matches()) {
				_addProjectNames(matcher.group("projectName"));
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
			_addProjectNames(_getDefaultProjectNames());
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
			Collections.addAll(
				_projectNames,
				jobProperty.getValue(
				).split(
					","
				));
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
		PortalTestClassJob portalTestClassJob = (PortalTestClassJob)getJob();

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			portalTestClassJob.getPortalGitWorkingDirectory();

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

				if (playwrightTestProjectJobProperty.getValue() != null) {
					String projectNames =
						playwrightTestProjectJobProperty.getValue();

					_addProjectNames(projectNames);

					playwrightJobProperties.add(
						playwrightTestProjectJobProperty);
				}
			}

			List<JobProperty> playwrightExcludeProjectJobProperties =
				getJobProperties(
					modifiedFile, "playwright.projects.excludes",
					JobProperty.Type.MODULE_TEST_DIR, null);

			for (JobProperty playwrightExcludeProjectJobProperty :
					playwrightExcludeProjectJobProperties) {

				if (playwrightExcludeProjectJobProperty.getValue() != null) {
					String projectNames =
						playwrightExcludeProjectJobProperty.getValue();

					removeProjectNames(projectNames);

					playwrightJobProperties.add(
						playwrightExcludeProjectJobProperty);
				}
			}
		}

		playwrightJobProperties.removeAll(Collections.singleton(null));

		return new ArrayList<>(playwrightJobProperties);
	}

	protected List<JSONObject> getSpecJSONObjects() {
		return _specJSONObjects;
	}

	protected void removeProjectNames(String jobPropertyValue) {
		String[] excludesProjectNames = jobPropertyValue.split("\\s*,\\s*");

		for (String excludeProjectName : excludesProjectNames) {
			_projectNames.remove(excludeProjectName);
		}
	}

	protected void setTestClasses() {
		long start = System.currentTimeMillis();

		_loadPlaywrightJSONObjects();

		for (String projectName : _projectNames) {
			List<TestClass> testClasses = _getTestClasses(projectName);

			if (testClasses.isEmpty()) {
				continue;
			}

			SegmentTestClassGroup segmentTestClassGroup =
				TestClassGroupFactory.newSegmentTestClassGroup(this);

			if (segmentTestClassGroup instanceof
					PlaywrightSegmentTestClassGroup) {

				PlaywrightSegmentTestClassGroup
					playwrightSegmentTestClassGroup =
						(PlaywrightSegmentTestClassGroup)segmentTestClassGroup;

				playwrightSegmentTestClassGroup.setProjectName(projectName);

				int axisCount = getAxisCount(testClasses);

				if (axisCount >= 1) {
					for (int axisIndex = 0; axisIndex < axisCount;
						 axisIndex++) {

						AxisTestClassGroup axisTestClassGroup =
							TestClassGroupFactory.newAxisTestClassGroup(this);

						playwrightSegmentTestClassGroup.addAxisTestClassGroup(
							axisTestClassGroup);

						synchronized (_loadedProjectNames) {
							if (!_loadedProjectNames.contains(projectName) ||
								(axisCount > 1)) {

								_loadedProjectNames.add(projectName);

								StringBuilder sb = new StringBuilder();

								sb.append("npx playwright test --project=");
								sb.append(projectName);
								sb.append(" --shard=");
								sb.append(axisIndex + 1);
								sb.append("/");
								sb.append(axisCount);
								sb.append(" --list");

								String result = _callNPMCommand(
									getPlaywrightBaseDir(), sb.toString());

								for (TestClass testClass : testClasses) {
									if (result.contains(testClass.getName())) {
										axisTestClassGroup.addTestClass(
											testClass);
									}
								}
							}
							else {
								for (TestClass testClass : testClasses) {
									axisTestClassGroup.addTestClass(testClass);
								}
							}

							addAxisTestClassGroup(axisTestClassGroup);

							playwrightSegmentTestClassGroup.setSlaveLabel(
								axisTestClassGroup.getSlaveLabel());
						}
					}
				}

				for (TestClass testClass : testClasses) {
					addTestClass(testClass);
				}

				addSegmentTestClassGroup(playwrightSegmentTestClassGroup);
			}
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

	private void _addProjectNames(String projectNames) {
		projectNames = projectNames.trim();

		Collections.addAll(_projectNames, projectNames.split("\\s*,\\s*"));
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

	private String _getDefaultProjectNames() {
		String playwrightProjectName = System.getenv("PLAYWRIGHT_PROJECT_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(playwrightProjectName)) {
			return playwrightProjectName;
		}

		_loadPlaywrightJSONObjects();

		StringBuilder sb = new StringBuilder();

		JSONObject configJSONObject = _playwrightJSONObject.getJSONObject(
			"config");

		JSONArray projectsJSONArray = configJSONObject.optJSONArray("projects");

		for (int i = 0; i < projectsJSONArray.length(); i++) {
			JSONObject projectJSONObject = projectsJSONArray.getJSONObject(i);

			sb.append(projectJSONObject.optString("name"));

			sb.append(",");
		}

		sb.setLength(sb.length() - 1);

		return sb.toString();
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

	private List<JSONObject> _getSpecJSONObjects(JSONObject jsonObject) {
		List<JSONObject> specJSONObjects = new ArrayList<>();

		JSONArray suitesJSONArray = jsonObject.getJSONArray("suites");

		for (int i = 0; i < suitesJSONArray.length(); i++) {
			JSONObject suiteJSONObject = suitesJSONArray.getJSONObject(i);

			if (suiteJSONObject.has("suites")) {
				specJSONObjects.addAll(_getSpecJSONObjects(suiteJSONObject));
			}

			JSONArray specsJSONArray = suiteJSONObject.optJSONArray("specs");

			if (specsJSONArray == null) {
				continue;
			}

			String file = suiteJSONObject.getString("file");
			String title = suiteJSONObject.getString("title");

			for (int j = 0; j < specsJSONArray.length(); j++) {
				JSONObject specJSONObject = specsJSONArray.getJSONObject(j);

				if (!title.equals(file)) {
					specJSONObject.put("subSuite", title);
				}

				specJSONObjects.add(specJSONObject);
				specJSONObjects.add(specsJSONArray.getJSONObject(j));
			}
		}

		return specJSONObjects;
	}

	private List<TestClass> _getTestClasses(String projectName) {
		List<TestClass> testClasses = new ArrayList<>();

		JSONObject configJSONObject = _playwrightJSONObject.getJSONObject(
			"config");

		File rootDir = new File(configJSONObject.getString("rootDir"));

		Map<File, Set<String>> specTitlesMap = new HashMap<>();

		for (JSONObject specJSONObject : getSpecJSONObjects()) {
			JSONArray testsJSONArray = specJSONObject.optJSONArray("tests");

			if ((testsJSONArray == null) || testsJSONArray.isEmpty()) {
				continue;
			}

			JSONObject testJSONObject = testsJSONArray.getJSONObject(0);

			if (!Objects.equals(
					projectName, testJSONObject.optString("projectName"))) {

				continue;
			}

			File specFile = new File(rootDir, specJSONObject.getString("file"));

			Set<String> specTitles = specTitlesMap.get(specFile);

			if (specTitles == null) {
				specTitles = new HashSet<>();
			}

			if (specJSONObject.has("subSuite")) {
				specTitles.add(
					specJSONObject.getString("subSuite") + " › " +
						specJSONObject.getString("title"));
			}
			else {
				specTitles.add(specJSONObject.getString("title"));
			}

			specTitlesMap.put(specFile, specTitles);
		}

		if (isRootCauseAnalysis()) {
			String portalBatchTestSelector = System.getenv(
				"PORTAL_BATCH_TEST_SELECTOR");

			if (JenkinsResultsParserUtil.isNullOrEmpty(
					portalBatchTestSelector)) {

				portalBatchTestSelector = getBuildStartProperty(
					"PORTAL_BATCH_TEST_SELECTOR");
			}

			Matcher matcher = _playwrightFileNamePattern.matcher(
				portalBatchTestSelector);

			if (matcher.matches()) {
				File specFile = new File(rootDir, matcher.group("filePath"));

				TestClass testClass = TestClassFactory.newTestClass(
					this, specFile);

				for (String specTitle :
						specTitlesMap.getOrDefault(specFile, new HashSet<>())) {

					testClass.addTestClassMethod(
						TestClassFactory.newTestClassMethod(
							false, specTitle, testClass));
				}

				testClasses.add(testClass);

				return testClasses;
			}
		}

		for (Map.Entry<File, Set<String>> entry : specTitlesMap.entrySet()) {
			TestClass testClass = TestClassFactory.newTestClass(
				this, entry.getKey());

			for (String specTitle : entry.getValue()) {
				testClass.addTestClassMethod(
					TestClassFactory.newTestClassMethod(
						false, specTitle, testClass));
			}

			testClasses.add(testClass);
		}

		return testClasses;
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
			catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				_callNPMCommand(playwrightBaseDir, "npm install");

				String result = _callNPMCommand(
					playwrightBaseDir,
					"npm run playwright test -- --list --reporter=json");

				int index = result.indexOf("\n{");

				result = result.substring(index);

				result = result.replace(
					"Finished executing Bash commands.", "");

				_playwrightJSONObject = new JSONObject(result.trim());
			}
			catch (Exception exception) {
				_sendNotification("Unable to parse Playwright JSON object");

				exception.printStackTrace();
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

			_specJSONObjects.addAll(_getSpecJSONObjects(_playwrightJSONObject));

			_playwrightJSONObjectsLoaded.set(true);
		}
	}

	private void _sendNotification(String message) {
		StringBuilder sb = new StringBuilder();

		sb.append(message);
		sb.append(" <@U04GTH03Q>, <@U01EV0V1Y6N>\n");

		sb.append(System.getenv("TOP_LEVEL_BUILD_URL"));

		NotificationUtil.sendSlackNotification(
			sb.toString(), "#ci-notifications", ":playwright:",
			"Playwright batch creation failure", "Liferay Playwright");
	}

	private static final Set<String> _loadedProjectNames =
		Collections.synchronizedSet(new HashSet<>());
	private static final Pattern _playwrightFileNamePattern = Pattern.compile(
		"tests/(?<filePath>(?<projectName>[^/]+)/.*.spec.ts)");
	private static JSONObject _playwrightJSONObject;
	private static final AtomicBoolean _playwrightJSONObjectsLoaded =
		new AtomicBoolean();
	private static final List<JSONObject> _specJSONObjects =
		Collections.synchronizedList(new ArrayList<JSONObject>());

	private final Set<String> _projectNames = new HashSet<>();

}