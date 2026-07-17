/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.suite;

import com.liferay.jenkins.results.parser.GitWorkingDirectory;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.PortalAcceptancePullRequestJob;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * @author Kenji Heigel
 */
public class RelevantRuleEngine {

	public static void clear() {
		_relevantRuleEngine = null;
	}

	public static RelevantRuleEngine getInstance() {
		if (_relevantRuleEngine == null) {
			throw new IllegalStateException("Relevant rule engine is null");
		}

		return _relevantRuleEngine;
	}

	public static RelevantRuleEngine getInstance(
		GitWorkingDirectory gitWorkingDirectory, String testSuiteName) {

		if (_relevantRuleEngine == null) {
			_relevantRuleEngine = new RelevantRuleEngine(
				gitWorkingDirectory, null, testSuiteName);
		}

		return _relevantRuleEngine;
	}

	public static RelevantRuleEngine getInstance(
		PortalAcceptancePullRequestJob portalAcceptancePullRequestJob) {

		if (_relevantRuleEngine == null) {
			_relevantRuleEngine = new RelevantRuleEngine(
				portalAcceptancePullRequestJob.getPortalGitWorkingDirectory(),
				portalAcceptancePullRequestJob,
				portalAcceptancePullRequestJob.getTestSuiteName());
		}

		return _relevantRuleEngine;
	}

	public RelevantRuleEngine(
		GitWorkingDirectory gitWorkingDirectory, Job job,
		String testSuiteName) {

		_gitWorkingDirectory = gitWorkingDirectory;
		_job = job;
		_testSuiteName = testSuiteName;

		_baseDir = gitWorkingDirectory.getWorkingDirectory();

		if (_relevantRuleEngine == null) {
			_relevantRuleEngine = this;
		}
	}

	public RelevantRuleEngine(
		GitWorkingDirectory gitWorkingDirectory, String testSuiteName) {

		this(gitWorkingDirectory, null, testSuiteName);
	}

	public File getBaseDir() {
		return _baseDir;
	}

	public GitWorkingDirectory getGitWorkingDirectory() {
		return _gitWorkingDirectory;
	}

	public Job getJob() {
		return _job;
	}

	public List<RelevantRule> getMatchingRelevantRules(
		List<File> modifiedFiles) {

		_populateRelevantRuleMap(
			_getTestPropertiesModifiedFilesMap(modifiedFiles));

		List<RelevantRule> matchingRelevantRules = new ArrayList<>();

		for (Map.Entry<RelevantRule, Set<File>> entry :
				_relevantRuleMap.entrySet()) {

			RelevantRule relevantRule = entry.getKey();

			for (File modifiedFile : entry.getValue()) {
				if (relevantRule.matches(modifiedFile)) {
					matchingRelevantRules.add(relevantRule);

					break;
				}
			}
		}

		return matchingRelevantRules;
	}

	public RelevantRule getRelevantRule(
		String filePath, Job job, Properties properties,
		String relevantRuleName) {

		String relevantRuleKey = filePath + "_" + relevantRuleName;

		for (RelevantRule relevantRule : _relevantRuleMap.keySet()) {
			if (relevantRuleKey.equals(relevantRule.getKey())) {
				return relevantRule;
			}
		}

		String ruleType = JenkinsResultsParserUtil.getProperty(
			properties, "relevant.rule.type", relevantRuleName, _testSuiteName);

		RelevantRule relevantRule = null;

		if (Objects.equals(ruleType, "modules-compile")) {
			relevantRule = new CompileModulesRelevantRule(
				filePath, _gitWorkingDirectory, job, relevantRuleName,
				properties);
		}

		if (Objects.equals(ruleType, "ant-all")) {
			relevantRule = new AntAllRelevantRule(
				filePath, _gitWorkingDirectory, job, relevantRuleName,
				properties);
		}

		if (Objects.equals(ruleType, "workspace-build")) {
			relevantRule = new WorkspaceBuildRelevantRule(
				filePath, _gitWorkingDirectory, job, relevantRuleName,
				properties);
		}

		if (Objects.equals(ruleType, "modules")) {
			relevantRule = new ModulesRelevantRule(
				filePath, _gitWorkingDirectory, job, relevantRuleName,
				properties);
		}

		if (Objects.equals(ruleType, "portal-core-java-unit-test")) {
			relevantRule = new PortalCoreJavaUnitTestRelevantRule(
				filePath, _gitWorkingDirectory, job, relevantRuleName,
				properties);
		}

		if (relevantRule == null) {
			relevantRule = new RelevantRule(
				filePath, _gitWorkingDirectory, job, relevantRuleName,
				properties);
		}

		_relevantRuleMap.put(relevantRule, new HashSet<>());

		return relevantRule;
	}

	public List<String> getRelevantRuleNames(List<RelevantRule> relevantRules) {
		List<String> relevantRuleNames = new ArrayList<>();

		for (RelevantRule relevantRule : relevantRules) {
			relevantRuleNames.add(relevantRule.getName());
		}

		return relevantRuleNames;
	}

	public String getTestSuiteName() {
		return _testSuiteName;
	}

	public void setBaseDir(File baseDir) {
		_baseDir = baseDir;
	}

	private Properties _getRelevantRuleProperties(
		Properties properties, String relevantRuleName) {

		Properties relevantRuleProperties = new Properties();

		for (Object object : properties.keySet()) {
			String propertyName = (String)object;

			if (propertyName.contains("[" + relevantRuleName + "]") &&
				propertyName.contains("[" + getTestSuiteName() + "]")) {

				String propertyValue = properties.getProperty(propertyName);

				relevantRuleProperties.setProperty(propertyName, propertyValue);
			}
		}

		return relevantRuleProperties;
	}

	private Set<String> _getTestPropertiesFilePaths(
		File file, Set<String> testPropertiesFilePaths) {

		if (testPropertiesFilePaths == null) {
			testPropertiesFilePaths = new HashSet<>();
		}

		file = file.getParentFile();

		File testPropertiesFile = new File(file, "test.properties");

		String testPropertiesFilePath =
			JenkinsResultsParserUtil.getCanonicalPath(testPropertiesFile);

		if (testPropertiesFile.exists() &&
			!testPropertiesFilePaths.contains(testPropertiesFilePath)) {

			testPropertiesFilePaths.add(testPropertiesFilePath);
		}

		if (file.equals(_baseDir)) {
			return testPropertiesFilePaths;
		}

		return _getTestPropertiesFilePaths(file, testPropertiesFilePaths);
	}

	private Map<String, Set<File>> _getTestPropertiesModifiedFilesMap(
		List<File> modifiedFiles) {

		Map<String, Set<File>> testPropertiesModifiedFilesMap = new HashMap<>();

		if (modifiedFiles == null) {
			modifiedFiles = _gitWorkingDirectory.getModifiedFilesList(true);
		}

		for (File modifiedFile : modifiedFiles) {
			for (String testPropertiesFilePath :
					_getTestPropertiesFilePaths(modifiedFile, null)) {

				Set<File> testPropertiesModifiedFiles =
					testPropertiesModifiedFilesMap.computeIfAbsent(
						testPropertiesFilePath, key -> new HashSet<>());

				testPropertiesModifiedFiles.add(modifiedFile);
			}
		}

		return testPropertiesModifiedFilesMap;
	}

	private void _populateRelevantRuleMap(
		Map<String, Set<File>> testPropertiesModifiedFilesMap) {

		for (Map.Entry<String, Set<File>> entry :
				testPropertiesModifiedFilesMap.entrySet()) {

			String testPropertiesFilePath = entry.getKey();

			File testPropertiesFile = new File(testPropertiesFilePath);

			Properties properties = JenkinsResultsParserUtil.getProperties(
				testPropertiesFile);

			String relevantRuleNames = JenkinsResultsParserUtil.getProperty(
				properties, "relevant.rule.names", _testSuiteName);

			if (relevantRuleNames == null) {
				continue;
			}

			for (String relevantRuleName : relevantRuleNames.split(",")) {
				_relevantRuleMap.put(
					getRelevantRule(
						testPropertiesFilePath, _job,
						_getRelevantRuleProperties(
							properties, relevantRuleName),
						relevantRuleName),
					entry.getValue());
			}
		}
	}

	private static RelevantRuleEngine _relevantRuleEngine;

	private File _baseDir;
	private final GitWorkingDirectory _gitWorkingDirectory;
	private final Job _job;
	private final Map<RelevantRule, Set<File>> _relevantRuleMap =
		new LinkedHashMap<>();
	private final String _testSuiteName;

}