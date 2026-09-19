/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.suite;

import com.liferay.jenkins.results.parser.GitWorkingDirectory;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;
import com.liferay.jenkins.results.parser.test.batch.TestBatch;
import com.liferay.jenkins.results.parser.test.batch.TestBatchFactory;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kenji Heigel
 */
public class RelevantRule implements Comparable<RelevantRule> {

	public RelevantRule(
		String filePath, GitWorkingDirectory gitWorkingDirectory, Job job,
		String name, Properties properties) {

		_filePath = filePath;
		_gitWorkingDirectory = gitWorkingDirectory;
		_job = job;
		_name = name;
		_properties = properties;
	}

	public RelevantRule(
		String filePath, GitWorkingDirectory gitWorkingDirectory, String name,
		Properties properties) {

		this(filePath, gitWorkingDirectory, null, name, properties);
	}

	@Override
	public int compareTo(RelevantRule relevantRule) {
		return _name.compareTo(relevantRule.getName());
	}

	public String getFilePath() {
		return _filePath;
	}

	public GitWorkingDirectory getGitWorkingDirectory() {
		return _gitWorkingDirectory;
	}

	public String getKey() {
		return _filePath + "_" + _name;
	}

	public List<PathMatcher> getModifiedFilesExcludesPathMatchers() {
		if (_modifiedFilesExcludesPathMatchers != null) {
			return _modifiedFilesExcludesPathMatchers;
		}

		List<PathMatcher> modifiedFilesExcludesPathMatchers = new ArrayList<>();

		String modifiedFilesExcludes = JenkinsResultsParserUtil.getProperty(
			getProperties(), "modified.files.excludes", getName(),
			getTestSuiteName());

		if (modifiedFilesExcludes != null) {
			modifiedFilesExcludesPathMatchers.addAll(
				JenkinsResultsParserUtil.toPathMatchers(
					_getParentFilePath() + "/",
					modifiedFilesExcludes.split(",")));
		}

		String modifiedFilesGlobalExcludes = _getBaseDirTestProperty(
			"modified.files.global.excludes");

		if (modifiedFilesGlobalExcludes != null) {
			modifiedFilesExcludesPathMatchers.addAll(
				JenkinsResultsParserUtil.toPathMatchers(
					_getBaseDirPath() + "/",
					modifiedFilesGlobalExcludes.split(",")));
		}

		if (!modifiedFilesExcludesPathMatchers.isEmpty()) {
			_modifiedFilesExcludesPathMatchers =
				modifiedFilesExcludesPathMatchers;
		}

		return _modifiedFilesExcludesPathMatchers;
	}

	public List<PathMatcher> getModifiedFilesIncludesPathMatchers() {
		if (_modifiedFilesIncludesPathMatchers != null) {
			return _modifiedFilesIncludesPathMatchers;
		}

		String modifiedFilesIncludes = JenkinsResultsParserUtil.getProperty(
			getProperties(), "modified.files.includes", getName(),
			getTestSuiteName());

		if ((modifiedFilesIncludes == null) ||
			modifiedFilesIncludes.isEmpty()) {

			_modifiedFilesIncludesPathMatchers = Collections.emptyList();
		}
		else {
			_modifiedFilesIncludesPathMatchers =
				JenkinsResultsParserUtil.toPathMatchers(
					_getParentFilePath() + "/",
					modifiedFilesIncludes.split(","));
		}

		String modifiedFilesGlobalIncludes = _getBaseDirTestProperty(
			"modified.files.global.includes");

		if (modifiedFilesGlobalIncludes != null) {
			_modifiedFilesIncludesPathMatchers.addAll(
				JenkinsResultsParserUtil.toPathMatchers(
					_getBaseDirPath() + "/",
					modifiedFilesGlobalIncludes.split(",")));
		}

		return _modifiedFilesIncludesPathMatchers;
	}

	public String getName() {
		return _name;
	}

	public Properties getProperties() {
		return _properties;
	}

	public Set<JobProperty> getTestBatchNamesJobProperties() {
		return _testBatchNamesJobProperties;
	}

	public JobProperty getTestBatchNamesJobProperty() {
		if (_job == null) {
			throw new IllegalStateException("Job is null");
		}

		File propertiesFile = new File(_filePath);

		File propertiesBaseDir = propertiesFile.getParentFile();

		JobProperty.Type jobPropertyType = JobProperty.Type.DEFAULT_TEST_DIR;

		if (!_filePath.endsWith("liferay-portal/test.properties")) {
			jobPropertyType = JobProperty.Type.MODULE_TEST_DIR;
		}

		return JobPropertyFactory.newJobProperty(
			"test.batch.names", "relevant", null, _name, _job,
			propertiesBaseDir, jobPropertyType, true);
	}

	public List<TestBatch> getTestBatches() {
		if (_job == null) {
			throw new IllegalStateException("Job is null");
		}

		if (_testBatches == null) {
			JobProperty testBatchNamesJobProperty =
				getTestBatchNamesJobProperty();

			String testBatchNamesPropertyValue =
				testBatchNamesJobProperty.getValue();

			if (testBatchNamesPropertyValue == null) {
				return Collections.emptyList();
			}

			_testBatchNamesJobProperties.add(testBatchNamesJobProperty);

			_testBatches = new ArrayList<>();

			for (String testBatchName :
					testBatchNamesPropertyValue.split(",")) {

				_testBatches.add(
					TestBatchFactory.newTestBatch(
						new File(_filePath), getProperties(), testBatchName,
						getName(), getTestSuiteName()));
			}
		}

		return _testBatches;
	}

	public String getTestScriptCommand() {
		return JenkinsResultsParserUtil.getProperty(
			getProperties(), "test.script.command", getName(),
			getTestSuiteName());
	}

	public String getTestScriptCommandDir() {
		return JenkinsResultsParserUtil.getProperty(
			getProperties(), "test.script.command.dir", getName(),
			getTestSuiteName());
	}

	public List<TestScriptCommand> getTestScriptCommands() {
		String testScriptCommand = getTestScriptCommand();

		if (testScriptCommand == null) {
			return Collections.emptyList();
		}

		return Collections.singletonList(
			new TestScriptCommand(
				testScriptCommand, getTestScriptCommandDir()));
	}

	public String getTestSuiteName() {
		RelevantRuleEngine relevantRuleEngine =
			RelevantRuleEngine.getInstance();

		return relevantRuleEngine.getTestSuiteName();
	}

	public boolean matches(File modifiedFile) {
		return JenkinsResultsParserUtil.isFileIncluded(
			getModifiedFilesExcludesPathMatchers(),
			getModifiedFilesIncludesPathMatchers(), modifiedFile);
	}

	public void validate() throws RelevantRuleConfigurationException {
		if (_job != null) {
			List<TestBatch> testBatches = getTestBatches();

			if (testBatches.isEmpty()) {
				throw new RelevantRuleConfigurationException(
					JenkinsResultsParserUtil.combine(
						"Unable to find test.batch.names for relevant rule \"",
						getName(), "\" in ", _filePath));
			}
		}
		else {
			List<TestScriptCommand> testScriptCommands =
				getTestScriptCommands();

			if (testScriptCommands.isEmpty()) {
				throw new RelevantRuleConfigurationException(
					JenkinsResultsParserUtil.combine(
						"Unable to find test.script.command for relevant ",
						"rule \"", getName(), "\" in ", _filePath));
			}
		}

		List<PathMatcher> modifiedFilesIncludes =
			getModifiedFilesIncludesPathMatchers();

		if (modifiedFilesIncludes.isEmpty()) {
			throw new RelevantRuleConfigurationException(
				JenkinsResultsParserUtil.combine(
					"Unable to find modified.files.includes for relevant ",
					"rule \"", getName(), "\" in ", _filePath));
		}
	}

	public static class TestScriptCommand {

		public TestScriptCommand(String command, String commandDirPath) {
			_command = command;
			_commandDirPath = commandDirPath;
		}

		public String getCommand() {
			return _command;
		}

		public String getCommandDirPath() {
			return _commandDirPath;
		}

		private final String _command;
		private final String _commandDirPath;

	}

	protected String getGradlePackageName(File moduleDir) {
		String moduleDirPath = JenkinsResultsParserUtil.getCanonicalPath(
			moduleDir);

		int index = moduleDirPath.indexOf("/modules/");

		if (index == -1) {
			return "";
		}

		String relativeModuleDirPath = moduleDirPath.substring(index + 9);

		return ":" + relativeModuleDirPath.replace('/', ':');
	}

	protected List<File> getModifiedDirsList(File rootDirectory) {
		return _gitWorkingDirectory.getModifiedDirsList(
			true, null, null, rootDirectory);
	}

	protected List<File> getModifiedModuleProjectDirsList() throws IOException {
		if (_modifiedModuleProjectDirsList != null) {
			return _modifiedModuleProjectDirsList;
		}

		_modifiedModuleProjectDirsList = getModifiedModuleProjectDirsList(
			getModifiedFilesExcludesPathMatchers(),
			getModifiedFilesIncludesPathMatchers());

		return _modifiedModuleProjectDirsList;
	}

	protected List<File> getModifiedModuleProjectDirsList(
			List<PathMatcher> excludesPathMatchers,
			List<PathMatcher> includesPathMatchers)
		throws IOException {

		List<File> modifiedFiles = new ArrayList<>();

		for (File modifiedFile : _getModifiedFilesList()) {
			if (JenkinsResultsParserUtil.isFileIncluded(
					excludesPathMatchers, includesPathMatchers, modifiedFile)) {

				modifiedFiles.add(modifiedFile);
			}
		}

		Set<String> moduleDirPaths = _getModuleDirPaths();

		Set<File> modifiedModuleProjectDirs = new LinkedHashSet<>();

		for (File modifiedFile : modifiedFiles) {
			File parentDir = modifiedFile.getParentFile();

			while (parentDir != null) {
				String parentFilePath =
					JenkinsResultsParserUtil.getCanonicalPath(parentDir);

				if (moduleDirPaths.contains(parentFilePath)) {
					modifiedModuleProjectDirs.add(parentDir);

					break;
				}

				parentDir = parentDir.getParentFile();
			}
		}

		return new ArrayList<>(modifiedModuleProjectDirs);
	}

	private String _getBaseDirPath() {
		RelevantRuleEngine relevantRuleEngine =
			RelevantRuleEngine.getInstance();

		return JenkinsResultsParserUtil.getCanonicalPath(
			relevantRuleEngine.getBaseDir());
	}

	private String _getBaseDirTestProperty(String propertyName) {
		RelevantRuleEngine relevantRuleEngine =
			RelevantRuleEngine.getInstance();

		File baseTestPropertiesFile = new File(
			relevantRuleEngine.getBaseDir(), "test.properties");

		if (!baseTestPropertiesFile.exists()) {
			return null;
		}

		return JenkinsResultsParserUtil.getProperty(
			JenkinsResultsParserUtil.getProperties(baseTestPropertiesFile),
			propertyName, getTestSuiteName());
	}

	private List<File> _getModifiedFilesList() {
		return _gitWorkingDirectory.getModifiedFilesList(true);
	}

	private Set<String> _getModuleDirPaths() throws IOException {
		synchronized (_moduleDirPaths) {
			if (!_moduleDirPaths.isEmpty()) {
				return _moduleDirPaths;
			}

			PortalGitWorkingDirectory portalGitWorkingDirectory =
				_getPortalGitWorkingDirectory();

			File modulesDir = new File(
				portalGitWorkingDirectory.getWorkingDirectory(), "modules");

			if (!modulesDir.exists()) {
				return _moduleDirPaths;
			}

			Files.walkFileTree(
				modulesDir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
						Path filePath,
						BasicFileAttributes basicFileAttributes) {

						File dir = filePath.toFile();

						String dirName = dir.getName();

						if (dirName.equals("build") ||
							dirName.equals("classes") ||
							dirName.equals("node_modules") ||
							dirName.equals("node_modules_cache") ||
							dirName.equals("test-coverage") ||
							dirName.equals("test-results") ||
							dirName.startsWith(".")) {

							return FileVisitResult.SKIP_SUBTREE;
						}

						PortalGitWorkingDirectory.Module module =
							PortalGitWorkingDirectory.Module.getModule(
								filePath);

						if (module != null) {
							_moduleDirPaths.add(
								JenkinsResultsParserUtil.getCanonicalPath(
									module.getFile()));
						}

						return FileVisitResult.CONTINUE;
					}

				});

			return _moduleDirPaths;
		}
	}

	private String _getParentFilePath() {
		File file = new File(_filePath);

		return JenkinsResultsParserUtil.getCanonicalPath(file.getParentFile());
	}

	private PortalGitWorkingDirectory _getPortalGitWorkingDirectory() {
		return (PortalGitWorkingDirectory)_gitWorkingDirectory;
	}

	private static final Set<String> _moduleDirPaths =
		ConcurrentHashMap.newKeySet();

	private final String _filePath;
	private final GitWorkingDirectory _gitWorkingDirectory;
	private final Job _job;
	private List<PathMatcher> _modifiedFilesExcludesPathMatchers;
	private List<PathMatcher> _modifiedFilesIncludesPathMatchers;
	private List<File> _modifiedModuleProjectDirsList;
	private final String _name;
	private final Properties _properties;
	private final Set<JobProperty> _testBatchNamesJobProperties =
		new HashSet<>();
	private List<TestBatch> _testBatches;

}